package com.studentnest.controller;

import com.studentnest.model.DashboardStats;
import com.studentnest.service.AnalyticsService;
import com.studentnest.service.SessionManager;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HomeController {
    private final MainController host;
    private final AnalyticsService analytics = new AnalyticsService();
    private final VBox root;
    private final HBox statsRow;
    private final VBox hero;
    private final Label guestNote;

    public HomeController(MainController host) {
        this.host = host;
        statsRow = new HBox(14);
        guestNote = new Label();
        guestNote.getStyleClass().add("hero-copy");
        guestNote.setWrapText(true);
        hero = buildHero();
        FlowPane features = buildFeatures();
        root = ViewBuilder.page(
                "Home Overview",
                "Find verified student housing around Daffodil International University.",
                hero,
                statsRow,
                features
        );
        refresh();
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        if (SessionManager.getInstance().isLoggedIn()) {
            hero.getChildren().remove(guestNote);
        } else if (!hero.getChildren().contains(guestNote)) {
            guestNote.setText("Browsing as guest. Sign in to keep favorites, send inquiries, and leave reviews.");
            hero.getChildren().add(guestNote);
        }
        statsRow.getChildren().setAll(ViewBuilder.loading());
        Task<DashboardStats> task = new Task<>() {
            @Override
            protected DashboardStats call() {
                return analytics.load();
            }
        };
        task.setOnSucceeded(e -> {
            DashboardStats data = task.getValue();
            Node verified = statCard(String.valueOf(data.getVerifiedListings()), "Verified properties");
            Node students = statCard(String.valueOf(data.getRegisteredStudents()), "Registered students");
            Node available = statCard(String.valueOf(data.getAvailableListings()), "Available listings");
            statsRow.getChildren().setAll(verified, students, available);
            HBox.setHgrow(verified, Priority.ALWAYS);
            HBox.setHgrow(students, Priority.ALWAYS);
            HBox.setHgrow(available, Priority.ALWAYS);
        });
        task.setOnFailed(e -> {
            Label error = new Label("Unable to load live statistics.");
            error.getStyleClass().add("field-error");
            statsRow.getChildren().setAll(error);
        });
        Thread thread = new Thread(task, "home-stats");
        thread.setDaemon(true);
        thread.start();
    }

    private VBox buildHero() {
        VBox hero = new VBox(14);
        hero.getStyleClass().add("hero");
        Label title = new Label("Campus housing, without the guesswork");
        title.getStyleClass().add("hero-title");
        title.setWrapText(true);
        Label copy = new Label("Browse Khagan, Candgaon, Charabag, Kumkumari, Dattopara, and Shadhupara with live availability, owner contact, and inquiry tracking.");
        copy.getStyleClass().add("hero-copy");
        copy.setWrapText(true);
        HBox actions = new HBox(10);
        Button browse = new Button("Browse Properties");
        browse.getStyleClass().addAll("btn", "btn-primary");
        browse.setOnAction(e -> host.showBrowse());
        Button list = new Button("List Your Property");
        list.getStyleClass().addAll("btn", "btn-warn");
        list.setOnAction(e -> host.showListProperty());
        actions.getChildren().addAll(browse, list);
        hero.getChildren().addAll(title, copy, actions);
        return hero;
    }

    private FlowPane buildFeatures() {
        FlowPane features = new FlowPane(14, 14);
        features.getChildren().addAll(
                feature("Smart Search", "Filter by location, rent, type, bedrooms, and amenities in real time.", "feature-blue"),
                feature("Verified Listings", "Administrators review listings before they receive a verification badge.", "feature-green"),
                feature("Direct Contact", "Copy owner phone numbers instantly and keep the conversation off-platform.", "feature-yellow"),
                feature("Campus-Focused Housing", "Purpose-built for DIU students living around the Ashulia campus belt.", "feature-pink")
        );
        return features;
    }

    private Node statCard(String value, String label) {
        VBox box = new VBox(4);
        box.getStyleClass().add("card");
        Label v = new Label(value);
        v.getStyleClass().add("stat-value");
        Label l = new Label(label);
        l.getStyleClass().add("stat-label");
        box.getChildren().addAll(v, l);
        return box;
    }

    private Node feature(String title, String copy, String colorClass) {
        VBox box = new VBox(8);
        box.getStyleClass().addAll("card", colorClass);
        box.setPrefWidth(280);
        Label heading = new Label(title);
        heading.getStyleClass().add("feature-title");
        Label body = new Label(copy);
        body.setWrapText(true);
        body.getStyleClass().add("page-subtitle");
        box.getChildren().addAll(heading, body);
        return box;
    }
}
