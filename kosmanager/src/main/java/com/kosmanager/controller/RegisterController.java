package com.kosmanager.controller;

import com.kosmanager.util.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private TextField txtRegEmail;

    @FXML
    private TextField txtRegUsername;

    @FXML
    private PasswordField txtRegPassword;

    @FXML
    private PasswordField txtRegConfirmPassword;

    @FXML
    private void handleRegister() {
        String email = txtRegEmail.getText();
        String username = txtRegUsername.getText();
        String password = txtRegPassword.getText();
        String confirmPassword = txtRegConfirmPassword.getText();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Register Gagal", "Semua kolom wajib diisi!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Register Gagal", "Konfirmasi password tidak cocok!");
            return;
        }

        String query = "INSERT INTO `user` (username, password, role) VALUES (?, ?, 'admin')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Register Berhasil", "Akun " + username + " berhasil disimpan ke database! Silakan login.");
            handleGoToLogin();

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062) {
                showAlert(Alert.AlertType.ERROR, "Register Gagal", "Username '" + username + "' sudah digunakan!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menyimpan data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kosmanager/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtRegUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Kos Manager");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke halaman Login!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}