package com.studentnest.dao;

import com.studentnest.db.DatabaseConnection;
import com.studentnest.model.User;
import com.studentnest.model.UserType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load user", ex);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE lower(username) = lower(?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load user by username", ex);
        }
        return Optional.empty();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE lower(email) = lower(?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to check email", ex);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to list users", ex);
        }
        return users;
    }

    public int countByType(UserType type) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_type = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type.name());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count users", ex);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count users", ex);
        }
    }

    public int countAdmins() {
        return countByType(UserType.ADMIN);
    }

    public long insert(User user) {
        String sql = """
                INSERT INTO users (name, email, phone, username, password_hash, user_type, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getUsername());
            statement.setString(5, user.getPasswordHash());
            statement.setString(6, user.getUserType().name());
            statement.setString(7, timestamp(user.getCreatedAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    user.setId(id);
                    return id;
                }
            }
            throw new IllegalStateException("User insert did not return an id");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create user", ex);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to delete user", ex);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setUserType(UserType.fromString(rs.getString("user_type")));
        user.setCreatedAt(parseDate(rs.getString("created_at")));
        return user;
    }

    private String timestamp(LocalDateTime value) {
        if (value == null) {
            value = LocalDateTime.now();
        }
        return Timestamp.valueOf(value).toString();
    }

    private LocalDateTime parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            String normalized = raw.replace('T', ' ');
            if (normalized.length() > 19) {
                normalized = normalized.substring(0, 19);
            }
            return Timestamp.valueOf(normalized).toLocalDateTime();
        } catch (IllegalArgumentException ex) {
            return LocalDateTime.now();
        }
    }
}
