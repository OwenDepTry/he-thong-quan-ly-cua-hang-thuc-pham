package dao;

import config.DBConnection;
import entity.HangHoa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangHoaDAO {

    public List<HangHoa> findAll() {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT * FROM HangHoa";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HangHoa h = new HangHoa();
                h.setMaHang(rs.getString("MaHang"));
                h.setMaSanPham(rs.getString("MaSanPham"));
                h.setSoLuong(rs.getInt("SoLuong"));
                h.setNgayNhap(rs.getString("NgayNhap"));
                h.setHanSuDung(rs.getString("HanSuDung"));
                list.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}