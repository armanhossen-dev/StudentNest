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
import java.sql.*;

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
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE user_type != 'Admin'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

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
        }
        updateCounts();
    }

    private void loadRooms() {
        rooms.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.*, u.name as owner_name FROM rooms r JOIN users u ON r.owner_id = u.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

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
        }
        updateCounts();
    }

    private void loadUserStats() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT user_type, COUNT(*) as count FROM users WHERE user_type != 'Admin' GROUP BY user_type";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("user_type"), rs.getInt("count")));
            }

            userStatsChart.setData(pieChartData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete user: " + selectedUser.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getConnection();

                // First delete user's rooms if house owner
                if ("House Owner".equals(selectedUser.getUserType())) {
                    String deleteRoomsSql = "DELETE FROM rooms WHERE owner_id = ?";
                    PreparedStatement deleteRoomsStmt = conn.prepareStatement(deleteRoomsSql);
                    deleteRoomsStmt.setInt(1, selectedUser.getId());
                    deleteRoomsStmt.executeUpdate();
                }

                // Delete user
                String deleteUserSql = "DELETE FROM users WHERE id = ?";
                PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql);
                deleteUserStmt.setInt(1, selectedUser.getId());

                if (deleteUserStmt.executeUpdate() > 0) {
                    showAlert("Success", "User deleted successfully!");
                    loadUsers();
                    loadRooms();
                    loadUserStats();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user");
            }
        }
    }

    @FXML
    public void handleDeleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this room in " + selectedRoom.getLocation() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "DELETE FROM rooms WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedRoom.getId());

                if (stmt.executeUpdate() > 0) {
                    showAlert("Success", "Room deleted successfully!");
                    loadRooms();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete room");
            }
        }
    }

    @FXML
    public void handleLogout() {
        SceneManager.switchScene("login.fxml", 800, 600);
    }

    private void updateCounts() {
        userCountLabel.setText(String.valueOf(users.size()));
        roomCountLabel.setText(String.valueOf(rooms.size()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
