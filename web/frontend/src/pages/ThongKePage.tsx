import { useState } from 'react';
import { Download, TrendingDown, TrendingUp } from 'lucide-react';
import { api } from '../lib/api';
import { downloadCsv } from '../lib/csv';
import { addDays, formatDate, formatMoney, formatMoneyShort, formatNumber, isoDate, percentChange } from '../lib/format';
import type { BaoCao } from '../lib/types';
import { categoryOf } from '../lib/category';
import { Empty, ErrorState, Loading, useLoad } from '../components/ui';
import { RevenueChart } from '../components/RevenueChart';
import { ProductImage } from '../components/ProductImage';

type Preset = '7' | '30' | 'thang' | 'thangtruoc' | '12thang' | 'tuychon';

function range(p: Preset, today: string): { tuNgay: string; denNgay: string } {
  const [y, m] = today.split('-').map(Number);
  switch (p) {
    case '7':
      return { tuNgay: addDays(today, -6), denNgay: today };
    case 'thang':
      return { tuNgay: isoDate(new Date(y, m - 1, 1)), denNgay: today };
    case 'thangtruoc':
      return { tuNgay: isoDate(new Date(y, m - 2, 1)), denNgay: isoDate(new Date(y, m - 1, 0)) };
    case '12thang':
      return { tuNgay: isoDate(new Date(y, m - 12, 1)), denNgay: today };
    default:
      return { tuNgay: addDays(today, -29), denNgay: today };
  }
}

const PRESETS: [Preset, string][] = [
  ['7', '7 ngày'],
  ['30', '30 ngày'],
  ['thang', 'Tháng này'],
  ['thangtruoc', 'Tháng trước'],
  ['12thang', '12 tháng'],
  ['tuychon', 'Tùy chọn'],
];

export default function ThongKePage() {
  const today = isoDate(new Date());
  const [preset, setPreset] = useState<Preset>('30');
  const [custom, setCustom] = useState(range('30', today));
  const r = preset === 'tuychon' ? custom : range(preset, today);
  const report = useLoad(() => api.baoCao({ ...r, top: 10 }), [r.tuNgay, r.denNgay]);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Thống kê</h1>
          <p>
            Từ {formatDate(r.tuNgay)} đến {formatDate(r.denNgay)}. Doanh thu là tiền khách thực trả sau giảm giá.
          </p>
        </div>
        <button className="btn" onClick={() => report.data && exportCsv(report.data)} disabled={!report.data}>
          <Download size={16} aria-hidden="true" /> Xuất Excel
        </button>
      </header>

      <div className="toolbar">
        <div className="filters" role="group" aria-label="Khoảng thời gian">
          {PRESETS.map(([v, label]) => (
            <button key={v} className="filter" aria-pressed={preset === v} onClick={() => setPreset(v)}>
              {label}
            </button>
          ))}
        </div>
        {preset === 'tuychon' && (
          <>
            <label className="inline">
              Từ
              <input type="date" value={custom.tuNgay} max={custom.denNgay} onChange={(e) => e.target.value && setCustom({ ...custom, tuNgay: e.target.value })} />
            </label>
            <label className="inline">
              Đến
              <input type="date" value={custom.denNgay} min={custom.tuNgay} max={today} onChange={(e) => e.target.value && setCustom({ ...custom, denNgay: e.target.value })} />
            </label>
          </>
        )}
      </div>

      {report.error ? (
        <ErrorState message={report.error} onRetry={report.reload} />
      ) : !report.data ? (
        <Loading />
      ) : (
        <Report data={report.data} loading={report.loading} />
      )}
    </div>
  );
}

function Delta({ now, before, label }: { now: number; before: number; label: string }) {
  const p = percentChange(now, before);
  if (p === null) return <small className="muted">Kỳ trước chưa có số liệu</small>;
  const up = p >= 0;
  // Tăng trên 100% thì nói "gấp N lần" cho dễ hình dung hơn "tăng 5.149%"
  const text =
    p >= 100
      ? `Gấp ${(now / before).toLocaleString('vi-VN', { maximumFractionDigits: 1 })} lần kỳ trước`
      : `${up ? 'Tăng' : 'Giảm'} ${Math.abs(p).toLocaleString('vi-VN', { maximumFractionDigits: 1 })}% so với kỳ trước`;
  return (
    <small className={up ? 'delta up' : 'delta down'} title={`${label} kỳ trước: ${formatMoney(before).replace(' ₫', '')}`}>
      {up ? <TrendingUp size={14} aria-hidden="true" /> : <TrendingDown size={14} aria-hidden="true" />}
      {text}
    </small>
  );
}

const THU = ['', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy', 'Chủ nhật'];
const THU_NGAN = ['', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];
const pct = (x: number) => `${x.toLocaleString('vi-VN', { maximumFractionDigits: 1 })}%`;

function Report({ data, loading }: { data: BaoCao; loading: boolean }) {
  const t = data.tongHop;
  const k = data.kyTruoc;
  const bienLoiNhuan = t.doanhThu > 0 ? (t.loiNhuanGop / t.doanhThu) * 100 : 0;
  const maxSp = Math.max(1, ...data.sanPhamBanChay.map((s) => s.doanhThu));
  const maxNv = Math.max(1, ...data.theoNhanVien.map((n) => n.doanhThu));
  const best = data.theoKy.reduce((a, b) => (b.doanhThu > a.doanhThu ? b : a), data.theoKy[0]);

  return (
    <div className={`report${loading ? ' is-loading' : ''}`}>
      <section className="panel report-figures" aria-label="Tổng hợp">
        <div className="figure">
          <span>Doanh thu</span>
          <strong>{formatMoney(t.doanhThu)}</strong>
          <Delta now={t.doanhThu} before={k.doanhThu} label="Doanh thu" />
        </div>
        <div className="figure">
          <span>Lợi nhuận gộp ước tính</span>
          <strong>{formatMoney(t.loiNhuanGop)}</strong>
          <small className="muted" title="Doanh thu trừ giá vốn, giá vốn tính theo giá nhập bình quân của từng sản phẩm">
            Biên lợi nhuận {pct(bienLoiNhuan)}
          </small>
        </div>
        <div className="figure">
          <span>Số hóa đơn</span>
          <strong>{formatNumber(t.soHoaDon)}</strong>
          <Delta now={t.soHoaDon} before={k.soHoaDon} label="Số hóa đơn" />
        </div>
        <div className="figure">
          <span>Trung bình mỗi hóa đơn</span>
          <strong>{formatMoney(t.giaTriTrungBinh)}</strong>
          <small className="muted">Đã giảm giá {formatMoney(t.tienGiam)}</small>
        </div>
      </section>

      <section className="panel report-chart" aria-labelledby="chart-title">
        <div className="panel-head">
          <h2 id="chart-title">Doanh thu theo {data.nhom === 'thang' ? 'tháng' : 'ngày'}</h2>
          <span className="muted small">
            {best && best.doanhThu > 0
              ? `Cao nhất ${data.nhom === 'thang' ? `tháng ${best.ky.slice(5, 7)}/${best.ky.slice(0, 4)}` : formatDate(best.ky)}: ${formatMoney(best.doanhThu)}. `
              : ''}
            Nhập hàng {formatMoney(t.tienNhap)} ({formatNumber(t.soPhieuNhap)} phiếu)
          </span>
        </div>
        <RevenueChart
          days={data.theoKy.map((d) => ({ ngay: d.ky, doanhThu: d.doanhThu, soHoaDon: d.soHoaDon }))}
          nhom={data.nhom}
          lastLabel=""
          height={190}
        />
      </section>

      <div className="report-cols even">
        <section className="panel" aria-labelledby="nhom-title">
          <div className="panel-head">
            <h2 id="nhom-title">Tỉ trọng theo nhóm hàng</h2>
            <span className="muted small">Theo doanh thu trước giảm giá</span>
          </div>
          {data.theoNhomHang.length === 0 ? <Empty>Chưa có số liệu.</Empty> : <CategoryDonut items={data.theoNhomHang} />}
        </section>
        <section className="panel" aria-labelledby="thu-title">
          <div className="panel-head">
            <h2 id="thu-title">Theo thứ trong tuần</h2>
            <span className="muted small">Doanh thu trung bình mỗi ngày</span>
          </div>
          <WeekdayBars items={data.theoThu} />
        </section>
      </div>

      <div className="report-cols">
        <section className="panel" aria-labelledby="top-title">
          <div className="panel-head">
            <h2 id="top-title">Sản phẩm bán chạy</h2>
            <span className="muted small">Theo doanh thu trước giảm giá</span>
          </div>
          {data.sanPhamBanChay.length === 0 ? (
            <Empty>Chưa bán được sản phẩm nào trong khoảng này.</Empty>
          ) : (
            <ol className="rank">
              {data.sanPhamBanChay.map((s, i) => (
                <li key={s.maSanPham} className={`cat cat-${categoryOf(s.loai, s.tenSanPham).key}`}>
                  <span className="rank-no">{i + 1}</span>
                  <ProductImage sp={s} className="thumb-sm" iconSize={16} />
                  <div className="rank-main">
                    <div className="rank-top">
                      <strong>{s.tenSanPham}</strong>
                      <span>{formatMoney(s.doanhThu)}</span>
                    </div>
                    <span className="rank-bar" aria-hidden="true">
                      <i style={{ width: `${(s.doanhThu / maxSp) * 100}%` }} />
                    </span>
                    <small className="muted">
                      {formatNumber(s.soLuong)} {s.donViTinh ?? ''}
                    </small>
                  </div>
                </li>
              ))}
            </ol>
          )}
        </section>

        <div className="report-stack">
          <section className="panel" aria-labelledby="kh-title">
            <div className="panel-head">
              <h2 id="kh-title">Khách hàng thân thiết</h2>
            </div>
            {data.khachHangThanThiet.length === 0 ? (
              <Empty>Chưa có khách thành viên mua hàng.</Empty>
            ) : (
              <ol className="rank compact">
                {data.khachHangThanThiet.map((c, i) => (
                  <li key={c.maKhachHang}>
                    <span className="rank-no">{i + 1}</span>
                    <div className="rank-main">
                      <div className="rank-top">
                        <strong>{c.hoTen || c.maKhachHang}</strong>
                        <span>{formatMoney(c.doanhThu)}</span>
                      </div>
                      <small className="muted">{formatNumber(c.soHoaDon)} hóa đơn</small>
                    </div>
                  </li>
                ))}
              </ol>
            )}
          </section>

          <section className="panel" aria-labelledby="nv-title">
            <div className="panel-head">
              <h2 id="nv-title">Theo nhân viên</h2>
            </div>
            {data.theoNhanVien.length === 0 ? (
              <Empty>Chưa có hóa đơn nào.</Empty>
            ) : (
              <ol className="rank">
                {data.theoNhanVien.map((n, i) => (
                  <li key={n.maNhanVien}>
                    <span className="rank-no">{i + 1}</span>
                    <div className="rank-main">
                      <div className="rank-top">
                        <strong>{n.hoTen || n.maNhanVien}</strong>
                        <span>{formatMoney(n.doanhThu)}</span>
                      </div>
                      <span className="rank-bar" aria-hidden="true">
                        <i style={{ width: `${(n.doanhThu / maxNv) * 100}%` }} />
                      </span>
                      <small className="muted">{formatNumber(n.soHoaDon)} hóa đơn</small>
                    </div>
                  </li>
                ))}
              </ol>
            )}
          </section>
        </div>
      </div>
    </div>
  );
}

/** Biểu đồ vành khuyên tỉ trọng doanh thu theo nhóm hàng, màu theo nhóm như ở màn bán hàng. */
function CategoryDonut({ items }: { items: BaoCao['theoNhomHang'] }) {
  const total = items.reduce((s, x) => s + x.doanhThu, 0) || 1;
  const R = 46;
  const C = 2 * Math.PI * R;
  let offset = 0;
  const segs = items.map((x) => {
    const len = (x.doanhThu / total) * C;
    const seg = { ...x, key: categoryOf(x.loai).key, dash: `${Math.max(len - 1.5, 0)} ${C}`, off: -offset };
    offset += len;
    return seg;
  });
  const top = items[0];
  return (
    <div className="donut-wrap">
      <svg className="donut" viewBox="0 0 120 120" role="img" aria-label="Tỉ trọng doanh thu theo nhóm hàng">
        <circle className="donut-track" cx="60" cy="60" r={R} />
        {segs.map((s) => (
          <circle key={s.loai} className={`seg cat cat-${s.key}`} cx="60" cy="60" r={R} strokeDasharray={s.dash} strokeDashoffset={s.off}>
            <title>
              {s.loai}: {formatMoney(s.doanhThu)} ({pct((s.doanhThu / total) * 100)})
            </title>
          </circle>
        ))}
        <text x="60" y="57" textAnchor="middle" className="donut-big">
          {pct((top.doanhThu / total) * 100)}
        </text>
        <text x="60" y="72" textAnchor="middle" className="donut-small">
          {top.loai.length > 16 ? `${top.loai.slice(0, 15)}…` : top.loai}
        </text>
      </svg>
      <ul className="legend">
        {segs.map((s) => (
          <li key={s.loai} className={`cat cat-${s.key}`}>
            <i aria-hidden="true" />
            <span>{s.loai}</span>
            <strong>{pct((s.doanhThu / total) * 100)}</strong>
            <small className="muted">{formatMoney(s.doanhThu)}</small>
          </li>
        ))}
      </ul>
    </div>
  );
}

function WeekdayBars({ items }: { items: BaoCao['theoThu'] }) {
  const avg = items.map((x) => (x.soNgay ? x.doanhThu / x.soNgay : 0));
  const max = Math.max(1, ...avg);
  const bestIdx = avg.indexOf(Math.max(...avg));
  return (
    <div className="weekbars-wrap">
      <div className="weekbars" role="img" aria-label="Doanh thu trung bình theo thứ trong tuần">
        {items.map((x, i) => (
          <div key={x.thu} className={`weekbar${i === bestIdx && avg[i] > 0 ? ' best' : ''}`} title={`${THU[x.thu]}: trung bình ${formatMoney(avg[i])} mỗi ngày, ${formatNumber(x.soHoaDon)} hóa đơn`}>
            <span className="weekbar-val">{avg[i] > 0 ? formatMoneyShort(avg[i]).replace(' ₫', '') : ''}</span>
            <span className="weekbar-bar">
              <i style={{ height: `${(avg[i] / max) * 100}%` }} />
            </span>
            <span className="weekbar-label">{THU_NGAN[x.thu]}</span>
          </div>
        ))}
      </div>
      {avg[bestIdx] > 0 && (
        <p className="muted small weekbars-note">
          Bán tốt nhất vào {THU[items[bestIdx].thu].toLowerCase()}, trung bình {formatMoney(avg[bestIdx])} mỗi ngày.
        </p>
      )}
    </div>
  );
}

function exportCsv(d: BaoCao) {
  const ky = (s: string) => (d.nhom === 'thang' ? `${s.slice(5, 7)}/${s.slice(0, 4)}` : formatDate(s));
  downloadCsv(`thong-ke-${d.tuNgay}-den-${d.denNgay}.csv`, [
    ['Thống kê bán hàng', `${formatDate(d.tuNgay)} - ${formatDate(d.denNgay)}`],
    [],
    ['Doanh thu', Math.round(d.tongHop.doanhThu)],
    ['Số hóa đơn', d.tongHop.soHoaDon],
    ['Giảm giá', Math.round(d.tongHop.tienGiam)],
    ['Tiền nhập hàng', Math.round(d.tongHop.tienNhap)],
    ['Giá vốn ước tính', Math.round(d.tongHop.giaVon)],
    ['Lợi nhuận gộp ước tính', Math.round(d.tongHop.loiNhuanGop)],
    [],
    [d.nhom === 'thang' ? 'Tháng' : 'Ngày', 'Doanh thu', 'Số hóa đơn'],
    ...d.theoKy.map((x) => [ky(x.ky), Math.round(x.doanhThu), x.soHoaDon]),
    [],
    ['Mã sản phẩm', 'Sản phẩm', 'Số lượng', 'Đơn vị', 'Doanh thu'],
    ...d.sanPhamBanChay.map((s) => [s.maSanPham, s.tenSanPham, s.soLuong, s.donViTinh, Math.round(s.doanhThu)]),
    [],
    ['Mã nhân viên', 'Nhân viên', 'Số hóa đơn', 'Doanh thu'],
    ...d.theoNhanVien.map((n) => [n.maNhanVien, n.hoTen, n.soHoaDon, Math.round(n.doanhThu)]),
    [],
    ['Nhóm hàng', 'Số lượng', 'Doanh thu'],
    ...d.theoNhomHang.map((x) => [x.loai, x.soLuong, Math.round(x.doanhThu)]),
    [],
    ['Thứ', 'Doanh thu', 'Số hóa đơn', 'Số ngày'],
    ...d.theoThu.map((x) => [THU[x.thu], Math.round(x.doanhThu), x.soHoaDon, x.soNgay]),
    [],
    ['Mã khách hàng', 'Khách hàng', 'Số hóa đơn', 'Doanh thu'],
    ...d.khachHangThanThiet.map((c) => [c.maKhachHang, c.hoTen, c.soHoaDon, Math.round(c.doanhThu)]),
  ]);
}
