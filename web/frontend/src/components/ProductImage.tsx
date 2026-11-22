import { useState } from 'react';
import { imageUrl } from '../lib/api';
import { categoryOf } from '../lib/category';

/**
 * Ảnh sản phẩm; chưa có ảnh hoặc ảnh lỗi (mất mạng, link hỏng) thì hiện icon nhóm hàng trên nền màu nhóm.
 * Ảnh mang tính minh họa (tên sản phẩm luôn hiện bên cạnh) nên alt để trống.
 */
export function ProductImage({
  sp,
  className = '',
  iconSize = 18,
}: {
  sp: { tenSanPham: string; loai?: string | null; hinhAnh?: string | null; nguonAnh?: string | null };
  className?: string;
  iconSize?: number;
}) {
  const src = imageUrl(sp.hinhAnh);
  const [failed, setFailed] = useState<string | null>(null);
  const c = categoryOf(sp.loai ?? null, sp.tenSanPham);

  if (!src || failed === src) {
    return (
      <span className={`pimg pimg-fallback cat cat-${c.key} ${className}`} aria-hidden="true">
        <c.icon size={iconSize} />
      </span>
    );
  }
  return (
    <img
      className={`pimg cat cat-${c.key} ${className}`}
      src={src}
      alt=""
      loading="lazy"
      decoding="async"
      title={sp.nguonAnh ? `Ảnh: ${sp.nguonAnh}` : undefined}
      onError={() => setFailed(src)}
    />
  );
}
