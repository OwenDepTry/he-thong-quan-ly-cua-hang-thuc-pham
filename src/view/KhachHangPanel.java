package view;

import dao.KhachHangDAO;
import dao.TinhThanhDAO;
import entity.KhachHang;
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

public class KhachHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaKH;
    private JTextField txtHo;
    private JTextField txtTenLot;
    private JTextField txtTen;
    private JTextField txtNgaySinh;
    private JTextField txtSDT;
    private JTextField txtDiaChi;
    private JTextField txtNgayThamGia;
    private JTextField txtDiem;

    private JComboBox<String> cboPhai;
    private JComboBox<TinhThanh> cboTinhThanh;
    private JComboBox<String> cboTrangThai;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public KhachHangPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
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
                new String[]{"Mã KH", "Họ tên", "Phái", "Ngày sinh", "SDT", "Tỉnh thành", "Địa chỉ", "Ngày tham gia", "Điểm", "Trạng thái"}, 0
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKH = new JTextField();
        txtHo = new JTextField();
        txtTenLot = new JTextField();
        txtTen = new JTextField();
        txtNgaySinh = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();
        txtNgayThamGia = new JTextField();
        txtDiem = new JTextField();

        cboPhai = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboTinhThanh = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        addFormRow(formPanel, gbc, 0, "Mã khách hàng:", txtMaKH);
        addFormRow(formPanel, gbc, 1, "Họ:", txtHo);
        addFormRow(formPanel, gbc, 2, "Tên lót:", txtTenLot);
        addFormRow(formPanel, gbc, 3, "Tên:", txtTen);
        addFormRow(formPanel, gbc, 4, "Phái:", cboPhai);
        addFormRow(formPanel, gbc, 5, "Ngày sinh (yyyy-mm-dd):", txtNgaySinh);
        addFormRow(formPanel, gbc, 6, "SĐT:", txtSDT);
        addFormRow(formPanel, gbc, 7, "Tỉnh thành:", cboTinhThanh);
        addFormRow(formPanel, gbc, 8, "Địa chỉ:", txtDiaChi);
        addFormRow(formPanel, gbc, 9, "Ngày tham gia (yyyy-mm-dd):", txtNgayThamGia);
        addFormRow(formPanel, gbc, 10, "Điểm:", txtDiem);
        addFormRow(formPanel, gbc, 11, "Trạng thái:", cboTrangThai);

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

        btnThem.addActionListener(e -> themKhachHang());
        btnSua.addActionListener(e -> suaKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
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
        fillTable(khachHangDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(khachHangDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<KhachHang> list) {
        model.setRowCount(0);

        for (KhachHang kh : list) {
            model.addRow(new Object[]{
                kh.getMaKH(),
                kh.getHoTenDayDu(),
                kh.getPhai(),
                kh.getNgaySinh(),
                kh.getSdt(),
                getTinhThanhName(kh.getTinhThanh()),
                kh.getDiaChi(),
                kh.getNgayThamGia(),
                kh.getDiem(),
                kh.getTrangThai()
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

        String maKH = model.getValueAt(row, 0).toString();
        List<KhachHang> list = khachHangDAO.getAll();

        for (KhachHang kh : list) {
            if (kh.getMaKH().equals(maKH)) {
                txtMaKH.setText(kh.getMaKH());
                txtHo.setText(kh.getHo());
                txtTenLot.setText(kh.getTenLot());
                txtTen.setText(kh.getTen());
                cboPhai.setSelectedItem(kh.getPhai());
                txtNgaySinh.setText(String.valueOf(kh.getNgaySinh()));
                txtSDT.setText(kh.getSdt());
                txtDiaChi.setText(kh.getDiaChi());
                txtNgayThamGia.setText(String.valueOf(kh.getNgayThamGia()));
                txtDiem.setText(String.valueOf(kh.getDiem()));
                cboTrangThai.setSelectedItem(kh.getTrangThai());
                selectTinhThanh(kh.getTinhThanh());

                txtMaKH.setEditable(false);
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

    private KhachHang readForm() {
        String maKH = txtMaKH.getText().trim();
        String ho = txtHo.getText().trim();
        String tenLot = txtTenLot.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maKH.isEmpty() || ho.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã KH, họ, tên, SĐT không được để trống.");
            return null;
        }

        TinhThanh tt = (TinhThanh) cboTinhThanh.getSelectedItem();
        if (tt == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tỉnh thành.");
            return null;
        }

        try {
            Date ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
            Date ngayThamGia = Date.valueOf(txtNgayThamGia.getText().trim());
            int diem = Integer.parseInt(txtDiem.getText().trim());

            if (diem < 0) {
                JOptionPane.showMessageDialog(this, "Điểm phải >= 0.");
                return null;
            }

            return new KhachHang(
                    maKH,
                    ho,
                    tenLot,
                    ten,
                    cboPhai.getSelectedItem().toString(),
                    ngaySinh,
                    sdt,
                    tt.getMaTThanh(),
                    diaChi,
                    ngayThamGia,
                    diem,
                    cboTrangThai.getSelectedItem().toString()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày phải đúng định dạng yyyy-mm-dd và điểm phải là số.");
            return null;
        }
    }

    private void themKhachHang() {
        KhachHang kh = readForm();
        if (kh == null) return;

        if (khachHangDAO.insert(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại.");
        }
    }

    private void suaKhachHang() {
        KhachHang kh = readForm();
        if (kh == null) return;

        if (khachHangDAO.update(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại.");
        }
    }

    private void xoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khách hàng " + maKH + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDAO.delete(maKH)) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaKH.setText("");
        txtHo.setText("");
        txtTenLot.setText("");
        txtTen.setText("");
        txtNgaySinh.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtNgayThamGia.setText("");
        txtDiem.setText("");
        txtMaKH.setEditable(true);
        table.clearSelection();

        cboPhai.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        if (cboTinhThanh.getItemCount() > 0) cboTinhThanh.setSelectedIndex(0);
    }
}