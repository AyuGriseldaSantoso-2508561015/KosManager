package com.kosmanager.dao;

import com.kosmanager.model.Penyewa;
import com.kosmanager.util.DBConnection;
import com.kosmanager.util.DatabaseException;
import com.kosmanager.service.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PenyewaDAO implements BaseDAO<Penyewa> {

    @Override
    public void tambah(Penyewa p) {
        String sql = "INSERT INTO penyewa(id_user, nama, no_wa, no_ktp) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Login.getAdminId());
            ps.setString(2, p.getNama());
            ps.setString(3, p.getNoWa());
            ps.setString(4, p.getNoKtp());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menambah penyewa: " + e.getMessage(), e);
        }
    }

    @Override
    public void ubah(Penyewa p) {
        String sql = "UPDATE penyewa SET nama=?, no_wa=?, no_ktp=? WHERE id_penyewa=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoWa());
            ps.setString(3, p.getNoKtp());
            ps.setInt(4, p.getIdPenyewa());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengubah penyewa: " + e.getMessage(), e);
        }
    }

    @Override
    public void hapus(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM penyewa WHERE id_penyewa=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus penyewa (penyewa mungkin sudah punya transaksi)", e);
        }
    }

   @Override
    public Penyewa cariById(int id) {
        String sql = "SELECT id_penyewa, nama, no_wa, no_ktp FROM penyewa WHERE id_penyewa=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Penyewa(rs.getInt("id_penyewa"), rs.getString("nama"),
                            rs.getString("no_wa"), rs.getString("no_ktp"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari penyewa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Penyewa> ambilSemua() {
        List<Penyewa> hasil = new ArrayList<>();
        String sql = "SELECT id_penyewa, nama, no_wa, no_ktp FROM penyewa ORDER BY nama";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Penyewa(rs.getInt("id_penyewa"), rs.getString("nama"),
                        rs.getString("no_wa"), rs.getString("no_ktp")));
            }
            return hasil;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal membaca data penyewa: " + e.getMessage(), e);
        }
    }

    public List<Penyewa> cariSemua() {
        return ambilSemua();
    }
}

