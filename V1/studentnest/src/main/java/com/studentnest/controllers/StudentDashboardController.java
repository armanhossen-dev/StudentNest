package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import java.sql.*;

public class StudentDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private VBox roomsContainer;
    @FXML private Button logoutButton;

    private ObservableList<Room> rooms = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");

        // Initialize filters
        locationFilter.getItems().addAll("All Locations", "Khagan", "Candgaon", "Charabag",
                "Kumkumari", "Dattopara", "Shadhupara");
        priceFilter.getItems().addAll("All Prices", "0-5000", "5000-10000", "10000-15000", "15000+");

        locationFilter.setValue("All Locations");
        priceFilter.setValue("All Prices");

        loadRooms();
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
            case "0-5000": return price <= 5000;
            case "5000-10000": return price > 5000 && price <= 10000;
            case "10000-15000": return price > 10000 && price <= 15000;
            case "15000+": return price > 15000;
            default: return true;
        }
    }

    private VBox createRoomCard(Room room) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-background-radius: 5;");

        Label titleLabel = new Label("Room in " + room.getLocation());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label("Price: ৳" + room.getPrice() + "/month");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3;");

        Label ownerLabel = new Label("Owner: " + room.getOwnerName());
        Label descriptionLabel = new Label("Description: " + room.getDescription());

        Button contactButton = new Button("Contact Owner");
        contactButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
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
        SceneManager.switchScene("login.fxml", 800, 600);
    }
}