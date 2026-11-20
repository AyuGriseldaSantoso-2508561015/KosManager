package com.kosmanager.controller;

import com.kosmanager.dao.KamarDAO;
import com.kosmanager.model.Kamar;
import com.kosmanager.model.KamarStandard;
import com.kosmanager.model.KamarVip;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Format;
import com.kosmanager.util.Navigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

    private final ObservableList<Kamar> data =
            FXCollections.observableArrayList();

    private FilteredList<Kamar> terfilter;

    @FXML
    private void initialize() {

        // Pilihan tipe kamar
        cbTipeKamar.setItems(FXCollections.observableArrayList(
                "Standard",
                "VIP"
        ));
        cbTipeKamar.setPromptText("Pilih Tipe");

        // Pilihan filter status
        cbFilterStatus.setItems(FXCollections.observableArrayList(
                "Semua",
                "Kosong",
                "Terisi"
        ));
        cbFilterStatus.setValue("Semua");

        // Pilihan status kamar
        cbStatusKamar.setItems(FXCollections.observableArrayList(
                "Kosong",
                "Terisi"
        ));
        cbStatusKamar.setPromptText("Pilih Status");
        cbStatusKamar.setValue(null);

        // Mengatur kolom tabel
        Navigator.kolom(tblKamar, 0,
                k -> String.valueOf(k.getIdKamar()));

        Navigator.kolom(tblKamar, 1,
                Kamar::getNomorKamar);

        Navigator.kolom(tblKamar, 2,
                Kamar::getTipe);

        Navigator.kolom(tblKamar, 3,
                k -> Format.rupiah(k.getHargaSewa()));

        Navigator.kolom(tblKamar, 4,
                Kamar::getStatus);

        // Mengatur data tabel dan filter
        terfilter = new FilteredList<>(data, k -> true);
        tblKamar.setItems(terfilter);

        // Biaya fasilitas hanya untuk kamar VIP
        txtBiayaFasilitas.setDisable(true);

        txtCariKamar.textProperty().addListener(
                (o, a, b) -> terapkanFilter()
        );

        cbFilterStatus.valueProperty().addListener(
                (o, a, b) -> terapkanFilter()
        );

        cbTipeKamar.valueProperty().addListener(
                (o, a, b) -> {
                    boolean vip = "VIP".equals(b);

                    txtBiayaFasilitas.setDisable(!vip);

                    if (!vip) {
                        txtBiayaFasilitas.clear();
                    }
                }
        );

        // Memilih data kamar dari tabel
        tblKamar.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, k) -> isiForm(k));

        // Mengatur tombol
        btnTambahKamar.setOnAction(e -> tambah());
        btnUbahKamar.setOnAction(e -> ubah());
        btnHapusKamar.setOnAction(e -> hapus());
        btnBersihkanKamar.setOnAction(e -> bersihkan());

        // Memuat data kamar
        muat();
    }

    // Memuat seluruh data kamar
    private void muat() {
        try {
            data.setAll(dao.cariSemua());
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    // Menerapkan pencarian dan filter
    private void terapkanFilter() {

        String q = txtCariKamar.getText() == null
                ? ""
                : txtCariKamar.getText().trim().toLowerCase();

        String st = cbFilterStatus.getValue();

        terfilter.setPredicate(k ->
                (q.isEmpty()
                        || k.getNomorKamar().toLowerCase().contains(q)
                        || k.getTipe().toLowerCase().contains(q))
                &&
                (st == null
                        || st.equals("Semua")
                        || k.getStatus().equalsIgnoreCase(st))
        );
    }

    // Mengisi form berdasarkan kamar yang dipilih
    private void isiForm(Kamar k) {

        if (k == null) {
            return;
        }

        txtNomorKamar.setText(k.getNomorKamar());
        cbTipeKamar.setValue(k.getTipe());

        txtHargaSewa.setText(
                String.valueOf((long) k.getHargaSewa())
        );

        txtBiayaFasilitas.setText(
                k.getBiayaFasilitas() > 0
                        ? String.valueOf((long) k.getBiayaFasilitas())
                        : ""
        );

        cbStatusKamar.setValue(k.getStatus());
    }

    // Membaca data dari form
    private Kamar bacaForm(int id) {

        String nomor = txtNomorKamar.getText().trim();
        String tipe = cbTipeKamar.getValue();
        String status = cbStatusKamar.getValue();

        if (nomor.isEmpty()
                || tipe == null
                || status == null
                || txtHargaSewa.getText().isBlank()) {

            Alerts.error(
                    "Nomor kamar, tipe, harga sewa, dan status wajib diisi"
            );

            return null;
        }

        try {

            double harga = Double.parseDouble(
                    txtHargaSewa.getText().trim()
            );

            double fasilitas = txtBiayaFasilitas.getText().isBlank()
                    ? 0
                    : Double.parseDouble(
                            txtBiayaFasilitas.getText().trim()
                    );

            return buatKamar(
                    id, nomor, tipe, harga, status, fasilitas
            );

        } catch (NumberFormatException ex) {

            Alerts.error(
                    "Harga sewa dan biaya fasilitas harus berupa angka"
            );

        } catch (IllegalArgumentException ex) {

            Alerts.error(ex.getMessage());
        }

        return null;
    }

    // Menambahkan kamar
    private void tambah() {

        Kamar k = bacaForm(0);

        if (k == null) {
            return;
        }

        try {

            dao.tambah(k);
            muat();
            bersihkan();

            Alerts.info("Data kamar berhasil ditambahkan");

        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    // Mengubah data kamar
    private void ubah() {

        Kamar dipilih = tblKamar.getSelectionModel()
                .getSelectedItem();

        if (dipilih == null) {
            Alerts.error("Pilih kamar pada tabel terlebih dahulu");
            return;
        }

        Kamar k = bacaForm(dipilih.getIdKamar());

        if (k == null) {
            return;
        }

        try {

            dao.ubah(k);
            muat();

            Alerts.info("Data kamar berhasil diubah");

        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    // Menghapus kamar
    private void hapus() {

        Kamar dipilih = tblKamar.getSelectionModel()
                .getSelectedItem();

        if (dipilih == null) {
            Alerts.error("Pilih kamar pada tabel terlebih dahulu");
            return;
        }

        if (!Alerts.konfirmasi(
                "Hapus kamar " + dipilih.getNomorKamar() + "?"
        )) {
            return;
        }

        try {

            dao.hapus(dipilih.getIdKamar());
            muat();
            bersihkan();

        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    // Membersihkan form
    private void bersihkan() {

        tblKamar.getSelectionModel().clearSelection();

        txtNomorKamar.clear();
        cbTipeKamar.setValue(null);
        txtHargaSewa.clear();
        txtBiayaFasilitas.clear();

        cbStatusKamar.setValue(null);
        cbStatusKamar.setPromptText("Pilih Status");

        txtBiayaFasilitas.setDisable(true);
    }

    // Membuat objek kamar berdasarkan tipe
    private Kamar buatKamar(
            int id,
            String nomor,
            String tipe,
            double harga,
            String status,
            double fasilitas
    ) {

        if ("VIP".equalsIgnoreCase(tipe)) {
            return new KamarVip(
                    id, nomor, harga, status, fasilitas
            );
        }

        return new KamarStandard(
                id, nomor, harga, status
        );
    }
}
