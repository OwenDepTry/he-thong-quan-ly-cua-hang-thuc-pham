package dao;

import config.DBConnection;
import entity.KhuyenMai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai ORDER BY MaKhuyenMai";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowForTable(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> searchForTable(String field, String keyword) {
        List<Object[]> list = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (normalizedKeyword.isEmpty()) {
            return findAllForTable();
        }

        String column = resolveSearchColumn(field);
        String sql = "SELECT * FROM KhuyenMai WHERE " + column + " LIKE ? ORDER BY MaKhuyenMai";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + normalizedKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowForTable(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public KhuyenMai findById(String maKhuyenMai) {
        String sql = "SELECT * FROM KhuyenMai WHERE MaKhuyenMai = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhuyenMai);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKhuyenMai(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsById(String maKhuyenMai) {
        String sql = "SELECT 1 FROM KhuyenMai WHERE MaKhuyenMai = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhuyenMai);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai (MaKhuyenMai, TenKhuyenMai, DieuKien, PhanTramGiam, NgayBatDau, NgayKetThuc, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKhuyenMai());
            ps.setString(3, km.getDieuKien());
            ps.setDouble(4, km.getPhanTramGiam());
            ps.setString(5, km.getNgayBatDau());
            ps.setString(6, km.getNgayKetThuc());
            ps.setString(7, km.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET TenKhuyenMai = ?, DieuKien = ?, PhanTramGiam = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? "
                + "WHERE MaKhuyenMai = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getTenKhuyenMai());
            ps.setString(2, km.getDieuKien());
            ps.setDouble(3, km.getPhanTramGiam());
            ps.setString(4, km.getNgayBatDau());
            ps.setString(5, km.getNgayKetThuc());
            ps.setString(6, km.getTrangThai());
            ps.setString(7, km.getMaKhuyenMai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maKhuyenMai) {
        String sql = "DELETE FROM KhuyenMai WHERE MaKhuyenMai = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhuyenMai);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Object[] mapRowForTable(ResultSet rs) throws Exception {
        return new Object[]{
            rs.getString("MaKhuyenMai"),
            rs.getString("TenKhuyenMai"),
            rs.getString("DieuKien"),
            rs.getDouble("PhanTramGiam"),
            rs.getString("NgayBatDau"),
            rs.getString("NgayKetThuc"),
            rs.getString("TrangThai")
        };
    }

    private KhuyenMai mapKhuyenMai(ResultSet rs) throws Exception {
        return new KhuyenMai(
                rs.getString("MaKhuyenMai"),
                rs.getString("TenKhuyenMai"),
                rs.getString("DieuKien"),
                rs.getDouble("PhanTramGiam"),
                rs.getString("NgayBatDau"),
                rs.getString("NgayKetThuc"),
                rs.getString("TrangThai")
        );
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "MaKhuyenMai";
        }

        switch (field.trim().toLowerCase()) {
            case "tên":
            case "ten":
                return "TenKhuyenMai";
            case "trạng thái":
            case "trang thai":
                return "TrangThai";
            default:
                return "MaKhuyenMai";
        }
    }
}