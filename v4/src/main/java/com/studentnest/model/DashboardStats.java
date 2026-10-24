package com.studentnest.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardStats {
    private int registeredUsers;
    private int registeredStudents;
    private int houseOwners;
    private int admins;
    private int activeListings;
    private int verifiedListings;
    private int availableListings;
    private int communityReviews;
    private Map<String, Integer> listingsByLocation = new LinkedHashMap<>();

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public int getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(int registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    public int getHouseOwners() {
        return houseOwners;
    }

    public void setHouseOwners(int houseOwners) {
        this.houseOwners = houseOwners;
    }

    public int getAdmins() {
        return admins;
    }

    public void setAdmins(int admins) {
        this.admins = admins;
    }

    public int getActiveListings() {
        return activeListings;
    }

    public void setActiveListings(int activeListings) {
        this.activeListings = activeListings;
    }

    public int getVerifiedListings() {
        return verifiedListings;
    }

    public void setVerifiedListings(int verifiedListings) {
        this.verifiedListings = verifiedListings;
    }

    public int getAvailableListings() {
        return availableListings;
    }

    public void setAvailableListings(int availableListings) {
        this.availableListings = availableListings;
    }

    public int getCommunityReviews() {
        return communityReviews;
    }

    public void setCommunityReviews(int communityReviews) {
        this.communityReviews = communityReviews;
    }

    public Map<String, Integer> getListingsByLocation() {
        return listingsByLocation;
    }

    public void setListingsByLocation(Map<String, Integer> listingsByLocation) {
        this.listingsByLocation = listingsByLocation;
    }
}
