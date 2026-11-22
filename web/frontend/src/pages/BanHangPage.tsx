import { useEffect, useMemo, useRef, useState } from 'react';
import { AlertCircle, Minus, Plus, Search, ShoppingBasket, Trash2 } from 'lucide-react';
import { ApiError, api } from '../lib/api';
import { PrintButton } from '../components/form';
import { useAuth } from '../lib/auth';
import { categoryOf } from '../lib/category';
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
  const { user } = useAuth();
  const products = useLoad(() => api.sanPham(), []);
  const [cat, setCat] = useState('');
  const searchRef = useRef<HTMLInputElement>(null);
  const receiptRef = useRef<HTMLDivElement>(null);
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

  // Nhóm hàng có trong danh sách (để làm bộ lọc), kèm số sản phẩm mỗi nhóm
  const groups = useMemo(() => {
    const map = new Map<string, { key: string; label: string; icon: ReturnType<typeof categoryOf>['icon']; count: number }>();
    for (const s of products.data ?? []) {
      const c = categoryOf(s.loai, s.tenSanPham);
      const g = map.get(c.key) ?? { key: c.key, label: c.label, icon: c.icon, count: 0 };
      g.count++;
      map.set(c.key, g);
    }
    return [...map.values()];
  }, [products.data]);

  const filtered = useMemo(() => {
    const k = normalize(q);
    return (products.data ?? []).filter(
      (s) =>
        (!k || normalize(`${s.maSanPham} ${s.tenSanPham}`).includes(k)) &&
        (!cat || categoryOf(s.loai, s.tenSanPham).key === cat),
    );
  }, [products.data, q, cat]);

  // Phím "/" để nhảy tới ô tìm sản phẩm (thao tác nhanh tại quầy)
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      const t = e.target as HTMLElement;
      if (e.key === '/' && !['INPUT', 'SELECT', 'TEXTAREA'].includes(t.tagName)) {
        e.preventDefault();
        searchRef.current?.focus();
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

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

  function step(l: CartLine, delta: number) {
    const next = (Number.isNaN(l.soLuong) ? 0 : l.soLuong) + delta;
    if (next < 1) remove(l.sp.maSanPham);
    else setQty(l.sp.maSanPham, Math.min(next, l.sp.soLuongTon));
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

  const inCart = new Map(cart.map((l) => [l.sp.maSanPham, l.soLuong]));
  const itemCount = cart.reduce((n, l) => n + (l.soLuong || 0), 0);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Bán hàng</h1>
          <p>Chọn sản phẩm để thêm vào hóa đơn. Nhấn / để tìm nhanh.</p>
        </div>
      </header>

      <div className="pos">
        <section className="pos-left" aria-label="Sản phẩm">
          <div className="toolbar">
            <div className="search-box">
              <Search size={17} aria-hidden="true" />
              <input
                ref={searchRef}
                placeholder="Tìm theo tên hoặc mã sản phẩm"
                value={q}
                onChange={(e) => setQ(e.target.value)}
                aria-label="Tìm sản phẩm để bán"
              />
            </div>
          </div>
          {groups.length > 1 && (
            <div className="filters" role="group" aria-label="Lọc theo nhóm hàng">
              <button className="filter" aria-pressed={cat === ''} onClick={() => setCat('')}>
                Tất cả <span className="count">{products.data?.length}</span>
              </button>
              {groups.map((g) => (
                <button key={g.key} className={`filter cat cat-${g.key}`} aria-pressed={cat === g.key} onClick={() => setCat(cat === g.key ? '' : g.key)}>
                  <g.icon size={15} aria-hidden="true" /> {g.label} <span className="count">{g.count}</span>
                </button>
              ))}
            </div>
          )}

          <div className="product-grid">
            {filtered.map((s) => {
              const c = categoryOf(s.loai, s.tenSanPham);
              const n = inCart.get(s.maSanPham);
              const out = s.soLuongTon <= 0;
              return (
                <button
                  key={s.maSanPham}
                  className={`product-tile cat cat-${c.key}${out ? ' disabled' : ''}`}
                  onClick={() => add(s)}
                  title={s.moTa ?? s.tenSanPham}
                >
                  <span className="tile-top">
                    <span className="tile-icon" aria-hidden="true">
                      <c.icon size={16} />
                    </span>
                    {n ? <span className="in-cart" aria-label={`Đã chọn ${n}`}>{n}</span> : null}
                  </span>
                  <span className="name">{s.tenSanPham}</span>
                  <span className="price">{formatMoney(s.gia)}</span>
                  <span className={`left${out ? ' out' : s.soLuongTon <= 30 ? ' low' : ''}`}>
                    {out ? 'Hết hàng' : `Còn ${s.soLuongTon} ${s.donViTinh ?? ''}`}
                  </span>
                </button>
              );
            })}
          </div>
          {filtered.length === 0 && <p className="grid-empty">Không có sản phẩm nào khớp với “{q}”.</p>}

          {cart.length > 0 && (
            <div className="cart-bar">
              <span>
                <strong>{itemCount}</strong> sản phẩm trong hóa đơn
              </span>
              <button className="btn btn-sm" onClick={() => receiptRef.current?.scrollIntoView({ behavior: 'smooth' })}>
                Xem hóa đơn
              </button>
            </div>
          )}
        </section>

        <div className="receipt-wrap" ref={receiptRef}>
          <section className="receipt" aria-label="Hóa đơn đang lập">
            <div className="receipt-head">
              <h2>Hóa đơn bán hàng</h2>
              <p>Thu ngân: {user?.hoTen || user?.maNhanVien}</p>
            </div>

            {cart.length === 0 ? (
              <div className="receipt-empty">
                <ShoppingBasket size={30} aria-hidden="true" />
                Chưa có sản phẩm nào. Bấm vào sản phẩm bên cạnh để thêm.
              </div>
            ) : (
              <ul className="receipt-lines">
                {cart.map((l) => (
                  <li key={l.sp.maSanPham}>
                    <div className="line-top">
                      <span className="line-name">{l.sp.tenSanPham}</span>
                      <span className="line-total">{formatMoney(l.sp.gia * (l.soLuong || 0))}</span>
                    </div>
                    <div className="line-bottom">
                      <span className="stepper">
                        <button onClick={() => step(l, -1)} aria-label={`Bớt một ${l.sp.tenSanPham}`}>
                          <Minus size={14} />
                        </button>
                        <input
                          type="number"
                          min={1}
                          max={l.sp.soLuongTon}
                          value={Number.isNaN(l.soLuong) ? '' : l.soLuong}
                          onChange={(e) => setQty(l.sp.maSanPham, e.target.valueAsNumber)}
                          aria-label={`Số lượng ${l.sp.tenSanPham}`}
                        />
                        <button
                          onClick={() => step(l, 1)}
                          disabled={l.soLuong >= l.sp.soLuongTon}
                          aria-label={`Thêm một ${l.sp.tenSanPham}`}
                        >
                          <Plus size={14} />
                        </button>
                      </span>
                      <span>
                        × {formatMoney(l.sp.gia)}
                        {l.soLuong > l.sp.soLuongTon && <span className="text-danger block">Chỉ còn {l.sp.soLuongTon}</span>}
                      </span>
                      <button className="icon-btn danger" onClick={() => remove(l.sp.maSanPham)} aria-label={`Bỏ ${l.sp.tenSanPham}`}>
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}

            <div className="receipt-fields">
              <label className="field">
                <span>Khách hàng</span>
                <select value={maKH} onChange={(e) => setMaKH(e.target.value)}>
                  <option value="">Khách lẻ</option>
                  {(customers.data ?? []).map((k) => (
                    <option key={k.maKhachHang} value={k.maKhachHang}>
                      {k.hoTen}
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
                      {k.tenKhuyenMai}: giảm {k.phanTramGiam}%{k.dieuKien ? `, đơn ${k.dieuKien.replace('>=', 'từ ')}` : ''}
                    </option>
                  ))}
                </select>
                {promos.data && promos.data.length === 0 && <em className="field-hint">Hôm nay không có khuyến mãi nào.</em>}
              </label>
            </div>

            <div className="totals">
              <div>
                <span>Tổng tiền hàng</span>
                <span>{formatMoney(preview?.tongTien ?? localTotal)}</span>
              </div>
              <div>
                <span>Giảm giá{preview?.phanTramGiam ? ` ${preview.phanTramGiam}%` : ''}</span>
                <span>{(preview?.tienGiam ?? 0) > 0 ? `−${formatMoney(preview?.tienGiam)}` : formatMoney(0)}</span>
              </div>
              <div className="grand">
                <span>Khách trả</span>
                <strong>{formatMoney(preview?.thanhTien ?? localTotal)}</strong>
              </div>
            </div>

            {previewError && (
              <div className="alert alert-error" role="alert">
                <AlertCircle size={16} aria-hidden="true" />
                <span>{previewError}</span>
              </div>
            )}

            <div className="receipt-actions">
              <button className="btn" onClick={reset} disabled={cart.length === 0 || saving}>
                Hủy
              </button>
              <button className="btn btn-primary" onClick={submit} disabled={!canSubmit}>
                {saving ? 'Đang lưu…' : previewing ? 'Đang tính…' : 'Lập hóa đơn'}
              </button>
            </div>
          </section>
        </div>
      </div>

      {done && (
        <Modal
          title={`Đã lập hóa đơn ${done.hoaDon.maHoaDon}`}
          onClose={() => setDone(null)}
          className="receipt-modal"
          footer={
            <>
              <PrintButton label="In hóa đơn" />
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
    <div className="receipt print-area">
      <p className="receipt-store">Cửa hàng thực phẩm</p>
      <div className="receipt-head">
        <h2>Hóa đơn {h.maHoaDon}</h2>
        <p>{formatDate(h.thoiGian)}</p>
      </div>
      <dl className="meta">
        <dt>Khách hàng</dt>
        <dd>{h.maKhachHang ? h.tenKhachHang || h.maKhachHang : 'Khách lẻ'}</dd>
        <dt>Thu ngân</dt>
        <dd>{h.tenNhanVien || h.maNhanVien}</dd>
        {h.maKhuyenMai && (
          <>
            <dt>Khuyến mãi</dt>
            <dd>{h.maKhuyenMai}</dd>
          </>
        )}
      </dl>
      <ul className="receipt-lines">
        {hd.items.map((i) => (
          <li key={i.maSanPham}>
            <div className="line-top">
              <span className="line-name">{i.tenSanPham}</span>
              <span className="line-total">{formatMoney(i.thanhTien)}</span>
            </div>
            <div className="line-bottom">
              <span>
                {i.soLuong} {i.donViTinh ?? ''} × {formatMoney(i.donGia)}
              </span>
            </div>
          </li>
        ))}
      </ul>
      <div className="totals">
        <div>
          <span>Tổng tiền hàng</span>
          <span>{formatMoney(h.tongTien)}</span>
        </div>
        <div>
          <span>Giảm giá</span>
          <span>{h.tienGiam > 0 ? `−${formatMoney(h.tienGiam)}` : formatMoney(0)}</span>
        </div>
        <div className="grand">
          <span>Thành tiền</span>
          <strong>{formatMoney(h.thanhTien)}</strong>
        </div>
      </div>
    </div>
  );
}
