package dao;

import config.DBConnection;
import entity.Loai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LoaiDAO {

    public List<Loai> getAll() {
        List<Loai> list = new ArrayList<>();
        String sql = "SELECT MaLoai, TenLoai FROM loai ORDER BY MaLoai";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Loai(
                        rs.getInt("MaLoai"),
                        rs.getString("TenLoai")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}