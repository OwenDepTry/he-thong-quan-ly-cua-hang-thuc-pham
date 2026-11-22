import { CalendarClock, CheckCircle2, PackageX, ShoppingBasket } from 'lucide-react';
import { Link } from 'react-router-dom';
import { api } from '../lib/api';
import { useAuth } from '../lib/auth';
import { addDays, formatDate, formatMoney, formatNumber } from '../lib/format';
import type { Dashboard } from '../lib/types';
import { ErrorState, Loading, useLoad } from '../components/ui';
import { RevenueChart, type DayRevenue } from '../components/RevenueChart';

const CHART_DAYS = 14;

export default function DashboardPage() {
  const { user, isQuanLy } = useAuth();
  const { data, error, loading, reload } = useLoad(() => api.dashboard(), []);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>{greeting()}, {user?.hoTen || user?.maNhanVien}</h1>
          <p>{data ? longDate(data.ngay) : ' '}</p>
        </div>
        <div className="actions">
          <Link className="btn btn-primary" to="/ban-hang">
            <ShoppingBasket size={17} aria-hidden="true" /> Bán hàng
          </Link>
        </div>
      </header>

      {loading && !data && <Loading text="Đang tải số liệu…" />}
      {error && <ErrorState message={error} onRetry={reload} />}

      {data && (
        <div className="overview">
          <SalesPanel data={data} isQuanLy={isQuanLy} />
          <TodoPanel data={data} />
        </div>
      )}
    </div>
  );
}

function SalesPanel({ data, isQuanLy }: { data: Dashboard; isQuanLy: boolean }) {
  return (
    <section className="panel sales" aria-label="Bán hàng">
      {isQuanLy ? (
        <>
          <span className="sales-label">Doanh thu hôm nay</span>
          <div className="sales-figure">
            {formatNumber(Math.round(data.doanhThuHomNay ?? 0))}
            <span className="unit">₫</span>
          </div>
          <span className="sales-sub">{data.hoaDonHomNay} hóa đơn</span>
          <SalesChart endDate={data.ngay} />
        </>
      ) : (
        <>
          <span className="sales-label">Hóa đơn hôm nay</span>
          <div className="sales-figure">{formatNumber(data.hoaDonHomNay)}</div>
          <span className="sales-sub">của cả cửa hàng</span>
        </>
      )}

      <dl className="figures">
        <div className="figure">
          <dt>Tháng này</dt>
          <dd>
            {isQuanLy ? formatMoney(data.doanhThuThangNay) : formatNumber(data.hoaDonThangNay)}
            <small>{isQuanLy ? `${formatNumber(data.hoaDonThangNay)} hóa đơn` : 'hóa đơn'}</small>
          </dd>
        </div>
        <div className="figure">
          <dt>Sản phẩm đang bán</dt>
          <dd>
            {formatNumber(data.tongSanPham)}
            <small className={data.soSapHetHang > 0 ? 'warn' : ''}>
              {data.soSapHetHang > 0 ? `${data.soSapHetHang} sắp hết hàng` : 'Tồn kho ổn định'}
            </small>
          </dd>
        </div>
        <div className="figure">
          <dt>Khách hàng thành viên</dt>
          <dd>
            {formatNumber(data.tongKhachHang)}
            <small>khách hàng</small>
          </dd>
        </div>
      </dl>
    </section>
  );
}

/** Doanh thu 14 ngày, lấy từ API báo cáo (đã cộng sẵn theo ngày ở database). */
function SalesChart({ endDate }: { endDate: string }) {
  const { data, error } = useLoad(() => loadDays(endDate), [endDate]);
  if (error) return null; // biểu đồ là phần phụ: lỗi thì ẩn, không làm hỏng trang
  if (!data) return <div className="chart skeleton" style={{ height: 150 }} aria-hidden="true" />;
  const total = data.reduce((s, d) => s + d.doanhThu, 0);
  const best = data.reduce((a, b) => (b.doanhThu > a.doanhThu ? b : a), data[0]);
  return (
    <>
      <RevenueChart days={data} />
      <div className="chart-note">
        <span>{CHART_DAYS} ngày gần nhất: {formatMoney(total)}</span>
        {best.doanhThu > 0 && <span>Cao nhất {formatDate(best.ngay)}</span>}
      </div>
    </>
  );
}

async function loadDays(endDate: string): Promise<DayRevenue[]> {
  const r = await api.baoCao({ tuNgay: addDays(endDate, -(CHART_DAYS - 1)), denNgay: endDate, nhom: 'ngay', top: 1 });
  return r.theoKy.map((d) => ({ ngay: d.ky, doanhThu: d.doanhThu, soHoaDon: d.soHoaDon }));
}

type TodoItem = { key: string; kind: 'stock' | 'expiry'; title: string; detail: string; badge: string; danger: boolean; order: number; href?: string };

function TodoPanel({ data }: { data: Dashboard }) {
  // Gộp hàng sắp hết và lô sắp hết hạn thành một danh sách việc cần làm, nặng nhất lên đầu
  const items: TodoItem[] = [
    ...data.sapHetHang.map((s) => ({
      key: `s-${s.maSanPham}`,
      kind: 'stock' as const,
      title: s.tenSanPham,
      detail: s.soLuongTon <= 0 ? 'Cần nhập thêm hàng' : `Tồn kho dưới ${data.nguongSapHetHang}`,
      badge: s.soLuongTon <= 0 ? 'Hết hàng' : `Còn ${s.soLuongTon} ${s.donViTinh ?? ''}`.trim(),
      danger: s.soLuongTon <= 0,
      order: s.soLuongTon <= 0 ? 0 : 2,
      href: `/nhap-hang?sp=${encodeURIComponent(s.maSanPham)}`,
    })),
    ...data.sapHetHan.map((h) => ({
      key: `h-${h.maHang}`,
      kind: 'expiry' as const,
      title: h.tenSanPham,
      detail: `Lô ${h.maHang}, ${h.soLuong} sản phẩm, hạn ${formatDate(h.hanSuDung)}`,
      badge: h.soNgayConLai < 0 ? `Quá hạn ${-h.soNgayConLai} ngày` : h.soNgayConLai === 0 ? 'Hết hạn hôm nay' : `Còn ${h.soNgayConLai} ngày`,
      danger: h.soNgayConLai <= 0,
      order: h.soNgayConLai <= 0 ? 1 : 3,
    })),
  ].sort((a, b) => a.order - b.order);

  const total = data.soSapHetHang + data.soLoSapHetHan;
  const hidden = total - items.length;

  return (
    <section className="panel todo" aria-labelledby="todo-title">
      <div className="todo-head">
        <h2 id="todo-title">Cần xử lý</h2>
        {total > 0 && <span className="total">{total} việc</span>}
      </div>
      {items.length === 0 ? (
        <p className="todo-empty">
          <CheckCircle2 size={20} aria-hidden="true" /> Không có hàng sắp hết hay lô sắp hết hạn.
        </p>
      ) : (
        <ul className="todo-list">
          {items.map((it) => (
            <li key={it.key}>
              <span className={`todo-icon ${it.kind}`} aria-hidden="true">
                {it.kind === 'stock' ? <PackageX size={17} /> : <CalendarClock size={17} />}
              </span>
              <div>
                {it.href ? (
                  <Link to={it.href} className="todo-link" title="Lập phiếu nhập cho sản phẩm này">
                    <strong>{it.title}</strong>
                  </Link>
                ) : (
                  <strong>{it.title}</strong>
                )}
                <small>{it.detail}</small>
              </div>
              <span className={`pill ${it.danger ? 'pill-danger' : 'pill-warn'}`}>{it.badge}</span>
            </li>
          ))}
        </ul>
      )}
      {
        <div className="todo-foot">
          <span>{hidden > 0 ? `Còn ${hidden} việc khác` : `Tồn kho dưới ${data.nguongSapHetHang} và hạn trong ${data.soNgayCanhBaoHetHan} ngày`}</span>
          <Link to="/nhap-hang" className="link">
            Nhập hàng
          </Link>
        </div>
      }
    </section>
  );
}

function greeting(): string {
  const h = new Date().getHours();
  return h < 11 ? 'Chào buổi sáng' : h < 14 ? 'Chào buổi trưa' : h < 18 ? 'Chào buổi chiều' : 'Chào buổi tối';
}

function longDate(isoDate: string): string {
  const d = new Date(`${isoDate}T00:00:00`);
  const thu = ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'][d.getDay()];
  return `${thu}, ${formatDate(isoDate)}`;
}
