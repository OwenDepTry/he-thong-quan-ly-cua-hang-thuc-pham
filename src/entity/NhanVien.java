package entity;

public class NhanVien {
    private String maNhanVien;
    private String ho;
    private String tenLot;
    private String ten;
    private String phai;
    private String ngaySinh;
    private String soDienThoai;
    private String tinh;
    private String diaChi;
    private double luong;
    private String chucVu;
    private String trangThai;
    private String matKhau;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String ho, String tenLot, String ten, String phai,
                    String ngaySinh, String soDienThoai, String tinh, String diaChi,
                    double luong, String chucVu, String trangThai, String matKhau) {
        this.maNhanVien = maNhanVien;
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.phai = phai;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.tinh = tinh;
        this.diaChi = diaChi;
        this.luong = luong;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
        this.matKhau = matKhau;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
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

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
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

    public double getLuong() {
        return luong;
    }

    public void setLuong(double luong) {
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

    public String getHoTen() {
        StringBuilder sb = new StringBuilder();
        if (ho != null && !ho.trim().isEmpty()) {
            sb.append(ho.trim());
        }
        if (tenLot != null && !tenLot.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(tenLot.trim());
        }
        if (ten != null && !ten.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(ten.trim());
        }
        return sb.toString();
    }
}