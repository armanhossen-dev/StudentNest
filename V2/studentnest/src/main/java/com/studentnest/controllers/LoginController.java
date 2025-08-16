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
 * Controller class for the login FXML file.
 * Handles user input and logic for user authentication.
 */
public class LoginController {

    // FXML Components
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;

    // User Session Management
    private static int currentUserId;
    private static String currentUserName;

    /**
     * Initialize the controller - called automatically by JavaFX
     */
    @FXML
    public void initialize() {
        try {
            // Setup user type dropdown
            userTypeComboBox.getItems().clear();
            userTypeComboBox.getItems().addAll("Student", "House Owner", "Admin");
            userTypeComboBox.setValue("Student");

            System.out.println("Login controller initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing login controller: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle login button click
     */
    @FXML
    public void handleLogin() {
        try {
            String userType = userTypeComboBox.getValue();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields", Alert.AlertType.WARNING);
                return;
            }

            if (userType == null || userType.isEmpty()) {
                showAlert("Error", "Please select a user type", Alert.AlertType.WARNING);
                return;
            }

            // Show loading state
            loginButton.setDisable(true);
            loginButton.setText("Logging in...");

            // Authenticate user in background thread to prevent UI freezing
            javafx.concurrent.Task<Boolean> loginTask = new javafx.concurrent.Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return authenticateUser(username, password, userType);
                }

                @Override
                protected void succeeded() {
                    // Re-enable login button
                    loginButton.setDisable(false);
                    loginButton.setText("LOGIN");

                    if (getValue()) {
                        // Navigate to appropriate dashboard
                        navigateToDashboard(userType);
                    } else {
                        showAlert("Login Failed",
                                "Invalid username, password, or user type. Please try again.",
                                Alert.AlertType.ERROR);
                    }
                }

                @Override
                protected void failed() {
                    // Re-enable login button
                    loginButton.setDisable(false);
                    loginButton.setText("LOGIN");

                    showAlert("Connection Error",
                            "Unable to connect to the database. Please try again later.",
                            Alert.AlertType.ERROR);
                }
            };

            new Thread(loginTask).start();
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();

            // Re-enable button in case of error
            loginButton.setDisable(false);
            loginButton.setText("LOGIN");

            showAlert("Error", "An unexpected error occurred during login.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigate to the appropriate dashboard based on user type
     */
    private void navigateToDashboard(String userType) {
        try {
            switch (userType) {
                case "Student":
                    SceneManager.switchScene("student-dashboard.fxml", 1200, 800);
                    break;
                case "House Owner":
                    SceneManager.switchScene("houseowner-dashboard.fxml", 1200, 800);
                    break;
                case "Admin":
                    SceneManager.switchScene("admin-dashboard.fxml", 1200, 800);
                    break;
                default:
                    showAlert("Error", "Unknown user type: " + userType, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error",
                    "Unable to load dashboard. Please try again.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Authenticate user against the database
     */
    private boolean authenticateUser(String username, String password, String userType) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection failed");
                return false;
            }

            String sql = "SELECT id, name FROM users WHERE username = ? AND password = ? AND user_type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, userType);

            rs = stmt.executeQuery();
            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUserName = rs.getString("name");
                System.out.println("User authenticated: " + currentUserName + " (ID: " + currentUserId + ")");
                return true;
            } else {
                System.out.println("Authentication failed for username: " + username);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Clean up database resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    /**
     * Handle create account button click - FIXED VERSION
     */
//    @FXML
//    public void handleCreateAccount() {
//        try {
//            // Switch to registration scene
//            SceneManager.switchScene("registration.fxml", 1000, 647);
//            System.out.println("Navigating to registration page...");
//
//        } catch (Exception e) {
//            System.err.println("Error loading registration page: " + e.getMessage());
//            e.printStackTrace();
//
//            showAlert("Navigation Error",
//                    "Unable to load registration page. Please check if registration.fxml exists.\nError: " + e.getMessage(),
//                    Alert.AlertType.ERROR);
//        }
//    }
    @FXML
    public void handleCreateAccount() {
        try {
            // Switch to registration scene
            SceneManager.switchScene("registration.fxml", 1000, 647);
            System.out.println("Navigating to registration page...");

            // Get the current scene from the primary stage
            javafx.scene.Scene currentScene = SceneManager.getPrimaryStage().getScene();

            // Clear existing stylesheets to avoid conflicts
            currentScene.getStylesheets().clear();

            // Apply the new stylesheet for the registration page
            currentScene.getStylesheets().add(getClass().getResource("/css/registration.css").toExternalForm());
            System.out.println("login.css stylesheet applied to registration scene.");

        } catch (Exception e) {
            System.err.println("Error loading registration page or applying stylesheet: " + e.getMessage());
            e.printStackTrace();

            showAlert("Navigation Error",
                    "Unable to load registration page or apply stylesheet. Please check if registration.fxml and /css/login.css exist.\nError: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }


    /**
     * Show alert dialog with specified type
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
                    alert.getDialogPane().getStylesheets().add(
                            getClass().getResource("/css/login.css").toExternalForm()
                    );
                } catch (Exception e) {
                    // If login.css doesn't exist, try main stylesheet
                    try {
                        alert.getDialogPane().getStylesheets().add(
                                getClass().getResource("/css/styles1.css").toExternalForm()
                        );
                    } catch (Exception e2) {
                        // Continue without styling if no CSS is available
                        System.err.println("Warning: Could not apply CSS to alert dialog");
                    }
                }

                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
            }
        });
    }

    // Getter methods for current user session
    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * Clear current user session (useful for logout)
     */
    public static void clearCurrentUser() {
        currentUserId = 0;
        currentUserName = null;
        System.out.println("User session cleared");
    }

    /**
     * Check if user is currently logged in
     */
    public static boolean isUserLoggedIn() {
        return currentUserId > 0 && currentUserName != null;
    }
}
