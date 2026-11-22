import { Apple, Beef, Cookie, Egg, Fish, Milk, Package, Carrot, Wheat, type LucideIcon } from 'lucide-react';
import { normalize } from './format';

/**
 * Nhóm hàng → màu + icon. Màu mang thông tin (nhìn là biết rau, thịt, hải sản…),
 * dùng chung cho ô sản phẩm khi bán hàng, nhãn loại trong bảng và bộ lọc.
 */
export interface Category {
  key: string;
  label: string;
  icon: LucideIcon;
}

const RAU: Category = { key: 'rau', label: 'Rau củ', icon: Carrot };
const THIT: Category = { key: 'thit', label: 'Thịt', icon: Beef };
const HAI_SAN: Category = { key: 'haisan', label: 'Hải sản', icon: Fish };
const TRAI_CAY: Category = { key: 'traicay', label: 'Trái cây', icon: Apple };
const SUA: Category = { key: 'sua', label: 'Sữa, đồ uống', icon: Milk };
const KHO: Category = { key: 'kho', label: 'Đồ khô', icon: Wheat };
const BANH: Category = { key: 'banh', label: 'Bánh kẹo', icon: Cookie };
const TRUNG: Category = { key: 'trung', label: 'Trứng, đậu', icon: Egg };
const KHAC: Category = { key: 'khac', label: 'Khác', icon: Package };

const SEAFOOD = /\b(ca|tom|muc|cua|ghe|so|oc|ngheu|hai san)\b/;

/** Xác định nhóm từ cột Loại; "Thịt và hải sản" được tách theo tên sản phẩm (cá, tôm, mực… là hải sản). */
export function categoryOf(loai: string | null | undefined, tenSanPham = ''): Category {
  const l = normalize(loai);
  const name = normalize(tenSanPham);
  if (l.includes('rau') || l.includes('cu qua')) return RAU;
  if (l.includes('trai cay') || l.includes('hoa qua')) return TRAI_CAY;
  if (l.includes('thit') || l.includes('hai san')) {
    return l.startsWith('hai san') || SEAFOOD.test(name) ? HAI_SAN : THIT;
  }
  if (l.includes('sua') || l.includes('uong') || l.includes('nuoc')) return SUA;
  if (l.includes('kho') || l.includes('gia vi') || l.includes('gao')) return KHO;
  if (l.includes('banh') || l.includes('keo')) return BANH;
  if (l.includes('trung') || l.includes('dau')) return TRUNG;
  return KHAC;
}
