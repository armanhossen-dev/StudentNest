package com.studentnest;

import javafx.application.Application;
import javafx.stage.Stage;
import com.studentnest.utils.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setTitle("StudentNest - Student Home Finder");
        primaryStage.setResizable(false);

        // Set application icon from resources or Photo folder
        try {
            java.net.URL logoUrl = getClass().getResource("/images/logo.png");
            if (logoUrl != null) {
                primaryStage.getIcons().add(new javafx.scene.image.Image(logoUrl.toExternalForm()));
            } else {
                java.io.File logoFile = new java.io.File("Photo/logo.png");
                if (logoFile.exists()) {
                    primaryStage.getIcons().add(new javafx.scene.image.Image(logoFile.toURI().toString()));
                }
            }
        } catch (Exception ignored) {}

        // Load login scene
        SceneManager.switchScene("login.fxml", 800, 600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}