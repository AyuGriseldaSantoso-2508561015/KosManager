package com.kosmanager.model;

public class KamarStandard extends Kamar {

    public KamarStandard() {
        setTipe("Standard");
        setBiayaFasilitas(0);
    }

    public KamarStandard(int idKamar, int idUser, String nomorKamar,
                         double hargaSewa, String status) {
        super(idKamar, idUser, nomorKamar, "Standard", hargaSewa, 0, status);
    }

    @Override
    public double hitungHargaTotal() {
        return getHargaSewa();
    }
}