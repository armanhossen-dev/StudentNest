package com.studentnest.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties properties = new Properties();

    private AppConfig() {
        loadDefaults();
        loadClasspath();
        loadExternalFile();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    private void loadDefaults() {
        properties.setProperty("db.mysql.host", "localhost");
        properties.setProperty("db.mysql.port", "3306");
        properties.setProperty("db.mysql.name", "studentnest");
        properties.setProperty("db.mysql.user", "root");
        properties.setProperty("db.mysql.password", "");
        properties.setProperty("db.sqlite.path", "studentnest.db");
        properties.setProperty("app.version", "3.1.0");
        properties.setProperty("app.display.version", "v3.1 Modern");
    }

    private void loadClasspath() {
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException ignored) {
            // Defaults remain in place if the bundled file cannot be read.
        }
    }

    private void loadExternalFile() {
        Path configPath = Path.of("application.properties");
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                properties.load(in);
            } catch (IOException ignored) {
                // Keep previously loaded values.
            }
        }
    }

    public String get(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env;
        }
        String sys = System.getProperty(key);
        if (sys != null) {
            return sys;
        }
        return properties.getProperty(key, "");
    }

    public String getMysqlHost() {
        return firstNonBlank(System.getenv("STUDENTNEST_DB_HOST"), get("db.mysql.host"));
    }

    public int getMysqlPort() {
        String value = firstNonBlank(System.getenv("STUDENTNEST_DB_PORT"), get("db.mysql.port"));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 3306;
        }
    }

    public String getMysqlName() {
        return firstNonBlank(System.getenv("STUDENTNEST_DB_NAME"), get("db.mysql.name"));
    }

    public String getMysqlUser() {
        return firstNonBlank(System.getenv("STUDENTNEST_DB_USER"), get("db.mysql.user"));
    }

    public String getMysqlPassword() {
        String env = System.getenv("STUDENTNEST_DB_PASSWORD");
        if (env != null) {
            return env;
        }
        return get("db.mysql.password");
    }

    public String getSqlitePath() {
        return firstNonBlank(System.getenv("STUDENTNEST_SQLITE_PATH"), get("db.sqlite.path"));
    }

    public String getVersion() {
        return get("app.version");
    }

    public String getDisplayVersion() {
        return get("app.display.version");
    }

    public String mysqlJdbcUrl() {
        return "jdbc:mysql://" + getMysqlHost() + ":" + getMysqlPort() + "/" + getMysqlName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8"
                + "&connectTimeout=3000&socketTimeout=5000";
    }

    public String sqliteJdbcUrl() {
        return "jdbc:sqlite:" + getSqlitePath();
    }

    private static String firstNonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return fallback;
    }
}
