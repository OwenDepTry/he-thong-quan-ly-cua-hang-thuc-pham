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

        String sql = "SELECT hd.MaHD, hd.MaKH, hd.MaNV, hd.TongTien, hd.TienGiam, hd.ThoiGian, hdkm.MaKM "
                + "FROM hoadon hd "
                + "LEFT JOIN hoadon_khuyenmai hdkm ON hd.MaHD = hdkm.MaHD "
                + "ORDER BY hd.ThoiGian DESC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                        rs.getString("MaHD"),
                        rs.getString("MaKH"),
                        rs.getString("MaNV"),
                        rs.getInt("TongTien"),
                        rs.getInt("TienGiam"),
                        rs.getString("MaKM"),
                        rs.getTimestamp("ThoiGian")
                );
                list.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean existsById(String maHD) {
        String sql = "SELECT 1 FROM hoadon WHERE MaHD = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertHoaDon(HoaDon hd, List<ChiTietHoaDon> chiTietList) {
        String sqlCheckTon = "SELECT SoLuongTon FROM sanpham WHERE MaSP = ?";
        String sqlInsertHoaDon = "INSERT INTO hoadon (MaHD, MaKH, MaNV, TongTien, TienGiam, ThoiGian) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlInsertChiTiet = "INSERT INTO chitiethoadon (MaHD, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        String sqlTruKho = "UPDATE sanpham SET SoLuongTon = SoLuongTon - ? WHERE MaSP = ?";
        String sqlInsertKhuyenMai = "INSERT INTO hoadon_khuyenmai (MaHD, MaKM) VALUES (?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            if (existsByIdInConnection(conn, hd.getMaHD())) {
                conn.rollback();
                return false;
            }

            for (ChiTietHoaDon ct : chiTietList) {
                Integer tonKho = getSoLuongTon(conn, ct.getMaSP());
                if (tonKho == null) {
                    conn.rollback();
                    return false;
                }
                if (ct.getSoLuong() <= 0 || ct.getDonGia() <= 0 || ct.getSoLuong() > tonKho) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement psHD = conn.prepareStatement(sqlInsertHoaDon)) {
                psHD.setString(1, hd.getMaHD());
                psHD.setString(2, hd.getMaKH());
                psHD.setString(3, hd.getMaNV());
                psHD.setInt(4, hd.getTongTien());
                psHD.setInt(5, hd.getTienGiam());
                psHD.setTimestamp(6, hd.getThoiGian());
                psHD.executeUpdate();
            }

            if (hd.getMaKM() != null && !hd.getMaKM().isBlank()) {
                try (PreparedStatement psKM = conn.prepareStatement(sqlInsertKhuyenMai)) {
                    psKM.setString(1, hd.getMaHD());
                    psKM.setString(2, hd.getMaKM());
                    psKM.executeUpdate();
                }
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlInsertChiTiet)) {
                for (ChiTietHoaDon ct : chiTietList) {
                    psCT.setString(1, ct.getMaHD());
                    psCT.setString(2, ct.getMaSP());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setInt(4, ct.getDonGia());
                    psCT.addBatch();
                }
                psCT.executeBatch();
            }

            try (PreparedStatement psKho = conn.prepareStatement(sqlTruKho)) {
                for (ChiTietHoaDon ct : chiTietList) {
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

    public List<ChiTietHoaDon> getChiTietByMaHD(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT MaHD, MaSP, SoLuong, DonGia FROM chitiethoadon WHERE MaHD = ? ORDER BY MaSP";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietHoaDon(
                            rs.getString("MaHD"),
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

    public boolean xoaHoaDon(String maHD) {
        String sqlGetChiTiet = "SELECT MaSP, SoLuong FROM chitiethoadon WHERE MaHD = ?";
        String sqlCongLaiKho = "UPDATE sanpham SET SoLuongTon = SoLuongTon + ? WHERE MaSP = ?";
        String sqlDeleteKhuyenMai = "DELETE FROM hoadon_khuyenmai WHERE MaHD = ?";
        String sqlDeleteChiTiet = "DELETE FROM chitiethoadon WHERE MaHD = ?";
        String sqlDeleteHoaDon = "DELETE FROM hoadon WHERE MaHD = ?";

        Connection conn = null;

        try {
            conn = DBConnection.open();
            conn.setAutoCommit(false);

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetChiTiet);
                 PreparedStatement psKho = conn.prepareStatement(sqlCongLaiKho)) {

                psGet.setString(1, maHD);

                try (ResultSet rs = psGet.executeQuery()) {
                    while (rs.next()) {
                        psKho.setInt(1, rs.getInt("SoLuong"));
                        psKho.setString(2, rs.getString("MaSP"));
                        psKho.addBatch();
                    }
                }

                psKho.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteKhuyenMai)) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteChiTiet)) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteHoaDon)) {
                ps.setString(1, maHD);
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

    private boolean existsByIdInConnection(Connection conn, String maHD) throws Exception {
        String sql = "SELECT 1 FROM hoadon WHERE MaHD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer getSoLuongTon(Connection conn, String maSP) throws Exception {
        String sql = "SELECT SoLuongTon FROM sanpham WHERE MaSP = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoLuongTon");
                }
            }
        }
        return null;
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