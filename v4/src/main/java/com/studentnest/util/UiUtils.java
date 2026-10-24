package com.studentnest.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class UiUtils {
    private UiUtils() {
    }

    public static boolean confirm(Stage owner, String title, String message) {
        Stage dialog = new Stage();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(title);
        dialog.setResizable(false);

        boolean isDestructive = title != null && (
                title.toLowerCase().contains("delete") ||
                title.toLowerCase().contains("sign out") ||
                title.toLowerCase().contains("logout") ||
                title.toLowerCase().contains("remove")
        );

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(42, 42);
        iconCircle.setMinSize(42, 42);
        iconCircle.setMaxSize(42, 42);
        iconCircle.setStyle(isDestructive
                ? "-fx-background-color: rgba(239, 68, 68, 0.16); -fx-background-radius: 99;"
                : "-fx-background-color: rgba(37, 99, 235, 0.16); -fx-background-radius: 99;");
        Label iconLabel = new Label(isDestructive ? "🚪" : "ℹ");
        iconLabel.setStyle("-fx-font-size: 18px;");
        iconCircle.getChildren().add(iconLabel);

        Label heading = new Label(title);
        heading.getStyleClass().add("confirm-dialog-title");

        Label body = new Label(message);
        body.setWrapText(true);
        body.getStyleClass().add("confirm-dialog-body");

        VBox textCol = new VBox(4, heading, body);
        textCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        HBox header = new HBox(14, iconCircle, textCol);
        header.setAlignment(Pos.CENTER_LEFT);

        AtomicBoolean confirmed = new AtomicBoolean(false);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().addAll("btn", "btn-ghost");
        cancelBtn.setPrefWidth(96);
        cancelBtn.setCancelButton(true);
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = new Button("Confirm");
        confirmBtn.getStyleClass().addAll("btn", isDestructive ? "btn-danger" : "btn-primary");
        confirmBtn.setPrefWidth(96);
        confirmBtn.setDefaultButton(true);
        confirmBtn.setOnAction(e -> {
            confirmed.set(true);
            dialog.close();
        });

        HBox actions = new HBox(10, cancelBtn, confirmBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(18, header, actions);
        root.getStyleClass().add("modal-card");
        root.setPadding(new Insets(20, 22, 20, 22));
        root.setPrefWidth(430);

        Scene scene = new Scene(root);
        if (owner != null && owner.getScene() != null) {
            scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        } else {
            var app = UiUtils.class.getResource("/css/app.css");
            var theme = UiUtils.class.getResource(com.studentnest.config.ThemeManager.getInstance().stylesheet());
            if (app != null) scene.getStylesheets().add(app.toExternalForm());
            if (theme != null) scene.getStylesheets().add(theme.toExternalForm());
        }

        dialog.setScene(scene);
        dialog.showAndWait();
        return confirmed.get();
    }

    public static void alert(Stage owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        style(alert);
        alert.showAndWait();
    }

    public static ImageView roomImageView(String path, double width, double height) {
        ImageView view = new ImageView(loadRoomImage(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    public static Image loadRoomImage(String path) {
        if (path != null && !path.isBlank()) {
            Path file = Path.of(path);
            if (Files.exists(file)) {
                try (InputStream in = new FileInputStream(file.toFile())) {
                    return new Image(in);
                } catch (Exception ignored) {
                    // Fall through to bundled placeholder.
                }
            }
            InputStream resource = UiUtils.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (resource != null) {
                return new Image(resource);
            }
        }
        InputStream fallback = UiUtils.class.getResourceAsStream("/images/fallback-room.png");
        if (fallback != null) {
            return new Image(fallback);
        }
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+ip1sAAAAASUVORK5CYII=");
    }

    public static String initials(String name) {
        if (name == null || name.isBlank()) {
            return "SN";
        }
        if (name.equalsIgnoreCase("Guest")) {
            return "G";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private static void style(Alert alert) {
        DialogPane pane = alert.getDialogPane();
        pane.getStyleClass().add("app-dialog");
        var css = UiUtils.class.getResource("/css/app.css");
        var theme = UiUtils.class.getResource(com.studentnest.config.ThemeManager.getInstance().stylesheet());
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }
        if (theme != null) {
            pane.getStylesheets().add(theme.toExternalForm());
        }
    }
}
