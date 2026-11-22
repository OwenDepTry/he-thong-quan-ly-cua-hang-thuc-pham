import { useState, type FormEvent } from 'react';
import { api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatDate, formatMoney } from '../lib/format';
import type { HoaDonChiTiet } from '../lib/types';
import { Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';
import { HoaDonView } from './BanHangPage';

const PAGE_SIZE = 20;

export default function HoaDonPage() {
  const { isQuanLy } = useAuth();
  const toast = useToast();
  const [filter, setFilter] = useState({ q: '', tuNgay: '', denNgay: '' });
  const [applied, setApplied] = useState(filter);
  const [page, setPage] = useState(0);
  const [detail, setDetail] = useState<HoaDonChiTiet | null>(null);

  const list = useLoad(
    () => api.hoaDon({ ...applied, page, size: PAGE_SIZE }),
    [applied.q, applied.tuNgay, applied.denNgay, page],
  );

  function search(e: FormEvent) {
    e.preventDefault();
    setPage(0);
    setApplied(filter);
  }

  async function open(ma: string) {
    try {
      setDetail(await api.chiTietHoaDon(ma));
    } catch (e) {
      toast.error((e as Error).message);
    }
  }

  const totalPages = list.data ? Math.max(1, Math.ceil(list.data.total / PAGE_SIZE)) : 1;

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Hóa đơn</h1>
          <p className="muted">
            {isQuanLy ? 'Tất cả hóa đơn của cửa hàng' : 'Hóa đơn do bạn lập'}
            {list.data && ` · ${list.data.total} hóa đơn`}
          </p>
        </div>
      </header>

      <form className="toolbar" onSubmit={search}>
        <input
          className="search"
          placeholder="Mã hóa đơn hoặc mã khách hàng"
          value={filter.q}
          onChange={(e) => setFilter({ ...filter, q: e.target.value })}
          aria-label="Tìm hóa đơn"
        />
        <label className="inline">
          Từ
          <input type="date" value={filter.tuNgay} onChange={(e) => setFilter({ ...filter, tuNgay: e.target.value })} />
        </label>
        <label className="inline">
          Đến
          <input type="date" value={filter.denNgay} onChange={(e) => setFilter({ ...filter, denNgay: e.target.value })} />
        </label>
        <button className="btn" type="submit">
          Tìm
        </button>
      </form>

      {list.loading && !list.data && <Loading />}
      {list.error && <ErrorState message={list.error} onRetry={list.reload} />}
      {list.data && list.data.items.length === 0 && <Empty>Không có hóa đơn nào.</Empty>}
      {list.data && list.data.items.length > 0 && (
        <>
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Ngày</th>
                  <th>Khách hàng</th>
                  {isQuanLy && <th>Nhân viên</th>}
                  <th className="num">Tổng tiền</th>
                  <th className="num">Giảm</th>
                  <th className="num">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                {list.data.items.map((h) => (
                  <tr key={h.maHoaDon} className="clickable" onClick={() => open(h.maHoaDon)}>
                    <td className="mono">{h.maHoaDon}</td>
                    <td>{formatDate(h.thoiGian)}</td>
                    <td>{h.maKhachHang ? h.tenKhachHang || h.maKhachHang : <span className="muted">Khách lẻ</span>}</td>
                    {isQuanLy && <td>{h.tenNhanVien || h.maNhanVien}</td>}
                    <td className="num">{formatMoney(h.tongTien)}</td>
                    <td className="num">
                      {h.tienGiam > 0 ? (
                        <span title={h.maKhuyenMai ?? ''}>-{formatMoney(h.tienGiam)}</span>
                      ) : (
                        <span className="muted">—</span>
                      )}
                    </td>
                    <td className="num">
                      <strong>{formatMoney(h.thanhTien)}</strong>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="pager">
            <button className="btn btn-sm" disabled={page === 0 || list.loading} onClick={() => setPage((p) => p - 1)}>
              ← Trước
            </button>
            <span>
              Trang {page + 1} / {totalPages}
            </span>
            <button
              className="btn btn-sm"
              disabled={page + 1 >= totalPages || list.loading}
              onClick={() => setPage((p) => p + 1)}
            >
              Sau →
            </button>
          </div>
        </>
      )}

      {detail && (
        <Modal title={`Hóa đơn ${detail.hoaDon.maHoaDon}`} onClose={() => setDetail(null)} wide>
          <HoaDonView hd={detail} />
        </Modal>
      )}
    </div>
  );
}
