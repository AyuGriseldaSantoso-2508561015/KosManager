package com.kosmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kosmanager.model.Kamar;
import com.kosmanager.model.KamarStandard;
import com.kosmanager.model.KamarVip;
import com.kosmanager.service.Login;
import com.kosmanager.util.DBConnection;
import com.kosmanager.util.DatabaseException;

public class KamarDAO implements BaseDAO<Kamar> {

    @Override
    public void tambah(Kamar kamar) {
        String sql = "INSERT INTO kamar (id_user, nomor_kamar, tipe, harga_sewa, biaya_fasilitas, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Login.getUserId());
            ps.setString(2, kamar.getNomorKamar());
            ps.setString(3, kamar.getTipe());
            ps.setDouble(4, kamar.getHargaSewa());
            ps.setDouble(5, kamar.getBiayaFasilitas());
            ps.setString(6, kamar.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menambahkan data kamar.", e);
        }
    }

    @Override
    public void ubah(Kamar kamar) {
        String sql = "UPDATE kamar SET nomor_kamar = ?, tipe = ?, harga_sewa = ?, "
                   + "biaya_fasilitas = ?, status = ? WHERE id_kamar = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kamar.getNomorKamar());
            ps.setString(2, kamar.getTipe());
            ps.setDouble(3, kamar.getHargaSewa());
            ps.setDouble(4, kamar.getBiayaFasilitas());
            ps.setString(5, kamar.getStatus());
            ps.setInt(6, kamar.getIdKamar());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengubah data kamar.", e);
        }
    }

    @Override
    public void hapus(int idKamar) {
        String sql = "DELETE FROM kamar WHERE id_kamar = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idKamar);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus data kamar. Pastikan kamar tidak sedang dipakai di transaksi.", e);
        }
    }

    @Override
    public Kamar cariById(int idKamar) {
        String sql = "SELECT * FROM kamar WHERE id_kamar = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idKamar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari data kamar.", e);
        }
        return null;
    }

    @Override
    public List<Kamar> ambilSemua() {
        List<Kamar> daftarKamar = new ArrayList<>();
        String sql = "SELECT * FROM kamar ORDER BY nomor_kamar ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftarKamar.add(mapResultSet(rs));
            }
       } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Gagal mengambil daftar kamar: " + e.getMessage(), e);
        }
        return daftarKamar;
    }

    public List<Kamar> ambilKamarKosong() {
        List<Kamar> daftarKamar = new ArrayList<>();
        String sql = "SELECT * FROM kamar WHERE status = 'Kosong' ORDER BY nomor_kamar ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftarKamar.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil daftar kamar kosong.", e);
        }
        return daftarKamar;
    }

    public List<Kamar> cariSemua() {
        return ambilSemua();
    }

    public List<Kamar> cariKosong() {
        return ambilKamarKosong();
    }

    public int hitungByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM kamar WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghitung status kamar.", e);
        }
        return 0;
    }

    public void ubahStatus(int idKamar, String statusBaru) {
        String sql = "UPDATE kamar SET status = ? WHERE id_kamar = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusBaru);
            ps.setInt(2, idKamar);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengubah status kamar.", e);
        }
    }

    private Kamar mapResultSet(ResultSet rs) throws SQLException {
        int idKamar = rs.getInt("id_kamar");
        int idUser = rs.getInt("id_user");
        String nomorKamar = rs.getString("nomor_kamar");
        String tipe = rs.getString("tipe");
        double hargaSewa = rs.getDouble("harga_sewa");
        double biayaFasilitas = rs.getDouble("biaya_fasilitas");
        String status = rs.getString("status");

        if ("VIP".equalsIgnoreCase(tipe)) {
            return new KamarVip(idKamar, idUser, nomorKamar, hargaSewa, biayaFasilitas, status);
        } else {
            return new KamarStandard(idKamar, idUser, nomorKamar, hargaSewa, status);
        }
    }
}