package com.kosmanager.controller;
import com.kosmanager.model.Pembayaran;
import com.kosmanager.util.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PembayaranController implements Initializable {

    @FXML private ComboBox<TransaksiItem> cbTransaksi;
    @FXML private Label lblTagihan;
    @FXML private Label lblSisa;
    @FXML private TextField txtJumlahBayar;
    @FXML private DatePicker dpTanggalBayar;
    @FXML private ComboBox<String> cbMetode;
    @FXML private ComboBox<String> cbStatusBayar;
    @FXML private Button btnSimpanBayar;
    @FXML private Button btnCetakStruk;

    @FXML private TextField txtCariBayar;
    @FXML private ComboBox<String> cbFilterBayar;
    @FXML private TableView<Pembayaran> tblPembayaran;
    @FXML private TableColumn<Pembayaran, Integer> colId;
    @FXML private TableColumn<Pembayaran, String> colPenyewa;
    @FXML private TableColumn<Pembayaran, String> colJumlah;
    @FXML private TableColumn<Pembayaran, String> colTanggal;
    @FXML private TableColumn<Pembayaran, String> colMetode;
    @FXML private TableColumn<Pembayaran, String> colStatus;

    private final ObservableList<Pembayaran> listPembayaran = FXCollections.observableArrayList();
private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rupiahFormat.setMaximumFractionDigits(0);
        dpTanggalBayar.setValue(LocalDate.now());
        cbFilterBayar.setValue("Semua");

        setupTableColumns();
        loadTransaksiCombo();
        loadDataPembayaran();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPembayaran"));
        colPenyewa.setCellValueFactory(new PropertyValueFactory<>("namaPenyewa"));
        colJumlah.setCellValueFactory(cellData ->
                new SimpleStringProperty(rupiahFormat.format(cellData.getValue().getJumlahBayar())));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalBayar"));
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTransaksiCombo() {
        ObservableList<TransaksiItem> items = FXCollections.observableArrayList();
        String sql = "SELECT t.id_transaksi, p.nama, k.nomor_kamar, t.total_biaya, " +
                     "COALESCE((SELECT SUM(pb.jumlah_bayar) FROM pembayaran pb WHERE pb.id_transaksi = t.id_transaksi), 0) AS sudah_bayar " +
                     "FROM transaksi_sewa t " +
                     "JOIN penyewa p ON t.id_penyewa = p.id_penyewa " +
                     "JOIN kamar k ON t.id_kamar = k.id_kamar " +
                     "ORDER BY t.id_transaksi DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new TransaksiItem(
                        rs.getInt("id_transaksi"),
                        rs.getString("nama"),
                        rs.getString("nomor_kamar"),
                        rs.getDouble("total_biaya"),
                        rs.getDouble("sudah_bayar")
                ));
            }
            cbTransaksi.setItems(items);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", "Gagal memuat daftar transaksi: " + e.getMessage());
        }
    }

    private void loadDataPembayaran() {
        listPembayaran.clear();
        String keyword = txtCariBayar.getText() == null ? "" : txtCariBayar.getText().trim().toLowerCase();
        String statusFilter = cbFilterBayar.getValue();

        String sql = "SELECT pb.id_pembayaran, pb.id_transaksi, p.nama, k.nomor_kamar, " +
                     "pb.jumlah_bayar, pb.tanggal_bayar, pb.metode_pembayaran, pb.status " +
                     "FROM pembayaran pb " +
                     "JOIN transaksi_sewa t ON pb.id_transaksi = t.id_transaksi " +
                     "JOIN penyewa p ON t.id_penyewa = p.id_penyewa " +
                     "JOIN kamar k ON t.id_kamar = k.id_kamar " +
                     "ORDER BY pb.id_pembayaran DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nama = rs.getString("nama");
                String status = rs.getString("status");

                boolean cocokNama = keyword.isEmpty() || nama.toLowerCase().contains(keyword);
                boolean cocokStatus = statusFilter == null || statusFilter.equals("Semua") || status.equalsIgnoreCase(statusFilter);

                if (cocokNama && cocokStatus) {
                    listPembayaran.add(new Pembayaran(
                            rs.getInt("id_pembayaran"),
                            rs.getInt("id_transaksi"),
                            nama,
                            rs.getString("nomor_kamar"),
                            rs.getDouble("jumlah_bayar"),
                            rs.getString("tanggal_bayar"),
                            rs.getString("metode_pembayaran"),
                            status
                    ));
                }
            }
            tblPembayaran.setItems(listPembayaran);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", "Gagal memuat riwayat pembayaran: " + e.getMessage());
        }
    }

    @FXML
    private void handlePilihTransaksi() {
        TransaksiItem selected = cbTransaksi.getValue();
        if (selected != null) {
            double sisa = Math.max(0, selected.totalBiaya - selected.sudahBayar);
            lblTagihan.setText("Tagihan: " + rupiahFormat.format(selected.totalBiaya));
            lblSisa.setText("Sisa: " + rupiahFormat.format(sisa));
            txtJumlahBayar.setText(String.valueOf((long) sisa));
            cbStatusBayar.setValue(sisa <= 0 ? "Lunas" : "Lunas");
        } else {
            lblTagihan.setText("Tagihan: Rp0");
            lblSisa.setText("Sisa: Rp0");
        }
    }

    @FXML
    private void handleSimpanBayar() {
        TransaksiItem transaksi = cbTransaksi.getValue();
        String jumlahText = txtJumlahBayar.getText().trim();
        LocalDate tanggal = dpTanggalBayar.getValue();
        String metode = cbMetode.getValue();
        String status = cbStatusBayar.getValue();

        if (transaksi == null || jumlahText.isEmpty() || tanggal == null || metode == null || status == null) {
            showAlert(Alert.AlertType.WARNING, "Input Belum Lengkap", "Harap lengkapi semua kolom pembayaran terlebih dahulu!");
            return;
        }

        double jumlahBayar;
        try {
            jumlahBayar = Double.parseDouble(jumlahText);
            if (jumlahBayar <= 0) {
                showAlert(Alert.AlertType.WARNING, "Nominal Tidak Valid", "Jumlah bayar harus lebih dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format Salah", "Jumlah bayar harus berupa angka!");
            return;
        }

        String insertSql = "INSERT INTO pembayaran (id_transaksi, jumlah_bayar, tanggal_bayar, metode_pembayaran, status) VALUES (?, ?, ?, ?, ?)";
        String updateTransaksiSql = "UPDATE transaksi_sewa SET status_pembayaran = ? WHERE id_transaksi = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql);
                 PreparedStatement psUpdate = conn.prepareStatement(updateTransaksiSql)) {

                psInsert.setInt(1, transaksi.idTransaksi);
                psInsert.setDouble(2, jumlahBayar);
                psInsert.setDate(3, Date.valueOf(tanggal));
                psInsert.setString(4, metode);
                psInsert.setString(5, status);
                psInsert.executeUpdate();

                psUpdate.setString(1, status);
                psUpdate.setInt(2, transaksi.idTransaksi);
                psUpdate.executeUpdate();

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data pembayaran berhasil disimpan!");

                resetForm();
                loadTransaksiCombo();
                loadDataPembayaran();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Menyimpan", "Terjadi kesalahan database: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterData() {
        loadDataPembayaran();
    }

   @FXML
    private void handleCetakStruk() {
        Pembayaran terpilih = tblPembayaran.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih Data", "Klik salah satu baris pembayaran di tabel sebelah kanan untuk mencetak struk!");
            return;
        }

        String struk = "====================================\n" +
                       "        STRUK PEMBAYARAN KOS        \n" +
                       "             KOSMANAGER             \n" +
                       "====================================\n" +
                       "No. Pembayaran : #" + terpilih.getIdPembayaran() + "\n" +
                       "ID Transaksi   : #" + terpilih.getIdTransaksi() + "\n" +
                       "Nama Penyewa   : " + terpilih.getNamaPenyewa() + "\n" +
                       "Nomor Kamar    : " + terpilih.getNomorKamar() + "\n" +
                       "Tanggal Bayar  : " + terpilih.getTanggalBayar() + "\n" +
                       "Metode Bayar   : " + terpilih.getMetodePembayaran() + "\n" +
                       "Jumlah Bayar   : " + rupiahFormat.format(terpilih.getJumlahBayar()) + "\n" +
                       "Status         : " + terpilih.getStatus() + "\n" +
                       "====================================\n" +
                       " Terima kasih atas pembayaran Anda! \n" +
                       "====================================";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Struk Pembayaran");
        alert.setHeaderText("Bukti Pembayaran #" + terpilih.getIdPembayaran());

        TextArea area = new TextArea(struk);
        area.setEditable(false);
        area.setPrefColumnCount(38);
        area.setPrefRowCount(15);
        area.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px;");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void resetForm() {
        cbTransaksi.setValue(null);
        lblTagihan.setText("Tagihan: Rp0");
        lblSisa.setText("Sisa: Rp0");
        txtJumlahBayar.clear();
        dpTanggalBayar.setValue(LocalDate.now());
        cbMetode.setValue(null);
        cbStatusBayar.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class TransaksiItem {
        final int idTransaksi;
        final String namaPenyewa;
        final String nomorKamar;
        final double totalBiaya;
        final double sudahBayar;

        TransaksiItem(int idTransaksi, String namaPenyewa, String nomorKamar, double totalBiaya, double sudahBayar) {
            this.idTransaksi = idTransaksi;
            this.namaPenyewa = namaPenyewa;
            this.nomorKamar = nomorKamar;
            this.totalBiaya = totalBiaya;
            this.sudahBayar = sudahBayar;
        }

        @Override
        public String toString() {
            return "TRX-" + idTransaksi + " | " + namaPenyewa + " (Kamar " + nomorKamar + ")";
        }
    }
}