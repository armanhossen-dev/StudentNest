package com.studentnest.controllers;

import com.studentnest.models.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardController {

    // ========== EXISTING FXML VARIABLES ==========
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private VBox roomsContainer;
    @FXML private Button logoutButton;
    @FXML private Label userCountLabel;
    @FXML private Label roomCountLabel;
    @FXML private Button aboutUsButton;
    @FXML private Button feedbackButton;

    // ========== NEW FXML VARIABLES FOR THEME FUNCTIONALITY ==========
    @FXML private Button themeToggleButton;
    @FXML private BorderPane rootContainer;

    // ========== EXISTING VARIABLES ==========
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Room> rooms = FXCollections.observableArrayList();
    private Room selectedRoom = null;

    @FXML
    public void initialize() {
        // Defer initialization to ensure FXML elements are properly injected
        Platform.runLater(() -> {
            initializeComponents();
        });
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

            // Step 1: Initialize all UI components and set initial values
            initializeUIComponents();

            // Step 2: Add listeners and set up bindings
            setupListenersAndBindings();

            // Step 3: Load initial data
            loadRooms();

            // Step 4: Update quick statistics
            updateQuickStats();

            System.out.println("Student Dashboard initialized successfully");

        } catch (Exception e) {
            System.err.println("Error during dashboard initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUIComponents() {
        // Set welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + LoginController.getCurrentUserName() + "!");
        }

        // Initialize location combo box
        if (locationFilter != null) {
            locationFilter.getItems().addAll("All Locations", "Khagan", "Candgaon", "Charabag",
                    "Kumkumari", "Dattopara", "Shadhupara");
            locationFilter.setValue("All Locations");
        }

        // Initialize price combo box
        if (priceFilter != null) {
            priceFilter.getItems().addAll("All Prices", "0-5000", "5000-10000", "10000-15000", "15000+");
            priceFilter.setValue("All Prices");
        }
    }

    private void setupListenersAndBindings() {
        // Add any additional listeners here if needed
        // The roomsContainer (VBox) will be populated dynamically in displayRooms()
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

            URL cssUrl = getClass().getResource("/css/student-dashboard.css");
            if (cssUrl != null) {
                rootContainer.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS stylesheet loaded successfully");
            } else {
                System.err.println("Warning: CSS file not found at /css/student-dashboard.css");
                // Try alternative path
                cssUrl = getClass().getResource("/student-dashboard.css");
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
            // Update room count using the existing roomCountLabel from FXML
            if (roomCountLabel != null) {
                roomCountLabel.setText(String.valueOf(rooms.size()));
            }

            // Update user count if needed
            if (userCountLabel != null) {
                updateUserCount();
            }
        } catch (Exception e) {
            System.err.println("Error updating quick stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUserCount() {
        try {
            String sql = "SELECT COUNT(*) as user_count FROM users";
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    userCountLabel.setText(String.valueOf(rs.getInt("user_count")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating user count: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fill room details (if you have form fields to populate)
     */
    private void fillRoomDetails(Room room) {
        // This method can be used if you have form fields to populate
        // For now, it's just a placeholder
        System.out.println("Selected room: " + room.getLocation() + " - ৳" + room.getPrice());
    }

    /**
     * Handle theme toggle button click
     */
    @FXML
    private void handleThemeToggle() {
        if (rootContainer != null) {
            if (rootContainer.getStyleClass().contains("dark-theme")) {
                rootContainer.getStyleClass().remove("dark-theme");
                if (themeToggleButton != null) {
                    themeToggleButton.setText("🌙"); // Moon for switching to dark mode
                }
            } else {
                rootContainer.getStyleClass().add("dark-theme");
                if (themeToggleButton != null) {
                    themeToggleButton.setText("☀"); // Sun for switching to light mode
                }
            }
        }
    }

    private void loadRooms() {
        rooms.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Updated query to match your actual database structure
            String sql = "SELECT r.*, u.name as owner_name " +
                    "FROM rooms r " +
                    "JOIN users u ON r.owner_id = u.id " +
                    "ORDER BY r.id";
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
                room.setContactInfo(rs.getString("contact_number"));
                room.setMapLink(rs.getString("map_link"));
                room.setOwnerName(rs.getString("owner_name"));

                // Handle images from image1_path and image2_path columns
                room.setImages(new ArrayList<>());
                String image1Path = rs.getString("image1_path");
                String image2Path = rs.getString("image2_path");

                if (image1Path != null && !image1Path.isEmpty()) {
                    room.getImages().add(image1Path);
                }
                if (image2Path != null && !image2Path.isEmpty()) {
                    room.getImages().add(image2Path);
                }

                rooms.add(room);
                System.out.println("Loaded room: " + room.getLocation() + " - ৳" + room.getPrice()); // Debug output
            }

            System.out.println("Total rooms loaded: " + rooms.size()); // Debug output
            displayRooms();
            updateQuickStats(); // Update stats after loading rooms
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load rooms: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayRooms() {
        if (roomsContainer != null) {
            roomsContainer.getChildren().clear();

            for (Room room : rooms) {
                if (shouldShowRoom(room)) {
                    VBox roomCard = createRoomCard(room);
                    roomsContainer.getChildren().add(roomCard);
                }
            }
        }
    }


    private VBox createRoomCard(Room room) {
        VBox card = new VBox(10);
        card.getStyleClass().add("room-card");

        HBox imageContainer = new HBox(10);
        imageContainer.getStyleClass().add("image-container");
        // This loop iterates through the list of image paths for the room
        for (String imagePath : room.getImages()) {
            // This method loads the image and returns an ImageView
            ImageView imageView = loadRoomImage(imagePath);
            if (imageView != null) {
                // If the image loads, it is added to the HBox
                imageContainer.getChildren().add(imageView);
            }
        }

        // Other room details...
        Label titleLabel = new Label("Room in " + room.getLocation());
        titleLabel.getStyleClass().add("location-label");

        Label priceLabel = new Label("Price: ৳" + room.getPrice() + "/month");
        priceLabel.getStyleClass().add("price-label");

        // Corrected code: Declare the missing variables
        Label descriptionLabel = new Label("Description: " + room.getDescription());
        Label contactLabel = new Label("Contact: " + room.getContactNumber());

        Button contactButton = new Button("View Details");
        contactButton.getStyleClass().add("contact-btn");
        contactButton.setOnAction(e -> showRoomDetails(room));

        // All elements, including the image container, are added to the card
        card.getChildren().addAll(imageContainer, titleLabel, priceLabel, descriptionLabel, contactLabel, contactButton);
        return card;
    }

    private boolean shouldShowRoom(Room room) {
        String selectedLocation = locationFilter != null ? locationFilter.getValue() : "All Locations";
        String selectedPrice = priceFilter != null ? priceFilter.getValue() : "All Prices";

        if (selectedLocation == null) selectedLocation = "All Locations";
        if (selectedPrice == null) selectedPrice = "All Prices";

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



    private ImageView loadRoomImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("room-image");
                return imageView;
            } else {
                System.err.println("Image file not found: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    private void showRoomDetails(Room room) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Details");
        alert.setHeaderText("Room in " + room.getLocation());

        VBox content = new VBox(10);

        HBox imageContainer = new HBox(10);
        for (String imagePath : room.getImages()) {
            ImageView imageView = loadRoomImage(imagePath);
            if (imageView != null) {
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.getStyleClass().add("room-image-details");
                imageContainer.getChildren().add(imageView);
            }
        }
        content.getChildren().add(imageContainer);

        Label priceLabel = new Label("Price: ৳" + room.getPrice() + "/month");
        Label descriptionLabel = new Label("Description: " + room.getDescription());
        Label contactLabel = new Label("Contact: " + room.getContactNumber());
        Label ownerLabel = new Label("Owner: " + room.getOwnerName());

        content.getChildren().addAll(priceLabel, descriptionLabel, contactLabel, ownerLabel);

        if (room.getMapLink() != null && !room.getMapLink().isEmpty()) {
            Hyperlink mapLink = new Hyperlink("View on Map");
            mapLink.setOnAction(e -> openMapLink(room.getMapLink()));
            content.getChildren().add(mapLink);
        }

        alert.getDialogPane().setContent(content);

        // Apply styling to the alert
        try {
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            URL imageUrl = getClass().getResource("/images/img.png");
            if (imageUrl != null) {
                Image icon = new Image(imageUrl.toExternalForm());
                alertStage.getIcons().add(icon);
            }

            // Try to style the alert to match the application theme
            URL cssUrl = getClass().getResource("/css/student-dashboard.css");
            if (cssUrl != null) {
                alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error styling alert dialog: " + e.getMessage());
        }

        alert.showAndWait();
    }

    private void openMapLink(String mapLink) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(mapLink));
        } catch (Exception e) {
            System.err.println("Error opening map link: " + e.getMessage());
        }
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
    public void handleAboutUs() {
        SceneManager.switchScene("about-us.fxml", "student-dashboard.css", 800, 600);
    }

    @FXML
    public void handleFeedback() {
        SceneManager.switchScene("feedback-page.fxml", "student-dashboard.css", 800, 600);
        FeedbackController controller = (FeedbackController) SceneManager.getController();
        if (controller != null) {
            controller.setPreviousPage("student-dashboard.fxml", "student-dashboard.css");
        }
    }

    @FXML
    public void handleLogout() {
        try {
            SceneManager.switchScene("login.fxml", "login.css", 1000, 620);
        } catch (Exception e) {
            System.err.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Unable to logout. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show alert dialog with styling
     */
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
                    URL cssUrl = getClass().getResource("/css/student-dashboard.css");
                    if (cssUrl != null) {
                        alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
                    } else {
                        System.err.println("Warning: Could not apply CSS to alert dialog, file not found: /css/student-dashboard.css");
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