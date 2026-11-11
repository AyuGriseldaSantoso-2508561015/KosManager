module com.kosmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.kosmanager to javafx.fxml;
    exports com.kosmanager;
}
