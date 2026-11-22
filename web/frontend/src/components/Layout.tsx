import { NavLink, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { Loading } from './ui';

const MENU = [
  { to: '/', label: 'Tổng quan', icon: '◎', end: true },
  { to: '/ban-hang', label: 'Lập hóa đơn', icon: '＋' },
  { to: '/hoa-don', label: 'Hóa đơn', icon: '≡' },
  { to: '/san-pham', label: 'Sản phẩm', icon: '▦' },
];

export function RequireAuth() {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <Loading text="Đang kiểm tra đăng nhập…" />;
  if (!user) return <Navigate to="/dang-nhap" replace state={{ from: location.pathname }} />;
  return <Outlet />;
}

export function Layout() {
  const { user, isQuanLy, logout } = useAuth();
  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">
            🛒
          </span>
          <div>
            <strong>Cửa hàng thực phẩm</strong>
            <small>Hệ thống quản lý</small>
          </div>
        </div>
        <nav>
          {MENU.map((m) => (
            <NavLink key={m.to} to={m.to} end={m.end} className={({ isActive }) => (isActive ? 'active' : '')}>
              <span className="nav-icon" aria-hidden="true">
                {m.icon}
              </span>
              {m.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-user">
          <div className="avatar" aria-hidden="true">
            {(user?.hoTen || user?.maNhanVien || '?').trim().split(/\s+/).pop()?.[0]?.toUpperCase()}
          </div>
          <div className="who">
            <strong title={user?.hoTen}>{user?.hoTen || user?.maNhanVien}</strong>
            <small>{isQuanLy ? 'Quản lý' : 'Nhân viên bán hàng'}</small>
          </div>
          <button className="btn btn-ghost btn-sm" onClick={logout}>
            Đăng xuất
          </button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
