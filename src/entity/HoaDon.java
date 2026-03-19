package entity;

import java.sql.Timestamp;

public class HoaDon {
    private String maHD;
    private String maKH;
    private String maNV;
    private int tongTien;
    private Timestamp thoiGian;

    public HoaDon() {
    }

    public HoaDon(String maHD, String maKH, String maNV, int tongTien, Timestamp thoiGian) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.maNV = maNV;
        this.tongTien = tongTien;
        this.thoiGian = thoiGian;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public Timestamp getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Timestamp thoiGian) {
        this.thoiGian = thoiGian;
    }
}