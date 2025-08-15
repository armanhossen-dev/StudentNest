package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import java.sql.*;

public class RegistrationController {
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    @FXML
    public void initialize() {
        userTypeComboBox.getItems().addAll("Student", "House Owner");
        userTypeComboBox.setValue("Student");
    }

    @FXML
    public void handleRegister() {
        String userType = userTypeComboBox.getValue();
        String name = nameField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        if (registerUser(name, phone, username, password, userType)) {
            showAlert("Success", "Registration successful! Please login.");
            SceneManager.switchScene("login.fxml", 800, 600);
        } else {
            showAlert("Error", "Registration failed. Username might already exist.");
        }
    }

    private boolean registerUser(String name, String phone, String username, String password, String userType) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO users (name, phone, username, password, user_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.setString(5, userType);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void handleBackToLogin() {
        SceneManager.switchScene("login.fxml", 800, 600);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}