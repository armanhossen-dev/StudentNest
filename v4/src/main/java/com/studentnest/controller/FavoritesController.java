package com.studentnest.controller;

import com.studentnest.model.Room;
import com.studentnest.service.FavoriteService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class FavoritesController {
    private final MainController host;
    private final FavoriteService favoriteService = new FavoriteService();
    private final SessionManager session = SessionManager.getInstance();
    private final VBox root;
    private final FlowPane grid;
    private final Label hint;

    public FavoritesController(MainController host) {
        this.host = host;
        hint = new Label();
        hint.getStyleClass().add("page-subtitle");
        hint.setWrapText(true);
        grid = new FlowPane(16, 16);
        root = ViewBuilder.page(
                "Saved Favorites",
                "Keep shortlisted rooms in one place. Guest saves stay on this device until you sign in.",
                hint,
                grid
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        hint.setText(session.isLoggedIn()
                ? "Favorites are stored with your account."
                : "Guest favorites are stored locally and merge into your account after sign-in.");
        List<Room> rooms = favoriteService.listFavoriteRooms();
        if (rooms.isEmpty()) {
            grid.getChildren().setAll(ViewBuilder.empty("No saved rooms yet", "Browse properties and tap Save to build a shortlist."));
            return;
        }
        grid.getChildren().clear();
        for (Room room : rooms) {
            grid.getChildren().add(card(room));
        }
    }

    private Node card(Room room) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(300);
        ImageView image = UiUtils.roomImageView(room.getPrimaryImage(), 270, 140);
        Label title = new Label(room.getTitle());
        title.getStyleClass().add("property-title");
        title.setWrapText(true);
        Label meta = new Label(room.getLocation() + "  ·  " + room.getFormattedPrice() + "/month");
        meta.getStyleClass().add("property-meta");
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button details = new Button("View Details");
        details.getStyleClass().addAll("btn", "btn-primary");
        details.setOnAction(e -> PropertyDetailsDialog.show(host, room, this::refresh));
        Button remove = new Button("Remove");
        remove.getStyleClass().addAll("btn", "btn-ghost");
        remove.setOnAction(e -> {
            favoriteService.toggle(room.getId());
            NotificationUtils.info("Removed from favorites");
            host.refreshUserChrome();
            refresh();
        });
        actions.getChildren().addAll(details, remove);
        card.getChildren().addAll(image, title, meta, actions);
        return card;
    }
}
