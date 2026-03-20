package entity;

import java.sql.Date;

public class HangHoa {
    private String maHang;
    private String maSP;
    private int soLuong;
    private Date ngaySanXuat;
    private int giamGia;
    private String trangThai;

    public HangHoa() {
    }

    public HangHoa(String maHang, String maSP, int soLuong, Date ngaySanXuat, int giamGia, String trangThai) {
        this.maHang = maHang;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.ngaySanXuat = ngaySanXuat;
        this.giamGia = giamGia;
        this.trangThai = trangThai;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Date getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(Date ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public int getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(int giamGia) {
        this.giamGia = giamGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
