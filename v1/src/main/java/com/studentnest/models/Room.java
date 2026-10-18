package com.studentnest.models;

public class Room {
    private int id;
    private int ownerId;
    private String location;
    private double price;
    private String description;
    private String contactNumber;
    private String mapLink;
    private String ownerName;

    public Room() {}

    public Room(int ownerId, String location, double price, String description,
                String contactNumber, String mapLink) {
        this.ownerId = ownerId;
        this.location = location;
        this.price = price;
        this.description = description;
        this.contactNumber = contactNumber;
        this.mapLink = mapLink;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getMapLink() { return mapLink; }
    public void setMapLink(String mapLink) { this.mapLink = mapLink; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}