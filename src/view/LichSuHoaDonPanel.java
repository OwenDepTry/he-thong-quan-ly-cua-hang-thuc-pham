package view;

import dao.HoaDonDAO;
import entity.HoaDon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class LichSuHoaDonPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimMaHD;
    private JLabel lblTongHoaDon;
    private JLabel lblTongDoanhThu;

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public LichSuHoaDonPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadData();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 125, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel("LỊCH SỬ HÓA ĐƠN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Theo dõi danh sách hóa đơn đã lập trong hệ thống");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(235, 255, 235));

        JPanel text = new JPanel(new java.awt.GridLayout(2, 1));
        text.setOpaque(false);
        text.add(lblTitle);
        text.add(lblSub);

        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);
        top.add(createFilterPanel(), BorderLayout.NORTH);
        top.add(createSummaryPanel(), BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new String[]{"Mã HD", "Mã KH", "Mã NV", "Mã KM", "Tiền giảm", "Thanh toán", "Thời gian"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        configTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lịch sử hóa đơn"));

        main.add(top, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);

        return main;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblTim = new JLabel("Tìm theo mã hóa đơn:");
        lblTim.setFont(new Font("Arial", Font.BOLD, 14));

        txtTimMaHD = new JTextField();
        txtTimMaHD.setPreferredSize(new Dimension(180, 32));
        txtTimMaHD.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimMaHD.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnTim = createButton("Tìm", new Color(46, 125, 50));
        JButton btnTaiLai = createButton("Tải lại", new Color(97, 97, 97));

        btnTim.addActionListener(e -> timKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimMaHD.setText("");
            loadData();
        });

        panel.add(lblTim);
        panel.add(txtTimMaHD);
        panel.add(btnTim);
        panel.add(btnTaiLai);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);

        lblTongHoaDon = new JLabel("Tổng hóa đơn: 0");
        lblTongHoaDon.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongHoaDon.setForeground(new Color(27, 94, 32));

        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongDoanhThu.setForeground(new Color(27, 94, 32));

        panel.add(lblTongHoaDon);
        panel.add(lblTongDoanhThu);

        return panel;
    }

    private void loadData() {
        model.setRowCount(0);

        try {
            List<HoaDon> list = hoaDonDAO.getAll();
            int tongDoanhThu = 0;

            for (HoaDon hd : list) {
                model.addRow(new Object[]{
                    safe(hd.getMaHD()),
                    safe(hd.getMaKH()),
                    safe(hd.getMaNV()),
                    safe(hd.getMaKM()),
                    formatMoney(hd.getTienGiam()),
                    formatMoney(hd.getTongTien()),
                    hd.getThoiGian()
                });

                tongDoanhThu += hd.getTongTien();
            }

            lblTongHoaDon.setText("Tổng hóa đơn: " + list.size());
            lblTongDoanhThu.setText("Tổng doanh thu: " + formatMoney(tongDoanhThu));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được lịch sử hóa đơn.\n" + e.getMessage()
            );
        }
    }

    private void timKiem() {
        String keyword = txtTimMaHD.getText().trim().toLowerCase();
        model.setRowCount(0);

        try {
            List<HoaDon> list = hoaDonDAO.getAll();
            int count = 0;
            int tongDoanhThu = 0;

            for (HoaDon hd : list) {
                if (safe(hd.getMaHD()).toLowerCase().contains(keyword)) {
                    model.addRow(new Object[]{
                        safe(hd.getMaHD()),
                        safe(hd.getMaKH()),
                        safe(hd.getMaNV()),
                        safe(hd.getMaKM()),
                        formatMoney(hd.getTienGiam()),
                        formatMoney(hd.getTongTien()),
                        hd.getThoiGian()
                    });
                    count++;
                    tongDoanhThu += hd.getTongTien();
                }
            }

            lblTongHoaDon.setText("Tổng hóa đơn: " + count);
            lblTongDoanhThu.setText("Tổng doanh thu: " + formatMoney(tongDoanhThu));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm kiếm được dữ liệu.");
        }
    }

    private void configTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(new Color(225, 235, 225));
        table.setSelectionBackground(new Color(200, 230, 201));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(232, 245, 233));
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 34));
        return btn;
    }

    private String formatMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(money) + " VNĐ";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}