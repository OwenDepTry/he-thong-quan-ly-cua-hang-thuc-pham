import type {
  Dashboard,
  HoaDonChiTiet,
  HoaDonTomTat,
  KhachHang,
  KhuyenMai,
  LoaiSanPham,
  LoginResponse,
  SanPham,
  SanPhamInput,
  TaoHoaDonRequest,
  TinhTien,
  Trang,
  UserInfo,
} from './types';

/** Để trống = cùng domain (/api). Đặt VITE_API_URL khi frontend và backend ở 2 domain khác nhau. */
const BASE_URL = (import.meta.env.VITE_API_URL ?? '').replace(/\/+$/, '');
const TOKEN_KEY = 'qlch.token';

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public fieldErrors: Record<string, string> = {},
  ) {
    super(message);
  }
}

// localStorage có thể bị chặn (chế độ riêng tư, iframe) – khi đó chỉ giữ token trong bộ nhớ
let memoryToken: string | null = null;

export const tokenStore = {
  get(): string | null {
    try {
      return localStorage.getItem(TOKEN_KEY) ?? memoryToken;
    } catch {
      return memoryToken;
    }
  },
  set(token: string | null) {
    memoryToken = token;
    try {
      if (token) localStorage.setItem(TOKEN_KEY, token);
      else localStorage.removeItem(TOKEN_KEY);
    } catch {
      /* bỏ qua */
    }
  },
};

/** Được gọi khi API trả 401 (token hết hạn) để đăng xuất. */
let onUnauthorized: () => void = () => {};
export function setUnauthorizedHandler(fn: () => void) {
  onUnauthorized = fn;
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {};
  const token = tokenStore.get();
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers['Content-Type'] = 'application/json';

  let res: Response;
  try {
    res = await fetch(BASE_URL + path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new ApiError(0, 'Không kết nối được máy chủ. Kiểm tra mạng hoặc thử lại sau.');
  }

  if (res.status === 204) return undefined as T;

  const text = await res.text();
  let data: unknown = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = null;
    }
  }

  if (!res.ok) {
    const err = (data ?? {}) as { message?: string; errors?: Record<string, string> };
    if (res.status === 401 && path !== '/api/auth/login') {
      onUnauthorized();
    }
    const fallback =
      res.status === 401
        ? 'Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.'
        : res.status === 403
          ? 'Bạn không có quyền thực hiện thao tác này.'
          : `Lỗi máy chủ (${res.status}).`;
    throw new ApiError(res.status, err.message || fallback, err.errors ?? {});
  }
  return data as T;
}

function qs(params: Record<string, string | number | undefined | null>): string {
  const s = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null && v !== '') s.set(k, String(v));
  }
  const str = s.toString();
  return str ? `?${str}` : '';
}

export const api = {
  login: (maNhanVien: string, matKhau: string) =>
    request<LoginResponse>('POST', '/api/auth/login', { maNhanVien, matKhau }),
  me: () => request<UserInfo>('GET', '/api/auth/me'),

  dashboard: () => request<Dashboard>('GET', '/api/dashboard'),

  sanPham: (q?: string, loai?: string) => request<SanPham[]>('GET', `/api/san-pham${qs({ q, loai })}`),
  loaiSanPham: () => request<LoaiSanPham[]>('GET', '/api/loai-san-pham'),
  themSanPham: (sp: SanPhamInput) => request<SanPham>('POST', '/api/san-pham', sp),
  suaSanPham: (ma: string, sp: SanPhamInput) =>
    request<SanPham>('PUT', `/api/san-pham/${encodeURIComponent(ma)}`, sp),
  xoaSanPham: (ma: string) => request<void>('DELETE', `/api/san-pham/${encodeURIComponent(ma)}`),

  khachHang: (q?: string) => request<KhachHang[]>('GET', `/api/khach-hang${qs({ q })}`),
  khuyenMaiHieuLuc: () => request<KhuyenMai[]>('GET', '/api/khuyen-mai/hieu-luc'),

  hoaDon: (p: { q?: string; tuNgay?: string; denNgay?: string; page?: number; size?: number }) =>
    request<Trang<HoaDonTomTat>>('GET', `/api/hoa-don${qs(p)}`),
  chiTietHoaDon: (ma: string) => request<HoaDonChiTiet>('GET', `/api/hoa-don/${encodeURIComponent(ma)}`),
  xemTruocHoaDon: (req: TaoHoaDonRequest) => request<TinhTien>('POST', '/api/hoa-don/xem-truoc', req),
  taoHoaDon: (req: TaoHoaDonRequest) => request<HoaDonChiTiet>('POST', '/api/hoa-don', req),
};
