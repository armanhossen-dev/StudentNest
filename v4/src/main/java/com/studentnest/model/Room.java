package com.studentnest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Room {
    public static final List<String> SUPPORTED_LOCATIONS = List.of(
            "Khagan", "Candgaon", "Charabag", "Kumkumari", "Dattopara", "Shadhupara"
    );

    public static final List<String> SUPPORTED_AMENITIES = List.of(
            "WiFi", "Furnished", "Kitchen", "Air Conditioning",
            "Attached Bathroom", "Balcony", "Parking", "Generator"
    );

    private long id;
    private String title;
    private String description;
    private String location;
    private double price;
    private String contactNumber;
    private long ownerId;
    private String ownerName;
    private boolean available;
    private boolean verified;
    private RoomType roomType;
    private int bedrooms;
    private String amenities;
    private String mapLink;
    private String image1Path;
    private String image2Path;
    private String image3Path;
    private LocalDateTime createdAt;

    public Room() {
        this.available = true;
        this.verified = false;
        this.roomType = RoomType.SINGLE_ROOM;
        this.bedrooms = 1;
        this.createdAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getImage1Path() {
        return image1Path;
    }

    public void setImage1Path(String image1Path) {
        this.image1Path = image1Path;
    }

    public String getImage2Path() {
        return image2Path;
    }

    public void setImage2Path(String image2Path) {
        this.image2Path = image2Path;
    }

    public String getImage3Path() {
        return image3Path;
    }

    public void setImage3Path(String image3Path) {
        this.image3Path = image3Path;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getAmenityList() {
        if (amenities == null || amenities.isBlank()) {
            return List.of();
        }
        return Arrays.stream(amenities.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getPreviewAmenities() {
        return getAmenityList().stream().limit(3).toList();
    }

    public String getFormattedPrice() {
        return "৳" + String.format("%,.0f", price);
    }

    public String getPrimaryImage() {
        if (image1Path != null && !image1Path.isBlank()) {
            return image1Path;
        }
        if (image2Path != null && !image2Path.isBlank()) {
            return image2Path;
        }
        return image3Path;
    }

    public List<String> getImagePaths() {
        List<String> paths = new ArrayList<>();
        if (image1Path != null && !image1Path.isBlank()) {
            paths.add(image1Path);
        }
        if (image2Path != null && !image2Path.isBlank()) {
            paths.add(image2Path);
        }
        if (image3Path != null && !image3Path.isBlank()) {
            paths.add(image3Path);
        }
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room room)) {
            return false;
        }
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
