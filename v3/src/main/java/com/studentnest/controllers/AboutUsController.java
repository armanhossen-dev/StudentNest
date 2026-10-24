package com.studentnest.controllers;

import com.studentnest.utils.SceneManager;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Enhanced Controller class for the About Us page of StudentNest application.
 * Uses SceneManager for smooth transitions and handles contributor information display.
 */
public class AboutUsController implements Initializable {

    // FXML injected components
    @FXML
    private TableView<Contributor> contributorsTable;

    @FXML
    private TableColumn<Contributor, String> nameColumn;

    @FXML
    private TableColumn<Contributor, String> idColumn;

    @FXML
    private TableColumn<Contributor, String> githubColumn;

    /**
     * Data model for contributors
     */
    public static class Contributor {
        private final SimpleStringProperty name;
        private final SimpleStringProperty studentId;
        private final SimpleStringProperty githubProfile;

        public Contributor(String name, String studentId, String githubProfile) {
            this.name = new SimpleStringProperty(name);
            this.studentId = new SimpleStringProperty(studentId);
            this.githubProfile = new SimpleStringProperty(githubProfile);
        }

        // Property getters for TableView binding
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty studentIdProperty() { return studentId; }
        public SimpleStringProperty githubProfileProperty() { return githubProfile; }

        // Standard getters
        public String getName() { return name.get(); }
        public String getStudentId() { return studentId.get(); }
        public String getGithubProfile() { return githubProfile.get(); }

        // Setters
        public void setName(String name) { this.name.set(name); }
        public void setStudentId(String studentId) { this.studentId.set(studentId); }
        public void setGithubProfile(String githubProfile) { this.githubProfile.set(githubProfile); }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadContributorsData();
        addEntranceAnimations();
    }

    /**
     * Sets up the table columns with proper cell value factories
     */
    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        githubColumn.setCellValueFactory(cellData -> cellData.getValue().githubProfileProperty());

        // Make table responsive
        contributorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add selection listener
        contributorsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> handleContributorSelection(newSelection)
        );
    }

    /**
     * Loads the contributors data into the table
     * Fixed contributor information with corrected GitHub usernames and IDs
     */
    private void loadContributorsData() {
        ObservableList<Contributor> contributors = FXCollections.observableArrayList();

        // Corrected team member information
        contributors.add(new Contributor("Md. Hasibur Rahman", "DIU-806", "@Hasib55-bit"));
        contributors.add(new Contributor("Md. Arman Hossen Ripon", "DIU-883", "@armanhossen-dev"));
        contributors.add(new Contributor("Md. Wahidur Rahman", "DIU-865", "@wahidur-rahman"));
        contributors.add(new Contributor("Md Sabbir Hossine", "DIU-673", "@sabbir-hossine"));
        contributors.add(new Contributor("Md. Rahul Hossain", "DIU-766", "@Rahul003-phy"));

        contributorsTable.setItems(contributors);
    }

    /**
     * Adds entrance animations to components
     */
    private void addEntranceAnimations() {
        // Fade in the contributors table
        if (contributorsTable != null) {
            contributorsTable.setOpacity(0.0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), contributorsTable);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setDelay(Duration.millis(500)); // Small delay for better effect
            fadeIn.play();
        }
    }

    /**
     * Handles the back button action - returns to dashboard with smooth transition
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Use SceneManager for smooth transition back to dashboard
            SceneManager.goBackToDashboard(event, "/fxml/dashboard.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating back to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Get Started button action - navigates to home search
     */
    @FXML
    private void handleGetStarted(ActionEvent event) {
        try {
            // Use SceneManager for smooth transition to home search
            SceneManager.switchScene(event, "/fxml/home-search.fxml",
                    "StudentNest - Find Your Home",
                    SceneManager.TransitionType.SLIDE_LEFT);
        } catch (Exception e) {
            System.err.println("Error navigating to home search: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles contributor selection in the table
     */
    private void handleContributorSelection(Contributor contributor) {
        if (contributor != null) {
            System.out.println("Selected contributor: " + contributor.getName());
            showContributorInfo(contributor);
        }
    }

    /**
     * Shows information about the selected contributor
     */
    private void showContributorInfo(Contributor contributor) {
        try {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Contributor Information");
            info.setHeaderText("About " + contributor.getName());

            String content = String.format(
                    "Student ID: %s%nGitHub: %s%n%nClick OK to close this dialog.",
                    contributor.getStudentId(),
                    contributor.getGithubProfile()
            );

            info.setContentText(content);

            // Apply custom styling if CSS file exists
            try {
                String cssPath = "/css/about-us.css";
                if (getClass().getResource(cssPath) != null) {
                    info.getDialogPane().getStylesheets().add(
                            getClass().getResource(cssPath).toExternalForm()
                    );
                }
            } catch (Exception cssException) {
                System.err.println("Could not load CSS for dialog: " + cssException.getMessage());
                // Continue without styling
            }

            info.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing contributor info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to add a new contributor (useful for dynamic data loading)
     */
    public void addContributor(String name, String studentId, String githubProfile) {
        if (name != null && studentId != null && githubProfile != null) {
            Contributor newContributor = new Contributor(name, studentId, githubProfile);
            contributorsTable.getItems().add(newContributor);
        }
    }

    /**
     * Refreshes the contributors data (useful for database integration)
     */
    public void refreshContributorsData() {
        try {
            loadContributorsData();
        } catch (Exception e) {
            System.err.println("Error refreshing contributors data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets all contributors (useful for external access)
     */
    public ObservableList<Contributor> getAllContributors() {
        return contributorsTable.getItems();
    }

    /**
     * Sets contributors data from external source
     */
    public void setContributorsData(ObservableList<Contributor> contributors) {
        if (contributors != null) {
            contributorsTable.setItems(contributors);
        }
    }

    /**
     * Removes a contributor by student ID
     */
    public boolean removeContributor(String studentId) {
        if (studentId != null) {
            return contributorsTable.getItems().removeIf(
                    contributor -> contributor.getStudentId().equals(studentId)
            );
        }
        return false;
    }

    /**
     * Updates contributor information
     */
    public boolean updateContributor(String studentId, String newName, String newGithubProfile) {
        if (studentId != null) {
            for (Contributor contributor : contributorsTable.getItems()) {
                if (contributor.getStudentId().equals(studentId)) {
                    if (newName != null) {
                        contributor.setName(newName);
                    }
                    if (newGithubProfile != null) {
                        contributor.setGithubProfile(newGithubProfile);
                    }
                    contributorsTable.refresh(); // Refresh table display
                    return true;
                }
            }
        }
        return false;
    }
}