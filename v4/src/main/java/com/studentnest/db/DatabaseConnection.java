package com.studentnest.db;

import com.studentnest.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private final AtomicReference<DatabaseType> activeType = new AtomicReference<>(DatabaseType.SQLITE);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean mysqlAttempted = new AtomicBoolean(false);
    private String lastError = "";

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() {
        if (initialized.get()) {
            return;
        }
        AppConfig config = AppConfig.getInstance();
        boolean forceSqlite = Boolean.parseBoolean(System.getProperty("studentnest.force.sqlite", "false"));
        if (!forceSqlite && tryMysql(config)) {
            activeType.set(DatabaseType.MYSQL);
            initialized.set(true);
            lastError = "";
            return;
        }
        trySqlite(config);
        activeType.set(DatabaseType.SQLITE);
        initialized.set(true);
    }

    private boolean tryMysql(AppConfig config) {
        mysqlAttempted.set(true);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(3);
            try (Connection connection = DriverManager.getConnection(
                    config.mysqlJdbcUrl(), config.getMysqlUser(), config.getMysqlPassword())) {
                connection.createStatement().execute("SELECT 1");
            }
            return true;
        } catch (Exception ex) {
            lastError = "MySQL unavailable: " + ex.getMessage();
            return false;
        }
    }

    private void trySqlite(AppConfig config) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(config.sqliteJdbcUrl())) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("PRAGMA foreign_keys = ON");
                    statement.execute("SELECT 1");
                }
            }
            lastError = lastError.isBlank() ? "" : lastError + " | Using SQLite fallback.";
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize SQLite fallback database", ex);
        }
    }

    public Connection getConnection() throws SQLException {
        if (!initialized.get()) {
            initialize();
        }
        AppConfig config = AppConfig.getInstance();
        if (activeType.get() == DatabaseType.MYSQL) {
            Connection connection = DriverManager.getConnection(
                    config.mysqlJdbcUrl(), config.getMysqlUser(), config.getMysqlPassword());
            connection.setAutoCommit(true);
            return connection;
        }
        Connection connection = DriverManager.getConnection(config.sqliteJdbcUrl());
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public DatabaseType getActiveType() {
        if (!initialized.get()) {
            initialize();
        }
        return activeType.get();
    }

    public boolean isMysql() {
        return getActiveType() == DatabaseType.MYSQL;
    }

    public boolean isConnected() {
        try (Connection connection = getConnection()) {
            return connection != null && connection.isValid(2);
        } catch (SQLException ex) {
            return false;
        }
    }

    public String statusLabel() {
        if (!initialized.get()) {
            return "Connecting";
        }
        return isMysql() ? "MySQL connected" : "SQLite connected";
    }

    public String getLastError() {
        return lastError;
    }

    public String placeholder() {
        return isMysql() ? "?" : "?";
    }

    public boolean supportsBooleanNative() {
        return isMysql();
    }
}
