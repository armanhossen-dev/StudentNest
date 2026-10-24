package com.studentnest.controller;

import com.studentnest.model.Room;
import com.studentnest.model.RoomType;
import com.studentnest.model.SearchFilter;
import com.studentnest.service.FavoriteService;
import com.studentnest.service.RoomService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BrowseController {
    private final MainController host;
    private final RoomService roomService = new RoomService();
    private final FavoriteService favoriteService = new FavoriteService();
    private final SessionManager session = SessionManager.getInstance();
    private final VBox root;
    private final FlowPane grid;
    private final TextField searchField;
    private final ComboBox<String> locationBox;
    private final ComboBox<SearchFilter.RentRange> rentBox;
    private final ComboBox<String> typeBox;
    private final ComboBox<SearchFilter.BedroomFilter> bedroomBox;
    private final ComboBox<SearchFilter.SortOption> sortBox;
    private final Label resultLabel;

    public BrowseController(MainController host) {
        this.host = host;
        searchField = new TextField();
        searchField.setPromptText("Search title, location, amenities...");
        locationBox = new ComboBox<>();
        locationBox.getItems().add("All locations");
        locationBox.getItems().addAll(Room.SUPPORTED_LOCATIONS);
        locationBox.getSelectionModel().selectFirst();
        rentBox = new ComboBox<>(FXCollections.observableArrayList(SearchFilter.RentRange.values()));
        rentBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(SearchFilter.RentRange object) { return object == null ? "" : object.getLabel(); }
            @Override public SearchFilter.RentRange fromString(String string) { return SearchFilter.RentRange.ANY; }
        });
        rentBox.getSelectionModel().selectFirst();
        typeBox = new ComboBox<>();
        typeBox.getItems().add("All types");
        for (RoomType type : RoomType.values()) {
            typeBox.getItems().add(type.getDisplayName());
        }
        typeBox.getSelectionModel().selectFirst();
        bedroomBox = new ComboBox<>(FXCollections.observableArrayList(SearchFilter.BedroomFilter.values()));
        bedroomBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(SearchFilter.BedroomFilter object) { return object == null ? "" : object.getLabel(); }
            @Override public SearchFilter.BedroomFilter fromString(String string) { return SearchFilter.BedroomFilter.ANY; }
        });
        bedroomBox.getSelectionModel().selectFirst();
        sortBox = new ComboBox<>(FXCollections.observableArrayList(SearchFilter.SortOption.values()));
        sortBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(SearchFilter.SortOption object) { return object == null ? "" : object.getLabel(); }
            @Override public SearchFilter.SortOption fromString(String string) { return SearchFilter.SortOption.DEFAULT; }
        });
        sortBox.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, o, n) -> refresh());
        locationBox.valueProperty().addListener((obs, o, n) -> refresh());
        rentBox.valueProperty().addListener((obs, o, n) -> refresh());
        typeBox.valueProperty().addListener((obs, o, n) -> refresh());
        bedroomBox.valueProperty().addListener((obs, o, n) -> refresh());
        sortBox.valueProperty().addListener((obs, o, n) -> refresh());

        HBox filters = new HBox(10, searchField, locationBox, rentBox, typeBox, bedroomBox, sortBox);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        resultLabel = new Label();
        resultLabel.getStyleClass().add("muted");
        grid = new FlowPane(16, 16);
        root = ViewBuilder.page(
                "Browse Properties",
                "Search DIU-area housing. Filters and sorting work together.",
                ViewBuilder.card(filters, resultLabel),
                grid
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        SearchFilter filter = currentFilter();
        grid.getChildren().setAll(ViewBuilder.loading());
        Task<List<Room>> task = new Task<>() {
            @Override
            protected List<Room> call() {
                return roomService.search(filter);
            }
        };
        task.setOnSucceeded(e -> render(task.getValue()));
        task.setOnFailed(e -> {
            resultLabel.setText("Unable to load listings.");
            grid.getChildren().setAll(ViewBuilder.empty("Could not load properties", "Check the database connection and try again."));
        });
        Thread thread = new Thread(task, "browse-rooms");
        thread.setDaemon(true);
        thread.start();
    }

    public void openDetails(long roomId) {
        roomService.findById(roomId).ifPresent(room -> PropertyDetailsDialog.show(host, room, this::refresh));
    }

    private void render(List<Room> rooms) {
        resultLabel.setText(rooms.size() + " listings");
        if (rooms.isEmpty()) {
            grid.getChildren().setAll(ViewBuilder.empty("No properties match", "Try a different location, rent range, or keyword."));
            return;
        }
        grid.getChildren().clear();
        for (Room room : rooms) {
            grid.getChildren().add(propertyCard(room));
        }
    }

    private Node propertyCard(Room room) {
        VBox card = new VBox();
        card.getStyleClass().add("property-card");
        card.setPrefWidth(300);

        ImageView image = UiUtils.roomImageView(room.getPrimaryImage(), 300, 160);
        StackPane imageWrap = new StackPane(image);
        HBox badges = new HBox(6);
        badges.setPadding(new Insets(10));
        badges.setAlignment(Pos.TOP_LEFT);
        if (room.isVerified()) {
            badges.getChildren().add(ViewBuilder.badge("Verified", "badge-green"));
        } else {
            badges.getChildren().add(ViewBuilder.badge("Demo / unverified", "badge-yellow"));
        }
        if (!room.isAvailable()) {
            badges.getChildren().add(ViewBuilder.badge("Unavailable", "badge-red"));
        }
        StackPane.setAlignment(badges, Pos.TOP_LEFT);
        imageWrap.getChildren().add(badges);

        VBox body = new VBox(8);
        body.setPadding(new Insets(14));
        Label title = new Label(room.getTitle());
        title.getStyleClass().add("property-title");
        title.setWrapText(true);
        Label meta = new Label(room.getLocation() + "  ·  " + room.getRoomType().getDisplayName() + "  ·  " + room.getBedrooms() + " bed");
        meta.getStyleClass().add("property-meta");
        Label price = new Label(room.getFormattedPrice() + "/month");
        price.getStyleClass().add("price");
        FlowPane amenities = new FlowPane(6, 6);
        for (String item : room.getPreviewAmenities()) {
            Label chip = new Label(item);
            chip.getStyleClass().add("chip");
            amenities.getChildren().add(chip);
        }
        HBox actions = new HBox(8);
        Button details = new Button("View Details");
        details.getStyleClass().addAll("btn", "btn-primary");
        details.setOnAction(e -> PropertyDetailsDialog.show(host, room, this::refresh));
        Button contact = new Button("Contact");
        contact.getStyleClass().addAll("btn", "btn-ghost");
        contact.setOnAction(e -> copyPhone(room.getContactNumber()));
        Button fav = new Button(session.isFavorite(room.getId()) ? "Saved" : "Save");
        fav.getStyleClass().addAll("btn", "btn-secondary");
        fav.setOnAction(e -> {
            boolean saved = favoriteService.toggle(room.getId());
            fav.setText(saved ? "Saved" : "Save");
            NotificationUtils.success(saved ? "Added to favorites" : "Removed from favorites");
            host.refreshUserChrome();
        });
        actions.getChildren().addAll(details, contact, fav);
        body.getChildren().addAll(title, meta, price, amenities, actions);
        card.getChildren().addAll(imageWrap, body);
        return card;
    }

    private void copyPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            NotificationUtils.warning("No contact number available");
            return;
        }
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(phone);
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
        NotificationUtils.success("Copied " + phone);
    }

    private SearchFilter currentFilter() {
        SearchFilter filter = new SearchFilter();
        filter.setKeyword(searchField.getText());
        String location = locationBox.getValue();
        filter.setLocation("All locations".equals(location) ? "" : location);
        filter.setRentRange(rentBox.getValue());
        String type = typeBox.getValue();
        if (type != null && !"All types".equals(type)) {
            filter.setRoomType(RoomType.fromDisplayName(type));
        }
        filter.setBedrooms(bedroomBox.getValue());
        filter.setSortOption(sortBox.getValue());
        return filter;
    }
}
