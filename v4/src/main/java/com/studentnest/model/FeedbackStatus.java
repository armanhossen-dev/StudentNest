package com.studentnest.model;

public enum FeedbackStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static FeedbackStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        try {
            return FeedbackStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING;
        }
    }
}
