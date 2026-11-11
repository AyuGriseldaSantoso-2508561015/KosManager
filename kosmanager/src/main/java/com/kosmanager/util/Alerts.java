package com.kosmanager.util;

import javafx.scene.control.Alert;

public class Alerts {
    private Alerts() {}

    // menampilkan pesan informasi
    public static void info(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    // menampilkan pesan kesalahan
    public static void error(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Terjadi Kesalahan!");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }




}