package dao;

import config.DBConnection;
import entity.DonVi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DonViDAO {

    public List<DonVi> getAll() {
        List<DonVi> list = new ArrayList<>();
        String sql = "SELECT MaDonVi, TenDonVi FROM donvi ORDER BY MaDonVi";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DonVi(
                        rs.getInt("MaDonVi"),
                        rs.getString("TenDonVi")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}