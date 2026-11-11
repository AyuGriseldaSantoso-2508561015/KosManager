package com.kosmanager.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    // Mengubah password menjadi hash SHA-256
    public static String hash(String password) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] passwordBytes =
                    password.getBytes(StandardCharsets.UTF_8);

            byte[] hasilHash =
                    messageDigest.digest(passwordBytes);

            StringBuilder hasil = new StringBuilder();

            for (byte data : hasilHash) {
                String hex = String.format("%02x", data);
                hasil.append(hex);
            }

            return hasil.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Terjadi kesalahan saat membuat hash password.");
            return null;
        }
    }
}