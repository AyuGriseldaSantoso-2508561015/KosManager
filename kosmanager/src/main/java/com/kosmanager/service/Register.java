package com.kosmanager.service;

import com.kosmanager.dao.UserDAO;

// proses pendaftaran akun admin baru
public class Register {
    private final UserDAO userDAO = new UserDAO();

    public void proses(String username, String password) {
        userDAO.register(username, password);
    }
}
