package com.studentnest.model;

public class Favorite {
    private long id;
    private long userId;
    private long roomId;

    public Favorite() {
    }

    public Favorite(long userId, long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }
}
