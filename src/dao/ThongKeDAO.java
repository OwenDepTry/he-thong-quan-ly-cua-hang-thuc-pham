package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.Date;
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

    public int getTongSoSanPhamDaBan() {
        String sql = "SELECT COALESCE(SUM(SoLuong), 0) FROM chitiethoadon";
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

    public int getTongSoKhachHang() {
        String sql = "SELECT COUNT(*) FROM khachhang";
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

    public int getDoanhThuHomNay() {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM hoadon WHERE DATE(ThoiGian) = CURDATE()";
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

    public int getDoanhThuThangNay() {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM hoadon WHERE MONTH(ThoiGian) = MONTH(CURDATE()) AND YEAR(ThoiGian) = YEAR(CURDATE())";
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

    public int getDoanhThuNamNay() {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM hoadon WHERE YEAR(ThoiGian) = YEAR(CURDATE())";
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

    public List<Object[]> thongKeDoanhThu7NgayGanNhat() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT DATE(ThoiGian) AS Ngay,
                   COUNT(*) AS SoHoaDon,
                   COALESCE(SUM(TongTien), 0) AS DoanhThu
            FROM hoadon
            WHERE DATE(ThoiGian) >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(ThoiGian)
            ORDER BY DATE(ThoiGian) ASC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getDate("Ngay"),
                    rs.getInt("SoHoaDon"),
                    rs.getInt("DoanhThu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> thongKeTheoNhanVien() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT nv.MaNV,
                   nv.HoTen,
                   COUNT(hd.MaHD) AS SoHoaDon,
                   COALESCE(SUM(hd.TongTien), 0) AS TongDoanhThu
            FROM nhanvien nv
            LEFT JOIN hoadon hd ON nv.MaNV = hd.MaNV
            GROUP BY nv.MaNV, nv.HoTen
            ORDER BY TongDoanhThu DESC, SoHoaDon DESC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getInt("SoHoaDon"),
                    rs.getInt("TongDoanhThu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> thongKeTheoKhachHang() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT kh.MaKH,
                   kh.HoTen,
                   COUNT(hd.MaHD) AS SoLanMua,
                   COALESCE(SUM(hd.TongTien), 0) AS TongChiTieu
            FROM khachhang kh
            LEFT JOIN hoadon hd ON kh.MaKH = hd.MaKH
            GROUP BY kh.MaKH, kh.HoTen
            ORDER BY TongChiTieu DESC, SoLanMua DESC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaKH"),
                    rs.getString("HoTen"),
                    rs.getInt("SoLanMua"),
                    rs.getInt("TongChiTieu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> thongKeSanPhamBanChay() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT sp.MaSP,
                   sp.TenSP,
                   COALESCE(SUM(ct.SoLuong), 0) AS SoLuongDaBan,
                   COALESCE(SUM(ct.SoLuong * ct.DonGia), 0) AS DoanhThuSP
            FROM sanpham sp
            LEFT JOIN chitiethoadon ct ON sp.MaSP = ct.MaSP
            GROUP BY sp.MaSP, sp.TenSP
            ORDER BY SoLuongDaBan DESC, DoanhThuSP DESC
            LIMIT 10
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getInt("SoLuongDaBan"),
                    rs.getInt("DoanhThuSP")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> thongKeDoanhThuTheoKhoangNgay(Date tuNgay, Date denNgay) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT DATE(ThoiGian) AS Ngay,
                   COUNT(*) AS SoHoaDon,
                   COALESCE(SUM(TongTien), 0) AS DoanhThu
            FROM hoadon
            WHERE DATE(ThoiGian) BETWEEN ? AND ?
            GROUP BY DATE(ThoiGian)
            ORDER BY DATE(ThoiGian) ASC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getDate("Ngay"),
                        rs.getInt("SoHoaDon"),
                        rs.getInt("DoanhThu")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTongDoanhThuTheoKhoangNgay(Date tuNgay, Date denNgay) {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) FROM hoadon WHERE DATE(ThoiGian) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

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

    public int getTongHoaDonTheoKhoangNgay(Date tuNgay, Date denNgay) {
        String sql = "SELECT COUNT(*) FROM hoadon WHERE DATE(ThoiGian) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);

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
}