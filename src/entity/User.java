package entity;

public class User {
    private String maNV;
    private String hoTen;
    private String chucVu;
    private String trangThai;

    public User(String maNV, String hoTen, String chucVu, String trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getChucVu() {
        return chucVu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public boolean isAdmin() {
        return "ADM".equalsIgnoreCase(chucVu);
    }
}
