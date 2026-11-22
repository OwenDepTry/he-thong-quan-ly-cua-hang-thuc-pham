import { useMemo, useState } from 'react';
import { ArrowDown, ArrowUp } from 'lucide-react';

/** Sắp xếp bảng theo cột: bấm tiêu đề để đổi chiều. */
export function useSort<T, K extends keyof T>(rows: T[], initial: { key: K; asc: boolean }) {
  const [sort, setSort] = useState(initial);
  const sorted = useMemo(() => {
    const dir = sort.asc ? 1 : -1;
    return [...rows].sort((a, b) => {
      const x = a[sort.key];
      const y = b[sort.key];
      if (x === y) return 0;
      if (x === null || x === undefined) return 1;
      if (y === null || y === undefined) return -1;
      return (typeof x === 'number' && typeof y === 'number' ? x - y : String(x).localeCompare(String(y), 'vi')) * dir;
    });
  }, [rows, sort]);

  const header = (key: K, label: string, cls = '', firstDesc = false) => (
    <th
      className={`sortable ${cls}`}
      onClick={() => setSort((s) => (s.key === key ? { key, asc: !s.asc } : { key, asc: !firstDesc }))}
      aria-sort={sort.key === key ? (sort.asc ? 'ascending' : 'descending') : 'none'}
    >
      {label}
      {sort.key === key && <span className="sort">{sort.asc ? <ArrowUp size={14} /> : <ArrowDown size={14} />}</span>}
    </th>
  );
  return { sorted, header };
}
