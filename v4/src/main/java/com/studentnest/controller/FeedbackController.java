package com.studentnest.controller;

import com.studentnest.model.Feedback;
import com.studentnest.service.FeedbackService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeedbackController {
    private final MainController host;
    private final FeedbackService feedbackService = new FeedbackService();
    private final VBox root;
    private final VBox feed;
    private final ToggleGroup stars = new ToggleGroup();
    private final TextArea comment = new TextArea();
    private int rating = 5;

    public FeedbackController(MainController host) {
        this.host = host;
        HBox starRow = new HBox(4);
        for (int i = 1; i <= 5; i++) {
            ToggleButton star = new ToggleButton("★");
            star.getStyleClass().add("star-button");
            star.setUserData(i);
            star.setToggleGroup(stars);
            if (i == 5) {
                star.setSelected(true);
            }
            int value = i;
            star.setOnAction(e -> rating = value);
            starRow.getChildren().add(star);
        }
        comment.setPromptText("Share your experience with StudentNest");
        comment.setPrefRowCount(4);
        comment.setWrapText(true);
        Button submit = new Button("Submit review");
        submit.getStyleClass().addAll("btn", "btn-primary");
        submit.setOnAction(e -> submit());
        Label note = new Label("Reviews appear publicly only after administrator approval.");
        note.getStyleClass().add("muted");
        VBox form = ViewBuilder.card(new Label("Leave a review"), starRow, comment, submit, note);
        if (!SessionManager.getInstance().getUserType().canSubmitReview()) {
            submit.setDisable(true);
            note.setText("Sign in to submit a review. Guests can still read approved testimonials.");
        }
        feed = new VBox(12);
        root = ViewBuilder.page(
                "Feedback & Reviews",
                "Community testimonials from DIU students and house owners.",
                form,
                feed
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        List<Feedback> items = feedbackService.publicFeed();
        if (items.isEmpty()) {
            feed.getChildren().setAll(ViewBuilder.empty("No approved reviews yet", "Approved testimonials will appear here."));
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM yyyy");
        feed.getChildren().clear();
        for (Feedback item : items) {
            VBox card = ViewBuilder.card();
            Label name = new Label(item.getUserName() + "  ·  " + item.getUserRole());
            name.getStyleClass().add("feature-title");
            Label starsLabel = new Label("★".repeat(item.getRating()) + "☆".repeat(5 - item.getRating()));
            starsLabel.setStyle("-fx-text-fill: #EAB308; -fx-font-size: 16px;");
            Label body = new Label(item.getComment());
            body.setWrapText(true);
            Label date = new Label(item.getCreatedAt() == null ? "" : fmt.format(item.getCreatedAt()));
            date.getStyleClass().add("muted");
            card.getChildren().addAll(name, starsLabel, body, date);
            feed.getChildren().add(card);
        }
    }

    private void submit() {
        try {
            feedbackService.submit(SessionManager.getInstance().getCurrentUser(), rating, comment.getText());
            NotificationUtils.success("Review submitted for moderation");
            comment.clear();
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }
}
