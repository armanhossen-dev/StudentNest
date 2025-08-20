package com.studentnest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import com.studentnest.database.DatabaseConnection;
import com.studentnest.models.User;
import com.studentnest.models.Room;
import com.studentnest.models.Feedback;
import com.studentnest.utils.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AdminDashboardController {

    @FXML private BorderPane rootContainer;
    @FXML private Label welcomeLabel;
    @FXML private Button themeToggleButton;
    @FXML private Button logoutButton;

    // Navigation buttons
    @FXML private Button usersButton;
    @FXML private Button roomsButton;
    @FXML private Button statisticsButton;
    @FXML private Button feedbackButton;

    // Tab pane
    @FXML private TabPane mainTabPane;

    // Users table and columns
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> userTypeColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private Button deleteUserButton;
    @FXML private Button refreshUsersButton;

    // Rooms table and columns
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomLocationColumn;
    @FXML private TableColumn<Room, Double> roomPriceColumn;
    @FXML private TableColumn<Room, String> roomOwnerColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, String> roomContactColumn;
    @FXML private Button deleteRoomButton;
    @FXML private Button refreshRoomsButton;

    // Statistics
    @FXML private PieChart userStatsChart;

    // Stats labels
    @FXML private Label userCountLabel;
    @FXML private Label roomCountLabel;
    @FXML private Label ownerCountLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label monthlyUsersLabel;
    @FXML private Label monthlyRoomsLabel;

    // Feedback table and columns
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> feedbackUserColumn;
    @FXML private TableColumn<Feedback, String> feedbackTextColumn;
    @FXML private TableColumn<Feedback, Timestamp> feedbackDateColumn;
    @FXML private TableColumn<Feedback, String> feedbackStatusColumn;
    @FXML private Button deleteFeedbackButton;
    @FXML private Button markResolvedButton;
    @FXML private Button refreshFeedbackButton;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Room> rooms = FXCollections.observableArrayList();
    private ObservableList<Feedback> feedback = FXCollections.observableArrayList();

    private boolean isDarkTheme = false;

    @FXML
    public void initialize() {
        // Set welcome message
        String currentUser = LoginController.getCurrentUserName();
        welcomeLabel.setText("Welcome, " + (currentUser != null ? currentUser : "Admin") + "!");

        // Initialize table columns
        setupTableColumns();

        // Load data
        loadAllData();

        // Setup animations for UI elements
        setupAnimations();

        // Initialize theme
        applyTheme();
    }

    private void setupTableColumns() {
        // Users table setup
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        usersTable.setItems(users);

        // Rooms table setup
        roomLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        roomOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        roomsTable.setItems(rooms);

        // Feedback table setup
        feedbackUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        feedbackDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        feedbackStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        feedbackTable.setItems(feedback);

        // Format price column to show currency
        roomPriceColumn.setCellFactory(column -> new TableCell<Room, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("৳" + String.format("%.0f", price));
                }
            }
        });

        // Format date column
        feedbackDateColumn.setCellFactory(column -> new TableCell<Feedback, Timestamp>() {
            @Override
            protected void updateItem(Timestamp timestamp, boolean empty) {
                super.updateItem(timestamp, empty);
                if (empty || timestamp == null) {
                    setText(null);
                } else {
                    setText(timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                }
            }
        });
    }

    private void setupAnimations() {
        // Add fade-in animation to main content
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainTabPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void loadAllData() {
        Platform.runLater(() -> {
            loadUsers();
            loadRooms();
            loadFeedback();
            loadUserStats();
            loadMonthlyStats();
        });
    }

    private void loadUsers() {
        users.clear();
        String sql = "SELECT * FROM users WHERE user_type != 'Admin' ORDER BY created_at DESC";
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
            showAlert("Database Error", "Failed to load users: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        updateCounts();
    }

    private void loadRooms() {
        rooms.clear();
        String sql = """
        SELECT r.*, u.name as owner_name, r.contact_number as contact_info
        FROM rooms r
        JOIN users u ON r.owner_id = u.id
        ORDER BY r.created_at DESC
        """;
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
                room.setRoomType(rs.getString("room_type"));
                room.setContactInfo(rs.getString("contact_info")); // This is now correct due to the alias
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load rooms: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        updateCounts();
    }

    private void loadFeedback() {
        feedback.clear();
        String sql = """
            SELECT f.*, u.username as user_name, 
            COALESCE(f.status, 'Pending') as status
            FROM feedback_and_issues f 
            JOIN users u ON f.user_id = u.id 
            ORDER BY f.created_at DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Feedback fb = new Feedback();
                fb.setId(rs.getInt("id"));
                fb.setUserId(rs.getInt("user_id"));
                fb.setUserName(rs.getString("user_name"));
                fb.setFeedbackText(rs.getString("feedback_text"));
                fb.setCreatedAt(rs.getTimestamp("created_at"));
                fb.setStatus(rs.getString("status"));
                feedback.add(fb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load feedback: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadUserStats() {
        String sql = "SELECT user_type, COUNT(*) as count FROM users WHERE user_type != 'Admin' GROUP BY user_type";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while (rs.next()) {
                String userType = rs.getString("user_type");
                int count = rs.getInt("count");
                pieChartData.add(new PieChart.Data(userType + " (" + count + ")", count));
            }
            userStatsChart.setData(pieChartData);
            userStatsChart.setLegendVisible(true);
            userStatsChart.setLabelsVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadMonthlyStats() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        // Monthly users
        String usersSql = "SELECT COUNT(*) FROM users WHERE MONTH(created_at) = ? AND YEAR(created_at) = ? AND user_type != 'Admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(usersSql)) {
            stmt.setInt(1, currentMonth);
            stmt.setInt(2, currentYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                monthlyUsersLabel.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Monthly rooms
        String roomsSql = "SELECT COUNT(*) FROM rooms WHERE MONTH(created_at) = ? AND YEAR(created_at) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(roomsSql)) {
            stmt.setInt(1, currentMonth);
            stmt.setInt(2, currentYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                monthlyRoomsLabel.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Navigation handlers
    @FXML
    private void handleShowUsers() {
        selectTabWithAnimation(0);
    }

    @FXML
    private void handleShowRooms() {
        selectTabWithAnimation(1);
    }

    @FXML
    private void handleShowStats() {
        selectTabWithAnimation(2);
    }

    @FXML
    private void handleShowFeedback() {
        selectTabWithAnimation(3);
    }

    private void selectTabWithAnimation(int tabIndex) {
        mainTabPane.getSelectionModel().select(tabIndex);

        // Add a subtle scale animation
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), mainTabPane);
        scale.setFromX(0.98);
        scale.setFromY(0.98);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    // Theme toggle handler
    @FXML
    private void handleThemeToggle() {
        isDarkTheme = !isDarkTheme;
        applyTheme();

        // Update button text
        themeToggleButton.setText(isDarkTheme ? "☀" : "🌙");

        // Add animation effect
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), themeToggleButton);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    private void applyTheme() {
        if (isDarkTheme) {
            rootContainer.getStyleClass().add("dark-theme");
        } else {
            rootContainer.getStyleClass().remove("dark-theme");
        }
    }

    // Delete handlers
    @FXML
    public void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Selection Required", "Please select a user to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete User: " + selectedUser.getName());
        confirmAlert.setContentText("Are you sure you want to delete this user?\n\nThis action cannot be undone and will also delete all rooms posted by this user if they are a House Owner.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    // First, delete the user's rooms if they are a 'House Owner'
                    if ("House Owner".equals(selectedUser.getUserType())) {
                        String deleteRoomsSql = "DELETE FROM rooms WHERE owner_id = ?";
                        try (PreparedStatement deleteRoomsStmt = conn.prepareStatement(deleteRoomsSql)) {
                            deleteRoomsStmt.setInt(1, selectedUser.getId());
                            int roomsDeleted = deleteRoomsStmt.executeUpdate();
                            System.out.println("Deleted " + roomsDeleted + " rooms for user: " + selectedUser.getName());
                        }
                    }

                    // Delete user's feedback
                    String deleteFeedbackSql = "DELETE FROM feedback_and_issues WHERE user_id = ?";
                    try (PreparedStatement deleteFeedbackStmt = conn.prepareStatement(deleteFeedbackSql)) {
                        deleteFeedbackStmt.setInt(1, selectedUser.getId());
                        deleteFeedbackStmt.executeUpdate();
                    }

                    // Then, delete the user
                    String deleteUserSql = "DELETE FROM users WHERE id = ?";
                    try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                        deleteUserStmt.setInt(1, selectedUser.getId());
                        if (deleteUserStmt.executeUpdate() > 0) {
                            conn.commit();
                            showAlert("Success", "User '" + selectedUser.getName() + "' has been successfully deleted!", Alert.AlertType.INFORMATION);
                            loadAllData(); // Refresh all data
                        } else {
                            conn.rollback();
                            showAlert("Error", "Failed to delete user. Please try again.", Alert.AlertType.ERROR);
                        }
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDeleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Selection Required", "Please select a room to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Room");
        confirmAlert.setContentText("Are you sure you want to delete the room in " + selectedRoom.getLocation() + "?\n\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String sql = "DELETE FROM rooms WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room has been successfully deleted!", Alert.AlertType.INFORMATION);
                    loadRooms();
                    loadMonthlyStats();
                } else {
                    showAlert("Error", "Failed to delete room. Please try again.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete room: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDeleteFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback == null) {
            showAlert("Selection Required", "Please select feedback to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Feedback");
        confirmAlert.setContentText("Are you sure you want to delete this feedback?\n\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String sql = "DELETE FROM feedback_and_issues WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedFeedback.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Feedback has been successfully deleted!", Alert.AlertType.INFORMATION);
                    loadFeedback();
                } else {
                    showAlert("Error", "Failed to delete feedback. Please try again.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete feedback: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleMarkResolved() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback == null) {
            showAlert("Selection Required", "Please select feedback to mark as resolved.", Alert.AlertType.WARNING);
            return;
        }

        if ("Resolved".equals(selectedFeedback.getStatus())) {
            showAlert("Already Resolved", "This feedback is already marked as resolved.", Alert.AlertType.INFORMATION);
            return;
        }

        String sql = "UPDATE feedback_and_issues SET status = 'Resolved' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedFeedback.getId());

            if (stmt.executeUpdate() > 0) {
                showAlert("Success", "Feedback has been marked as resolved!", Alert.AlertType.INFORMATION);
                loadFeedback();
            } else {
                showAlert("Error", "Failed to update feedback status. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update feedback: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Refresh handlers
    @FXML
    public void handleRefreshUsers() {
        loadUsers();
        showAlert("Refreshed", "Users data has been refreshed!", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleRefreshRooms() {
        loadRooms();
        showAlert("Refreshed", "Rooms data has been refreshed!", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleRefreshFeedback() {
        loadFeedback();
        showAlert("Refreshed", "Feedback data has been refreshed!", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Logout");
        confirmAlert.setHeaderText("Logout Confirmation");
        confirmAlert.setContentText("Are you sure you want to logout?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Clear current user session
                LoginController.setCurrentUserName(null);

                // Navigate back to login
                SceneManager.switchScene("login.fxml", "login.css", 1000, 620);
                System.out.println("Successfully logged out and navigated to login page.");
            } catch (Exception e) {
                System.err.println("Error navigating to login page: " + e.getMessage());
                e.printStackTrace();
                showAlert("Navigation Error", "Failed to logout properly. Please restart the application.", Alert.AlertType.ERROR);
            }
        }
    }

    private void updateCounts() {
        Platform.runLater(() -> {
            userCountLabel.setText(String.valueOf(users.size()));
            roomCountLabel.setText(String.valueOf(rooms.size()));

            // Count owners and students
            long ownerCount = users.stream().filter(u -> "House Owner".equals(u.getUserType())).count();
            long studentCount = users.stream().filter(u -> "Student".equals(u.getUserType())).count();

            ownerCountLabel.setText(String.valueOf(ownerCount));
            studentCountLabel.setText(String.valueOf(studentCount));
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                // Set application icon for alert
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                try {
                    URL imageUrl = getClass().getResource("/images/img.png");
                    if (imageUrl != null) {
                        Image icon = new Image(imageUrl.toExternalForm());
                        alertStage.getIcons().add(icon);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Could not load application icon for alert.");
                }

                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Convenience method for info alerts
    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
}