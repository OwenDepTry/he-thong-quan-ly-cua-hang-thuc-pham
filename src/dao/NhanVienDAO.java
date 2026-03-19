package dao;

import config.DBConnection;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien ORDER BY MaNV";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new NhanVien(
                        rs.getString("MaNV"),
                        rs.getString("Ho"),
                        rs.getString("TenLot"),
                        rs.getString("Ten"),
                        rs.getString("Phai"),
                        rs.getDate("NgaySinh"),
                        rs.getString("SDT"),
                        rs.getInt("TinhThanh"),
                        rs.getString("DiaChi"),
                        rs.getInt("Luong"),
                        rs.getString("ChucVu"),
                        rs.getString("TrangThai"),
                        rs.getString("MatKhau")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<NhanVien> searchByKeyword(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE MaNV LIKE ? OR Ho LIKE ? OR Ten LIKE ? OR SDT LIKE ? ORDER BY MaNV";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new NhanVien(
                            rs.getString("MaNV"),
                            rs.getString("Ho"),
                            rs.getString("TenLot"),
                            rs.getString("Ten"),
                            rs.getString("Phai"),
                            rs.getDate("NgaySinh"),
                            rs.getString("SDT"),
                            rs.getInt("TinhThanh"),
                            rs.getString("DiaChi"),
                            rs.getInt("Luong"),
                            rs.getString("ChucVu"),
                            rs.getString("TrangThai"),
                            rs.getString("MatKhau")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO nhanvien (MaNV, Ho, TenLot, Ten, Phai, NgaySinh, SDT, TinhThanh, DiaChi, Luong, ChucVu, TrangThai, MatKhau) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHo());
            ps.setString(3, nv.getTenLot());
            ps.setString(4, nv.getTen());
            ps.setString(5, nv.getPhai());
            ps.setDate(6, nv.getNgaySinh());
            ps.setString(7, nv.getSdt());
            ps.setInt(8, nv.getTinhThanh());
            ps.setString(9, nv.getDiaChi());
            ps.setInt(10, nv.getLuong());
            ps.setString(11, nv.getChucVu());
            ps.setString(12, nv.getTrangThai());
            ps.setString(13, nv.getMatKhau());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE nhanvien SET Ho=?, TenLot=?, Ten=?, Phai=?, NgaySinh=?, SDT=?, TinhThanh=?, DiaChi=?, Luong=?, ChucVu=?, TrangThai=?, MatKhau=? WHERE MaNV=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHo());
            ps.setString(2, nv.getTenLot());
            ps.setString(3, nv.getTen());
            ps.setString(4, nv.getPhai());
            ps.setDate(5, nv.getNgaySinh());
            ps.setString(6, nv.getSdt());
            ps.setInt(7, nv.getTinhThanh());
            ps.setString(8, nv.getDiaChi());
            ps.setInt(9, nv.getLuong());
            ps.setString(10, nv.getChucVu());
            ps.setString(11, nv.getTrangThai());
            ps.setString(12, nv.getMatKhau());
            ps.setString(13, nv.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM nhanvien WHERE MaNV=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}