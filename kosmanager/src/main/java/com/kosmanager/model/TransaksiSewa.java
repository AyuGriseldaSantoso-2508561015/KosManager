package com.kosmanager.model;

import kosmanager.dao.TransaksiDAO;
import kosmanager.util.Format;

import java.time.LocalDate;

public class TransaksiSewa {
    private int idTransaksi;
    private int idPenyewa;
    private int idKamar;
    private int durasiSewa;
    private double totalBiaya;
    private String statusPembayaran = "Belum Lunas";
    private LocalDate tanggalTransaksi = LocalDate.now();

    // data tampilan hasil JOIN (tidak disimpan di tabel transaksi_sewa)
    private String namaPenyewa;
    private String nomorKamar;

    /** Menghitung total biaya melalui objek Kamar, sehingga otomatis sesuai tipe kamar. */
    public double hitungTotal(Kamar kamar) {
        this.totalBiaya = kamar.hitungTotalBiaya(durasiSewa);
        return totalBiaya;
    }

    /** Menyimpan transaksi dan mengubah status kamar menjadi Terisi. */
    public void simpan() { new TransaksiDAO().tambah(this); }

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public int getIdPenyewa() { return idPenyewa; }
    public void setIdPenyewa(int idPenyewa) { this.idPenyewa = idPenyewa; }
    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }
    public int getDurasiSewa() { return durasiSewa; }
    public void setDurasiSewa(int durasiSewa) { this.durasiSewa = durasiSewa; }
    public double getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(double totalBiaya) { this.totalBiaya = totalBiaya; }
    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }
    public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(LocalDate tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public void setNamaPenyewa(String namaPenyewa) { this.namaPenyewa = namaPenyewa; }
    public String getNomorKamar() { return nomorKamar; }
    public void setNomorKamar(String nomorKamar) { this.nomorKamar = nomorKamar; }

    /** Ringkasan transaksi sewa sebagai struk. */
    public String cetakStruk() {
        return "No. Transaksi : " + idTransaksi
                + "\nPenyewa       : " + namaPenyewa
                + "\nKamar         : " + nomorKamar
                + "\nDurasi        : " + durasiSewa + " bulan"
                + "\nTanggal       : " + tanggalTransaksi
                + "\nTotal Sewa    : " + Format.rupiah(totalBiaya)
                + "\nStatus        : " + statusPembayaran;
    }

    @Override
    public String toString() {
        return "#" + idTransaksi + " - " + namaPenyewa + " - Kamar " + nomorKamar + " (" + Format.rupiah(totalBiaya) + ")";
    }
}
