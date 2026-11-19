package com.kosmanager.controller;

import java.util.List;

import com.kosmanager.model.User;
import com.kosmanager.service.Login;
import com.kosmanager.util.Alerts;
import com.kosmanager.util.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {
    private static final String AKTIF =
            "-fx-background-color: #2F3E63; -fx-text-fill: white; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";
    private static final String BIASA =
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;";

    @FXML private Button btnDashboard;
    @FXML private Button btnKelolaKamar;
    @FXML private Button btnKelolaPenyewa;
    @FXML private Button btnTransaksiSewa;
    @FXML private Button btnPembayaran;
    @FXML private Button btnLogout;
    @FXML private Label lblHalaman;
    @FXML private Label lblUser;
    @FXML private StackPane contentArea;

    @FXML
    private void initialize() {
        User user = Login.getUserAktif();
        if (user != null) {
            lblUser.setText("Halo, " + user.getUsername());
        }
        btnDashboard.setOnAction(e -> buka("DashboardView", "Dashboard", btnDashboard));
        btnKelolaKamar.setOnAction(e -> buka("KamarView", "Kelola Kamar", btnKelolaKamar));
        btnKelolaPenyewa.setOnAction(e -> buka("PenyewaView", "Kelola Penyewa", btnKelolaPenyewa));
        btnTransaksiSewa.setOnAction(e -> buka("TransaksiView", "Transaksi Sewa", btnTransaksiSewa));
        btnPembayaran.setOnAction(e -> buka("PembayaranView", "Pembayaran & Riwayat", btnPembayaran));
        btnLogout.setOnAction(e -> logout());
        buka("DashboardView", "Dashboard", btnDashboard);
    }

    private void buka(String view, String judul, Button aktif) {
        try {
            contentArea.getChildren().setAll(Navigator.muat(view));
            lblHalaman.setText(judul);
            List<Button> semua = List.of(btnDashboard, btnKelolaKamar, btnKelolaPenyewa, btnTransaksiSewa, btnPembayaran);
            for (Button b : semua) b.setStyle(b == aktif ? AKTIF : BIASA);
        } catch (RuntimeException ex) {
            Alerts.error(ex.getMessage());
        }
    }

    private void logout() {
        if (Alerts.konfirmasi("Yakin ingin keluar?")) {
            new Login().logout();
            Navigator.tampilkan("LoginView", "KosManager - Login", 900, 600);
        }
    }
}
