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
import com.studentnest.models.Feedback; // You'll need to create this model
import com.studentnest.utils.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> userTypeColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private Button deleteUserButton;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomLocationColumn;
    @FXML private TableColumn<Room, Double> roomPriceColumn;
    @FXML private TableColumn<Room, String> roomOwnerColumn;
    @FXML private Button deleteRoomButton;
    @FXML private PieChart userStatsChart;
    @FXML private Button logoutButton;

    @FXML private Label userCountLabel;
    @FXML private Label roomCountLabel;

    // FXML injections for the new navigation buttons and the TabPane
    @FXML private TabPane mainTabPane;
    @FXML private Button usersButton;
    @FXML private Button roomsButton;
    @FXML private Button statisticsButton;
    @FXML private Button feedbackButton;

    // NEW: FXML for the feedback table
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> feedbackUserColumn;
    @FXML private TableColumn<Feedback, String> feedbackTextColumn;
    @FXML private TableColumn<Feedback, Timestamp> feedbackDateColumn;
    @FXML private Button deleteFeedbackButton; // FXML for the new delete button


    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Room> rooms = FXCollections.observableArrayList();
    private ObservableList<Feedback> feedback = FXCollections.observableArrayList();

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

        // NEW: Setup feedback table
        feedbackUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        feedbackDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        feedbackTable.setItems(feedback);


        loadUsers();
        loadRooms();
        loadUserStats();
        loadFeedback(); // NEW: Load feedback on startup
    }

    // NEW: Method to load all feedback from the database
    private void loadFeedback() {
        feedback.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT f.*, u.username as user_name FROM feedback_and_issues f JOIN users u ON f.user_id = u.id ORDER BY f.created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Feedback fb = new Feedback();
                fb.setId(rs.getInt("id"));
                fb.setUserId(rs.getInt("user_id"));
                fb.setUserName(rs.getString("user_name"));
                fb.setFeedbackText(rs.getString("feedback_text"));
                fb.setCreatedAt(rs.getTimestamp("created_at"));
                feedback.add(fb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // New methods to handle button clicks and switch tabs
    @FXML
    private void handleShowUsers() {
        mainTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void handleShowRooms() {
        mainTabPane.getSelectionModel().select(1);
    }

    @FXML
    private void handleShowStats() {
        mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    private void handleShowFeedback() {
        mainTabPane.getSelectionModel().select(3);
    }

    // NEW: Method to handle deleting a selected feedback
    @FXML
    public void handleDeleteFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback == null) {
            showAlert("Error", "Please select a feedback to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this feedback?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM feedback_and_issues WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedFeedback.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Feedback deleted successfully!", Alert.AlertType.INFORMATION);
                    loadFeedback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete feedback", Alert.AlertType.ERROR);
            }
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
            // Fix: Use the new SceneManager.switchScene method with the CSS file name.
            SceneManager.switchScene("login.fxml", "login.css", 1000, 620);
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
