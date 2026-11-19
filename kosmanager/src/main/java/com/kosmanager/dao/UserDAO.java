package com.kosmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kosmanager.model.User;
import com.kosmanager.util.DBConnection;
import com.kosmanager.util.PasswordUtil;

public class UserDAO {

    // method untuk login, Cek apakah username & password cocok
    public User login(String username, String password) {
        User user = null;

        String sql = "SELECT * FROM `user` WHERE username = ? AND password = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_user");
                String namaUser = rs.getString("username");
                String passwordUser = rs.getString("password");
                String role = rs.getString("role");

                user = new User(id, namaUser, passwordUser, role);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Login gagal: " + e.getMessage());
        }

        return user;
    }

    // method untuk register
    public void register(String username, String password) {
        String cekUsername = "SELECT * FROM `user` WHERE username = ?";
        String sql = "INSERT INTO `user` (username, password, role) VALUES (?, ?, 'admin')";

        try {
            Connection conn = DBConnection.getConnection();

            // mengecek apakah username sudah digunakan
            PreparedStatement psCek = conn.prepareStatement(cekUsername);
            psCek.setString(1, username);

            ResultSet rs = psCek.executeQuery();

            if (rs.next()) {
                System.out.println("Username sudah digunakan!");
            } else {
                
                // menambahkan user baru
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, username);
                ps.setString(2, PasswordUtil.hash(password));

                ps.executeUpdate();

                System.out.println("Registrasi berhasil!");
                ps.close();
            }

            rs.close();
            psCek.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Registrasi gagal: " + e.getMessage());
        }
    }
}