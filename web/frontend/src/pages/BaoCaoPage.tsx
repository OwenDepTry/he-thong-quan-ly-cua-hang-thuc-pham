import { useState } from 'react';
import { Download, TrendingDown, TrendingUp } from 'lucide-react';
import { api } from '../lib/api';
import { downloadCsv } from '../lib/csv';
import { addDays, formatDate, formatMoney, formatNumber, isoDate, percentChange } from '../lib/format';
import type { BaoCao } from '../lib/types';
import { categoryOf } from '../lib/category';
import { Empty, ErrorState, Loading, useLoad } from '../components/ui';
import { RevenueChart } from '../components/RevenueChart';

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

export default function BaoCaoPage() {
  const today = isoDate(new Date());
  const [preset, setPreset] = useState<Preset>('30');
  const [custom, setCustom] = useState(range('30', today));
  const r = preset === 'tuychon' ? custom : range(preset, today);
  const report = useLoad(() => api.baoCao({ ...r, top: 10 }), [r.tuNgay, r.denNgay]);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <h1>Báo cáo</h1>
          <p>
            Doanh thu là số tiền khách thực trả sau giảm giá, từ {formatDate(r.tuNgay)} đến {formatDate(r.denNgay)}.
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

function Report({ data, loading }: { data: BaoCao; loading: boolean }) {
  const t = data.tongHop;
  const k = data.kyTruoc;
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
          <span>Số hóa đơn</span>
          <strong>{formatNumber(t.soHoaDon)}</strong>
          <Delta now={t.soHoaDon} before={k.soHoaDon} label="Số hóa đơn" />
        </div>
        <div className="figure">
          <span>Trung bình mỗi hóa đơn</span>
          <strong>{formatMoney(t.giaTriTrungBinh)}</strong>
          <small className="muted">Đã giảm giá {formatMoney(t.tienGiam)}</small>
        </div>
        <div className="figure">
          <span>Tiền nhập hàng</span>
          <strong>{formatMoney(t.tienNhap)}</strong>
          <small className="muted">{formatNumber(t.soPhieuNhap)} phiếu nhập</small>
        </div>
      </section>

      <section className="panel report-chart" aria-labelledby="chart-title">
        <div className="panel-head">
          <h2 id="chart-title">Doanh thu theo {data.nhom === 'thang' ? 'tháng' : 'ngày'}</h2>
          {best && best.doanhThu > 0 && (
            <span className="muted small">
              Cao nhất {data.nhom === 'thang' ? `tháng ${best.ky.slice(5, 7)}/${best.ky.slice(0, 4)}` : formatDate(best.ky)}: {formatMoney(best.doanhThu)}
            </span>
          )}
        </div>
        <RevenueChart
          days={data.theoKy.map((d) => ({ ngay: d.ky, doanhThu: d.doanhThu, soHoaDon: d.soHoaDon }))}
          nhom={data.nhom}
          lastLabel=""
          height={190}
        />
      </section>

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
  );
}

function exportCsv(d: BaoCao) {
  const ky = (s: string) => (d.nhom === 'thang' ? `${s.slice(5, 7)}/${s.slice(0, 4)}` : formatDate(s));
  downloadCsv(`bao-cao-${d.tuNgay}-den-${d.denNgay}.csv`, [
    ['Báo cáo doanh thu', `${formatDate(d.tuNgay)} - ${formatDate(d.denNgay)}`],
    [],
    ['Doanh thu', Math.round(d.tongHop.doanhThu)],
    ['Số hóa đơn', d.tongHop.soHoaDon],
    ['Giảm giá', Math.round(d.tongHop.tienGiam)],
    ['Tiền nhập hàng', Math.round(d.tongHop.tienNhap)],
    [],
    [d.nhom === 'thang' ? 'Tháng' : 'Ngày', 'Doanh thu', 'Số hóa đơn'],
    ...d.theoKy.map((x) => [ky(x.ky), Math.round(x.doanhThu), x.soHoaDon]),
    [],
    ['Mã sản phẩm', 'Sản phẩm', 'Số lượng', 'Đơn vị', 'Doanh thu'],
    ...d.sanPhamBanChay.map((s) => [s.maSanPham, s.tenSanPham, s.soLuong, s.donViTinh, Math.round(s.doanhThu)]),
    [],
    ['Mã nhân viên', 'Nhân viên', 'Số hóa đơn', 'Doanh thu'],
    ...d.theoNhanVien.map((n) => [n.maNhanVien, n.hoTen, n.soHoaDon, Math.round(n.doanhThu)]),
  ]);
}
