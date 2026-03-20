package view;

import dao.NhaCungCapDAO;
import dao.NhanVienSimpleDAO;
import dao.PhieuNhapDAO;
import dao.SanPhamDAO;
import entity.ChiTietPNhap;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhapHang;
import entity.SanPham;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import service.PdfPhieuNhapService;

public class PhieuNhapPanel extends JPanel {

    private JTextField txtMaPhieu;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;

    private JComboBox<String> cboNhaCungCap;
    private JComboBox<String> cboNhanVien;

    private JLabel lblTongTien;

    private JTable tableChiTiet;
    private JTable tablePhieuNhap;

    private DefaultTableModel modelChiTiet;
    private DefaultTableModel modelPhieuNhap;

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final PdfPhieuNhapService pdfPhieuNhapService = new PdfPhieuNhapService();

    private final List<ChiTietPNhap> dsChiTiet = new ArrayList<>();

    public PhieuNhapPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadComboData();
        loadPhieuNhapData();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 125, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU NHẬP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lập phiếu nhập hàng, cập nhật số lượng tồn và xuất PDF");
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

        JPanel topArea = new JPanel(new BorderLayout(10, 10));
        topArea.setOpaque(false);
        topArea.add(createFormPanel(), BorderLayout.CENTER);
        topArea.add(createButtonPanel(), BorderLayout.SOUTH);

        JScrollPane scrollChiTiet = new JScrollPane(createChiTietTable());
        scrollChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập"));

        JScrollPane scrollPhieuNhap = new JScrollPane(createPhieuNhapTable());
        scrollPhieuNhap.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));
        scrollPhieuNhap.setPreferredSize(new Dimension(100, 240));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollChiTiet, scrollPhieuNhap);
        splitPane.setResizeWeight(0.52);
        splitPane.setDividerSize(8);

        main.add(topArea, BorderLayout.NORTH);
        main.add(splitPane, BorderLayout.CENTER);

        return main;
    }

    private JPanel createFormPanel() {
        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setOpaque(false);

        JPanel form = new JPanel(new GridLayout(4, 4, 12, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font fieldFont = new Font("Arial", Font.PLAIN, 15);

        txtMaPhieu = new JTextField();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        txtDonGia = new JTextField();

        cboNhaCungCap = new JComboBox<>();
        cboNhanVien = new JComboBox<>();

        lblTongTien = new JLabel(formatMoney(0));
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongTien.setForeground(new Color(27, 94, 32));

        styleField(txtMaPhieu, fieldFont);
        styleField(txtMaSP, fieldFont);
        styleField(txtSoLuong, fieldFont);
        styleField(txtDonGia, fieldFont);
        styleCombo(cboNhaCungCap, fieldFont);
        styleCombo(cboNhanVien, fieldFont);

        form.add(createLabel("Mã phiếu:", labelFont));
        form.add(txtMaPhieu);
        form.add(createLabel("Nhà cung cấp:", labelFont));
        form.add(cboNhaCungCap);

        form.add(createLabel("Người nhập:", labelFont));
        form.add(cboNhanVien);
        form.add(createLabel("Mã sản phẩm:", labelFont));
        form.add(txtMaSP);

        form.add(createLabel("Số lượng:", labelFont));
        form.add(txtSoLuong);
        form.add(createLabel("Đơn giá nhập:", labelFont));
        form.add(txtDonGia);

        form.add(createLabel("Tổng tiền:", labelFont));
        form.add(lblTongTien);
        form.add(new JLabel());
        form.add(new JLabel());

        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton btnThemSP = createActionButton("Thêm SP", new Color(46, 125, 50));
        JButton btnXoaDong = createActionButton("Xóa dòng", new Color(230, 81, 0));
        JButton btnLuu = createActionButton("Lưu phiếu", new Color(27, 94, 32));
        JButton btnXoa = createActionButton("Xóa phiếu", new Color(198, 40, 40));
        JButton btnXuatPDF = createActionButton("Xuất PDF", new Color(21, 101, 192));
        JButton btnMoi = createActionButton("Làm mới", new Color(97, 97, 97));

        panel.add(btnThemSP);
        panel.add(btnXoaDong);
        panel.add(btnLuu);
        panel.add(btnXoa);
        panel.add(btnXuatPDF);
        panel.add(btnMoi);

        btnThemSP.addActionListener(e -> themSanPhamVaoPhieuNhap());
        btnXoaDong.addActionListener(e -> xoaDongChiTiet());
        btnLuu.addActionListener(e -> luuPhieuNhap());
        btnXoa.addActionListener(e -> xoaPhieuNhap());
        btnXuatPDF.addActionListener(e -> xuatPDFPhieuNhap());
        btnMoi.addActionListener(e -> lamMoiForm());

        return panel;
    }

    private JTable createChiTietTable() {
        modelChiTiet = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá nhập", "Thành tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableChiTiet = new JTable(modelChiTiet);
        configTable(tableChiTiet);

        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(90);
        tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(220);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(90);
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(140);

        return tableChiTiet;
    }

    private JTable createPhieuNhapTable() {
        modelPhieuNhap = new DefaultTableModel(
                new String[]{"Mã phiếu", "Mã NCC", "Người nhập", "Tổng tiền", "Thời gian"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePhieuNhap = new JTable(modelPhieuNhap);
        configTable(tablePhieuNhap);
        tablePhieuNhap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablePhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablePhieuNhap.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablePhieuNhap.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablePhieuNhap.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablePhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(180);

        tablePhieuNhap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiPhieuNhapDuocChon();
            }
        });

        return tablePhieuNhap;
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

    private void loadComboData() {
        cboNhaCungCap.removeAllItems();
        for (NhaCungCap ncc : nhaCungCapDAO.getAll()) {
            cboNhaCungCap.addItem(ncc.getMaNCCap() + " - " + ncc.getTenNCCap());
        }

        cboNhanVien.removeAllItems();
        for (NhanVien nv : nhanVienDAO.getAll()) {
            cboNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTenDayDu());
        }
    }

    private void loadPhieuNhapData() {
        modelPhieuNhap.setRowCount(0);

        for (PhieuNhapHang pn : phieuNhapDAO.getAll()) {
            modelPhieuNhap.addRow(new Object[]{
                pn.getMaPhieu(),
                pn.getMaNCCap(),
                pn.getNguoiNhap(),
                formatMoney(pn.getTongTien()),
                pn.getThoiGian()
            });
        }
    }

    private void themSanPhamVaoPhieuNhap() {
        String maPhieu = txtMaPhieu.getText().trim();
        String maSP = txtMaSP.getText().trim();
        String soLuongText = txtSoLuong.getText().trim();
        String donGiaText = txtDonGia.getText().trim();

        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu trước.");
            txtMaPhieu.requestFocus();
            return;
        }

        if (maSP.isEmpty() || soLuongText.isEmpty() || donGiaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm, số lượng và đơn giá.");
            return;
        }

        SanPham sp = sanPhamDAO.findById(maSP);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm: " + maSP);
            return;
        }

        int soLuong;
        int donGia;
        try {
            soLuong = Integer.parseInt(soLuongText);
            donGia = Integer.parseInt(donGiaText);

            if (soLuong <= 0 || donGia <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải lớn hơn 0.");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng hoặc đơn giá không hợp lệ.");
            return;
        }

        for (ChiTietPNhap ct : dsChiTiet) {
            if (ct.getMaSP().equalsIgnoreCase(maSP)) {
                ct.setSoLuong(ct.getSoLuong() + soLuong);
                ct.setDonGia(donGia);
                refreshChiTietTable();
                capNhatTongTien();
                txtMaSP.setText("");
                txtSoLuong.setText("");
                txtDonGia.setText("");
                txtMaSP.requestFocus();
                return;
            }
        }

        ChiTietPNhap ct = new ChiTietPNhap(maPhieu, maSP, soLuong, donGia);
        dsChiTiet.add(ct);
        modelChiTiet.addRow(new Object[]{
            ct.getMaSP(),
            sp.getTenSP(),
            ct.getSoLuong(),
            formatMoney(ct.getDonGia()),
            formatMoney(ct.getThanhTien())
        });

        capNhatTongTien();
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtMaSP.requestFocus();
    }

    private void xoaDongChiTiet() {
        int row = tableChiTiet.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng chi tiết cần xóa.");
            return;
        }

        dsChiTiet.remove(row);
        modelChiTiet.removeRow(row);
        capNhatTongTien();
    }

    private void luuPhieuNhap() {
        String maPhieu = txtMaPhieu.getText().trim();

        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu.");
            return;
        }

        if (cboNhaCungCap.getSelectedItem() == null || cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp và người nhập.");
            return;
        }

        if (dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm.");
            return;
        }

        if (phieuNhapDAO.existsById(maPhieu)) {
            JOptionPane.showMessageDialog(this, "Mã phiếu đã tồn tại.");
            return;
        }

        int tongTien = tinhTongTien();

        PhieuNhapHang pn = new PhieuNhapHang();
        pn.setMaPhieu(maPhieu);
        pn.setMaNCCap(getSelectedCode(cboNhaCungCap));
        pn.setNguoiNhap(getSelectedCode(cboNhanVien));
        pn.setTongTien(tongTien);
        pn.setThoiGian(new Timestamp(System.currentTimeMillis()));

        for (ChiTietPNhap ct : dsChiTiet) {
            ct.setMaPhieu(maPhieu);
        }

        boolean ok = phieuNhapDAO.insertPhieuNhap(pn, dsChiTiet);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thành công.");
            loadPhieuNhapData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thất bại.");
        }
    }

    private void xoaPhieuNhap() {
        int row = tablePhieuNhap.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xóa.");
            return;
        }

        String maPhieu = String.valueOf(modelPhieuNhap.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa phiếu nhập " + maPhieu + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = phieuNhapDAO.xoaPhieuNhap(maPhieu);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thành công.");
                loadPhieuNhapData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thất bại.");
            }
        }
    }

    private void xuatPDFPhieuNhap() {
        PhieuNhapHang pn;
        List<ChiTietPNhap> chiTietXuat;

        int selectedRow = tablePhieuNhap.getSelectedRow();

        if (selectedRow >= 0) {
            String maPhieu = String.valueOf(modelPhieuNhap.getValueAt(selectedRow, 0));
            pn = timPhieuNhapTheoMa(maPhieu);
            if (pn == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phiếu nhập để xuất.");
                return;
            }
            chiTietXuat = phieuNhapDAO.getChiTietByMaPhieu(maPhieu);
        } else {
            if (txtMaPhieu.getText().trim().isEmpty() || dsChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu đã lưu hoặc tạo phiếu có dữ liệu để xuất.");
                return;
            }

            pn = new PhieuNhapHang();
            pn.setMaPhieu(txtMaPhieu.getText().trim());
            pn.setMaNCCap(getSelectedCode(cboNhaCungCap));
            pn.setNguoiNhap(getSelectedCode(cboNhanVien));
            pn.setTongTien(tinhTongTien());
            pn.setThoiGian(new Timestamp(System.currentTimeMillis()));

            chiTietXuat = new ArrayList<>(dsChiTiet);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file PDF");
        chooser.setSelectedFile(new File(pn.getMaPhieu() + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                pdfPhieuNhapService.exportPhieuNhapToPDF(pn, chiTietXuat, filePath);
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Xuất PDF thất bại: " + e.getMessage());
            }
        }
    }

    private void hienThiPhieuNhapDuocChon() {
        int row = tablePhieuNhap.getSelectedRow();
        if (row < 0) {
            return;
        }

        String maPhieu = String.valueOf(modelPhieuNhap.getValueAt(row, 0));
        PhieuNhapHang pn = timPhieuNhapTheoMa(maPhieu);
        if (pn == null) {
            return;
        }

        txtMaPhieu.setText(pn.getMaPhieu());
        txtMaPhieu.setEditable(false);

        setComboByCode(cboNhaCungCap, pn.getMaNCCap());
        setComboByCode(cboNhanVien, pn.getNguoiNhap());

        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);

        List<ChiTietPNhap> list = phieuNhapDAO.getChiTietByMaPhieu(maPhieu);
        for (ChiTietPNhap ct : list) {
            dsChiTiet.add(ct);
            String tenSP = sanPhamDAO.getTenSanPhamByMa(ct.getMaSP());
            modelChiTiet.addRow(new Object[]{
                ct.getMaSP(),
                tenSP,
                ct.getSoLuong(),
                formatMoney(ct.getDonGia()),
                formatMoney(ct.getThanhTien())
            });
        }

        lblTongTien.setText(formatMoney(pn.getTongTien()));
    }

    private void lamMoiForm() {
        txtMaPhieu.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtMaPhieu.setEditable(true);

        if (cboNhaCungCap.getItemCount() > 0) {
            cboNhaCungCap.setSelectedIndex(0);
        }
        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }

        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        tablePhieuNhap.clearSelection();
        lblTongTien.setText(formatMoney(0));
    }

    private void refreshChiTietTable() {
        modelChiTiet.setRowCount(0);
        for (ChiTietPNhap ct : dsChiTiet) {
            String tenSP = sanPhamDAO.getTenSanPhamByMa(ct.getMaSP());
            modelChiTiet.addRow(new Object[]{
                ct.getMaSP(),
                tenSP,
                ct.getSoLuong(),
                formatMoney(ct.getDonGia()),
                formatMoney(ct.getThanhTien())
            });
        }
    }

    private void capNhatTongTien() {
        lblTongTien.setText(formatMoney(tinhTongTien()));
    }

    private int tinhTongTien() {
        int tong = 0;
        for (ChiTietPNhap ct : dsChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    private PhieuNhapHang timPhieuNhapTheoMa(String maPhieu) {
        for (PhieuNhapHang pn : phieuNhapDAO.getAll()) {
            if (pn.getMaPhieu().equalsIgnoreCase(maPhieu)) {
                return pn;
            }
        }
        return null;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private void styleField(JTextField field, Font font) {
        field.setFont(font);
        field.setPreferredSize(new Dimension(240, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    private void styleCombo(JComboBox<String> combo, Font font) {
        combo.setFont(font);
        combo.setPreferredSize(new Dimension(240, 34));
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(125, 38));
        return btn;
    }

    private String getSelectedCode(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();
        if (item == null) {
            return "";
        }
        String text = item.toString();
        int idx = text.indexOf(" - ");
        return idx >= 0 ? text.substring(0, idx).trim() : text.trim();
    }

    private void setComboByCode(JComboBox<String> combo, String code) {
        if (code == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item != null && item.startsWith(code + " -")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String formatMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(money) + " VNĐ";
    }
}