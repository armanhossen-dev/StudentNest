package com.studentnest.models;

public class HouseOwner extends User {
    private String address;

    public HouseOwner() {
        super();
    }

    public HouseOwner(String name, String phone, String username, String password, String address) {
        super(name, phone, username, password, "House Owner");
        this.address = address;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}