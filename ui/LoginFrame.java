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
import java.awt.Insets;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
        setSize(1300, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(235, 235, 235));
        root.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
        setContentPane(root);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(185, 185, 185), 1));
        root.add(card, BorderLayout.CENTER);

        JLabel lblHeader = new JLabel("Đăng nhập", SwingConstants.CENTER);
        lblHeader.setOpaque(true);
        lblHeader.setBackground(new Color(67, 160, 71));
        lblHeader.setForeground(new Color(255, 230, 30));
        lblHeader.setFont(new Font("Arial", Font.BOLD, 34));
        lblHeader.setPreferredSize(new Dimension(100, 110));
        card.add(lblHeader, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        card.add(body, BorderLayout.CENTER);

        body.add(createLeftPanel(), BorderLayout.WEST);
        body.add(createImagePanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(new Color(242, 242, 242));
        left.setPreferredSize(new Dimension(340, 0));
        left.setBorder(BorderFactory.createEmptyBorder(90, 32, 40, 20));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblUser = new JLabel("Tên tài khoản");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUser.setAlignmentX(LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(260, 40));
        txtUsername.setPreferredSize(new Dimension(260, 40));
        txtUsername.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190), 1));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPass.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(260, 40));
        txtPassword.setPreferredSize(new Dimension(260, 40));
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190), 1));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(0, 140, 75));
        btnLogin.setOpaque(true);
        btnLogin.setContentAreaFilled(true);
        btnLogin.setBorderPainted(true);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 110, 60), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnLogin.setMargin(new Insets(0, 0, 0, 0));
        btnLogin.setMaximumSize(new Dimension(140, 42));
        btnLogin.setPreferredSize(new Dimension(140, 42));
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());

        left.add(lblUser);
        left.add(Box.createVerticalStrut(10));
        left.add(txtUsername);
        left.add(Box.createVerticalStrut(40));
        left.add(lblPass);
        left.add(Box.createVerticalStrut(10));
        left.add(txtPassword);
        left.add(Box.createVerticalStrut(40));
        left.add(btnLogin);

        return left;
    }

    private JPanel createImagePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        URL imageUrl = getClass().getResource("/images/login-food.jpg");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image img = icon.getImage().getScaledInstance(900, 560, Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(img));
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            wrapper.add(lbl, BorderLayout.CENTER);
        } else {
            JLabel fallback = new JLabel("login-food.jpg", SwingConstants.CENTER);
            fallback.setOpaque(true);
            fallback.setBackground(new Color(210, 230, 210));
            wrapper.add(fallback, BorderLayout.CENTER);
        }

        return wrapper;
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
                    message = "Không thể kết nối cơ sở dữ liệu.\n"
                            + (loginResult.getErrorMessage() == null ? "" : loginResult.getErrorMessage());
                    break;
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