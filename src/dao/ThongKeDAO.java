package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public int getTongDoanhThu() {
        String sql = "SELECT COALESCE(SUM(TongTien), 0) AS TongDoanhThu FROM hoadon";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TongDoanhThu");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTongPhieuNhap() {
        String sql = "SELECT COUNT(*) AS TongPhieuNhap FROM phieunhaphang";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TongPhieuNhap");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTongHoaDon() {
        String sql = "SELECT COUNT(*) AS TongHoaDon FROM hoadon";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TongHoaDon");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Object[]> getTopSanPhamBanChay() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT sp.MaSP, sp.TenSP, SUM(ct.SoLuong) AS TongBan
            FROM chitiethoadon ct
            JOIN sanpham sp ON ct.MaSP = sp.MaSP
            GROUP BY sp.MaSP, sp.TenSP
            ORDER BY TongBan DESC
        """;

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

    public List<Object[]> getTonKho() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT MaSP, TenSP, Gia, SoLuongTon
            FROM sanpham
            ORDER BY MaSP
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSP"),
                    rs.getString("TenSP"),
                    rs.getInt("Gia"),
                    rs.getInt("SoLuongTon")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}