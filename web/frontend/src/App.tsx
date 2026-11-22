import { Link, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './lib/auth';
import { ToastProvider } from './components/ui';
import { Layout, RequireAuth, RequireQuanLy } from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import SanPhamPage from './pages/SanPhamPage';
import BanHangPage from './pages/BanHangPage';
import HoaDonPage from './pages/HoaDonPage';
import NhapHangPage from './pages/NhapHangPage';
import KhachHangPage from './pages/KhachHangPage';
import BaoCaoPage from './pages/BaoCaoPage';
import NhanVienPage from './pages/NhanVienPage';

export function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <Routes>
          <Route path="/dang-nhap" element={<LoginPage />} />
          <Route element={<RequireAuth />}>
            <Route element={<Layout />}>
              <Route index element={<DashboardPage />} />
              <Route path="ban-hang" element={<BanHangPage />} />
              <Route path="hoa-don" element={<HoaDonPage />} />
              <Route path="san-pham" element={<SanPhamPage />} />
              <Route path="nhap-hang" element={<NhapHangPage />} />
              <Route path="khach-hang" element={<KhachHangPage />} />
              <Route element={<RequireQuanLy />}>
                <Route path="bao-cao" element={<BaoCaoPage />} />
                <Route path="nhan-vien" element={<NhanVienPage />} />
              </Route>
              <Route
                path="*"
                element={
                  <div className="page">
                    <h1>Không tìm thấy trang</h1>
                    <Link to="/">Về trang tổng quan</Link>
                  </div>
                }
              />
            </Route>
          </Route>
        </Routes>
      </ToastProvider>
    </AuthProvider>
  );
}
