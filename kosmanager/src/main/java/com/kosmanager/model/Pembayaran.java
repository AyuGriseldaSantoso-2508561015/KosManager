package com.kosmanager.model;

public class Pembayaran {
    private int idPembayaran;
    private int idTransaksi;
    private String namaPenyewa;
    private String nomorKamar;
    private double jumlahBayar;
    private String tanggalBayar;
    private String metodePembayaran;
    private String status;

    public Pembayaran(int idPembayaran, int idTransaksi, String namaPenyewa, String nomorKamar,
                      double jumlahBayar, String tanggalBayar, String metodePembayaran, String status) {
        this.idPembayaran = idPembayaran;
        this.idTransaksi = idTransaksi;
        this.namaPenyewa = namaPenyewa;
        this.nomorKamar = nomorKamar;
        this.jumlahBayar = jumlahBayar;
        this.tanggalBayar = tanggalBayar;
        this.metodePembayaran = metodePembayaran;
        this.status = status;
    }

    public int getIdPembayaran() { return idPembayaran; }
    public int getIdTransaksi() { return idTransaksi; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public String getNomorKamar() { return nomorKamar; }
    public double getJumlahBayar() { return jumlahBayar; }
    public String getTanggalBayar() { return tanggalBayar; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public String getStatus() { return status; }
}