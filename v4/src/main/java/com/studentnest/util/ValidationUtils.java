package com.studentnest.util;

import com.studentnest.model.Room;
import com.studentnest.model.RoomType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^(\\+8801|8801|01)[0-9]{9}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,32}$");
    private static final List<String> IMAGE_EXTENSIONS = List.of(".png", ".jpg", ".jpeg", ".gif", ".webp");

    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String requireText(String value, String field, int min, int max) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() < min) {
            throw new IllegalArgumentException(field + " must be at least " + min + " characters");
        }
        if (trimmed.length() > max) {
            throw new IllegalArgumentException(field + " must be at most " + max + " characters");
        }
        return trimmed;
    }

    public static String requireEmail(String email) {
        String value = requireText(email, "Email", 5, 160);
        if (!EMAIL.matcher(value).matches()) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        return value.toLowerCase(Locale.ROOT);
    }

    public static String requirePhone(String phone) {
        if (isBlank(phone)) {
            throw new IllegalArgumentException("Phone number is required");
        }
        String compact = phone.trim().replace(" ", "").replace("-", "");
        if (!PHONE.matcher(compact).matches()) {
            throw new IllegalArgumentException("Enter a valid Bangladeshi phone number");
        }
        return compact;
    }

    public static String requireUsername(String username) {
        String value = requireText(username, "Username", 3, 32);
        if (!USERNAME.matcher(value).matches()) {
            throw new IllegalArgumentException("Username may contain letters, numbers, dots, underscores, and hyphens");
        }
        return value.toLowerCase(Locale.ROOT);
    }

    public static String requirePassword(String password) {
        if (isBlank(password) || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (password.length() > 72) {
            throw new IllegalArgumentException("Password is too long");
        }
        return password;
    }

    public static double requirePositivePrice(String raw) {
        if (isBlank(raw)) {
            throw new IllegalArgumentException("Monthly rent is required");
        }
        try {
            double value = Double.parseDouble(raw.trim().replace(",", "").replace("৳", ""));
            if (value <= 0) {
                throw new IllegalArgumentException("Monthly rent must be greater than zero");
            }
            if (value > 1_000_000) {
                throw new IllegalArgumentException("Monthly rent is unrealistically high");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Monthly rent must be a number");
        }
    }

    public static int requireBedrooms(int bedrooms) {
        if (bedrooms < 1 || bedrooms > 12) {
            throw new IllegalArgumentException("Bedrooms must be between 1 and 12");
        }
        return bedrooms;
    }

    public static String requireLocation(String location) {
        String value = requireText(location, "Location", 2, 80);
        boolean known = Room.SUPPORTED_LOCATIONS.stream().anyMatch(item -> item.equalsIgnoreCase(value))
                || value.toLowerCase(Locale.ROOT).contains("khagan");
        if (!known) {
            throw new IllegalArgumentException("Choose a supported DIU campus-area location");
        }
        return value;
    }

    public static RoomType requireRoomType(RoomType type) {
        if (type == null) {
            throw new IllegalArgumentException("Property type is required");
        }
        return type;
    }

    public static int requireRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return rating;
    }

    public static String optionalHttpUrl(String url) {
        if (isBlank(url)) {
            return null;
        }
        String value = url.trim();
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Map link must be an http or https URL");
            }
            if (uri.getHost() == null) {
                throw new IllegalArgumentException("Map link must be a valid URL");
            }
            return value;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Map link must be a valid http or https URL");
        }
    }

    public static boolean isAllowedImage(String filename) {
        if (isBlank(filename)) {
            return false;
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        return IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    public static List<String> sanitizeAmenities(List<String> selected) {
        if (selected == null || selected.isEmpty()) {
            return List.of();
        }
        List<String> cleaned = new ArrayList<>();
        for (String item : selected) {
            if (Room.SUPPORTED_AMENITIES.contains(item) && !cleaned.contains(item)) {
                cleaned.add(item);
            }
        }
        return cleaned;
    }

    public static String mapsSearchUrl(String location) {
        String query = isBlank(location) ? "Daffodil International University" : location + ", Daffodil International University, Bangladesh";
        return "https://www.google.com/maps/search/?api=1&query=" + encode(query);
    }

    private static String encode(String value) {
        return value.replace(" ", "+");
    }
}
