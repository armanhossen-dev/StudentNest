package com.studentnest.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {
    private SchemaInitializer() {
    }

    public static void initialize() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.initialize();
        try (Connection connection = db.getConnection(); Statement statement = connection.createStatement()) {
            if (db.isMysql()) {
                createMysql(statement);
            } else {
                createSqlite(statement);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to initialize database schema", ex);
        }
    }

    private static void createMysql(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(120) NOT NULL,
                    email VARCHAR(160) NOT NULL,
                    phone VARCHAR(40) NOT NULL,
                    username VARCHAR(80) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    user_type VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(180) NOT NULL,
                    description TEXT NOT NULL,
                    location VARCHAR(80) NOT NULL,
                    price DECIMAL(12,2) NOT NULL,
                    contact_number VARCHAR(40) NOT NULL,
                    owner_id BIGINT NOT NULL,
                    owner_name VARCHAR(120) NOT NULL,
                    is_available TINYINT(1) NOT NULL DEFAULT 1,
                    is_verified TINYINT(1) NOT NULL DEFAULT 0,
                    room_type VARCHAR(40) NOT NULL,
                    bedrooms INT NOT NULL DEFAULT 1,
                    amenities VARCHAR(400),
                    map_link VARCHAR(500),
                    image1_path VARCHAR(400),
                    image2_path VARCHAR(400),
                    image3_path VARCHAR(400),
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_rooms_owner FOREIGN KEY (owner_id) REFERENCES users(id)
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS inquiries (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    room_id BIGINT NOT NULL,
                    room_title VARCHAR(180) NOT NULL,
                    student_name VARCHAR(120) NOT NULL,
                    student_contact VARCHAR(40) NOT NULL,
                    message TEXT NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                    student_id BIGINT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_inquiries_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS feedback (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    user_name VARCHAR(120) NOT NULL,
                    user_role VARCHAR(40) NOT NULL,
                    rating INT NOT NULL,
                    comment TEXT NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS favorites (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    room_id BIGINT NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY uq_favorites_user_room (user_id, room_id),
                    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    CONSTRAINT fk_favorites_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )
                """);
        createIndexQuietly(statement, "CREATE INDEX idx_rooms_location ON rooms(location)");
        createIndexQuietly(statement, "CREATE INDEX idx_rooms_owner ON rooms(owner_id)");
        createIndexQuietly(statement, "CREATE INDEX idx_inquiries_room ON inquiries(room_id)");
    }

    private static void createIndexQuietly(Statement statement, String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ignored) {
            // Index already exists or the engine does not support IF NOT EXISTS.
        }
    }

    private static void createSqlite(Statement statement) throws SQLException {
        statement.execute("PRAGMA foreign_keys = ON");
        statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    user_type TEXT NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    location TEXT NOT NULL,
                    price REAL NOT NULL,
                    contact_number TEXT NOT NULL,
                    owner_id INTEGER NOT NULL,
                    owner_name TEXT NOT NULL,
                    is_available INTEGER NOT NULL DEFAULT 1,
                    is_verified INTEGER NOT NULL DEFAULT 0,
                    room_type TEXT NOT NULL,
                    bedrooms INTEGER NOT NULL DEFAULT 1,
                    amenities TEXT,
                    map_link TEXT,
                    image1_path TEXT,
                    image2_path TEXT,
                    image3_path TEXT,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(id)
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS inquiries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room_id INTEGER NOT NULL,
                    room_title TEXT NOT NULL,
                    student_name TEXT NOT NULL,
                    student_contact TEXT NOT NULL,
                    message TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    student_id INTEGER,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS feedback (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    user_name TEXT NOT NULL,
                    user_role TEXT NOT NULL,
                    rating INTEGER NOT NULL,
                    comment TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """);
        statement.execute("""
                CREATE TABLE IF NOT EXISTS favorites (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    room_id INTEGER NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (user_id, room_id),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )
                """);
        statement.execute("CREATE INDEX IF NOT EXISTS idx_rooms_location ON rooms(location)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_rooms_owner ON rooms(owner_id)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_inquiries_room ON inquiries(room_id)");
    }
}
