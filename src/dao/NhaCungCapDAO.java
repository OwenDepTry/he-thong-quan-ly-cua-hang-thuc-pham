package dao;

import config.DBConnection;
import entity.NhaCungCap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap ORDER BY MaNCCap";

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

    public List<NhaCungCap> searchByKeyword(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap WHERE MaNCCap LIKE ? OR TenNCCap LIKE ? OR TenLienHe LIKE ? OR SDThoai LIKE ? ORDER BY MaNCCap";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);

            try (ResultSet rs = ps.executeQuery()) {
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(NhaCungCap ncc) {
        String sql = "INSERT INTO nhacungcap (MaNCCap, TenNCCap, TenLienHe, SDThoai, TinhThanh, DiaChi, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getMaNCCap());
            ps.setString(2, ncc.getTenNCCap());
            ps.setString(3, ncc.getTenLienHe());
            ps.setString(4, ncc.getsDThoai());
            ps.setInt(5, ncc.getTinhThanh());
            ps.setString(6, ncc.getDiaChi());
            ps.setString(7, ncc.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(NhaCungCap ncc) {
        String sql = "UPDATE nhacungcap SET TenNCCap=?, TenLienHe=?, SDThoai=?, TinhThanh=?, DiaChi=?, TrangThai=? WHERE MaNCCap=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenNCCap());
            ps.setString(2, ncc.getTenLienHe());
            ps.setString(3, ncc.getsDThoai());
            ps.setInt(4, ncc.getTinhThanh());
            ps.setString(5, ncc.getDiaChi());
            ps.setString(6, ncc.getTrangThai());
            ps.setString(7, ncc.getMaNCCap());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNCCap) {
        String sql = "DELETE FROM nhacungcap WHERE MaNCCap=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNCCap);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}