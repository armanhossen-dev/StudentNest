package com.studentnest.controllers;

import com.studentnest.utils.SceneManager;
import javafx.fxml.FXML;

public class AboutUsController {

    private String previousPageFxml;
    private String previousPageCss;

    /**
     * This method is called by the dashboard controllers to set the previous page
     * and its associated stylesheet for correct navigation.
     * @param fxml The FXML file name of the previous scene.
     * @param css The CSS file name of the previous scene.
     */
    public void setPreviousPage(String fxml, String css) {
        this.previousPageFxml = fxml;
        this.previousPageCss = css;
    }

    @FXML
    public void handleBack() {
        // Navigate back to the stored previous page
        if (previousPageFxml != null) {
            // Fix: Use the updated SceneManager method with the FXML, CSS, and dimensions.
            SceneManager.switchScene(previousPageFxml, previousPageCss, 1200, 700);
        } else {
            // Fallback in case previousPageFxml is not set.
            // Fix: Use the updated SceneManager method with the CSS file name.
            SceneManager.switchScene("login.fxml", "login.css", 1000, 620);
        }
    }
}