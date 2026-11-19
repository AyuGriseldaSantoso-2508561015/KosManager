package com.kosmanager.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.kosmanager.model.Kamar;
import com.kosmanager.model.Penyewa;
import com.kosmanager.model.TransaksiSewa;
import com.kosmanager.dao.KamarDAO;
import com.kosmanager.dao.PenyewaDAO;
import com.kosmanager.dao.TransaksiDAO;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Format;
import com.kosmanager.util.Navigator;

import java.time.LocalDate;

public class TransaksiController {
    @FXML private ComboBox<Penyewa> cbPenyewa;
    @FXML private ComboBox<Kamar> cbKamarKosong;
    @FXML private Label lblTipeKamar;
    @FXML private Label lblHargaKamar;
    @FXML private TextField txtDurasi;
    @FXML private DatePicker dpTanggal;
    @FXML private Label lblTotalBiaya;
    @FXML private Button btnSimpanTransaksi;
    @FXML private Button btnBatalTransaksi;
    @FXML private TableView<TransaksiSewa> tblTransaksi;

    private final PenyewaDAO penyewaDAO = new PenyewaDAO();
    private final KamarDAO kamarDAO = new KamarDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    @FXML
    private void initialize() {
        Navigator.kolom(tblTransaksi, 0, t -> String.valueOf(t.getIdTransaksi()));
        Navigator.kolom(tblTransaksi, 1, TransaksiSewa::getNamaPenyewa);
        Navigator.kolom(tblTransaksi, 2, TransaksiSewa::getNomorKamar);
        Navigator.kolom(tblTransaksi, 3, t -> t.getDurasiSewa() + " bln");
        Navigator.kolom(tblTransaksi, 4, t -> Format.rupiah(t.getTotalBiaya()));
        Navigator.kolom(tblTransaksi, 5, TransaksiSewa::getStatusPembayaran);
        Navigator.kolom(tblTransaksi, 6, t -> t.getTanggalTransaksi().toString());

        dpTanggal.setValue(LocalDate.now());
        cbKamarKosong.valueProperty().addListener((o, a, b) -> hitung());
        txtDurasi.textProperty().addListener((o, a, b) -> hitung());
        btnSimpanTransaksi.setOnAction(e -> simpan());
        btnBatalTransaksi.setOnAction(e -> bersihkan());
        muat();
    }

    private void muat() {
        try {
            cbPenyewa.setItems(FXCollections.observableArrayList(penyewaDAO.cariSemua()));
            cbKamarKosong.setItems(FXCollections.observableArrayList(kamarDAO.cariKosong()));
            tblTransaksi.setItems(FXCollections.observableArrayList(transaksiDAO.cariSemua()));
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private int bacaDurasi() {
        try {
            return Integer.parseInt(txtDurasi.getText().trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /** Total dihitung lewat objek Kamar: Standard dan VIP menghasilkan rumus berbeda (polimorfisme). */
    private void hitung() {
        Kamar k = cbKamarKosong.getValue();
        if (k == null) {
            lblTipeKamar.setText("Tipe: -");
            lblHargaKamar.setText("Harga: -");
            lblTotalBiaya.setText("Rp0");
            return;
        }
        lblTipeKamar.setText("Tipe: " + k.getTipe());
        lblHargaKamar.setText("Harga: " + Format.rupiah(k.getHargaSewa()));
        int durasi = bacaDurasi();
        lblTotalBiaya.setText(durasi > 0 ? Format.rupiah(k.hitungTotalBiaya(durasi)) : "Rp0");
    }

    private void simpan() {
        Penyewa p = cbPenyewa.getValue();
        Kamar k = cbKamarKosong.getValue();
        int durasi = bacaDurasi();
        if (p == null || k == null) {
            Alerts.error("Pilih penyewa dan kamar terlebih dahulu");
            return;
        }
        if (durasi <= 0) {
            Alerts.error("Durasi sewa harus berupa angka lebih dari 0");
            return;
        }
        if (dpTanggal.getValue() == null) {
            Alerts.error("Tanggal transaksi wajib diisi");
            return;
        }
        try {
            TransaksiSewa t = new TransaksiSewa();
            t.setIdPenyewa(p.getIdPenyewa());
            t.setIdKamar(k.getIdKamar());
            t.setDurasiSewa(durasi);
            t.setTanggalTransaksi(dpTanggal.getValue());
            t.hitungTotal(k);
            t.simpan();
            Alerts.info("Transaksi berhasil disimpan.\nTotal biaya: " + Format.rupiah(t.getTotalBiaya())
                    + "\nKamar " + k.getNomorKamar() + " kini berstatus Terisi.\nCatat pembayarannya di menu Pembayaran.");
            bersihkan();
            muat();
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void bersihkan() {
        cbPenyewa.setValue(null);
        cbKamarKosong.setValue(null);
        txtDurasi.clear();
        dpTanggal.setValue(LocalDate.now());
        hitung();
    }
}
