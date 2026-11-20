package com.kosmanager.dao;

import com.kosmanager.model.TransaksiSewa;
import com.kosmanager.util.DBConnection;
import com.kosmanager.util.DatabaseException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO implements BaseDAO<TransaksiSewa> {
    private static final String SELECT =
            "SELECT t.id_transaksi, t.id_penyewa, t.id_kamar, t.durasi_sewa, t.total_biaya, "
          + "t.status_pembayaran, t.tanggal_transaksi, p.nama, k.nomor_kamar "
          + "FROM transaksi_sewa t JOIN penyewa p ON p.id_penyewa = t.id_penyewa "
          + "JOIN kamar k ON k.id_kamar = t.id_kamar ";

    /** Menyimpan transaksi dan mengubah status kamar menjadi Terisi dalam satu transaksi database. */
    @Override
    public void tambah(TransaksiSewa t) {
        String insert = "INSERT INTO transaksi_sewa(id_penyewa, id_kamar, durasi_sewa, total_biaya, status_pembayaran, tanggal_transaksi) "
                      + "VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, t.getIdPenyewa());
                    ps.setInt(2, t.getIdKamar());
                    ps.setInt(3, t.getDurasiSewa());
                    ps.setDouble(4, t.getTotalBiaya());
                    ps.setString(5, t.getStatusPembayaran());
                    ps.setDate(6, Date.valueOf(t.getTanggalTransaksi()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) t.setIdTransaksi(keys.getInt(1));
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE kamar SET status = 'Terisi' WHERE id_kamar = ?")) {
                    ps.setInt(1, t.getIdKamar());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public void ubah(TransaksiSewa t) {
        String sql = "UPDATE transaksi_sewa SET durasi_sewa=?, total_biaya=?, status_pembayaran=?, tanggal_transaksi=? WHERE id_transaksi=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, t.getDurasiSewa());
            ps.setDouble(2, t.getTotalBiaya());
            ps.setString(3, t.getStatusPembayaran());
            ps.setDate(4, Date.valueOf(t.getTanggalTransaksi()));
            ps.setInt(5, t.getIdTransaksi());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengubah transaksi: " + e.getMessage(), e);
        }
    }

    /** Menghapus transaksi (beserta pembayarannya) dan mengosongkan kembali kamarnya. */
    @Override
    public void hapus(int id) {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int idKamar = -1;
                try (PreparedStatement ps = c.prepareStatement("SELECT id_kamar FROM transaksi_sewa WHERE id_transaksi=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idKamar = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM transaksi_sewa WHERE id_transaksi=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                if (idKamar > 0) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE kamar SET status='Kosong' WHERE id_kamar=?")) {
                        ps.setInt(1, idKamar);
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransaksiSewa> ambilSemua() {
        return query(SELECT + "ORDER BY t.id_transaksi DESC");
    }

    public List<TransaksiSewa> cariSemua() {
        return ambilSemua();
    }

    public List<TransaksiSewa> cariTerbaru(int batas) {
        return query(SELECT + "ORDER BY t.id_transaksi DESC LIMIT " + batas);
    }

    public List<TransaksiSewa> cariBelumLunas() {
        return query(SELECT + "WHERE t.status_pembayaran = 'Belum Lunas' ORDER BY t.id_transaksi DESC");
    }

    @Override
    public TransaksiSewa cariById(int id) {
        for (TransaksiSewa t : query(SELECT + "WHERE t.id_transaksi = " + id)) return t;
        return null;
    }

    public int hitungBelumLunas() {
        return cariBelumLunas().size();
    }

    private List<TransaksiSewa> query(String sql) {
        List<TransaksiSewa> hasil = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TransaksiSewa t = new TransaksiSewa();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setIdPenyewa(rs.getInt("id_penyewa"));
                t.setIdKamar(rs.getInt("id_kamar"));
                t.setDurasiSewa(rs.getInt("durasi_sewa"));
                t.setTotalBiaya(rs.getDouble("total_biaya"));
                t.setStatusPembayaran(rs.getString("status_pembayaran"));
                t.setTanggalTransaksi(rs.getDate("tanggal_transaksi").toLocalDate());
                t.setNamaPenyewa(rs.getString("nama"));
                t.setNomorKamar(rs.getString("nomor_kamar"));
                hasil.add(t);
            }
            return hasil;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal membaca data transaksi: " + e.getMessage(), e);
        }
    }
}