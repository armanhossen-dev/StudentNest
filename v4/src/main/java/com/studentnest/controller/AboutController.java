package com.studentnest.controller;

import com.studentnest.config.AppConfig;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AboutController {
    private final VBox root;

    public AboutController() {
        VBox purpose = ViewBuilder.card(
                heading("Purpose"),
                body("StudentNest is a native desktop marketplace that helps Daffodil International University students find housing around the Ashulia campus, and helps house owners list rooms without a website or third-party portal.")
        );
        VBox guide = ViewBuilder.card(
                heading("DIU campus-area guide"),
                body("Khagan sits closest to the main campus gates and is popular for hostels and shared seats. Candgaon and Charabag offer quieter flats a short ride away. Kumkumari and Dattopara are typically more affordable. Shadhupara is useful when you want a slightly longer commute in exchange for more space.")
        );
        VBox safety = ViewBuilder.card(
                heading("Accommodation safety tips"),
                body("Visit in daylight, confirm the owner identity before paying an advance, never transfer money from an unverified listing, and keep a written record of rent, utilities, and notice period. StudentNest copies phone numbers and map links; it does not process payments.")
        );
        VBox credits = ViewBuilder.card(
                heading("Team credits"),
                body("Designed and engineered for DIU students. Demo listings and demo accounts are fictional development data and are labelled as unverified until an administrator reviews them."),
                body("Version " + AppConfig.getInstance().getVersion() + "  ·  " + AppConfig.getInstance().getDisplayVersion())
        );
        root = ViewBuilder.page(
                "About StudentNest",
                "A campus-focused desktop application for student accommodation around DIU.",
                purpose, guide, safety, credits
        );
    }

    public Node getView() {
        return root;
    }

    private Label heading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("feature-title");
        return label;
    }

    private Label body(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("page-subtitle");
        return label;
    }
}
