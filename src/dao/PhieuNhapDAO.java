package dao;

import config.DBConnection;
import entity.ChiTietPNhap;
import entity.PhieuNhapHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDAO {

    public List<PhieuNhapHang> getAll() {
        List<PhieuNhapHang> list = new ArrayList<>();
        String sql = "SELECT * FROM phieunhaphang ORDER BY ThoiGian DESC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PhieuNhapHang(
                        rs.getString("MaPhieu"),
                        rs.getString("MaNCCap"),
                        rs.getString("NguoiNhap"),
                        rs.getInt("TongTien"),
                        rs.getTimestamp("ThoiGian")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertPhieuNhap(PhieuNhapHang pn, List<ChiTietPNhap> chiTietList) {
        String sqlPhieu = "INSERT INTO phieunhaphang (MaPhieu, MaNCCap, NguoiNhap, TongTien, ThoiGian) VALUES (?, ?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chitietpnhap (MaPhieu, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        String sqlUpdateKho = "UPDATE sanpham SET SoLuongTon = SoLuongTon + ? WHERE MaSP = ?";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            try (PreparedStatement psPhieu = conn.prepareStatement(sqlPhieu)) {
                psPhieu.setString(1, pn.getMaPhieu());
                psPhieu.setString(2, pn.getMaNCCap());
                psPhieu.setString(3, pn.getNguoiNhap());
                psPhieu.setInt(4, pn.getTongTien());
                psPhieu.setTimestamp(5, pn.getThoiGian());
                psPhieu.executeUpdate();
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlChiTiet)) {
                for (ChiTietPNhap ct : chiTietList) {
                    psCT.setString(1, ct.getMaPhieu());
                    psCT.setString(2, ct.getMaSP());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setInt(4, ct.getDonGia());
                    psCT.addBatch();
                }
                psCT.executeBatch();
            }

            try (PreparedStatement psKho = conn.prepareStatement(sqlUpdateKho)) {
                for (ChiTietPNhap ct : chiTietList) {
                    psKho.setInt(1, ct.getSoLuong());
                    psKho.setString(2, ct.getMaSP());
                    psKho.addBatch();
                }
                psKho.executeBatch();
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