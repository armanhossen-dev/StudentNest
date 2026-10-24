package com.studentnest.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

public final class ImageStorage {
    public static final Path UPLOAD_DIR = Path.of("room_images");

    private ImageStorage() {
    }

    public static Path ensureDirectory() {
        try {
            Files.createDirectories(UPLOAD_DIR);
            return UPLOAD_DIR;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create room_images directory", ex);
        }
    }

    public static String copyUpload(Path source) {
        if (source == null || !Files.exists(source)) {
            throw new IllegalArgumentException("Choose a valid image file");
        }
        String filename = source.getFileName().toString();
        if (!ValidationUtils.isAllowedImage(filename)) {
            throw new IllegalArgumentException("Images must be PNG, JPG, JPEG, GIF, or WEBP");
        }
        ensureDirectory();
        String ext = extension(filename);
        String stored = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = UPLOAD_DIR.resolve(stored);
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to copy image", ex);
        }
        return target.toString();
    }

    public static void deleteIfOwned(String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        Path file = Path.of(path);
        if (!file.isAbsolute()) {
            file = Path.of(path);
        }
        try {
            Path uploads = UPLOAD_DIR.toAbsolutePath().normalize();
            Path candidate = file.toAbsolutePath().normalize();
            if (candidate.startsWith(uploads) && Files.exists(candidate)) {
                Files.deleteIfExists(candidate);
            }
        } catch (IOException ignored) {
            // Keep listing deletion successful even if an image file is missing.
        }
    }

    public static boolean exists(String path) {
        return path != null && !path.isBlank() && Files.exists(Path.of(path));
    }

    public static InputStream bundledFallback() {
        return ImageStorage.class.getResourceAsStream("/images/fallback-room.png");
    }

    private static String extension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) {
            return ".png";
        }
        return filename.substring(idx).toLowerCase(Locale.ROOT);
    }
}
