import { cloneElement, useMemo, useState, type FormEvent, type ReactElement } from 'react';
import { ArrowDown, ArrowUp, Pencil, Plus, Search, Trash2 } from 'lucide-react';
import { categoryOf } from '../lib/category';
import { ApiError, api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatMoney, formatNumber, normalize, parseMoneyInput } from '../lib/format';
import type { LoaiSanPham, SanPham, SanPhamInput } from '../lib/types';
import { ConfirmDialog, Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';

type SortKey = 'maSanPham' | 'tenSanPham' | 'gia' | 'soLuongTon';

export default function SanPhamPage() {
  const { isQuanLy } = useAuth();
  const toast = useToast();
  const list = useLoad(() => api.sanPham(), []);
  const loai = useLoad(() => api.loaiSanPham(), []);

  const [q, setQ] = useState('');
  const [loaiFilter, setLoaiFilter] = useState('');
  const [sort, setSort] = useState<{ key: SortKey; asc: boolean }>({ key: 'maSanPham', asc: true });
  const [editing, setEditing] = useState<SanPham | 'new' | null>(null);
  const [deleting, setDeleting] = useState<SanPham | null>(null);
  const [busy, setBusy] = useState(false);

  // Lọc tại trình duyệt: danh sách nhỏ, gõ tới đâu lọc tới đó, không phân biệt dấu
  const rows = useMemo(() => {
    const k = normalize(q);
    const data = (list.data ?? []).filter(
      (s) =>
        (!k || normalize(`${s.maSanPham} ${s.tenSanPham} ${s.moTa ?? ''}`).includes(k)) &&
        (!loaiFilter || s.loai === loaiFilter),
    );
    const dir = sort.asc ? 1 : -1;
    return [...data].sort((a, b) => {
      const x = a[sort.key];
      const y = b[sort.key];
      return (typeof x === 'number' && typeof y === 'number' ? x - y : String(x).localeCompare(String(y), 'vi')) * dir;
    });
  }, [list.data, q, loaiFilter, sort]);

  function toggleSort(key: SortKey) {
    setSort((s) => (s.key === key ? { key, asc: !s.asc } : { key, asc: true }));
  }

  async function confirmDelete() {
    if (!deleting) return;
    setBusy(true);
    try {
      await api.xoaSanPham(deleting.maSanPham);
      toast.success(`Đã xóa ${deleting.tenSanPham}`);
      setDeleting(null);
      list.reload();
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setBusy(false);
    }
  }

  const header = (key: SortKey, label: string, cls = '') => (
    <th className={`sortable ${cls}`} onClick={() => toggleSort(key)} aria-sort={sort.key === key ? (sort.asc ? 'ascending' : 'descending') : 'none'}>
      {label}
      {sort.key === key && <span className="sort">{sort.asc ? <ArrowUp size={14} /> : <ArrowDown size={14} />}</span>}
    </th>
  );
  const maxStock = Math.max(100, ...(list.data ?? []).map((s) => s.soLuongTon));

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Sản phẩm</h1>
          <p>
            {list.data ? (rows.length === list.data.length ? `${list.data.length} sản phẩm` : `Đang hiện ${rows.length} trên ${list.data.length} sản phẩm`) : ' '}
            {!isQuanLy && '. Chỉ quản lý được thêm, sửa, xóa'}
          </p>
        </div>
        {isQuanLy && (
          <button className="btn btn-primary" onClick={() => setEditing('new')}>
            <Plus size={17} aria-hidden="true" /> Thêm sản phẩm
          </button>
        )}
      </header>

      <div className="toolbar">
        <div className="search-box">
          <Search size={17} aria-hidden="true" />
          <input
            placeholder="Tìm theo tên, mã hoặc mô tả, gõ không dấu cũng được"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            aria-label="Tìm sản phẩm"
          />
        </div>
      </div>
      {(loai.data ?? []).length > 0 && (
        <div className="filters" role="group" aria-label="Lọc theo loại" style={{ marginBottom: 16 }}>
          <button className="filter" aria-pressed={loaiFilter === ''} onClick={() => setLoaiFilter('')}>
            Tất cả loại
          </button>
          {(loai.data ?? []).map((l) => {
            const c = categoryOf(l.tenLoai);
            return (
              <button
                key={l.maLoai}
                className={`filter cat cat-${c.key}`}
                aria-pressed={loaiFilter === l.tenLoai}
                onClick={() => setLoaiFilter(loaiFilter === l.tenLoai ? '' : l.tenLoai)}
              >
                <c.icon size={15} aria-hidden="true" /> {l.tenLoai}
              </button>
            );
          })}
        </div>
      )}

      {list.loading && !list.data && <Loading />}
      {list.error && <ErrorState message={list.error} onRetry={list.reload} />}
      {list.data && rows.length === 0 && <Empty>Không có sản phẩm phù hợp.</Empty>}
      {list.data && rows.length > 0 && (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                {header('tenSanPham', 'Sản phẩm')}
                <th className="hide-sm">Loại</th>
                {header('gia', 'Giá bán', 'num')}
                {header('soLuongTon', 'Tồn kho', 'num')}
                {isQuanLy && <th className="num"><span className="sr-only">Thao tác</span></th>}
              </tr>
            </thead>
            <tbody>
              {rows.map((s) => (
                <tr key={s.maSanPham} onDoubleClick={() => isQuanLy && setEditing(s)}>
                  <td>
                    <strong>{s.tenSanPham}</strong>
                    <small className="muted block">
                      {s.maSanPham}
                      {s.moTa ? `, ${s.moTa}` : ''}
                    </small>
                  </td>
                  <td className="hide-sm">
                    {s.loai ? (
                      <span className={`chip cat cat-${categoryOf(s.loai, s.tenSanPham).key}`}>{s.loai}</span>
                    ) : (
                      <span className="muted">—</span>
                    )}
                  </td>
                  <td className="num">
                    {formatMoney(s.gia)}
                    <small className="muted block">mỗi {s.donViTinh ?? 'đơn vị'}</small>
                  </td>
                  <td className="num">
                    <span className={`stock${s.soLuongTon <= 0 ? ' out' : s.soLuongTon <= 30 ? ' low' : ''}`}>
                      <span className={s.soLuongTon <= 0 ? 'text-danger' : s.soLuongTon <= 30 ? 'text-warn' : ''}>
                        {s.soLuongTon <= 0 ? 'Hết hàng' : formatNumber(s.soLuongTon)}
                      </span>
                      <span className="stock-bar hide-sm" aria-hidden="true">
                        <i style={{ width: `${Math.min(100, (s.soLuongTon / maxStock) * 100)}%` }} />
                      </span>
                    </span>
                  </td>
                  {isQuanLy && (
                    <td className="num nowrap">
                      <span className="row-actions">
                        <button className="icon-btn" onClick={() => setEditing(s)} aria-label={`Sửa ${s.tenSanPham}`} title="Sửa">
                          <Pencil size={16} />
                        </button>
                        <button className="icon-btn danger" onClick={() => setDeleting(s)} aria-label={`Xóa ${s.tenSanPham}`} title="Xóa">
                          <Trash2 size={16} />
                        </button>
                      </span>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {editing && (
        <SanPhamForm
          initial={editing === 'new' ? null : editing}
          loai={loai.data ?? []}
          onClose={() => setEditing(null)}
          onSaved={(sp, isNew) => {
            toast.success(isNew ? `Đã thêm ${sp.tenSanPham}` : `Đã lưu ${sp.tenSanPham}`);
            setEditing(null);
            list.reload();
          }}
        />
      )}
      {deleting && (
        <ConfirmDialog
          title="Xóa sản phẩm"
          message={
            <>
              Xóa <strong>{deleting.tenSanPham}</strong> ({deleting.maSanPham})? Sản phẩm đã có trong hóa đơn hoặc phiếu
              nhập sẽ không xóa được.
            </>
          }
          confirmText="Xóa"
          danger
          busy={busy}
          onConfirm={confirmDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}

function SanPhamForm({
  initial,
  loai,
  onClose,
  onSaved,
}: {
  initial: SanPham | null;
  loai: LoaiSanPham[];
  onClose: () => void;
  onSaved: (sp: SanPham, isNew: boolean) => void;
}) {
  const isNew = initial === null;
  const [f, setF] = useState({
    maSanPham: initial?.maSanPham ?? '',
    maNhom: initial?.maNhom ?? '',
    tenSanPham: initial?.tenSanPham ?? '',
    loai: initial?.loai ?? loai[0]?.tenLoai ?? '',
    donViTinh: initial?.donViTinh ?? 'Kg',
    hanSuDung: initial?.hanSuDung ?? '',
    moTa: initial?.moTa ?? '',
    gia: initial ? formatNumber(initial.gia) : '',
    soLuongTon: initial ? String(initial.soLuongTon) : '0',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const set = (k: keyof typeof f) => (e: { target: { value: string } }) => setF({ ...f, [k]: e.target.value });

  function validate(): Record<string, string> {
    const e: Record<string, string> = {};
    if (isNew && !f.maSanPham.trim()) e.maSanPham = 'Vui lòng nhập mã sản phẩm';
    else if (isNew && !/^[A-Za-z0-9_-]+$/.test(f.maSanPham.trim())) e.maSanPham = 'Mã chỉ gồm chữ, số, - và _';
    if (!f.tenSanPham.trim()) e.tenSanPham = 'Vui lòng nhập tên sản phẩm';
    if (!f.donViTinh.trim()) e.donViTinh = 'Vui lòng nhập đơn vị tính';
    const gia = parseMoneyInput(f.gia);
    if (gia === null || gia < 0) e.gia = 'Giá phải là số không âm';
    if (!/^\d+$/.test(f.soLuongTon.trim())) e.soLuongTon = 'Tồn kho là số nguyên không âm';
    if (f.hanSuDung.trim() && !/^\d{1,5}$/.test(f.hanSuDung.trim())) e.hanSuDung = 'Hạn sử dụng là số ngày';
    return e;
  }

  async function submit(ev: FormEvent) {
    ev.preventDefault();
    const e = validate();
    setErrors(e);
    setFormError(null);
    if (Object.keys(e).length) return;

    const body: SanPhamInput = {
      maSanPham: f.maSanPham.trim(),
      maNhom: f.maNhom.trim() || null,
      tenSanPham: f.tenSanPham.trim(),
      loai: f.loai || null,
      donViTinh: f.donViTinh.trim(),
      hanSuDung: f.hanSuDung.trim() || null,
      moTa: f.moTa.trim() || null,
      gia: parseMoneyInput(f.gia),
      soLuongTon: Number(f.soLuongTon.trim()),
    };
    setBusy(true);
    try {
      const saved = isNew ? await api.themSanPham(body) : await api.suaSanPham(initial.maSanPham, body);
      onSaved(saved, isNew);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setFormError(err.message);
      } else {
        setFormError('Không lưu được sản phẩm.');
      }
      setBusy(false);
    }
  }

  const field = (k: keyof typeof f, label: string, input: ReactElement<Record<string, unknown>>, hint?: string) => {
    const id = `sp-${k}`;
    const msg = errors[k] ?? hint;
    return (
      <div className={`field${errors[k] ? ' has-error' : ''}`}>
        <label htmlFor={id}>{label}</label>
        {cloneElement(input, {
          id,
          'aria-invalid': errors[k] ? true : undefined,
          'aria-describedby': msg ? `${id}-msg` : undefined,
        })}
        {msg && (
          <em id={`${id}-msg`} className={errors[k] ? 'field-error' : 'field-hint'}>
            {msg}
          </em>
        )}
      </div>
    );
  };

  return (
    <Modal
      title={isNew ? 'Thêm sản phẩm' : `Sửa sản phẩm ${initial.maSanPham}`}
      onClose={onClose}
      footer={
        <>
          <button className="btn" type="button" onClick={onClose} disabled={busy}>
            Hủy
          </button>
          <button className="btn btn-primary" type="submit" form="sp-form" disabled={busy}>
            {busy ? 'Đang lưu…' : 'Lưu'}
          </button>
        </>
      }
    >
      <form id="sp-form" className="form-grid" onSubmit={submit} noValidate>
        {field('maSanPham', 'Mã sản phẩm *', <input value={f.maSanPham} onChange={set('maSanPham')} disabled={!isNew} placeholder="VD: SP011" autoFocus={isNew} />, isNew ? undefined : 'Không thể đổi mã khi sửa')}
        {field('maNhom', 'Mã nhóm', <input value={f.maNhom} onChange={set('maNhom')} placeholder="VD: RC04" />)}
        <div className="span-2">
          {field('tenSanPham', 'Tên sản phẩm *', <input value={f.tenSanPham} onChange={set('tenSanPham')} placeholder="VD: Bắp cải" autoFocus={!isNew} />)}
        </div>
        {field(
          'loai',
          'Loại',
          <select value={f.loai} onChange={set('loai')}>
            <option value="">— Không chọn —</option>
            {loai.map((l) => (
              <option key={l.maLoai} value={l.tenLoai}>
                {l.tenLoai}
              </option>
            ))}
            {f.loai && !loai.some((l) => l.tenLoai === f.loai) && <option value={f.loai}>{f.loai}</option>}
          </select>,
        )}
        {field('donViTinh', 'Đơn vị tính *', <input value={f.donViTinh} onChange={set('donViTinh')} placeholder="Kg, Hộp, Chai…" />)}
        {field('gia', 'Giá bán (đồng) *', <input value={f.gia} onChange={set('gia')} inputMode="numeric" placeholder="VD: 25.000" />)}
        {field('soLuongTon', 'Tồn kho *', <input value={f.soLuongTon} onChange={set('soLuongTon')} inputMode="numeric" />)}
        {field('hanSuDung', 'Hạn sử dụng (ngày)', <input value={f.hanSuDung} onChange={set('hanSuDung')} inputMode="numeric" placeholder="VD: 7" />)}
        <div className="span-2">{field('moTa', 'Mô tả', <input value={f.moTa} onChange={set('moTa')} />)}</div>
      </form>
      {formError && <div className="alert alert-error">{formError}</div>}
    </Modal>
  );
}
