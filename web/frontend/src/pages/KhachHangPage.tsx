import { useMemo, useState, type FormEvent } from 'react';
import { Pencil, Plus, Search, Trash2 } from 'lucide-react';
import { ApiError, api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatDate, formatMoney, formatNumber, isoDate, normalize } from '../lib/format';
import type { KhachHang, KhachHangInput, TrangThai } from '../lib/types';
import { ConfirmDialog, Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';
import { Field } from '../components/form';
import { useSort } from '../components/sortable';

type Loc = 'active' | 'inactive' | 'all';

export default function KhachHangPage() {
  const { isQuanLy } = useAuth();
  const toast = useToast();
  const list = useLoad(() => api.khachHang(undefined, { tatCa: true, limit: 500 }), []);
  const [q, setQ] = useState('');
  const [loc, setLoc] = useState<Loc>('active');
  const [editing, setEditing] = useState<KhachHang | 'new' | null>(null);
  const [deleting, setDeleting] = useState<KhachHang | null>(null);
  const [busy, setBusy] = useState(false);

  const filtered = useMemo(() => {
    const k = normalize(q);
    return (list.data ?? []).filter(
      (c) =>
        (loc === 'all' || c.trangThai === loc) &&
        (!k || normalize(`${c.maKhachHang} ${c.hoTen} ${c.soDienThoai ?? ''}`).includes(k)),
    );
  }, [list.data, q, loc]);
  // Mặc định khách chi tiêu nhiều nhất lên đầu
  const { sorted, header } = useSort(filtered, { key: 'tongChiTieu' as keyof KhachHang, asc: false });
  const count = (l: Loc) => (list.data ?? []).filter((c) => l === 'all' || c.trangThai === l).length;

  async function confirmDelete() {
    if (!deleting) return;
    setBusy(true);
    try {
      await api.xoaKhachHang(deleting.maKhachHang);
      toast.success(`Đã xóa khách hàng ${deleting.hoTen}`);
      setDeleting(null);
      list.reload();
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Khách hàng</h1>
          <p>
            Khách thành viên, xếp theo tổng chi tiêu.
            {!isQuanLy && ' Chỉ quản lý được thêm, sửa, xóa.'}
          </p>
        </div>
        {isQuanLy && (
          <button className="btn btn-primary" onClick={() => setEditing('new')}>
            <Plus size={17} aria-hidden="true" /> Thêm khách hàng
          </button>
        )}
      </header>

      <div className="toolbar">
        <div className="search-box">
          <Search size={17} aria-hidden="true" />
          <input placeholder="Tìm theo tên, mã hoặc số điện thoại" value={q} onChange={(e) => setQ(e.target.value)} aria-label="Tìm khách hàng" />
        </div>
        <div className="filters" role="group" aria-label="Lọc theo trạng thái">
          {(
            [
              ['active', 'Đang hoạt động'],
              ['inactive', 'Ngừng hoạt động'],
              ['all', 'Tất cả'],
            ] as [Loc, string][]
          ).map(([v, label]) => (
            <button key={v} className="filter" aria-pressed={loc === v} onClick={() => setLoc(v)}>
              {label} <span className="count">{count(v)}</span>
            </button>
          ))}
        </div>
      </div>

      {list.error ? (
        <ErrorState message={list.error} onRetry={list.reload} />
      ) : !list.data ? (
        <Loading />
      ) : sorted.length === 0 ? (
        <Empty>Không có khách hàng nào khớp.</Empty>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                {header('hoTen', 'Khách hàng')}
                <th className="hide-sm">Số điện thoại</th>
                {header('soHoaDon', 'Hóa đơn', 'num hide-sm', true)}
                {header('tongChiTieu', 'Tổng chi tiêu', 'num', true)}
                {header('lanMuaCuoi', 'Lần mua gần nhất', 'hide-sm', true)}
                {isQuanLy && (
                  <th className="num">
                    <span className="sr-only">Thao tác</span>
                  </th>
                )}
              </tr>
            </thead>
            <tbody>
              {sorted.map((c) => (
                <tr key={c.maKhachHang} onDoubleClick={() => isQuanLy && setEditing(c)}>
                  <td>
                    <strong>{c.hoTen}</strong>
                    {c.trangThai === 'inactive' && <span className="pill pill-muted">Ngừng</span>}
                    <small className="muted block">
                      {c.maKhachHang}
                      {c.tinh ? `, ${c.tinh}` : ''}
                    </small>
                  </td>
                  <td className="hide-sm">{c.soDienThoai ?? '—'}</td>
                  <td className="num hide-sm">{formatNumber(c.soHoaDon)}</td>
                  <td className="num">{formatMoney(c.tongChiTieu)}</td>
                  <td className="hide-sm">{c.lanMuaCuoi ? formatDate(c.lanMuaCuoi) : <span className="muted">Chưa mua</span>}</td>
                  {isQuanLy && (
                    <td className="num nowrap">
                      <span className="row-actions">
                        <button className="icon-btn" onClick={() => setEditing(c)} aria-label={`Sửa ${c.hoTen}`} title="Sửa">
                          <Pencil size={16} />
                        </button>
                        <button className="icon-btn danger" onClick={() => setDeleting(c)} aria-label={`Xóa ${c.hoTen}`} title="Xóa">
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
        <KhachHangForm
          initial={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={(kh, isNew) => {
            setEditing(null);
            toast.success(isNew ? `Đã thêm khách hàng ${kh.hoTen} (${kh.maKhachHang})` : `Đã lưu ${kh.hoTen}`);
            list.reload();
          }}
        />
      )}
      {deleting && (
        <ConfirmDialog
          title="Xóa khách hàng"
          message={
            <>
              Xóa <strong>{deleting.hoTen}</strong>? Khách đã có hóa đơn sẽ không xóa được, khi đó hãy chuyển sang Ngừng hoạt động.
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

function KhachHangForm({
  initial,
  onClose,
  onSaved,
}: {
  initial: KhachHang | null;
  onClose: () => void;
  onSaved: (kh: KhachHang, isNew: boolean) => void;
}) {
  const isNew = initial === null;
  const [f, setF] = useState({
    maKhachHang: initial?.maKhachHang ?? '',
    ho: initial?.ho ?? '',
    tenLot: initial?.tenLot ?? '',
    ten: initial?.ten ?? '',
    phai: initial?.phai ?? '',
    ngaySinh: initial?.ngaySinh ?? '',
    soDienThoai: initial?.soDienThoai ?? '',
    tinh: initial?.tinh ?? '',
    trangThai: (initial?.trangThai ?? 'active') as TrangThai,
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const set = (k: keyof typeof f) => (e: { target: { value: string } }) => setF({ ...f, [k]: e.target.value });

  async function submit(ev: FormEvent) {
    ev.preventDefault();
    const e: Record<string, string> = {};
    if (!f.ten.trim()) e.ten = 'Vui lòng nhập tên';
    if (!/^0\d{9,10}$/.test(f.soDienThoai.trim())) e.soDienThoai = 'Gồm 10 hoặc 11 chữ số, bắt đầu bằng 0';
    if (isNew && f.maKhachHang.trim() && !/^[A-Za-z0-9_-]+$/.test(f.maKhachHang.trim())) e.maKhachHang = 'Mã chỉ gồm chữ, số, - và _';
    if (f.ngaySinh && f.ngaySinh >= isoDate(new Date())) e.ngaySinh = 'Ngày sinh phải trước hôm nay';
    setErrors(e);
    setFormError(null);
    if (Object.keys(e).length) return;

    const body: KhachHangInput = {
      maKhachHang: isNew ? f.maKhachHang.trim() || null : undefined,
      ho: f.ho.trim() || null,
      tenLot: f.tenLot.trim() || null,
      ten: f.ten.trim(),
      phai: f.phai || null,
      ngaySinh: f.ngaySinh || null,
      soDienThoai: f.soDienThoai.trim(),
      tinh: f.tinh.trim() || null,
      trangThai: f.trangThai,
    };
    setBusy(true);
    try {
      onSaved(isNew ? await api.themKhachHang(body) : await api.suaKhachHang(initial.maKhachHang, body), isNew);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setFormError(err.message);
      } else setFormError('Không lưu được khách hàng.');
      setBusy(false);
    }
  }

  return (
    <Modal
      title={isNew ? 'Thêm khách hàng' : `Sửa khách hàng ${initial.maKhachHang}`}
      onClose={onClose}
      footer={
        <>
          <button className="btn" type="button" onClick={onClose} disabled={busy}>
            Hủy
          </button>
          <button className="btn btn-primary" type="submit" form="kh-form" disabled={busy}>
            {busy ? 'Đang lưu…' : 'Lưu'}
          </button>
        </>
      }
    >
      <form id="kh-form" className="form-grid" onSubmit={submit} noValidate>
        <Field id="kh-ho" label="Họ">
          <input value={f.ho} onChange={set('ho')} placeholder="VD: Nguyễn" autoFocus />
        </Field>
        <Field id="kh-tenlot" label="Tên lót">
          <input value={f.tenLot} onChange={set('tenLot')} placeholder="VD: Thị" />
        </Field>
        <Field id="kh-ten" label="Tên *" error={errors.ten}>
          <input value={f.ten} onChange={set('ten')} placeholder="VD: Lan" />
        </Field>
        <Field id="kh-sdt" label="Số điện thoại *" error={errors.soDienThoai}>
          <input value={f.soDienThoai} onChange={set('soDienThoai')} inputMode="tel" placeholder="VD: 0912345678" />
        </Field>
        <Field id="kh-phai" label="Giới tính">
          <select value={f.phai} onChange={set('phai')}>
            <option value="">Không ghi</option>
            <option value="nữ">Nữ</option>
            <option value="nam">Nam</option>
            <option value="khác">Khác</option>
          </select>
        </Field>
        <Field id="kh-ngaysinh" label="Ngày sinh" error={errors.ngaySinh}>
          <input type="date" value={f.ngaySinh} onChange={set('ngaySinh')} />
        </Field>
        <Field id="kh-tinh" label="Tỉnh, thành phố">
          <input value={f.tinh} onChange={set('tinh')} placeholder="VD: TP. Hồ Chí Minh" />
        </Field>
        {isNew ? (
          <Field id="kh-ma" label="Mã khách hàng" error={errors.maKhachHang} hint="Bỏ trống để hệ thống tự tạo">
            <input value={f.maKhachHang} onChange={set('maKhachHang')} placeholder="VD: KH031" />
          </Field>
        ) : (
          <Field id="kh-trangthai" label="Trạng thái" error={errors.trangThai}>
            <select value={f.trangThai} onChange={set('trangThai')}>
              <option value="active">Đang hoạt động</option>
              <option value="inactive">Ngừng hoạt động</option>
            </select>
          </Field>
        )}
      </form>
      {!isNew && (
        <p className="muted small">
          Tham gia {formatDate(initial.ngayThamGia)}, {formatNumber(initial.diem)} điểm tích lũy, {formatNumber(initial.soHoaDon)} hóa đơn.
        </p>
      )}
      {formError && <div className="alert alert-error">{formError}</div>}
    </Modal>
  );
}
