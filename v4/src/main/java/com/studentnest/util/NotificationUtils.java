package com.studentnest.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class NotificationUtils {
    public enum Kind {
        SUCCESS, INFO, WARNING, ERROR
    }

    private static Pane host;

    private NotificationUtils() {
    }

    public static void setHost(Pane pane) {
        host = pane;
    }

    public static void success(String message) {
        show(Kind.SUCCESS, "Success", message);
    }

    public static void info(String message) {
        show(Kind.INFO, "Info", message);
    }

    public static void warning(String message) {
        show(Kind.WARNING, "Warning", message);
    }

    public static void error(String message) {
        show(Kind.ERROR, "Error", message);
    }

    public static void show(Kind kind, String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(kind, title, message));
            return;
        }
        if (host == null) {
            return;
        }

        // Remove any previous active toast so they do not overlap
        host.getChildren().removeIf(node -> node.getStyleClass().contains("toast"));

        VBox toast = new VBox(2);
        toast.getStyleClass().addAll("toast", "toast-" + kind.name().toLowerCase());
        toast.setPadding(new Insets(10, 16, 10, 16));
        toast.setMaxWidth(340);
        toast.setMaxHeight(Region.USE_PREF_SIZE);
        toast.setPrefHeight(Region.USE_PREF_SIZE);

        Label heading = new Label(title);
        heading.getStyleClass().add("toast-title");
        Label body = new Label(message);
        body.setWrapText(true);
        body.getStyleClass().add("toast-body");
        toast.getChildren().addAll(heading, body);

        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 24, 24, 0));
        host.getChildren().add(toast);

        toast.setOpacity(0);
        toast.setTranslateY(24);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toast);
        fadeIn.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(180), toast);
        slide.setToY(0);
        fadeIn.play();
        slide.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(3.4));
        pause.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(220), toast);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> host.getChildren().remove(toast));
            fadeOut.play();
        });
        pause.play();
    }
}
