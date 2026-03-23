package entity;

public class KhachHang {

    private String maKhachHang;
    private String ho;
    private String tenLot;
    private String ten;
    private String phai;
    private String ngaySinh;
    private String soDienThoai;
    private String tinh;
    private String ngayThamGia;
    private int diem;
    private String trangThai;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String ho, String tenLot, String ten, String phai,
            String ngaySinh, String soDienThoai, String tinh, String ngayThamGia, int diem, String trangThai) {
        this.maKhachHang = maKhachHang;
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.phai = phai;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.tinh = tinh;
        this.ngayThamGia = ngayThamGia;
        this.diem = diem;
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
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

    public String getNgayThamGia() {
        return ngayThamGia;
    }

    public void setNgayThamGia(String ngayThamGia) {
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

    public String getHoTen() {
        StringBuilder sb = new StringBuilder();
        if (ho != null && !ho.trim().isEmpty()) {
            sb.append(ho.trim());
        }
        if (tenLot != null && !tenLot.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(tenLot.trim());
        }
        if (ten != null && !ten.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(ten.trim());
        }
        return sb.toString().trim();
    }
}