package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT * FROM SanPham";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaSanPham"),
                    rs.getString("MaNhom"),
                    rs.getString("TenSanPham"),
                    rs.getString("Loai"),
                    rs.getString("DonViTinh"),
                    rs.getString("HanSuDung"),
                    rs.getString("MoTa"),
                    rs.getDouble("Gia"),
                    rs.getInt("SoLuongTon")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(String maSanPham, String maNhom, String tenSanPham, String loai,
                          String donViTinh, String hanSuDung, String moTa,
                          double gia, int soLuongTon) {
        String sql = "INSERT INTO SanPham (MaSanPham, MaNhom, TenSanPham, Loai, DonViTinh, HanSuDung, MoTa, Gia, SoLuongTon) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSanPham);
            ps.setString(2, maNhom);
            ps.setString(3, tenSanPham);
            ps.setString(4, loai);
            ps.setString(5, donViTinh);
            ps.setString(6, hanSuDung);
            ps.setString(7, moTa);
            ps.setDouble(8, gia);
            ps.setInt(9, soLuongTon);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(String maSanPham, String maNhom, String tenSanPham, String loai,
                          String donViTinh, String hanSuDung, String moTa,
                          double gia, int soLuongTon) {
        String sql = "UPDATE SanPham "
                   + "SET MaNhom = ?, TenSanPham = ?, Loai = ?, DonViTinh = ?, HanSuDung = ?, MoTa = ?, Gia = ?, SoLuongTon = ? "
                   + "WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhom);
            ps.setString(2, tenSanPham);
            ps.setString(3, loai);
            ps.setString(4, donViTinh);
            ps.setString(5, hanSuDung);
            ps.setString(6, moTa);
            ps.setDouble(7, gia);
            ps.setInt(8, soLuongTon);
            ps.setString(9, maSanPham);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteById(String maSanPham) {
        String sql = "DELETE FROM SanPham WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsById(String maSanPham) {
        String sql = "SELECT 1 FROM SanPham WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}