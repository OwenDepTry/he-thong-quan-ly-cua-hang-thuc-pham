import { ApiError, api, setUnauthorizedHandler, tokenStore } from './api';

function mockFetch(status: number, body: unknown) {
  const fn = vi.fn().mockResolvedValue(
    new Response(body === undefined ? null : JSON.stringify(body), {
      status,
      headers: { 'Content-Type': 'application/json' },
    }),
  );
  vi.stubGlobal('fetch', fn);
  return fn;
}

describe('api', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    tokenStore.set(null);
  });

  it('gửi token trong header Authorization', async () => {
    tokenStore.set('abc');
    const fetch = mockFetch(200, []);
    await api.sanPham('sua');
    const [url, init] = fetch.mock.calls[0];
    expect(url).toBe('/api/san-pham?q=sua');
    expect(init.headers.Authorization).toBe('Bearer abc');
  });

  it('trả lỗi kèm thông báo và lỗi từng trường từ backend', async () => {
    mockFetch(400, { message: 'Dữ liệu không hợp lệ.', errors: { gia: 'Giá không được âm' } });
    const err = await api.themSanPham({} as never).catch((e) => e);
    expect(err).toBeInstanceOf(ApiError);
    expect(err.status).toBe(400);
    expect(err.message).toBe('Dữ liệu không hợp lệ.');
    expect(err.fieldErrors.gia).toBe('Giá không được âm');
  });

  it('401 ở API thường thì gọi đăng xuất, còn sai mật khẩu thì không', async () => {
    const handler = vi.fn();
    setUnauthorizedHandler(handler);

    mockFetch(401, { message: 'Sai mã nhân viên hoặc mật khẩu.' });
    await expect(api.login('a', 'b')).rejects.toThrow('Sai mã nhân viên hoặc mật khẩu.');
    expect(handler).not.toHaveBeenCalled();

    mockFetch(401, null);
    await expect(api.dashboard()).rejects.toThrow('Phiên đăng nhập đã hết hạn');
    expect(handler).toHaveBeenCalledOnce();
  });

  it('báo lỗi dễ hiểu khi mất kết nối', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')));
    await expect(api.dashboard()).rejects.toMatchObject({ status: 0 });
  });
});
