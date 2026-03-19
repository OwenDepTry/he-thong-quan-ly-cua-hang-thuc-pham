package entity;

public class ChiTietPNhap {
    private String maPhieu;
    private String maSP;
    private int soLuong;
    private int donGia;

    public ChiTietPNhap() {
    }

    public ChiTietPNhap(String maPhieu, String maSP, int soLuong, int donGia) {
        this.maPhieu = maPhieu;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
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

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public int getThanhTien() {
        return soLuong * donGia;
    }
}