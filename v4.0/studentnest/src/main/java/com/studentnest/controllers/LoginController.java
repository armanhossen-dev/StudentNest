package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;

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
    @FXML private TextField passwordTextField; // For showing password as text
    @FXML private Button passwordToggleButton;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;

    // Password visibility state
    private boolean isPasswordVisible = false;

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

            // Bind password fields together
            setupPasswordToggle();

            System.out.println("Login controller initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing login controller: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup password visibility toggle functionality
     */
    private void setupPasswordToggle() {
        // Bind the text fields so they stay in sync
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // Initially hide the text field
        passwordTextField.setVisible(false);
        passwordTextField.setManaged(false);

        // Set initial toggle button state
        passwordToggleButton.setText("👁");
    }

    /**
     * Toggle password visibility
     */
    @FXML
    public void togglePasswordVisibility() {
        try {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                // Show password as text
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                passwordToggleButton.setText("🙈"); // Hide icon
                passwordTextField.requestFocus();
                passwordTextField.positionCaret(passwordTextField.getText().length());
            } else {
                // Hide password (show as dots)
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordToggleButton.setText("👁"); // Show icon
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
            }
        } catch (Exception e) {
            System.err.println("Error toggling password visibility: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the current password value from the active field
     */
    private String getCurrentPassword() {
        return isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
    }

    /**
     * Handle login button click
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            String userType = userTypeComboBox.getValue();
            String username = usernameField.getText().trim();
            String password = getCurrentPassword();

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
            loginButton.setText("LOGGING IN...");

            // Authenticate user in background thread to prevent UI freezing
            Task<Boolean> loginTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return authenticateUser(username, password, userType);
                }

                @Override
                protected void succeeded() {
                    // Re-enable login button
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        loginButton.setText("LOGIN");

                        if (getValue()) {
                            // Navigate to appropriate dashboard
                            navigateToDashboard(userType, event);
                        } else {
                            showAlert("Login Failed",
                                    "Invalid username, password, or user type. Please try again.",
                                    Alert.AlertType.ERROR);
                        }
                    });
                }

                @Override
                protected void failed() {
                    // Re-enable login button
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        loginButton.setText("LOGIN");

                        showAlert("Connection Error",
                                "Unable to connect to the database. Please try again later.",
                                Alert.AlertType.ERROR);
                    });
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

    // Inside LoginController.java

     /**
     * Navigate to the appropriate dashboard based on user type
     */
        /**
     * Navigate to the appropriate dashboard based on user type
     */
    private void navigateToDashboard(String userType, ActionEvent event) {
        try {
            String fxmlFile;
            String cssFile;
            String title;
            int width, height;

            switch (userType) {
                case "Student":
                    fxmlFile = "/fxml/student-dashboard.fxml";
                    cssFile = "/css/student-dashboard.css";
                    title = "StudentNest - Student Dashboard";
                    width = 1400;
                    height = 850;
                    break;
                case "House Owner":
                    fxmlFile = "/fxml/houseowner-dashboard.fxml";
                    cssFile = "/css/modern-dashboard.css";
                    title = "StudentNest - House Owner Dashboard";
                    width = 1400;
                    height = 850;
                    break;
                case "Admin":
                    fxmlFile = "/fxml/admin-dashboard.fxml";
                    // Use the CSS file name that matches your project structure
                    cssFile = "/css/AdminDashboard.css"; // This matches your FXML reference
                    title = "StudentNest - Admin Dashboard";
                    width = 1400;
                    height = 850;
                    break;
                default:
                    showAlert("Error", "Unknown user type: " + userType, Alert.AlertType.ERROR);
                    return;
            }

            // Use the correct SceneManager method with ActionEvent
            SceneManager.switchScene(event, fxmlFile, cssFile, width, height, title);

        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load the dashboard. Please try again.", Alert.AlertType.ERROR);
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
     * Handle create account button click
     */
    @FXML
    public void handleCreateAccount(ActionEvent event) {
        try {
            // Switch to registration scene using the SceneManager with correct parameters
            SceneManager.switchScene(event, "/fxml/registration.fxml", "/css/registration.css", 1000, 680, "StudentNest - Create Account");
            System.out.println("Navigating to registration page...");
        } catch (Exception e) {
            System.err.println("Error navigating to registration page: " + e.getMessage());
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load the registration page. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show alert dialog with specified type
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
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
                    }
                } catch (Exception e) {
                    // Continue without styling if no CSS is available
                    System.err.println("Warning: Could not apply CSS to alert dialog");
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
     * Sets the name of the currently logged-in user.
     * @param name The name of the user to set.
     */
    public static void setCurrentUserName(String name) {
        currentUserName = name;
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