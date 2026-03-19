package entity;

public class NhaCungCap {
    private String maNCCap;
    private String tenNCCap;
    private String tenLienHe;
    private String sDThoai;
    private int tinhThanh;
    private String diaChi;
    private String trangThai;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNCCap, String tenNCCap, String tenLienHe, String sDThoai,
                      int tinhThanh, String diaChi, String trangThai) {
        this.maNCCap = maNCCap;
        this.tenNCCap = tenNCCap;
        this.tenLienHe = tenLienHe;
        this.sDThoai = sDThoai;
        this.tinhThanh = tinhThanh;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
    }

    public String getMaNCCap() {
        return maNCCap;
    }

    public void setMaNCCap(String maNCCap) {
        this.maNCCap = maNCCap;
    }

    public String getTenNCCap() {
        return tenNCCap;
    }

    public void setTenNCCap(String tenNCCap) {
        this.tenNCCap = tenNCCap;
    }

    public String getTenLienHe() {
        return tenLienHe;
    }

    public void setTenLienHe(String tenLienHe) {
        this.tenLienHe = tenLienHe;
    }

    public String getsDThoai() {
        return sDThoai;
    }

    public void setsDThoai(String sDThoai) {
        this.sDThoai = sDThoai;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}