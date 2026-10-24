package com.studentnest.controller;

import com.studentnest.model.DashboardStats;
import com.studentnest.model.Feedback;
import com.studentnest.model.Room;
import com.studentnest.model.User;
import com.studentnest.service.AnalyticsService;
import com.studentnest.service.AuthService;
import com.studentnest.service.FeedbackService;
import com.studentnest.service.RoomService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AdminController {
    private final MainController host;
    private final AnalyticsService analytics = new AnalyticsService();
    private final AuthService authService = new AuthService();
    private final RoomService roomService = new RoomService();
    private final FeedbackService feedbackService = new FeedbackService();
    private final FlowPane kpiRow = new FlowPane(14, 14);
    private final PieChart pieChart = new PieChart();
    private final BarChart<String, Number> barChart;
    private final TableView<User> userTable = new TableView<>();
    private final TableView<Room> listingTable = new TableView<>();
    private final TableView<Feedback> feedbackTable = new TableView<>();
    private final VBox root;

    public AdminController(MainController host) {
        this.host = host;
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        barChart = new BarChart<>(x, y);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        pieChart.setAnimated(false);
        pieChart.setLabelsVisible(true);
        pieChart.setTitle("Users by role");
        barChart.setTitle("Listings by location");

        setupUsers();
        setupListings();
        setupFeedback();

        HBox charts = new HBox(14, ViewBuilder.card(pieChart), ViewBuilder.card(barChart));
        HBox.setHgrow(charts.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(charts.getChildren().get(1), Priority.ALWAYS);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                tab("Users", userPanel()),
                tab("Listings", listingPanel()),
                tab("Feedback", feedbackPanel())
        );

        root = ViewBuilder.page(
                "Admin Panel",
                "Live analytics and moderation tools. All figures come from the database.",
                kpiRow,
                charts,
                tabs
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        User actor = SessionManager.getInstance().getCurrentUser();
        kpiRow.getChildren().setAll(ViewBuilder.loading());
        Task<DashboardStats> task = new Task<>() {
            @Override
            protected DashboardStats call() {
                return analytics.load();
            }
        };
        task.setOnSucceeded(e -> renderStats(task.getValue()));
        task.setOnFailed(e -> kpiRow.getChildren().setAll(new Label("Unable to load analytics")));
        Thread thread = new Thread(task, "admin-stats");
        thread.setDaemon(true);
        thread.start();
        try {
            userTable.setItems(FXCollections.observableArrayList(authService.listUsers(actor)));
            listingTable.setItems(FXCollections.observableArrayList(roomService.findAll()));
            feedbackTable.setItems(FXCollections.observableArrayList(feedbackService.moderateList(actor)));
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private void renderStats(DashboardStats stats) {
        kpiRow.getChildren().setAll(
                kpi(String.valueOf(stats.getRegisteredUsers()), "Registered Users"),
                kpi(String.valueOf(stats.getActiveListings()), "Active Listings"),
                kpi(String.valueOf(stats.getVerifiedListings()), "Verified Listings"),
                kpi(String.valueOf(stats.getCommunityReviews()), "Community Reviews")
        );
        pieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Students", stats.getRegisteredStudents()),
                new PieChart.Data("House Owners", stats.getHouseOwners()),
                new PieChart.Data("Admins", stats.getAdmins())
        ));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Listings");
        for (Map.Entry<String, Integer> entry : stats.getListingsByLocation().entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().setAll(series);
    }

    private Node kpi(String value, String label) {
        VBox box = ViewBuilder.card();
        box.setPrefWidth(220);
        Label v = new Label(value);
        v.getStyleClass().add("stat-value");
        Label l = new Label(label);
        l.getStyleClass().add("stat-label");
        box.getChildren().addAll(v, l);
        return box;
    }

    private VBox userPanel() {
        Button delete = new Button("Delete user");
        delete.getStyleClass().addAll("btn", "btn-danger");
        delete.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                NotificationUtils.warning("Select a user first");
                return;
            }
            if (!UiUtils.confirm(host.getStage(), "Delete user", "Delete " + selected.getUsername() + "?")) {
                return;
            }
            try {
                authService.deleteUser(SessionManager.getInstance().getCurrentUser(), selected.getId());
                NotificationUtils.success("User deleted");
                refresh();
            } catch (Exception ex) {
                NotificationUtils.error(ex.getMessage());
            }
        });
        return new VBox(10, new HBox(8, delete), userTable);
    }

    private VBox listingPanel() {
        Button verify = new Button("Verify");
        verify.getStyleClass().addAll("btn", "btn-success");
        verify.setOnAction(e -> setVerified(true));
        Button unverify = new Button("Unverify");
        unverify.getStyleClass().addAll("btn", "btn-warn");
        unverify.setOnAction(e -> setVerified(false));
        Button delete = new Button("Delete listing");
        delete.getStyleClass().addAll("btn", "btn-danger");
        delete.setOnAction(e -> {
            Room selected = listingTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                NotificationUtils.warning("Select a listing first");
                return;
            }
            if (!UiUtils.confirm(host.getStage(), "Delete listing", "Delete \"" + selected.getTitle() + "\"?")) {
                return;
            }
            try {
                roomService.delete(SessionManager.getInstance().getCurrentUser(), selected.getId());
                NotificationUtils.success("Listing deleted");
                refresh();
            } catch (Exception ex) {
                NotificationUtils.error(ex.getMessage());
            }
        });
        return new VBox(10, new HBox(8, verify, unverify, delete), listingTable);
    }

    private VBox feedbackPanel() {
        Button approve = new Button("Approve");
        approve.getStyleClass().addAll("btn", "btn-success");
        approve.setOnAction(e -> moderateFeedback(true));
        Button reject = new Button("Reject");
        reject.getStyleClass().addAll("btn", "btn-warn");
        reject.setOnAction(e -> moderateFeedback(false));
        Button delete = new Button("Delete");
        delete.getStyleClass().addAll("btn", "btn-danger");
        delete.setOnAction(e -> {
            Feedback selected = feedbackTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                NotificationUtils.warning("Select feedback first");
                return;
            }
            try {
                feedbackService.delete(SessionManager.getInstance().getCurrentUser(), selected.getId());
                NotificationUtils.success("Feedback deleted");
                refresh();
            } catch (Exception ex) {
                NotificationUtils.error(ex.getMessage());
            }
        });
        return new VBox(10, new HBox(8, approve, reject, delete), feedbackTable);
    }

    private void setVerified(boolean verified) {
        Room selected = listingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtils.warning("Select a listing first");
            return;
        }
        try {
            roomService.setVerified(SessionManager.getInstance().getCurrentUser(), selected.getId(), verified);
            NotificationUtils.success(verified ? "Listing verified" : "Verification removed");
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private void moderateFeedback(boolean approve) {
        Feedback selected = feedbackTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtils.warning("Select feedback first");
            return;
        }
        try {
            if (approve) {
                feedbackService.approve(SessionManager.getInstance().getCurrentUser(), selected.getId());
            } else {
                feedbackService.reject(SessionManager.getInstance().getCurrentUser(), selected.getId());
            }
            NotificationUtils.success(approve ? "Review approved" : "Review rejected");
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private void setupUsers() {
        userTable.getColumns().addAll(
                userCol("ID", u -> String.valueOf(u.getId())),
                userCol("Name", User::getName),
                userCol("Username", User::getUsername),
                userCol("Phone", User::getPhone),
                userCol("Role", u -> u.getUserType().displayName())
        );
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        userTable.setPrefHeight(280);
    }

    private void setupListings() {
        listingTable.getColumns().addAll(
                roomCol("ID", r -> String.valueOf(r.getId())),
                roomCol("Title", Room::getTitle),
                roomCol("Location", Room::getLocation),
                roomCol("Rent", Room::getFormattedPrice),
                roomCol("Owner", Room::getOwnerName),
                roomCol("Availability", r -> r.isAvailable() ? "Available" : "Unavailable"),
                roomCol("Verification", r -> r.isVerified() ? "Verified" : "Unverified")
        );
        listingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        listingTable.setPrefHeight(280);
    }

    private void setupFeedback() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        feedbackTable.getColumns().addAll(
                fbCol("User", Feedback::getUserName),
                fbCol("Role", Feedback::getUserRole),
                fbCol("Rating", f -> String.valueOf(f.getRating())),
                fbCol("Comment", Feedback::getComment),
                fbCol("Status", f -> f.getStatus().name()),
                fbCol("Date", f -> f.getCreatedAt() == null ? "" : fmt.format(f.getCreatedAt()))
        );
        feedbackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        feedbackTable.setPrefHeight(280);
    }

    private Tab tab(String title, Node content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        return tab;
    }

    private TableColumn<User, String> userCol(String title, java.util.function.Function<User, String> mapper) {
        TableColumn<User, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return column;
    }

    private TableColumn<Room, String> roomCol(String title, java.util.function.Function<Room, String> mapper) {
        TableColumn<Room, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return column;
    }

    private TableColumn<Feedback, String> fbCol(String title, java.util.function.Function<Feedback, String> mapper) {
        TableColumn<Feedback, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return column;
    }
}
