package view;

import dao.NhaCungCapDAO;
import dao.TinhThanhDAO;
import entity.NhaCungCap;
import entity.TinhThanh;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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

public class NhaCungCapPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtTenLienHe;
    private JTextField txtSDT;
    private JTextField txtDiaChi;

    private JComboBox<TinhThanh> cboTinhThanh;
    private JComboBox<String> cboTrangThai;

    private JLabel lblTongNCC;
    private JLabel lblDangHopTac;

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public NhaCungCapPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadComboData();
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

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý đơn vị cung cấp, người liên hệ và trạng thái hợp tác");
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
                new String[]{"Mã NCC", "Tên NCC", "Liên hệ", "SĐT", "Tỉnh thành", "Địa chỉ", "Trạng thái"},
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
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách nhà cung cấp"));

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

        lblTongNCC = createSummaryLabel("Tổng NCC: 0");
        lblDangHopTac = createSummaryLabel("Đang hợp tác: 0");

        panel.add(lblTongNCC);
        panel.add(lblDangHopTac);

        return panel;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        txtMaNCC = new JTextField();
        txtTenNCC = new JTextField();
        txtTenLienHe = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();

        cboTinhThanh = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        styleField(txtMaNCC);
        styleField(txtTenNCC);
        styleField(txtTenLienHe);
        styleField(txtSDT);
        styleField(txtDiaChi);
        styleCombo(cboTinhThanh);
        styleCombo(cboTrangThai);

        panel.add(createFormLabel("Mã NCC:"));
        panel.add(txtMaNCC);
        panel.add(createFormLabel("Tên NCC:"));
        panel.add(txtTenNCC);
        panel.add(createFormLabel("Tên liên hệ:"));
        panel.add(txtTenLienHe);
        panel.add(createFormLabel("Số điện thoại:"));
        panel.add(txtSDT);
        panel.add(createFormLabel("Tỉnh thành:"));
        panel.add(cboTinhThanh);
        panel.add(createFormLabel("Địa chỉ:"));
        panel.add(txtDiaChi);
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

        btnThem.addActionListener(e -> themNCC());
        btnSua.addActionListener(e -> suaNCC());
        btnXoa.addActionListener(e -> xoaNCC());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);

        return panel;
    }

    private void loadComboData() {
        cboTinhThanh.removeAllItems();
        for (TinhThanh tt : tinhThanhDAO.getAll()) {
            cboTinhThanh.addItem(tt);
        }
    }

    private void loadData() {
        fillTable(nhaCungCapDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(nhaCungCapDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<NhaCungCap> list) {
        model.setRowCount(0);
        int dangHopTac = 0;

        for (NhaCungCap ncc : list) {
            model.addRow(new Object[]{
                ncc.getMaNCCap(),
                ncc.getTenNCCap(),
                ncc.getTenLienHe(),
                ncc.getsDThoai(),
                getTinhThanhName(ncc.getTinhThanh()),
                ncc.getDiaChi(),
                ncc.getTrangThai()
            });

            if ("active".equalsIgnoreCase(ncc.getTrangThai())) {
                dangHopTac++;
            }
        }

        lblTongNCC.setText("Tổng NCC: " + list.size());
        lblDangHopTac.setText("Đang hợp tác: " + dangHopTac);
    }

    private String getTinhThanhName(int maTinhThanh) {
        for (int i = 0; i < cboTinhThanh.getItemCount(); i++) {
            TinhThanh tt = cboTinhThanh.getItemAt(i);
            if (tt.getMaTThanh() == maTinhThanh) {
                return tt.getTenTThanh();
            }
        }
        return String.valueOf(maTinhThanh);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String ma = model.getValueAt(row, 0).toString();
        for (NhaCungCap ncc : nhaCungCapDAO.getAll()) {
            if (ncc.getMaNCCap().equals(ma)) {
                txtMaNCC.setText(ncc.getMaNCCap());
                txtTenNCC.setText(ncc.getTenNCCap());
                txtTenLienHe.setText(ncc.getTenLienHe());
                txtSDT.setText(ncc.getsDThoai());
                txtDiaChi.setText(ncc.getDiaChi());
                cboTrangThai.setSelectedItem(ncc.getTrangThai());
                selectTinhThanh(ncc.getTinhThanh());
                txtMaNCC.setEditable(false);
                break;
            }
        }
    }

    private void selectTinhThanh(int maTinhThanh) {
        for (int i = 0; i < cboTinhThanh.getItemCount(); i++) {
            if (cboTinhThanh.getItemAt(i).getMaTThanh() == maTinhThanh) {
                cboTinhThanh.setSelectedIndex(i);
                break;
            }
        }
    }

    private NhaCungCap readForm() {
        String ma = txtMaNCC.getText().trim();
        String ten = txtTenNCC.getText().trim();
        String lienHe = txtTenLienHe.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || lienHe.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã NCC, tên NCC, liên hệ, SĐT không được để trống.");
            return null;
        }

        TinhThanh tt = (TinhThanh) cboTinhThanh.getSelectedItem();
        if (tt == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tỉnh thành.");
            return null;
        }

        return new NhaCungCap(
                ma,
                ten,
                lienHe,
                sdt,
                tt.getMaTThanh(),
                diaChi,
                cboTrangThai.getSelectedItem().toString()
        );
    }

    private void themNCC() {
        NhaCungCap ncc = readForm();
        if (ncc == null) return;

        if (nhaCungCapDAO.insert(ncc)) {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thất bại.");
        }
    }

    private void suaNCC() {
        NhaCungCap ncc = readForm();
        if (ncc == null) return;

        if (nhaCungCapDAO.update(ncc)) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thất bại.");
        }
    }

    private void xoaNCC() {
        String ma = txtMaNCC.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhà cung cấp " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhaCungCapDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtTenLienHe.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtMaNCC.setEditable(true);
        table.clearSelection();

        if (cboTinhThanh.getItemCount() > 0) cboTinhThanh.setSelectedIndex(0);
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