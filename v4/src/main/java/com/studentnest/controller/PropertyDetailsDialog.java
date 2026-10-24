package com.studentnest.controller;

import com.studentnest.model.Room;
import com.studentnest.service.FavoriteService;
import com.studentnest.service.InquiryService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import com.studentnest.util.ValidationUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class PropertyDetailsDialog {
    private PropertyDetailsDialog() {
    }

    public static void show(MainController host, Room room, Runnable onChange) {
        Stage dialog = new Stage();
        dialog.initOwner(host.getStage());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(room.getTitle());

        VBox root = new VBox(16);
        root.getStyleClass().addAll("modal-card", "dialog-window-card");
        root.setPrefWidth(640);
        root.setPadding(new Insets(16, 20, 20, 20));

        List<String> images = room.getImagePaths();
        if (images.isEmpty()) {
            images = List.of("");
        }
        AtomicInteger index = new AtomicInteger(0);
        ImageView gallery = UiUtils.roomImageView(images.get(0), 600, 240);
        HBox galleryNav = new HBox(8);
        galleryNav.setAlignment(Pos.CENTER);
        Button prev = new Button("Previous");
        prev.getStyleClass().addAll("btn", "btn-ghost");
        Button next = new Button("Next");
        next.getStyleClass().addAll("btn", "btn-ghost");
        Label counter = new Label("1 / " + images.size());
        counter.getStyleClass().add("muted");
        List<String> imageList = images;
        prev.setOnAction(e -> {
            int i = (index.get() - 1 + imageList.size()) % imageList.size();
            index.set(i);
            gallery.setImage(UiUtils.loadRoomImage(imageList.get(i)));
            counter.setText((i + 1) + " / " + imageList.size());
        });
        next.setOnAction(e -> {
            int i = (index.get() + 1) % imageList.size();
            index.set(i);
            gallery.setImage(UiUtils.loadRoomImage(imageList.get(i)));
            counter.setText((i + 1) + " / " + imageList.size());
        });
        galleryNav.getChildren().addAll(prev, counter, next);

        Label title = new Label(room.getTitle());
        title.getStyleClass().add("page-title");
        title.setWrapText(true);
        Label price = new Label(room.getFormattedPrice() + " / month");
        price.getStyleClass().add("price");
        HBox badges = new HBox(8);
        badges.getChildren().add(ViewBuilder.badge(room.isVerified() ? "Verified" : "Unverified demo listing",
                room.isVerified() ? "badge-green" : "badge-yellow"));
        badges.getChildren().add(ViewBuilder.badge(room.isAvailable() ? "Available" : "Unavailable",
                room.isAvailable() ? "badge-green" : "badge-red"));

        Label meta = new Label(room.getLocation() + "  ·  " + room.getRoomType().getDisplayName()
                + "  ·  " + room.getBedrooms() + " bedroom(s)  ·  Owner: " + room.getOwnerName());
        meta.getStyleClass().add("page-subtitle");
        meta.setWrapText(true);
        Label description = new Label(room.getDescription());
        description.setWrapText(true);

        FlowPane amenities = new FlowPane(6, 6);
        for (String item : room.getAmenityList()) {
            Label chip = new Label(item);
            chip.getStyleClass().add("chip");
            amenities.getChildren().add(chip);
        }

        HBox actions = new HBox(8);
        Button copy = new Button("Copy Phone");
        copy.getStyleClass().addAll("btn", "btn-secondary");
        copy.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(room.getContactNumber());
            Clipboard.getSystemClipboard().setContent(content);
            NotificationUtils.success("Copied " + room.getContactNumber());
        });
        Button maps = new Button("Open Google Maps");
        maps.getStyleClass().addAll("btn", "btn-ghost");
        maps.setOnAction(e -> openMaps(room));
        FavoriteService favoriteService = new FavoriteService();
        Button fav = new Button(SessionManager.getInstance().isFavorite(room.getId()) ? "Unsave" : "Save");
        fav.getStyleClass().addAll("btn", "btn-secondary");
        fav.setOnAction(e -> {
            boolean saved = favoriteService.toggle(room.getId());
            fav.setText(saved ? "Unsave" : "Save");
            host.refreshUserChrome();
            if (onChange != null) {
                onChange.run();
            }
            NotificationUtils.success(saved ? "Saved" : "Removed from favorites");
        });
        actions.getChildren().addAll(copy, maps, fav);

        VBox inquiryBox = buildInquiry(room);
        root.getChildren().addAll(gallery, galleryNav, title, price, badges, meta, description, amenities, actions, inquiryBox);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(root);
        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 680, 760);
        if (host.getStage() != null && host.getStage().getScene() != null) {
            scene.getStylesheets().addAll(host.getStage().getScene().getStylesheets());
        }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static VBox buildInquiry(Room room) {
        Label heading = new Label("Submit Inquiry");
        heading.getStyleClass().add("feature-title");
        TextField name = new TextField();
        name.setPromptText("Your name");
        TextField contact = new TextField();
        contact.setPromptText("Contact number");
        TextArea message = new TextArea();
        message.setPromptText("Introduce yourself and ask about availability");
        message.setPrefRowCount(3);
        Label error = new Label();
        error.getStyleClass().add("field-error");
        error.setVisible(false);
        error.setManaged(false);
        if (SessionManager.getInstance().isLoggedIn()) {
            name.setText(SessionManager.getInstance().getCurrentUser().getName());
            contact.setText(SessionManager.getInstance().getCurrentUser().getPhone());
        }
        Button send = new Button("Submit Inquiry");
        send.getStyleClass().addAll("btn", "btn-primary");
        send.setOnAction(e -> {
            try {
                new InquiryService().submit(SessionManager.getInstance().getCurrentUser(),
                        room.getId(), name.getText(), contact.getText(), message.getText());
                NotificationUtils.success("Inquiry sent");
                message.clear();
                error.setVisible(false);
                error.setManaged(false);
            } catch (Exception ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
                error.setManaged(true);
            }
        });
        VBox box = ViewBuilder.quietCard(heading, name, contact, message, error, send);
        if (!SessionManager.getInstance().getUserType().canSubmitInquiry()) {
            send.setDisable(true);
            Label note = new Label("Sign in as a student to send an inquiry.");
            note.getStyleClass().add("muted");
            box.getChildren().add(note);
        }
        return box;
    }

    private static void openMaps(Room room) {
        try {
            String url = room.getMapLink();
            if (url == null || url.isBlank()) {
                url = ValidationUtils.mapsSearchUrl(room.getLocation());
            } else {
                url = ValidationUtils.optionalHttpUrl(url);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
                NotificationUtils.info("Opening maps");
            } else {
                ClipboardContent content = new ClipboardContent();
                content.putString(url);
                Clipboard.getSystemClipboard().setContent(content);
                NotificationUtils.info("Maps URL copied");
            }
        } catch (Exception ex) {
            NotificationUtils.error("Unable to open maps");
        }
    }
}
