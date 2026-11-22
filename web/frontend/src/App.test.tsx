import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { App } from './App';
import { tokenStore } from './lib/api';
import type { BaoCao, Dashboard, SanPham } from './lib/types';
import { addDays, isoDate } from './lib/format';

const dashboard: Dashboard = {
  ngay: '2026-11-20',
  doanhThuHomNay: 1_500_000,
  hoaDonHomNay: 4,
  doanhThuThangNay: 42_000_000,
  hoaDonThangNay: 120,
  tongSanPham: 10,
  tongKhachHang: 7,
  nguongSapHetHang: 30,
  soSapHetHang: 1,
  sapHetHang: [{ maSanPham: 'SP006', tenSanPham: 'Rau muống', donViTinh: 'Kg', soLuongTon: 0 }],
  soNgayCanhBaoHetHan: 7,
  soLoSapHetHan: 0,
  sapHetHan: [],
};

const sanPham: SanPham[] = [
  { maSanPham: 'SP001', maNhom: 'TH01', tenSanPham: 'Thịt bò', loai: 'Thịt và hải sản', donViTinh: 'Kg', hanSuDung: '3', moTa: null, gia: 30000, soLuongTon: 50 },
  { maSanPham: 'SP008', maNhom: 'TC01', tenSanPham: 'Táo', loai: 'Trái cây', donViTinh: 'Kg', hanSuDung: '10', moTa: null, gia: 40000, soLuongTon: 5 },
];

type Handler = (body: unknown) => [number, unknown];

/** Backend giả: map "METHOD /path" → handler. Ghi lại các request để kiểm tra. */
function fakeBackend(routes: Record<string, Handler>) {
  const calls: { key: string; body: unknown }[] = [];
  vi.stubGlobal(
    'fetch',
    vi.fn(async (url: string, init: RequestInit = {}) => {
      const path = url.split('?')[0];
      const key = `${init.method ?? 'GET'} ${path}`;
      const body = init.body ? JSON.parse(String(init.body)) : undefined;
      calls.push({ key, body });
      const h = routes[key];
      const [status, data] = h ? h(body) : [404, { message: `Không có ${key}` }];
      return new Response(data === undefined ? null : JSON.stringify(data), { status });
    }),
  );
  return calls;
}

function user(role: 'QL' | 'NV') {
  return { maNhanVien: role === 'QL' ? 'admin' : 'nhanvien', hoTen: role === 'QL' ? 'Siêu Quản Lý' : 'Người Nhân Viên', role };
}

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>,
  );
}

afterEach(() => {
  vi.unstubAllGlobals();
  tokenStore.set(null);
});

describe('App', () => {
  it('chưa đăng nhập thì chuyển tới trang đăng nhập, đăng nhập xong vào tổng quan', async () => {
    const calls = fakeBackend({
      'POST /api/auth/login': () => [200, { token: 't-ql', expiresAt: '2026-11-21T00:00:00Z', user: user('QL') }],
      'GET /api/dashboard': () => [200, dashboard],
    });
    renderAt('/');

    expect(await screen.findByRole('heading', { name: 'Đăng nhập' })).toBeInTheDocument();
    await userEvent.type(screen.getByLabelText('Mã nhân viên'), 'admin');
    await userEvent.type(screen.getByLabelText('Mật khẩu'), 'admin');
    await userEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }));

    expect(await screen.findByText('Doanh thu hôm nay')).toBeInTheDocument();
    expect(screen.getByText('1.500.000')).toBeInTheDocument(); // hiện đủ số tiền, không làm tròn
    expect(screen.getByText('Rau muống')).toBeInTheDocument();
    expect(calls[0].body).toEqual({ maNhanVien: 'admin', matKhau: 'admin' });
    expect(tokenStore.get()).toBe('t-ql');
  });

  it('hiện lỗi khi sai mật khẩu', async () => {
    fakeBackend({ 'POST /api/auth/login': () => [401, { message: 'Sai mã nhân viên hoặc mật khẩu.' }] });
    renderAt('/dang-nhap');
    await userEvent.type(screen.getByLabelText('Mã nhân viên'), 'admin');
    await userEvent.type(screen.getByLabelText('Mật khẩu'), 'sai');
    await userEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Sai mã nhân viên hoặc mật khẩu.');
  });

  it('nhân viên không thấy doanh thu và không có nút thêm/sửa sản phẩm', async () => {
    tokenStore.set('t-nv');
    fakeBackend({
      'GET /api/auth/me': () => [200, user('NV')],
      'GET /api/dashboard': () => [200, { ...dashboard, doanhThuHomNay: null, doanhThuThangNay: null }],
      'GET /api/san-pham': () => [200, sanPham],
      'GET /api/loai-san-pham': () => [200, []],
    });
    renderAt('/');
    expect(await screen.findByText('Hóa đơn hôm nay')).toBeInTheDocument();
    expect(screen.queryByText('Doanh thu hôm nay')).not.toBeInTheDocument();

    await userEvent.click(screen.getByRole('link', { name: /Sản phẩm/ }));
    expect(await screen.findByText('Thịt bò')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Thêm sản phẩm/ })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Sửa' })).not.toBeInTheDocument();
  });

  it('quản lý tìm sản phẩm không dấu và thêm sản phẩm mới', async () => {
    tokenStore.set('t-ql');
    const calls = fakeBackend({
      'GET /api/auth/me': () => [200, user('QL')],
      'GET /api/san-pham': () => [200, sanPham],
      'GET /api/loai-san-pham': () => [200, [{ maLoai: 'L02', tenLoai: 'Rau củ' }]],
      'POST /api/san-pham': (b) => [201, { ...(b as object), gia: 18000, soLuongTon: 40 }],
    });
    renderAt('/san-pham');
    expect(await screen.findByText('Thịt bò')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Tìm sản phẩm'), 'tao');
    expect(screen.queryByText('Thịt bò')).not.toBeInTheDocument();
    expect(screen.getByText('Táo')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: /Thêm sản phẩm/ }));
    const dialog = screen.getByRole('dialog');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Lưu' }));
    expect(within(dialog).getByText('Vui lòng nhập mã sản phẩm')).toBeInTheDocument();

    await userEvent.type(within(dialog).getByLabelText('Mã sản phẩm *'), 'SP011');
    await userEvent.type(within(dialog).getByLabelText('Tên sản phẩm *'), 'Bí đỏ');
    await userEvent.type(within(dialog).getByLabelText('Giá bán (đồng) *'), '18.000');
    await userEvent.clear(within(dialog).getByLabelText('Tồn kho *'));
    await userEvent.type(within(dialog).getByLabelText('Tồn kho *'), '40');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Lưu' }));

    await waitFor(() => expect(calls.some((c) => c.key === 'POST /api/san-pham')).toBe(true));
    const post = calls.find((c) => c.key === 'POST /api/san-pham')!;
    expect(post.body).toMatchObject({ maSanPham: 'SP011', tenSanPham: 'Bí đỏ', gia: 18000, soLuongTon: 40, loai: 'Rau củ' });
    expect(await screen.findByText('Đã thêm Bí đỏ')).toBeInTheDocument();
  });

  it('lập hóa đơn: thêm sản phẩm, máy chủ tính khuyến mãi, gửi đúng dữ liệu', async () => {
    tokenStore.set('t-nv');
    const calls = fakeBackend({
      'GET /api/auth/me': () => [200, user('NV')],
      'GET /api/san-pham': () => [200, sanPham],
      'GET /api/khach-hang': () => [200, [{ maKhachHang: 'KH001', hoTen: 'Nguyễn Văn A', soDienThoai: '0900' }]],
      'GET /api/khuyen-mai/hieu-luc': () => [
        200,
        [{ maKhuyenMai: 'KM004', tenKhuyenMai: 'Cuối tháng', dieuKien: '>=200000', phanTramGiam: 10, ngayBatDau: null, ngayKetThuc: null, trangThai: 'active' }],
      ],
      'POST /api/hoa-don/xem-truoc': (b) => {
        const req = b as { maKhuyenMai: string | null; items: { soLuong: number }[] };
        const tong = req.items[0].soLuong * 30000;
        if (req.maKhuyenMai && tong < 200000) return [400, { message: 'Hóa đơn chưa đạt điều kiện khuyến mãi KM004 (>=200000).' }];
        const giam = req.maKhuyenMai ? tong * 0.1 : 0;
        return [200, { items: [], tongTien: tong, tienGiam: giam, thanhTien: tong - giam, maKhuyenMai: req.maKhuyenMai, phanTramGiam: req.maKhuyenMai ? 10 : null }];
      },
      'POST /api/hoa-don': () => [
        201,
        {
          hoaDon: { maHoaDon: 'HD2001', maKhachHang: 'KH001', tenKhachHang: 'Nguyễn Văn A', maNhanVien: 'nhanvien', tenNhanVien: 'Người Nhân Viên', tongTien: 240000, tienGiam: 24000, thanhTien: 216000, thoiGian: '2026-11-20', maKhuyenMai: 'KM004' },
          items: [{ maSanPham: 'SP001', tenSanPham: 'Thịt bò', donViTinh: 'Kg', soLuong: 8, donGia: 30000, thanhTien: 240000 }],
        },
      ],
    });
    renderAt('/ban-hang');

    await userEvent.click(await screen.findByRole('button', { name: /Thịt bò/ }));
    await userEvent.selectOptions(await screen.findByLabelText('Khuyến mãi'), 'KM004');
    expect(await screen.findByRole('alert')).toHaveTextContent('chưa đạt điều kiện');
    expect(screen.getByRole('button', { name: 'Lập hóa đơn' })).toBeDisabled();

    const qty = screen.getByLabelText('Số lượng Thịt bò');
    await userEvent.clear(qty);
    await userEvent.type(qty, '8');
    await userEvent.selectOptions(screen.getByRole('combobox', { name: 'Khách hàng' }), 'KH001');

    await waitFor(() => expect(screen.getByRole('button', { name: 'Lập hóa đơn' })).toBeEnabled());
    expect(screen.getByText('216.000 ₫')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Lập hóa đơn' }));

    expect(await screen.findByText('Đã lập hóa đơn HD2001')).toBeInTheDocument();
    const post = calls.find((c) => c.key === 'POST /api/hoa-don')!;
    expect(post.body).toEqual({ maKhachHang: 'KH001', maKhuyenMai: 'KM004', items: [{ maSanPham: 'SP001', soLuong: 8 }] });
  });

  it('nhân viên không thấy Báo cáo, Nhân viên; gõ thẳng địa chỉ cũng bị đưa về tổng quan', async () => {
    tokenStore.set('t-nv');
    fakeBackend({
      'GET /api/auth/me': () => [200, user('NV')],
      'GET /api/dashboard': () => [200, { ...dashboard, doanhThuHomNay: null, doanhThuThangNay: null }],
    });
    renderAt('/bao-cao');
    expect(await screen.findByText('Hóa đơn hôm nay')).toBeInTheDocument();
    const nav = within(screen.getByRole('navigation', { name: 'Điều hướng chính' }));
    expect(nav.getByRole('link', { name: 'Nhập hàng' })).toBeInTheDocument();
    expect(nav.queryByRole('link', { name: 'Thống kê' })).not.toBeInTheDocument();
    expect(nav.queryByRole('link', { name: 'Nhân viên' })).not.toBeInTheDocument();
  });

  it('nhập hàng từ cảnh báo tồn kho: chọn sẵn sản phẩm, gợi ý hạn dùng, gửi đúng dữ liệu', async () => {
    tokenStore.set('t-nv');
    const today = isoDate(new Date());
    const calls = fakeBackend({
      'GET /api/auth/me': () => [200, user('NV')],
      'GET /api/phieu-nhap': () => [200, { items: [], total: 0, page: 0, size: 20 }],
      'GET /api/nha-cung-cap': () => [200, [{ maNhaCungCap: 'NCC01', ten: 'Vissan', tenLienHe: null, soDienThoai: null }]],
      'GET /api/san-pham': () => [200, sanPham],
      'POST /api/phieu-nhap': () => [
        201,
        {
          phieuNhap: { maPhieuNhap: 'PN101', maNhaCungCap: 'NCC01', tenNhaCungCap: 'Vissan', maNhanVien: 'nhanvien', tenNhanVien: 'Người Nhân Viên', tongTien: 1_000_000, thoiGian: today, soMatHang: 1 },
          items: [{ maSanPham: 'SP001', tenSanPham: 'Thịt bò', donViTinh: 'Kg', soLuong: 50, donGia: 20000, thanhTien: 1_000_000 }],
          loHangMoi: [{ maHang: 'HH106', maSanPham: 'SP001', soLuong: 50, hanSuDung: addDays(today, 3) }],
        },
      ],
    });
    renderAt('/nhap-hang?sp=SP001');

    const dialog = await screen.findByRole('dialog', { name: 'Lập phiếu nhập' });
    const sp = await within(dialog).findByLabelText(/Sản phẩm dòng 1/);
    expect(sp).toHaveValue('SP001');
    // Thịt bò dùng được 3 ngày nên hạn gợi ý là hôm nay + 3
    expect(within(dialog).getByLabelText(/Hạn sử dụng dòng 1/)).toHaveValue(addDays(today, 3));

    await userEvent.click(within(dialog).getByRole('button', { name: 'Lưu phiếu nhập' }));
    expect(within(dialog).getByText('Số lượng lớn hơn 0')).toBeInTheDocument();

    await userEvent.type(within(dialog).getByLabelText(/Số lượng dòng 1/), '50');
    await userEvent.type(within(dialog).getByLabelText(/Giá nhập dòng 1/), '20.000');
    expect(within(dialog).getByText('1.000.000 ₫')).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Lưu phiếu nhập' }));

    expect(await screen.findByText(/Đã lập phiếu nhập PN101/)).toBeInTheDocument();
    const post = calls.find((c) => c.key === 'POST /api/phieu-nhap')!;
    expect(post.body).toEqual({
      maNhaCungCap: 'NCC01',
      items: [{ maSanPham: 'SP001', soLuong: 50, donGia: 20000, hanSuDung: addDays(today, 3) }],
    });
    expect(screen.getByText('Lô HH106, hạn ' + addDays(today, 3).split('-').reverse().join('/'))).toBeInTheDocument();
  });

  it('thống kê: so sánh kỳ trước, lợi nhuận, nhóm hàng, thứ bán tốt nhất', async () => {
    tokenStore.set('t-ql');
    const tong = { doanhThu: 12_000_000, tienGiam: 300_000, soHoaDon: 40, giaTriTrungBinh: 300_000, tienNhap: 5_000_000, soPhieuNhap: 3, giaVon: 9_000_000, loiNhuanGop: 3_000_000 };
    const report: BaoCao = {
      tuNgay: '2026-10-25',
      denNgay: '2026-11-23',
      nhom: 'ngay',
      tongHop: tong,
      kyTruoc: { ...tong, doanhThu: 10_000_000, soHoaDon: 50 },
      theoKy: [{ ky: '2026-11-23', doanhThu: 12_000_000, soHoaDon: 40 }],
      sanPhamBanChay: [{ maSanPham: 'SP003', tenSanPham: 'Cá hồi', loai: 'Thịt và hải sản', donViTinh: 'Kg', soLuong: 20, doanhThu: 6_000_000 }],
      theoNhanVien: [{ maNhanVien: 'NV01', hoTen: 'Nguyễn Thị Hoa', soHoaDon: 40, doanhThu: 12_000_000 }],
      theoNhomHang: [
        { loai: 'Thịt và hải sản', soLuong: 20, doanhThu: 9_000_000 },
        { loai: 'Rau củ', soLuong: 50, doanhThu: 3_000_000 },
      ],
      theoThu: [1, 2, 3, 4, 5, 6, 7].map((thu) => ({ thu, doanhThu: thu === 6 ? 4_000_000 : 1_000_000, soHoaDon: 5, soNgay: 4 })),
      khachHangThanThiet: [{ maKhachHang: 'KH005', hoTen: 'Hoàng Minh Đức', soHoaDon: 6, doanhThu: 2_400_000 }],
    };
    fakeBackend({
      'GET /api/auth/me': () => [200, user('QL')],
      'GET /api/bao-cao': () => [200, report],
    });
    renderAt('/bao-cao'); // đường dẫn cũ chuyển sang /thong-ke
    expect(await screen.findByRole('heading', { name: 'Thống kê' })).toBeInTheDocument();
    expect(await screen.findByText('Tăng 20% so với kỳ trước')).toBeInTheDocument();
    expect(screen.getByText('Biên lợi nhuận 25%')).toBeInTheDocument();
    expect(screen.getAllByText('75%').length).toBeGreaterThan(0); // Thịt và hải sản chiếm 75%
    expect(screen.getByText(/Bán tốt nhất vào thứ bảy/)).toBeInTheDocument();
    expect(screen.getByText('Hoàng Minh Đức')).toBeInTheDocument();
    expect(screen.getByText('Giảm 20% so với kỳ trước')).toBeInTheDocument();
    expect(screen.getByText('Cá hồi')).toBeInTheDocument();
    expect(screen.getByText('Nguyễn Thị Hoa')).toBeInTheDocument();
  });
});
