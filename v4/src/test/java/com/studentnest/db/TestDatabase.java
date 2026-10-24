package com.studentnest.db;

import java.nio.file.Files;
import java.nio.file.Path;

public final class TestDatabase {
    private static boolean ready;

    private TestDatabase() {
    }

    public static synchronized void ensure() {
        if (ready) {
            return;
        }
        try {
            Path dir = Path.of("target", "testdb");
            Files.createDirectories(dir);
        } catch (Exception ignored) {
            // Directory creation is best-effort; SQLite can still create the file.
        }
        System.setProperty("studentnest.force.sqlite", "true");
        System.setProperty("db.sqlite.path", "target/testdb/studentnest-test.db");
        DatabaseConnection.getInstance().initialize();
        SchemaInitializer.initialize();
        DataSeeder.seed();
        ready = true;
    }
}
