package view;

import dao.AuthDAO;
import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống quản lý cửa hàng thực phẩm");
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(236, 245, 236));

        JPanel leftPanel = createLeftBanner();
        JPanel rightPanel = createLoginForm();

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());
    }

    private JPanel createLeftBanner() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(46, 125, 50),
                        getWidth(), getHeight(), new Color(102, 187, 106)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(40, 50, 180, 180);
                g2.fillOval(220, 250, 220, 220);
                g2.fillOval(80, 380, 130, 130);

                g2.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(470, 560));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 36, 40, 36));

        JLabel lblBrand = new JLabel("GREEN MART");
        lblBrand.setFont(new Font("Arial", Font.BOLD, 34));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblLine1 = new JLabel("HỆ THỐNG QUẢN LÝ");
        lblLine1.setFont(new Font("Arial", Font.BOLD, 30));
        lblLine1.setForeground(Color.WHITE);
        lblLine1.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblLine2 = new JLabel("CỬA HÀNG THỰC PHẨM");
        lblLine2.setFont(new Font("Arial", Font.BOLD, 30));
        lblLine2.setForeground(Color.WHITE);
        lblLine2.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(
                "<html><div style='width:340px;'>"
                + "Quản lý sản phẩm, hóa đơn, phiếu nhập, khách hàng, nhân viên "
                + "và thống kê doanh thu trong một hệ thống thống nhất."
                + "</div></html>"
        );
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDesc.setForeground(new Color(240, 255, 240));
        lblDesc.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblIcons = new JLabel("🥬  🥕  🍎  🥛  🧾");
        lblIcons.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcons.setForeground(Color.WHITE);
        lblIcons.setAlignmentX(LEFT_ALIGNMENT);

        JPanel featureBox = new JPanel();
        featureBox.setOpaque(false);
        featureBox.setLayout(new BoxLayout(featureBox, BoxLayout.Y_AXIS));
        featureBox.setAlignmentX(LEFT_ALIGNMENT);
        featureBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel f1 = new JLabel("• Quản lý bán hàng nhanh chóng");
        JLabel f2 = new JLabel("• Kiểm soát nhập hàng và tồn kho");
        JLabel f3 = new JLabel("• Giao diện trực quan, dễ sử dụng");

        Font f = new Font("Arial", Font.PLAIN, 16);
        f1.setFont(f);
        f2.setFont(f);
        f3.setFont(f);

        f1.setForeground(Color.WHITE);
        f2.setForeground(Color.WHITE);
        f3.setForeground(Color.WHITE);

        featureBox.add(f1);
        featureBox.add(Box.createVerticalStrut(8));
        featureBox.add(f2);
        featureBox.add(Box.createVerticalStrut(8));
        featureBox.add(f3);

        JLabel footer = new JLabel("Phần mềm quản lý cửa hàng thực phẩm");
        footer.setFont(new Font("Arial", Font.ITALIC, 14));
        footer.setForeground(new Color(235, 255, 235));
        footer.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(lblBrand);
        panel.add(Box.createVerticalStrut(35));
        panel.add(lblLine1);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblLine2);
        panel.add(Box.createVerticalStrut(24));
        panel.add(lblDesc);
        panel.add(Box.createVerticalStrut(28));
        panel.add(lblIcons);
        panel.add(Box.createVerticalGlue());
        panel.add(featureBox);
        panel.add(Box.createVerticalStrut(18));
        panel.add(footer);

        return panel;
    }

    private JPanel createLoginForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(236, 245, 236));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(380, 400));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(198, 220, 198), 1),
                BorderFactory.createEmptyBorder(28, 30, 28, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(34, 139, 34));
        card.add(lblTitle, gbc);

        gbc.gridy++;
        JLabel lblSub = new JLabel("Nhập thông tin để truy cập chương trình", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(110, 110, 110));
        card.add(lblSub, gbc);

        gbc.gridy++;
        JLabel line = new JLabel(" ");
        line.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 235, 225)));
        card.add(line, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel lblUser = new JLabel("Mã nhân viên / Tài khoản");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        lblUser.setForeground(new Color(55, 71, 79));
        card.add(lblUser, gbc);

        gbc.gridy++;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsername.setPreferredSize(new Dimension(260, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(txtUsername, gbc);

        gbc.gridy++;
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        lblPass.setForeground(new Color(55, 71, 79));
        card.add(lblPass, gbc);

        gbc.gridy++;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        txtPassword.setPreferredSize(new Dimension(260, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(txtPassword, gbc);

        gbc.gridy++;
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setBackground(new Color(46, 125, 50));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btnLogin.setPreferredSize(new Dimension(260, 42));
        card.add(btnLogin, gbc);

        gbc.gridy++;
        JLabel lblNote = new JLabel(
                "<html><center>Hệ thống quản lý cửa hàng thực phẩm<br>Green Mart</center></html>",
                SwingConstants.CENTER
        );
        lblNote.setFont(new Font("Arial", Font.ITALIC, 13));
        lblNote.setForeground(new Color(120, 120, 120));
        card.add(lblNote, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ tài khoản và mật khẩu.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        AuthDAO dao = new AuthDAO();
        User user = dao.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đăng nhập thành công. Xin chào " + user.getHoTen() + "!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Sai tài khoản hoặc mật khẩu, hoặc tài khoản đã bị khóa.",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}