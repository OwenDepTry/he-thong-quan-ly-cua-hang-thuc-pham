import { useEffect, useMemo, useState, type FormEvent } from 'react';
import { useSearchParams } from 'react-router-dom';
import { PackagePlus, Plus, Search, Trash2 } from 'lucide-react';
import { ApiError, api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { addDays, formatDate, formatMoney, formatNumber, isoDate, parseMoneyInput } from '../lib/format';
import type { NhaCungCap, PhieuNhapChiTiet, SanPham } from '../lib/types';
import { Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';
import { Field, Pager, PrintButton } from '../components/form';

const PAGE_SIZE = 20;

export default function NhapHangPage() {
  const { isQuanLy } = useAuth();
  const toast = useToast();
  const [params, setParams] = useSearchParams();
  const [filter, setFilter] = useState({ q: '', tuNgay: '', denNgay: '' });
  const [applied, setApplied] = useState(filter);
  const [page, setPage] = useState(0);
  const [detail, setDetail] = useState<PhieuNhapChiTiet | null>(null);
  // ?sp=SP012 (từ Tổng quan "Cần xử lý") mở sẵn form với sản phẩm đó
  const [creating, setCreating] = useState<string[] | null>(() => (params.get('sp') ? [params.get('sp')!] : null));

  const list = useLoad(
    () => api.phieuNhap({ ...applied, page, size: PAGE_SIZE }),
    [applied.q, applied.tuNgay, applied.denNgay, page],
  );

  function search(e: FormEvent) {
    e.preventDefault();
    setPage(0);
    setApplied(filter);
  }

  async function open(ma: string) {
    try {
      setDetail(await api.chiTietPhieuNhap(ma));
    } catch (e) {
      toast.error((e as Error).message);
    }
  }

  function closeForm() {
    setCreating(null);
    if (params.has('sp')) setParams({}, { replace: true });
  }

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Nhập hàng</h1>
          <p>
            {isQuanLy ? 'Phiếu nhập của cả cửa hàng' : 'Phiếu nhập do bạn lập'}
            {list.data ? `, ${formatNumber(list.data.total)} phiếu` : ''}. Nhập hàng sẽ cộng ngay vào tồn kho.
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setCreating([])}>
          <PackagePlus size={17} aria-hidden="true" /> Lập phiếu nhập
        </button>
      </header>

      <form className="toolbar" onSubmit={search}>
        <div className="search-box">
          <Search size={17} aria-hidden="true" />
          <input
            placeholder="Mã phiếu hoặc tên nhà cung cấp"
            value={filter.q}
            onChange={(e) => setFilter({ ...filter, q: e.target.value })}
            aria-label="Tìm phiếu nhập"
          />
        </div>
        <label className="inline">
          Từ
          <input type="date" value={filter.tuNgay} onChange={(e) => setFilter({ ...filter, tuNgay: e.target.value })} />
        </label>
        <label className="inline">
          Đến
          <input type="date" value={filter.denNgay} onChange={(e) => setFilter({ ...filter, denNgay: e.target.value })} />
        </label>
        <button className="btn btn-primary" type="submit">
          Tìm
        </button>
      </form>

      {list.error ? (
        <ErrorState message={list.error} onRetry={list.reload} />
      ) : !list.data ? (
        <Loading />
      ) : list.data.items.length === 0 ? (
        <Empty>Chưa có phiếu nhập nào khớp bộ lọc.</Empty>
      ) : (
        <>
          <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Phiếu nhập</th>
                <th>Nhà cung cấp</th>
                {isQuanLy && <th className="hide-sm">Người nhập</th>}
                <th className="num hide-sm">Mặt hàng</th>
                <th className="num">Tổng tiền</th>
              </tr>
            </thead>
            <tbody>
              {list.data.items.map((p) => (
                <tr key={p.maPhieuNhap} className="clickable" onClick={() => open(p.maPhieuNhap)} tabIndex={0} onKeyDown={(e) => e.key === 'Enter' && open(p.maPhieuNhap)}>
                  <td>
                    <strong>{p.maPhieuNhap}</strong>
                    <small className="muted block">{formatDate(p.thoiGian)}</small>
                  </td>
                  <td>{p.tenNhaCungCap || p.maNhaCungCap || '—'}</td>
                  {isQuanLy && <td className="hide-sm">{p.tenNhanVien || p.maNhanVien}</td>}
                  <td className="num hide-sm">{p.soMatHang}</td>
                  <td className="num">{formatMoney(p.tongTien)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          </div>
          <Pager page={page} total={list.data.total} size={PAGE_SIZE} busy={list.loading} onPage={setPage} />
        </>
      )}

      {creating && (
        <PhieuNhapForm
          preselect={creating}
          onClose={closeForm}
          onSaved={(pn) => {
            closeForm();
            toast.success(`Đã lập phiếu nhập ${pn.phieuNhap.maPhieuNhap}, tồn kho đã được cộng`);
            setDetail(pn);
            setPage(0);
            list.reload();
          }}
        />
      )}

      {detail && (
        <Modal
          title="Chi tiết phiếu nhập"
          onClose={() => setDetail(null)}
          className="receipt-modal"
          footer={
            <>
              <button className="btn" onClick={() => setDetail(null)}>
                Đóng
              </button>
              <PrintButton label="In phiếu" />
            </>
          }
        >
          <PhieuNhapView pn={detail} />
        </Modal>
      )}
    </div>
  );
}

function PhieuNhapView({ pn }: { pn: PhieuNhapChiTiet }) {
  const p = pn.phieuNhap;
  return (
    <div className="receipt print-area">
      <p className="receipt-store">Cửa hàng thực phẩm</p>
      <div className="receipt-head">
        <h2>Phiếu nhập {p.maPhieuNhap}</h2>
        <p>{formatDate(p.thoiGian)}</p>
      </div>
      <dl className="meta">
        <dt>Nhà cung cấp</dt>
        <dd>{p.tenNhaCungCap || p.maNhaCungCap}</dd>
        <dt>Người nhập</dt>
        <dd>{p.tenNhanVien || p.maNhanVien}</dd>
      </dl>
      <ul className="receipt-lines">
        {pn.items.map((it) => {
          const lo = pn.loHangMoi.find((l) => l.maSanPham === it.maSanPham);
          return (
            <li key={it.maSanPham}>
              <div className="line-top">
                <span className="line-name">{it.tenSanPham}</span>
                <span className="line-total">{formatMoney(it.thanhTien)}</span>
              </div>
              <div className="line-bottom">
                <span>
                  {formatNumber(it.soLuong)} {it.donViTinh} × {formatMoney(it.donGia)}
                </span>
                {lo && <span>Lô {lo.maHang}, hạn {formatDate(lo.hanSuDung)}</span>}
              </div>
            </li>
          );
        })}
      </ul>
      <div className="totals">
        <div className="grand">
          <span>Tổng tiền nhập</span>
          <strong>{formatMoney(p.tongTien)}</strong>
        </div>
      </div>
    </div>
  );
}

interface Dong {
  key: number;
  maSanPham: string;
  soLuong: string;
  donGia: string;
  hanSuDung: string;
}

function PhieuNhapForm({
  preselect,
  onClose,
  onSaved,
}: {
  preselect: string[];
  onClose: () => void;
  onSaved: (pn: PhieuNhapChiTiet) => void;
}) {
  const ncc = useLoad(() => api.nhaCungCap(), []);
  const sp = useLoad(() => api.sanPham(), []);
  const [maNCC, setMaNCC] = useState('');
  const [lines, setLines] = useState<Dong[]>([]);
  const [nextKey, setNextKey] = useState(1);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const today = isoDate(new Date());

  const byMa = useMemo(() => new Map((sp.data ?? []).map((s) => [s.maSanPham, s])), [sp.data]);

  /** Dòng mới cho một sản phẩm; hạn dùng gợi ý = hôm nay + số ngày sử dụng của sản phẩm. */
  function newLine(key: number, s?: SanPham): Dong {
    const days = s?.hanSuDung ? Number(s.hanSuDung) : NaN;
    return {
      key,
      maSanPham: s?.maSanPham ?? '',
      soLuong: '',
      donGia: '',
      hanSuDung: Number.isFinite(days) && days > 0 ? addDays(today, days) : '',
    };
  }

  // Khi danh sách sản phẩm tải xong: thêm sẵn các sản phẩm được chọn trước, hoặc một dòng trống
  useEffect(() => {
    if (!sp.data || lines.length) return;
    const pre = preselect.map((m) => byMa.get(m)).filter((s): s is SanPham => !!s);
    const init = pre.length ? pre.map((s, i) => newLine(i + 1, s)) : [newLine(1)];
    setLines(init);
    setNextKey(init.length + 1);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sp.data]);

  useEffect(() => {
    if (!maNCC && ncc.data?.length) setMaNCC(ncc.data[0].maNhaCungCap);
  }, [ncc.data, maNCC]);

  function update(key: number, patch: Partial<Dong>) {
    setLines((ls) =>
      ls.map((l) => {
        if (l.key !== key) return l;
        // Đổi sản phẩm thì gợi ý lại hạn dùng theo sản phẩm mới
        if (patch.maSanPham !== undefined && patch.maSanPham !== l.maSanPham) {
          return { ...newLine(key, byMa.get(patch.maSanPham)), soLuong: l.soLuong, donGia: l.donGia };
        }
        return { ...l, ...patch };
      }),
    );
  }

  function addLine() {
    setLines((ls) => [...ls, newLine(nextKey)]);
    setNextKey((k) => k + 1);
  }

  const tong = lines.reduce((s, l) => s + (Number(l.soLuong) || 0) * (parseMoneyInput(l.donGia) ?? 0), 0);
  const chosen = new Set(lines.map((l) => l.maSanPham));

  function validate() {
    const e: Record<string, string> = {};
    if (!maNCC) e.ncc = 'Vui lòng chọn nhà cung cấp';
    const filled = lines.filter((l) => l.maSanPham);
    if (filled.length === 0) e.form = 'Phiếu nhập chưa có sản phẩm nào';
    const seen = new Set<string>();
    for (const l of filled) {
      if (seen.has(l.maSanPham)) e[`sp-${l.key}`] = 'Sản phẩm bị chọn trùng';
      seen.add(l.maSanPham);
      if (!/^\d+$/.test(l.soLuong.trim()) || Number(l.soLuong) <= 0) e[`sl-${l.key}`] = 'Số lượng lớn hơn 0';
      const g = parseMoneyInput(l.donGia);
      if (g === null || g < 0) e[`gia-${l.key}`] = 'Nhập giá nhập';
      if (l.hanSuDung && l.hanSuDung < today) e[`han-${l.key}`] = 'Hạn dùng đã qua';
    }
    return e;
  }

  async function submit(ev: FormEvent) {
    ev.preventDefault();
    const e = validate();
    setErrors(e);
    setFormError(e.form ?? null);
    if (Object.keys(e).length) return;
    setBusy(true);
    try {
      const pn = await api.taoPhieuNhap({
        maNhaCungCap: maNCC,
        items: lines
          .filter((l) => l.maSanPham)
          .map((l) => ({
            maSanPham: l.maSanPham,
            soLuong: Number(l.soLuong),
            donGia: parseMoneyInput(l.donGia) ?? 0,
            hanSuDung: l.hanSuDung || null,
          })),
      });
      onSaved(pn);
    } catch (err) {
      setFormError(err instanceof ApiError ? err.message : 'Không lưu được phiếu nhập.');
      setBusy(false);
    }
  }

  const loading = !ncc.data || !sp.data;
  const loadError = ncc.error || sp.error;

  return (
    <Modal
      title="Lập phiếu nhập"
      onClose={onClose}
      wide
      className="import-modal"
      footer={
        <>
          <span className="modal-total">
            Tổng tiền nhập <strong>{formatMoney(tong)}</strong>
          </span>
          <button className="btn" type="button" onClick={onClose} disabled={busy}>
            Hủy
          </button>
          <button className="btn btn-primary" type="submit" form="pn-form" disabled={busy || loading}>
            {busy ? 'Đang lưu…' : 'Lưu phiếu nhập'}
          </button>
        </>
      }
    >
      {loadError ? (
        <ErrorState message={loadError} />
      ) : loading ? (
        <Loading />
      ) : (
        <form id="pn-form" onSubmit={submit} noValidate>
          <Field id="pn-ncc" label="Nhà cung cấp" error={errors.ncc}>
            <select value={maNCC} onChange={(e) => setMaNCC(e.target.value)}>
              {(ncc.data as NhaCungCap[]).map((n) => (
                <option key={n.maNhaCungCap} value={n.maNhaCungCap}>
                  {n.ten}
                  {n.soDienThoai ? `, ${n.soDienThoai}` : ''}
                </option>
              ))}
            </select>
          </Field>

          <div className="import-lines" role="group" aria-label="Sản phẩm nhập">
            <div className="import-head" aria-hidden="true">
              <span>Sản phẩm</span>
              <span>Số lượng</span>
              <span>Giá nhập</span>
              <span>Hạn sử dụng</span>
              <span />
            </div>
            {lines.map((l, i) => {
              const s = byMa.get(l.maSanPham);
              return (
                <div className="import-line" key={l.key}>
                  <Field id={`pn-sp-${l.key}`} label={<span className="line-label">Sản phẩm<span className="sr-only"> dòng {i + 1}</span></span>} error={errors[`sp-${l.key}`]} hint={s ? `Đang tồn ${formatNumber(s.soLuongTon)} ${s.donViTinh ?? ''}` : undefined}>
                    <select value={l.maSanPham} onChange={(e) => update(l.key, { maSanPham: e.target.value })}>
                      <option value="">Chọn sản phẩm…</option>
                      {(sp.data as SanPham[]).map((x) => (
                        <option key={x.maSanPham} value={x.maSanPham} disabled={x.maSanPham !== l.maSanPham && chosen.has(x.maSanPham)}>
                          {x.tenSanPham} ({x.maSanPham})
                        </option>
                      ))}
                    </select>
                  </Field>
                  <Field id={`pn-sl-${l.key}`} label={<span className="line-label">Số lượng<span className="sr-only"> dòng {i + 1}</span></span>} error={errors[`sl-${l.key}`]} hint={s?.donViTinh ?? undefined}>
                    <input value={l.soLuong} onChange={(e) => update(l.key, { soLuong: e.target.value })} inputMode="numeric" placeholder="0" />
                  </Field>
                  <Field id={`pn-gia-${l.key}`} label={<span className="line-label">Giá nhập<span className="sr-only"> dòng {i + 1}</span></span>} error={errors[`gia-${l.key}`]} hint={s ? `Giá bán ${formatMoney(s.gia)}` : undefined}>
                    <input value={l.donGia} onChange={(e) => update(l.key, { donGia: e.target.value })} inputMode="numeric" placeholder="VD: 18.000" />
                  </Field>
                  <Field id={`pn-han-${l.key}`} label={<span className="line-label">Hạn sử dụng<span className="sr-only"> dòng {i + 1}</span></span>} error={errors[`han-${l.key}`]} hint={l.hanSuDung ? 'Tạo lô để theo dõi hạn' : 'Không theo dõi hạn'}>
                    <input type="date" min={today} value={l.hanSuDung} onChange={(e) => update(l.key, { hanSuDung: e.target.value })} />
                  </Field>
                  <button
                    type="button"
                    className="icon-btn danger"
                    onClick={() => setLines((ls) => ls.filter((x) => x.key !== l.key))}
                    aria-label={`Bỏ dòng ${i + 1}`}
                    title="Bỏ dòng"
                    disabled={lines.length === 1}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              );
            })}
            <button type="button" className="btn btn-ghost add-line" onClick={addLine}>
              <Plus size={16} aria-hidden="true" /> Thêm dòng
            </button>
          </div>
          {formError && <div className="alert alert-error">{formError}</div>}
        </form>
      )}
    </Modal>
  );
}
