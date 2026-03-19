package view;

import dao.NhanVienDAO;
import dao.TinhThanhDAO;
import entity.NhanVien;
import entity.TinhThanh;
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

public class NhanVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaNV;
    private JTextField txtHo;
    private JTextField txtTenLot;
    private JTextField txtTen;
    private JTextField txtNgaySinh;
    private JTextField txtSDT;
    private JTextField txtDiaChi;
    private JTextField txtLuong;
    private JTextField txtMatKhau;

    private JComboBox<String> cboPhai;
    private JComboBox<TinhThanh> cboTinhThanh;
    private JComboBox<String> cboChucVu;
    private JComboBox<String> cboTrangThai;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public NhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
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
                new String[]{"Mã NV", "Họ tên", "Phái", "Ngày sinh", "SĐT", "Tỉnh thành", "Địa chỉ", "Lương", "Chức vụ", "Trạng thái"}, 0
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = new JTextField();
        txtHo = new JTextField();
        txtTenLot = new JTextField();
        txtTen = new JTextField();
        txtNgaySinh = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();
        txtLuong = new JTextField();
        txtMatKhau = new JTextField();

        cboPhai = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboTinhThanh = new JComboBox<>();
        cboChucVu = new JComboBox<>(new String[]{"ADM", "NV"});
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        addFormRow(formPanel, gbc, 0, "Mã nhân viên:", txtMaNV);
        addFormRow(formPanel, gbc, 1, "Họ:", txtHo);
        addFormRow(formPanel, gbc, 2, "Tên lót:", txtTenLot);
        addFormRow(formPanel, gbc, 3, "Tên:", txtTen);
        addFormRow(formPanel, gbc, 4, "Phái:", cboPhai);
        addFormRow(formPanel, gbc, 5, "Ngày sinh (yyyy-mm-dd):", txtNgaySinh);
        addFormRow(formPanel, gbc, 6, "SĐT:", txtSDT);
        addFormRow(formPanel, gbc, 7, "Tỉnh thành:", cboTinhThanh);
        addFormRow(formPanel, gbc, 8, "Địa chỉ:", txtDiaChi);
        addFormRow(formPanel, gbc, 9, "Lương:", txtLuong);
        addFormRow(formPanel, gbc, 10, "Chức vụ:", cboChucVu);
        addFormRow(formPanel, gbc, 11, "Trạng thái:", cboTrangThai);
        addFormRow(formPanel, gbc, 12, "Mật khẩu:", txtMatKhau);

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

        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnXoa.addActionListener(e -> xoaNhanVien());
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
        fillTable(nhanVienDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(nhanVienDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<NhanVien> list) {
        model.setRowCount(0);

        for (NhanVien nv : list) {
            model.addRow(new Object[]{
                nv.getMaNV(),
                nv.getHoTenDayDu(),
                nv.getPhai(),
                nv.getNgaySinh(),
                nv.getSdt(),
                getTinhThanhName(nv.getTinhThanh()),
                nv.getDiaChi(),
                nv.getLuong(),
                nv.getChucVu(),
                nv.getTrangThai()
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

        String maNV = model.getValueAt(row, 0).toString();
        List<NhanVien> list = nhanVienDAO.getAll();

        for (NhanVien nv : list) {
            if (nv.getMaNV().equals(maNV)) {
                txtMaNV.setText(nv.getMaNV());
                txtHo.setText(nv.getHo());
                txtTenLot.setText(nv.getTenLot());
                txtTen.setText(nv.getTen());
                cboPhai.setSelectedItem(nv.getPhai());
                txtNgaySinh.setText(String.valueOf(nv.getNgaySinh()));
                txtSDT.setText(nv.getSdt());
                txtDiaChi.setText(nv.getDiaChi());
                txtLuong.setText(String.valueOf(nv.getLuong()));
                cboChucVu.setSelectedItem(nv.getChucVu());
                cboTrangThai.setSelectedItem(nv.getTrangThai());
                txtMatKhau.setText(nv.getMatKhau());
                selectTinhThanh(nv.getTinhThanh());

                txtMaNV.setEditable(false);
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

    private NhanVien readForm() {
        String maNV = txtMaNV.getText().trim();
        String ho = txtHo.getText().trim();
        String tenLot = txtTenLot.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String matKhau = txtMatKhau.getText().trim();

        if (maNV.isEmpty() || ho.isEmpty() || ten.isEmpty() || sdt.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã NV, họ, tên, SĐT, mật khẩu không được để trống.");
            return null;
        }

        TinhThanh tt = (TinhThanh) cboTinhThanh.getSelectedItem();
        if (tt == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tỉnh thành.");
            return null;
        }

        try {
            Date ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
            int luong = Integer.parseInt(txtLuong.getText().trim());

            if (luong < 0) {
                JOptionPane.showMessageDialog(this, "Lương phải >= 0.");
                return null;
            }

            return new NhanVien(
                    maNV,
                    ho,
                    tenLot,
                    ten,
                    cboPhai.getSelectedItem().toString(),
                    ngaySinh,
                    sdt,
                    tt.getMaTThanh(),
                    diaChi,
                    luong,
                    cboChucVu.getSelectedItem().toString(),
                    cboTrangThai.getSelectedItem().toString(),
                    matKhau
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải đúng định dạng yyyy-mm-dd và lương phải là số.");
            return null;
        }
    }

    private void themNhanVien() {
        NhanVien nv = readForm();
        if (nv == null) return;

        if (nhanVienDAO.insert(nv)) {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại.");
        }
    }

    private void suaNhanVien() {
        NhanVien nv = readForm();
        if (nv == null) return;

        if (nhanVienDAO.update(nv)) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại.");
        }
    }

    private void xoaNhanVien() {
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhân viên " + maNV + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.delete(maNV)) {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaNV.setText("");
        txtHo.setText("");
        txtTenLot.setText("");
        txtTen.setText("");
        txtNgaySinh.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtLuong.setText("");
        txtMatKhau.setText("");
        txtMaNV.setEditable(true);
        table.clearSelection();

        cboPhai.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        if (cboTinhThanh.getItemCount() > 0) cboTinhThanh.setSelectedIndex(0);
    }
}