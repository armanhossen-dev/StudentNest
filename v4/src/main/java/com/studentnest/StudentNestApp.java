package com.studentnest;

import com.studentnest.config.AppConfig;
import com.studentnest.config.ThemeManager;
import com.studentnest.controller.MainController;
import com.studentnest.db.DataSeeder;
import com.studentnest.db.DatabaseConnection;
import com.studentnest.db.SchemaInitializer;
import com.studentnest.util.ImageStorage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class StudentNestApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ImageStorage.ensureDirectory();
        SchemaInitializer.initialize();
        DataSeeder.seed();

        FXMLLoader loader = new FXMLLoader(StudentNestApp.class.getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1360, 860);
        applyTheme(scene);
        ThemeManager.getInstance().darkModeProperty().addListener((obs, oldVal, newVal) -> applyTheme(scene));

        MainController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle("StudentNest " + AppConfig.getInstance().getDisplayVersion());
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        InputStream icon = StudentNestApp.class.getResourceAsStream("/images/app-icon.png");
        if (icon != null) {
            stage.getIcons().add(new Image(icon));
        }
        stage.setScene(scene);
        stage.show();
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        var app = StudentNestApp.class.getResource("/css/app.css");
        var theme = StudentNestApp.class.getResource(ThemeManager.getInstance().stylesheet());
        if (app != null) {
            scene.getStylesheets().add(app.toExternalForm());
        }
        if (theme != null) {
            scene.getStylesheets().add(theme.toExternalForm());
        }
    }

    public static void main(String[] args) {
        DatabaseConnection.getInstance().initialize();
        launch(args);
    }
}
