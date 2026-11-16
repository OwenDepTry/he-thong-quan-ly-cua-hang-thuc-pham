package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 * Bộ icon vector vẽ bằng Java2D (phong cách nét mảnh giống Tabler/Lucide).
 * Không cần file ảnh hay thư viện ngoài, hiển thị sắc nét ở mọi độ phân giải
 * và giống nhau trên mọi máy (không phụ thuộc font emoji).
 *
 * Mỗi icon được mô tả trên lưới 24x24 bằng các lệnh:
 *   M x y | L x y | H x | V y | C x1 y1 x2 y2 x y | Z   (giống SVG path, tọa độ tuyệt đối)
 *   O cx cy r        – đường tròn (nét)
 *   D cx cy r        – chấm tròn (tô đặc)
 *   A x y w h s e    – cung tròn (Arc2D, độ)
 */
public final class AppIcons {

    private AppIcons() {
    }

    private static final Map<String, String> PATHS = new HashMap<>();

    static {
        PATHS.put("home", "M3 11 L12 3 L21 11 M5 9.5 V21 H19 V9.5 M10 21 V15 H14 V21");
        PATHS.put("box", "M3 7 L12 3 L21 7 L21 17 L12 21 L3 17 Z M3 7 L12 11 L21 7 M12 11 V21");
        PATHS.put("users", "O 9 8 3.5 M2.5 20 C2.5 16 5 14 9 14 C13 14 15.5 16 15.5 20 "
                + "M15.5 4.6 C17.6 5 18.6 6.6 18.6 8 C18.6 9.6 17.5 11 15.8 11.4 M18 14.2 C20.3 14.8 21.5 16.6 21.5 20");
        PATHS.put("user", "O 12 8 4 M4 21 C4 16.8 7.5 14.5 12 14.5 C16.5 14.5 20 16.8 20 21");
        PATHS.put("truck", "M2 6 H14 V16 H2 Z M14 9.5 H18 L21.5 13 V16 H14 O 6.5 18 2 O 17 18 2");
        PATHS.put("receipt", "M5 3 H19 V21 L16.5 19.3 L14.2 21 L12 19.3 L9.8 21 L7.5 19.3 L5 21 Z "
                + "M9 8 H15 M9 12 H15 M9 16 H13");
        PATHS.put("import", "M4 15 V19 H20 V15 M12 3 V14 M7.5 9.5 L12 14 L16.5 9.5");
        PATHS.put("export", "M4 15 V19 H20 V15 M12 14 V3 M7.5 7.5 L12 3 L16.5 7.5");
        PATHS.put("tag", "M3 12 V4 H11 L21 14 L14 21 Z D 7.5 8.5 1.6");
        PATHS.put("chart", "M3 21 H21 M6.5 21 V13 M11 21 V6 M15.5 21 V10 M20 21 V15");
        PATHS.put("info", "O 12 12 9 M12 11 V16.5 D 12 7.8 1.2");
        PATHS.put("history", "O 12 12 9 M12 7 V12 L15.5 14");
        PATHS.put("logout", "M10 4 H5 V20 H10 M15 8 L19 12 L15 16 M19 12 H9");
        PATHS.put("plus", "M12 5 V19 M5 12 H19");
        PATHS.put("trash", "M4 7 H20 M9.5 4 H14.5 M6 7 L7 20 H17 L18 7 M10 11 V16 M14 11 V16");
        PATHS.put("edit", "M4 20 L4.6 16.2 L15.8 5 L19 8.2 L7.8 19.4 Z M13.8 7 L17 10.2");
        PATHS.put("eye", "M2 12 C5 6.5 19 6.5 22 12 C19 17.5 5 17.5 2 12 Z O 12 12 3");
        PATHS.put("file", "M6 3 H14 L19 8 V21 H6 Z M14 3 V8 H19 M9 13 H16 M9 17 H14");
        PATHS.put("table", "M3 5 H21 V19 H3 Z M3 10 H21 M3 14.5 H21 M9 5 V19");
        PATHS.put("search", "O 11 11 6.5 M16 16 L20.5 20.5");
        PATHS.put("refresh", "A 4 4 16 16 25 285 M20.5 4 V9.6 H15");
        PATHS.put("reset", "A 4 4 16 16 -130 285 M3.5 4 V9.6 H9");
        PATHS.put("basket", "M3 9.5 H21 L19 20 H5 Z M8 9.5 L11 3.5 M16 9.5 L13 3.5 M9.5 13.5 V16.5 M14.5 13.5 V16.5");
        PATHS.put("lock", "M5 11 H19 V21 H5 Z M8 11 V7.5 C8 5 9.8 3.5 12 3.5 C14.2 3.5 16 5 16 7.5 V11");
        PATHS.put("moon", "M20.5 14.2 C19.3 14.9 17.9 15.3 16.4 15.3 C11.9 15.3 8.3 11.7 8.3 7.2 C8.3 5.7 8.7 4.3 9.4 3.1 C5.6 4.2 2.8 7.7 2.8 11.9 C2.8 16.9 6.9 21 11.9 21 C16.1 21 19.6 18.2 20.5 14.2 Z");
        PATHS.put("sun", "O 12 12 4 M12 2.5 V4.5 M12 19.5 V21.5 M2.5 12 H4.5 M19.5 12 H21.5 M5.3 5.3 L6.7 6.7 M17.3 17.3 L18.7 18.7 M5.3 18.7 L6.7 17.3 M17.3 6.7 L18.7 5.3");
        PATHS.put("check", "M5 12.5 L10 17.5 L19.5 7");
        PATHS.put("dot", "D 12 12 2.5");
    }

    /** Icon theo tên nghiệp vụ (tên menu / tên nút) để code gọi cho gọn. */
    public static String nameFor(String label) {
        if (label == null) {
            return "dot";
        }
        switch (label) {
            case "Trang chủ": return "home";
            case "Sản phẩm": return "box";
            case "Nhân viên": return "users";
            case "Khách hàng": return "user";
            case "Nhà cung cấp": return "truck";
            case "Hóa đơn": return "receipt";
            case "Nhập hàng": return "import";
            case "Khuyến mãi": return "tag";
            case "Thống kê": return "chart";
            case "Thông tin": return "info";
            case "Lịch sử": return "history";
            case "Đăng Xuất": return "logout";
            case "Chế độ tối": return "moon";
            case "Chế độ sáng": return "sun";
            case "Thêm": return "plus";
            case "Xóa": return "trash";
            case "Sửa": return "edit";
            case "Chi tiết": return "eye";
            case "In PDF": return "file";
            case "Xuất excel": return "export";
            case "Nhập excel": return "import";
            default: return "dot";
        }
    }

    public static Icon get(String name, int size, Color color) {
        return new VectorIcon(PATHS.getOrDefault(name, PATHS.get("dot")), size, color);
    }

    private static final class VectorIcon implements Icon {

        private final String spec;
        private final int size;
        private final Color color;

        VectorIcon(String spec, int size, Color color) {
            this.spec = spec;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            Color paint = color;
            if (c != null && !c.isEnabled()) {
                paint = new Color(color.getRed(), color.getGreen(), color.getBlue(), 90);
            }
            g2.setColor(paint);
            g2.translate(x, y);
            double s = size / 24.0;
            g2.scale(s, s);
            g2.setStroke(new BasicStroke(size <= 16 ? 2.2f : 1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g2);
            g2.dispose();
        }

        private void draw(Graphics2D g2) {
            String[] t = tokenize(spec);
            Path2D.Double path = new Path2D.Double();
            double cx = 0;
            double cy = 0;
            int i = 0;
            while (i < t.length) {
                String cmd = t[i++];
                switch (cmd) {
                    case "M":
                        cx = d(t[i++]);
                        cy = d(t[i++]);
                        path.moveTo(cx, cy);
                        break;
                    case "L":
                        cx = d(t[i++]);
                        cy = d(t[i++]);
                        path.lineTo(cx, cy);
                        break;
                    case "H":
                        cx = d(t[i++]);
                        path.lineTo(cx, cy);
                        break;
                    case "V":
                        cy = d(t[i++]);
                        path.lineTo(cx, cy);
                        break;
                    case "C":
                        double x1 = d(t[i++]);
                        double y1 = d(t[i++]);
                        double x2 = d(t[i++]);
                        double y2 = d(t[i++]);
                        cx = d(t[i++]);
                        cy = d(t[i++]);
                        path.curveTo(x1, y1, x2, y2, cx, cy);
                        break;
                    case "Z":
                        path.closePath();
                        break;
                    case "O": {
                        double ox = d(t[i++]);
                        double oy = d(t[i++]);
                        double r = d(t[i++]);
                        g2.draw(new Ellipse2D.Double(ox - r, oy - r, r * 2, r * 2));
                        break;
                    }
                    case "D": {
                        double ox = d(t[i++]);
                        double oy = d(t[i++]);
                        double r = d(t[i++]);
                        g2.fill(new Ellipse2D.Double(ox - r, oy - r, r * 2, r * 2));
                        break;
                    }
                    case "A":
                        g2.draw(new Arc2D.Double(d(t[i++]), d(t[i++]), d(t[i++]), d(t[i++]),
                                d(t[i++]), d(t[i++]), Arc2D.OPEN));
                        break;
                    default:
                        // token lạ: bỏ qua
                        break;
                }
            }
            g2.draw(path);
        }

        private static String[] tokenize(String spec) {
            java.util.List<String> out = new java.util.ArrayList<>();
            java.util.regex.Matcher m = TOKEN.matcher(spec);
            while (m.find()) {
                out.add(m.group());
            }
            return out.toArray(new String[0]);
        }

        private static final java.util.regex.Pattern TOKEN =
                java.util.regex.Pattern.compile("[MLHVCZODA]|-?\\d*\\.?\\d+");

        private static double d(String s) {
            return Double.parseDouble(s);
        }
    }
}
