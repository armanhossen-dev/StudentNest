package com.studentnest.dao;

import com.studentnest.db.DatabaseConnection;
import com.studentnest.model.Room;
import com.studentnest.model.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoomDao {
    private final DatabaseConnection database = DatabaseConnection.getInstance();

    public Optional<Room> findById(long id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load room", ex);
        }
        return Optional.empty();
    }

    public List<Room> findAll() {
        return query("SELECT * FROM rooms ORDER BY created_at DESC");
    }

    public List<Room> findByOwner(long ownerId) {
        String sql = "SELECT * FROM rooms WHERE owner_id = ? ORDER BY created_at DESC";
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ownerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rooms.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to load owner rooms", ex);
        }
        return rooms;
    }

    public int countAll() {
        return scalar("SELECT COUNT(*) FROM rooms");
    }

    public int countVerified() {
        return scalar("SELECT COUNT(*) FROM rooms WHERE is_verified = 1");
    }

    public int countAvailable() {
        return scalar("SELECT COUNT(*) FROM rooms WHERE is_available = 1");
    }

    public Map<String, Integer> countByLocation() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String location : Room.SUPPORTED_LOCATIONS) {
            counts.put(location, 0);
        }
        String sql = "SELECT location, COUNT(*) AS total FROM rooms GROUP BY location";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String location = rs.getString("location");
                int total = rs.getInt("total");
                String matched = Room.SUPPORTED_LOCATIONS.stream()
                        .filter(item -> location != null && location.toLowerCase().contains(item.toLowerCase()))
                        .findFirst()
                        .orElse(null);
                if (matched != null) {
                    counts.put(matched, counts.get(matched) + total);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count rooms by location", ex);
        }
        return counts;
    }

    public long insert(Room room) {
        String sql = """
                INSERT INTO rooms (title, description, location, price, contact_number, owner_id, owner_name,
                    is_available, is_verified, room_type, bedrooms, amenities, map_link,
                    image1_path, image2_path, image3_path, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, room);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    room.setId(id);
                    return id;
                }
            }
            throw new IllegalStateException("Room insert did not return an id");
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create room", ex);
        }
    }

    public void update(Room room) {
        String sql = """
                UPDATE rooms SET title = ?, description = ?, location = ?, price = ?, contact_number = ?,
                    owner_id = ?, owner_name = ?, is_available = ?, is_verified = ?, room_type = ?,
                    bedrooms = ?, amenities = ?, map_link = ?, image1_path = ?, image2_path = ?,
                    image3_path = ?, created_at = ? WHERE id = ?
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, room);
            statement.setLong(18, room.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update room", ex);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to delete room", ex);
        }
    }

    public void setVerified(long id, boolean verified) {
        String sql = "UPDATE rooms SET is_verified = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, verified ? 1 : 0);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update verification", ex);
        }
    }

    public void setAvailable(long id, boolean available) {
        String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, available ? 1 : 0);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update availability", ex);
        }
    }

    private void bind(PreparedStatement statement, Room room) throws SQLException {
        statement.setString(1, room.getTitle());
        statement.setString(2, room.getDescription());
        statement.setString(3, room.getLocation());
        statement.setDouble(4, room.getPrice());
        statement.setString(5, room.getContactNumber());
        statement.setLong(6, room.getOwnerId());
        statement.setString(7, room.getOwnerName());
        statement.setInt(8, room.isAvailable() ? 1 : 0);
        statement.setInt(9, room.isVerified() ? 1 : 0);
        statement.setString(10, room.getRoomType().getDisplayName());
        statement.setInt(11, room.getBedrooms());
        statement.setString(12, room.getAmenities());
        statement.setString(13, room.getMapLink());
        statement.setString(14, room.getImage1Path());
        statement.setString(15, room.getImage2Path());
        statement.setString(16, room.getImage3Path());
        statement.setString(17, timestamp(room.getCreatedAt()));
    }

    private List<Room> query(String sql) {
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                rooms.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query rooms", ex);
        }
        return rooms;
    }

    private int scalar(String sql) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to count rooms", ex);
        }
    }

    private Room map(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setTitle(rs.getString("title"));
        room.setDescription(rs.getString("description"));
        room.setLocation(rs.getString("location"));
        room.setPrice(rs.getDouble("price"));
        room.setContactNumber(rs.getString("contact_number"));
        room.setOwnerId(rs.getLong("owner_id"));
        room.setOwnerName(rs.getString("owner_name"));
        room.setAvailable(rs.getInt("is_available") == 1);
        room.setVerified(rs.getInt("is_verified") == 1);
        room.setRoomType(RoomType.fromDisplayName(rs.getString("room_type")));
        room.setBedrooms(rs.getInt("bedrooms"));
        room.setAmenities(rs.getString("amenities"));
        room.setMapLink(rs.getString("map_link"));
        room.setImage1Path(rs.getString("image1_path"));
        room.setImage2Path(rs.getString("image2_path"));
        room.setImage3Path(rs.getString("image3_path"));
        room.setCreatedAt(parseDate(rs.getString("created_at")));
        return room;
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
