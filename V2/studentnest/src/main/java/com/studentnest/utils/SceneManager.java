package com.studentnest.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile, int width, int height) {
        try {
            URL fxmlUrl = SceneManager.class.getResource("/fxml/" + fxmlFile);

            if (fxmlUrl == null) {
                System.err.println("FXML file not found: " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);

            // ADD CSS TO SCENE
            String css = SceneManager.class.getResource("/css/styles1.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
