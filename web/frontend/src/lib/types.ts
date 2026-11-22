export type Role = 'QL' | 'NV';

export interface UserInfo {
  maNhanVien: string;
  hoTen: string;
  role: Role;
}

export interface LoginResponse {
  token: string;
  expiresAt: string;
  user: UserInfo;
}

export interface SanPham {
  maSanPham: string;
  maNhom: string | null;
  tenSanPham: string;
  loai: string | null;
  donViTinh: string | null;
  hanSuDung: string | null;
  moTa: string | null;
  gia: number;
  soLuongTon: number;
}

export type SanPhamInput = Omit<SanPham, 'gia' | 'soLuongTon'> & {
  gia: number | null;
  soLuongTon: number | null;
};

export interface LoaiSanPham {
  maLoai: string;
  tenLoai: string;
}

export interface KhachHang {
  maKhachHang: string;
  hoTen: string;
  soDienThoai: string | null;
}

export interface KhuyenMai {
  maKhuyenMai: string;
  tenKhuyenMai: string;
  dieuKien: string | null;
  phanTramGiam: number;
  ngayBatDau: string | null;
  ngayKetThuc: string | null;
  trangThai: string;
}

export interface DongHang {
  maSanPham: string;
  soLuong: number;
}

export interface TaoHoaDonRequest {
  maKhachHang: string | null;
  maKhuyenMai: string | null;
  items: DongHang[];
}

export interface ChiTiet {
  maSanPham: string;
  tenSanPham: string;
  donViTinh: string | null;
  soLuong: number;
  donGia: number;
  thanhTien: number;
}

export interface TinhTien {
  items: ChiTiet[];
  tongTien: number;
  tienGiam: number;
  thanhTien: number;
  maKhuyenMai: string | null;
  phanTramGiam: number | null;
}

export interface HoaDonTomTat {
  maHoaDon: string;
  maKhachHang: string | null;
  tenKhachHang: string;
  maNhanVien: string;
  tenNhanVien: string;
  tongTien: number;
  tienGiam: number;
  thanhTien: number;
  thoiGian: string | null;
  maKhuyenMai: string | null;
}

export interface HoaDonChiTiet {
  hoaDon: HoaDonTomTat;
  items: ChiTiet[];
}

export interface Trang<T> {
  items: T[];
  total: number;
  page: number;
  size: number;
}

export interface Dashboard {
  ngay: string;
  doanhThuHomNay: number | null;
  hoaDonHomNay: number;
  doanhThuThangNay: number | null;
  hoaDonThangNay: number;
  tongSanPham: number;
  tongKhachHang: number;
  nguongSapHetHang: number;
  soSapHetHang: number;
  sapHetHang: { maSanPham: string; tenSanPham: string; donViTinh: string | null; soLuongTon: number }[];
  soNgayCanhBaoHetHan: number;
  soLoSapHetHan: number;
  sapHetHan: {
    maHang: string;
    maSanPham: string;
    tenSanPham: string;
    soLuong: number;
    hanSuDung: string;
    soNgayConLai: number;
  }[];
}
