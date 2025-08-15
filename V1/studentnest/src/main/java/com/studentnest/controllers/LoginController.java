package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import java.sql.*;

public class LoginController {
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;

    private static int currentUserId;
    private static String currentUserName;

    @FXML
    public void initialize() {
        userTypeComboBox.getItems().addAll("Student", "House Owner", "Admin");
        userTypeComboBox.setValue("Student");
    }

    @FXML
    public void handleLogin() {
        String userType = userTypeComboBox.getValue();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        if (authenticateUser(username, password, userType)) {
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
            }
        } else {
            showAlert("Login Failed", "Invalid username, password, or user type");
        }
    }

    private boolean authenticateUser(String username, String password, String userType) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, name FROM users WHERE username = ? AND password = ? AND user_type = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, userType);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUserName = rs.getString("name");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void handleCreateAccount() {
        SceneManager.switchScene("registration.fxml", 600, 700);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static int getCurrentUserId() { return currentUserId; }
    public static String getCurrentUserName() { return currentUserName; }
}