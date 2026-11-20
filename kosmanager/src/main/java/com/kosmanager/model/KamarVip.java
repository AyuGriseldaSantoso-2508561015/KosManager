package com.kosmanager.model;

import com.kosmanager.service.Login;

public class KamarVip extends Kamar {

    public KamarVip() {
        setTipe("VIP");
    }

    public KamarVip(int idKamar, int idUser, String nomorKamar,
                    double hargaSewa, double biayaFasilitas, String status) {
        super(idKamar, idUser, nomorKamar, "VIP", hargaSewa, biayaFasilitas, status);
    }

    public KamarVip(int idKamar, String nomorKamar, double hargaSewa,
                    String status, double biayaFasilitas) {
        super(idKamar, Login.getUserId(), nomorKamar, "VIP", hargaSewa, biayaFasilitas, status);
    }

    @Override
    public double hitungHargaTotal() {
        return getHargaSewa() + getBiayaFasilitas();
    }
}