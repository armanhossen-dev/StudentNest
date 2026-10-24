package com.studentnest.db;

import com.studentnest.dao.RoomDao;
import com.studentnest.dao.UserDao;
import com.studentnest.model.UserType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaInitializerTest {
    @BeforeAll
    static void setup() {
        TestDatabase.ensure();
    }

    @Test
    void usesSqliteFallbackAndCreatesCoreTables() throws Exception {
        assertEquals(DatabaseType.SQLITE, DatabaseConnection.getInstance().getActiveType());
        assertTrue(DatabaseConnection.getInstance().isConnected());
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            for (String table : new String[]{"users", "rooms", "inquiries", "feedback", "favorites"}) {
                try (ResultSet rs = statement.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'")) {
                    assertTrue(rs.next(), "Missing table " + table);
                }
            }
        }
    }

    @Test
    void seedsDemoAccountsAndEightPropertiesOnce() {
        UserDao users = new UserDao();
        RoomDao rooms = new RoomDao();
        assertTrue(users.findByUsername("student").isPresent());
        assertTrue(users.findByUsername("owner").isPresent());
        assertTrue(users.findByUsername("admin").isPresent());
        assertEquals(UserType.ADMIN, users.findByUsername("admin").orElseThrow().getUserType());
        assertEquals(8, rooms.countAll());
        DataSeeder.seed();
        assertEquals(8, rooms.countAll());
    }
}
