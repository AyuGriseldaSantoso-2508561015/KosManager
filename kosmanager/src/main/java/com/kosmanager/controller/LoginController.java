package com.kosmanager.controller;

import com.kosmanager.service.Login;

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

    private final Login login = new Login();

    @FXML
    private void handleLogin() {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validasi kolom kosong
        if (username.isEmpty() || password.isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Login Gagal",
                    "Username dan Password tidak boleh kosong!");
            return;
        }

        // Proses login melalui Login.java dan UserDAO.java
        boolean berhasil = login.proses(username, password);

        if (berhasil) {

            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                    "Selamat datang, " + username + "!");

            try {

                // Membuka Main Layout
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/com/kosmanager/MainLayout.fxml")
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
    }

    @FXML
    private void handleGoToRegister() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/kosmanager/RegisterView.fxml")
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

    private void showAlert(Alert.AlertType type,
                           String title, String header) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
