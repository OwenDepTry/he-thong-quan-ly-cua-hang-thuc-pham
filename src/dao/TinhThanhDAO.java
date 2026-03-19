package dao;

import config.DBConnection;
import entity.TinhThanh;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TinhThanhDAO {

    public List<TinhThanh> getAll() {
        List<TinhThanh> list = new ArrayList<>();
        String sql = "SELECT MaTThanh, TenTThanh FROM tinhthanh ORDER BY MaTThanh";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TinhThanh(
                        rs.getInt("MaTThanh"),
                        rs.getString("TenTThanh")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}