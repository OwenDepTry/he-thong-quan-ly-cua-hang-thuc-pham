const money = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 });

/** 1234567 → "1.234.567 ₫" */
export function formatMoney(value: number | null | undefined): string {
  if (value === null || value === undefined || Number.isNaN(value)) return '—';
  return `${money.format(Math.round(value))} ₫`;
}

/** 1234567 → "1,2 tr" để hiển thị gọn trên thẻ số liệu */
export function formatMoneyShort(value: number | null | undefined): string {
  if (value === null || value === undefined) return '—';
  const abs = Math.abs(value);
  if (abs >= 1_000_000_000) return `${trim(value / 1_000_000_000)} tỷ ₫`;
  if (abs >= 1_000_000) return `${trim(value / 1_000_000)} tr ₫`;
  return formatMoney(value);
}

function trim(n: number): string {
  return n.toLocaleString('vi-VN', { maximumFractionDigits: 1 });
}

export function formatNumber(value: number | null | undefined): string {
  if (value === null || value === undefined) return '—';
  return money.format(value);
}

/** "2026-11-20" → "20/11/2026" (không đổi múi giờ) */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return '—';
  const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(iso);
  return m ? `${m[3]}/${m[2]}/${m[1]}` : iso;
}

/** Bỏ dấu tiếng Việt và viết thường – để tìm "sua" ra "Sữa". */
export function normalize(text: string | null | undefined): string {
  return (text ?? '')
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase()
    .trim();
}

/** Đọc số người dùng nhập kiểu Việt Nam: "8.000.000" hoặc "8000000" → 8000000. */
export function parseMoneyInput(text: string): number | null {
  const cleaned = text.replace(/[\s.₫đ]/g, '').replace(',', '.');
  if (cleaned === '') return null;
  const n = Number(cleaned);
  return Number.isFinite(n) ? n : null;
}

/** Ngày theo giờ máy, dạng "yyyy-MM-dd" (không dùng toISOString vì sẽ lệch sang UTC). */
export function isoDate(d: Date): string {
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}

export function addDays(iso: string, days: number): string {
  const [y, m, d] = iso.split('-').map(Number);
  return isoDate(new Date(y, m - 1, d + days));
}

/** Phần trăm thay đổi so với kỳ trước; null khi kỳ trước bằng 0. */
export function percentChange(now: number, before: number): number | null {
  if (!before) return null;
  return ((now - before) / before) * 100;
}
