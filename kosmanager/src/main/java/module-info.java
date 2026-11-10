module com.kosmanager {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.kosmanager to javafx.fxml;
    exports com.kosmanager;
}
