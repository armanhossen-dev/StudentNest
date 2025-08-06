module com.studentnest.studentnest {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.studentnest to javafx.fxml;
    exports com.studentnest;
}