package com.studentnest.controller;

import com.studentnest.model.Inquiry;
import com.studentnest.model.InquiryStatus;
import com.studentnest.model.Room;
import com.studentnest.model.User;
import com.studentnest.model.UserType;
import com.studentnest.service.InquiryService;
import com.studentnest.service.RoomService;
import com.studentnest.service.SessionManager;
import com.studentnest.util.NotificationUtils;
import com.studentnest.util.UiUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OwnerDashboardController {
    private final MainController host;
    private final RoomService roomService = new RoomService();
    private final InquiryService inquiryService = new InquiryService();
    private final TableView<Room> roomTable = new TableView<>();
    private final TableView<Inquiry> inquiryTable = new TableView<>();
    private final VBox root;

    public OwnerDashboardController(MainController host) {
        this.host = host;
        setupRoomTable();
        setupInquiryTable();

        Button add = new Button("Add listing");
        add.getStyleClass().addAll("btn", "btn-primary");
        add.setOnAction(e -> host.showListProperty());
        Button edit = new Button("Edit");
        edit.getStyleClass().addAll("btn", "btn-secondary");
        edit.setOnAction(e -> {
            Room selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                NotificationUtils.warning("Select a listing first");
                return;
            }
            host.editListing(selected.getId());
        });
        Button toggle = new Button("Toggle availability");
        toggle.getStyleClass().addAll("btn", "btn-ghost");
        toggle.setOnAction(e -> toggleAvailability());
        Button delete = new Button("Delete");
        delete.getStyleClass().addAll("btn", "btn-danger");
        delete.setOnAction(e -> deleteListing());
        HBox roomActions = new HBox(8, add, edit, toggle, delete);

        Button accept = new Button("Accept");
        accept.getStyleClass().addAll("btn", "btn-success");
        accept.setOnAction(e -> updateInquiry(InquiryStatus.ACCEPTED));
        Button decline = new Button("Decline");
        decline.getStyleClass().addAll("btn", "btn-danger");
        decline.setOnAction(e -> updateInquiry(InquiryStatus.DECLINED));
        HBox inquiryActions = new HBox(8, accept, decline);

        VBox listingsCard = ViewBuilder.card(new Label("My listings"), roomActions, roomTable);
        VBox inquiryCard = ViewBuilder.card(new Label("Inquiries"), inquiryActions, inquiryTable);
        VBox.setVgrow(roomTable, Priority.ALWAYS);
        VBox.setVgrow(inquiryTable, Priority.ALWAYS);
        root = ViewBuilder.page(
                "My Listings",
                "Manage your properties and respond to student inquiries.",
                listingsCard,
                inquiryCard
        );
    }

    public Node getView() {
        return root;
    }

    public void refresh() {
        User actor = SessionManager.getInstance().getCurrentUser();
        List<Room> rooms = actor.getUserType() == UserType.ADMIN
                ? roomService.findAll()
                : roomService.findByOwner(actor.getId());
        roomTable.setItems(FXCollections.observableArrayList(rooms));
        try {
            inquiryTable.setItems(FXCollections.observableArrayList(inquiryService.listFor(actor)));
        } catch (Exception ex) {
            inquiryTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void setupRoomTable() {
        TableColumn<Room, String> id = col("ID", room -> String.valueOf(room.getId()));
        TableColumn<Room, String> title = col("Title", Room::getTitle);
        TableColumn<Room, String> location = col("Location", Room::getLocation);
        TableColumn<Room, String> rent = col("Rent", Room::getFormattedPrice);
        TableColumn<Room, String> type = col("Type", room -> room.getRoomType().getDisplayName());
        TableColumn<Room, String> availability = col("Availability", room -> room.isAvailable() ? "Available" : "Unavailable");
        TableColumn<Room, String> verified = col("Verification", room -> room.isVerified() ? "Verified" : "Unverified");
        roomTable.getColumns().addAll(id, title, location, rent, type, availability, verified);
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        roomTable.setPrefHeight(260);
        roomTable.setPlaceholder(new Label("No listings yet"));
    }

    private void setupInquiryTable() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TableColumn<Inquiry, String> property = inquiryCol("Property", Inquiry::getRoomTitle);
        TableColumn<Inquiry, String> name = inquiryCol("Student", Inquiry::getStudentName);
        TableColumn<Inquiry, String> contact = inquiryCol("Contact", Inquiry::getStudentContact);
        TableColumn<Inquiry, String> message = inquiryCol("Message", Inquiry::getMessage);
        TableColumn<Inquiry, String> status = inquiryCol("Status", i -> i.getStatus().name());
        TableColumn<Inquiry, String> date = inquiryCol("Date", i -> i.getCreatedAt() == null ? "" : fmt.format(i.getCreatedAt()));
        inquiryTable.getColumns().addAll(property, name, contact, message, status, date);
        inquiryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        inquiryTable.setPrefHeight(240);
        inquiryTable.setPlaceholder(new Label("No inquiries yet"));
    }

    private void toggleAvailability() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtils.warning("Select a listing first");
            return;
        }
        try {
            roomService.toggleAvailability(SessionManager.getInstance().getCurrentUser(), selected.getId());
            NotificationUtils.success("Availability updated");
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private void deleteListing() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtils.warning("Select a listing first");
            return;
        }
        if (!UiUtils.confirm(host.getStage(), "Delete listing", "Delete \"" + selected.getTitle() + "\"? This cannot be undone.")) {
            return;
        }
        try {
            roomService.delete(SessionManager.getInstance().getCurrentUser(), selected.getId());
            NotificationUtils.success("Listing deleted");
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private void updateInquiry(InquiryStatus status) {
        Inquiry selected = inquiryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtils.warning("Select an inquiry first");
            return;
        }
        try {
            inquiryService.updateStatus(SessionManager.getInstance().getCurrentUser(), selected.getId(), status);
            NotificationUtils.success("Inquiry " + status.name().toLowerCase());
            refresh();
        } catch (Exception ex) {
            NotificationUtils.error(ex.getMessage());
        }
    }

    private TableColumn<Room, String> col(String title, java.util.function.Function<Room, String> mapper) {
        TableColumn<Room, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return column;
    }

    private TableColumn<Inquiry, String> inquiryCol(String title, java.util.function.Function<Inquiry, String> mapper) {
        TableColumn<Inquiry, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return column;
    }
}
