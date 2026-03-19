package dao;

import config.DBConnection;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangSimpleDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang WHERE TrangThai = 'active' ORDER BY MaKH";

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
}