import { formatDate, formatMoney, formatMoneyShort, normalize, parseMoneyInput } from './format';

describe('format', () => {
  it('định dạng tiền Việt Nam', () => {
    expect(formatMoney(1234567)).toBe('1.234.567 ₫');
    expect(formatMoney(null)).toBe('—');
    expect(formatMoneyShort(2_500_000)).toBe('2,5 tr ₫');
    expect(formatMoneyShort(1_200_000_000)).toBe('1,2 tỷ ₫');
  });

  it('đổi ngày ISO sang dd/MM/yyyy không lệch múi giờ', () => {
    expect(formatDate('2026-01-05')).toBe('05/01/2026');
    expect(formatDate(null)).toBe('—');
  });

  it('bỏ dấu tiếng Việt để tìm kiếm', () => {
    expect(normalize('Sữa Đậu Nành')).toBe('sua dau nanh');
    expect(normalize('Thịt bò').includes(normalize('thit bo'))).toBe(true);
  });

  it('đọc số tiền người dùng nhập', () => {
    expect(parseMoneyInput('8.000.000')).toBe(8000000);
    expect(parseMoneyInput('25000')).toBe(25000);
    expect(parseMoneyInput('')).toBeNull();
    expect(parseMoneyInput('abc')).toBeNull();
  });
});
