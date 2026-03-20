package view;

import dao.HangHoaDAO;
import dao.SanPhamDAO;
import entity.HangHoa;
import entity.SanPham;
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

public class HangHoaPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;
    private JTextField txtMaHang;
    private JTextField txtSoLuong;
    private JTextField txtNSX;
    private JTextField txtGiamGia;

    private JComboBox<String> cboMaSP;
    private JComboBox<String> cboTrangThai;

    private JLabel lblTongLo;
    private JLabel lblTongSoLuong;

    private final HangHoaDAO hangHoaDAO = new HangHoaDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public HangHoaPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadSanPham();
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

        JLabel lblTitle = new JLabel("QUẢN LÝ HÀNG HÓA");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Quản lý lô hàng, ngày sản xuất, giảm giá và tình trạng hàng");
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
                new String[]{"Mã hàng", "Mã SP", "Tên sản phẩm", "Số lượng", "NSX", "Giảm giá", "Trạng thái"},
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
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách hàng hóa"));

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

        lblTongLo = createSummaryLabel("Tổng lô hàng: 0");
        lblTongSoLuong = createSummaryLabel("Tổng số lượng: 0");

        panel.add(lblTongLo);
        panel.add(lblTongSoLuong);

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

        txtMaHang = new JTextField();
        txtSoLuong = new JTextField();
        txtNSX = new JTextField();
        txtGiamGia = new JTextField();

        cboMaSP = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

        styleField(txtMaHang);
        styleField(txtSoLuong);
        styleField(txtNSX);
        styleField(txtGiamGia);
        styleCombo(cboMaSP);
        styleCombo(cboTrangThai);

        panel.add(createFormLabel("Mã hàng:"));
        panel.add(txtMaHang);
        panel.add(createFormLabel("Mã sản phẩm:"));
        panel.add(cboMaSP);
        panel.add(createFormLabel("Số lượng:"));
        panel.add(txtSoLuong);
        panel.add(createFormLabel("Ngày sản xuất:"));
        panel.add(txtNSX);
        panel.add(createFormLabel("Giảm giá (%):"));
        panel.add(txtGiamGia);
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

        btnThem.addActionListener(e -> themHangHoa());
        btnSua.addActionListener(e -> suaHangHoa());
        btnXoa.addActionListener(e -> xoaHangHoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);

        return panel;
    }

    private void loadSanPham() {
        cboMaSP.removeAllItems();
        for (SanPham sp : sanPhamDAO.getAll()) {
            cboMaSP.addItem(sp.getMaSP() + " - " + sp.getTenSP());
        }
    }

    private void loadData() {
        fillTable(hangHoaDAO.getAll());
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            fillTable(hangHoaDAO.searchByKeyword(keyword));
        }
    }

    private void fillTable(List<HangHoa> list) {
        model.setRowCount(0);

        int tongSoLuong = 0;

        for (HangHoa h : list) {
            model.addRow(new Object[]{
                h.getMaHang(),
                h.getMaSP(),
                sanPhamDAO.getTenSanPhamByMa(h.getMaSP()),
                h.getSoLuong(),
                h.getNgaySanXuat(),
                h.getGiamGia() + "%",
                h.getTrangThai()
            });
            tongSoLuong += h.getSoLuong();
        }

        lblTongLo.setText("Tổng lô hàng: " + list.size());
        lblTongSoLuong.setText("Tổng số lượng: " + NumberFormat.getInstance(new Locale("vi", "VN")).format(tongSoLuong));
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String maHang = model.getValueAt(row, 0).toString();
        for (HangHoa h : hangHoaDAO.getAll()) {
            if (h.getMaHang().equals(maHang)) {
                txtMaHang.setText(h.getMaHang());
                setSelectedSanPham(h.getMaSP());
                txtSoLuong.setText(String.valueOf(h.getSoLuong()));
                txtNSX.setText(String.valueOf(h.getNgaySanXuat()));
                txtGiamGia.setText(String.valueOf(h.getGiamGia()));
                cboTrangThai.setSelectedItem(h.getTrangThai());
                txtMaHang.setEditable(false);
                break;
            }
        }
    }

    private void setSelectedSanPham(String maSP) {
        for (int i = 0; i < cboMaSP.getItemCount(); i++) {
            String item = cboMaSP.getItemAt(i);
            if (item.startsWith(maSP + " -")) {
                cboMaSP.setSelectedIndex(i);
                break;
            }
        }
    }

    private String getSelectedMaSP() {
        Object item = cboMaSP.getSelectedItem();
        if (item == null) return "";
        String text = item.toString();
        int idx = text.indexOf(" - ");
        return idx >= 0 ? text.substring(0, idx).trim() : text.trim();
    }

    private HangHoa readForm() {
        String maHang = txtMaHang.getText().trim();
        String maSP = getSelectedMaSP();

        if (maHang.isEmpty() || maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hàng và mã sản phẩm không được để trống.");
            return null;
        }

        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            Date nsx = Date.valueOf(txtNSX.getText().trim());
            int giamGia = Integer.parseInt(txtGiamGia.getText().trim());

            if (soLuong < 0 || giamGia < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và giảm giá phải >= 0.");
                return null;
            }

            return new HangHoa(
                    maHang,
                    maSP,
                    soLuong,
                    nsx,
                    giamGia,
                    cboTrangThai.getSelectedItem().toString()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày phải theo dạng yyyy-mm-dd và số lượng/giảm giá phải là số.");
            return null;
        }
    }

    private void themHangHoa() {
        HangHoa h = readForm();
        if (h == null) return;

        if (hangHoaDAO.insert(h)) {
            JOptionPane.showMessageDialog(this, "Thêm hàng hóa thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm hàng hóa thất bại.");
        }
    }

    private void suaHangHoa() {
        HangHoa h = readForm();
        if (h == null) return;

        if (hangHoaDAO.update(h)) {
            JOptionPane.showMessageDialog(this, "Cập nhật hàng hóa thành công.");
            loadData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật hàng hóa thất bại.");
        }
    }

    private void xoaHangHoa() {
        String ma = txtMaHang.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hàng hóa cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa hàng hóa " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (hangHoaDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa hàng hóa thành công.");
                loadData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa hàng hóa thất bại.");
            }
        }
    }

    private void lamMoiForm() {
        txtMaHang.setText("");
        txtSoLuong.setText("");
        txtNSX.setText("");
        txtGiamGia.setText("");
        txtMaHang.setEditable(true);
        table.clearSelection();

        if (cboMaSP.getItemCount() > 0) cboMaSP.setSelectedIndex(0);
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