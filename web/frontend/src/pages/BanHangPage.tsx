import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { ApiError, api } from '../lib/api';
import { formatDate, formatMoney, normalize } from '../lib/format';
import type { HoaDonChiTiet, SanPham, TaoHoaDonRequest, TinhTien } from '../lib/types';
import { ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';

interface CartLine {
  sp: SanPham;
  soLuong: number;
}

/** Màn hình bán hàng: chọn sản phẩm → giỏ hàng → khách hàng / khuyến mãi → lập hóa đơn. */
export default function BanHangPage() {
  const toast = useToast();
  const products = useLoad(() => api.sanPham(), []);
  const customers = useLoad(() => api.khachHang(), []);
  const promos = useLoad(() => api.khuyenMaiHieuLuc(), []);

  const [q, setQ] = useState('');
  const [cart, setCart] = useState<CartLine[]>([]);
  const [maKH, setMaKH] = useState('');
  const [maKM, setMaKM] = useState('');
  const [preview, setPreview] = useState<TinhTien | null>(null);
  const [previewError, setPreviewError] = useState<string | null>(null);
  const [previewing, setPreviewing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [done, setDone] = useState<HoaDonChiTiet | null>(null);

  const filtered = useMemo(() => {
    const k = normalize(q);
    return (products.data ?? [])
      .filter((s) => !k || normalize(`${s.maSanPham} ${s.tenSanPham}`).includes(k))
      .slice(0, 60);
  }, [products.data, q]);

  const request: TaoHoaDonRequest = useMemo(
    () => ({
      maKhachHang: maKH || null,
      maKhuyenMai: maKM || null,
      items: cart.map((l) => ({ maSanPham: l.sp.maSanPham, soLuong: l.soLuong })),
    }),
    [cart, maKH, maKM],
  );

  // Hỏi server tính tiền (giá, tồn kho, khuyến mãi) mỗi khi giỏ hàng đổi – chờ 300ms để đỡ gọi liên tục
  useEffect(() => {
    if (request.items.length === 0 || request.items.some((i) => !(i.soLuong >= 1))) {
      setPreview(null);
      setPreviewError(null);
      return;
    }
    let cancelled = false;
    setPreviewing(true);
    const t = setTimeout(() => {
      api
        .xemTruocHoaDon(request)
        .then((p) => {
          if (cancelled) return;
          setPreview(p);
          setPreviewError(null);
        })
        .catch((e: Error) => {
          if (cancelled) return;
          setPreview(null);
          setPreviewError(e.message);
        })
        .finally(() => !cancelled && setPreviewing(false));
    }, 300);
    return () => {
      cancelled = true;
      clearTimeout(t);
    };
  }, [request]);

  function add(sp: SanPham) {
    if (sp.soLuongTon <= 0) {
      toast.error(`${sp.tenSanPham} đã hết hàng`);
      return;
    }
    setCart((c) => {
      const found = c.find((l) => l.sp.maSanPham === sp.maSanPham);
      if (found) {
        return c.map((l) =>
          l.sp.maSanPham === sp.maSanPham ? { ...l, soLuong: Math.min(l.soLuong + 1, sp.soLuongTon) } : l,
        );
      }
      return [...c, { sp, soLuong: 1 }];
    });
  }

  function setQty(ma: string, value: number) {
    setCart((c) => c.map((l) => (l.sp.maSanPham === ma ? { ...l, soLuong: value } : l)));
  }

  function remove(ma: string) {
    setCart((c) => c.filter((l) => l.sp.maSanPham !== ma));
  }

  function reset() {
    setCart([]);
    setMaKH('');
    setMaKM('');
    setPreview(null);
    setPreviewError(null);
  }

  async function submit() {
    setSaving(true);
    try {
      const hd = await api.taoHoaDon(request);
      setDone(hd);
      reset();
      products.reload(); // tồn kho đã thay đổi
    } catch (e) {
      toast.error(e instanceof ApiError ? e.message : 'Không lập được hóa đơn.');
    } finally {
      setSaving(false);
    }
  }

  const localTotal = cart.reduce((sum, l) => sum + l.sp.gia * (l.soLuong || 0), 0);
  const overStock = cart.find((l) => l.soLuong > l.sp.soLuongTon);
  const invalidQty = cart.some((l) => !Number.isInteger(l.soLuong) || l.soLuong < 1);
  const canSubmit = cart.length > 0 && !overStock && !invalidQty && !!preview && !previewError && !previewing && !saving;

  if (products.loading && !products.data) return <Loading />;
  if (products.error) return <ErrorState message={products.error} onRetry={products.reload} />;

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Lập hóa đơn</h1>
          <p className="muted">Bấm vào sản phẩm để thêm vào hóa đơn. Giá và khuyến mãi được máy chủ tính lại khi lưu.</p>
        </div>
      </header>

      <div className="pos">
        <section className="card pos-products">
          <input
            className="search"
            placeholder="Tìm sản phẩm theo mã hoặc tên…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            aria-label="Tìm sản phẩm để bán"
          />
          <div className="product-grid">
            {filtered.map((s) => (
              <button
                key={s.maSanPham}
                className={`product-tile${s.soLuongTon <= 0 ? ' disabled' : ''}`}
                onClick={() => add(s)}
                title={s.moTa ?? s.tenSanPham}
              >
                <strong>{s.tenSanPham}</strong>
                <span className="price">{formatMoney(s.gia)}</span>
                <small className={s.soLuongTon <= 0 ? 'text-danger' : s.soLuongTon <= 30 ? 'text-warn' : 'muted'}>
                  {s.soLuongTon <= 0 ? 'Hết hàng' : `Tồn ${s.soLuongTon} ${s.donViTinh ?? ''}`}
                </small>
              </button>
            ))}
            {filtered.length === 0 && <p className="muted">Không tìm thấy sản phẩm.</p>}
          </div>
        </section>

        <section className="card pos-cart">
          <h2>Hóa đơn</h2>
          {cart.length === 0 ? (
            <p className="muted empty-cart">Chưa có sản phẩm nào.</p>
          ) : (
            <table className="table table-compact">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th className="num">SL</th>
                  <th className="num">Thành tiền</th>
                  <th aria-label="Xóa" />
                </tr>
              </thead>
              <tbody>
                {cart.map((l) => (
                  <tr key={l.sp.maSanPham}>
                    <td>
                      {l.sp.tenSanPham}
                      <small className="muted block">{formatMoney(l.sp.gia)}</small>
                      {l.soLuong > l.sp.soLuongTon && (
                        <small className="text-danger block">Chỉ còn {l.sp.soLuongTon}</small>
                      )}
                    </td>
                    <td className="num">
                      <input
                        className="qty"
                        type="number"
                        min={1}
                        max={l.sp.soLuongTon}
                        value={Number.isNaN(l.soLuong) ? '' : l.soLuong}
                        onChange={(e) => setQty(l.sp.maSanPham, e.target.valueAsNumber)}
                        aria-label={`Số lượng ${l.sp.tenSanPham}`}
                      />
                    </td>
                    <td className="num">{formatMoney(l.sp.gia * (l.soLuong || 0))}</td>
                    <td>
                      <button className="icon-btn" onClick={() => remove(l.sp.maSanPham)} aria-label={`Bỏ ${l.sp.tenSanPham}`}>
                        ×
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <label className="field">
            <span>Khách hàng</span>
            <select value={maKH} onChange={(e) => setMaKH(e.target.value)}>
              <option value="">Khách lẻ</option>
              {(customers.data ?? []).map((k) => (
                <option key={k.maKhachHang} value={k.maKhachHang}>
                  {k.maKhachHang} – {k.hoTen}
                  {k.soDienThoai ? ` (${k.soDienThoai})` : ''}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>Khuyến mãi</span>
            <select value={maKM} onChange={(e) => setMaKM(e.target.value)}>
              <option value="">Không áp dụng</option>
              {(promos.data ?? []).map((k) => (
                <option key={k.maKhuyenMai} value={k.maKhuyenMai}>
                  {k.maKhuyenMai} – {k.tenKhuyenMai} (giảm {k.phanTramGiam}%{k.dieuKien ? `, đơn ${k.dieuKien}` : ''})
                </option>
              ))}
            </select>
            {promos.data && promos.data.length === 0 && <em className="field-hint">Hôm nay không có khuyến mãi nào.</em>}
          </label>

          <div className="totals">
            <div>
              <span>Tổng tiền</span>
              <strong>{formatMoney(preview?.tongTien ?? localTotal)}</strong>
            </div>
            <div>
              <span>Giảm giá{preview?.phanTramGiam ? ` (${preview.phanTramGiam}%)` : ''}</span>
              <strong>{formatMoney(preview?.tienGiam ?? 0)}</strong>
            </div>
            <div className="grand">
              <span>Khách trả</span>
              <strong>{formatMoney(preview?.thanhTien ?? localTotal)}</strong>
            </div>
          </div>

          {previewError && (
            <div className="alert alert-error" role="alert">
              {previewError}
            </div>
          )}

          <div className="pos-actions">
            <button className="btn" onClick={reset} disabled={cart.length === 0 || saving}>
              Hủy
            </button>
            <button className="btn btn-primary" onClick={submit} disabled={!canSubmit}>
              {saving ? 'Đang lưu…' : previewing ? 'Đang tính…' : 'Lập hóa đơn'}
            </button>
          </div>
        </section>
      </div>

      {done && (
        <Modal
          title={`Đã lập hóa đơn ${done.hoaDon.maHoaDon}`}
          onClose={() => setDone(null)}
          footer={
            <>
              <Link className="btn" to="/hoa-don">
                Xem danh sách hóa đơn
              </Link>
              <button className="btn btn-primary" onClick={() => setDone(null)} autoFocus>
                Lập hóa đơn mới
              </button>
            </>
          }
        >
          <HoaDonView hd={done} />
        </Modal>
      )}
    </div>
  );
}

export function HoaDonView({ hd }: { hd: HoaDonChiTiet }) {
  const h = hd.hoaDon;
  return (
    <div className="invoice">
      <dl className="meta">
        <div>
          <dt>Ngày</dt>
          <dd>{formatDate(h.thoiGian)}</dd>
        </div>
        <div>
          <dt>Khách hàng</dt>
          <dd>{h.maKhachHang ? `${h.tenKhachHang} (${h.maKhachHang})` : 'Khách lẻ'}</dd>
        </div>
        <div>
          <dt>Nhân viên</dt>
          <dd>
            {h.tenNhanVien} ({h.maNhanVien})
          </dd>
        </div>
        <div>
          <dt>Khuyến mãi</dt>
          <dd>{h.maKhuyenMai ?? '—'}</dd>
        </div>
      </dl>
      <table className="table table-compact">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th className="num">SL</th>
            <th className="num">Đơn giá</th>
            <th className="num">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          {hd.items.map((i) => (
            <tr key={i.maSanPham}>
              <td>
                {i.tenSanPham} <small className="muted">{i.maSanPham}</small>
              </td>
              <td className="num">
                {i.soLuong} {i.donViTinh ?? ''}
              </td>
              <td className="num">{formatMoney(i.donGia)}</td>
              <td className="num">{formatMoney(i.thanhTien)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="totals">
        <div>
          <span>Tổng tiền</span>
          <strong>{formatMoney(h.tongTien)}</strong>
        </div>
        <div>
          <span>Giảm giá</span>
          <strong>{formatMoney(h.tienGiam)}</strong>
        </div>
        <div className="grand">
          <span>Thành tiền</span>
          <strong>{formatMoney(h.thanhTien)}</strong>
        </div>
      </div>
    </div>
  );
}
