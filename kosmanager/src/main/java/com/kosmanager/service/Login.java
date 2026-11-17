package com.kosmanager.service;

import com.kosmanager.dao.UserDAO;
import com.kosmanager.model.User;

// proses login dan logout admin. Kelas ini juga menyimpan admin yang sedang aktif 
public class Login {
    private static User userAktif; // Login "memiliki" sebuah User (composition)
    private final UserDAO userDAO = new UserDAO();

    // cek apakah username dan password cocok
    public boolean proses(String username, String password) {
        User hasil = userDAO.login(username, password);
        if (hasil == null) return false;
        userAktif = hasil;
        return true;
    }

    // keluar dari sistem, menghapus admin yang sedang aktif
    public void logout() {
        userAktif = null;
    }

    public static User getUserAktif() { 
        return userAktif; }

    // digunakan DAO untuk mencatat admin yang mengelola data
    public static int getUserId() {
        return userAktif == null ? 1 : userAktif.getIdUser();
    }
}
