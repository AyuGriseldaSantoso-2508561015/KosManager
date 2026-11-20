package com.kosmanager.model;

import com.kosmanager.service.Login;

public class KamarStandard extends Kamar {

    public KamarStandard() {
        setTipe("Standard");
        setBiayaFasilitas(0);
    }

    public KamarStandard(int idKamar, int idUser, String nomorKamar,
                         double hargaSewa, String status) {
        super(idKamar, idUser, nomorKamar, "Standard", hargaSewa, 0, status);
    }

    public KamarStandard(int idKamar, String nomorKamar,
                         double hargaSewa, String status) {
        super(idKamar, Login.getUserId(), nomorKamar, "Standard", hargaSewa, 0, status);
    }

    @Override
    public double hitungHargaTotal() {
        return getHargaSewa();
    }
}