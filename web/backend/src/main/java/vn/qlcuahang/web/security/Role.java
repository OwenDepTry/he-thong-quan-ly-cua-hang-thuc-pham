package vn.qlcuahang.web.security;

/** Vai trò lấy từ cột NhanVien.ChucVu. */
public enum Role {
    /** Quản lý: toàn quyền. */
    QL,
    /** Nhân viên bán hàng. */
    NV;

    public String authority() {
        return "ROLE_" + name();
    }

    /** "QL"/"ADMIN" (không phân biệt hoa thường) là quản lý, còn lại là nhân viên – giống app desktop. */
    public static Role fromChucVu(String chucVu) {
        String v = chucVu == null ? "" : chucVu.trim();
        return "QL".equalsIgnoreCase(v) || "ADMIN".equalsIgnoreCase(v) ? QL : NV;
    }
}
