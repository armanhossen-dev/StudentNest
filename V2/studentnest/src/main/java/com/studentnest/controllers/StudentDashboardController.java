package com.studentnest.controllers;

import com.studentnest.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;

public class StudentDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private VBox roomsContainer;
    @FXML private Button logoutButton;

    @FXML private Label userCountLabel;
    @FXML private Label roomCountLabel;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Room> rooms = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");

        // Initialize filters
        locationFilter.getItems().addAll("All Locations", "Khagan", "Candgaon", "Charabag",
                "Kumkumari", "Dattopara", "Shadhupara");
        priceFilter.getItems().addAll("All Prices", "0-3000", "3000-5000", "5000-9000", "9000+");

        locationFilter.setValue("All Locations");
        priceFilter.setValue("All Prices");

        loadRooms();
        // Call the new methods to load user and room counts
        loadUserCount();
        loadRoomCount();
    }

    private void loadRooms() {
        rooms.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.*, u.name as owner_name FROM rooms r JOIN users u ON r.owner_id = u.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setOwnerId(rs.getInt("owner_id"));
                room.setLocation(rs.getString("location"));
                room.setPrice(rs.getDouble("price"));
                room.setDescription(rs.getString("description"));
                room.setContactNumber(rs.getString("contact_number"));
                room.setMapLink(rs.getString("map_link"));
                room.setOwnerName(rs.getString("owner_name"));
                rooms.add(room);
            }

            displayRooms();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New method to load the total user count
    private void loadUserCount() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int count = rs.getInt(1);
                userCountLabel.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New method to load the total room count
    private void loadRoomCount() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM rooms";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int count = rs.getInt(1);
                roomCountLabel.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayRooms() {
        roomsContainer.getChildren().clear();

        for (Room room : rooms) {
            if (shouldShowRoom(room)) {
                VBox roomCard = createRoomCard(room);
                roomsContainer.getChildren().add(roomCard);
            }
        }
    }

    private boolean shouldShowRoom(Room room) {
        String selectedLocation = locationFilter.getValue();
        String selectedPrice = priceFilter.getValue();

        boolean locationMatch = selectedLocation.equals("All Locations") ||
                room.getLocation().equals(selectedLocation);

        boolean priceMatch = selectedPrice.equals("All Prices") ||
                isPriceInRange(room.getPrice(), selectedPrice);

        return locationMatch && priceMatch;
    }

    private boolean isPriceInRange(double price, String priceRange) {
        switch (priceRange) {
            case "0-5000": return price <= 3000;
            case "5000-10000": return price > 3000 && price <= 5000;
            case "10000-15000": return price > 5000 && price <= 9000;
            case "15000+": return price > 9000;
            default: return true;
        }
    }


    private VBox createRoomCard(Room room) {
        VBox card = new VBox(10);
        card.getStyleClass().add("room-card");  // Add CSS class

        Label titleLabel = new Label("Room in " + room.getLocation());
        titleLabel.getStyleClass().add("location-label");  // Add CSS class

        Label priceLabel = new Label("Price: ৳" + room.getPrice() + "/month");
        priceLabel.getStyleClass().add("price-label");  // Add CSS class

        Label ownerLabel = new Label("Owner: " + room.getOwnerName());
        Label descriptionLabel = new Label("Description: " + room.getDescription());

        Button contactButton = new Button("Contact Owner");
        contactButton.getStyleClass().add("contact-btn");  // Add CSS class
        contactButton.setOnAction(e -> showContactInfo(room));

        card.getChildren().addAll(titleLabel, priceLabel, ownerLabel, descriptionLabel, contactButton);
        return card;
    }
    private void showContactInfo(Room room) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Information");
        alert.setHeaderText("Owner: " + room.getOwnerName());
        alert.setContentText("Phone: " + room.getContactNumber() + "\n" +
                "Location: " + room.getLocation() + "\n" +
                (room.getMapLink().isEmpty() ? "" : "Map: " + room.getMapLink()));
        alert.showAndWait();
    }

    @FXML
    public void handleLocationFilter() {
        displayRooms();
    }

    @FXML
    public void handlePriceFilter() {
        displayRooms();
    }

    @FXML
    public void handleLogout() {
        try {
            // Use consistent window size with the login controller
            SceneManager.switchScene("login.fxml", 1000, 620);
            // Load the login.css stylesheet to ensure consistent styling
            URL cssUrl = this.getClass().getResource("/css/login.css");
            if (cssUrl != null) {
                SceneManager.getPrimaryStage().getScene().getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("login.css loaded successfully on back to login.");
            } else {
                System.err.println("Warning: CSS file not found: /css/login.css.");
            }
            System.out.println("Navigating back to login page...");
        } catch (Exception e) {
            System.err.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to display an alert dialog with specified type.
     * @param title The title of the alert.
     * @param message The message to display.
     * @param alertType The type of alert (INFO, WARNING, ERROR).
     */

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        javafx.application.Platform.runLater(() -> {
            try {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                // Check if there is a primary stage and set the icon
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                URL imageUrl = getClass().getResource("/images/img.png");
                if (imageUrl != null) {
                    Image icon = new Image(imageUrl.toExternalForm());
                    alertStage.getIcons().add(icon);
                } else {
                    System.err.println("Warning: The image file was not found. Please check the path: /images/img.png");
                }

                // Style the alert to match the application theme
                try {
                    alert.getDialogPane().getStylesheets().addAll(
                            getClass().getResource("/css/registration.css").toExternalForm()
                    );
                } catch (Exception e) {
                    System.err.println("Could not apply styles to alert dialog");
                }
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
            }
        });
    }

}