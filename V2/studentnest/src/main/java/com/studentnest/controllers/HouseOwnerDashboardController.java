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

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");

        locationComboBox.getItems().addAll("Khagan", "Candgaon", "Charabag",
                "Kumkumari", "Dattopara", "Shadhupara");

        roomsList.setItems(rooms);
        roomsList.setCellFactory(lv -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getLocation() + " - ৳" + room.getPrice() + "/month");
                }
            }
        });

        roomsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRoom = newSelection;
                fillRoomDetails(newSelection);
            }
        });

        loadRooms();
    }

    private void loadRooms() {
        rooms.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM rooms WHERE owner_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoginController.getCurrentUserId());
            ResultSet rs = stmt.executeQuery();

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
        } catch (SQLException e) {
            e.printStackTrace();
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
            showAlert("Error", "Please fill in all required fields");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO rooms (owner_id, location, price, description, contact_number, map_link) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoginController.getCurrentUserId());
            stmt.setString(2, location);
            stmt.setDouble(3, price);
            stmt.setString(4, description);
            stmt.setString(5, contact);
            stmt.setString(6, mapLink);

            if (stmt.executeUpdate() > 0) {
                showAlert("Success", "Room added successfully!");
                clearForm();
                loadRooms();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid price");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add room");
        }
    }

    @FXML
    public void handleUpdateRoom() {
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to update");
            return;
        }

        String location = locationComboBox.getValue();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        String contact = contactField.getText();
        String mapLink = mapLinkField.getText();

        if (location == null || priceText.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert("Error", "Please fill in all required fields");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE rooms SET location = ?, price = ?, description = ?, contact_number = ?, map_link = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, location);
            stmt.setDouble(2, price);
            stmt.setString(3, description);
            stmt.setString(4, contact);
            stmt.setString(5, mapLink);
            stmt.setInt(6, selectedRoom.getId());

            if (stmt.executeUpdate() > 0) {
                showAlert("Success", "Room updated successfully!");
                clearForm();
                loadRooms();
                selectedRoom = null;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid price");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update room");
        }
    }

    @FXML
    public void handleDeleteRoom() {
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this room?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "DELETE FROM rooms WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room deleted successfully!");
                    clearForm();
                    loadRooms();
                    selectedRoom = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete room");
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
    public void handleLogout() {
        SceneManager.switchScene("login.fxml", 800, 600);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}