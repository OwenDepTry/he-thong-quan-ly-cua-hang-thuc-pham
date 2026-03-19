package dao;

import config.DBConnection;
import entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO {

    public User login(String maNV, String matKhau) {
        String sql = "SELECT * FROM nhanvien WHERE MaNV = ? AND MatKhau = ?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV.trim());
            ps.setString(2, matKhau.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // DEBUG
                System.out.println("FOUND USER: " + rs.getString("MaNV"));

                if (!"active".equalsIgnoreCase(rs.getString("TrangThai"))) {
                    System.out.println("USER NOT ACTIVE");
                    return null;
                }

                String hoTen = rs.getString("Ho") + " "
                        + (rs.getString("TenLot") == null ? "" : rs.getString("TenLot")) + " "
                        + rs.getString("Ten");

                return new User(
                        rs.getString("MaNV"),
                        hoTen.trim().replaceAll("\\s+", " "),
                        rs.getString("ChucVu"),
                        rs.getString("TrangThai")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}