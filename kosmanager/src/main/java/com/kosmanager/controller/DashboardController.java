package com.kosmanager.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import com.kosmanager.model.TransaksiSewa;
import com.kosmanager.dao.KamarDAO;
import com.kosmanager.dao.PenyewaDAO;
import com.kosmanager.dao.TransaksiDAO;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Format;
import com.kosmanager.util.Navigator;

public class DashboardController {
    @FXML private Label lblKamarKosong;
    @FXML private Label lblKamarTerisi;
    @FXML private Label lblTotalPenyewa;
    @FXML private Label lblBelumLunas;
    @FXML private TableView<TransaksiSewa> tblTransaksiTerbaru;

    @FXML
    private void initialize() {
        Navigator.kolom(tblTransaksiTerbaru, 0, t -> String.valueOf(t.getIdTransaksi()));
        Navigator.kolom(tblTransaksiTerbaru, 1, TransaksiSewa::getNamaPenyewa);
        Navigator.kolom(tblTransaksiTerbaru, 2, TransaksiSewa::getNomorKamar);
        Navigator.kolom(tblTransaksiTerbaru, 3, t -> String.valueOf(t.getDurasiSewa()));
        Navigator.kolom(tblTransaksiTerbaru, 4, t -> Format.rupiah(t.getTotalBiaya()));
        Navigator.kolom(tblTransaksiTerbaru, 5, TransaksiSewa::getStatusPembayaran);
        Navigator.kolom(tblTransaksiTerbaru, 6, t -> t.getTanggalTransaksi().toString());
        muat();
    }

    private void muat() {
        try {
            KamarDAO kamarDAO = new KamarDAO();
            TransaksiDAO transaksiDAO = new TransaksiDAO();
            lblKamarKosong.setText(String.valueOf(kamarDAO.hitungByStatus("Kosong")));
            lblKamarTerisi.setText(String.valueOf(kamarDAO.hitungByStatus("Terisi")));
            lblTotalPenyewa.setText(String.valueOf(new PenyewaDAO().cariSemua().size()));
            lblBelumLunas.setText(String.valueOf(transaksiDAO.hitungBelumLunas()));
            tblTransaksiTerbaru.setItems(FXCollections.observableArrayList(transaksiDAO.cariTerbaru(10)));
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }
}
