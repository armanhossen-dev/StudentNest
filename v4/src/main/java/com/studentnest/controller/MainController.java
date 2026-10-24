package com.studentnest.controller;

import com.studentnest.config.AppConfig;
import com.studentnest.config.ThemeManager;
import com.studentnest.db.DatabaseConnection;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.service.AuthService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {
    @FXML private StackPane rootStack;
    @FXML private StackPane contentPane;
    @FXML private ScrollPane contentScroll;
    @FXML private Label versionBadge;
    @FXML private Label dbStatusLabel;
    @FXML private Region dbDot;
    @FXML private Button favoritesChip;
    @FXML private Button themeButton;
    @FXML private Label avatarLabel;
    @FXML private Label userNameLabel;
    @FXML private Label roleBadge;
    @FXML private Button authButton;
    @FXML private Button navHome;
    @FXML private Button navBrowse;
    @FXML private Button navFavorites;
    @FXML private Button navList;
    @FXML private Button navMine;
    @FXML private Button navAdmin;
    @FXML private Button navFeedback;
    @FXML private Button navAbout;

    private Stage stage;
    private final SessionManager session = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final HomeController homeController = new HomeController(this);
    private final BrowseController browseController = new BrowseController(this);
    private final FavoritesController favoritesController = new FavoritesController(this);
    private final ListPropertyController listPropertyController = new ListPropertyController(this);
    private final OwnerDashboardController ownerDashboardController = new OwnerDashboardController(this);
    private final AdminController adminController = new AdminController(this);
    private final FeedbackController feedbackController = new FeedbackController(this);
    private final AboutController aboutController = new AboutController();
    private Button activeNav;

    @FXML
    public void initialize() {
        NotificationUtils.setHost(rootStack);
        versionBadge.setText(AppConfig.getInstance().getDisplayVersion());
        refreshDatabaseStatus();
        session.currentUserProperty().addListener((obs, oldUser, newUser) -> Platform.runLater(this::refreshUserChrome));
        session.favoriteCountProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> favoritesChip.setText("♥ " + newVal.intValue() + " Saved")));
        ThemeManager.getInstance().darkModeProperty().addListener((obs, oldVal, newVal) -> updateThemeButton());
        updateThemeButton();
        refreshUserChrome();
        showHome();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public StackPane getRootStack() {
        return rootStack;
    }

    @FXML
    public void showHome() {
        setContent(homeController.getView(), navHome);
        homeController.refresh();
    }

    @FXML
    public void showBrowse() {
        setContent(browseController.getView(), navBrowse);
        browseController.refresh();
    }

    @FXML
    public void openFavorites() {
        setContent(favoritesController.getView(), navFavorites);
        favoritesController.refresh();
    }

    @FXML
    public void showListProperty() {
        if (!requireRole(UserType.OWNER, UserType.ADMIN, "Sign in as a house owner to list a property.")) {
            return;
        }
        setContent(listPropertyController.getView(), navList);
        listPropertyController.refresh();
    }

    @FXML
    public void showMyListings() {
        if (!requireRole(UserType.OWNER, UserType.ADMIN, "Sign in as a house owner to manage listings.")) {
            return;
        }
        setContent(ownerDashboardController.getView(), navMine);
        ownerDashboardController.refresh();
    }

    @FXML
    public void showAdmin() {
        if (!requireRole(UserType.ADMIN, UserType.ADMIN, "Administrator access is required.")) {
            return;
        }
        setContent(adminController.getView(), navAdmin);
        adminController.refresh();
    }

    @FXML
    public void showFeedback() {
        setContent(feedbackController.getView(), navFeedback);
        feedbackController.refresh();
    }

    @FXML
    public void showAbout() {
        setContent(aboutController.getView(), navAbout);
    }

    public void openPropertyDetails(long roomId) {
        browseController.openDetails(roomId);
        setContent(browseController.getView(), navBrowse);
    }

    public void editListing(long roomId) {
        listPropertyController.edit(roomId);
        setContent(listPropertyController.getView(), navList);
    }

    @FXML
    public void toggleTheme() {
        ThemeManager.getInstance().toggle();
        NotificationUtils.info(ThemeManager.getInstance().isDarkMode() ? "Dark theme saved" : "Light theme saved");
    }

    @FXML
    public void onAuthClicked() {
        if (session.isLoggedIn()) {
            if (UiUtils.confirm(stage, "Sign out", "Sign out of StudentNest?")) {
                authService.logout();
                NotificationUtils.info("You are now browsing as a guest.");
                showHome();
            }
            return;
        }
        AuthDialog.show(stage, authService, this::refreshUserChrome);
    }

    public void refreshUserChrome() {
        User user = session.getCurrentUser();
        boolean isGuest = user.getUserType() == UserType.GUEST;
        if (isGuest) {
            userNameLabel.setText("Guest");
            roleBadge.setVisible(false);
            roleBadge.setManaged(false);
            avatarLabel.setText("G");
        } else {
            userNameLabel.setText(user.getName());
            roleBadge.setText(user.getUserType().displayName());
            roleBadge.setVisible(true);
            roleBadge.setManaged(true);
            avatarLabel.setText(UiUtils.initials(user.getName()));
        }

        if (session.isLoggedIn()) {
            authButton.setText("Log out");
            authButton.getStyleClass().removeAll("topbar-auth-signin", "topbar-auth-logout");
            authButton.getStyleClass().add("topbar-auth-logout");
        } else {
            authButton.setText("Sign in");
            authButton.getStyleClass().removeAll("topbar-auth-signin", "topbar-auth-logout");
            authButton.getStyleClass().add("topbar-auth-signin");
        }

        favoritesChip.setText("♥ " + session.getFavoriteCount() + " Saved");
        applyNavPermissions(user.getUserType());
        refreshDatabaseStatus();
        homeController.refresh();
    }

    public void refreshDatabaseStatus() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        boolean connected = db.isConnected();
        dbStatusLabel.setText(db.statusLabel());
        dbDot.getStyleClass().removeAll("status-dot-on", "status-dot-off");
        dbDot.getStyleClass().add(connected ? "status-dot-on" : "status-dot-off");
    }

    private void applyNavPermissions(UserType type) {
        boolean owner = type == UserType.OWNER || type == UserType.ADMIN;
        boolean admin = type == UserType.ADMIN;
        navList.setVisible(owner);
        navList.setManaged(owner);
        navMine.setVisible(owner);
        navMine.setManaged(owner);
        navAdmin.setVisible(admin);
        navAdmin.setManaged(admin);
    }

    private boolean requireRole(UserType allowed, UserType also, String message) {
        UserType type = session.getUserType();
        if (type == allowed || type == also) {
            return true;
        }
        NotificationUtils.warning(message);
        if (!session.isLoggedIn()) {
            AuthDialog.show(stage, authService, this::refreshUserChrome);
        }
        type = session.getUserType();
        return type == allowed || type == also;
    }

    private void setContent(Node node, Button nav) {
        contentPane.getChildren().setAll(node);
        contentScroll.setVvalue(0);
        if (activeNav != null) {
            activeNav.getStyleClass().remove("active");
        }
        if (nav != null && !nav.getStyleClass().contains("active")) {
            nav.getStyleClass().add("active");
        }
        activeNav = nav;
    }

    private void updateThemeButton() {
        themeButton.setText(ThemeManager.getInstance().isDarkMode() ? "☀ Light" : "🌙 Dark");
    }
}
