package dao;

import config.DBConnection;
import entity.NhaCungCap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap ORDER BY MaNhaCungCap";

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
        String sql = "SELECT * FROM NhaCungCap WHERE " + column + " LIKE ? ORDER BY MaNhaCungCap";

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

    public NhaCungCap findById(String maNhaCungCap) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhaCungCap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhaCungCap(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsById(String maNhaCungCap) {
        String sql = "SELECT 1 FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhaCungCap);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsPhone(String phone, String excludeMa) {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM NhaCungCap WHERE SoDienThoai = ?");
        boolean hasExclude = excludeMa != null && !excludeMa.trim().isEmpty();

        if (hasExclude) {
            sql.append(" AND MaNhaCungCap <> ?");
        }

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, phone);
            if (hasExclude) {
                ps.setString(2, excludeMa);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insert(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (MaNhaCungCap, Ten, TenLienHe, SoDienThoai, Tinh, DiaChi, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTen());
            ps.setString(3, ncc.getTenLienHe());
            ps.setString(4, ncc.getSoDienThoai());
            ps.setString(5, ncc.getTinh());
            ps.setString(6, ncc.getDiaChi());
            ps.setString(7, ncc.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET Ten = ?, TenLienHe = ?, SoDienThoai = ?, Tinh = ?, DiaChi = ?, TrangThai = ? "
                + "WHERE MaNhaCungCap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getTen());
            ps.setString(2, ncc.getTenLienHe());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setString(4, ncc.getTinh());
            ps.setString(5, ncc.getDiaChi());
            ps.setString(6, ncc.getTrangThai());
            ps.setString(7, ncc.getMaNhaCungCap());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNhaCungCap) {
        String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhaCungCap);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Object[] mapRowForTable(ResultSet rs) throws Exception {
        return new Object[]{
            rs.getString("MaNhaCungCap"),
            rs.getString("Ten"),
            rs.getString("TenLienHe"),
            rs.getString("SoDienThoai"),
            rs.getString("Tinh"),
            rs.getString("DiaChi"),
            rs.getString("TrangThai")
        };
    }

    private NhaCungCap mapNhaCungCap(ResultSet rs) throws Exception {
        return new NhaCungCap(
                rs.getString("MaNhaCungCap"),
                rs.getString("Ten"),
                rs.getString("TenLienHe"),
                rs.getString("SoDienThoai"),
                rs.getString("Tinh"),
                rs.getString("DiaChi"),
                rs.getString("TrangThai")
        );
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "MaNhaCungCap";
        }

        switch (field.trim().toLowerCase()) {
            case "tên":
            case "ten":
                return "Ten";
            case "tên liên hệ":
            case "ten lien he":
                return "TenLienHe";
            case "số điện thoại":
            case "sdt":
                return "SoDienThoai";
            default:
                return "MaNhaCungCap";
        }
    }
}