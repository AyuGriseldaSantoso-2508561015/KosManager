package com.kosmanager.controller;

import java.io.IOException;

import com.kosmanager.service.Register;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField txtRegUsername;

    @FXML
    private PasswordField txtRegPassword;

    @FXML
    private PasswordField txtRegConfirmPassword;

    private final Register register = new Register();

    @FXML
    private void handleRegister() {

        String username = txtRegUsername.getText().trim();
        String password = txtRegPassword.getText();
        String confirmPassword = txtRegConfirmPassword.getText();

        // validasi kolom kosong
        if (username.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Register Gagal",
                    "Semua kolom wajib diisi!");
            return;
        }

        // validasi konfirmasi password
        if (!password.equals(confirmPassword)) {

            showAlert(Alert.AlertType.ERROR, "Register Gagal",
                    "Konfirmasi password tidak cocok!");
            return;
        }

        // proses registrasi melalui Register.java
        boolean berhasil = register.proses(username, password);

        if (berhasil) {

            showAlert(Alert.AlertType.INFORMATION, "Register Berhasil",
                    "Akun " + username
                    + " berhasil disimpan! Silakan login.");

            handleGoToLogin();

        } else {

            showAlert(Alert.AlertType.ERROR, "Register Gagal",
                    "Username sudah digunakan atau terjadi kesalahan database!");
        }
    }

    @FXML
    private void handleGoToLogin() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/kosmanager/LoginView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) txtRegUsername.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Login Kos Manager");

        } catch (IOException e) {

            e.printStackTrace();

            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal kembali ke halaman Login!");
        }
    }

    private void showAlert(Alert.AlertType type,
                           String title, String header) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
