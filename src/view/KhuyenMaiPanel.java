package view;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class KhuyenMaiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JTextField txtPhanTram;
    private JTextField txtNgayBatDau;
    private JTextField txtNgayKetThuc;

    private JComboBox<String> cboTrangThai;

    private JLabel lblTongKM;
    private JLabel lblDangHoatDong;

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    public KhuyenMaiPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý chương trình giảm giá, thời gian áp dụng và trạng thái");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(235, 255, 235));

        JPanel text = new JPanel(new GridLayout(2, 1));
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
        top.add(createSearchPanel(), BorderLayout.NORTH);
        top.add(createSummaryPanel(), BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new String[]{"Mã KM", "Tên KM", "Giảm (%)", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        configTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách khuyến mãi"));

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(390, 100));
        right.add(createFormPanel(), BorderLayout.CENTER);
        right.add(createButtonPanel(), BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(scrollTable, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        return main;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel lbl = new JLabel("Tìm kiếm:");
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(260, 34));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchData();
            }
        });

        panel.add(lbl);
        panel.add(txtSearch);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);

        lblTongKM = createSummaryLabel("Tổng khuyến mãi: 0");
        lblDangHoatDong = createSummaryLabel("Đang hoạt động: 0");

        panel.add(lblTongKM);
        panel.add(lblDangHoatDong);

        return panel;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        txtMaKM = new JTextField();
        txtTenKM = new JTextField();
        txtPhanTram = new JTextField();
        txtNgayBatDau = new JTextField();
        txtNgayKetThuc = new JTextField();

        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        styleField(txtMaKM);
        styleField(txtTenKM);
        styleField(txtPhanTram);
        styleField(txtNgayBatDau);
        styleField(txtNgayKetThuc);
        styleCombo(cboTrangThai);

        panel.add(createFormLabel("Mã KM:"));
        panel.add(txtMaKM);
        panel.add(createFormLabel("Tên khuyến mãi:"));
        panel.add(txtTenKM);
        panel.add(createFormLabel("Phần trăm giảm:"));
        panel.add(txtPhanTram);
        panel.add(createFormLabel("Ngày bắt đầu:"));
        panel.add(txtNgayBatDau);
        panel.add(createFormLabel("Ngày kết thúc:"));
        panel.add(txtNgayKetThuc);
        panel.add(createFormLabel("Trạng thái:"));
        panel.add(cboTrangThai);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton btnThem = createButton("Thêm", new Color(46, 125, 50));
        JButton btnSua = createButton("Sửa", new Color(21, 101, 192));
        JButton btnXoa = createButton("Xóa", new Color(198, 40, 40));
        JButton btnLamMoi = createButton("Làm mới", new Color(97, 97, 97));

        btnThem.addActionListener(e -> themKM());
        btnSua.addActionListener(e -> suaKM());
        btnXoa.addActionListener(e -> xoaKM());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);

        return panel;
    }

    private void loadData() {
        fillTable(khuyenMaiDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(khuyenMaiDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<KhuyenMai> list) {
        model.setRowCount(0);
        int dangHoatDong = 0;

        for (KhuyenMai km : list) {
            model.addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                km.getPhanTramGiam() + "%",
                km.getNgayBatDau(),
                km.getNgayKetThuc(),
                km.getTrangThai()
            });

            if ("active".equalsIgnoreCase(km.getTrangThai())) {
                dangHoatDong++;
            }
        }

        lblTongKM.setText("Tổng khuyến mãi: " + list.size());
        lblDangHoatDong.setText("Đang hoạt động: " + dangHoatDong);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String ma = model.getValueAt(row, 0).toString();
        for (KhuyenMai km : khuyenMaiDAO.getAll()) {
            if (km.getMaKM().equals(ma)) {
                txtMaKM.setText(km.getMaKM());
                txtTenKM.setText(km.getTenKM());
                txtPhanTram.setText(String.valueOf(km.getPhanTramGiam()));
                txtNgayBatDau.setText(String.valueOf(km.getNgayBatDau()));
                txtNgayKetThuc.setText(String.valueOf(km.getNgayKetThuc()));
                cboTrangThai.setSelectedItem(km.getTrangThai());
                txtMaKM.setEditable(false);
                break;
            }
        }
    }

    private KhuyenMai readForm() {
        String ma = txtMaKM.getText().trim();
        String ten = txtTenKM.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã KM và tên khuyến mãi không được để trống.");
            return null;
        }

        try {
            int phanTram = Integer.parseInt(txtPhanTram.getText().trim());
            Date ngayBD = Date.valueOf(txtNgayBatDau.getText().trim());
            Date ngayKT = Date.valueOf(txtNgayKetThuc.getText().trim());

            if (phanTram < 0 || phanTram > 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm phải từ 0 đến 100.");
                return null;
            }

            if (ngayBD.after(ngayKT)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
                return null;
            }

            return new KhuyenMai(
                    ma,
                    ten,
                    phanTram,
                    ngayBD,
                    ngayKT,
                    cboTrangThai.getSelectedItem().toString()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày phải theo dạng yyyy-mm-dd và phần trăm giảm phải là số.");
            return null;
        }
    }

    private void themKM() {
        KhuyenMai km = readForm();
        if (km == null) return;

        if (khuyenMaiDAO.insert(km)) {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại.");
        }
    }

    private void suaKM() {
        KhuyenMai km = readForm();
        if (km == null) return;

        if (khuyenMaiDAO.update(km)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại.");
        }
    }

    private void xoaKM() {
        String ma = txtMaKM.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khuyến mãi " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaKM.setText("");
        txtTenKM.setText("");
        txtPhanTram.setText("");
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtMaKM.setEditable(true);
        table.clearSelection();
        cboTrangThai.setSelectedIndex(0);
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 36));
        return btn;
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
}