package com.studentnest.dao;

import com.studentnest.db.DatabaseConnection;
import com.studentnest.model.Inquiry;
import com.studentnest.model.InquiryStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InquiryDao {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public List<Inquiry> findAll() {
        return query("SELECT * FROM inquiries ORDER BY created_at DESC", null);
    }

    public List<Inquiry> findByOwner(long ownerId) {
        String sql = """
                SELECT i.* FROM inquiries i
                INNER JOIN rooms r ON r.id = i.room_id
                WHERE r.owner_id = ?
                ORDER BY i.created_at DESC
                """;
        return query(sql, ownerId);
    }

    public long insert(Inquiry inquiry) {
        String sql = """
                INSERT INTO inquiries (room_id, room_title, student_name, student_contact, message, status, student_id, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, inquiry.getRoomId());
            statement.setString(2, inquiry.getRoomTitle());
            statement.setString(3, inquiry.getStudentName());
            statement.setString(4, inquiry.getStudentContact());
            statement.setString(5, inquiry.getMessage());
            statement.setString(6, inquiry.getStatus().name());
            if (inquiry.getStudentId() == null) {
                statement.setObject(7, null);
            } else {
                statement.setLong(7, inquiry.getStudentId());
            }
            statement.setString(8, timestamp(inquiry.getCreatedAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    inquiry.setId(id);
                    return id;
                }
            }
            throw new IllegalStateException("Inquiry insert did not return an id");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create inquiry", ex);
        }
    }

    public void updateStatus(long id, InquiryStatus status) {
        String sql = "UPDATE inquiries SET status = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update inquiry status", ex);
        }
    }

    public Inquiry findById(long id) {
        String sql = "SELECT * FROM inquiries WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load inquiry", ex);
        }
        return null;
    }

    private List<Inquiry> query(String sql, Long ownerId) {
        List<Inquiry> inquiries = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (ownerId != null) {
                statement.setLong(1, ownerId);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    inquiries.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to list inquiries", ex);
        }
        return inquiries;
    }

    private Inquiry map(ResultSet rs) throws SQLException {
        Inquiry inquiry = new Inquiry();
        inquiry.setId(rs.getLong("id"));
        inquiry.setRoomId(rs.getLong("room_id"));
        inquiry.setRoomTitle(rs.getString("room_title"));
        inquiry.setStudentName(rs.getString("student_name"));
        inquiry.setStudentContact(rs.getString("student_contact"));
        inquiry.setMessage(rs.getString("message"));
        inquiry.setStatus(InquiryStatus.fromString(rs.getString("status")));
        long studentId = rs.getLong("student_id");
        if (!rs.wasNull()) {
            inquiry.setStudentId(studentId);
        }
        inquiry.setCreatedAt(parseDate(rs.getString("created_at")));
        return inquiry;
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
