CREATE DATABASE IF NOT EXISTS kosmanager;
USE kosmanager;

CREATE TABLE `user` (
  id_user  INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50)  NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role     VARCHAR(20)  NOT NULL DEFAULT 'admin'
);

CREATE TABLE kamar (
  id_kamar        INT AUTO_INCREMENT PRIMARY KEY,
  id_user         INT NOT NULL,
  nomor_kamar     VARCHAR(10)   NOT NULL UNIQUE,
  tipe            VARCHAR(30)   NOT NULL,
  harga_sewa      DECIMAL(12,2) NOT NULL,
  biaya_fasilitas DECIMAL(12,2) NOT NULL DEFAULT 0,
  status          VARCHAR(20)   NOT NULL DEFAULT 'Kosong',
  FOREIGN KEY (id_user) REFERENCES `user`(id_user)
);

CREATE TABLE penyewa (
  id_penyewa INT AUTO_INCREMENT PRIMARY KEY,
  id_user    INT NOT NULL,
  nama       VARCHAR(100) NOT NULL,
  no_wa      VARCHAR(20)  NOT NULL,
  no_ktp     VARCHAR(16)  NOT NULL,
  FOREIGN KEY (id_user) REFERENCES `user`(id_user)
);

CREATE TABLE transaksi_sewa (
  id_transaksi      INT AUTO_INCREMENT PRIMARY KEY,
  id_penyewa        INT NOT NULL,
  id_kamar          INT NOT NULL,
  durasi_sewa       INT NOT NULL,
  total_biaya       DECIMAL(12,2) NOT NULL,
  status_pembayaran VARCHAR(20)   NOT NULL DEFAULT 'Belum Lunas',
  tanggal_transaksi DATE NOT NULL,
  FOREIGN KEY (id_penyewa) REFERENCES penyewa(id_penyewa),
  FOREIGN KEY (id_kamar)   REFERENCES kamar(id_kamar)
);

CREATE TABLE pembayaran (
  id_pembayaran     INT AUTO_INCREMENT PRIMARY KEY,
  id_transaksi      INT NOT NULL,
  jumlah_bayar      DECIMAL(12,2) NOT NULL,
  tanggal_bayar     DATE NOT NULL,
  metode_pembayaran VARCHAR(30) NOT NULL,
  status            VARCHAR(20) NOT NULL,
  FOREIGN KEY (id_transaksi) REFERENCES transaksi_sewa(id_transaksi) ON DELETE CASCADE
);

// Akun awal: admin01 / admin123 
INSERT INTO `user`(username,password,role)
VALUES ('admin01','admin123','admin');
INSERT INTO kamar(id_user,nomor_kamar,tipe,harga_sewa,biaya_fasilitas,status) VALUES
 (1,'A01','Standard',800000,0,'Kosong'),
 (1,'A02','Standard',800000,0,'Kosong'),
 (1,'B01','VIP',1200000,200000,'Kosong');
INSERT INTO penyewa(id_user,nama,no_wa,no_ktp) VALUES (1,'Rika','081234567890','5101000000000001');