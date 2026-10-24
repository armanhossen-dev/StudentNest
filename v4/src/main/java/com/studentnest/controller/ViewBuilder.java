package com.studentnest.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ViewBuilder {
    private ViewBuilder() {
    }

    public static VBox page(String title, String subtitle, Node... extras) {
        VBox box = new VBox(18);
        box.setPadding(new Insets(4, 4, 28, 4));
        Label heading = new Label(title);
        heading.getStyleClass().add("page-title");
        Label copy = new Label(subtitle);
        copy.getStyleClass().add("page-subtitle");
        copy.setWrapText(true);
        VBox header = new VBox(6, heading, copy);
        box.getChildren().add(header);
        if (extras != null) {
            box.getChildren().addAll(extras);
        }
        return box;
    }

    public static VBox card(Node... children) {
        VBox card = new VBox(12, children);
        card.getStyleClass().add("card");
        return card;
    }

    public static VBox quietCard(Node... children) {
        VBox card = new VBox(10, children);
        card.getStyleClass().add("card-quiet");
        return card;
    }

    public static Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public static VBox empty(String title, String copy) {
        VBox box = new VBox(8);
        box.getStyleClass().add("empty-state");
        box.setAlignment(Pos.CENTER);
        Label heading = new Label(title);
        heading.getStyleClass().add("empty-title");
        Label body = new Label(copy);
        body.getStyleClass().add("empty-copy");
        body.setWrapText(true);
        box.getChildren().addAll(heading, body);
        return box;
    }

    public static HBox loading() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(28, 28);
        Label label = new Label("Loading listings...");
        label.getStyleClass().add("muted");
        HBox box = new HBox(12, indicator, label);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(16));
        return box;
    }

    public static Label badge(String text, String extraClass) {
        Label label = new Label(text);
        label.getStyleClass().addAll("badge", extraClass);
        return label;
    }
}
