package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class HoaDonDAO {

    private static final Pattern DIEU_KIEN_PATTERN = Pattern.compile("^\\s*(>=|>|=)?\\s*([\\d.,]+)");

    private String lastError;

    /** Lý do thất bại của lần insert/update/delete gần nhất (null nếu thành công). */
    public String getLastError() {
        return lastError;
    }

    public List<Object[]> findAllForTable() {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT hd.MaHoaDon,
                   hd.MaKhachHang,
                   COALESCE(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten), '') AS TenKhachHang,
                   hd.MaNhanVien,
                   COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '') AS TenNhanVien,
                   hd.TongTien,
                   hd.TienGiam,
                   (hd.TongTien - hd.TienGiam) AS ThanhTien,
                   hd.ThoiGian,
                   COALESCE(hd.MaKhuyenMai, '') AS MaKhuyenMai
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            ORDER BY CAST(SUBSTRING(hd.MaHoaDon, 3) AS UNSIGNED) DESC, hd.MaHoaDon DESC
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaHoaDon"),
                    rs.getString("MaKhachHang"),
                    rs.getString("TenKhachHang").trim(),
                    rs.getString("MaNhanVien"),
                    rs.getString("TenNhanVien").trim(),
                    rs.getDouble("TongTien"),
                    rs.getDouble("TienGiam"),
                    rs.getDouble("ThanhTien"),
                    rs.getString("ThoiGian"),
                    rs.getString("MaKhuyenMai")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> search(String keyword) {
        return search(null, keyword);
    }

    public List<Object[]> search(String field, String keyword) {
        List<Object[]> list = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (normalizedKeyword.isEmpty()) {
            return findAllForTable();
        }

        String column = resolveSearchColumn(field);

        String sql = """
            SELECT hd.MaHoaDon,
                   hd.MaKhachHang,
                   COALESCE(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten), '') AS TenKhachHang,
                   hd.MaNhanVien,
                   COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '') AS TenNhanVien,
                   hd.TongTien,
                   hd.TienGiam,
                   (hd.TongTien - hd.TienGiam) AS ThanhTien,
                   hd.ThoiGian,
                   COALESCE(hd.MaKhuyenMai, '') AS MaKhuyenMai
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            WHERE %s LIKE ?
            ORDER BY CAST(SUBSTRING(hd.MaHoaDon, 3) AS UNSIGNED) DESC, hd.MaHoaDon DESC
        """.formatted(column);

        String k = "%" + normalizedKeyword + "%";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaHoaDon"),
                        rs.getString("MaKhachHang"),
                        rs.getString("TenKhachHang").trim(),
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien").trim(),
                        rs.getDouble("TongTien"),
                        rs.getDouble("TienGiam"),
                        rs.getDouble("ThanhTien"),
                        rs.getString("ThoiGian"),
                        rs.getString("MaKhuyenMai")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> searchByNhanVien(String keyword, String maNhanVien) {
        return searchByNhanVien(null, keyword, maNhanVien);
    }

    public List<Object[]> searchByNhanVien(String field, String keyword, String maNhanVien) {
        List<Object[]> list = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (normalizedKeyword.isEmpty()) {
            return findAllForTableByNhanVien(maNhanVien);
        }

        String column = resolveSearchColumn(field);
        String sql = """
            SELECT hd.MaHoaDon,
                hd.MaKhachHang,
                COALESCE(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten), '') AS TenKhachHang,
                hd.MaNhanVien,
                COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '') AS TenNhanVien,
                hd.TongTien,
                hd.TienGiam,
                (hd.TongTien - hd.TienGiam) AS ThanhTien,
                hd.ThoiGian,
                COALESCE(hd.MaKhuyenMai, '') AS MaKhuyenMai
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            WHERE hd.MaNhanVien = ?
              AND %s LIKE ?
            ORDER BY CAST(SUBSTRING(hd.MaHoaDon, 3) AS UNSIGNED) DESC, hd.MaHoaDon DESC
        """.formatted(column);

        String k = "%" + normalizedKeyword + "%";
        try (Connection conn = DBConnection.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);
            ps.setString(2, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaHoaDon"),
                        rs.getString("MaKhachHang"),
                        rs.getString("TenKhachHang").trim(),
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien").trim(),
                        rs.getDouble("TongTien"),
                        rs.getDouble("TienGiam"),
                        rs.getDouble("ThanhTien"),
                        rs.getString("ThoiGian"),
                        rs.getString("MaKhuyenMai")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> findDetailsByHoaDon(String maHoaDon) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT ct.MaSanPham, sp.TenSanPham, ct.SoLuong, ct.DonGia,
                   (ct.SoLuong * ct.DonGia) AS ThanhTien
            FROM ChiTietHoaDon ct
            JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham
            WHERE ct.MaHoaDon = ?
        """;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);

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

    public List<String> getKhachHangOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaKhachHang, Ho, TenLot, Ten FROM KhachHang WHERE TrangThai = 'active'";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(
                    rs.getString("MaKhachHang") + " - "
                    + rs.getString("Ho") + " "
                    + rs.getString("TenLot") + " "
                    + rs.getString("Ten")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getNhanVienOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaNhanVien, Ho, TenLot, Ten FROM NhanVien WHERE TrangThai = 'active'";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(
                    rs.getString("MaNhanVien") + " - "
                    + rs.getString("Ho") + " "
                    + rs.getString("TenLot") + " "
                    + rs.getString("Ten")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getKhuyenMaiOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaKhuyenMai, TenKhuyenMai FROM KhuyenMai "
                + "WHERE TrangThai = 'active' AND CURDATE() BETWEEN NgayBatDau AND NgayKetThuc";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaKhuyenMai") + " - " + rs.getString("TenKhuyenMai"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSanPhamOptions() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaSanPham, TenSanPham FROM SanPham";

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
                    return rs.getString("TenSanPham");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public double getGiaSanPham(String maSP) {
        String sql = "SELECT Gia FROM SanPham WHERE MaSanPham = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Gia");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getPhanTramKhuyenMai(String maKM) {
        String sql = "SELECT PhanTramGiam FROM KhuyenMai WHERE MaKhuyenMai = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("PhanTramGiam");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean exists(String maHoaDon) {
        String sql = "SELECT 1 FROM HoaDon WHERE MaHoaDon = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaHoaDon() {
        String sql = "SELECT MaHoaDon FROM HoaDon WHERE MaHoaDon LIKE 'HD%'";
        int max = 0;

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String ma = rs.getString("MaHoaDon");
                if (ma == null) {
                    continue;
                }

                ma = ma.trim().toUpperCase();
                if (!ma.startsWith("HD")) {
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

        return "HD" + (max + 1);
    }

    public boolean insertHoaDon(String maHoaDon, String maKH, String maNV, String thoiGian,
                                String maKM, String tongTien, String tienGiam,
                                DefaultTableModel itemModel) {
        String sqlHoaDon = """
            INSERT INTO HoaDon (MaHoaDon, MaKhachHang, MaNhanVien, TongTien, TienGiam, ThoiGian, MaKhuyenMai)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        String sqlChiTiet = """
            INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHD = conn.prepareStatement(sqlHoaDon);
                PreparedStatement psCT = conn.prepareStatement(sqlChiTiet)) {

                psHD.setString(1, maHoaDon);
                psHD.setString(2, maKH);
                psHD.setString(3, maNV);
                psHD.setDouble(4, Double.parseDouble(tongTien.replace(",", "").trim()));
                psHD.setDouble(5, Double.parseDouble(tienGiam.replace(",", "").trim()));
                psHD.setString(6, thoiGian);

                if (maKM == null || maKM.trim().isEmpty()) {
                    psHD.setNull(7, java.sql.Types.VARCHAR);
                } else {
                    psHD.setString(7, maKM);
                }

                kiemTraKhuyenMaiHopLe(conn, maKM,
                        Double.parseDouble(tongTien.replace(",", "").trim()), thoiGian);

                psHD.executeUpdate();

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

                    psCT.setString(1, maHoaDon);
                    psCT.setString(2, maSP);
                    psCT.setInt(3, soLuong);
                    psCT.setDouble(4, donGia);
                    psCT.executeUpdate();

                    truTonKho(conn, maSP, soLuong);
                }

                conn.commit();
                lastError = null;
                return true;

            } catch (Exception e) {
                conn.rollback();
                lastError = e.getMessage();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maHoaDon) {
        String sqlCT = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        String sqlHD = "DELETE FROM HoaDon WHERE MaHoaDon = ?";

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCT = conn.prepareStatement(sqlCT);
                 PreparedStatement psHD = conn.prepareStatement(sqlHD)) {

                hoanTonKho(conn, maHoaDon);

                psCT.setString(1, maHoaDon);
                psCT.executeUpdate();

                psHD.setString(1, maHoaDon);
                psHD.executeUpdate();

                conn.commit();
                lastError = null;
                return true;

            } catch (Exception e) {
                conn.rollback();
                lastError = e.getMessage();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateHoaDon(String maHoaDon, String maKH, String maNV, String thoiGian,
                                String maKM, String tongTien, String tienGiam,
                                DefaultTableModel itemModel) {
        String sqlHD = """
            UPDATE HoaDon
            SET MaKhachHang = ?, MaNhanVien = ?, TongTien = ?, TienGiam = ?, ThoiGian = ?, MaKhuyenMai = ?
            WHERE MaHoaDon = ?
        """;

        String sqlDeleteCT = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";

        String sqlInsertCT = """
            INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.open()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHD = conn.prepareStatement(sqlHD);
                PreparedStatement psDeleteCT = conn.prepareStatement(sqlDeleteCT);
                PreparedStatement psInsertCT = conn.prepareStatement(sqlInsertCT)) {

                psHD.setString(1, maKH);
                psHD.setString(2, maNV);
                psHD.setDouble(3, Double.parseDouble(tongTien.replace(",", "").trim()));
                psHD.setDouble(4, Double.parseDouble(tienGiam.replace(",", "").trim()));
                psHD.setString(5, thoiGian);

                if (maKM == null || maKM.trim().isEmpty()) {
                    psHD.setNull(6, java.sql.Types.VARCHAR);
                } else {
                    psHD.setString(6, maKM);
                }

                kiemTraKhuyenMaiHopLe(conn, maKM,
                        Double.parseDouble(tongTien.replace(",", "").trim()), thoiGian);

                psHD.setString(7, maHoaDon);
                psHD.executeUpdate();

                // Hoàn lại tồn kho theo chi tiết cũ trước khi ghi chi tiết mới
                hoanTonKho(conn, maHoaDon);

                psDeleteCT.setString(1, maHoaDon);
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

                    psInsertCT.setString(1, maHoaDon);
                    psInsertCT.setString(2, maSP);
                    psInsertCT.setInt(3, soLuong);
                    psInsertCT.setDouble(4, donGia);
                    psInsertCT.executeUpdate();

                    truTonKho(conn, maSP, soLuong);
                }

                conn.commit();
                lastError = null;
                return true;

            } catch (Exception e) {
                conn.rollback();
                lastError = e.getMessage();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Trừ tồn kho; báo lỗi nếu không đủ hàng (để rollback cả hóa đơn). */
    private void truTonKho(Connection conn, String maSP, int soLuong) throws SQLException {
        String sql = "UPDATE SanPham SET SoLuongTon = SoLuongTon - ? WHERE MaSanPham = ? AND SoLuongTon >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSP);
            ps.setInt(3, soLuong);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Sản phẩm " + maSP + " không đủ tồn kho (cần " + soLuong
                        + ", còn " + getSoLuongTon(conn, maSP) + ").");
            }
        }
    }

    /** Cộng lại tồn kho theo toàn bộ chi tiết hiện có của hóa đơn. */
    private void hoanTonKho(Connection conn, String maHoaDon) throws SQLException {
        String sql = """
            UPDATE SanPham sp
            JOIN ChiTietHoaDon ct ON ct.MaSanPham = sp.MaSanPham
            SET sp.SoLuongTon = sp.SoLuongTon + ct.SoLuong
            WHERE ct.MaHoaDon = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.executeUpdate();
        }
    }

    private int getSoLuongTon(Connection conn, String maSP) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SoLuongTon FROM SanPham WHERE MaSanPham = ?")) {
            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Kiểm tra khuyến mãi có áp dụng được không: đang active, ngày hóa đơn nằm trong
     * khoảng hiệu lực, tổng tiền đạt điều kiện. Trả về null nếu hợp lệ, ngược lại là lý do.
     */
    public String kiemTraKhuyenMai(String maKM, double tongTien, String ngayHoaDon) {
        if (maKM == null || maKM.isBlank()) {
            return null;
        }
        try (Connection conn = DBConnection.open()) {
            return lyDoKhuyenMaiKhongHopLe(conn, maKM, tongTien, ngayHoaDon);
        } catch (Exception e) {
            e.printStackTrace();
            return "Không kiểm tra được khuyến mãi: " + e.getMessage();
        }
    }

    private void kiemTraKhuyenMaiHopLe(Connection conn, String maKM, double tongTien, String ngayHoaDon)
            throws SQLException {
        if (maKM == null || maKM.isBlank()) {
            return;
        }
        String lyDo = lyDoKhuyenMaiKhongHopLe(conn, maKM, tongTien, ngayHoaDon);
        if (lyDo != null) {
            throw new SQLException(lyDo);
        }
    }

    private String lyDoKhuyenMaiKhongHopLe(Connection conn, String maKM, double tongTien, String ngayHoaDon)
            throws SQLException {
        String sql = "SELECT DieuKien, NgayBatDau, NgayKetThuc, TrangThai FROM KhuyenMai WHERE MaKhuyenMai = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return "Khuyến mãi " + maKM + " không tồn tại.";
                }
                if (!"active".equalsIgnoreCase(String.valueOf(rs.getString("TrangThai")).trim())) {
                    return "Khuyến mãi " + maKM + " đang ngừng áp dụng.";
                }

                LocalDate ngay;
                try {
                    ngay = LocalDate.parse(ngayHoaDon.trim());
                } catch (Exception e) {
                    return "Ngày hóa đơn không hợp lệ (định dạng yyyy-MM-dd).";
                }
                java.sql.Date bd = rs.getDate("NgayBatDau");
                java.sql.Date kt = rs.getDate("NgayKetThuc");
                if (bd != null && ngay.isBefore(bd.toLocalDate())) {
                    return "Khuyến mãi " + maKM + " chưa bắt đầu (từ " + bd + ").";
                }
                if (kt != null && ngay.isAfter(kt.toLocalDate())) {
                    return "Khuyến mãi " + maKM + " đã hết hạn (đến " + kt + ").";
                }

                String dieuKien = rs.getString("DieuKien");
                Matcher m = dieuKien == null ? null : DIEU_KIEN_PATTERN.matcher(dieuKien);
                if (m != null && m.find()) {
                    double nguong = Double.parseDouble(m.group(2).replace(",", "").replace(".", ""));
                    boolean dat = ">".equals(m.group(1)) ? tongTien > nguong : tongTien >= nguong;
                    if (!dat) {
                        return "Hóa đơn chưa đạt điều kiện khuyến mãi " + maKM + " (" + dieuKien.trim() + ").";
                    }
                }
                return null;
            }
        }
    }

    private String extractCode(String value) {
        int idx = value.indexOf(" - ");
        return idx >= 0 ? value.substring(0, idx).trim() : value.trim();
    }

    private String resolveSearchColumn(String field) {
        if (field == null) {
            return "hd.MaHoaDon";
        }

        switch (field.trim().toLowerCase()) {
            case "mã kh":
            case "ma kh":
                return "hd.MaKhachHang";
            case "mã nv":
            case "ma nv":
                return "hd.MaNhanVien";
            default:
                return "hd.MaHoaDon";
        }
    }

    public List<Object[]> findAllForTableByNhanVien(String maNhanVien) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT hd.MaHoaDon,
                hd.MaKhachHang,
                COALESCE(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten), '') AS TenKhachHang,
                hd.MaNhanVien,
                COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '') AS TenNhanVien,
                hd.TongTien,
                hd.TienGiam,
                (hd.TongTien - hd.TienGiam) AS ThanhTien,
                hd.ThoiGian,
                COALESCE(hd.MaKhuyenMai, '') AS MaKhuyenMai
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            WHERE hd.MaNhanVien = ?
            ORDER BY CAST(SUBSTRING(hd.MaHoaDon, 3) AS UNSIGNED) DESC, hd.MaHoaDon DESC
        """;

        try (Connection conn = DBConnection.open();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("MaHoaDon"),
                        rs.getString("MaKhachHang"),
                        rs.getString("TenKhachHang").trim(),
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien").trim(),
                        rs.getDouble("TongTien"),
                        rs.getDouble("TienGiam"),
                        rs.getDouble("ThanhTien"),
                        rs.getString("ThoiGian"),
                        rs.getString("MaKhuyenMai")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}