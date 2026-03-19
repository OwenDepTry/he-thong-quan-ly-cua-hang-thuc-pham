package view;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class KhuyenMaiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JTextField txtPhanTramGiam;
    private JTextField txtNgayBatDau;
    private JTextField txtNgayKetThuc;
    private JComboBox<String> cboTrangThai;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ KHUYẾN MÃI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(100, 55));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        txtSearch = new JTextField(20);
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        model = new DefaultTableModel(
                new String[]{"Mã KM", "Tên khuyến mãi", "% giảm", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scrollPane = new JScrollPane(table);

        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khuyến mãi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKM = new JTextField();
        txtTenKM = new JTextField();
        txtPhanTramGiam = new JTextField();
        txtNgayBatDau = new JTextField();
        txtNgayKetThuc = new JTextField();
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        addFormRow(formPanel, gbc, 0, "Mã khuyến mãi:", txtMaKM);
        addFormRow(formPanel, gbc, 1, "Tên khuyến mãi:", txtTenKM);
        addFormRow(formPanel, gbc, 2, "Phần trăm giảm:", txtPhanTramGiam);
        addFormRow(formPanel, gbc, 3, "Ngày bắt đầu (yyyy-mm-dd):", txtNgayBatDau);
        addFormRow(formPanel, gbc, 4, "Ngày kết thúc (yyyy-mm-dd):", txtNgayKetThuc);
        addFormRow(formPanel, gbc, 5, "Trạng thái:", cboTrangThai);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        bottomPanel.add(formPanel);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e -> suaKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnLamMoi.addActionListener(e -> lamMoiForm());

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
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        panel.add(comp, gbc);
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

        for (KhuyenMai km : list) {
            model.addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                km.getPhanTramGiam(),
                km.getNgayBatDau(),
                km.getNgayKetThuc(),
                km.getTrangThai()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        txtMaKM.setText(model.getValueAt(row, 0).toString());
        txtTenKM.setText(model.getValueAt(row, 1).toString());
        txtPhanTramGiam.setText(model.getValueAt(row, 2).toString());
        txtNgayBatDau.setText(model.getValueAt(row, 3).toString());
        txtNgayKetThuc.setText(model.getValueAt(row, 4).toString());
        cboTrangThai.setSelectedItem(model.getValueAt(row, 5).toString());

        txtMaKM.setEditable(false);
    }

    private KhuyenMai readForm() {
        String maKM = txtMaKM.getText().trim();
        String tenKM = txtTenKM.getText().trim();

        if (maKM.isEmpty() || tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã KM và tên khuyến mãi không được để trống.");
            return null;
        }

        try {
            int phanTramGiam = Integer.parseInt(txtPhanTramGiam.getText().trim());
            Date ngayBatDau = Date.valueOf(txtNgayBatDau.getText().trim());
            Date ngayKetThuc = Date.valueOf(txtNgayKetThuc.getText().trim());

            if (phanTramGiam < 0 || phanTramGiam > 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm phải từ 0 đến 100.");
                return null;
            }

            if (ngayKetThuc.before(ngayBatDau)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
                return null;
            }

            return new KhuyenMai(
                    maKM,
                    tenKM,
                    phanTramGiam,
                    ngayBatDau,
                    ngayKetThuc,
                    cboTrangThai.getSelectedItem().toString()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày phải đúng định dạng yyyy-mm-dd và % giảm phải là số.");
            return null;
        }
    }

    private void themKhuyenMai() {
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

    private void suaKhuyenMai() {
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

    private void xoaKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khuyến mãi " + maKM + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.delete(maKM)) {
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
        txtPhanTramGiam.setText("");
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtMaKM.setEditable(true);
        table.clearSelection();
        cboTrangThai.setSelectedIndex(0);
    }
}