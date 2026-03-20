package dao;

import config.DBConnection;
import entity.HangHoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HangHoaDAO {

    public List<HangHoa> getAll() {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT * FROM hang ORDER BY MaHang";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<HangHoa> searchByKeyword(String keyword) {
        List<HangHoa> list = new ArrayList<>();
        String sql = "SELECT * FROM hang WHERE MaHang LIKE ? OR MaSP LIKE ? OR TrangThai LIKE ? ORDER BY MaHang";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(HangHoa hang) {
        String sql = "INSERT INTO hang (MaHang, MaSP, SoLuong, NSX, GiamGia, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hang.getMaHang());
            ps.setString(2, hang.getMaSP());
            ps.setInt(3, hang.getSoLuong());
            ps.setDate(4, hang.getNgaySanXuat());
            ps.setInt(5, hang.getGiamGia());
            ps.setString(6, hang.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(HangHoa hang) {
        String sql = "UPDATE hang SET MaSP=?, SoLuong=?, NSX=?, GiamGia=?, TrangThai=? WHERE MaHang=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hang.getMaSP());
            ps.setInt(2, hang.getSoLuong());
            ps.setDate(3, hang.getNgaySanXuat());
            ps.setInt(4, hang.getGiamGia());
            ps.setString(5, hang.getTrangThai());
            ps.setString(6, hang.getMaHang());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maHang) {
        String sql = "DELETE FROM hang WHERE MaHang=?";

        try (Connection conn = DBConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private HangHoa mapRow(ResultSet rs) throws Exception {
        return new HangHoa(
                rs.getString("MaHang"),
                rs.getString("MaSP"),
                rs.getInt("SoLuong"),
                rs.getDate("NSX"),
                rs.getInt("GiamGia"),
                rs.getString("TrangThai")
        );
    }
}
