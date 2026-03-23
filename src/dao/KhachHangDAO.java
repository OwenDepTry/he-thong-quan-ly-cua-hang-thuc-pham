package dao;

import config.DBConnection;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY MaKhachHang";

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
        String sql = "SELECT * FROM KhachHang WHERE " + column + " LIKE ? ORDER BY MaKhachHang";

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

    public KhachHang findById(String maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKhachHang(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsById(String maKhachHang) {
        String sql = "SELECT 1 FROM KhachHang WHERE MaKhachHang = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsPhone(String phone, String excludeMaKhachHang) {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM KhachHang WHERE SoDienThoai = ?");
        boolean hasExclude = excludeMaKhachHang != null && !excludeMaKhachHang.trim().isEmpty();
        if (hasExclude) {
            sql.append(" AND MaKhachHang <> ?");
        }

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, phone);
            if (hasExclude) {
                ps.setString(2, excludeMaKhachHang);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (MaKhachHang, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh, NgayThamGia, Diem, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, kh, false);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET Ho = ?, TenLot = ?, Ten = ?, Phai = ?, NgaySinh = ?, SoDienThoai = ?, Tinh = ?, NgayThamGia = ?, Diem = ?, TrangThai = ? "
                + "WHERE MaKhachHang = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, kh, true);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maKhachHang) {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fillStatement(PreparedStatement ps, KhachHang kh, boolean updateMode) throws Exception {
        int index = 1;
        if (!updateMode) {
            ps.setString(index++, kh.getMaKhachHang());
        }

        ps.setString(index++, kh.getHo());
        ps.setString(index++, kh.getTenLot());
        ps.setString(index++, kh.getTen());
        ps.setString(index++, kh.getPhai());
        ps.setString(index++, kh.getNgaySinh());
        ps.setString(index++, kh.getSoDienThoai());
        ps.setString(index++, kh.getTinh());
        ps.setString(index++, kh.getNgayThamGia());
        ps.setInt(index++, kh.getDiem());
        ps.setString(index++, kh.getTrangThai());

        if (updateMode) {
            ps.setString(index, kh.getMaKhachHang());
        }
    }

    private Object[] mapRowForTable(ResultSet rs) throws Exception {
        return new Object[]{
            rs.getString("MaKhachHang"),
            rs.getString("Ho"),
            rs.getString("TenLot"),
            rs.getString("Ten"),
            rs.getString("Phai"),
            rs.getString("NgaySinh"),
            rs.getString("SoDienThoai"),
            rs.getString("Tinh"),
            rs.getString("NgayThamGia"),
            rs.getInt("Diem"),
            rs.getString("TrangThai")
        };
    }

    private KhachHang mapKhachHang(ResultSet rs) throws Exception {
        return new KhachHang(
                rs.getString("MaKhachHang"),
                rs.getString("Ho"),
                rs.getString("TenLot"),
                rs.getString("Ten"),
                rs.getString("Phai"),
                rs.getString("NgaySinh"),
                rs.getString("SoDienThoai"),
                rs.getString("Tinh"),
                rs.getString("NgayThamGia"),
                rs.getInt("Diem"),
                rs.getString("TrangThai")
        );
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "MaKhachHang";
        }

        switch (field.trim().toLowerCase()) {
            case "tên":
            case "ten":
                return "Ten";
            case "họ":
            case "ho":
                return "Ho";
            case "số điện thoại":
            case "sdt":
                return "SoDienThoai";
            default:
                return "MaKhachHang";
        }
    }
}