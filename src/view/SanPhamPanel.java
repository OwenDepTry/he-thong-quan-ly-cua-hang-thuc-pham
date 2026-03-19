package view;

import dao.DonViDAO;
import dao.LoaiDAO;
import dao.SanPhamDAO;
import entity.DonVi;
import entity.Loai;
import entity.SanPham;
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

public class SanPhamPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JTextField txtHSDung;
    private JTextField txtMoTa;
    private JTextField txtGia;
    private JTextField txtSoLuongTon;

    private JComboBox<Loai> cboLoai;
    private JComboBox<DonVi> cboDonVi;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final LoaiDAO loaiDAO = new LoaiDAO();
    private final DonViDAO donViDAO = new DonViDAO();

    public SanPhamPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ SẢN PHẨM", SwingConstants.CENTER);
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
                new String[]{"Mã SP", "Tên sản phẩm", "Loại", "Đơn vị", "HSD", "Mô tả", "Giá", "SL tồn"}, 0
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        txtHSDung = new JTextField();
        txtMoTa = new JTextField();
        txtGia = new JTextField();
        txtSoLuongTon = new JTextField();

        cboLoai = new JComboBox<>();
        cboDonVi = new JComboBox<>();

        addFormRow(formPanel, gbc, 0, "Mã sản phẩm:", txtMaSP);
        addFormRow(formPanel, gbc, 1, "Tên sản phẩm:", txtTenSP);
        addFormRow(formPanel, gbc, 2, "Loại:", cboLoai);
        addFormRow(formPanel, gbc, 3, "Đơn vị tính:", cboDonVi);
        addFormRow(formPanel, gbc, 4, "Hạn sử dụng:", txtHSDung);
        addFormRow(formPanel, gbc, 5, "Mô tả:", txtMoTa);
        addFormRow(formPanel, gbc, 6, "Giá:", txtGia);
        addFormRow(formPanel, gbc, 7, "Số lượng tồn:", txtSoLuongTon);

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

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
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

        for (SanPham sp : list) {
            model.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                getLoaiName(sp.getLoai()),
                getDonViName(sp.getDonViTinh()),
                sp.getHsDung(),
                sp.getMoTa(),
                sp.getGia(),
                sp.getSoLuongTon()
            });
        }
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
        List<SanPham> list = sanPhamDAO.getAll();

        for (SanPham sp : list) {
            if (sp.getMaSP().equals(maSP)) {
                txtMaSP.setText(sp.getMaSP());
                txtTenSP.setText(sp.getTenSP());
                txtHSDung.setText(String.valueOf(sp.getHsDung()));
                txtMoTa.setText(sp.getMoTa());
                txtGia.setText(String.valueOf(sp.getGia()));
                txtSoLuongTon.setText(String.valueOf(sp.getSoLuongTon()));

                selectLoai(sp.getLoai());
                selectDonVi(sp.getDonViTinh());

                txtMaSP.setEditable(false);
                break;
            }
        }
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
}