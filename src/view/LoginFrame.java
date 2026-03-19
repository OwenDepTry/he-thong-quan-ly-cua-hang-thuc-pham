package view;

import dao.AuthDAO;
import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống quản lý cửa hàng thực phẩm");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 248, 250));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", JLabel.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(34, 139, 34));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setPreferredSize(new Dimension(500, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Tên tài khoản:");
        JLabel lblPass = new JLabel("Mật khẩu:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(btnLogin, gbc);

        root.add(lblTitle, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);

        add(root);

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        System.out.println("INPUT USER: [" + username + "]");
        System.out.println("INPUT PASS: [" + password + "]");

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
