package entity;

import java.sql.Date;

public class NhanVien {
    private String maNV;
    private String ho;
    private String tenLot;
    private String ten;
    private String phai;
    private Date ngaySinh;
    private String sdt;
    private int tinhThanh;
    private String diaChi;
    private int luong;
    private String chucVu;
    private String trangThai;
    private String matKhau;

    public NhanVien() {
    }

    public NhanVien(String maNV, String ho, String tenLot, String ten, String phai, Date ngaySinh,
                    String sdt, int tinhThanh, String diaChi, int luong, String chucVu,
                    String trangThai, String matKhau) {
        this.maNV = maNV;
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.phai = phai;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.tinhThanh = tinhThanh;
        this.diaChi = diaChi;
        this.luong = luong;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
        this.matKhau = matKhau;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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

    public int getLuong() {
        return luong;
    }

    public void setLuong(int luong) {
        this.luong = luong;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTenDayDu() {
        return (ho + " " + (tenLot == null ? "" : tenLot) + " " + ten).trim().replaceAll("\\s+", " ");
    }
}