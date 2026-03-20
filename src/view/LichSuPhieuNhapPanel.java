package view;

import dao.PhieuNhapDAO;
import entity.PhieuNhapHang;
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

public class LichSuPhieuNhapPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimMaPhieu;
    private JLabel lblTongPhieu;
    private JLabel lblTongTienNhap;

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    public LichSuPhieuNhapPanel() {
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

        JLabel lblTitle = new JLabel("LỊCH SỬ PHIẾU NHẬP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Theo dõi danh sách phiếu nhập hàng đã lưu");
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
                new String[]{"Mã phiếu", "Mã NCC", "Người nhập", "Tổng tiền", "Thời gian"},
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lịch sử phiếu nhập"));

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

        JLabel lblTim = new JLabel("Tìm theo mã phiếu:");
        lblTim.setFont(new Font("Arial", Font.BOLD, 14));

        txtTimMaPhieu = new JTextField();
        txtTimMaPhieu.setPreferredSize(new Dimension(180, 32));
        txtTimMaPhieu.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimMaPhieu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnTim = createButton("Tìm", new Color(46, 125, 50));
        JButton btnTaiLai = createButton("Tải lại", new Color(97, 97, 97));

        btnTim.addActionListener(e -> timKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimMaPhieu.setText("");
            loadData();
        });

        panel.add(lblTim);
        panel.add(txtTimMaPhieu);
        panel.add(btnTim);
        panel.add(btnTaiLai);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);

        lblTongPhieu = new JLabel("Tổng phiếu nhập: 0");
        lblTongPhieu.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongPhieu.setForeground(new Color(27, 94, 32));

        lblTongTienNhap = new JLabel("Tổng tiền nhập: 0 VNĐ");
        lblTongTienNhap.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongTienNhap.setForeground(new Color(27, 94, 32));

        panel.add(lblTongPhieu);
        panel.add(lblTongTienNhap);

        return panel;
    }

    private void loadData() {
        model.setRowCount(0);

        try {
            List<PhieuNhapHang> list = phieuNhapDAO.getAll();
            int tongTien = 0;

            for (PhieuNhapHang pn : list) {
                model.addRow(new Object[]{
                    pn.getMaPhieu(),
                    pn.getMaNCCap(),
                    pn.getNguoiNhap(),
                    formatMoney(pn.getTongTien()),
                    pn.getThoiGian()
                });

                tongTien += pn.getTongTien();
            }

            lblTongPhieu.setText("Tổng phiếu nhập: " + list.size());
            lblTongTienNhap.setText("Tổng tiền nhập: " + formatMoney(tongTien));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được lịch sử phiếu nhập.\n" + e.getMessage()
            );
        }
    }

    private void timKiem() {
        String keyword = txtTimMaPhieu.getText().trim().toLowerCase();
        model.setRowCount(0);

        try {
            List<PhieuNhapHang> list = phieuNhapDAO.getAll();
            int count = 0;
            int tongTien = 0;

            for (PhieuNhapHang pn : list) {
                if (pn.getMaPhieu() != null && pn.getMaPhieu().toLowerCase().contains(keyword)) {
                    model.addRow(new Object[]{
                        pn.getMaPhieu(),
                        pn.getMaNCCap(),
                        pn.getNguoiNhap(),
                        formatMoney(pn.getTongTien()),
                        pn.getThoiGian()
                    });
                    count++;
                    tongTien += pn.getTongTien();
                }
            }

            lblTongPhieu.setText("Tổng phiếu nhập: " + count);
            lblTongTienNhap.setText("Tổng tiền nhập: " + formatMoney(tongTien));

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
}