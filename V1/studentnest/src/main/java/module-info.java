module studentnest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Export packages that contain classes used by FXML
    exports com.studentnest;
    exports com.studentnest.controllers;
    exports com.studentnest.models;
    exports com.studentnest.database;
    exports com.studentnest.utils;

    // Open packages for FXML reflection
    opens com.studentnest to javafx.fxml;
    opens com.studentnest.controllers to javafx.fxml;
    opens com.studentnest.models to javafx.fxml;
}