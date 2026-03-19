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
        String sql = "SELECT MaPhieu, MaNCCap, NguoiNhap, TongTien, ThoiGian FROM phieunhaphang ORDER BY ThoiGian DESC";

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

    public boolean existsById(String maPhieu) {
        String sql = "SELECT 1 FROM phieunhaphang WHERE MaPhieu = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhieu);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertPhieuNhap(PhieuNhapHang pn, List<ChiTietPNhap> chiTietList) {
        String sqlInsertPhieu = "INSERT INTO phieunhaphang (MaPhieu, MaNCCap, NguoiNhap, TongTien, ThoiGian) VALUES (?, ?, ?, ?, ?)";
        String sqlInsertChiTiet = "INSERT INTO chitietpnhap (MaPhieu, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        String sqlCongKho = "UPDATE sanpham SET SoLuongTon = SoLuongTon + ? WHERE MaSP = ?";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            if (existsByIdInConnection(conn, pn.getMaPhieu())) {
                conn.rollback();
                return false;
            }

            for (ChiTietPNhap ct : chiTietList) {
                if (ct.getSoLuong() <= 0 || ct.getDonGia() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement psPN = conn.prepareStatement(sqlInsertPhieu)) {
                psPN.setString(1, pn.getMaPhieu());
                psPN.setString(2, pn.getMaNCCap());
                psPN.setString(3, pn.getNguoiNhap());
                psPN.setInt(4, pn.getTongTien());
                psPN.setTimestamp(5, pn.getThoiGian());
                psPN.executeUpdate();
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlInsertChiTiet)) {
                for (ChiTietPNhap ct : chiTietList) {
                    psCT.setString(1, ct.getMaPhieu());
                    psCT.setString(2, ct.getMaSP());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setInt(4, ct.getDonGia());
                    psCT.addBatch();
                }
                psCT.executeBatch();
            }

            try (PreparedStatement psKho = conn.prepareStatement(sqlCongKho)) {
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
            rollbackQuietly(conn);
        } finally {
            closeConnectionQuietly(conn);
        }

        return false;
    }

    public List<ChiTietPNhap> getChiTietByMaPhieu(String maPhieu) {
        List<ChiTietPNhap> list = new ArrayList<>();
        String sql = "SELECT MaPhieu, MaSP, SoLuong, DonGia FROM chitietpnhap WHERE MaPhieu = ? ORDER BY MaSP";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhieu);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietPNhap(
                            rs.getString("MaPhieu"),
                            rs.getString("MaSP"),
                            rs.getInt("SoLuong"),
                            rs.getInt("DonGia")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean xoaPhieuNhap(String maPhieu) {
        String sqlGetChiTiet = "SELECT MaSP, SoLuong FROM chitietpnhap WHERE MaPhieu = ?";
        String sqlTruKhoLai = "UPDATE sanpham SET SoLuongTon = SoLuongTon - ? WHERE MaSP = ?";
        String sqlDeleteChiTiet = "DELETE FROM chitietpnhap WHERE MaPhieu = ?";
        String sqlDeletePhieu = "DELETE FROM phieunhaphang WHERE MaPhieu = ?";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetChiTiet);
                 PreparedStatement psKho = conn.prepareStatement(sqlTruKhoLai)) {

                psGet.setString(1, maPhieu);

                try (ResultSet rs = psGet.executeQuery()) {
                    while (rs.next()) {
                        psKho.setInt(1, rs.getInt("SoLuong"));
                        psKho.setString(2, rs.getString("MaSP"));
                        psKho.addBatch();
                    }
                }

                psKho.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteChiTiet)) {
                ps.setString(1, maPhieu);
                ps.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sqlDeletePhieu)) {
                ps.setString(1, maPhieu);
                rows = ps.executeUpdate();
            }

            conn.commit();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            rollbackQuietly(conn);
        } finally {
            closeConnectionQuietly(conn);
        }

        return false;
    }

    private boolean existsByIdInConnection(Connection conn, String maPhieu) throws Exception {
        String sql = "SELECT 1 FROM phieunhaphang WHERE MaPhieu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnectionQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}