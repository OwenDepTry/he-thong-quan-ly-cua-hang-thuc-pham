package entity;

public class NhaCungCap {

    private String maNhaCungCap;
    private String ten;
    private String tenLienHe;
    private String soDienThoai;
    private String tinh;
    private String diaChi;
    private String trangThai;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNhaCungCap, String ten, String tenLienHe,
                      String soDienThoai, String tinh, String diaChi, String trangThai) {
        this.maNhaCungCap = maNhaCungCap;
        this.ten = ten;
        this.tenLienHe = tenLienHe;
        this.soDienThoai = soDienThoai;
        this.tinh = tinh;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
    }

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTenLienHe() {
        return tenLienHe;
    }

    public void setTenLienHe(String tenLienHe) {
        this.tenLienHe = tenLienHe;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}