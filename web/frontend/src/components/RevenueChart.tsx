import { useEffect, useRef, useState } from 'react';
import { formatDate, formatMoney, formatMoneyShort } from '../lib/format';

/** Đo bề rộng thật của khung để vẽ SVG đúng tỉ lệ 1:1 (chữ trục luôn cùng cỡ, không bị phóng/thu). */
function useWidth(fallback: number) {
  const ref = useRef<HTMLDivElement>(null);
  const [width, setWidth] = useState(fallback);
  useEffect(() => {
    const el = ref.current;
    if (!el || typeof ResizeObserver === 'undefined') return;
    const ro = new ResizeObserver(([e]) => setWidth(Math.max(240, Math.round(e.contentRect.width))));
    ro.observe(el);
    return () => ro.disconnect();
  }, []);
  return { ref, width };
}

export interface DayRevenue {
  ngay: string; // yyyy-MM-dd
  doanhThu: number;
  soHoaDon: number;
}

/**
 * Biểu đồ cột doanh thu (SVG thuần, không cần thư viện).
 * nhom = 'ngay' | 'thang'; lastLabel khác rỗng thì tô đậm cột cuối (VD "Hôm nay").
 */
export function RevenueChart({
  days,
  nhom = 'ngay',
  lastLabel = 'Hôm nay',
  height = 150,
}: {
  days: DayRevenue[];
  nhom?: 'ngay' | 'thang';
  lastLabel?: string;
  height?: number;
}) {
  const { ref, width: W } = useWidth(640);
  const H = height;
  const top = 8;
  const bottom = 24;
  const max = Math.max(1, ...days.map((d) => d.doanhThu));
  const step = W / days.length;
  const barW = Math.min(28, step * 0.62);
  const chartH = H - top - bottom;
  const last = days.length - 1;
  // Khoảng 7 nhãn trên trục, đếm lùi từ cột cuối để cột cuối luôn có nhãn
  const every = Math.max(1, Math.ceil(days.length / Math.max(3, Math.floor(W / 80))));
  const label = (d: DayRevenue, i: number) =>
    i === last && lastLabel ? lastLabel : nhom === 'thang' ? `T${Number(d.ngay.slice(5, 7))}/${d.ngay.slice(2, 4)}` : formatDate(d.ngay).slice(0, 5);
  const when = (d: DayRevenue) => (nhom === 'thang' ? `Tháng ${d.ngay.slice(5, 7)}/${d.ngay.slice(0, 4)}` : formatDate(d.ngay));

  return (
    <div className="chart" ref={ref}>
      <svg viewBox={`0 0 ${W} ${H}`} role="img" aria-label={`Biểu đồ doanh thu ${days.length} ${nhom === 'thang' ? 'tháng' : 'ngày'}`}>
        <line className="grid" x1="0" x2={W} y1={top} y2={top} />
        <line className="grid" x1="0" x2={W} y1={top + chartH / 2} y2={top + chartH / 2} />
        <text className="axis" x={W} y={top - 1} textAnchor="end" dy="-2">
          {formatMoneyShort(max)}
        </text>
        {days.map((d, i) => {
          const h = Math.max(d.doanhThu > 0 ? 3 : 0, (d.doanhThu / max) * chartH);
          const x = i * step + (step - barW) / 2;
          const showLabel = (last - i) % every === 0;
          return (
            <g key={d.ngay}>
              <title>
                {when(d)}: {formatMoney(d.doanhThu)} ({d.soHoaDon} hóa đơn)
              </title>
              <rect x={i * step} y={top} width={step} height={chartH} fill="transparent" />
              <rect className={`bar${i === last && lastLabel ? ' today' : ''}`} x={x} y={top + chartH - h} width={barW} height={h} rx="4" />
              {showLabel && (
                <text className="axis" x={x + barW / 2} y={H - 6} textAnchor="middle">
                  {label(d, i)}
                </text>
              )}
            </g>
          );
        })}
      </svg>
    </div>
  );
}
