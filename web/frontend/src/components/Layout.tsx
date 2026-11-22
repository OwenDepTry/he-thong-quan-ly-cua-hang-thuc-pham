import { useEffect, useState } from 'react';
import {
  BarChart3,
  LayoutDashboard,
  LogOut,
  Menu,
  Moon,
  Package,
  PackagePlus,
  ReceiptText,
  ShoppingBasket,
  Store,
  Sun,
  UserCog,
  Users,
  X,
  type LucideIcon,
} from 'lucide-react';
import { NavLink, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { useTheme } from '../lib/theme';
import { Loading } from './ui';

interface MenuItem {
  to: string;
  label: string;
  icon: LucideIcon;
  end?: boolean;
  quanLy?: boolean;
  /** Hiện trên thanh tab dưới đáy (điện thoại) */
  tab?: boolean;
}

const MENU: { group: string; items: MenuItem[] }[] = [
  {
    group: 'Bán hàng',
    items: [
      { to: '/', label: 'Tổng quan', icon: LayoutDashboard, end: true, tab: true },
      { to: '/ban-hang', label: 'Bán hàng', icon: ShoppingBasket, tab: true },
      { to: '/hoa-don', label: 'Hóa đơn', icon: ReceiptText, tab: true },
    ],
  },
  {
    group: 'Kho hàng',
    items: [
      { to: '/nhap-hang', label: 'Nhập hàng', icon: PackagePlus },
      { to: '/san-pham', label: 'Sản phẩm', icon: Package },
    ],
  },
  {
    group: 'Cửa hàng',
    items: [
      { to: '/khach-hang', label: 'Khách hàng', icon: Users },
      { to: '/bao-cao', label: 'Báo cáo', icon: BarChart3, quanLy: true },
      { to: '/nhan-vien', label: 'Nhân viên', icon: UserCog, quanLy: true },
    ],
  },
];

export function RequireAuth() {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <Loading text="Đang kiểm tra đăng nhập…" />;
  if (!user) return <Navigate to="/dang-nhap" replace state={{ from: location.pathname }} />;
  return <Outlet />;
}

/** Trang chỉ dành cho quản lý; nhân viên gõ thẳng URL sẽ về Tổng quan. */
export function RequireQuanLy() {
  const { isQuanLy } = useAuth();
  return isQuanLy ? <Outlet /> : <Navigate to="/" replace />;
}

export function initials(name: string | undefined): string {
  return (name || '?').trim().split(/\s+/).pop()?.[0]?.toUpperCase() ?? '?';
}

export function Layout() {
  const { user, isQuanLy, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const location = useLocation();
  const [moreOpen, setMoreOpen] = useState(false);
  const name = user?.hoTen || user?.maNhanVien;
  const groups = MENU.map((g) => ({ ...g, items: g.items.filter((m) => !m.quanLy || isQuanLy) }));
  const all = groups.flatMap((g) => g.items);
  const extra = all.filter((m) => !m.tab);
  const extraActive = extra.some((m) => location.pathname.startsWith(m.to));

  // Đóng bảng "Thêm" khi chuyển trang
  useEffect(() => setMoreOpen(false), [location.pathname]);

  const link = (m: MenuItem) => (
    <NavLink key={m.to} to={m.to} end={m.end} aria-label={m.label} title={m.label} className={({ isActive }) => (isActive ? 'active' : '')}>
      <m.icon size={18} aria-hidden="true" />
      <span>{m.label}</span>
    </NavLink>
  );
  const themeButton = (
    <button
      className="icon-btn"
      onClick={toggle}
      aria-label={theme === 'dark' ? 'Chuyển sang giao diện sáng' : 'Chuyển sang giao diện tối'}
      title={theme === 'dark' ? 'Giao diện sáng' : 'Giao diện tối'}
    >
      {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
    </button>
  );
  const logoutButton = (
    <button className="icon-btn" onClick={logout} aria-label="Đăng xuất" title="Đăng xuất">
      <LogOut size={18} />
    </button>
  );
  const brand = (
    <NavLink to="/" className="brand" aria-label="Về trang tổng quan">
      <span className="brand-mark" aria-hidden="true">
        <Store size={18} />
      </span>
      <span className="brand-text">
        <strong>Cửa hàng thực phẩm</strong>
        <small>Quản lý bán hàng</small>
      </span>
    </NavLink>
  );

  return (
    <div className="app">
      {/* Máy tính: sidebar trái. Điện thoại: thanh trên + tab dưới đáy (CSS đổi bố cục) */}
      <aside className="sidebar">
        {brand}
        <nav className="sidenav" aria-label="Điều hướng chính">
          {groups.map(
            (g) =>
              g.items.length > 0 && (
                <div key={g.group} className="navgroup">
                  <span className="navgroup-title">{g.group}</span>
                  {g.items.map(link)}
                </div>
              ),
          )}
        </nav>
        <div className="sidebar-foot">
          <div className="userchip">
            <div className="avatar" aria-hidden="true">
              {initials(name)}
            </div>
            <div className="who">
              <strong title={user?.hoTen}>{name}</strong>
              <small>{isQuanLy ? 'Quản lý' : 'Nhân viên bán hàng'}</small>
            </div>
          </div>
          <div className="sidebar-actions">
            {themeButton}
            {logoutButton}
          </div>
        </div>
      </aside>

      <header className="mobilebar">
        {brand}
        <div className="mobilebar-right">
          {themeButton}
          <div className="avatar" aria-label={name} title={name}>
            {initials(name)}
          </div>
          {logoutButton}
        </div>
      </header>

      <main className="content">
        <Outlet />
      </main>

      <nav className="tabbar" aria-label="Điều hướng nhanh">
        {all.filter((m) => m.tab).map(link)}
        <button className={extraActive ? 'active' : ''} onClick={() => setMoreOpen((o) => !o)} aria-expanded={moreOpen} aria-label="Thêm mục">
          {moreOpen ? <X size={18} aria-hidden="true" /> : <Menu size={18} aria-hidden="true" />}
          <span>Thêm</span>
        </button>
      </nav>
      {moreOpen && (
        <div className="sheet-backdrop" onClick={() => setMoreOpen(false)}>
          <div className="sheet" onClick={(e) => e.stopPropagation()}>
            {extra.map(link)}
          </div>
        </div>
      )}
    </div>
  );
}
