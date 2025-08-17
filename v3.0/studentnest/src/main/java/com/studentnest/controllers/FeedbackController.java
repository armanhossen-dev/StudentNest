package com.studentnest.controllers;

import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FeedbackController {

    @FXML
    private TextArea feedbackTextArea;

    private String previousPageFxml;
    private String previousPageCss;

    /**
     * This method is called by the dashboard controllers to set the previous page
     * and its associated stylesheet for correct navigation.
     * @param fxml The FXML file name of the previous scene.
     * @param css The CSS file name of the previous scene.
     */
    public void setPreviousPage(String fxml, String css) {
        this.previousPageFxml = fxml;
        this.previousPageCss = css;
    }

    /**
     * Handles the submission of user feedback to the database.
     */
    @FXML
    public void handleSubmitFeedback() {
        String feedbackText = feedbackTextArea.getText().trim();
        if (feedbackText.isEmpty()) {
            showAlert(AlertType.ERROR, "Submission Failed", "Please write something before submitting.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO feedback_and_issues (user_id, feedback_text) VALUES (?, ?)")) {

            stmt.setInt(1, LoginController.getCurrentUserId());
            stmt.setString(2, feedbackText);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Your feedback has been submitted successfully!");
                handleBack(); // Go back to the correct dashboard
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to submit feedback. Please try again later.");
        }
    }

    /**
     * Handles the 'back' button action, navigating the user to the previous page.
     */
    @FXML
    public void handleBack() {
        // Navigate back to the stored previous page
        if (previousPageFxml != null) {
            // Use the new SceneManager method with FXML, CSS, and dimensions
            SceneManager.switchScene(previousPageFxml, previousPageCss, 1200, 700);
        } else {
            // Fallback in case previousPageFxml is not set
            SceneManager.switchScene("login.fxml", "login.css", 1000, 620);
        }
    }

    /**
     * Shows a standard alert dialog with a title, message, and type.
     * @param type The type of alert to display.
     * @param title The title of the alert dialog.
     * @param message The content message of the alert dialog.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
