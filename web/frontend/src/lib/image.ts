/**
 * Thu nhỏ ảnh ngay trên trình duyệt trước khi tải lên (cạnh dài tối đa 800px, WebP hoặc JPEG),
 * để ảnh chụp từ điện thoại vài MB chỉ còn khoảng 100 KB.
 */
export async function resizeImage(file: File, maxSide = 800): Promise<Blob> {
  if (!file.type.startsWith('image/')) throw new Error('File được chọn không phải ảnh.');
  const bitmap = await createImageBitmap(file);
  const scale = Math.min(1, maxSide / Math.max(bitmap.width, bitmap.height));
  const w = Math.round(bitmap.width * scale);
  const h = Math.round(bitmap.height * scale);
  const canvas = document.createElement('canvas');
  canvas.width = w;
  canvas.height = h;
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Trình duyệt không xử lý được ảnh.');
  ctx.drawImage(bitmap, 0, 0, w, h);
  bitmap.close();
  const toBlob = (type: string) =>
    new Promise<Blob | null>((resolve) => canvas.toBlob(resolve, type, 0.85));
  // Safari cũ không xuất được WebP: toBlob trả về PNG, khi đó dùng JPEG cho nhẹ
  const webp = await toBlob('image/webp');
  const blob = webp && webp.type === 'image/webp' ? webp : await toBlob('image/jpeg');
  if (!blob) throw new Error('Không nén được ảnh.');
  return blob;
}
