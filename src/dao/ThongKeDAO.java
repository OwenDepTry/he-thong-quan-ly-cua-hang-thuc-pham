package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public double getTongDoanhThu(int year) {
        String sql = "SELECT COALESCE(SUM(TongTien - TienGiam), 0) "
                + "FROM HoaDon WHERE YEAR(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTongSoHoaDon(int year) {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE YEAR(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
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

    public int getTongSoPhieuNhap(int year) {
        String sql = "SELECT COUNT(*) FROM PhieuNhap WHERE YEAR(ThoiGian) = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
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

    public int[] getDoanhThuTheoThang(int year) {
        int[] data = new int[12];
        String sql = "SELECT MONTH(ThoiGian) AS Thang, COALESCE(SUM(TongTien - TienGiam), 0) AS DoanhThu "
                + "FROM HoaDon WHERE YEAR(ThoiGian) = ? "
                + "GROUP BY MONTH(ThoiGian)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("Thang");
                    int doanhThu = (int) Math.round(rs.getDouble("DoanhThu"));
                    if (thang >= 1 && thang <= 12) {
                        data[thang - 1] = doanhThu;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<Object[]> getThongKeSanPhamTheoNam(int year) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT sp.MaSanPham,
                   sp.TenSanPham,
                   COALESCE(SUM(ct.SoLuong), 0) AS SoLuongBan,
                   COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) AS DoanhThu,
                   COALESCE(SUM(ct.SoLuong * (ct.DonGia - sp.Gia)), 0) AS LoiNhuan
            FROM ChiTietHoaDon ct
            JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon
            JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham
            WHERE YEAR(hd.ThoiGian) = ?
            GROUP BY sp.MaSanPham, sp.TenSanPham
            ORDER BY DoanhThu DESC, sp.MaSanPham ASC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        rs.getInt("SoLuongBan"),
                        rs.getDouble("DoanhThu"),
                        rs.getDouble("LoiNhuan")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int[] getDoanhThuSanPhamTheoQuy(int year) {
        int[] data = new int[4];

        String sql = """
            SELECT QUARTER(hd.ThoiGian) AS Quy,
                   COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) AS DoanhThu
            FROM ChiTietHoaDon ct
            JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon
            WHERE YEAR(hd.ThoiGian) = ?
            GROUP BY QUARTER(hd.ThoiGian)
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int quy = rs.getInt("Quy");
                    int doanhThu = (int) Math.round(rs.getDouble("DoanhThu"));
                    if (quy >= 1 && quy <= 4) {
                        data[quy - 1] = doanhThu;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<Object[]> getThongKeNhanVienTheoNam(int year) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT nv.MaNhanVien,
                   nv.Ho,
                   nv.Ten,
                   COUNT(hd.MaHoaDon) AS SoLuongHoaDon,
                   COALESCE(SUM(hd.TongTien - hd.TienGiam), 0) AS TongSoTien
            FROM NhanVien nv
            LEFT JOIN HoaDon hd
                   ON nv.MaNhanVien = hd.MaNhanVien
                  AND YEAR(hd.ThoiGian) = ?
            GROUP BY nv.MaNhanVien, nv.Ho, nv.Ten
            ORDER BY TongSoTien DESC, nv.MaNhanVien ASC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaNhanVien"),
                        rs.getString("Ho"),
                        rs.getString("Ten"),
                        rs.getInt("SoLuongHoaDon"),
                        rs.getDouble("TongSoTien")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getThongKeKhachHangTheoNam(int year) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT kh.MaKhachHang,
                   kh.Ho,
                   kh.Ten,
                   COALESCE(SUM(ct.SoLuong), 0) AS SoLuongSanPhamDaMua,
                   COALESCE(SUM(hd.TongTien - hd.TienGiam), 0) AS TongSoTien
            FROM KhachHang kh
            LEFT JOIN HoaDon hd
                   ON kh.MaKhachHang = hd.MaKhachHang
                  AND YEAR(hd.ThoiGian) = ?
            LEFT JOIN ChiTietHoaDon ct
                   ON hd.MaHoaDon = ct.MaHoaDon
            GROUP BY kh.MaKhachHang, kh.Ho, kh.Ten
            ORDER BY TongSoTien DESC, kh.MaKhachHang ASC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaKhachHang"),
                        rs.getString("Ho"),
                        rs.getString("Ten"),
                        rs.getInt("SoLuongSanPhamDaMua"),
                        rs.getDouble("TongSoTien")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}