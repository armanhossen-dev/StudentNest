package com.studentnest.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String username;
    private String passwordHash;
    private UserType userType;
    private LocalDateTime createdAt;

    public User() {
        this.userType = UserType.STUDENT;
        this.createdAt = LocalDateTime.now();
    }

    public User(long id, String name, String email, String phone, String username,
                String passwordHash, UserType userType, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    public static User guest() {
        User guest = new User();
        guest.setId(0);
        guest.setName("Guest");
        guest.setUsername("guest");
        guest.setUserType(UserType.GUEST);
        return guest;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isGuest() {
        return userType == UserType.GUEST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id == user.id && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
