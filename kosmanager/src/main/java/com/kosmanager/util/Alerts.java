package com.kosmanager.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alerts {
    private Alerts() { }

    public static void info(String pesan) { tampil(Alert.AlertType.INFORMATION, "Informasi", pesan); }
    public static void error(String pesan) { tampil(Alert.AlertType.ERROR, "Terjadi Kesalahan", pesan); }

    public static boolean konfirmasi(String pesan) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        a.setTitle("Konfirmasi");
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private static void tampil(Alert.AlertType tipe, String judul, String pesan) {
        Alert a = new Alert(tipe, pesan, ButtonType.OK);
        a.setTitle(judul);
        a.setHeaderText(null);
        a.showAndWait();
    }
}