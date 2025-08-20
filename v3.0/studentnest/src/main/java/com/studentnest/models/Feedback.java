package com.studentnest.models;

import java.sql.Timestamp;

public class Feedback {
    private int id;
    private int userId;
    private String userName;
    private String feedbackText;
    private Timestamp createdAt;
    private String status;

    // Default constructor
    public Feedback() {
        this.status = "Pending"; // Default status
    }

    // Parameterized constructor
    public Feedback(int id, int userId, String userName, String feedbackText, Timestamp createdAt, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.feedbackText = feedbackText;
        this.createdAt = createdAt;
        this.status = status != null ? status : "Pending";
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "Pending";
    }

    // Utility methods
    public boolean isResolved() {
        return "Resolved".equalsIgnoreCase(this.status);
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(this.status);
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", feedbackText='" + feedbackText + '\'' +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }

    // equals and hashCode for proper comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Feedback feedback = (Feedback) obj;
        return id == feedback.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}