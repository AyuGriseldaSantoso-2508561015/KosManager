package com.kosmanager.model;

public class KamarVip extends Kamar {

    public KamarVip() {
        setTipe("VIP");
    }

    public KamarVip(int idKamar, int idUser, String nomorKamar,
                    double hargaSewa, double biayaFasilitas, String status) {
        super(idKamar, idUser, nomorKamar, "VIP", hargaSewa, biayaFasilitas, status);
    }

    @Override
    public double hitungHargaTotal() {
        return getHargaSewa() + getBiayaFasilitas();
    }
}