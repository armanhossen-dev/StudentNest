package com.studentnest.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static Stage primaryStage;
    private static FXMLLoader lastLoader;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switches the primary stage's scene to a new FXML file with a specific CSS stylesheet.
     * @param fxmlFile The FXML file to load (e.g., "login.fxml").
     * @param cssFile The CSS file to apply (e.g., "login.css"). Can be null to apply no CSS.
     * @param width The desired scene width.
     * @param height The desired scene height.
     */
    public static void switchScene(String fxmlFile, String cssFile, int width, int height) {
        try {
            URL fxmlUrl = SceneManager.class.getResource("/fxml/" + fxmlFile);

            if (fxmlUrl == null) {
                System.err.println("FXML file not found: " + fxmlFile);
                return;
            }

            // Store the loader instance for getController()
            lastLoader = new FXMLLoader(fxmlUrl);
            Parent root = lastLoader.load();
            Scene scene = new Scene(root, width, height);

            // Apply CSS if a file is provided
            if (cssFile != null && !cssFile.isEmpty()) {
                URL cssUrl = SceneManager.class.getResource("/css/" + cssFile);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println(cssFile + " loaded successfully.");
                } else {
                    System.err.println("Warning: CSS file not found: " + cssFile);
                }
            }

            if (primaryStage != null) {
                primaryStage.setScene(scene);
                primaryStage.show();
            } else {
                System.err.println("Primary Stage is not set. Cannot switch scene.");
            }

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Object getController() {
        if (lastLoader != null) {
            return lastLoader.getController();
        }
        return null;
    }


}
