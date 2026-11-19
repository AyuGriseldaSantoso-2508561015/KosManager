package com.kosmanager.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.kosmanager.model.Kamar;
import com.kosmanager.model.KamarStandard;
import com.kosmanager.model.KamarVip;
import com.kosmanager.dao.KamarDAO;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Format;
import com.kosmanager.util.Navigator;

public class KamarController {
    @FXML private TextField txtCariKamar;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private TableView<Kamar> tblKamar;
    @FXML private TextField txtNomorKamar;
    @FXML private ComboBox<String> cbTipeKamar;
    @FXML private TextField txtHargaSewa;
    @FXML private TextField txtBiayaFasilitas;
    @FXML private ComboBox<String> cbStatusKamar;
    @FXML private Button btnTambahKamar;
    @FXML private Button btnUbahKamar;
    @FXML private Button btnHapusKamar;
    @FXML private Button btnBersihkanKamar;

    private final KamarDAO dao = new KamarDAO();
    private final ObservableList<Kamar> data = FXCollections.observableArrayList();
    private FilteredList<Kamar> terfilter;

    @FXML
    private void initialize() {
        Navigator.kolom(tblKamar, 0, k -> String.valueOf(k.getIdKamar()));
        Navigator.kolom(tblKamar, 1, Kamar::getNomorKamar);
        Navigator.kolom(tblKamar, 2, Kamar::getTipe);
        Navigator.kolom(tblKamar, 3, k -> Format.rupiah(k.getHargaSewa()));
        Navigator.kolom(tblKamar, 4, Kamar::getStatus);

        terfilter = new FilteredList<>(data, k -> true);
        tblKamar.setItems(terfilter);
        cbFilterStatus.setValue("Semua");
        cbStatusKamar.setValue("Kosong");
        txtBiayaFasilitas.setDisable(true);

        txtCariKamar.textProperty().addListener((o, a, b) -> terapkanFilter());
        cbFilterStatus.valueProperty().addListener((o, a, b) -> terapkanFilter());
        cbTipeKamar.valueProperty().addListener((o, a, b) -> {
            boolean vip = "VIP".equals(b);
            txtBiayaFasilitas.setDisable(!vip);
            if (!vip) txtBiayaFasilitas.clear();
        });
        tblKamar.getSelectionModel().selectedItemProperty().addListener((o, a, k) -> isiForm(k));

        btnTambahKamar.setOnAction(e -> tambah());
        btnUbahKamar.setOnAction(e -> ubah());
        btnHapusKamar.setOnAction(e -> hapus());
        btnBersihkanKamar.setOnAction(e -> bersihkan());
        muat();
    }

    private void muat() {
        try {
            data.setAll(dao.cariSemua());
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void terapkanFilter() {
        String q = txtCariKamar.getText() == null ? "" : txtCariKamar.getText().trim().toLowerCase();
        String st = cbFilterStatus.getValue();
        terfilter.setPredicate(k ->
                (q.isEmpty() || k.getNomorKamar().toLowerCase().contains(q) || k.getTipe().toLowerCase().contains(q))
                && (st == null || st.equals("Semua") || k.getStatus().equalsIgnoreCase(st)));
    }

    private void isiForm(Kamar k) {
        if (k == null) return;
        txtNomorKamar.setText(k.getNomorKamar());
        cbTipeKamar.setValue(k.getTipe());
        txtHargaSewa.setText(String.valueOf((long) k.getHargaSewa()));
        txtBiayaFasilitas.setText(k.getBiayaFasilitas() > 0 ? String.valueOf((long) k.getBiayaFasilitas()) : "");
        cbStatusKamar.setValue(k.getStatus());
    }

    // membaca form menjadi objek Kamar, mengembalikan null bila input tidak valid
    private Kamar bacaForm(int id) {
        String nomor = txtNomorKamar.getText().trim();
        String tipe = cbTipeKamar.getValue();
        String status = cbStatusKamar.getValue();
        if (nomor.isEmpty() || tipe == null || status == null || txtHargaSewa.getText().isBlank()) {
            Alerts.error("Nomor kamar, tipe, harga sewa, dan status wajib diisi");
            return null;
        }
        try {
            double harga = Double.parseDouble(txtHargaSewa.getText().trim());
            double fasilitas = txtBiayaFasilitas.getText().isBlank() ? 0 : Double.parseDouble(txtBiayaFasilitas.getText().trim());
            return buatKamar(id, nomor, tipe, harga, status, fasilitas);
        } catch (NumberFormatException ex) {
            Alerts.error("Harga sewa dan biaya fasilitas harus berupa angka");
        } catch (IllegalArgumentException ex) {
            Alerts.error(ex.getMessage());
        }
        return null;
    }

    private void tambah() {
        Kamar k = bacaForm(0);
        if (k == null) return;
        try {
            dao.tambah(k);
            muat();
            bersihkan();
            Alerts.info("Data kamar berhasil ditambahkan");
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void ubah() {
        Kamar dipilih = tblKamar.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Alerts.error("Pilih kamar pada tabel terlebih dahulu");
            return;
        }
        Kamar k = bacaForm(dipilih.getIdKamar());
        if (k == null) return;
        try {
            dao.ubah(k);
            muat();
            Alerts.info("Data kamar berhasil diubah");
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void hapus() {
        Kamar dipilih = tblKamar.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Alerts.error("Pilih kamar pada tabel terlebih dahulu");
            return;
        }
        if (!Alerts.konfirmasi("Hapus kamar " + dipilih.getNomorKamar() + "?")) return;
        try {
            dao.hapus(dipilih.getIdKamar());
            muat();
            bersihkan();
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void bersihkan() {
        tblKamar.getSelectionModel().clearSelection();
        txtNomorKamar.clear();
        cbTipeKamar.setValue(null);
        txtHargaSewa.clear();
        txtBiayaFasilitas.clear();
        cbStatusKamar.setValue("Kosong");
    }

    // membuat KamarStandard atau KamarVip berdasarkan nilai tipe
    private Kamar buatKamar(int id, String nomor, String tipe, double harga, String status, double fasilitas) {
        if ("VIP".equalsIgnoreCase(tipe)) {
            return new KamarVip(id, nomor, harga, status, fasilitas);
        }
        return new KamarStandard(id, nomor, harga, status);
    }
}

