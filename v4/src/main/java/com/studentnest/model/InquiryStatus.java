package com.studentnest.model;

public enum InquiryStatus {
    PENDING,
    ACCEPTED,
    DECLINED;

    public static InquiryStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        try {
            return InquiryStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING;
        }
    }
}
