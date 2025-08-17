package com.studentnest.model;

public class StudentHome {
    private String location;
    private double price;
    private String availability;
    private String description;

    public StudentHome(String location, double price, String availability, String description) {
        this.location = location;
        this.price = price;
        this.availability = availability;
        this.description = description;
    }

    // Getters and Setters for all fields
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}