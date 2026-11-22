import { formatDate, formatMoney, formatMoneyShort } from '../lib/format';

export interface DayRevenue {
  ngay: string; // yyyy-MM-dd
  doanhThu: number;
  soHoaDon: number;
}

/** Biểu đồ cột doanh thu theo ngày (SVG thuần, không cần thư viện). Cột hôm nay tô đậm. */
export function RevenueChart({ days }: { days: DayRevenue[] }) {
  const W = 640;
  const H = 150;
  const top = 8;
  const bottom = 24;
  const max = Math.max(1, ...days.map((d) => d.doanhThu));
  const step = W / days.length;
  const barW = Math.min(28, step * 0.62);
  const chartH = H - top - bottom;
  const last = days.length - 1;

  return (
    <div className="chart">
      <svg viewBox={`0 0 ${W} ${H}`} role="img" aria-label={`Doanh thu ${days.length} ngày gần nhất`}>
        <line className="grid" x1="0" x2={W} y1={top} y2={top} />
        <line className="grid" x1="0" x2={W} y1={top + chartH / 2} y2={top + chartH / 2} />
        <text className="axis" x={W} y={top - 1} textAnchor="end" dy="-2">
          {formatMoneyShort(max)}
        </text>
        {days.map((d, i) => {
          const h = Math.max(d.doanhThu > 0 ? 3 : 0, (d.doanhThu / max) * chartH);
          const x = i * step + (step - barW) / 2;
          const showLabel = i === last || (last - i) % 3 === 0;
          return (
            <g key={d.ngay}>
              <title>
                {formatDate(d.ngay)}: {formatMoney(d.doanhThu)} ({d.soHoaDon} hóa đơn)
              </title>
              <rect x={i * step} y={top} width={step} height={chartH} fill="transparent" />
              <rect className={`bar${i === last ? ' today' : ''}`} x={x} y={top + chartH - h} width={barW} height={h} rx="4" />
              {showLabel && (
                <text className="axis" x={x + barW / 2} y={H - 6} textAnchor="middle">
                  {i === last ? 'Hôm nay' : formatDate(d.ngay).slice(0, 5)}
                </text>
              )}
            </g>
          );
        })}
      </svg>
    </div>
  );
}
