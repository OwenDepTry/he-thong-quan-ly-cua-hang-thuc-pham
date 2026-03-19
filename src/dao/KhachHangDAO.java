package dao;

import config.DBConnection;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang ORDER BY MaKH";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new KhachHang(
                        rs.getString("MaKH"),
                        rs.getString("Ho"),
                        rs.getString("TenLot"),
                        rs.getString("Ten"),
                        rs.getString("Phai"),
                        rs.getDate("NgaySinh"),
                        rs.getString("SDT"),
                        rs.getInt("TinhThanh"),
                        rs.getString("DiaChi"),
                        rs.getDate("NgayThamGia"),
                        rs.getInt("Diem"),
                        rs.getString("TrangThai")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<KhachHang> searchByKeyword(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang WHERE MaKH LIKE ? OR Ho LIKE ? OR Ten LIKE ? OR SDT LIKE ? ORDER BY MaKH";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new KhachHang(
                            rs.getString("MaKH"),
                            rs.getString("Ho"),
                            rs.getString("TenLot"),
                            rs.getString("Ten"),
                            rs.getString("Phai"),
                            rs.getDate("NgaySinh"),
                            rs.getString("SDT"),
                            rs.getInt("TinhThanh"),
                            rs.getString("DiaChi"),
                            rs.getDate("NgayThamGia"),
                            rs.getInt("Diem"),
                            rs.getString("TrangThai")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO khachhang (MaKH, Ho, TenLot, Ten, Phai, NgaySinh, SDT, TinhThanh, DiaChi, NgayThamGia, Diem, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHo());
            ps.setString(3, kh.getTenLot());
            ps.setString(4, kh.getTen());
            ps.setString(5, kh.getPhai());
            ps.setDate(6, kh.getNgaySinh());
            ps.setString(7, kh.getSdt());
            ps.setInt(8, kh.getTinhThanh());
            ps.setString(9, kh.getDiaChi());
            ps.setDate(10, kh.getNgayThamGia());
            ps.setInt(11, kh.getDiem());
            ps.setString(12, kh.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE khachhang SET Ho=?, TenLot=?, Ten=?, Phai=?, NgaySinh=?, SDT=?, TinhThanh=?, DiaChi=?, NgayThamGia=?, Diem=?, TrangThai=? WHERE MaKH=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getHo());
            ps.setString(2, kh.getTenLot());
            ps.setString(3, kh.getTen());
            ps.setString(4, kh.getPhai());
            ps.setDate(5, kh.getNgaySinh());
            ps.setString(6, kh.getSdt());
            ps.setInt(7, kh.getTinhThanh());
            ps.setString(8, kh.getDiaChi());
            ps.setDate(9, kh.getNgayThamGia());
            ps.setInt(10, kh.getDiem());
            ps.setString(11, kh.getTrangThai());
            ps.setString(12, kh.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM khachhang WHERE MaKH=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}