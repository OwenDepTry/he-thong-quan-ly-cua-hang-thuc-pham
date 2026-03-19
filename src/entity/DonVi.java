package entity;

public class DonVi {
    private int maDonVi;
    private String tenDonVi;

    public DonVi(int maDonVi, String tenDonVi) {
        this.maDonVi = maDonVi;
        this.tenDonVi = tenDonVi;
    }

    public int getMaDonVi() {
        return maDonVi;
    }

    public String getTenDonVi() {
        return tenDonVi;
    }

    @Override
    public String toString() {
        return tenDonVi;
    }
}