import { useCallback, useEffect, useState } from 'react';

/** Chế độ sáng/tối: mặc định theo hệ điều hành, người dùng bấm đổi thì nhớ lựa chọn. */
export type Theme = 'light' | 'dark';
const KEY = 'qlch.theme';

function stored(): Theme | null {
  try {
    const v = localStorage.getItem(KEY);
    return v === 'light' || v === 'dark' ? v : null;
  } catch {
    return null;
  }
}

function system(): Theme {
  return typeof window !== 'undefined' && window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function apply(theme: Theme) {
  document.documentElement.dataset.theme = theme;
  document.documentElement.style.colorScheme = theme;
}

/** Gọi trước khi render để không bị nháy sáng khi mở trang ở chế độ tối. */
export function applyStoredTheme() {
  apply(stored() ?? system());
}

export function useTheme() {
  const [theme, setTheme] = useState<Theme>(() => stored() ?? system());

  useEffect(() => apply(theme), [theme]);

  // Chưa chọn lần nào thì đi theo hệ điều hành khi hệ điều hành đổi
  useEffect(() => {
    const mq = window.matchMedia?.('(prefers-color-scheme: dark)');
    if (!mq) return;
    const onChange = () => !stored() && setTheme(mq.matches ? 'dark' : 'light');
    mq.addEventListener?.('change', onChange);
    return () => mq.removeEventListener?.('change', onChange);
  }, []);

  const toggle = useCallback(() => {
    setTheme((t) => {
      const next: Theme = t === 'dark' ? 'light' : 'dark';
      try {
        localStorage.setItem(KEY, next);
      } catch {
        /* bỏ qua */
      }
      return next;
    });
  }, []);

  return { theme, toggle };
}
