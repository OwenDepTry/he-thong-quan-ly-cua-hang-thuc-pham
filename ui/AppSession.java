package ui;

import entity.NhanVien;

public class AppSession {
    private static NhanVien currentUser;

    public static void setCurrentUser(NhanVien user) {
        currentUser = user;
    }

    public static NhanVien getCurrentUser() {
        return currentUser;
    }

    public static String getMaNhanVien() {
        return currentUser == null ? "" : currentUser.getMaNhanVien();
    }

    public static boolean isAdmin() {
        return currentUser != null && "QL".equalsIgnoreCase(currentUser.getChucVu());
    }

    public static boolean isStaff() {
        return currentUser != null && "NV".equalsIgnoreCase(currentUser.getChucVu());
    }

    public static void clear() {
        currentUser = null;
    }
}