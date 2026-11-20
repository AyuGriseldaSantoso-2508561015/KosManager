module com.kosmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.kosmanager to javafx.fxml;
    opens com.kosmanager.controller to javafx.fxml;
    opens com.kosmanager.model to javafx.base, javafx.fxml;

    exports com.kosmanager;
    exports com.kosmanager.controller;
    exports com.kosmanager.dao;
    exports com.kosmanager.model;
    exports com.kosmanager.service;
    exports com.kosmanager.util;
}