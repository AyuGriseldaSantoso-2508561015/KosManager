package com.kosmanager.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.kosmanager.model.Penyewa;
import com.kosmanager.dao.PenyewaDAO;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Navigator;

public class PenyewaController {
    @FXML private TextField txtCariPenyewa;
    @FXML private TableView<Penyewa> tblPenyewa;
    @FXML private TextField txtNama;
    @FXML private TextField txtNoWa;
    @FXML private TextField txtNoKtp;
    @FXML private Button btnTambahPenyewa;
    @FXML private Button btnUbahPenyewa;
    @FXML private Button btnHapusPenyewa;
    @FXML private Button btnBersihPenyewa;

    private final PenyewaDAO dao = new PenyewaDAO();
    private final ObservableList<Penyewa> data = FXCollections.observableArrayList();
    private FilteredList<Penyewa> terfilter;

    @FXML
    private void initialize() {
        Navigator.kolom(tblPenyewa, 0, p -> String.valueOf(p.getIdPenyewa()));
        Navigator.kolom(tblPenyewa, 1, Penyewa::getNama);
        Navigator.kolom(tblPenyewa, 2, Penyewa::getNoWa);
        Navigator.kolom(tblPenyewa, 3, Penyewa::getNoKtp);

        terfilter = new FilteredList<>(data, p -> true);
        tblPenyewa.setItems(terfilter);
        txtCariPenyewa.textProperty().addListener((o, a, b) -> {
            String q = b == null ? "" : b.trim().toLowerCase();
            terfilter.setPredicate(p -> q.isEmpty() || p.getNama().toLowerCase().contains(q) || p.getNoWa().contains(q));
        });
        tblPenyewa.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            if (p == null) return;
            txtNama.setText(p.getNama());
            txtNoWa.setText(p.getNoWa());
            txtNoKtp.setText(p.getNoKtp());
        });

        btnTambahPenyewa.setOnAction(e -> tambah());
        btnUbahPenyewa.setOnAction(e -> ubah());
        btnHapusPenyewa.setOnAction(e -> hapus());
        btnBersihPenyewa.setOnAction(e -> bersihkan());
        muat();
    }

    private void muat() {
        try {
            data.setAll(dao.cariSemua());
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private Penyewa bacaForm(int id) {
        String nama = txtNama.getText().trim();
        String wa = txtNoWa.getText().trim();
        String ktp = txtNoKtp.getText().trim();
        if (nama.isEmpty() || wa.isEmpty() || ktp.isEmpty()) {
            Alerts.error("Nama, nomor WhatsApp, dan nomor KTP wajib diisi");
            return null;
        }
        if (!wa.matches("\\d{9,15}")) {
            Alerts.error("Nomor WhatsApp harus berupa angka (9-15 digit)");
            return null;
        }
        if (!ktp.matches("\\d{16}")) {
            Alerts.error("Nomor KTP harus 16 digit angka");
            return null;
        }
        return new Penyewa(id, nama, wa, ktp);
    }

    private void tambah() {
        Penyewa p = bacaForm(0);
        if (p == null) return;
        try {
            dao.tambah(p);
            muat();
            bersihkan();
            Alerts.info("Data penyewa berhasil ditambahkan");
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void ubah() {
        Penyewa dipilih = tblPenyewa.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Alerts.error("Pilih penyewa pada tabel terlebih dahulu");
            return;
        }
        Penyewa p = bacaForm(dipilih.getIdPenyewa());
        if (p == null) return;
        try {
            dao.ubah(p);
            muat();
            Alerts.info("Data penyewa berhasil diubah");
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void hapus() {
        Penyewa dipilih = tblPenyewa.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Alerts.error("Pilih penyewa pada tabel terlebih dahulu");
            return;
        }
        if (!Alerts.konfirmasi("Hapus penyewa " + dipilih.getNama() + "?")) return;
        try {
            dao.hapus(dipilih.getIdPenyewa());
            muat();
            bersihkan();
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void bersihkan() {
        tblPenyewa.getSelectionModel().clearSelection();
        txtNama.clear();
        txtNoWa.clear();
        txtNoKtp.clear();
    }
}
