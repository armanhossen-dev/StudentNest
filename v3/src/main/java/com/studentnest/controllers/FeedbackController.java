package com.studentnest.controllers;

import com.studentnest.database.DatabaseConnection;
import com.studentnest.utils.SceneManager;
import javafx.event.ActionEvent; // Added import for ActionEvent
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL;
import javafx.stage.Stage;
import javafx.scene.image.Image;

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
    public void handleSubmitFeedback(ActionEvent event) {
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
                handleBack(event); // Pass the ActionEvent to the handleBack method
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to submit feedback. Please try again later.");
        }
    }

    /**
     * Handles the 'back' button action, navigating the user to the previous page.
     * @param event The action event from the back button.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        // Navigate back to the stored previous page
        if (previousPageFxml != null) {
            // Correct the dimensions to match the dashboard's dimensions
            SceneManager.switchScene(event, previousPageFxml, previousPageCss, 1400, 800);
        } else {
            // Fallback in case previousPageFxml is not set
            SceneManager.switchScene(event, previousPageFxml, previousPageCss, 1400, 800);
            SceneManager.switchScene(event, "/fxml/feedback-page.fxml", "/css/styles1.css", 800, 600,
                    "StudentNest - House Owner Dashboard");
        }
    }

    /**
     * Shows a standard alert dialog with a title, message, and type.
     * @param type The type of alert to display.
     * @param title The title of the alert dialog.
     * @param message The content message of the alert dialog.
     */
    private void showAlert(AlertType type, String title, String message) {
        javafx.application.Platform.runLater(() -> {
            try {
                Alert alert = new Alert(type);
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

                try {
                    URL cssUrl = getClass().getResource("/css/login.css");
                    if (cssUrl != null) {
                        alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
                    } else {
                        System.err.println("Warning: Could not apply CSS to alert dialog, file not found: /css/login.css");
                    }
                } catch (Exception e) {
                    System.err.println("Error applying CSS to alert dialog: " + e.getMessage());
                }

                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing alert: " + e.getMessage());
            }
        });
    }
}