package com.studentnest.controllers;

import javafx.application.Platform;
import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;

import com.studentnest.database.DatabaseConnection;

public class HouseOwnerDashboardController {

    // ========== FXML VARIABLES ==========
    @FXML private Label welcomeLabel;
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
    @FXML private ComboBox<String> roomTypeComboBox;
    @FXML private Label image1PathLabel;
    @FXML private Label image2PathLabel;
    @FXML private Button feedbackButton;
    @FXML private Button themeToggleButton;
    @FXML private BorderPane rootContainer;
    @FXML private Label totalRoomsLabel;
    @FXML private Label occupiedRoomsLabel;

    // ========== INSTANCE VARIABLES ==========
    private File image1File;
    private File image2File;
    private ObservableList<Room> rooms = FXCollections.observableArrayList();
    private Room selectedRoom = null;

    @FXML
    public void initialize() {
        // Defer initialization to ensure FXML elements are properly injected
        Platform.runLater(this::initializeComponents);
    }

    private void initializeComponents() {
        try {
            // Apply CSS stylesheet programmatically
            applyCSSStylesheet();

            // Apply dark theme by default (only if rootContainer is not null)
            if (rootContainer != null) {
                rootContainer.getStyleClass().add("dark-theme");
                if (themeToggleButton != null) {
                    themeToggleButton.setText("☀"); // Set to sun icon since we're starting in dark mode
                }
            }

            // Set welcome message
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");
            }

            // Initialize location combo box
            if (locationComboBox != null) {
                locationComboBox.getItems().addAll("Khagan", "Candgaon", "Charabag",
                        "Kumkumari", "Dattopara", "Shadhupara");
            }

            // Initialize rooms list
            if (roomsList != null) {
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

                // Add selection listener for rooms list
                roomsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedRoom = newSelection;
                        fillRoomDetails(newSelection);
                    }
                });
            }

            // Initialize room types combo box
            if (roomTypeComboBox != null) {
                ObservableList<String> roomTypes = FXCollections.observableArrayList("Single", "Shared", "Family");
                roomTypeComboBox.setItems(roomTypes);
            }

            // Load rooms and update stats
            loadRooms();
            updateQuickStats();

            System.out.println("House Owner Dashboard initialized successfully");

        } catch (Exception e) {
            System.err.println("Error during dashboard initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Apply CSS stylesheet programmatically
     */
    private void applyCSSStylesheet() {
        try {
            // Check if rootContainer is null
            if (rootContainer == null) {
                System.err.println("Warning: rootContainer is null, cannot apply CSS");
                return;
            }

            URL cssUrl = getClass().getResource("/css/modern-dashboard.css");
            if (cssUrl != null) {
                rootContainer.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS stylesheet loaded successfully");
            } else {
                System.err.println("Warning: CSS file not found at /css/modern-dashboard.css");
                // Try alternative path
                cssUrl = getClass().getResource("/modern-dashboard.css");
                if (cssUrl != null) {
                    rootContainer.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS stylesheet loaded from alternative path");
                } else {
                    System.err.println("Error: Could not find CSS file in any expected location");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS stylesheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update quick stats in sidebar
     */
    private void updateQuickStats() {
        try {
            if (totalRoomsLabel != null) {
                totalRoomsLabel.setText(String.valueOf(rooms.size()));
            }

            // For now, set occupied rooms to a placeholder
            // You can implement actual logic to count occupied rooms from database
            if (occupiedRoomsLabel != null) {
                occupiedRoomsLabel.setText(String.valueOf(rooms.size() > 0 ? (int)(rooms.size() * 0.6) : 0));
            }
        } catch (Exception e) {
            System.err.println("Error updating quick stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRooms() {
        rooms.clear();
        String sql = "SELECT id, owner_id, location, price, description, contact_number, map_link, room_type, image1_path, image2_path FROM rooms WHERE owner_id = ?";
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
                    room.setContactInfo(rs.getString("contact_number"));
                    room.setMapLink(rs.getString("map_link"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setImage1Path(rs.getString("image1_path"));
                    room.setImage2Path(rs.getString("image2_path"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load rooms.", Alert.AlertType.ERROR);
        }

        // Update stats after loading rooms
        updateQuickStats();
    }

    @FXML
    private void handleUploadImage1() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        image1File = fileChooser.showOpenDialog(null);
        if (image1File != null) {
            image1PathLabel.setText(image1File.getName());
        }
    }

    @FXML
    private void handleUploadImage2() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        image2File = fileChooser.showOpenDialog(null);
        if (image2File != null) {
            image2PathLabel.setText(image2File.getName());
        }
    }

    private void fillRoomDetails(Room room) {
        locationComboBox.setValue(room.getLocation());
        priceField.setText(String.valueOf(room.getPrice()));
        descriptionArea.setText(room.getDescription());
        contactField.setText(room.getContactInfo());
        mapLinkField.setText(room.getMapLink());
        roomTypeComboBox.setValue(room.getRoomType());
    }

    @FXML
    public void handleAddRoom() {
        String location = locationComboBox.getValue();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        String contact = contactField.getText();
        String mapLink = mapLinkField.getText();
        String roomType = roomTypeComboBox.getValue();

        // Handle image file saving
        String image1Path = saveImage(image1File);
        String image2Path = saveImage(image2File);

        if (location == null || priceText.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields", Alert.AlertType.WARNING);
            return;
        }
        if (roomType == null) {
            showAlert("Validation Error", "Please select a room type.", Alert.AlertType.WARNING);
            return;
        }
        try {
            double price = Double.parseDouble(priceText);
            String sql = "INSERT INTO rooms (owner_id, location, price, description, contact_number, map_link, room_type, image1_path, image2_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, LoginController.getCurrentUserId());
                stmt.setString(2, location);
                stmt.setDouble(3, price);
                stmt.setString(4, description);
                stmt.setString(5, contact);
                stmt.setString(6, mapLink);
                stmt.setString(7, roomType);
                stmt.setString(8, image1Path);
                stmt.setString(9, image2Path);

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room added successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadRooms(); // This will also update stats
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid price", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add room.", Alert.AlertType.ERROR);
        }
    }

    // Helper method to save the image to a local directory
    private String saveImage(File sourceFile) {
        if (sourceFile == null) {
            return null;
        }

        try {
            // Get the absolute path to a known location, e.g., the user's home directory or a dedicated application data folder.
            // This makes the image path independent of the application's launch location.
            Path destinationDir = Path.of(System.getProperty("user.home"), "StudentNest", "room_images");

            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            // Create a unique file name to avoid overwriting
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path destinationFile = destinationDir.resolve(fileName);
            Files.copy(sourceFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Return the absolute path of the saved file
            return destinationFile.toAbsolutePath().toString();
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
            return null;
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
        String roomType = roomTypeComboBox.getValue();

        if (location == null || priceText.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields", Alert.AlertType.WARNING);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            // Handle image updates
            String image1Path = saveImage(image1File);
            String image2Path = saveImage(image2File);

            String sql;
            if (image1Path != null || image2Path != null) {
                sql = "UPDATE rooms SET location = ?, price = ?, description = ?, contact_number = ?, map_link = ?, room_type = ?" +
                        (image1Path != null ? ", image1_path = ?" : "") +
                        (image2Path != null ? ", image2_path = ?" : "") +
                        " WHERE id = ?";
            } else {
                sql = "UPDATE rooms SET location = ?, price = ?, description = ?, contact_number = ?, map_link = ?, room_type = ? WHERE id = ?";
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                stmt.setString(paramIndex++, location);
                stmt.setDouble(paramIndex++, price);
                stmt.setString(paramIndex++, description);
                stmt.setString(paramIndex++, contact);
                stmt.setString(paramIndex++, mapLink);
                stmt.setString(paramIndex++, roomType);

                if (image1Path != null) {
                    stmt.setString(paramIndex++, image1Path);
                }
                if (image2Path != null) {
                    stmt.setString(paramIndex++, image2Path);
                }

                stmt.setInt(paramIndex, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room updated successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadRooms(); // This will also update stats
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
                    loadRooms(); // This will also update stats
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
        roomTypeComboBox.setValue(null);
        if (image1PathLabel != null) {
            image1PathLabel.setText("No image selected");
        }
        if (image2PathLabel != null) {
            image2PathLabel.setText("No image selected");
        }
        image1File = null;
        image2File = null;
    }

    @FXML
    public void handleManageRooms() {
        System.out.println("Manage Rooms button clicked.");
    }

    @FXML
    private void handleThemeToggle() {
        if (rootContainer != null) {
            if (rootContainer.getStyleClass().contains("dark-theme")) {
                rootContainer.getStyleClass().remove("dark-theme");
                if (themeToggleButton != null) {
                    themeToggleButton.setText("🌙"); // Moon for dark mode
                }
            } else {
                rootContainer.getStyleClass().add("dark-theme");
                if (themeToggleButton != null) {
                    themeToggleButton.setText("☀"); // Sun for light mode
                }
            }
        }
    }

    @FXML
    public void handleFeedback(ActionEvent event) {
        try {
            // Use switchSceneAndGetController to get the controller instance
            FeedbackController controller = SceneManager.switchSceneAndGetController(
                    event,
                    "/fxml/feedback-page.fxml",
                    "StudentNest - Feedback"
            );

            // Set previous page information if controller was loaded successfully
            if (controller != null) {
                controller.setPreviousPage("/fxml/houseowner-dashboard.fxml", "/css/modern-dashboard.css");
            } else {
                System.err.println("Warning: Could not get FeedbackController instance");
            }

        } catch (Exception e) {
            System.err.println("Error navigating to feedback page: " + e.getMessage());
            e.printStackTrace();

            // Fallback: Navigate without getting controller
            try {
                SceneManager.switchScene(event, "/fxml/feedback-page.fxml", "/css/styles1.css", 800, 600,
                        "StudentNest - Feedback");
            } catch (Exception fallbackError) {
                System.err.println("Fallback navigation also failed: " + fallbackError.getMessage());
                // Show error alert to user
                showAlert("Navigation Error", "Could not open feedback page. Please try again.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            // Use the correct SceneManager method with ActionEvent and proper parameters
            SceneManager.switchScene(event, "/fxml/login.fxml", "/css/login.css", 1000, 620, "StudentNest - Login");
            System.out.println("Navigating back to login page...");
        } catch (Exception e) {
            System.err.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Single showAlert method to avoid duplication
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                // Set icon for the alert window
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

    // Overloaded method for default INFO alerts
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
}