package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.User;
import com.studentnest.models.Room;
import com.studentnest.utils.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> userTypeColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private Button deleteUserButton;
    @FXML
    private TableView<Room> roomsTable;
    @FXML
    private TableColumn<Room, String> roomLocationColumn;
    @FXML
    private TableColumn<Room, Double> roomPriceColumn;
    @FXML
    private TableColumn<Room, String> roomOwnerColumn;
    @FXML
    private Button deleteRoomButton;
    @FXML
    private PieChart userStatsChart;
    @FXML
    private Button logoutButton;

    @FXML
    private Label userCountLabel;
    @FXML
    private Label roomCountLabel;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Room> rooms = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Admin Dashboard - Welcome, " + LoginController.getCurrentUserName() + "!");

        // Setup users table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        usersTable.setItems(users);

        // Setup rooms table
        roomLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        roomOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        roomsTable.setItems(rooms);

        loadUsers();
        loadRooms();
        loadUserStats();
    }

    private void loadUsers() {
        users.clear();
        String sql = "SELECT * FROM users WHERE user_type != 'Admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setUsername(rs.getString("username"));
                user.setUserType(rs.getString("user_type"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load users.", Alert.AlertType.ERROR);
        }
        updateCounts();
    }

    private void loadRooms() {
        rooms.clear();
        String sql = "SELECT r.*, u.name as owner_name FROM rooms r JOIN users u ON r.owner_id = u.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setPrice(rs.getDouble("price"));
                room.setDescription(rs.getString("description"));
                room.setOwnerName(rs.getString("owner_name"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load rooms.", Alert.AlertType.ERROR);
        }
        updateCounts();
    }

    private void loadUserStats() {
        String sql = "SELECT user_type, COUNT(*) as count FROM users WHERE user_type != 'Admin' GROUP BY user_type";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("user_type"), rs.getInt("count")));
            }
            userStatsChart.setData(pieChartData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user statistics.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete user: " + selectedUser.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // First, delete the user's rooms if they are a 'House Owner'
                if ("House Owner".equals(selectedUser.getUserType())) {
                    String deleteRoomsSql = "DELETE FROM rooms WHERE owner_id = ?";
                    try (PreparedStatement deleteRoomsStmt = conn.prepareStatement(deleteRoomsSql)) {
                        deleteRoomsStmt.setInt(1, selectedUser.getId());
                        deleteRoomsStmt.executeUpdate();
                    }
                }

                // Then, delete the user
                String deleteUserSql = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                    deleteUserStmt.setInt(1, selectedUser.getId());
                    if (deleteUserStmt.executeUpdate() > 0) {
                        showAlert("Success", "User deleted successfully!", Alert.AlertType.INFORMATION);
                        loadUsers();
                        loadRooms();
                        loadUserStats();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDeleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this room in " + selectedRoom.getLocation() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM rooms WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room deleted successfully!", Alert.AlertType.INFORMATION);
                    loadRooms();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete room", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleLogout() {
        try {
            SceneManager.switchScene("login.fxml", 1000, 620);
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
        }
    }

    private void updateCounts() {
        userCountLabel.setText(String.valueOf(users.size()));
        roomCountLabel.setText(String.valueOf(rooms.size()));
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
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
            }
        });
    }

    // Simplified showAlert for convenience
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
}