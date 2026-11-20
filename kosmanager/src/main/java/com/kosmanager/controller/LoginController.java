package com.kosmanager.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kosmanager.util.DBConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Gagal",
                    "Username dan Password tidak boleh kosong!");
            return;
        }

        String query = "SELECT * FROM `user` WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                        "Selamat datang, " + username + " (" + role + ")!");

                try {
                    // Membuka Main Layout
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/kosmanager/MainLayout.fxml")
                    );

                    Parent root = loader.load();

                    Stage stage = (Stage) txtUsername.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("KosManager");
                    stage.centerOnScreen();

                } catch (Exception e) {
                    e.printStackTrace();

                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Gagal membuka Main Layout: " + e.getMessage());
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal",
                        "Username atau Password salah!");
            }

        } catch (SQLException e) {
            e.printStackTrace();

            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Gagal terhubung ke database: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/kosmanager/RegisterView.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register Kos Manager");

        } catch (Exception e) {
            e.printStackTrace();

            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal membuka halaman Register: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
