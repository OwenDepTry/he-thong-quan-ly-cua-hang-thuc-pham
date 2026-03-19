package entity;

public class TinhThanh {
    private int maTThanh;
    private String tenTThanh;

    public TinhThanh(int maTThanh, String tenTThanh) {
        this.maTThanh = maTThanh;
        this.tenTThanh = tenTThanh;
    }

    public int getMaTThanh() {
        return maTThanh;
    }

    public String getTenTThanh() {
        return tenTThanh;
    }

    @Override
    public String toString() {
        return tenTThanh;
    }
}