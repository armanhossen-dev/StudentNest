package com.studentnest.dao;

import com.studentnest.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FavoriteDao {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Set<Long> findRoomIdsByUser(long userId) {
        Set<Long> ids = new HashSet<>();
        String sql = "SELECT room_id FROM favorites WHERE user_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("room_id"));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load favorites", ex);
        }
        return ids;
    }

    public boolean exists(long userId, long roomId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND room_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, roomId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to check favorite", ex);
        }
    }

    public void add(long userId, long roomId) {
        if (exists(userId, roomId)) {
            return;
        }
        String sql = "INSERT INTO favorites (user_id, room_id) VALUES (?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, roomId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to save favorite", ex);
        }
    }

    public void remove(long userId, long roomId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND room_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, roomId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to remove favorite", ex);
        }
    }

    public int countByUser(long userId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count favorites", ex);
        }
    }
}
