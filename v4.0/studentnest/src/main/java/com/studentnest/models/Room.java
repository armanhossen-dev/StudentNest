package com.studentnest.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private int id;
    private String location;
    private double price;
    private String description;
    private int ownerId;
    private String ownerName;
    private String roomType;
    private String contactInfo;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Additional fields that might be useful
    private boolean isAvailable;
    private int numberOfRooms;
    private String amenities;
    private String address;
    private String mapLink;
    private String image1Path;
    private String image2Path;

    // Default constructor
    public Room() {
        this.isAvailable = true; // Default to available
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Parameterized constructor
    public Room(int id, String location, double price, String description, int ownerId, String ownerName) {
        this();
        this.id = id;
        this.location = location;
        this.price = price;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    // Full constructor
    public Room(int id, String location, double price, String description, int ownerId,
                String ownerName, String roomType, String contactInfo, boolean isAvailable) {
        this(id, location, price, description, ownerId, ownerName);
        this.roomType = roomType;
        this.contactInfo = contactInfo;
        this.isAvailable = isAvailable;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    // Add getContactNumber() to match the controller
    public String getContactNumber() {
        return this.contactInfo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getAddress() {
        return address;
    }

    public String getMapLink() {
        return mapLink;
    }

    public String getImage1Path() {
        return image1Path;
    }

    public String getImage2Path() {
        return image2Path;
    }

    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        if (image1Path != null) {
            images.add(image1Path);
        }
        if (image2Path != null) {
            images.add(image2Path);
        }
        return images;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // Add setContactNumber() to match the controller
    public void setContactNumber(String contactNumber) {
        this.contactInfo = contactNumber;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public void setImage1Path(String image1Path) {
        this.image1Path = image1Path;
    }

    public void setImage2Path(String image2Path) {
        this.image2Path = image2Path;
    }

    public void setImages(List<String> imagePaths) {
        if (imagePaths != null && imagePaths.size() > 0) {
            this.image1Path = imagePaths.get(0);
            if (imagePaths.size() > 1) {
                this.image2Path = imagePaths.get(1);
            }
        }
    }

    // Utility methods
    public String getFormattedPrice() {
        return "৳" + String.format("%.0f", price);
    }

    public String getShortDescription() {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 47) + "...";
    }

    public boolean isPriceInRange(double minPrice, double maxPrice) {
        return price >= minPrice && price <= maxPrice;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", isAvailable=" + isAvailable +
                ", createdAt=" + createdAt +
                '}';
    }

    // equals and hashCode for proper comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Additional utility methods
    public boolean hasOwner() {
        return ownerId > 0 && ownerName != null && !ownerName.trim().isEmpty();
    }

    public boolean hasValidPrice() {
        return price > 0;
    }

    public boolean hasValidLocation() {
        return location != null && !location.trim().isEmpty();
    }

    public boolean isValidRoom() {
        return hasValidLocation() && hasValidPrice() && hasOwner();
    }
}
