package ui;

import dao.NhanVienDAO;
import dao.NhanVienDAO.LoginResult;
import dao.NhanVienDAO.LoginStatus;
import entity.NhanVien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Đăng nhập - Quản lý cửa hàng thực phẩm");
        setSize(1040, 640);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.SURFACE);
        setContentPane(root);

        root.add(createImagePanel(), BorderLayout.CENTER);
        root.add(createFormPanel(), BorderLayout.EAST);
    }

    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppTheme.SURFACE);
        outer.setPreferredSize(new Dimension(420, 0));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(320, 420));

        JLabel logo = new JLabel(AppIcons.get("basket", 26, AppTheme.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fillRoundRect(0, 0, 48, 48, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        fix(logo, 48, 48);

        JLabel lblTitle = new JLabel("Chào mừng trở lại");
        lblTitle.setFont(AppTheme.font(Font.BOLD, 24));
        lblTitle.setForeground(AppTheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Đăng nhập để tiếp tục quản lý cửa hàng");
        lblSub.setFont(AppTheme.font(Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.TEXT_MUTED);

        txtUsername = new JTextField();
        AppTheme.styleField(txtUsername, "Nhập tên tài khoản");
        txtUsername.putClientProperty("JTextField.leadingIcon", AppIcons.get("user", 16, AppTheme.TEXT_MUTED));
        fix(txtUsername, 320, 40);

        txtPassword = new JPasswordField();
        AppTheme.styleField(txtPassword, "Nhập mật khẩu");
        txtPassword.putClientProperty("JTextField.leadingIcon", AppIcons.get("lock", 16, AppTheme.TEXT_MUTED));
        txtPassword.putClientProperty("JPasswordField.showRevealButton", Boolean.TRUE);
        fix(txtPassword, 320, 40);

        AppButton btnLogin = new AppButton("Đăng nhập", null, AppButton.Variant.PRIMARY);
        btnLogin.setFont(AppTheme.font(Font.BOLD, 14));
        fix(btnLogin, 320, 42);
        btnLogin.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(btnLogin); // nhấn Enter để đăng nhập

        JLabel lblHint = new JLabel("Nhấn Enter để đăng nhập nhanh");
        lblHint.setFont(AppTheme.font(Font.PLAIN, 12));
        lblHint.setForeground(AppTheme.TEXT_MUTED);

        for (JComponent c : new JComponent[]{logo, lblTitle, lblSub, txtUsername, txtPassword, btnLogin, lblHint}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
        }

        form.add(logo);
        form.add(Box.createVerticalStrut(20));
        form.add(lblTitle);
        form.add(Box.createVerticalStrut(6));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(28));
        form.add(fieldLabel("Tên tài khoản"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("Mật khẩu"));
        form.add(Box.createVerticalStrut(6));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(24));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(12));
        form.add(lblHint);

        outer.add(form);
        return outer;
    }

    /** Đọc thông báo lỗi JDBC và nói rõ nguyên nhân + cách sửa bằng tiếng Việt. */
    static String diagnoseDbError(String raw) {
        String e = raw == null ? "" : raw.toLowerCase();
        if (e.contains("no suitable driver")) {
            return "Nguyên nhân: Java chưa nạp thư viện postgresql-*.jar (dù file đã nằm trong lib/).\n\n"
                    + "Cách sửa trong VS Code:\n"
                    + "1. Nhấn Ctrl+Shift+P, chọn \"Java: Clean Java Language Server Workspace\" rồi bấm Restart.\n"
                    + "2. Mở mục JAVA PROJECTS ở thanh bên trái, kiểm tra Referenced Libraries có postgresql-42.7.4.jar.\n"
                    + "3. Chạy lại Main.java.";
        }
        if (e.contains("refused") || e.contains("connect timed out") || e.contains("connection attempt failed")) {
            return "Nguyên nhân: không có PostgreSQL nào đang chạy ở cổng 55432 (cổng của container Docker).\n\n"
                    + "Cách sửa:\n"
                    + "1. Mở Docker Desktop, chờ biểu tượng chuyển sang \"Engine running\".\n"
                    + "2. Trong terminal ở thư mục project, chạy:  docker compose up -d\n"
                    + "3. Chạy:  docker compose ps  – cột STATUS phải là \"healthy\" rồi mới đăng nhập.";
        }
        if (e.contains("password authentication failed")) {
            return "Nguyên nhân: sai mật khẩu – app đang kết nối vào một PostgreSQL khác, không phải container Docker.\n\n"
                    + "Cách sửa:\n"
                    + "1. Nếu có file db.properties ở thư mục project, xóa nó đi (hoặc sửa db.url sang cổng 55432).\n"
                    + "2. Kiểm tra docker-compose.yml có dòng \"55432:5432\", rồi chạy:  docker compose up -d\n"
                    + "3. Chạy lại ứng dụng.";
        }
        if (e.contains("does not exist")) {
            return "Nguyên nhân: kết nối được PostgreSQL nhưng không có database qlcuahangthucpham "
                    + "(có thể đang kết nối nhầm vào một PostgreSQL khác cài sẵn trên máy).\n\n"
                    + "Cách sửa: xóa file db.properties nếu có, kiểm tra docker-compose.yml dùng cổng 55432. Hoặc chạy:  docker compose down -v  rồi  docker compose up -d  để nạp lại.";
        }
        if (e.contains("starting up") || e.contains("shutting down")) {
            return "Nguyên nhân: PostgreSQL đang khởi động (lần đầu mất khoảng 10–30 giây để nạp dữ liệu mẫu).\n\n"
                    + "Cách sửa: chờ một chút rồi bấm Đăng nhập lại.";
        }
        return "Cách khắc phục:\n"
                + "1. Mở Docker Desktop và chờ nó chạy xong.\n"
                + "2. Ở thư mục project, chạy lệnh:  docker compose up -d\n"
                + "3. Kiểm tra đã có file postgresql-*.jar trong thư mục lib/.\n"
                + "4. Bấm \"Xem chi tiết kỹ thuật\" bên dưới và gửi nội dung đó để được hỗ trợ.";
    }

    private static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.font(Font.BOLD, 13));
        lbl.setForeground(AppTheme.TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private static void fix(JComponent c, int w, int h) {
        Dimension d = new Dimension(w, h);
        c.setPreferredSize(d);
        c.setMaximumSize(d);
        c.setMinimumSize(d);
    }

    /** Ảnh nền phủ kín (crop giữ tỉ lệ) + lớp phủ tối và tên cửa hàng. */
    private JPanel createImagePanel() {
        URL imageUrl = getClass().getResource("/images/login-food.jpg");
        final Image img = imageUrl == null ? null : new ImageIcon(imageUrl).getImage();

        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                AppTheme.applyTextHints(g2);
                int w = getWidth();
                int h = getHeight();
                if (img != null && img.getWidth(this) > 0) {
                    double scale = Math.max((double) w / img.getWidth(this), (double) h / img.getHeight(this));
                    int iw = (int) (img.getWidth(this) * scale);
                    int ih = (int) (img.getHeight(this) * scale);
                    g2.drawImage(img, (w - iw) / 2, (h - ih) / 2, iw, ih, this);
                } else {
                    g2.setColor(AppTheme.PRIMARY_HOVER);
                    g2.fillRect(0, 0, w, h);
                }
                g2.setPaint(new GradientPaint(0, h * 0.35f, new Color(0, 0, 0, 0), 0, h, new Color(10, 40, 22, 215)));
                g2.fillRect(0, 0, w, h);

                g2.setColor(AppTheme.WHITE);
                g2.setFont(AppTheme.font(Font.BOLD, 30));
                g2.drawString("Cửa hàng thực phẩm", 40, h - 78);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setFont(AppTheme.font(Font.PLAIN, 15));
                g2.drawString("Quản lý bán hàng, kho và nhân sự – nhanh, chính xác.", 40, h - 48);
                g2.dispose();
            }
        };
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ tài khoản và mật khẩu!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        NhanVienDAO dao = new NhanVienDAO();
        LoginResult loginResult = dao.loginDetailed(username, password);

        if (loginResult.getStatus() != LoginStatus.SUCCESS) {
            String message;
            switch (loginResult.getStatus()) {
                case USER_NOT_FOUND:
                    message = "Tài khoản không tồn tại!";
                    break;
                case WRONG_PASSWORD:
                    message = "Mật khẩu không đúng!";
                    break;
                case INACTIVE:
                    message = "Tài khoản đã bị khóa hoặc không còn hoạt động!";
                    break;
                case DB_ERROR:
                    AppDialogs.error(this, "Không kết nối được cơ sở dữ liệu",
                            "Ứng dụng chưa kết nối được tới PostgreSQL.\n\n"
                            + diagnoseDbError(loginResult.getErrorMessage()),
                            loginResult.getErrorMessage());
                    return;
                default:
                    message = "Đăng nhập thất bại!";
                    break;
            }

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        NhanVien nv = loginResult.getNhanVien();

        String role = "NV".equalsIgnoreCase(nv.getChucVu()) ? "STAFF" : "ADMIN";
        String displayName = nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()
                ? nv.getMaNhanVien()
                : nv.getHoTen();

        AppSession.setCurrentUser(nv);

        try {
            MainFrame mainFrame = new MainFrame(role, displayName);
            mainFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Mở màn hình chính thất bại:\n" + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Error e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi biên dịch/chạy ở MainFrame hoặc panel con:\n" + e.getMessage(),
                    "Lỗi nghiêm trọng",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}