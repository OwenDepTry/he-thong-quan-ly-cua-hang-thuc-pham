import { useState, type FormEvent } from 'react';
import { Pencil, Plus } from 'lucide-react';
import { ApiError, api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatMoney, formatNumber, isoDate, parseMoneyInput } from '../lib/format';
import type { NhanVien, NhanVienInput, Role, TrangThai } from '../lib/types';
import { Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';
import { Field } from '../components/form';

export default function NhanVienPage() {
  const { user } = useAuth();
  const toast = useToast();
  const list = useLoad(() => api.nhanVien(), []);
  const [editing, setEditing] = useState<NhanVien | 'new' | null>(null);
  const data = list.data ?? [];
  const dangLam = data.filter((n) => n.trangThai === 'active').length;

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Nhân viên</h1>
          <p>
            {list.data ? `${dangLam} người đang làm, ${data.length - dangLam} đã nghỉ. ` : ''}Số liệu bán hàng tính trong tháng này.
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setEditing('new')}>
          <Plus size={17} aria-hidden="true" /> Thêm nhân viên
        </button>
      </header>

      {list.error ? (
        <ErrorState message={list.error} onRetry={list.reload} />
      ) : !list.data ? (
        <Loading />
      ) : data.length === 0 ? (
        <Empty>Chưa có nhân viên nào.</Empty>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Nhân viên</th>
                <th>Chức vụ</th>
                <th className="hide-sm">Số điện thoại</th>
                <th className="num hide-sm">Lương</th>
                <th className="num">Hóa đơn tháng này</th>
                <th className="num hide-sm">Doanh thu tháng này</th>
                <th className="num">
                  <span className="sr-only">Thao tác</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {data.map((n) => (
                <tr key={n.maNhanVien} className={n.trangThai === 'inactive' ? 'row-off' : ''} onDoubleClick={() => setEditing(n)}>
                  <td>
                    <strong>{n.hoTen || n.maNhanVien}</strong>
                    {n.maNhanVien === user?.maNhanVien && <span className="pill pill-ok">Bạn</span>}
                    <small className="muted block">
                      {n.maNhanVien}
                      {n.trangThai === 'inactive' ? ', đã nghỉ' : ''}
                    </small>
                  </td>
                  <td>
                    <span className={`pill ${n.chucVu === 'QL' ? 'pill-info' : 'pill-muted'}`}>{n.chucVu === 'QL' ? 'Quản lý' : 'Bán hàng'}</span>
                  </td>
                  <td className="hide-sm">{n.soDienThoai ?? '—'}</td>
                  <td className="num hide-sm">{formatMoney(n.luong)}</td>
                  <td className="num">{formatNumber(n.hoaDonThangNay)}</td>
                  <td className="num hide-sm">{formatMoney(n.doanhThuThangNay)}</td>
                  <td className="num">
                    <button className="icon-btn" onClick={() => setEditing(n)} aria-label={`Sửa ${n.hoTen}`} title="Sửa">
                      <Pencil size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {editing && (
        <NhanVienForm
          initial={editing === 'new' ? null : editing}
          isSelf={editing !== 'new' && editing.maNhanVien === user?.maNhanVien}
          onClose={() => setEditing(null)}
          onSaved={(nv, isNew) => {
            setEditing(null);
            toast.success(isNew ? `Đã thêm ${nv.hoTen}, mã đăng nhập ${nv.maNhanVien}` : `Đã lưu ${nv.hoTen}`);
            list.reload();
          }}
        />
      )}
    </div>
  );
}

function NhanVienForm({
  initial,
  isSelf,
  onClose,
  onSaved,
}: {
  initial: NhanVien | null;
  isSelf: boolean;
  onClose: () => void;
  onSaved: (nv: NhanVien, isNew: boolean) => void;
}) {
  const isNew = initial === null;
  const [f, setF] = useState({
    maNhanVien: initial?.maNhanVien ?? '',
    ho: initial?.ho ?? '',
    tenLot: initial?.tenLot ?? '',
    ten: initial?.ten ?? '',
    phai: initial?.phai ?? '',
    ngaySinh: initial?.ngaySinh ?? '',
    soDienThoai: initial?.soDienThoai ?? '',
    tinh: initial?.tinh ?? '',
    diaChi: initial?.diaChi ?? '',
    luong: initial ? formatNumber(initial.luong) : '',
    chucVu: (initial?.chucVu ?? 'NV') as Role,
    trangThai: (initial?.trangThai ?? 'active') as TrangThai,
    matKhau: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const set = (k: keyof typeof f) => (e: { target: { value: string } }) => setF({ ...f, [k]: e.target.value });

  async function submit(ev: FormEvent) {
    ev.preventDefault();
    const e: Record<string, string> = {};
    if (!f.ten.trim()) e.ten = 'Vui lòng nhập tên';
    if (f.soDienThoai.trim() && !/^0\d{9,10}$/.test(f.soDienThoai.trim())) e.soDienThoai = 'Gồm 10 hoặc 11 chữ số, bắt đầu bằng 0';
    if (isNew && f.maNhanVien.trim() && !/^[A-Za-z0-9_-]+$/.test(f.maNhanVien.trim())) e.maNhanVien = 'Mã chỉ gồm chữ, số, - và _';
    const luong = f.luong.trim() ? parseMoneyInput(f.luong) : null;
    if (f.luong.trim() && (luong === null || luong < 0)) e.luong = 'Lương là số không âm';
    if (f.ngaySinh && f.ngaySinh >= isoDate(new Date())) e.ngaySinh = 'Ngày sinh phải trước hôm nay';
    if (isNew && f.matKhau.length < 6) e.matKhau = 'Mật khẩu ít nhất 6 ký tự';
    if (!isNew && f.matKhau && f.matKhau.length < 6) e.matKhau = 'Mật khẩu ít nhất 6 ký tự';
    setErrors(e);
    setFormError(null);
    if (Object.keys(e).length) return;

    const body: NhanVienInput = {
      maNhanVien: isNew ? f.maNhanVien.trim() || null : undefined,
      ho: f.ho.trim() || null,
      tenLot: f.tenLot.trim() || null,
      ten: f.ten.trim(),
      phai: f.phai || null,
      ngaySinh: f.ngaySinh || null,
      soDienThoai: f.soDienThoai.trim() || null,
      tinh: f.tinh.trim() || null,
      diaChi: f.diaChi.trim() || null,
      luong,
      chucVu: f.chucVu,
      trangThai: f.trangThai,
      matKhau: f.matKhau || null,
    };
    setBusy(true);
    try {
      onSaved(isNew ? await api.themNhanVien(body) : await api.suaNhanVien(initial.maNhanVien, body), isNew);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setFormError(err.message);
      } else setFormError('Không lưu được nhân viên.');
      setBusy(false);
    }
  }

  return (
    <Modal
      title={isNew ? 'Thêm nhân viên' : `Sửa nhân viên ${initial.maNhanVien}`}
      onClose={onClose}
      wide
      footer={
        <>
          <button className="btn" type="button" onClick={onClose} disabled={busy}>
            Hủy
          </button>
          <button className="btn btn-primary" type="submit" form="nv-form" disabled={busy}>
            {busy ? 'Đang lưu…' : 'Lưu'}
          </button>
        </>
      }
    >
      <form id="nv-form" className="form-grid form-grid-3" onSubmit={submit} noValidate>
        <Field id="nv-ho" label="Họ">
          <input value={f.ho} onChange={set('ho')} autoFocus />
        </Field>
        <Field id="nv-tenlot" label="Tên lót">
          <input value={f.tenLot} onChange={set('tenLot')} />
        </Field>
        <Field id="nv-ten" label="Tên *" error={errors.ten}>
          <input value={f.ten} onChange={set('ten')} />
        </Field>
        <Field id="nv-chucvu" label="Chức vụ" hint={isSelf ? 'Không tự hạ chức vụ được' : undefined}>
          <select value={f.chucVu} onChange={set('chucVu')} disabled={isSelf}>
            <option value="NV">Nhân viên bán hàng</option>
            <option value="QL">Quản lý</option>
          </select>
        </Field>
        <Field id="nv-luong" label="Lương (đồng)" error={errors.luong}>
          <input value={f.luong} onChange={set('luong')} inputMode="numeric" placeholder="VD: 8.000.000" />
        </Field>
        {isNew ? (
          <Field id="nv-ma" label="Mã đăng nhập" error={errors.maNhanVien} hint="Bỏ trống để tự tạo (NV05…)">
            <input value={f.maNhanVien} onChange={set('maNhanVien')} />
          </Field>
        ) : (
          <Field id="nv-trangthai" label="Trạng thái" hint={isSelf ? 'Không tự khóa tài khoản được' : 'Đã nghỉ thì không đăng nhập được'}>
            <select value={f.trangThai} onChange={set('trangThai')} disabled={isSelf}>
              <option value="active">Đang làm</option>
              <option value="inactive">Đã nghỉ</option>
            </select>
          </Field>
        )}
        <Field id="nv-sdt" label="Số điện thoại" error={errors.soDienThoai}>
          <input value={f.soDienThoai} onChange={set('soDienThoai')} inputMode="tel" />
        </Field>
        <Field id="nv-phai" label="Giới tính">
          <select value={f.phai} onChange={set('phai')}>
            <option value="">Không ghi</option>
            <option value="nữ">Nữ</option>
            <option value="nam">Nam</option>
            <option value="khác">Khác</option>
          </select>
        </Field>
        <Field id="nv-ngaysinh" label="Ngày sinh" error={errors.ngaySinh}>
          <input type="date" value={f.ngaySinh} onChange={set('ngaySinh')} />
        </Field>
        <Field id="nv-tinh" label="Tỉnh, thành phố">
          <input value={f.tinh} onChange={set('tinh')} />
        </Field>
        <Field id="nv-diachi" label="Địa chỉ" className="span-2">
          <input value={f.diaChi} onChange={set('diaChi')} />
        </Field>
        <Field
          id="nv-matkhau"
          label={isNew ? 'Mật khẩu *' : 'Đặt lại mật khẩu'}
          error={errors.matKhau}
          hint={isNew ? 'Ít nhất 6 ký tự, lưu dạng mã hóa' : 'Bỏ trống để giữ mật khẩu cũ'}
          className="span-2"
        >
          <input type="password" value={f.matKhau} onChange={set('matKhau')} autoComplete="new-password" />
        </Field>
      </form>
      {formError && <div className="alert alert-error">{formError}</div>}
    </Modal>
  );
}
