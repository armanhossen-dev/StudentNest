package com.studentnest.db;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DatabaseSupport {
    private static boolean ready;

    private DatabaseSupport() {
    }

    public static synchronized void ensureReady() {
        if (ready) {
            return;
        }
        try {
            Path dir = Path.of("target");
            Files.createDirectories(dir);
            System.setProperty("db.sqlite.path", dir.resolve("test-studentnest.db").toAbsolutePath().toString());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to prepare test database path", ex);
        }
        SchemaInitializer.initialize();
        DataSeeder.seed();
        ready = true;
    }
}
