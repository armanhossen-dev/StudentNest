package com.studentnest.controller;

import com.studentnest.model.Room;
import com.studentnest.model.RoomType;
import com.studentnest.service.RoomService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.ImageStorage;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.ValidationUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListPropertyController {
    private final MainController host;
    private final RoomService roomService = new RoomService();
    private final VBox root;
    private final TextField titleField = new TextField();
    private final ComboBox<String> locationBox = new ComboBox<>();
    private final ComboBox<RoomType> typeBox = new ComboBox<>();
    private final TextField priceField = new TextField();
    private final Spinner<Integer> bedroomSpinner = new Spinner<>(1, 12, 1);
    private final TextField phoneField = new TextField();
    private final TextField mapField = new TextField();
    private final TextArea descriptionArea = new TextArea();
    private final Label imageLabel = new Label("No images selected");
    private final Label errorLabel = new Label();
    private final List<CheckBox> amenityBoxes = new ArrayList<>();
    private String image1;
    private String image2;
    private String image3;
    private Long editingId;

    public ListPropertyController(MainController host) {
        this.host = host;
        locationBox.getItems().addAll(Room.SUPPORTED_LOCATIONS);
        locationBox.getSelectionModel().selectFirst();
        typeBox.getItems().addAll(RoomType.values());
        typeBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(RoomType object) { return object == null ? "" : object.getDisplayName(); }
            @Override public RoomType fromString(String string) { return RoomType.fromDisplayName(string); }
        });
        typeBox.getSelectionModel().selectFirst();
        bedroomSpinner.setEditable(true);
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setWrapText(true);
        errorLabel.getStyleClass().add("field-error");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        imageLabel.getStyleClass().add("muted");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.add(labeled("Title", titleField), 0, 0, 2, 1);
        grid.add(labeled("Location", locationBox), 0, 1);
        grid.add(labeled("Property type", typeBox), 1, 1);
        grid.add(labeled("Monthly rent (BDT)", priceField), 0, 2);
        grid.add(labeled("Bedrooms", bedroomSpinner), 1, 2);
        grid.add(labeled("Contact number", phoneField), 0, 3);
        grid.add(labeled("Google Maps URL (optional)", mapField), 1, 3);
        grid.add(labeled("Description", descriptionArea), 0, 4, 2, 1);

        FlowPane amenities = new FlowPane(12, 8);
        for (String amenity : Room.SUPPORTED_AMENITIES) {
            CheckBox box = new CheckBox(amenity);
            amenityBoxes.add(box);
            amenities.getChildren().add(box);
        }

        Button upload = new Button("Upload images");
        upload.getStyleClass().addAll("btn", "btn-ghost");
        upload.setOnAction(e -> chooseImages());
        Button save = new Button("Save listing");
        save.getStyleClass().addAll("btn", "btn-primary");
        save.setOnAction(e -> save());
        Button reset = new Button("Clear form");
        reset.getStyleClass().addAll("btn", "btn-secondary");
        reset.setOnAction(e -> clear());
        HBox actions = new HBox(10, save, reset, upload);

        Label amenityTitle = new Label("Amenities");
        amenityTitle.getStyleClass().add("stat-label");
        VBox form = ViewBuilder.card(grid, amenityTitle, amenities, imageLabel, errorLabel, actions);
        root = ViewBuilder.page(
                "List Property",
                "New listings start unverified. An administrator must review them before they receive a verification badge.",
                form
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        if (editingId == null && SessionManager.getInstance().isLoggedIn()
                && (phoneField.getText() == null || phoneField.getText().isBlank())) {
            phoneField.setText(SessionManager.getInstance().getCurrentUser().getPhone());
        }
    }

    public void edit(long roomId) {
        roomService.findById(roomId).ifPresent(room -> {
            editingId = room.getId();
            titleField.setText(room.getTitle());
            locationBox.setValue(Room.SUPPORTED_LOCATIONS.stream()
                    .filter(item -> room.getLocation().toLowerCase().contains(item.toLowerCase()))
                    .findFirst().orElse(room.getLocation()));
            typeBox.setValue(room.getRoomType());
            priceField.setText(String.valueOf((long) room.getPrice()));
            bedroomSpinner.getValueFactory().setValue(room.getBedrooms());
            phoneField.setText(room.getContactNumber());
            mapField.setText(room.getMapLink());
            descriptionArea.setText(room.getDescription());
            image1 = room.getImage1Path();
            image2 = room.getImage2Path();
            image3 = room.getImage3Path();
            imageLabel.setText(countImages() + " image(s) attached");
            for (CheckBox box : amenityBoxes) {
                box.setSelected(room.getAmenityList().contains(box.getText()));
            }
        });
    }

    private void save() {
        try {
            Room room = editingId == null ? new Room() : roomService.findById(editingId)
                    .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
            room.setTitle(titleField.getText());
            room.setLocation(locationBox.getValue());
            room.setRoomType(typeBox.getValue());
            room.setPrice(ValidationUtils.requirePositivePrice(priceField.getText()));
            room.setBedrooms(bedroomSpinner.getValue());
            room.setContactNumber(phoneField.getText());
            room.setMapLink(mapField.getText());
            room.setDescription(descriptionArea.getText());
            List<String> selected = amenityBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
            room.setAmenities(String.join(", ", ValidationUtils.sanitizeAmenities(selected)));
            room.setImage1Path(image1);
            room.setImage2Path(image2);
            room.setImage3Path(image3);
            if (editingId == null) {
                roomService.create(SessionManager.getInstance().getCurrentUser(), room);
                NotificationUtils.success("Listing created and awaiting verification");
            } else {
                room.setId(editingId);
                roomService.update(SessionManager.getInstance().getCurrentUser(), room);
                NotificationUtils.success("Listing updated");
            }
            clear();
            host.showMyListings();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void chooseImages() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose listing photos");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
        List<File> files = chooser.showOpenMultipleDialog(host.getStage());
        if (files == null || files.isEmpty()) {
            return;
        }
        try {
            String[] slots = new String[3];
            int i = 0;
            for (File file : files) {
                if (i >= 3) {
                    break;
                }
                slots[i++] = ImageStorage.copyUpload(file.toPath());
            }
            image1 = slots[0];
            image2 = slots[1];
            image3 = slots[2];
            imageLabel.setText(countImages() + " image(s) copied into room_images/");
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private int countImages() {
        int count = 0;
        if (image1 != null) count++;
        if (image2 != null) count++;
        if (image3 != null) count++;
        return count;
    }

    private void clear() {
        editingId = null;
        titleField.clear();
        priceField.clear();
        mapField.clear();
        descriptionArea.clear();
        image1 = image2 = image3 = null;
        imageLabel.setText("No images selected");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        amenityBoxes.forEach(box -> box.setSelected(false));
        typeBox.getSelectionModel().selectFirst();
        locationBox.getSelectionModel().selectFirst();
        bedroomSpinner.getValueFactory().setValue(1);
    }

    private VBox labeled(String title, Node field) {
        Label label = new Label(title);
        label.getStyleClass().add("stat-label");
        return new VBox(4, label, field);
    }
}
