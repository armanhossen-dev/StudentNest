package com.studentnest.utils;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Utility class for managing scene transitions in the StudentNest application.
 * Provides smooth animations and consistent navigation experience.
 */
public class SceneManager {


    // Inside public class SceneManager { ...

    private static Stage primaryStage;

    /**
     * Sets the primary stage for the application.
     * This should be called once at application startup.
     * @param stage The primary stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Gets the primary stage.
     * @return The primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    /**
     * Navigation animation types
     */
    public enum TransitionType {
        FADE,
        SLIDE_LEFT,
        SLIDE_RIGHT,
        SLIDE_UP,
        SLIDE_DOWN,
        NONE
    }

    /**
     * Switches to a new scene with fade transition
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param title New window title
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String title) {
        switchScene(event, fxmlPath, title, TransitionType.FADE);
    }

    /**
     * Switches to a new scene with specified transition type
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param title New window title
     * @param transitionType Type of transition animation
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String title, TransitionType transitionType) {
        try {
            // Get current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load new FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            // Apply transition based on type
            switch (transitionType) {
                case FADE:
                    applyFadeTransition(currentStage, newRoot, title);
                    break;
                case SLIDE_LEFT:
                    applySlideTransition(currentStage, newRoot, title, -currentStage.getWidth(), 0);
                    break;
                case SLIDE_RIGHT:
                    applySlideTransition(currentStage, newRoot, title, currentStage.getWidth(), 0);
                    break;
                case SLIDE_UP:
                    applySlideTransition(currentStage, newRoot, title, 0, -currentStage.getHeight());
                    break;
                case SLIDE_DOWN:
                    applySlideTransition(currentStage, newRoot, title, 0, currentStage.getHeight());
                    break;
                case NONE:
                default:
                    applyDirectTransition(currentStage, newRoot, title);
                    break;
            }

        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
            showNavigationError("Could not load the requested page. Please try again.");
        }
    }

    /**
     * Switches to a new scene with CSS styling and custom window dimensions
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param cssPath Path to the CSS file
     * @param width New window width
     * @param height New window height
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String cssPath, double width, double height) {
        switchScene(event, fxmlPath, cssPath, width, height, TransitionType.FADE);
    }

    /**
     * Switches to a new scene with CSS styling, custom dimensions and transition type
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param cssPath Path to the CSS file
     * @param width New window width
     * @param height New window height
     * @param transitionType Type of transition animation
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String cssPath, double width, double height, TransitionType transitionType) {
        try {
            // Get current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load new FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            // Create new scene with specified dimensions
            Scene newScene = new Scene(newRoot, width, height);

            // Apply CSS if provided
            if (cssPath != null && !cssPath.trim().isEmpty()) {
                try {
                    String cssResource = SceneManager.class.getResource(cssPath).toExternalForm();
                    newScene.getStylesheets().add(cssResource);
                } catch (Exception cssException) {
                    System.err.println("Warning: Could not load CSS file: " + cssPath);
                    // Continue without CSS styling
                }
            }

            // Store the new scene for transition
            final Scene sceneToApply = newScene;
            final String title = currentStage.getTitle(); // Keep current title

            // Apply transition based on type
            switch (transitionType) {
                case FADE:
                    applyFadeTransitionWithScene(currentStage, sceneToApply, title);
                    break;
                case SLIDE_LEFT:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, -currentStage.getWidth(), 0);
                    break;
                case SLIDE_RIGHT:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, currentStage.getWidth(), 0);
                    break;
                case SLIDE_UP:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, 0, -currentStage.getHeight());
                    break;
                case SLIDE_DOWN:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, 0, currentStage.getHeight());
                    break;
                case NONE:
                default:
                    currentStage.setScene(sceneToApply);
                    currentStage.setTitle(title);
                    break;
            }

            // Set window dimensions
            currentStage.setWidth(width);
            currentStage.setHeight(height);

        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
            showNavigationError("Could not load the requested page. Please try again.");
        } catch (Exception e) {
            System.err.println("Error loading CSS or setting scene dimensions: " + cssPath);
            e.printStackTrace();
            showNavigationError("Could not apply styling or set window dimensions.");
        }
    }

    /**
     * Overloaded version with title parameter
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param cssPath Path to the CSS file
     * @param width New window width
     * @param height New window height
     * @param title New window title
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String cssPath, double width, double height, String title) {
        switchScene(event, fxmlPath, cssPath, width, height, title, TransitionType.FADE);
    }

    /**
     * Full-featured scene switching method with all parameters
     * @param event The action event from the button click
     * @param fxmlPath Path to the FXML file
     * @param cssPath Path to the CSS file
     * @param width New window width
     * @param height New window height
     * @param title New window title
     * @param transitionType Type of transition animation
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String cssPath, double width, double height, String title, TransitionType transitionType) {
        try {
            // Get current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load new FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            // Create new scene with specified dimensions
            Scene newScene = new Scene(newRoot, width, height);

            // Apply CSS if provided
            if (cssPath != null && !cssPath.trim().isEmpty()) {
                try {
                    String cssResource = SceneManager.class.getResource(cssPath).toExternalForm();
                    newScene.getStylesheets().add(cssResource);
                } catch (Exception cssException) {
                    System.err.println("Warning: Could not load CSS file: " + cssPath);
                    // Continue without CSS styling
                }
            }

            // Store the new scene for transition
            final Scene sceneToApply = newScene;

            // Apply transition based on type
            switch (transitionType) {
                case FADE:
                    applyFadeTransitionWithScene(currentStage, sceneToApply, title);
                    break;
                case SLIDE_LEFT:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, -currentStage.getWidth(), 0);
                    break;
                case SLIDE_RIGHT:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, currentStage.getWidth(), 0);
                    break;
                case SLIDE_UP:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, 0, -currentStage.getHeight());
                    break;
                case SLIDE_DOWN:
                    applySlideTransitionWithScene(currentStage, sceneToApply, title, 0, currentStage.getHeight());
                    break;
                case NONE:
                default:
                    currentStage.setScene(sceneToApply);
                    currentStage.setTitle(title);
                    break;
            }

            // Set window dimensions
            currentStage.setWidth(width);
            currentStage.setHeight(height);

        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
            showNavigationError("Could not load the requested page. Please try again.");
        } catch (Exception e) {
            System.err.println("Error loading CSS or setting scene dimensions: " + cssPath);
            e.printStackTrace();
            showNavigationError("Could not apply styling or set window dimensions.");
        }
    }

    /**
     * Applies fade transition between scenes
     */
    private static void applyFadeTransition(Stage stage, Parent newRoot, String title) {
        Node currentRoot = stage.getScene().getRoot();

        // Fade out current scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            // Switch scene
            Scene newScene = new Scene(newRoot);
            stage.setScene(newScene);
            stage.setTitle(title);

            // Fade in new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    /**
     * Applies slide transition between scenes
     */
    private static void applySlideTransition(Stage stage, Parent newRoot, String title, double deltaX, double deltaY) {
        Node currentRoot = stage.getScene().getRoot();

        // Slide out current scene
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), currentRoot);
        slideOut.setFromX(0);
        slideOut.setFromY(0);
        slideOut.setToX(deltaX);
        slideOut.setToY(deltaY);

        slideOut.setOnFinished(e -> {
            // Switch scene
            Scene newScene = new Scene(newRoot);
            stage.setScene(newScene);
            stage.setTitle(title);

            // Slide in new scene from opposite direction
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newRoot);
            slideIn.setFromX(-deltaX);
            slideIn.setFromY(-deltaY);
            slideIn.setToX(0);
            slideIn.setToY(0);
            slideIn.play();
        });

        slideOut.play();
    }

    /**
     * Applies fade transition with a pre-built scene
     */
    private static void applyFadeTransitionWithScene(Stage stage, Scene newScene, String title) {
        Node currentRoot = stage.getScene().getRoot();

        // Fade out current scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            // Switch to the new scene
            stage.setScene(newScene);
            stage.setTitle(title);

            // Fade in new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    /**
     * Applies slide transition with a pre-built scene
     */
    private static void applySlideTransitionWithScene(Stage stage, Scene newScene, String title, double deltaX, double deltaY) {
        Node currentRoot = stage.getScene().getRoot();

        // Slide out current scene
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), currentRoot);
        slideOut.setFromX(0);
        slideOut.setFromY(0);
        slideOut.setToX(deltaX);
        slideOut.setToY(deltaY);

        slideOut.setOnFinished(e -> {
            // Switch to the new scene
            stage.setScene(newScene);
            stage.setTitle(title);

            // Slide in new scene from opposite direction
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newScene.getRoot());
            slideIn.setFromX(-deltaX);
            slideIn.setFromY(-deltaY);
            slideIn.setToX(0);
            slideIn.setToY(0);
            slideIn.play();
        });

        slideOut.play();
    }

    /**
     * Applies direct transition (no animation)
     */
    private static void applyDirectTransition(Stage stage, Parent newRoot, String title) {
        Scene newScene = new Scene(newRoot);
        stage.setScene(newScene);
        stage.setTitle(title);
    }

    /**
     * Goes back to the previous scene (typically dashboard)
     * @param event The action event from the back button
     * @param dashboardPath Path to the dashboard FXML
     */
    public static void goBackToDashboard(ActionEvent event, String dashboardPath) {
        switchScene(event, dashboardPath, "StudentNest - Dashboard", TransitionType.SLIDE_RIGHT);
    }

    /**
     * Goes back to the previous scene with fade transition
     * @param event The action event from the back button
     * @param previousScenePath Path to the previous scene FXML
     * @param previousSceneTitle Title for the previous scene
     */
    public static void goBack(ActionEvent event, String previousScenePath, String previousSceneTitle) {
        switchScene(event, previousScenePath, previousSceneTitle, TransitionType.FADE);
    }

    /**
     * Shows a navigation error message
     * @param message Error message to display
     */
    private static void showNavigationError(String message) {
        // You can implement this to show a toast notification or alert
        System.err.println("Navigation Error: " + message);
        // TODO: Implement proper error notification UI
    }

    /**
     * Gets the controller from a loaded FXML file
     * @param event The action event
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @return The controller instance
     */
    public static <T> T switchSceneAndGetController(ActionEvent event, String fxmlPath, String title) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
            currentStage.setTitle(title);

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error loading scene with controller: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the current window
     * @param event The action event
     */
    public static void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Minimizes the current window
     * @param event The action event
     */
    public static void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}