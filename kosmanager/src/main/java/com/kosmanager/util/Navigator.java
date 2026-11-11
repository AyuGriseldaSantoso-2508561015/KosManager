package com.kosmanager.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Function;

public final class Navigator {

    private static Stage stage;

    private Navigator() {
    }

    // Menyimpan window utama aplikasi
    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    // Membuat loader untuk file FXML
    public static FXMLLoader loader(String namaFile) {
        String path = "/com/kosmanager/view/" + namaFile + ".fxml";
        return new FXMLLoader(Navigator.class.getResource(path));
    }

    // Memuat halaman FXML
    public static Parent muat(String namaFile) {
        try {
            return loader(namaFile).load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Gagal memuat halaman " + namaFile, e);
        }
    }

    // Menampilkan halaman pada window utama
    public static void tampilkan(String namaFile, String judul,
                                 double lebar, double tinggi) {
        Parent root = muat(namaFile);

        stage.setScene(new Scene(root, lebar, tinggi));
        stage.setTitle(judul);
        stage.centerOnScreen();
    }

    // Menghubungkan kolom tabel dengan data
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
