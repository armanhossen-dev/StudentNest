package com.studentnest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.studentnest.utils.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setTitle("StudentNest - Student Home Finder");
        primaryStage.setResizable(false);

        // Load login scene
        SceneManager.switchScene("login.fxml", 800, 600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}