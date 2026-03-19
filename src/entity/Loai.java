package entity;

public class Loai {
    private int maLoai;
    private String tenLoai;

    public Loai(int maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    @Override
    public String toString() {
        return tenLoai;
    }
}