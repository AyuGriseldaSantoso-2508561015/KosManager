package com.kosmanager.util;

import java.io.IOException;
import java.util.function.Function;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public final class Navigator {

    private static Stage stage;

    private Navigator() {
    }

    // menyimpan jendela utama aplikasi
    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    // membuat loader untuk file FXML
    public static FXMLLoader loader(String namaFile) {
        String path = "/com/kosmanager/view/" + namaFile + ".fxml";
        return new FXMLLoader(Navigator.class.getResource(path));
    }

    // memuat halaman FXML
    public static Parent muat(String namaFile) {
        try {
            return loader(namaFile).load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Gagal memuat halaman " + namaFile, e);
        }
    }

    // menampilkan halaman pada jendela utama
    public static void tampilkan(String namaFile, String judul,
                                 double lebar, double tinggi) {
        Parent root = muat(namaFile);

        stage.setScene(new Scene(root, lebar, tinggi));
        stage.setTitle(judul);
        stage.centerOnScreen();
    }

    // menghubungkan kolom tabel dengan data
    @SuppressWarnings("unchecked")
    public static <T> void kolom(TableView<T> tabel, int indeks, Function<T, String> pengambilData) {

        TableColumn<T, String> kolom = (TableColumn<T, String>) tabel.getColumns().get(indeks);

        kolom.setCellValueFactory(data ->
                new SimpleStringProperty(
                        pengambilData.apply(data.getValue())
                )
        );
    }
}
