package view;

import dao.NhaCungCapDAO;
import dao.TinhThanhDAO;
import entity.NhaCungCap;
import entity.TinhThanh;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

public class NhaCungCapPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaNCCap;
    private JTextField txtTenNCCap;
    private JTextField txtTenLienHe;
    private JTextField txtSDT;
    private JTextField txtDiaChi;

    private JComboBox<TinhThanh> cboTinhThanh;
    private JComboBox<String> cboTrangThai;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public NhaCungCapPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÀ CUNG CẤP", SwingConstants.CENTER);
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
                new String[]{"Mã NCC", "Tên nhà cung cấp", "Tên liên hệ", "SĐT", "Tỉnh thành", "Địa chỉ", "Trạng thái"}, 0
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhà cung cấp"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNCCap = new JTextField();
        txtTenNCCap = new JTextField();
        txtTenLienHe = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();

        cboTinhThanh = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        addFormRow(formPanel, gbc, 0, "Mã nhà cung cấp:", txtMaNCCap);
        addFormRow(formPanel, gbc, 1, "Tên nhà cung cấp:", txtTenNCCap);
        addFormRow(formPanel, gbc, 2, "Tên liên hệ:", txtTenLienHe);
        addFormRow(formPanel, gbc, 3, "SĐT:", txtSDT);
        addFormRow(formPanel, gbc, 4, "Tỉnh thành:", cboTinhThanh);
        addFormRow(formPanel, gbc, 5, "Địa chỉ:", txtDiaChi);
        addFormRow(formPanel, gbc, 6, "Trạng thái:", cboTrangThai);

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

        btnThem.addActionListener(e -> themNCC());
        btnSua.addActionListener(e -> suaNCC());
        btnXoa.addActionListener(e -> xoaNCC());
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
        gbc.weightx = 0.2;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(comp, gbc);
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
        }
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

        String maNCCap = model.getValueAt(row, 0).toString();
        List<NhaCungCap> list = nhaCungCapDAO.getAll();

        for (NhaCungCap ncc : list) {
            if (ncc.getMaNCCap().equals(maNCCap)) {
                txtMaNCCap.setText(ncc.getMaNCCap());
                txtTenNCCap.setText(ncc.getTenNCCap());
                txtTenLienHe.setText(ncc.getTenLienHe());
                txtSDT.setText(ncc.getsDThoai());
                txtDiaChi.setText(ncc.getDiaChi());
                cboTrangThai.setSelectedItem(ncc.getTrangThai());
                selectTinhThanh(ncc.getTinhThanh());

                txtMaNCCap.setEditable(false);
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
        String maNCCap = txtMaNCCap.getText().trim();
        String tenNCCap = txtTenNCCap.getText().trim();
        String tenLienHe = txtTenLienHe.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maNCCap.isEmpty() || tenNCCap.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã NCC, tên nhà cung cấp, SĐT không được để trống.");
            return null;
        }

        TinhThanh tt = (TinhThanh) cboTinhThanh.getSelectedItem();
        if (tt == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tỉnh thành.");
            return null;
        }

        return new NhaCungCap(
                maNCCap,
                tenNCCap,
                tenLienHe,
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
        String maNCCap = txtMaNCCap.getText().trim();
        if (maNCCap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhà cung cấp " + maNCCap + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhaCungCapDAO.delete(maNCCap)) {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaNCCap.setText("");
        txtTenNCCap.setText("");
        txtTenLienHe.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtMaNCCap.setEditable(true);
        table.clearSelection();

        cboTrangThai.setSelectedIndex(0);
        if (cboTinhThanh.getItemCount() > 0) cboTinhThanh.setSelectedIndex(0);
    }
}