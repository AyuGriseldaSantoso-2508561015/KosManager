package com.kosmanager.model;

public class Penyewa {
    private int idPenyewa;
    private String nama;
    private String noWa;
    private String noKtp;

    public Penyewa(int idPenyewa, String nama, String noWa, String noKtp) {
        this.idPenyewa = idPenyewa;
        this.nama = nama;
        this.noWa = noWa;
        this.noKtp = noKtp;
    }

    public int getIdPenyewa() { return idPenyewa; }
    public void setIdPenyewa(int idPenyewa) { this.idPenyewa = idPenyewa; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoWa() { return noWa; }
    public void setNoWa(String noWa) { this.noWa = noWa; }
    public String getNoKtp() { return noKtp; }
    public void setNoKtp(String noKtp) { this.noKtp = noKtp; }

    @Override
    public String toString() { return nama; }
}
