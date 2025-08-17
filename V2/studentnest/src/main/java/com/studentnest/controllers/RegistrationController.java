package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;

/**
 * Controller class for the registration FXML file.
 * Handles user input and logic for creating a new user account.
 */
public class RegistrationController {

    // FXML fields linked to the UI components in registration.fxml
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    /**
     * Initializes the controller. This method is called automatically after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        try {
            // Populate the ComboBox with user types.
            userTypeComboBox.getItems().clear(); // Clear any existing items first
            userTypeComboBox.getItems().addAll("Student", "House Owner");
            // Set the default value to "Student".
            userTypeComboBox.setValue("Student");

            // Load CSS stylesheet
            loadStylesheet();

            System.out.println("Registration controller initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing registration controller: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load CSS stylesheet for the registration page
     */
    private void loadStylesheet() {
        try {
            if (registerButton != null && registerButton.getScene() != null) {
                registerButton.getScene().getStylesheets().clear();
                // Load both common styles and registration-specific styles
                registerButton.getScene().getStylesheets().addAll(
                        getClass().getResource("/css/registration.css").toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS stylesheets: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the "Register" button is clicked.
     * Retrieves user input, validates it, and attempts to register the new user.
     */
    @FXML
    public void handleRegister() {
        try {
            String userType = userTypeComboBox.getValue();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            // Enhanced input validation
            if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields", Alert.AlertType.WARNING);
                return;
            }

            if (userType == null || userType.isEmpty()) {
                showAlert("Error", "Please select a user type", Alert.AlertType.WARNING);
                return;
            }

            // Additional validation for minimum requirements
            if (username.length() < 3) {
                showAlert("Error", "Username must be at least 3 characters long", Alert.AlertType.WARNING);
                return;
            }

            if (password.length() < 4) {
                showAlert("Error", "Password must be at least 4 characters long", Alert.AlertType.WARNING);
                return;
            }

            // Disable register button during processing
            registerButton.setDisable(true);
            registerButton.setText("Registering...");

            // Attempt to register the user in the database.
            if (registerUser(name, phone, username, password, userType)) {
                // Show a success message if registration is successful.
                showAlert("Success", "Registration successful! Please login.", Alert.AlertType.INFORMATION);
                // Switch back to the login scene with consistent dimensions
//                SceneManager.switchScene("login.fxml", 1000, 600);
                try {
                    // Switch to registration scene
                    SceneManager.switchScene("login.fxml", 1000, 620);
                    System.out.println("Navigating to registration page...");

                    // Get the current scene from the primary stage
                    javafx.scene.Scene currentScene = SceneManager.getPrimaryStage().getScene();

                    // Clear existing stylesheets to avoid conflicts
                    currentScene.getStylesheets().clear();

                    // Apply the new stylesheet for the registration page
                    currentScene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
                    System.out.println("login.css stylesheet applied to registration scene.");

                } catch (Exception e) {
                    System.err.println("Error loading login page or applying stylesheet: " + e.getMessage());
                    e.printStackTrace();

                    showAlert("Navigation Error",
                            "Unable to load login page or apply stylesheet. Please check if registration.fxml and /css/login.css exist.\nError: " + e.getMessage(),
                            Alert.AlertType.ERROR);
                }
            } else {
                // Show an error message if registration fails.
                showAlert("Error", "Registration failed. Username might already exist.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred during registration.", Alert.AlertType.ERROR);
        } finally {
            // Re-enable register button
            registerButton.setDisable(false);
            registerButton.setText("SIGN UP");
        }
    }

    /**
     * Inserts a new user into the database.
     * @param name The full name of the user.
     * @param phone The phone number of the user.
     * @param username The username for the new account.
     * @param password The password for the new account.
     * @param userType The type of user ("Student" or "House Owner").
     * @return true if the user was successfully registered, false otherwise.
     */
    private boolean registerUser(String name, String phone, String username, String password, String userType) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Establish a database connection.
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return false;
            }

            // First check if username already exists
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Username already exists: " + username);
                return false;
            }

            // Prepare a SQL statement to prevent SQL injection.
            String sql = "INSERT INTO users (name, phone, username, password, user_type) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, username);
            stmt.setString(4, password); // Consider hashing passwords in production
            stmt.setString(5, userType);

            // Execute the update and check the result.
            int result = stmt.executeUpdate();
            boolean success = result > 0;

            if (success) {
                System.out.println("User registered successfully: " + username);
            }

            return success;
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes.
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Clean up database resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }



    @FXML
    public void handleBackToLogin() {
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
            showAlert("Navigation Error",
                    "Unable to return to login page. Please try again.",
                    Alert.AlertType.ERROR);
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
