package dao;

import config.DBConnection;
import entity.LoaiSanPham;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {

    public List<LoaiSanPham> findAll() {
        List<LoaiSanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiSanPham";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiSanPham l = new LoaiSanPham();
                l.setMaLoai(rs.getString("MaLoai"));
                l.setTenLoai(rs.getString("TenLoai"));
                list.add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(LoaiSanPham l) {
        String sql = "INSERT INTO LoaiSanPham VALUES (?, ?)";
        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, l.getMaLoai());
            ps.setString(2, l.getTenLoai());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(LoaiSanPham l) {
        String sql = "UPDATE LoaiSanPham SET TenLoai=? WHERE MaLoai=?";
        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, l.getTenLoai());
            ps.setString(2, l.getMaLoai());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maLoai) {
        String sql = "DELETE FROM LoaiSanPham WHERE MaLoai=?";
        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLoai);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}