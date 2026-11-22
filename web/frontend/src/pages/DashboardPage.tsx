import { Link } from 'react-router-dom';
import { api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { formatDate, formatMoney, formatMoneyShort, formatNumber } from '../lib/format';
import { ErrorState, Loading, useLoad } from '../components/ui';

export default function DashboardPage() {
  const { user, isQuanLy } = useAuth();
  const { data, error, loading, reload } = useLoad(() => api.dashboard(), []);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Xin chào, {user?.hoTen || user?.maNhanVien}</h1>
          <p className="muted">
            {isQuanLy ? 'Tình hình cửa hàng hôm nay' : 'Chúc bạn một ca làm việc thuận lợi'}
            {data && ` · ${formatDate(data.ngay)}`}
          </p>
        </div>
        <div className="actions">
          <button className="btn" onClick={reload} disabled={loading}>
            Làm mới
          </button>
          <Link className="btn btn-primary" to="/ban-hang">
            + Lập hóa đơn
          </Link>
        </div>
      </header>

      {loading && !data && <Loading text="Đang tải số liệu…" />}
      {error && <ErrorState message={error} onRetry={reload} />}

      {data && (
        <>
          <section className="stats">
            {isQuanLy ? (
              <>
                <Stat
                  label="Doanh thu hôm nay"
                  value={formatMoneyShort(data.doanhThuHomNay)}
                  title={formatMoney(data.doanhThuHomNay)}
                  sub={`${data.hoaDonHomNay} hóa đơn`}
                />
                <Stat
                  label="Doanh thu tháng này"
                  value={formatMoneyShort(data.doanhThuThangNay)}
                  title={formatMoney(data.doanhThuThangNay)}
                  sub={`${formatNumber(data.hoaDonThangNay)} hóa đơn`}
                />
              </>
            ) : (
              <>
                <Stat label="Hóa đơn hôm nay" value={formatNumber(data.hoaDonHomNay)} sub="toàn cửa hàng" />
                <Stat label="Hóa đơn tháng này" value={formatNumber(data.hoaDonThangNay)} sub="toàn cửa hàng" />
              </>
            )}
            <Stat
              label="Sản phẩm"
              value={formatNumber(data.tongSanPham)}
              sub={data.soSapHetHang > 0 ? `${data.soSapHetHang} sắp hết hàng` : 'Tồn kho ổn định'}
              warn={data.soSapHetHang > 0}
            />
            <Stat label="Khách hàng" value={formatNumber(data.tongKhachHang)} sub="khách hàng thành viên" />
          </section>

          <section className="grid-2">
            <div className="card">
              <div className="card-head">
                <h2>Sắp hết hàng</h2>
                <span className="badge badge-warn">{data.soSapHetHang}</span>
              </div>
              <p className="muted small">Tồn kho từ {data.nguongSapHetHang} trở xuống</p>
              {data.sapHetHang.length === 0 ? (
                <p className="muted">Không có sản phẩm nào sắp hết hàng.</p>
              ) : (
                <ul className="list">
                  {data.sapHetHang.map((s) => (
                    <li key={s.maSanPham}>
                      <div>
                        <strong>{s.tenSanPham}</strong>
                        <small className="muted">{s.maSanPham}</small>
                      </div>
                      <span className={`pill ${s.soLuongTon <= 0 ? 'pill-danger' : 'pill-warn'}`}>
                        {s.soLuongTon <= 0 ? 'Hết hàng' : `Còn ${s.soLuongTon} ${s.donViTinh ?? ''}`}
                      </span>
                    </li>
                  ))}
                </ul>
              )}
              {data.soSapHetHang > data.sapHetHang.length && (
                <p className="muted small">và {data.soSapHetHang - data.sapHetHang.length} sản phẩm khác…</p>
              )}
              {isQuanLy && (
                <Link to="/san-pham" className="link small">
                  Xem sản phẩm →
                </Link>
              )}
            </div>

            <div className="card">
              <div className="card-head">
                <h2>Lô hàng sắp hết hạn</h2>
                <span className="badge badge-danger">{data.soLoSapHetHan}</span>
              </div>
              <p className="muted small">Hết hạn trong {data.soNgayCanhBaoHetHan} ngày tới hoặc vừa hết hạn</p>
              {data.sapHetHan.length === 0 ? (
                <p className="muted">Không có lô hàng nào sắp hết hạn.</p>
              ) : (
                <ul className="list">
                  {data.sapHetHan.map((h) => (
                    <li key={h.maHang}>
                      <div>
                        <strong>{h.tenSanPham}</strong>
                        <small className="muted">
                          Lô {h.maHang} · {h.soLuong} · HSD {formatDate(h.hanSuDung)}
                        </small>
                      </div>
                      <span className={`pill ${h.soNgayConLai <= 0 ? 'pill-danger' : 'pill-warn'}`}>
                        {h.soNgayConLai < 0
                          ? `Quá hạn ${-h.soNgayConLai} ngày`
                          : h.soNgayConLai === 0
                            ? 'Hết hạn hôm nay'
                            : `Còn ${h.soNgayConLai} ngày`}
                      </span>
                    </li>
                  ))}
                </ul>
              )}
              {data.soLoSapHetHan > data.sapHetHan.length && (
                <p className="muted small">và {data.soLoSapHetHan - data.sapHetHan.length} lô khác…</p>
              )}
            </div>
          </section>
        </>
      )}
    </div>
  );
}

function Stat({ label, value, sub, warn, title }: { label: string; value: string; sub: string; warn?: boolean; title?: string }) {
  return (
    <div className="stat" title={title}>
      <span className="stat-label">{label}</span>
      <strong className="stat-value">{value}</strong>
      <span className={`stat-sub${warn ? ' warn' : ''}`}>{sub}</span>
    </div>
  );
}
