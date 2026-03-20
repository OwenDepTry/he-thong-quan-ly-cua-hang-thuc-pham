package view;

import dao.KhachHangDAO;
import dao.TinhThanhDAO;
import entity.KhachHang;
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

    private JLabel lblTongKH;
    private JLabel lblTongDiem;

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();

    public KhachHangPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý hồ sơ khách hàng, điểm tích lũy và thông tin tham gia");
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
                new String[]{"Mã KH", "Họ tên", "Phái", "Ngày sinh", "SĐT", "Tỉnh thành", "Địa chỉ", "Ngày tham gia", "Điểm", "Trạng thái"},
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
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(410, 100));
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

        lblTongKH = createSummaryLabel("Tổng khách hàng: 0");
        lblTongDiem = createSummaryLabel("Tổng điểm tích lũy: 0");

        panel.add(lblTongKH);
        panel.add(lblTongDiem);

        return panel;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

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

        styleField(txtMaKH);
        styleField(txtHo);
        styleField(txtTenLot);
        styleField(txtTen);
        styleField(txtNgaySinh);
        styleField(txtSDT);
        styleField(txtDiaChi);
        styleField(txtNgayThamGia);
        styleField(txtDiem);
        styleCombo(cboPhai);
        styleCombo(cboTinhThanh);
        styleCombo(cboTrangThai);

        panel.add(createFormLabel("Mã khách hàng:"));
        panel.add(txtMaKH);
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
        panel.add(createFormLabel("Ngày tham gia:"));
        panel.add(txtNgayThamGia);
        panel.add(createFormLabel("Điểm:"));
        panel.add(txtDiem);
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

        btnThem.addActionListener(e -> themKhachHang());
        btnSua.addActionListener(e -> suaKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
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

        int tongDiem = 0;

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

            tongDiem += kh.getDiem();
        }

        lblTongKH.setText("Tổng khách hàng: " + list.size());
        lblTongDiem.setText("Tổng điểm tích lũy: " + NumberFormat.getInstance(new Locale("vi", "VN")).format(tongDiem));
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