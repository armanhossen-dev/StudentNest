package com.studentnest.controller;

import com.studentnest.model.UserType;
import com.studentnest.service.AuthService;
import com.studentnest.util.NotificationUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class AuthDialog {
    private AuthDialog() {
    }

    public static void show(Stage owner, AuthService authService, Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Sign in to StudentNest");
        dialog.setResizable(false);

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(loginTab(authService, dialog, onSuccess), registerTab(authService, dialog, onSuccess));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox root = new VBox(16);
        root.getStyleClass().addAll("modal-card", "auth-dialog-root");
        root.setPadding(new Insets(16, 20, 20, 20));
        Label heading = new Label("Welcome to StudentNest");
        heading.getStyleClass().add("page-title");
        Label note = new Label("Development-only demo accounts: student/student123, owner/owner123, admin/admin123");
        note.getStyleClass().add("demo-note");
        note.setWrapText(true);
        root.getChildren().addAll(heading, note, tabs);

        Scene scene = new Scene(root, 460, 640);
        if (owner != null && owner.getScene() != null) {
            scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static Tab loginTab(AuthService authService, Stage dialog, Runnable onSuccess) {
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Label error = new Label();
        error.getStyleClass().add("field-error");
        error.setVisible(false);
        error.setManaged(false);

        Button signIn = new Button("Sign in");
        signIn.getStyleClass().addAll("btn", "btn-primary");
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setOnAction(e -> {
            try {
                authService.login(username.getText(), password.getText());
                NotificationUtils.success("Signed in");
                onSuccess.run();
                dialog.close();
            } catch (Exception ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
                error.setManaged(true);
            }
        });

        Button guest = new Button("Continue as guest");
        guest.getStyleClass().addAll("btn", "btn-ghost");
        guest.setMaxWidth(Double.MAX_VALUE);
        guest.setOnAction(e -> {
            authService.continueAsGuest();
            dialog.close();
        });

        VBox demos = new VBox(8);
        demos.getChildren().addAll(
                demoButton("Student demo", "student", "student123", authService, dialog, onSuccess),
                demoButton("Owner demo", "owner", "owner123", authService, dialog, onSuccess),
                demoButton("Admin demo", "admin", "admin123", authService, dialog, onSuccess)
        );

        VBox form = new VBox(10, labeled("Username", username), labeled("Password", password), error, signIn, guest,
                new Label("Development-only demo logins"), demos);
        form.setPadding(new Insets(16));
        Tab tab = new Tab("Sign in");
        tab.setContent(form);
        return tab;
    }

    private static Tab registerTab(AuthService authService, Stage dialog, Runnable onSuccess) {
        TextField name = new TextField();
        name.setPromptText("Full name");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField phone = new TextField();
        phone.setPromptText("01XXXXXXXXX");
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password (min 6 characters)");
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("Student", "House Owner");
        role.getSelectionModel().selectFirst();
        Label error = new Label();
        error.getStyleClass().add("field-error");
        error.setWrapText(true);
        error.setVisible(false);
        error.setManaged(false);

        Button register = new Button("Create account");
        register.getStyleClass().addAll("btn", "btn-primary");
        register.setMaxWidth(Double.MAX_VALUE);
        register.setOnAction(e -> {
            try {
                UserType type = AuthService.parseRegistrationType(role.getValue());
                authService.register(name.getText(), email.getText(), phone.getText(),
                        username.getText(), password.getText(), type);
                NotificationUtils.success("Account created");
                onSuccess.run();
                dialog.close();
            } catch (Exception ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
                error.setManaged(true);
            }
        });

        VBox form = new VBox(10,
                labeled("Full name", name),
                labeled("Email", email),
                labeled("Phone", phone),
                labeled("Username", username),
                labeled("Password", password),
                labeled("Account type", role),
                error,
                register
        );
        form.setPadding(new Insets(16));
        Tab tab = new Tab("Register");
        tab.setContent(form);
        return tab;
    }

    private static Button demoButton(String label, String username, String password,
                                     AuthService authService, Stage dialog, Runnable onSuccess) {
        Button button = new Button(label);
        button.getStyleClass().addAll("btn", "btn-secondary");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            try {
                authService.login(username, password);
                NotificationUtils.success("Signed in with " + label.toLowerCase());
                onSuccess.run();
                dialog.close();
            } catch (Exception ex) {
                NotificationUtils.error(ex.getMessage());
            }
        });
        return button;
    }

    private static VBox labeled(String title, javafx.scene.Node field) {
        Label label = new Label(title);
        label.getStyleClass().add("stat-label");
        VBox box = new VBox(4, label, field);
        return box;
    }
}
