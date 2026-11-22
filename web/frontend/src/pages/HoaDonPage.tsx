import { useState, type FormEvent } from 'react';
import { ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatDate, formatMoney, formatNumber } from '../lib/format';
import type { HoaDonChiTiet } from '../lib/types';
import { Empty, ErrorState, Loading, Modal, useLoad, useToast } from '../components/ui';
import { HoaDonView } from './BanHangPage';
import { PrintButton } from '../components/form';

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
          <p>
            {isQuanLy ? 'Tất cả hóa đơn của cửa hàng' : 'Hóa đơn do bạn lập'}
            {list.data ? `, ${formatNumber(list.data.total)} hóa đơn` : ''}. Bấm vào một dòng để xem chi tiết.
          </p>
        </div>
      </header>

      <form className="toolbar" onSubmit={search}>
        <div className="search-box">
          <Search size={17} aria-hidden="true" />
          <input
            placeholder="Mã hóa đơn hoặc mã khách hàng"
            value={filter.q}
            onChange={(e) => setFilter({ ...filter, q: e.target.value })}
            aria-label="Tìm hóa đơn"
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

      {list.loading && !list.data && <Loading />}
      {list.error && <ErrorState message={list.error} onRetry={list.reload} />}
      {list.data && list.data.items.length === 0 && <Empty>Không có hóa đơn nào.</Empty>}
      {list.data && list.data.items.length > 0 && (
        <>
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Hóa đơn</th>
                  <th>Khách hàng</th>
                  {isQuanLy && <th className="hide-sm">Thu ngân</th>}
                  <th className="num hide-sm">Tiền hàng</th>
                  <th className="num hide-sm">Giảm giá</th>
                  <th className="num">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                {list.data.items.map((h) => (
                  <tr key={h.maHoaDon} className="clickable" onClick={() => open(h.maHoaDon)}>
                    <td>
                      <strong>{h.maHoaDon}</strong>
                      <small className="muted block">{formatDate(h.thoiGian)}</small>
                    </td>
                    <td>{h.maKhachHang ? h.tenKhachHang || h.maKhachHang : <span className="muted">Khách lẻ</span>}</td>
                    {isQuanLy && <td className="hide-sm">{h.tenNhanVien || h.maNhanVien}</td>}
                    <td className="num hide-sm">{formatMoney(h.tongTien)}</td>
                    <td className="num hide-sm">
                      {h.tienGiam > 0 ? (
                        <span className="pill pill-ok" title={h.maKhuyenMai ?? ''}>
                          −{formatMoney(h.tienGiam)}
                        </span>
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
            <span>
              Trang {page + 1} trên {formatNumber(totalPages)}
            </span>
            <span className="btns">
              <button className="btn btn-sm" disabled={page === 0 || list.loading} onClick={() => setPage((p) => p - 1)}>
                <ChevronLeft size={16} aria-hidden="true" /> Trang trước
              </button>
              <button className="btn btn-sm" disabled={page + 1 >= totalPages || list.loading} onClick={() => setPage((p) => p + 1)}>
                Trang sau <ChevronRight size={16} aria-hidden="true" />
              </button>
            </span>
          </div>
        </>
      )}

      {detail && (
        <Modal
          title="Chi tiết hóa đơn"
          onClose={() => setDetail(null)}
          className="receipt-modal"
          footer={
            <>
              <button className="btn" onClick={() => setDetail(null)}>
                Đóng
              </button>
              <PrintButton label="In hóa đơn" />
            </>
          }
        >
          <HoaDonView hd={detail} />
        </Modal>
      )}
    </div>
  );
}
