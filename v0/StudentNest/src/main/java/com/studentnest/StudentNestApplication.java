package com.studentnest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Objects;

public class StudentNestApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StudentNestApplication.class.getResource("student-home-finder.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 990, 700);

        Image icon = new Image(Objects.requireNonNull(StudentNestApplication.class.getResourceAsStream("logo.png")));
        stage.getIcons().add(icon);

        stage.setTitle("StudentNest - Student Home Finder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}