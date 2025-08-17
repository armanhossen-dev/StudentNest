package com.studentnest.controllers;

import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;

import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.User;
import javafx.stage.Stage;

public class HouseOwnerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TextField locationField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField contactField;
    @FXML private TextField mapLinkField;
    @FXML private Button addRoomButton;
    @FXML private ListView<Room> roomsList;
    @FXML private Button updateRoomButton;
    @FXML private Button deleteRoomButton;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private Button logoutButton;

    private ObservableList<Room> rooms = FXCollections.observableArrayList();
    private Room selectedRoom = null;


    @FXML private TabPane mainTabPane;

    // FXML injections for the buttons
    @FXML private Button manageRoomsButton;
    @FXML private Button addNewRoomButton;
    @FXML private Button statisticsButton;
    @FXML private Button feedbackButton;
    @FXML private Button supportButton;
    @FXML private Button aboutUsButton;

    // FXML for the input fields

    // FXML for the room list and buttons


    // Observable list to hold rooms for the list view
    private ObservableList<Room> ownerRooms = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");
        // Set up list view
        roomsList.setItems(ownerRooms);
        roomsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText("Location: " + room.getLocation() + " | Price: " + room.getPrice());
                }
            }
        });

        loadOwnerRooms();
    }

    // Method to load rooms belonging to the current user
    private void loadOwnerRooms() {
        ownerRooms.clear();
        String sql = "SELECT * FROM rooms WHERE owner_id = " + LoginController.getCurrentUserId();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setPrice(rs.getDouble("price"));
                room.setDescription(rs.getString("description"));
                room.setContactNumber(rs.getString("contact_number"));
                room.setMapLink(rs.getString("map_link"));
                ownerRooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load your rooms.", Alert.AlertType.ERROR);
        }
    }

    private void loadRooms() {
        rooms.clear();
        String sql = "SELECT * FROM rooms WHERE owner_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, LoginController.getCurrentUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setOwnerId(rs.getInt("owner_id"));
                    room.setLocation(rs.getString("location"));
                    room.setPrice(rs.getDouble("price"));
                    room.setDescription(rs.getString("description"));
                    room.setContactNumber(rs.getString("contact_number"));
                    room.setMapLink(rs.getString("map_link"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load rooms.", Alert.AlertType.ERROR);
        }
    }

    private void fillRoomDetails(Room room) {
        locationComboBox.setValue(room.getLocation());
        priceField.setText(String.valueOf(room.getPrice()));
        descriptionArea.setText(room.getDescription());
        contactField.setText(room.getContactNumber());
        mapLinkField.setText(room.getMapLink());
    }

    @FXML
    public void handleAddRoom() {
        String location = locationComboBox.getValue();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        String contact = contactField.getText();
        String mapLink = mapLinkField.getText();

        if (location == null || priceText.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields", Alert.AlertType.WARNING);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            String sql = "INSERT INTO rooms (owner_id, location, price, description, contact_number, map_link) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, LoginController.getCurrentUserId());
                stmt.setString(2, location);
                stmt.setDouble(3, price);
                stmt.setString(4, description);
                stmt.setString(5, contact);
                stmt.setString(6, mapLink);

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room added successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadRooms();
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid price", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add room.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleUpdateRoom() {
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to update", Alert.AlertType.ERROR);
            return;
        }

        String location = locationComboBox.getValue();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        String contact = contactField.getText();
        String mapLink = mapLinkField.getText();

        if (location == null || priceText.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields", Alert.AlertType.WARNING);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            String sql = "UPDATE rooms SET location = ?, price = ?, description = ?, contact_number = ?, map_link = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, location);
                stmt.setDouble(2, price);
                stmt.setString(3, description);
                stmt.setString(4, contact);
                stmt.setString(5, mapLink);
                stmt.setInt(6, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room updated successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadRooms();
                    selectedRoom = null;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid price", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update room.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDeleteRoom() {
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this room?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM rooms WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room deleted successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadRooms();
                    selectedRoom = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete room.", Alert.AlertType.ERROR);
            }
        }
    }

    private void clearForm() {
        locationComboBox.setValue(null);
        priceField.clear();
        descriptionArea.clear();
        contactField.clear();
        mapLinkField.clear();
    }

    @FXML
    public void handleFeedback() {
        SceneManager.switchScene("feedback-page.fxml", "styles1.css", 800, 600);
        FeedbackController controller = (FeedbackController) SceneManager.getController();
        if (controller != null) {
            controller.setPreviousPage("house-owner-dashboard.fxml", "styles1.css");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        javafx.application.Platform.runLater(() -> {
            try {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                URL imageUrl = getClass().getResource("/images/img.png");
                if (imageUrl != null) {
                    Image icon = new Image(imageUrl.toExternalForm());
                    alertStage.getIcons().add(icon);
                } else {
                    System.err.println("Warning: The image file was not found. Please check the path: /images/img.png");
                }

                // Try to style the alert to match the application theme
                try {
                    URL cssUrl = getClass().getResource("/css/login.css");
                    if (cssUrl != null) {
                        alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
                    } else {
                        System.err.println("Warning: Could not apply CSS to alert dialog, file not found: /css/login.css");
                    }
                } catch (Exception e) {
                    System.err.println("Error applying CSS to alert dialog: " + e.getMessage());
                }

                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
}


