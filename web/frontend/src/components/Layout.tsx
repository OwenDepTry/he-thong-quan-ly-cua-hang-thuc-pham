import { BarChart3, LogOut, Moon, Package, ReceiptText, ShoppingBasket, Store, Sun } from 'lucide-react';
import { NavLink, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { useTheme } from '../lib/theme';
import { Loading } from './ui';

const MENU = [
  { to: '/', label: 'Tổng quan', icon: BarChart3, end: true },
  { to: '/ban-hang', label: 'Bán hàng', icon: ShoppingBasket },
  { to: '/hoa-don', label: 'Hóa đơn', icon: ReceiptText },
  { to: '/san-pham', label: 'Sản phẩm', icon: Package },
];

export function RequireAuth() {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <Loading text="Đang kiểm tra đăng nhập…" />;
  if (!user) return <Navigate to="/dang-nhap" replace state={{ from: location.pathname }} />;
  return <Outlet />;
}

export function initials(name: string | undefined): string {
  return (name || '?').trim().split(/\s+/).pop()?.[0]?.toUpperCase() ?? '?';
}

export function Layout() {
  const { user, isQuanLy, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const name = user?.hoTen || user?.maNhanVien;

  return (
    <div className="app">
      <header className="topbar">
        <NavLink to="/" className="brand" aria-label="Về trang tổng quan">
          <span className="brand-mark" aria-hidden="true">
            <Store size={18} />
          </span>
          <span>
            <strong>Cửa hàng thực phẩm</strong>
            <small>Quản lý bán hàng</small>
          </span>
        </NavLink>

        {/* Trên điện thoại, CSS chuyển menu này thành thanh tab ở đáy màn hình */}
        <nav className="mainnav" aria-label="Điều hướng chính">
          {MENU.map((m) => (
            <NavLink key={m.to} to={m.to} end={m.end} aria-label={m.label} title={m.label} className={({ isActive }) => (isActive ? "active" : "")}>
              <m.icon size={17} aria-hidden="true" />
              <span>{m.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="topbar-right">
          <button
            className="icon-btn"
            onClick={toggle}
            aria-label={theme === 'dark' ? 'Chuyển sang giao diện sáng' : 'Chuyển sang giao diện tối'}
            title={theme === 'dark' ? 'Giao diện sáng' : 'Giao diện tối'}
          >
            {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
          </button>
          <div className="userchip">
            <div className="avatar" aria-hidden="true">
              {initials(name)}
            </div>
            <div className="who">
              <strong title={user?.hoTen}>{name}</strong>
              <small>{isQuanLy ? 'Quản lý' : 'Nhân viên bán hàng'}</small>
            </div>
          </div>
          <button className="icon-btn" onClick={logout} aria-label="Đăng xuất" title="Đăng xuất">
            <LogOut size={18} />
          </button>
        </div>
      </header>

      <main className="content">
        <Outlet />
      </main>

    </div>
  );
}
