package com.studentnest.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ThemeManager {
    private static final Path PREFS_PATH = Path.of(System.getProperty("user.home"), ".studentnest", "prefs.properties");
    private static final ThemeManager INSTANCE = new ThemeManager();
    private final BooleanProperty darkMode = new SimpleBooleanProperty(false);

    private ThemeManager() {
        load();
        darkMode.addListener((obs, oldVal, newVal) -> persist());
    }

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public boolean isDarkMode() {
        return darkMode.get();
    }

    public void setDarkMode(boolean value) {
        darkMode.set(value);
    }

    public void toggle() {
        darkMode.set(!darkMode.get());
    }

    public String stylesheet() {
        return isDarkMode() ? "/css/dark.css" : "/css/light.css";
    }

    private void load() {
        if (!Files.exists(PREFS_PATH)) {
            return;
        }
        Properties properties = new Properties();
        try (var in = Files.newInputStream(PREFS_PATH)) {
            properties.load(in);
            darkMode.set(Boolean.parseBoolean(properties.getProperty("darkMode", "false")));
        } catch (IOException ignored) {
            // Keep the default light theme if preferences cannot be read.
        }
    }

    private void persist() {
        try {
            Files.createDirectories(PREFS_PATH.getParent());
            Properties properties = new Properties();
            properties.setProperty("darkMode", Boolean.toString(darkMode.get()));
            try (var out = Files.newOutputStream(PREFS_PATH)) {
                properties.store(out, "StudentNest user preferences");
            }
        } catch (IOException ignored) {
            // Theme still applies for the current session.
        }
    }
}
