package dao;

import config.DBConnection;
import entity.KhuyenMai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    public List<KhuyenMai> getAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM khuyenmai ORDER BY MaKM";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new KhuyenMai(
                        rs.getString("MaKM"),
                        rs.getString("TenKM"),
                        rs.getInt("PhanTramGiam"),
                        rs.getDate("NgayBatDau"),
                        rs.getDate("NgayKetThuc"),
                        rs.getString("TrangThai")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<KhuyenMai> searchByKeyword(String keyword) {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM khuyenmai WHERE MaKM LIKE ? OR TenKM LIKE ? ORDER BY MaKM";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new KhuyenMai(
                            rs.getString("MaKM"),
                            rs.getString("TenKM"),
                            rs.getInt("PhanTramGiam"),
                            rs.getDate("NgayBatDau"),
                            rs.getDate("NgayKetThuc"),
                            rs.getString("TrangThai")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO khuyenmai (MaKM, TenKM, PhanTramGiam, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getPhanTramGiam());
            ps.setDate(4, km.getNgayBatDau());
            ps.setDate(5, km.getNgayKetThuc());
            ps.setString(6, km.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE khuyenmai SET TenKM=?, PhanTramGiam=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=? WHERE MaKM=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getPhanTramGiam());
            ps.setDate(3, km.getNgayBatDau());
            ps.setDate(4, km.getNgayKetThuc());
            ps.setString(5, km.getTrangThai());
            ps.setString(6, km.getMaKM());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maKM) {
        String sql = "DELETE FROM khuyenmai WHERE MaKM=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}