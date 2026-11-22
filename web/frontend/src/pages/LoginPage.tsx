import { useState, type FormEvent } from 'react';
import { AlertCircle, Apple, Beef, Carrot, Eye, EyeOff, Fish, Milk, Store, Wheat } from 'lucide-react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';

/** Gợi ý tài khoản dùng thử: chỉ hiện khi chạy dev hoặc bản demo (VITE_DEMO=true), không hiện ở cửa hàng thật. */
const SHOW_DEMO_ACCOUNTS = import.meta.env.DEV || import.meta.env.VITE_DEMO === 'true';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? '/';

  const [ma, setMa] = useState('');
  const [matKhau, setMatKhau] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [showPw, setShowPw] = useState(false);

  if (user) return <Navigate to={from} replace />;

  async function submit(e: FormEvent) {
    e.preventDefault();
    if (!ma.trim() || !matKhau) {
      setError('Vui lòng nhập mã nhân viên và mật khẩu.');
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await login(ma.trim(), matKhau);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Đăng nhập thất bại.');
      setBusy(false);
    }
  }

  return (
    <div className="login-page">
      <aside className="login-side" aria-hidden="true">
        <div className="brand">
          <span className="brand-mark">
            <Store size={18} />
          </span>
          <span>
            <strong>Cửa hàng thực phẩm</strong>
            <small>Quản lý bán hàng</small>
          </span>
        </div>
        <div className="login-pitch">
          <h2>Bán nhanh, kho rõ, cuối ngày khớp sổ.</h2>
          <p>Lập hóa đơn ngay tại quầy, theo dõi hàng sắp hết và lô sắp hết hạn, xem doanh thu theo ngày.</p>
        </div>
        <div className="login-cats">
          <span><Carrot size={20} /></span>
          <span><Beef size={20} /></span>
          <span><Fish size={20} /></span>
          <span><Apple size={20} /></span>
          <span><Milk size={20} /></span>
          <span><Wheat size={20} /></span>
        </div>
      </aside>

      <main className="login-main">
        <form className="login-form" onSubmit={submit} noValidate>
          <h1>Đăng nhập</h1>
          <p>Dùng mã nhân viên và mật khẩu cửa hàng cấp cho bạn.</p>
          <label className="field">
            <span>Mã nhân viên</span>
            <input
              autoFocus
              autoComplete="username"
              value={ma}
              onChange={(e) => setMa(e.target.value)}
              placeholder="VD: NV01"
            />
          </label>
          <div className="field">
            <label htmlFor="login-pw">Mật khẩu</label>
            <div className="pw">
              <input
                id="login-pw"
                type={showPw ? 'text' : 'password'}
                autoComplete="current-password"
                value={matKhau}
                onChange={(e) => setMatKhau(e.target.value)}
              />
              <button
                type="button"
                className="icon-btn"
                onClick={() => setShowPw((v) => !v)}
                aria-label={showPw ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
              >
                {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>
          {error && (
            <div className="alert alert-error" role="alert">
              <AlertCircle size={16} aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}
          <button className="btn btn-primary btn-block" type="submit" disabled={busy}>
            {busy ? 'Đang đăng nhập…' : 'Đăng nhập'}
          </button>
          {SHOW_DEMO_ACCOUNTS && (
            <p className="login-hint">
              Dùng thử: <strong>admin</strong> / <strong>admin</strong> (quản lý) hoặc <strong>nhanvien</strong> / <strong>123</strong> (nhân viên).
            </p>
          )}
        </form>
      </main>
    </div>
  );
}
