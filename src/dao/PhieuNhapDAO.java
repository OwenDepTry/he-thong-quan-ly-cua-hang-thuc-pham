package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PhieuNhapDAO {

    /*
     * Danh sách phiếu nhập lấy kèm tên NCC và tên NV bằng một câu JOIN duy nhất,
     * thay vì mỗi dòng gọi thêm 2 truy vấn (getTenNhaCungCapById / getTenNhanVienById).
     */
    private static final String SELECT_PHIEU_NHAP = """
        SELECT pn.MaPhieuNhap,
               pn.MaNhaCungCap,
               COALESCE(ncc.Ten, '') AS TenNhaCungCap,
               pn.MaNhanVien,
               COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '') AS TenNhanVien,
               pn.TongTien,
               pn.ThoiGian
        FROM PhieuNhap pn
        LEFT JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNhaCungCap
        LEFT JOIN NhanVien nv ON nv.MaNhanVien = pn.MaNhanVien
        """;

    private static final String ORDER_PHIEU_NHAP =
            " ORDER BY CAST(SUBSTRING(pn.MaPhieuNhap, 3) AS UNSIGNED) DESC, pn.MaPhieuNhap DESC";

    private List<Object[]> queryPhieuNhap(String where, String... params) {
        List<Object[]> list = new ArrayList<>();
        String sql = SELECT_PHIEU_NHAP + (where == null ? "" : " WHERE " + where) + ORDER_PHIEU_NHAP;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaPhieuNhap"),
                        safe(rs.getString("MaNhaCungCap")),
                        safe(rs.getString("TenNhaCungCap")),
                        safe(rs.getString("MaNhanVien")),
                        safe(rs.getString("TenNhanVien")),
                        rs.getDouble("TongTien"),
                        rs.getString("ThoiGian")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> findAllForTable() {
        return queryPhieuNhap(null);
    }

    public List<Object[]> findAllForTableByNhanVien(String maNhanVien) {
        return queryPhieuNhap("pn.MaNhanVien = ?", maNhanVien);
    }

    public List<Object[]> search(String keyword) {
        return search(null, keyword);
    }

    public List<Object[]> search(String field, String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return findAllForTable();
        }
        return queryPhieuNhap("pn." + resolveSearchColumn(field) + " LIKE ?", "%" + normalizedKeyword + "%");
    }

    public List<Object[]> searchByNhanVien(String keyword, String maNhanVien) {
        return searchByNhanVien(null, keyword, maNhanVien);
    }

    public List<Object[]> searchByNhanVien(String field, String keyword, String maNhanVien) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return findAllForTableByNhanVien(maNhanVien);
        }
        return queryPhieuNhap("pn.MaNhanVien = ? AND pn." + resolveSearchColumn(field) + " LIKE ?",
                maNhanVien, "%" + normalizedKeyword + "%");
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "MaPhieuNhap";
        }

        switch (field.trim().toLowerCase()) {
            case "mã ncc":
            case "ma ncc":
                return "MaNhaCungCap";
            case "mã nv":
            case "ma nv":
                return "MaNhanVien";
            default:
                return "MaPhieuNhap";
        }
    }

    public List<Object[]> findDetailsByPhieuNhap(String maPhieuNhap) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT ct.MaSanPham,
                   sp.TenSanPham,
                   ct.SoLuong,
                   ct.DonGia,
                   (ct.SoLuong * ct.DonGia) AS ThanhTien
            FROM ChiTietPhieuNhap ct
            JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham
            WHERE ct.MaPhieuNhap = ?
            ORDER BY ct.MaSanPham
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhieuNhap);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia"),
                        rs.getDouble("ThanhTien")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean exists(String maPhieuNhap) {
        String sql = "SELECT 1 FROM PhieuNhap WHERE MaPhieuNhap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaPhieuNhap() {
        String sql = "SELECT MaPhieuNhap FROM PhieuNhap WHERE MaPhieuNhap LIKE 'PN%'";
        int max = 0;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("MaPhieuNhap");
                if (ma == null) {
                    continue;
                }

                ma = ma.trim().toUpperCase();
                if (!ma.startsWith("PN")) {
                    continue;
                }

                String so = ma.substring(2).trim();
                if (so.matches("\\d+")) {
                    max = Math.max(max, Integer.parseInt(so));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "PN" + (max + 1);
    }

    public List<String> getNhanVienOptions() {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT MaNhanVien, Ho, COALESCE(TenLot, '') AS TenLot, Ten
            FROM NhanVien
            WHERE TrangThai = 'active'
            ORDER BY MaNhanVien
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String hoTen = (
                        safe(rs.getString("Ho")) + " "
                                + safe(rs.getString("TenLot")) + " "
                                + safe(rs.getString("Ten"))
                ).trim().replaceAll("\\s+", " ");

                list.add(rs.getString("MaNhanVien") + " - " + hoTen);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getNhaCungCapOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap ORDER BY MaNhaCungCap";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            String tenCol = null;
            for (int i = 1; i <= columnCount; i++) {
                String col = meta.getColumnName(i);
                if (equalsIgnoreCase(col, "TenNhaCungCap")
                        || equalsIgnoreCase(col, "Ten")
                        || equalsIgnoreCase(col, "TenNCC")
                        || equalsIgnoreCase(col, "HoTen")) {
                    tenCol = col;
                    break;
                }
            }

            while (rs.next()) {
                String ma = safe(rs.getString("MaNhaCungCap"));
                String ten = tenCol == null ? ma : safe(rs.getString(tenCol));
                list.add(ma + " - " + ten);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getTenNhaCungCapById(String maNCC) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNCC);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String col = meta.getColumnName(i);
                        if (equalsIgnoreCase(col, "TenNhaCungCap")
                                || equalsIgnoreCase(col, "Ten")
                                || equalsIgnoreCase(col, "TenNCC")
                                || equalsIgnoreCase(col, "HoTen")) {
                            return safe(rs.getString(col));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getTenNhanVienById(String maNV) {
        String sql = """
            SELECT Ho, COALESCE(TenLot, '') AS TenLot, Ten
            FROM NhanVien
            WHERE MaNhanVien = ?
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (safe(rs.getString("Ho")) + " "
                            + safe(rs.getString("TenLot")) + " "
                            + safe(rs.getString("Ten"))).trim().replaceAll("\\s+", " ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public List<String> getSanPhamOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaSanPham, TenSanPham FROM SanPham ORDER BY MaSanPham";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaSanPham") + " - " + rs.getString("TenSanPham"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getTenSanPham(String maSP) {
        String sql = "SELECT TenSanPham FROM SanPham WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return safe(rs.getString("TenSanPham"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public double getGiaNhapSanPham(String maSP) {
        String giaCol = getExistingColumn("SanPham", "GiaNhap", "Gia");

        if (giaCol == null) {
            return 0;
        }

        String sql = "SELECT " + giaCol + " FROM SanPham WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(giaCol);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean insertPhieuNhap(String maPhieuNhap, String maNCC, String maNV, String ngayNhap,
                                   String tongTien, DefaultTableModel itemModel) {

        String ngayCol = getExistingColumn("PhieuNhap", "NgayNhap", "ThoiGian");
        if (ngayCol == null) {
            ngayCol = "NgayNhap";
        }

        String sqlPN = "INSERT INTO PhieuNhap (MaPhieuNhap, MaNhaCungCap, MaNhanVien, TongTien, " + ngayCol + ") "
                + "VALUES (?, ?, ?, ?, ?)";

        String sqlCT = """
            INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGia)
            VALUES (?, ?, ?, ?)
        """;

        String sqlUpdateTon = """
            UPDATE SanPham
            SET SoLuongTon = SoLuongTon + ?
            WHERE MaSanPham = ?
        """;

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psPN = conn.prepareStatement(sqlPN);
                 PreparedStatement psCT = conn.prepareStatement(sqlCT);
                 PreparedStatement psTon = conn.prepareStatement(sqlUpdateTon)) {

                psPN.setString(1, maPhieuNhap);
                psPN.setString(2, maNCC);
                psPN.setString(3, maNV);
                psPN.setDouble(4, Double.parseDouble(tongTien.replace(",", "").trim()));
                psPN.setString(5, ngayNhap);
                psPN.executeUpdate();

                for (int i = 0; i < itemModel.getRowCount(); i++) {
                    Object maSpObj = itemModel.getValueAt(i, 0);
                    if (maSpObj == null || String.valueOf(maSpObj).trim().isEmpty()) {
                        continue;
                    }

                    String maSP = extractCode(String.valueOf(maSpObj));
                    Object soLuongObj = itemModel.getValueAt(i, 2);
                    Object donGiaObj = itemModel.getValueAt(i, 3);

                    if (soLuongObj == null || String.valueOf(soLuongObj).trim().isEmpty()) {
                        throw new IllegalArgumentException("Dòng " + (i + 1) + " chưa nhập số lượng.");
                    }

                    if (donGiaObj == null || String.valueOf(donGiaObj).trim().isEmpty()) {
                        throw new IllegalArgumentException("Dòng " + (i + 1) + " chưa nhập đơn giá.");
                    }

                    int soLuong = Integer.parseInt(String.valueOf(soLuongObj).trim());
                    double donGia = Double.parseDouble(String.valueOf(donGiaObj).replace(",", "").trim());

                    psCT.setString(1, maPhieuNhap);
                    psCT.setString(2, maSP);
                    psCT.setInt(3, soLuong);
                    psCT.setDouble(4, donGia);
                    psCT.executeUpdate();

                    psTon.setInt(1, soLuong);
                    psTon.setString(2, maSP);
                    psTon.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maPhieuNhap) {
        String sqlGetCT = "SELECT MaSanPham, SoLuong FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        String sqlTruTon = "UPDATE SanPham SET SoLuongTon = SoLuongTon - ? WHERE MaSanPham = ?";
        String sqlDeleteCT = "DELETE FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        String sqlDeletePN = "DELETE FROM PhieuNhap WHERE MaPhieuNhap = ?";

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetCT)) {
                    psGet.setString(1, maPhieuNhap);
                    try (ResultSet rs = psGet.executeQuery()) {
                        while (rs.next()) {
                            try (PreparedStatement psTon = conn.prepareStatement(sqlTruTon)) {
                                psTon.setInt(1, rs.getInt("SoLuong"));
                                psTon.setString(2, rs.getString("MaSanPham"));
                                psTon.executeUpdate();
                            }
                        }
                    }
                }

                try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteCT);
                     PreparedStatement ps2 = conn.prepareStatement(sqlDeletePN)) {

                    ps1.setString(1, maPhieuNhap);
                    ps1.executeUpdate();

                    ps2.setString(1, maPhieuNhap);
                    ps2.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePhieuNhap(String maPhieuNhap, String maNCC, String maNV, String ngayNhap,
                                String tongTien, DefaultTableModel itemModel) {

        String ngayCol = getExistingColumn("PhieuNhap", "NgayNhap", "ThoiGian");
        if (ngayCol == null) {
            ngayCol = "NgayNhap";
        }

        String sqlGetOldCT = "SELECT MaSanPham, SoLuong FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        String sqlTruTonCu = "UPDATE SanPham SET SoLuongTon = SoLuongTon - ? WHERE MaSanPham = ?";
        String sqlUpdatePN = "UPDATE PhieuNhap SET MaNhaCungCap = ?, MaNhanVien = ?, TongTien = ?, " + ngayCol + " = ? WHERE MaPhieuNhap = ?";
        String sqlDeleteCT = "DELETE FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        String sqlInsertCT = """
            INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGia)
            VALUES (?, ?, ?, ?)
        """;
        String sqlCongTonMoi = "UPDATE SanPham SET SoLuongTon = SoLuongTon + ? WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement psOld = conn.prepareStatement(sqlGetOldCT);
                    PreparedStatement psTruTon = conn.prepareStatement(sqlTruTonCu)) {

                    psOld.setString(1, maPhieuNhap);
                    try (ResultSet rs = psOld.executeQuery()) {
                        while (rs.next()) {
                            psTruTon.setInt(1, rs.getInt("SoLuong"));
                            psTruTon.setString(2, rs.getString("MaSanPham"));
                            psTruTon.executeUpdate();
                        }
                    }
                }

                try (PreparedStatement psUpdatePN = conn.prepareStatement(sqlUpdatePN);
                    PreparedStatement psDeleteCT = conn.prepareStatement(sqlDeleteCT);
                    PreparedStatement psInsertCT = conn.prepareStatement(sqlInsertCT);
                    PreparedStatement psCongTon = conn.prepareStatement(sqlCongTonMoi)) {

                    psUpdatePN.setString(1, maNCC);
                    psUpdatePN.setString(2, maNV);
                    psUpdatePN.setDouble(3, Double.parseDouble(tongTien.replace(",", "").trim()));
                    psUpdatePN.setString(4, ngayNhap);
                    psUpdatePN.setString(5, maPhieuNhap);
                    psUpdatePN.executeUpdate();

                    psDeleteCT.setString(1, maPhieuNhap);
                    psDeleteCT.executeUpdate();

                    for (int i = 0; i < itemModel.getRowCount(); i++) {
                        Object maSpObj = itemModel.getValueAt(i, 0);
                        if (maSpObj == null || String.valueOf(maSpObj).trim().isEmpty()) {
                            continue;
                        }

                        String maSP = extractCode(String.valueOf(maSpObj));
                        int soLuong = Integer.parseInt(String.valueOf(itemModel.getValueAt(i, 2)).trim());
                        double donGia = Double.parseDouble(
                                String.valueOf(itemModel.getValueAt(i, 3)).replace(",", "").trim()
                        );

                        psInsertCT.setString(1, maPhieuNhap);
                        psInsertCT.setString(2, maSP);
                        psInsertCT.setInt(3, soLuong);
                        psInsertCT.setDouble(4, donGia);
                        psInsertCT.executeUpdate();

                        psCongTon.setInt(1, soLuong);
                        psCongTon.setString(2, maSP);
                        psCongTon.executeUpdate();
                    }
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static final java.util.Map<String, String> COLUMN_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    /** Kết quả được cache vì cấu trúc bảng không đổi khi chương trình đang chạy. */
    private String getExistingColumn(String tableName, String... candidates) {
        String key = tableName + ":" + String.join(",", candidates);
        String cached = COLUMN_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        String found = lookupExistingColumn(tableName, candidates);
        if (found != null) {
            COLUMN_CACHE.put(key, found);
        }
        return found;
    }

    private String lookupExistingColumn(String tableName, String... candidates) {
        try (Connection conn = DBConnection.open()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, tableName, null)) {
                List<String> cols = new ArrayList<>();
                while (rs.next()) {
                    cols.add(rs.getString("COLUMN_NAME"));
                }
                for (String c : candidates) {
                    for (String real : cols) {
                        if (real.equalsIgnoreCase(c)) {
                            return real;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractCode(String value) {
        int idx = value.indexOf(" - ");
        return idx >= 0 ? value.substring(0, idx).trim() : value.trim();
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && a.equalsIgnoreCase(b);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }


}