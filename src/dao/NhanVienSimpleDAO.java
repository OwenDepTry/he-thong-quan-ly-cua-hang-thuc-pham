package dao;

import config.DBConnection;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienSimpleDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE TrangThai = 'active' ORDER BY MaNV";

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
}