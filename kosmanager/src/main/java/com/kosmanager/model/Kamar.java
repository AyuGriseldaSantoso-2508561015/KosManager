package com.kosmanager.model;

public abstract class Kamar {
    private int idKamar;
    private int idUser;
    private String nomorKamar;
    private String tipe;
    private double hargaSewa;
    private double biayaFasilitas;
    private String status;

    public Kamar() {
    }

    public Kamar(int idKamar, int idUser, String nomorKamar, String tipe,
                 double hargaSewa, double biayaFasilitas, String status) {
        this.idKamar = idKamar;
        this.idUser = idUser;
        this.nomorKamar = nomorKamar;
        this.tipe = tipe;
        this.hargaSewa = hargaSewa;
        this.biayaFasilitas = biayaFasilitas;
        this.status = status;
    }

    // Method abstract untuk polymorphism perhitungan total harga sewa per bulan
    public abstract double hitungHargaTotal();

    public int getIdKamar() {
        return idKamar;
    }

    public void setIdKamar(int idKamar) {
        this.idKamar = idKamar;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNomorKamar() {
        return nomorKamar;
    }

    public void setNomorKamar(String nomorKamar) {
        this.nomorKamar = nomorKamar;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public void setHargaSewa(double hargaSewa) {
        this.hargaSewa = hargaSewa;
    }

    public double getBiayaFasilitas() {
        return biayaFasilitas;
    }

    public void setBiayaFasilitas(double biayaFasilitas) {
        this.biayaFasilitas = biayaFasilitas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return nomorKamar + " (" + tipe + ")";
    }
}