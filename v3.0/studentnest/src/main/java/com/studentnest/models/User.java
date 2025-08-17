package com.studentnest.models;

public class User {
    private int id;
    private String name;
    private String phone;
    private String username;
    private String password;
    private String userType;

    public User() {}

    public User(String name, String phone, String username, String password, String userType) {
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}