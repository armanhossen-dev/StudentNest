package com.studentnest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.studentnest.utils.SceneManager;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Set the primary stage using your SceneManager
            SceneManager.setPrimaryStage(primaryStage);
            primaryStage.setTitle("StudentNest - Student Home Finder");

            // --- Code for setting the logo ---
            // Get the URL for the image from the resources folder
            URL imageUrl = getClass().getResource("/images/img.png");

            // Check if the URL is null, which means the image file was not found
            if (imageUrl == null) {
                System.err.println("Warning: The image file was not found. Please check the path.");
                System.err.println("Expected resource path: /images/img.png (which corresponds to src/main/resources/images/img.png)");
                // Continue without the icon - this is not a critical error
            } else {
                // If the URL is found, create the Image and set the icon
                Image icon = new Image(imageUrl.toExternalForm());
                primaryStage.getIcons().add(icon);
            }
            // --- End of logo code ---

            primaryStage.setResizable(false);

            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/login.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("FXML file not found: /fxml/login.fxml");
                System.err.println("Please ensure the file exists at src/main/resources/fxml/login.fxml");
                return;
            }

            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 620);

            // ADD CSS TO SCENE
            try {
                URL cssUrl = this.getClass().getResource("/css/login.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("login.css loaded successfully.");
                } else {
                    System.err.println("Warning: CSS file not found: /css/login.css. Please check the path.");
                }
            } catch (Exception cssException) {
                System.err.println("Warning: Could not load CSS: " + cssException.getMessage());
            }

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
