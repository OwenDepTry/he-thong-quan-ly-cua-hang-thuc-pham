import { useState, type FormEvent } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? '/';

  const [ma, setMa] = useState('');
  const [matKhau, setMatKhau] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

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
      <form className="login-card" onSubmit={submit} noValidate>
        <div className="login-head">
          <span className="brand-mark big" aria-hidden="true">
            🛒
          </span>
          <h1>Đăng nhập</h1>
          <p>Hệ thống quản lý cửa hàng thực phẩm</p>
        </div>
        <label className="field">
          <span>Mã nhân viên</span>
          <input
            autoFocus
            autoComplete="username"
            value={ma}
            onChange={(e) => setMa(e.target.value)}
            placeholder="VD: admin"
          />
        </label>
        <label className="field">
          <span>Mật khẩu</span>
          <input
            type="password"
            autoComplete="current-password"
            value={matKhau}
            onChange={(e) => setMatKhau(e.target.value)}
          />
        </label>
        {error && (
          <div className="alert alert-error" role="alert">
            {error}
          </div>
        )}
        <button className="btn btn-primary btn-block" type="submit" disabled={busy}>
          {busy ? 'Đang đăng nhập…' : 'Đăng nhập'}
        </button>
      </form>
    </div>
  );
}
