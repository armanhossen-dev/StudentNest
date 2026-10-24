package com.studentnest.model;

import java.util.Arrays;

public enum RoomType {
    SINGLE_ROOM("Single Room"),
    SHARED_SEAT("Shared Seat"),
    MASTER_BED("Master Bed"),
    STUDIO("Studio"),
    FULL_FLAT("Full Flat");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoomType fromDisplayName(String value) {
        if (value == null) {
            return SINGLE_ROOM;
        }
        return Arrays.stream(values())
                .filter(type -> type.displayName.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(SINGLE_ROOM);
    }
}
