package com.kosmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kosmanager.model.User;
import com.kosmanager.util.DBConnection;
import com.kosmanager.util.PasswordUtil;

public class UserDAO {

    // method untuk login
    // mengecek kecocokan username dan password
    public User login(String username, String password) {

        User user = null;

        String sql = "SELECT * FROM `user` WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Password di-hash sebelum dicocokkan
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int id = rs.getInt("id_user");
                    String namaUser = rs.getString("username");
                    String passwordUser = rs.getString("password");
                    String role = rs.getString("role");

                    user = new User(
                            id,
                            namaUser,
                            passwordUser,
                            role
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login gagal: " + e.getMessage());
        }

        return user;
    }

    // Method untuk register
    public boolean register(String username, String password) {

        String sql = "INSERT INTO `user` (username, password, role) VALUES (?, ?, 'admin')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Menyimpan username
            ps.setString(1, username);

            // Mengubah password menjadi hash SHA-256
            ps.setString(2, PasswordUtil.hash(password));

            // Menyimpan akun ke database
            ps.executeUpdate();

            System.out.println("Registrasi berhasil!");

            return true;

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                System.out.println("Username sudah digunakan!");
            } else {
                System.out.println("Registrasi gagal: " + e.getMessage());
            }

            return false;
        }
    }
}
