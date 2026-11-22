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

export type TrangThai = 'active' | 'inactive';

export interface KhachHang {
  maKhachHang: string;
  ho: string | null;
  tenLot: string | null;
  ten: string;
  hoTen: string;
  phai: string | null;
  ngaySinh: string | null;
  soDienThoai: string | null;
  tinh: string | null;
  ngayThamGia: string | null;
  diem: number;
  trangThai: TrangThai;
  soHoaDon: number;
  tongChiTieu: number;
  lanMuaCuoi: string | null;
}

export interface KhachHangInput {
  maKhachHang?: string | null;
  ho: string | null;
  tenLot: string | null;
  ten: string;
  phai: string | null;
  ngaySinh: string | null;
  soDienThoai: string;
  tinh: string | null;
  trangThai: TrangThai;
}

export interface NhanVien {
  maNhanVien: string;
  ho: string | null;
  tenLot: string | null;
  ten: string;
  hoTen: string;
  phai: string | null;
  ngaySinh: string | null;
  soDienThoai: string | null;
  tinh: string | null;
  diaChi: string | null;
  luong: number;
  chucVu: Role;
  trangThai: TrangThai;
  hoaDonThangNay: number;
  doanhThuThangNay: number;
}

export interface NhanVienInput {
  maNhanVien?: string | null;
  ho: string | null;
  tenLot: string | null;
  ten: string;
  phai: string | null;
  ngaySinh: string | null;
  soDienThoai: string | null;
  tinh: string | null;
  diaChi: string | null;
  luong: number | null;
  chucVu: Role;
  trangThai: TrangThai;
  matKhau: string | null;
}

export interface NhaCungCap {
  maNhaCungCap: string;
  ten: string;
  tenLienHe: string | null;
  soDienThoai: string | null;
}

export interface PhieuNhapTomTat {
  maPhieuNhap: string;
  maNhaCungCap: string | null;
  tenNhaCungCap: string | null;
  maNhanVien: string;
  tenNhanVien: string;
  tongTien: number;
  thoiGian: string | null;
  soMatHang: number;
}

export interface PhieuNhapChiTiet {
  phieuNhap: PhieuNhapTomTat;
  items: ChiTiet[];
  loHangMoi: { maHang: string; maSanPham: string; soLuong: number; hanSuDung: string }[];
}

export interface TaoPhieuNhapRequest {
  maNhaCungCap: string;
  items: { maSanPham: string; soLuong: number; donGia: number; hanSuDung: string | null }[];
}

export interface TongHop {
  doanhThu: number;
  tienGiam: number;
  soHoaDon: number;
  giaTriTrungBinh: number;
  tienNhap: number;
  soPhieuNhap: number;
}

export interface BaoCao {
  tuNgay: string;
  denNgay: string;
  nhom: 'ngay' | 'thang';
  tongHop: TongHop;
  kyTruoc: TongHop;
  theoKy: { ky: string; doanhThu: number; soHoaDon: number }[];
  sanPhamBanChay: {
    maSanPham: string;
    tenSanPham: string;
    loai: string | null;
    donViTinh: string | null;
    soLuong: number;
    doanhThu: number;
  }[];
  theoNhanVien: { maNhanVien: string; hoTen: string; soHoaDon: number; doanhThu: number }[];
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
