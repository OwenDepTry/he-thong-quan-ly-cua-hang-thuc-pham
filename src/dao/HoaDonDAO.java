package dao;

import config.DBConnection;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM hoadon ORDER BY ThoiGian DESC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new HoaDon(
                        rs.getString("MaHD"),
                        rs.getString("MaKH"),
                        rs.getString("MaNV"),
                        rs.getInt("TongTien"),
                        rs.getTimestamp("ThoiGian")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertHoaDon(HoaDon hd, List<ChiTietHoaDon> chiTietList) {
        String sqlHoaDon = "INSERT INTO hoadon (MaHD, MaKH, MaNV, TongTien, ThoiGian) VALUES (?, ?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chitiethoadon (MaHD, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            try (PreparedStatement psHD = conn.prepareStatement(sqlHoaDon)) {
                psHD.setString(1, hd.getMaHD());
                psHD.setString(2, hd.getMaKH());
                psHD.setString(3, hd.getMaNV());
                psHD.setInt(4, hd.getTongTien());
                psHD.setTimestamp(5, hd.getThoiGian());
                psHD.executeUpdate();
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlChiTiet)) {
                for (ChiTietHoaDon ct : chiTietList) {
                    psCT.setString(1, ct.getMaHD());
                    psCT.setString(2, ct.getMaSP());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setInt(4, ct.getDonGia());
                    psCT.addBatch();
                }
                psCT.executeBatch();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}