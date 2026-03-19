package dao;

import config.DBConnection;
import entity.SanPham;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM sanpham ORDER BY MaSP";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SanPham sp = new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("Loai"),
                        rs.getInt("DonViTinh"),
                        rs.getInt("HSDung"),
                        rs.getString("MoTa"),
                        rs.getInt("Gia"),
                        rs.getInt("SoLuongTon")
                );
                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<SanPham> searchByKeyword(String keyword) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM sanpham WHERE MaSP LIKE ? OR TenSP LIKE ? ORDER BY MaSP";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham(
                            rs.getString("MaSP"),
                            rs.getString("TenSP"),
                            rs.getInt("Loai"),
                            rs.getInt("DonViTinh"),
                            rs.getInt("HSDung"),
                            rs.getString("MoTa"),
                            rs.getInt("Gia"),
                            rs.getInt("SoLuongTon")
                    );
                    list.add(sp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO sanpham (MaSP, TenSP, Loai, DonViTinh, HSDung, MoTa, Gia, SoLuongTon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setInt(3, sp.getLoai());
            ps.setInt(4, sp.getDonViTinh());
            ps.setInt(5, sp.getHsDung());
            ps.setString(6, sp.getMoTa());
            ps.setInt(7, sp.getGia());
            ps.setInt(8, sp.getSoLuongTon());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE sanpham SET TenSP=?, Loai=?, DonViTinh=?, HSDung=?, MoTa=?, Gia=?, SoLuongTon=? WHERE MaSP=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSP());
            ps.setInt(2, sp.getLoai());
            ps.setInt(3, sp.getDonViTinh());
            ps.setInt(4, sp.getHsDung());
            ps.setString(5, sp.getMoTa());
            ps.setInt(6, sp.getGia());
            ps.setInt(7, sp.getSoLuongTon());
            ps.setString(8, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maSP) {
        String sql = "DELETE FROM sanpham WHERE MaSP=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public SanPham findById(String maSP) {
    String sql = "SELECT * FROM sanpham WHERE MaSP = ?";

    try (Connection conn = DBConnection.open();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, maSP);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("Loai"),
                        rs.getInt("DonViTinh"),
                        rs.getInt("HSDung"),
                        rs.getString("MoTa"),
                        rs.getInt("Gia"),
                        rs.getInt("SoLuongTon")
                );
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
    }

    public String getTenSanPhamByMa(String maSP) {
    String sql = "SELECT TenSP FROM sanpham WHERE MaSP = ?";

    try (Connection conn = DBConnection.open();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, maSP);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("TenSP");
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "";
    }
}