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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import service.PdfHoaDonService;

public class HoaDonPanel extends JPanel {

    private JTextField txtMaHD, txtMaSP, txtSoLuong;
    private JComboBox<String> cboKhachHang, cboNhanVien, cboKhuyenMai;
    private JLabel lblTongTienHang, lblTienGiam, lblTongThanhToan;

    private JTable tableChiTiet, tableHoaDon;
    private DefaultTableModel modelChiTiet, modelHoaDon;

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangSimpleDAO khachHangDAO = new KhachHangSimpleDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private final List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
    private final PdfHoaDonService pdfHoaDonService = new PdfHoaDonService();

    public HoaDonPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadHoaDonData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(100, 55));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topForm = new JPanel(new GridLayout(4, 4, 10, 10));
        topForm.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        txtMaHD = new JTextField();
        cboKhachHang = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        cboKhuyenMai = new JComboBox<>();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        lblTongTienHang = new JLabel("0");
        lblTienGiam = new JLabel("0");
        lblTongThanhToan = new JLabel("0");

        topForm.add(new JLabel("Mã hóa đơn:"));
        topForm.add(txtMaHD);
        topForm.add(new JLabel("Khách hàng:"));
        topForm.add(cboKhachHang);

        topForm.add(new JLabel("Nhân viên:"));
        topForm.add(cboNhanVien);
        topForm.add(new JLabel("Khuyến mãi:"));
        topForm.add(cboKhuyenMai);

        topForm.add(new JLabel("Mã sản phẩm:"));
        topForm.add(txtMaSP);
        topForm.add(new JLabel("Số lượng:"));
        topForm.add(txtSoLuong);

        topForm.add(new JLabel("Tổng tiền hàng:"));
        topForm.add(lblTongTienHang);
        topForm.add(new JLabel("Tiền giảm / Tổng thanh toán:"));
        topForm.add(new JLabel());

        JPanel tongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tongPanel.add(new JLabel("Tiền giảm:"));
        tongPanel.add(lblTienGiam);
        tongPanel.add(new JLabel("Tổng thanh toán:"));
        tongPanel.add(lblTongThanhToan);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnThemSP = new JButton("Thêm SP");
        JButton btnXoaDong = new JButton("Xóa dòng");
        JButton btnXoaHoaDon = new JButton("Xóa hóa đơn");
        JButton btnLuu = new JButton("Lưu");
        JButton btnXuatPDF = new JButton("Xuất PDF");
        JButton btnMoi = new JButton("Làm mới");

        buttonPanel.add(btnThemSP);
        buttonPanel.add(btnXoaDong);
        buttonPanel.add(btnXoaHoaDon);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnXuatPDF);
        buttonPanel.add(btnMoi);

        modelChiTiet = new DefaultTableModel(new String[]{"Mã SP", "Tên", "SL", "Giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChiTiet = new JTable(modelChiTiet);

        modelHoaDon = new DefaultTableModel(new String[]{"Mã HD", "KH", "NV", "KM", "Tiền giảm", "Tổng tiền", "Thời gian"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableHoaDon = new JTable(modelHoaDon);

        JPanel centerTop = new JPanel(new BorderLayout(10, 10));
        centerTop.add(topForm, BorderLayout.NORTH);
        centerTop.add(tongPanel, BorderLayout.CENTER);
        centerTop.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(centerTop, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tableChiTiet), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(new JScrollPane(tableHoaDon), BorderLayout.SOUTH);

        btnThemSP.addActionListener(e -> themSanPham());
        btnXoaDong.addActionListener(e -> xoaDong());
        btnXoaHoaDon.addActionListener(e -> xoaHoaDon());
        btnLuu.addActionListener(e -> luuHoaDon());
        btnXuatPDF.addActionListener(e -> xuatPDFHoaDon());
        btnMoi.addActionListener(e -> lamMoiForm());
        cboKhuyenMai.addActionListener(e -> capNhatTongTien());
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
        cboKhuyenMai.addItem("NONE - Không áp dụng");
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
                hd.getTienGiam(),
                hd.getTongTien(),
                hd.getThoiGian()
            });
        }
    }

    private void themSanPham() {
        try {
            String maSP = txtMaSP.getText().trim();
            String soLuongText = txtSoLuong.getText().trim();

            if (maSP.isEmpty() || soLuongText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm và số lượng.");
                return;
            }

            int sl = Integer.parseInt(soLuongText);

            if (sl <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.");
                return;
            }

            SanPham sp = sanPhamDAO.findById(maSP);
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm.");
                return;
            }

            for (int i = 0; i < dsChiTiet.size(); i++) {
                ChiTietHoaDon ct = dsChiTiet.get(i);
                if (ct.getMaSP().equalsIgnoreCase(maSP)) {
                    int tong = ct.getSoLuong() + sl;
                    if (tong > sp.getSoLuongTon()) {
                        JOptionPane.showMessageDialog(this, "Vượt tồn kho.");
                        return;
                    }

                    ct.setSoLuong(tong);
                    modelChiTiet.setValueAt(tong, i, 2);
                    modelChiTiet.setValueAt(ct.getThanhTien(), i, 4);
                    capNhatTongTien();
                    txtMaSP.setText("");
                    txtSoLuong.setText("");
                    return;
                }
            }

            if (sl > sp.getSoLuongTon()) {
                JOptionPane.showMessageDialog(this, "Vượt tồn kho.");
                return;
            }

            ChiTietHoaDon ct = new ChiTietHoaDon("", maSP, sl, sp.getGia());
            dsChiTiet.add(ct);
            modelChiTiet.addRow(new Object[]{maSP, sp.getTenSP(), sl, sp.getGia(), ct.getThanhTien()});

            capNhatTongTien();
            txtMaSP.setText("");
            txtSoLuong.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu sản phẩm không hợp lệ.");
        }
    }

    private void xoaDong() {
        int row = tableChiTiet.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng chi tiết cần xóa.");
            return;
        }

        dsChiTiet.remove(row);
        modelChiTiet.removeRow(row);
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        int tongHang = 0;
        for (ChiTietHoaDon ct : dsChiTiet) {
            tongHang += ct.getThanhTien();
        }

        int phanTramGiam = layPhanTramGiamDangChon();
        int tienGiam = tongHang * phanTramGiam / 100;
        int thanhToan = tongHang - tienGiam;

        lblTongTienHang.setText(String.valueOf(tongHang));
        lblTienGiam.setText(String.valueOf(tienGiam));
        lblTongThanhToan.setText(String.valueOf(thanhToan));
    }

    private int layPhanTramGiamDangChon() {
        Object selected = cboKhuyenMai.getSelectedItem();
        if (selected == null) {
            return 0;
        }

        String text = selected.toString();
        int start = text.lastIndexOf('(');
        int end = text.lastIndexOf('%');

        if (start >= 0 && end > start) {
            try {
                return Integer.parseInt(text.substring(start + 1, end).trim());
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    private String getMa(String s) {
        return s.split(" - ")[0].trim();
    }

    private void luuHoaDon() {
        String maHD = txtMaHD.getText().trim();
        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã hóa đơn.");
            return;
        }

        if (hoaDonDAO.existsById(maHD)) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn đã tồn tại.");
            return;
        }

        if (dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm trong hóa đơn.");
            return;
        }

        String maKH = getMa(cboKhachHang.getSelectedItem().toString());
        String maNV = getMa(cboNhanVien.getSelectedItem().toString());
        String maKM = getMa(cboKhuyenMai.getSelectedItem().toString());
        if ("NONE".equalsIgnoreCase(maKM)) {
            maKM = null;
        }

        int tongTien = Integer.parseInt(lblTongThanhToan.getText());
        int tienGiam = Integer.parseInt(lblTienGiam.getText());

        HoaDon hd = new HoaDon(
                maHD,
                maKH,
                maNV,
                tongTien,
                tienGiam,
                maKM,
                new java.sql.Timestamp(System.currentTimeMillis())
        );

        for (ChiTietHoaDon ct : dsChiTiet) {
            ct.setMaHD(maHD);
        }

        if (hoaDonDAO.insertHoaDon(hd, dsChiTiet)) {
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công.");
            loadHoaDonData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại.");
        }
    }

    private void xoaHoaDon() {
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
            return;
        }

        String maHD = tableHoaDon.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa hóa đơn " + maHD + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = hoaDonDAO.xoaHoaDon(maHD);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
            loadHoaDonData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại!");
        }
    }

    private void lamMoiForm() {
        txtMaHD.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        if (cboKhuyenMai.getItemCount() > 0) {
            cboKhuyenMai.setSelectedIndex(0);
        }
        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        lblTongTienHang.setText("0");
        lblTienGiam.setText("0");
        lblTongThanhToan.setText("0");
    }

    private void xuatPDFHoaDon() {
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn trong bảng để xuất PDF.");
            return;
        }

        try {
            String maHD = modelHoaDon.getValueAt(row, 0).toString();
            String maKH = modelHoaDon.getValueAt(row, 1).toString();
            String maNV = modelHoaDon.getValueAt(row, 2).toString();
            String maKM = modelHoaDon.getValueAt(row, 3).toString();
            int tienGiam = Integer.parseInt(modelHoaDon.getValueAt(row, 4).toString());
            int tongTien = Integer.parseInt(modelHoaDon.getValueAt(row, 5).toString());
            java.sql.Timestamp thoiGian = java.sql.Timestamp.valueOf(modelHoaDon.getValueAt(row, 6).toString());

            HoaDon hoaDon = new HoaDon(maHD, maKH, maNV, tongTien, tienGiam, maKM.isBlank() ? null : maKM, thoiGian);
            List<ChiTietHoaDon> ds = hoaDonDAO.getChiTietByMaHD(maHD);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
            fileChooser.setSelectedFile(new File("HoaDon_" + maHD + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                pdfHoaDonService.exportHoaDonToPDF(hoaDon, ds, filePath);
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Xuất PDF thất bại.");
        }
    }
}