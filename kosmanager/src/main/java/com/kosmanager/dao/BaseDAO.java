package com.kosmanager.dao;

import java.util.List;

public interface BaseDAO<T> {
    void tambah(T data);
    void ubah(T data);
    void hapus(int id);
    T cariById(int id);
    List<T> ambilSemua();
}