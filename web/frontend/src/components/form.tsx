import { cloneElement, type ReactElement, type ReactNode } from 'react';
import { ChevronLeft, ChevronRight, Printer } from 'lucide-react';
import { formatNumber } from '../lib/format';

/** Ô nhập có nhãn, gợi ý và lỗi; tự gắn id, aria-invalid, aria-describedby cho ô bên trong. */
export function Field({
  id,
  label,
  error,
  hint,
  className,
  children,
}: {
  id: string;
  label: ReactNode;
  error?: string;
  hint?: ReactNode;
  className?: string;
  children: ReactElement<Record<string, unknown>>;
}) {
  const msg = error ?? hint;
  return (
    <div className={`field${error ? ' has-error' : ''}${className ? ` ${className}` : ''}`}>
      <label htmlFor={id}>{label}</label>
      {cloneElement(children, {
        id,
        'aria-invalid': error ? true : undefined,
        'aria-describedby': msg ? `${id}-msg` : undefined,
      })}
      {msg && (
        <em id={`${id}-msg`} className={error ? 'field-error' : 'field-hint'}>
          {msg}
        </em>
      )}
    </div>
  );
}

export function Pager({
  page,
  total,
  size,
  busy,
  onPage,
}: {
  page: number;
  total: number;
  size: number;
  busy?: boolean;
  onPage: (p: number) => void;
}) {
  const totalPages = Math.max(1, Math.ceil(total / size));
  if (totalPages <= 1) return null;
  return (
    <div className="pager">
      <span>
        Trang {page + 1} trên {formatNumber(totalPages)}
      </span>
      <span className="btns">
        <button className="btn btn-sm" disabled={page === 0 || busy} onClick={() => onPage(page - 1)}>
          <ChevronLeft size={16} aria-hidden="true" /> Trang trước
        </button>
        <button className="btn btn-sm" disabled={page + 1 >= totalPages || busy} onClick={() => onPage(page + 1)}>
          Trang sau <ChevronRight size={16} aria-hidden="true" />
        </button>
      </span>
    </div>
  );
}

/** In phần tử có class "print-area" (hóa đơn, phiếu nhập). CSS @media print ẩn mọi thứ khác. */
export function PrintButton({ label = 'In' }: { label?: string }) {
  return (
    <button className="btn" type="button" onClick={() => window.print()}>
      <Printer size={16} aria-hidden="true" /> {label}
    </button>
  );
}
