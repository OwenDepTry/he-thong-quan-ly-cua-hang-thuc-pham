package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public int getTongDoanhThu() {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM hoadon";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTongSoHoaDon() {
        String sql = "SELECT COUNT(*) FROM hoadon";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTongSoPhieuNhap() {
        String sql = "SELECT COUNT(*) FROM phieunhaphang";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Object[]> getTopSanPhamBanChay() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT sp.MaSP, sp.TenSP, COALESCE(SUM(ct.SoLuong), 0) AS TongBan "
                   + "FROM sanpham sp "
                   + "LEFT JOIN chitiethoadon ct ON sp.MaSP = ct.MaSP "
                   + "GROUP BY sp.MaSP, sp.TenSP "
                   + "ORDER BY TongBan DESC, sp.TenSP ASC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getInt("TongBan")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getTonKhoSanPham() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT MaSP, TenSP, SoLuongTon, Gia FROM sanpham ORDER BY TenSP ASC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getInt("SoLuongTon"),
                    rs.getInt("Gia")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getDoanhThuTheoNgay(String ngay) {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) "
                   + "FROM hoadon "
                   + "WHERE DATE(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ngay);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getDoanhThuTheoThang(int thang, int nam) {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) "
                   + "FROM hoadon "
                   + "WHERE MONTH(ThoiGian) = ? AND YEAR(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, thang);
            ps.setInt(2, nam);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getDoanhThuTheoNam(int nam) {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) "
                   + "FROM hoadon "
                   + "WHERE YEAR(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nam);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Object[]> getThongKeTheoNhanVien() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT nv.MaNV, nv.Ho, nv.Ten, COUNT(hd.MaHD) AS SoHoaDon, COALESCE(SUM(hd.TongTien), 0) AS DoanhThu "
                   + "FROM nhanvien nv "
                   + "LEFT JOIN hoadon hd ON nv.MaNV = hd.MaNV "
                   + "GROUP BY nv.MaNV, nv.Ho, nv.Ten "
                   + "ORDER BY DoanhThu DESC, nv.MaNV ASC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String hoTen = rs.getString("Ho") + " " + rs.getString("Ten");
                list.add(new Object[]{
                    rs.getString("MaNV"),
                    hoTen.trim(),
                    rs.getInt("SoHoaDon"),
                    rs.getInt("DoanhThu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getThongKeTheoKhachHang() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT kh.MaKH, kh.Ho, kh.Ten, COUNT(hd.MaHD) AS SoLanMua, COALESCE(SUM(hd.TongTien), 0) AS TongChiTieu "
                   + "FROM khachhang kh "
                   + "LEFT JOIN hoadon hd ON kh.MaKH = hd.MaKH "
                   + "GROUP BY kh.MaKH, kh.Ho, kh.Ten "
                   + "ORDER BY TongChiTieu DESC, kh.MaKH ASC";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String hoTen = rs.getString("Ho") + " " + rs.getString("Ten");
                list.add(new Object[]{
                    rs.getString("MaKH"),
                    hoTen.trim(),
                    rs.getInt("SoLanMua"),
                    rs.getInt("TongChiTieu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}