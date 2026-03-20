package view;

import dao.NhanVienDAO;
import dao.TinhThanhDAO;
import entity.NhanVien;
import entity.TinhThanh;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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

    private JLabel lblTongNV;
    private JLabel lblTongLuong;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public NhanVienPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý hồ sơ nhân viên, vai trò, trạng thái và thông tin đăng nhập");
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
                new String[]{"Mã NV", "Họ tên", "Phái", "Ngày sinh", "SĐT", "Tỉnh thành", "Địa chỉ", "Lương", "Chức vụ", "Trạng thái"},
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
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(420, 100));
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

        panel.add(lbl);
        panel.add(txtSearch);

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

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);

        lblTongNV = createSummaryLabel("Tổng nhân viên: 0");
        lblTongLuong = createSummaryLabel("Tổng quỹ lương: 0 VNĐ");

        panel.add(lblTongNV);
        panel.add(lblTongLuong);

        return panel;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

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

        styleField(txtMaNV);
        styleField(txtHo);
        styleField(txtTenLot);
        styleField(txtTen);
        styleField(txtNgaySinh);
        styleField(txtSDT);
        styleField(txtDiaChi);
        styleField(txtLuong);
        styleField(txtMatKhau);
        styleCombo(cboPhai);
        styleCombo(cboTinhThanh);
        styleCombo(cboChucVu);
        styleCombo(cboTrangThai);

        panel.add(createFormLabel("Mã nhân viên:"));
        panel.add(txtMaNV);
        panel.add(createFormLabel("Họ:"));
        panel.add(txtHo);
        panel.add(createFormLabel("Tên lót:"));
        panel.add(txtTenLot);
        panel.add(createFormLabel("Tên:"));
        panel.add(txtTen);
        panel.add(createFormLabel("Phái:"));
        panel.add(cboPhai);
        panel.add(createFormLabel("Ngày sinh:"));
        panel.add(txtNgaySinh);
        panel.add(createFormLabel("SĐT:"));
        panel.add(txtSDT);
        panel.add(createFormLabel("Tỉnh thành:"));
        panel.add(cboTinhThanh);
        panel.add(createFormLabel("Địa chỉ:"));
        panel.add(txtDiaChi);
        panel.add(createFormLabel("Lương:"));
        panel.add(txtLuong);
        panel.add(createFormLabel("Chức vụ:"));
        panel.add(cboChucVu);
        panel.add(createFormLabel("Trạng thái:"));
        panel.add(cboTrangThai);
        panel.add(createFormLabel("Mật khẩu:"));
        panel.add(txtMatKhau);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton btnThem = createButton("Thêm", new Color(46, 125, 50));
        JButton btnSua = createButton("Sửa", new Color(21, 101, 192));
        JButton btnXoa = createButton("Xóa", new Color(198, 40, 40));
        JButton btnLamMoi = createButton("Làm mới", new Color(97, 97, 97));

        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnXoa.addActionListener(e -> xoaNhanVien());
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

        long tongLuong = 0;

        for (NhanVien nv : list) {
            model.addRow(new Object[]{
                nv.getMaNV(),
                nv.getHoTenDayDu(),
                nv.getPhai(),
                nv.getNgaySinh(),
                nv.getSdt(),
                getTinhThanhName(nv.getTinhThanh()),
                nv.getDiaChi(),
                formatMoney(nv.getLuong()),
                nv.getChucVu(),
                nv.getTrangThai()
            });

            tongLuong += nv.getLuong();
        }

        lblTongNV.setText("Tổng nhân viên: " + list.size());
        lblTongLuong.setText("Tổng quỹ lương: " + formatMoneyLong(tongLuong));
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

    private String formatMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(money) + " VNĐ";
    }

    private String formatMoneyLong(long money) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(money) + " VNĐ";
    }
}