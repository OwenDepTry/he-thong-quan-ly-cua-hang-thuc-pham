package view;

import dao.AuthDAO;
import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống quản lý cửa hàng thực phẩm");
        setSize(920, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(240, 244, 247));

        JPanel leftPanel = createLeftBanner();
        JPanel rightPanel = createLoginForm();

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());
    }

    private JPanel createLeftBanner() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(430, 520));
        panel.setBackground(new Color(39, 174, 96));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 35, 40, 35));

        JLabel lblStore = new JLabel("GREEN MART");
        lblStore.setFont(new Font("Arial", Font.BOLD, 32));
        lblStore.setForeground(Color.WHITE);
        lblStore.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle1 = new JLabel("HỆ THỐNG");
        lblTitle1.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle1.setForeground(Color.WHITE);
        lblTitle1.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle2 = new JLabel("QUẢN LÝ CỬA HÀNG");
        lblTitle2.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle2.setForeground(Color.WHITE);
        lblTitle2.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle3 = new JLabel("THỰC PHẨM");
        lblTitle3.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle3.setForeground(Color.WHITE);
        lblTitle3.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(
                "<html><div style='width:320px;'>"
                + "Quản lý sản phẩm, hóa đơn, phiếu nhập, khách hàng, nhân viên "
                + "và thống kê doanh thu trên cùng một hệ thống."
                + "</div></html>"
        );
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDesc.setForeground(new Color(235, 255, 240));
        lblDesc.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblIcon = new JLabel("🥬 🥕 🥛 🍎");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcon.setAlignmentX(LEFT_ALIGNMENT);
        lblIcon.setForeground(Color.WHITE);

        JPanel infoBox = new JPanel();
        infoBox.setOpaque(false);
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setAlignmentX(LEFT_ALIGNMENT);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblInfo1 = new JLabel("• Giao diện dễ dùng");
        JLabel lblInfo2 = new JLabel("• Quản lý nhanh, rõ ràng");
        JLabel lblInfo3 = new JLabel("• Phù hợp cho cửa hàng thực phẩm");

        Font infoFont = new Font("Arial", Font.PLAIN, 16);
        lblInfo1.setFont(infoFont);
        lblInfo2.setFont(infoFont);
        lblInfo3.setFont(infoFont);

        lblInfo1.setForeground(Color.WHITE);
        lblInfo2.setForeground(Color.WHITE);
        lblInfo3.setForeground(Color.WHITE);

        infoBox.add(lblInfo1);
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(lblInfo2);
        infoBox.add(Box.createVerticalStrut(8));
        infoBox.add(lblInfo3);

        panel.add(lblStore);
        panel.add(Box.createVerticalStrut(35));
        panel.add(lblTitle1);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblTitle2);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblTitle3);
        panel.add(Box.createVerticalStrut(24));
        panel.add(lblDesc);
        panel.add(Box.createVerticalStrut(25));
        panel.add(lblIcon);
        panel.add(Box.createVerticalGlue());
        panel.add(infoBox);

        return panel;
    }

    private JPanel createLoginForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 244, 247));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(360, 360));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel lblWelcome = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(34, 139, 34));
        card.add(lblWelcome, gbc);

        gbc.gridy++;
        JLabel lblSub = new JLabel("Vui lòng nhập tài khoản và mật khẩu", JLabel.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(110, 110, 110));
        card.add(lblSub, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel lblUser = new JLabel("Tên tài khoản");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(lblUser, gbc);

        gbc.gridy++;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsername.setPreferredSize(new Dimension(240, 38));
        card.add(txtUsername, gbc);

        gbc.gridy++;
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(lblPass, gbc);

        gbc.gridy++;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        txtPassword.setPreferredSize(new Dimension(240, 38));
        card.add(txtPassword, gbc);

        gbc.gridy++;
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(240, 42));
        card.add(btnLogin, gbc);

        gbc.gridy++;
        JLabel lblHint = new JLabel("GreenMart - Quản lý cửa hàng thực phẩm", JLabel.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 13));
        lblHint.setForeground(new Color(130, 130, 130));
        card.add(lblHint, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
            return;
        }

        AuthDAO dao = new AuthDAO();
        User user = dao.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công. Xin chào " + user.getHoTen() + "!");
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu, hoặc tài khoản đã bị khóa.");
        }
    }
}