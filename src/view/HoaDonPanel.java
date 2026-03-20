package view;

import dao.HoaDonDAO;
import dao.KhachHangSimpleDAO;
import dao.KhuyenMaiDAO;
import dao.NhanVienSimpleDAO;
import dao.SanPhamDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;
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
import service.PdfHoaDonService;

public class HoaDonPanel extends JPanel {

    private JTextField txtMaHD;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;

    private JComboBox<String> cboKhachHang;
    private JComboBox<String> cboNhanVien;
    private JComboBox<String> cboKhuyenMai;

    private JLabel lblTongTienHang;
    private JLabel lblTienGiam;
    private JLabel lblTongThanhToan;

    private JTable tableChiTiet;
    private JTable tableHoaDon;

    private DefaultTableModel modelChiTiet;
    private DefaultTableModel modelHoaDon;

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangSimpleDAO khachHangDAO = new KhachHangSimpleDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final PdfHoaDonService pdfHoaDonService = new PdfHoaDonService();

    private final List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

    public HoaDonPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadComboData();
        loadHoaDonData();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(56, 142, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lập hóa đơn, thêm sản phẩm, lưu hóa đơn và xuất PDF");
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
        scrollChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        JScrollPane scrollHoaDon = new JScrollPane(createHoaDonTable());
        scrollHoaDon.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        scrollHoaDon.setPreferredSize(new Dimension(100, 240));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollChiTiet, scrollHoaDon);
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

        txtMaHD = new JTextField();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();

        cboKhachHang = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        cboKhuyenMai = new JComboBox<>();

        lblTongTienHang = createMoneyLabel();
        lblTienGiam = createMoneyLabel();
        lblTongThanhToan = createMoneyLabel();

        styleField(txtMaHD, fieldFont);
        styleField(txtMaSP, fieldFont);
        styleField(txtSoLuong, fieldFont);
        styleCombo(cboKhachHang, fieldFont);
        styleCombo(cboNhanVien, fieldFont);
        styleCombo(cboKhuyenMai, fieldFont);

        form.add(createLabel("Mã hóa đơn:", labelFont));
        form.add(txtMaHD);
        form.add(createLabel("Khách hàng:", labelFont));
        form.add(cboKhachHang);

        form.add(createLabel("Nhân viên:", labelFont));
        form.add(cboNhanVien);
        form.add(createLabel("Khuyến mãi:", labelFont));
        form.add(cboKhuyenMai);

        form.add(createLabel("Mã sản phẩm:", labelFont));
        form.add(txtMaSP);
        form.add(createLabel("Số lượng:", labelFont));
        form.add(txtSoLuong);

        form.add(createLabel("Tổng tiền hàng:", labelFont));
        form.add(lblTongTienHang);
        form.add(createLabel("Tiền giảm / Thanh toán:", labelFont));

        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        moneyPanel.setOpaque(false);
        moneyPanel.add(lblTienGiam);
        moneyPanel.add(new JLabel("/"));
        moneyPanel.add(lblTongThanhToan);
        form.add(moneyPanel);

        wrap.add(form, BorderLayout.CENTER);

        JPanel note = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        note.setOpaque(false);
        JLabel lbl = new JLabel("Gợi ý: chọn khuyến mãi trước khi lưu để hệ thống tự tính tiền giảm.");
        lbl.setFont(new Font("Arial", Font.ITALIC, 13));
        lbl.setForeground(new Color(90, 90, 90));
        note.add(lbl);
        wrap.add(note, BorderLayout.SOUTH);

        cboKhuyenMai.addActionListener(e -> capNhatTongTien());

        return wrap;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton btnThemSP = createActionButton("Thêm SP", new Color(46, 125, 50));
        JButton btnXoaDong = createActionButton("Xóa dòng", new Color(230, 81, 0));
        JButton btnLuu = createActionButton("Lưu hóa đơn", new Color(27, 94, 32));
        JButton btnXoaHD = createActionButton("Xóa hóa đơn", new Color(198, 40, 40));
        JButton btnXuatPDF = createActionButton("Xuất PDF", new Color(21, 101, 192));
        JButton btnMoi = createActionButton("Làm mới", new Color(97, 97, 97));

        panel.add(btnThemSP);
        panel.add(btnXoaDong);
        panel.add(btnLuu);
        panel.add(btnXoaHD);
        panel.add(btnXuatPDF);
        panel.add(btnMoi);

        btnThemSP.addActionListener(e -> themSanPham());
        btnXoaDong.addActionListener(e -> xoaDong());
        btnLuu.addActionListener(e -> luuHoaDon());
        btnXoaHD.addActionListener(e -> xoaHoaDon());
        btnXuatPDF.addActionListener(e -> xuatPDFHoaDon());
        btnMoi.addActionListener(e -> lamMoiForm());

        return panel;
    }

    private JTable createChiTietTable() {
        modelChiTiet = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0
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

    private JTable createHoaDonTable() {
        modelHoaDon = new DefaultTableModel(
                new String[]{"Mã HD", "Mã KH", "Mã NV", "Mã KM", "Tiền giảm", "Thanh toán", "Thời gian"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHoaDon = new JTable(modelHoaDon);
        configTable(tableHoaDon);
        tableHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableHoaDon.getColumnModel().getColumn(0).setPreferredWidth(90);
        tableHoaDon.getColumnModel().getColumn(1).setPreferredWidth(90);
        tableHoaDon.getColumnModel().getColumn(2).setPreferredWidth(90);
        tableHoaDon.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableHoaDon.getColumnModel().getColumn(4).setPreferredWidth(110);
        tableHoaDon.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableHoaDon.getColumnModel().getColumn(6).setPreferredWidth(180);

        tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiHoaDonDuocChon();
            }
        });

        return tableHoaDon;
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
        cboKhachHang.removeAllItems();
        for (KhachHang kh : khachHangDAO.getAll()) {
            cboKhachHang.addItem(kh.getMaKH() + " - " + kh.getHoTenDayDu());
        }

        cboNhanVien.removeAllItems();
        for (NhanVien nv : nhanVienDAO.getAll()) {
            cboNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTenDayDu());
        }

        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem("Không áp dụng");
        for (KhuyenMai km : khuyenMaiDAO.getAll()) {
            cboKhuyenMai.addItem(km.getMaKM() + " - " + km.getTenKM() + " (" + km.getPhanTramGiam() + "%)");
        }
    }

    private void loadHoaDonData() {
        modelHoaDon.setRowCount(0);

        for (HoaDon hd : hoaDonDAO.getAll()) {
            modelHoaDon.addRow(new Object[]{
                hd.getMaHD(),
                hd.getMaKH(),
                hd.getMaNV(),
                hd.getMaKM() == null ? "" : hd.getMaKM(),
                formatMoney(hd.getTienGiam()),
                formatMoney(hd.getTongTien()),
                hd.getThoiGian()
            });
        }
    }

    private void themSanPham() {
        String maHD = txtMaHD.getText().trim();
        String maSP = txtMaSP.getText().trim();
        String soLuongText = txtSoLuong.getText().trim();

        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn trước.");
            txtMaHD.requestFocus();
            return;
        }

        if (maSP.isEmpty() || soLuongText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm và số lượng.");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongText);
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.");
            return;
        }

        SanPham sp = sanPhamDAO.findById(maSP);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm: " + maSP);
            return;
        }

        if (soLuong > sp.getSoLuongTon()) {
            JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho hiện tại (" + sp.getSoLuongTon() + ").");
            return;
        }

        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct.getMaSP().equalsIgnoreCase(maSP)) {
                int soLuongMoi = ct.getSoLuong() + soLuong;
                if (soLuongMoi > sp.getSoLuongTon()) {
                    JOptionPane.showMessageDialog(this, "Tổng số lượng cho sản phẩm này vượt tồn kho.");
                    return;
                }
                ct.setSoLuong(soLuongMoi);
                refreshChiTietTable();
                capNhatTongTien();
                txtMaSP.setText("");
                txtSoLuong.setText("");
                txtMaSP.requestFocus();
                return;
            }
        }

        ChiTietHoaDon ct = new ChiTietHoaDon(maHD, sp.getMaSP(), soLuong, sp.getGia());
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
        txtMaSP.requestFocus();
    }

    private void xoaDong() {
        int row = tableChiTiet.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng chi tiết cần xóa.");
            return;
        }

        dsChiTiet.remove(row);
        modelChiTiet.removeRow(row);
        capNhatTongTien();
    }

    private void luuHoaDon() {
        String maHD = txtMaHD.getText().trim();

        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn.");
            return;
        }

        if (cboKhachHang.getSelectedItem() == null || cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng và nhân viên.");
            return;
        }

        if (dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hóa đơn chưa có sản phẩm.");
            return;
        }

        if (hoaDonDAO.existsById(maHD)) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn đã tồn tại.");
            return;
        }

        int tongTienHang = tinhTongTienHang();
        int tienGiam = tinhTienGiam(tongTienHang);
        int thanhToan = tongTienHang - tienGiam;

        HoaDon hd = new HoaDon();
        hd.setMaHD(maHD);
        hd.setMaKH(getSelectedCode(cboKhachHang));
        hd.setMaNV(getSelectedCode(cboNhanVien));
        hd.setMaKM(getSelectedKhuyenMaiCode());
        hd.setTienGiam(tienGiam);
        hd.setTongTien(thanhToan);
        hd.setThoiGian(new Timestamp(System.currentTimeMillis()));

        for (ChiTietHoaDon ct : dsChiTiet) {
            ct.setMaHD(maHD);
        }

        boolean ok = hoaDonDAO.insertHoaDon(hd, dsChiTiet);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công.");
            loadHoaDonData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại.");
        }
    }

    private void xoaHoaDon() {
        int row = tableHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa.");
            return;
        }

        String maHD = String.valueOf(modelHoaDon.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa hóa đơn " + maHD + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = hoaDonDAO.xoaHoaDon(maHD);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công.");
                loadHoaDonData();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại.");
            }
        }
    }

    private void xuatPDFHoaDon() {
        HoaDon hd;
        List<ChiTietHoaDon> chiTietXuat;

        int selectedRow = tableHoaDon.getSelectedRow();

        if (selectedRow >= 0) {
            String maHD = String.valueOf(modelHoaDon.getValueAt(selectedRow, 0));
            hd = timHoaDonTheoMa(maHD);
            if (hd == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu hóa đơn để xuất.");
                return;
            }
            chiTietXuat = hoaDonDAO.getChiTietByMaHD(maHD);
        } else {
            if (txtMaHD.getText().trim().isEmpty() || dsChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn đã lưu hoặc tạo hóa đơn có dữ liệu để xuất.");
                return;
            }

            hd = new HoaDon();
            hd.setMaHD(txtMaHD.getText().trim());
            hd.setMaKH(getSelectedCode(cboKhachHang));
            hd.setMaNV(getSelectedCode(cboNhanVien));
            hd.setMaKM(getSelectedKhuyenMaiCode());
            hd.setTienGiam(tinhTienGiam(tinhTongTienHang()));
            hd.setTongTien(tinhTongTienHang() - hd.getTienGiam());
            hd.setThoiGian(new Timestamp(System.currentTimeMillis()));

            chiTietXuat = new ArrayList<>(dsChiTiet);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file PDF");
        chooser.setSelectedFile(new File(hd.getMaHD() + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                pdfHoaDonService.exportHoaDonToPDF(hd, chiTietXuat, filePath);
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Xuất PDF thất bại: " + e.getMessage());
            }
        }
    }

    private void hienThiHoaDonDuocChon() {
        int row = tableHoaDon.getSelectedRow();
        if (row < 0) {
            return;
        }

        String maHD = String.valueOf(modelHoaDon.getValueAt(row, 0));
        HoaDon hd = timHoaDonTheoMa(maHD);
        if (hd == null) {
            return;
        }

        txtMaHD.setText(hd.getMaHD());
        txtMaHD.setEditable(false);

        setComboByCode(cboKhachHang, hd.getMaKH());
        setComboByCode(cboNhanVien, hd.getMaNV());

        if (hd.getMaKM() == null || hd.getMaKM().isBlank()) {
            cboKhuyenMai.setSelectedIndex(0);
        } else {
            setComboByCode(cboKhuyenMai, hd.getMaKM());
        }

        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);

        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietByMaHD(maHD);
        for (ChiTietHoaDon ct : chiTietList) {
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

        lblTongTienHang.setText(formatMoney(tinhTongTienHang()));
        lblTienGiam.setText(formatMoney(hd.getTienGiam()));
        lblTongThanhToan.setText(formatMoney(hd.getTongTien()));
    }

    private void lamMoiForm() {
        txtMaHD.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtMaHD.setEditable(true);

        if (cboKhachHang.getItemCount() > 0) {
            cboKhachHang.setSelectedIndex(0);
        }
        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }
        if (cboKhuyenMai.getItemCount() > 0) {
            cboKhuyenMai.setSelectedIndex(0);
        }

        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        tableHoaDon.clearSelection();

        lblTongTienHang.setText(formatMoney(0));
        lblTienGiam.setText(formatMoney(0));
        lblTongThanhToan.setText(formatMoney(0));
    }

    private void refreshChiTietTable() {
        modelChiTiet.setRowCount(0);
        for (ChiTietHoaDon ct : dsChiTiet) {
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
        int tongTienHang = tinhTongTienHang();
        int tienGiam = tinhTienGiam(tongTienHang);
        int thanhToan = tongTienHang - tienGiam;

        lblTongTienHang.setText(formatMoney(tongTienHang));
        lblTienGiam.setText(formatMoney(tienGiam));
        lblTongThanhToan.setText(formatMoney(thanhToan));
    }

    private int tinhTongTienHang() {
        int tong = 0;
        for (ChiTietHoaDon ct : dsChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    private int tinhTienGiam(int tongTienHang) {
        String maKM = getSelectedKhuyenMaiCode();
        if (maKM == null) {
            return 0;
        }

        for (KhuyenMai km : khuyenMaiDAO.getAll()) {
            if (km.getMaKM().equalsIgnoreCase(maKM)) {
                return tongTienHang * km.getPhanTramGiam() / 100;
            }
        }

        return 0;
    }

    private HoaDon timHoaDonTheoMa(String maHD) {
        for (HoaDon hd : hoaDonDAO.getAll()) {
            if (hd.getMaHD().equalsIgnoreCase(maHD)) {
                return hd;
            }
        }
        return null;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private JLabel createMoneyLabel() {
        JLabel lbl = new JLabel(formatMoney(0));
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        lbl.setForeground(new Color(27, 94, 32));
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

    private String getSelectedKhuyenMaiCode() {
        Object item = cboKhuyenMai.getSelectedItem();
        if (item == null) {
            return null;
        }

        String text = item.toString().trim();
        if (text.equalsIgnoreCase("Không áp dụng")) {
            return null;
        }

        int idx = text.indexOf(" - ");
        return idx >= 0 ? text.substring(0, idx).trim() : text;
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