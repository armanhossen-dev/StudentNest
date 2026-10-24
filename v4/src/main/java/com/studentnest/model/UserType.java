package com.studentnest.model;

public enum UserType {
    GUEST,
    STUDENT,
    OWNER,
    ADMIN;

    public static UserType fromString(String value) {
        if (value == null || value.isBlank()) {
            return GUEST;
        }
        try {
            return UserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return GUEST;
        }
    }

    public boolean canPersistFavorites() {
        return this == STUDENT || this == OWNER || this == ADMIN;
    }

    public boolean canSubmitInquiry() {
        return this == STUDENT || this == OWNER || this == ADMIN;
    }

    public boolean canSubmitReview() {
        return this == STUDENT || this == OWNER || this == ADMIN;
    }

    public boolean canManageOwnListings() {
        return this == OWNER || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public String displayName() {
        return switch (this) {
            case GUEST -> "Guest";
            case STUDENT -> "Student";
            case OWNER -> "House Owner";
            case ADMIN -> "Administrator";
        };
    }
}
