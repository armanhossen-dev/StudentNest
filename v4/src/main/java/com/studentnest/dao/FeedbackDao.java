package com.studentnest.dao;

import com.studentnest.db.DatabaseConnection;
import com.studentnest.model.Feedback;
import com.studentnest.model.FeedbackStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public List<Feedback> findAll() {
        return query("SELECT * FROM feedback ORDER BY created_at DESC");
    }

    public List<Feedback> findApproved() {
        return query("SELECT * FROM feedback WHERE status = 'APPROVED' ORDER BY created_at DESC");
    }

    public int countApproved() {
        String sql = "SELECT COUNT(*) FROM feedback WHERE status = 'APPROVED'";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count feedback", ex);
        }
    }

    public long insert(Feedback feedback) {
        String sql = """
                INSERT INTO feedback (user_id, user_name, user_role, rating, comment, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, feedback.getUserId());
            statement.setString(2, feedback.getUserName());
            statement.setString(3, feedback.getUserRole());
            statement.setInt(4, feedback.getRating());
            statement.setString(5, feedback.getComment());
            statement.setString(6, feedback.getStatus().name());
            statement.setString(7, timestamp(feedback.getCreatedAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    feedback.setId(id);
                    return id;
                }
            }
            throw new IllegalStateException("Feedback insert did not return an id");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create feedback", ex);
        }
    }

    public void updateStatus(long id, FeedbackStatus status) {
        String sql = "UPDATE feedback SET status = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update feedback", ex);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM feedback WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to delete feedback", ex);
        }
    }

    private List<Feedback> query(String sql) {
        List<Feedback> items = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                items.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to list feedback", ex);
        }
        return items;
    }

    private Feedback map(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getLong("id"));
        feedback.setUserId(rs.getLong("user_id"));
        feedback.setUserName(rs.getString("user_name"));
        feedback.setUserRole(rs.getString("user_role"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComment(rs.getString("comment"));
        feedback.setStatus(FeedbackStatus.fromString(rs.getString("status")));
        feedback.setCreatedAt(parseDate(rs.getString("created_at")));
        return feedback;
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
