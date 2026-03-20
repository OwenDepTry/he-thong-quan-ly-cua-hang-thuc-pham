package view;

import dao.DonViDAO;
import dao.LoaiDAO;
import dao.SanPhamDAO;
import entity.DonVi;
import entity.Loai;
import entity.SanPham;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SanPhamPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JTextField txtHSDung;
    private JTextField txtGia;
    private JTextField txtSoLuongTon;
    private JTextArea txtMoTa;

    private JComboBox<Loai> cboLoai;
    private JComboBox<DonVi> cboDonVi;

    private JLabel lblTongSP;
    private JLabel lblTongTon;
    private JLabel lblGiaTriTon;

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final LoaiDAO loaiDAO = new LoaiDAO();
    private final DonViDAO donViDAO = new DonViDAO();

    public SanPhamPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ SẢN PHẨM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý danh mục sản phẩm, giá bán, tồn kho và thông tin mô tả");
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
                new String[]{"Mã SP", "Tên sản phẩm", "Loại", "Đơn vị", "HSD", "Mô tả", "Giá", "SL tồn"},
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
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

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

        lblTongSP = createSummaryLabel("Tổng sản phẩm: 0");
        lblTongTon = createSummaryLabel("Tổng tồn kho: 0");
        lblGiaTriTon = createSummaryLabel("Giá trị tồn: 0 VNĐ");

        panel.add(lblTongSP);
        panel.add(lblTongTon);
        panel.add(lblGiaTriTon);

        return panel;
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        txtHSDung = new JTextField();
        txtGia = new JTextField();
        txtSoLuongTon = new JTextField();
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        cboLoai = new JComboBox<>();
        cboDonVi = new JComboBox<>();

        styleField(txtMaSP);
        styleField(txtTenSP);
        styleField(txtHSDung);
        styleField(txtGia);
        styleField(txtSoLuongTon);
        styleCombo(cboLoai);
        styleCombo(cboDonVi);

        txtMoTa.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        panel.add(createFormLabel("Mã sản phẩm:"));
        panel.add(txtMaSP);
        panel.add(createFormLabel("Tên sản phẩm:"));
        panel.add(txtTenSP);
        panel.add(createFormLabel("Loại:"));
        panel.add(cboLoai);
        panel.add(createFormLabel("Đơn vị tính:"));
        panel.add(cboDonVi);
        panel.add(createFormLabel("Hạn sử dụng:"));
        panel.add(txtHSDung);
        panel.add(createFormLabel("Giá bán:"));
        panel.add(txtGia);
        panel.add(createFormLabel("Số lượng tồn:"));
        panel.add(txtSoLuongTon);
        panel.add(createFormLabel("Mô tả:"));
        panel.add(new JScrollPane(txtMoTa));

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton btnThem = createButton("Thêm", new Color(46, 125, 50));
        JButton btnSua = createButton("Sửa", new Color(21, 101, 192));
        JButton btnXoa = createButton("Xóa", new Color(198, 40, 40));
        JButton btnLamMoi = createButton("Làm mới", new Color(97, 97, 97));

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);

        return panel;
    }

    private void loadComboData() {
        cboLoai.removeAllItems();
        for (Loai loai : loaiDAO.getAll()) {
            cboLoai.addItem(loai);
        }

        cboDonVi.removeAllItems();
        for (DonVi donVi : donViDAO.getAll()) {
            cboDonVi.addItem(donVi);
        }
    }

    private void loadData() {
        fillTable(sanPhamDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(sanPhamDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<SanPham> list) {
        model.setRowCount(0);

        int tongTon = 0;
        long giaTriTon = 0;

        for (SanPham sp : list) {
            model.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                getLoaiName(sp.getLoai()),
                getDonViName(sp.getDonViTinh()),
                sp.getHsDung(),
                sp.getMoTa(),
                formatMoney(sp.getGia()),
                sp.getSoLuongTon()
            });

            tongTon += sp.getSoLuongTon();
            giaTriTon += (long) sp.getGia() * sp.getSoLuongTon();
        }

        lblTongSP.setText("Tổng sản phẩm: " + list.size());
        lblTongTon.setText("Tổng tồn kho: " + tongTon);
        lblGiaTriTon.setText("Giá trị tồn: " + formatMoneyLong(giaTriTon));
    }

    private String getLoaiName(int maLoai) {
        for (int i = 0; i < cboLoai.getItemCount(); i++) {
            Loai loai = cboLoai.getItemAt(i);
            if (loai.getMaLoai() == maLoai) {
                return loai.getTenLoai();
            }
        }
        return String.valueOf(maLoai);
    }

    private String getDonViName(int maDonVi) {
        for (int i = 0; i < cboDonVi.getItemCount(); i++) {
            DonVi dv = cboDonVi.getItemAt(i);
            if (dv.getMaDonVi() == maDonVi) {
                return dv.getTenDonVi();
            }
        }
        return String.valueOf(maDonVi);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String maSP = model.getValueAt(row, 0).toString();
        SanPham sp = sanPhamDAO.findById(maSP);
        if (sp == null) return;

        txtMaSP.setText(sp.getMaSP());
        txtTenSP.setText(sp.getTenSP());
        txtHSDung.setText(String.valueOf(sp.getHsDung()));
        txtMoTa.setText(sp.getMoTa());
        txtGia.setText(String.valueOf(sp.getGia()));
        txtSoLuongTon.setText(String.valueOf(sp.getSoLuongTon()));

        selectLoai(sp.getLoai());
        selectDonVi(sp.getDonViTinh());
        txtMaSP.setEditable(false);
    }

    private void selectLoai(int maLoai) {
        for (int i = 0; i < cboLoai.getItemCount(); i++) {
            if (cboLoai.getItemAt(i).getMaLoai() == maLoai) {
                cboLoai.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectDonVi(int maDonVi) {
        for (int i = 0; i < cboDonVi.getItemCount(); i++) {
            if (cboDonVi.getItemAt(i).getMaDonVi() == maDonVi) {
                cboDonVi.setSelectedIndex(i);
                break;
            }
        }
    }

    private SanPham readForm() {
        String maSP = txtMaSP.getText().trim();
        String tenSP = txtTenSP.getText().trim();
        String moTa = txtMoTa.getText().trim();

        if (maSP.isEmpty() || tenSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm và tên sản phẩm không được để trống.");
            return null;
        }

        Loai loai = (Loai) cboLoai.getSelectedItem();
        DonVi donVi = (DonVi) cboDonVi.getSelectedItem();

        if (loai == null || donVi == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại và đơn vị tính.");
            return null;
        }

        try {
            int hsDung = Integer.parseInt(txtHSDung.getText().trim());
            int gia = Integer.parseInt(txtGia.getText().trim());
            int soLuongTon = Integer.parseInt(txtSoLuongTon.getText().trim());

            if (hsDung < 0 || gia < 0 || soLuongTon < 0) {
                JOptionPane.showMessageDialog(this, "Hạn sử dụng, giá và số lượng tồn phải >= 0.");
                return null;
            }

            return new SanPham(
                    maSP,
                    tenSP,
                    loai.getMaLoai(),
                    donVi.getMaDonVi(),
                    hsDung,
                    moTa,
                    gia,
                    soLuongTon
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Hạn sử dụng, giá và số lượng tồn phải là số.");
            return null;
        }
    }

    private void themSanPham() {
        SanPham sp = readForm();
        if (sp == null) return;

        if (sanPhamDAO.insert(sp)) {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại.");
        }
    }

    private void suaSanPham() {
        SanPham sp = readForm();
        if (sp == null) return;

        if (sanPhamDAO.update(sp)) {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại.");
        }
    }

    private void xoaSanPham() {
        String maSP = txtMaSP.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sản phẩm " + maSP + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (sanPhamDAO.delete(maSP)) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtHSDung.setText("");
        txtMoTa.setText("");
        txtGia.setText("");
        txtSoLuongTon.setText("");
        txtMaSP.setEditable(true);
        table.clearSelection();

        if (cboLoai.getItemCount() > 0) cboLoai.setSelectedIndex(0);
        if (cboDonVi.getItemCount() > 0) cboDonVi.setSelectedIndex(0);
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