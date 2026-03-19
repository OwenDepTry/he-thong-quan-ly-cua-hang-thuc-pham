package entity;

import java.sql.Date;

public class KhachHang {
    private String maKH;
    private String ho;
    private String tenLot;
    private String ten;
    private String phai;
    private Date ngaySinh;
    private String sdt;
    private int tinhThanh;
    private String diaChi;
    private Date ngayThamGia;
    private int diem;
    private String trangThai;

    public KhachHang() {
    }

    public KhachHang(String maKH, String ho, String tenLot, String ten, String phai, Date ngaySinh,
                     String sdt, int tinhThanh, String diaChi, Date ngayThamGia, int diem, String trangThai) {
        this.maKH = maKH;
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.phai = phai;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.tinhThanh = tinhThanh;
        this.diaChi = diaChi;
        this.ngayThamGia = ngayThamGia;
        this.diem = diem;
        this.trangThai = trangThai;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTenLot() {
        return tenLot;
    }

    public void setTenLot(String tenLot) {
        this.tenLot = tenLot;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public int getTinhThanh() {
        return tinhThanh;
    }

    public void setTinhThanh(int tinhThanh) {
        this.tinhThanh = tinhThanh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Date getNgayThamGia() {
        return ngayThamGia;
    }

    public void setNgayThamGia(Date ngayThamGia) {
        this.ngayThamGia = ngayThamGia;
    }

    public int getDiem() {
        return diem;
    }

    public void setDiem(int diem) {
        this.diem = diem;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getHoTenDayDu() {
        return (ho + " " + (tenLot == null ? "" : tenLot) + " " + ten).trim().replaceAll("\\s+", " ");
    }
}