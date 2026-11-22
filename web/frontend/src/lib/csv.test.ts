import { describe, expect, it } from 'vitest';
import { toCsv } from './csv';

describe('toCsv', () => {
  it('đặt trong ngoặc kép khi có dấu phẩy, ngoặc kép hoặc xuống dòng', () => {
    expect(toCsv([['Mã', 'Tên'], ['SP1', 'Cà chua, loại 1'], ['SP2', 'Nói "ngon"'], ['SP3', null]])).toBe(
      'Mã,Tên\r\nSP1,"Cà chua, loại 1"\r\nSP2,"Nói ""ngon"""\r\nSP3,',
    );
  });
});
