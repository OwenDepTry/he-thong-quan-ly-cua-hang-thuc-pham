package dao;

import config.DBConnection;
import entity.NhaCungCap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapSimpleDAO {

    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap WHERE TrangThai = 'active' ORDER BY MaNCCap";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new NhaCungCap(
                        rs.getString("MaNCCap"),
                        rs.getString("TenNCCap"),
                        rs.getString("TenLienHe"),
                        rs.getString("SDThoai"),
                        rs.getInt("TinhThanh"),
                        rs.getString("DiaChi"),
                        rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}