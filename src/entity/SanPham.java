package entity;

public class SanPham {
    private String maSP;
    private String tenSP;
    private int loai;
    private int donViTinh;
    private int hsDung;
    private String moTa;
    private int gia;
    private int soLuongTon;

    public SanPham() {
    }

    public SanPham(String maSP, String tenSP, int loai, int donViTinh, int hsDung, String moTa, int gia, int soLuongTon) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loai = loai;
        this.donViTinh = donViTinh;
        this.hsDung = hsDung;
        this.moTa = moTa;
        this.gia = gia;
        this.soLuongTon = soLuongTon;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }

    public int getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(int donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getHsDung() {
        return hsDung;
    }

    public void setHsDung(int hsDung) {
        this.hsDung = hsDung;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
}
