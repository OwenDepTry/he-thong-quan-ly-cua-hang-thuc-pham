package dao;

import config.DBConnection;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public NhanVien login(String maNhanVien, String matKhau) {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ? AND MatKhau = ? AND TrangThai = 'active'";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien";

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
        String sql = "SELECT * FROM NhanVien WHERE " + column + " LIKE ? ORDER BY MaNhanVien";

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

    public NhanVien findById(String maNhanVien) {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsById(String maNhanVien) {
        String sql = "SELECT 1 FROM NhanVien WHERE MaNhanVien = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsPhone(String phone, String excludeMaNhanVien) {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM NhanVien WHERE SoDienThoai = ?");
        boolean hasExclude = excludeMaNhanVien != null && !excludeMaNhanVien.trim().isEmpty();

        if (hasExclude) {
            sql.append(" AND MaNhanVien <> ?");
        }

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, phone);
            if (hasExclude) {
                ps.setString(2, excludeMaNhanVien);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (MaNhanVien, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh, DiaChi, Luong, ChucVu, TrangThai, MatKhau) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, nv, false);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET Ho = ?, TenLot = ?, Ten = ?, Phai = ?, NgaySinh = ?, SoDienThoai = ?, Tinh = ?, DiaChi = ?, Luong = ?, ChucVu = ?, TrangThai = ?, MatKhau = ? "
                + "WHERE MaNhanVien = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, nv, true);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNhanVien) {
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateSelfInfo(NhanVien nv) {
        String sql = """
            UPDATE NhanVien
            SET Ho = ?, TenLot = ?, Ten = ?, Phai = ?, NgaySinh = ?,
                SoDienThoai = ?, Tinh = ?, DiaChi = ?
            WHERE MaNhanVien = ?
        """;

        try (Connection conn = DBConnection.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHo());
            ps.setString(2, nv.getTenLot());
            ps.setString(3, nv.getTen());
            ps.setString(4, nv.getPhai());
            ps.setString(5, nv.getNgaySinh());
            ps.setString(6, nv.getSoDienThoai());
            ps.setString(7, nv.getTinh());
            ps.setString(8, nv.getDiaChi());
            ps.setString(9, nv.getMaNhanVien());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean changePassword(String maNhanVien, String oldPassword, String newPassword) {
        String sql = """
            UPDATE NhanVien
            SET MatKhau = ?
            WHERE MaNhanVien = ? AND MatKhau = ?
        """;

        try (Connection conn = DBConnection.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, maNhanVien);
            ps.setString(3, oldPassword);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void fillStatement(PreparedStatement ps, NhanVien nv, boolean updateMode) throws Exception {
        int index = 1;

        if (!updateMode) {
            ps.setString(index++, nv.getMaNhanVien());
        }

        ps.setString(index++, nv.getHo());
        ps.setString(index++, nv.getTenLot());
        ps.setString(index++, nv.getTen());
        ps.setString(index++, nv.getPhai());
        ps.setString(index++, nv.getNgaySinh());
        ps.setString(index++, nv.getSoDienThoai());
        ps.setString(index++, nv.getTinh());
        ps.setString(index++, nv.getDiaChi());
        ps.setDouble(index++, nv.getLuong());
        ps.setString(index++, nv.getChucVu());
        ps.setString(index++, nv.getTrangThai());
        ps.setString(index++, nv.getMatKhau());

        if (updateMode) {
            ps.setString(index, nv.getMaNhanVien());
        }
    }

    private Object[] mapRowForTable(ResultSet rs) throws Exception {
        return new Object[]{
            rs.getString("MaNhanVien"),
            rs.getString("Ho"),
            rs.getString("TenLot"),
            rs.getString("Ten"),
            rs.getString("Phai"),
            rs.getString("NgaySinh"),
            rs.getString("SoDienThoai"),
            rs.getString("Tinh"),
            rs.getString("DiaChi"),
            rs.getDouble("Luong"),
            rs.getString("ChucVu"),
            rs.getString("TrangThai")
        };
    }

    private NhanVien mapNhanVien(ResultSet rs) throws Exception {
        return new NhanVien(
                rs.getString("MaNhanVien"),
                rs.getString("Ho"),
                rs.getString("TenLot"),
                rs.getString("Ten"),
                rs.getString("Phai"),
                rs.getString("NgaySinh"),
                rs.getString("SoDienThoai"),
                rs.getString("Tinh"),
                rs.getString("DiaChi"),
                rs.getDouble("Luong"),
                rs.getString("ChucVu"),
                rs.getString("TrangThai"),
                rs.getString("MatKhau")
        );
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "MaNhanVien";
        }

        switch (field.trim().toLowerCase()) {
            case "tên":
            case "ten":
                return "Ten";
            case "số điện thoại":
            case "sdt":
                return "SoDienThoai";
            case "chức vụ":
            case "chuc vu":
                return "ChucVu";
            default:
                return "MaNhanVien";
        }
    }
}