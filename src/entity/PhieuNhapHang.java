package entity;

import java.sql.Timestamp;

public class PhieuNhapHang {
    private String maPhieu;
    private String maNCCap;
    private String nguoiNhap;
    private int tongTien;
    private Timestamp thoiGian;

    public PhieuNhapHang() {
    }

    public PhieuNhapHang(String maPhieu, String maNCCap, String nguoiNhap, int tongTien, Timestamp thoiGian) {
        this.maPhieu = maPhieu;
        this.maNCCap = maNCCap;
        this.nguoiNhap = nguoiNhap;
        this.tongTien = tongTien;
        this.thoiGian = thoiGian;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getMaNCCap() {
        return maNCCap;
    }

    public void setMaNCCap(String maNCCap) {
        this.maNCCap = maNCCap;
    }

    public String getNguoiNhap() {
        return nguoiNhap;
    }

    public void setNguoiNhap(String nguoiNhap) {
        this.nguoiNhap = nguoiNhap;
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