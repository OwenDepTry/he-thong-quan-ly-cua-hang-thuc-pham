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
import service.PdfPhieuNhapService;

public class PhieuNhapPanel extends JPanel {

    private JTextField txtMaPhieu;
    private JComboBox<String> cboNhaCungCap;
    private JComboBox<String> cboNhanVien;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JLabel lblTongTien;

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;

    private JTable tablePhieuNhap;
    private DefaultTableModel modelPhieuNhap;

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final PdfPhieuNhapService pdfPhieuNhapService = new PdfPhieuNhapService();

    private final List<ChiTietPNhap> dsChiTiet = new ArrayList<>();

    public PhieuNhapPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadComboData();
        loadPhieuNhapData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(100, 55));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topForm = new JPanel(new GridLayout(3, 4, 10, 10));
        topForm.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập"));

        txtMaPhieu = new JTextField();
        cboNhaCungCap = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        txtDonGia = new JTextField();
        lblTongTien = new JLabel("0");

        topForm.add(new JLabel("Mã phiếu:"));
        topForm.add(txtMaPhieu);
        topForm.add(new JLabel("Nhà cung cấp:"));
        topForm.add(cboNhaCungCap);

        topForm.add(new JLabel("Người nhập:"));
        topForm.add(cboNhanVien);
        topForm.add(new JLabel("Mã sản phẩm:"));
        topForm.add(txtMaSP);

        topForm.add(new JLabel("Số lượng:"));
        topForm.add(txtSoLuong);
        topForm.add(new JLabel("Đơn giá nhập:"));
        topForm.add(txtDonGia);

        JPanel infoBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoBottom.add(new JLabel("Tổng tiền:"));
        infoBottom.add(lblTongTien);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnThemSP = new JButton("Thêm sản phẩm");
        JButton btnXoaDong = new JButton("Xóa dòng");
        JButton btnXoaPhieu = new JButton("Xóa phiếu nhập");
        JButton btnLuuPN = new JButton("Lưu phiếu nhập");
        JButton btnXuatPDF = new JButton("Xuất PDF");
        JButton btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThemSP);
        buttonPanel.add(btnXoaDong);
        buttonPanel.add(btnXoaPhieu);
        buttonPanel.add(btnLuuPN);
        buttonPanel.add(btnXuatPDF);
        buttonPanel.add(btnLamMoi);

        modelChiTiet = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableChiTiet = new JTable(modelChiTiet);
        JScrollPane scrollChiTiet = new JScrollPane(tableChiTiet);
        scrollChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập"));

        modelPhieuNhap = new DefaultTableModel(
                new String[]{"Mã phiếu", "Mã NCC", "Người nhập", "Tổng tiền", "Thời gian"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePhieuNhap = new JTable(modelPhieuNhap);
        JScrollPane scrollPhieuNhap = new JScrollPane(tablePhieuNhap);
        scrollPhieuNhap.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(scrollChiTiet, BorderLayout.CENTER);
        centerPanel.add(scrollPhieuNhap, BorderLayout.SOUTH);

        JPanel upper = new JPanel(new BorderLayout(10, 10));
        upper.add(topForm, BorderLayout.NORTH);
        upper.add(infoBottom, BorderLayout.CENTER);
        upper.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(upper, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        btnThemSP.addActionListener(e -> themSanPhamVaoPhieuNhap());
        btnXoaDong.addActionListener(e -> xoaDongChiTiet());
        btnXoaPhieu.addActionListener(e -> xoaPhieuNhap());
        btnLuuPN.addActionListener(e -> luuPhieuNhap());
        btnXuatPDF.addActionListener(e -> xuatPDFPhieuNhap());
        btnLamMoi.addActionListener(e -> lamMoiForm());
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
                pn.getTongTien(),
                pn.getThoiGian()
            });
        }
    }

    private void themSanPhamVaoPhieuNhap() {
        String maSP = txtMaSP.getText().trim();
        String soLuongText = txtSoLuong.getText().trim();
        String donGiaText = txtDonGia.getText().trim();

        if (maSP.isEmpty() || soLuongText.isEmpty() || donGiaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm, số lượng và đơn giá.");
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongText);
            int donGia = Integer.parseInt(donGiaText);

            if (soLuong <= 0 || donGia <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải > 0.");
                return;
            }

            SanPham sp = sanPhamDAO.findById(maSP);
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm.");
                return;
            }

            for (int i = 0; i < dsChiTiet.size(); i++) {
                ChiTietPNhap ct = dsChiTiet.get(i);
                if (ct.getMaSP().equalsIgnoreCase(maSP)) {
                    int soLuongMoi = ct.getSoLuong() + soLuong;
                    ct.setSoLuong(soLuongMoi);
                    ct.setDonGia(donGia);

                    modelChiTiet.setValueAt(soLuongMoi, i, 2);
                    modelChiTiet.setValueAt(donGia, i, 3);
                    modelChiTiet.setValueAt(ct.getThanhTien(), i, 4);

                    capNhatTongTien();
                    txtMaSP.setText("");
                    txtSoLuong.setText("");
                    txtDonGia.setText("");
                    return;
                }
            }

            ChiTietPNhap ct = new ChiTietPNhap("", sp.getMaSP(), soLuong, donGia);
            dsChiTiet.add(ct);

            modelChiTiet.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                soLuong,
                donGia,
                ct.getThanhTien()
            });

            capNhatTongTien();
            txtMaSP.setText("");
            txtSoLuong.setText("");
            txtDonGia.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số.");
        }
    }

    private void xoaDongChiTiet() {
        int row = tableChiTiet.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa.");
            return;
        }

        dsChiTiet.remove(row);
        modelChiTiet.removeRow(row);
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        int tong = 0;
        for (ChiTietPNhap ct : dsChiTiet) {
            tong += ct.getThanhTien();
        }
        lblTongTien.setText(String.valueOf(tong));
    }

    private String layMaTuCombo(String value) {
        String[] parts = value.split(" - ");
        return parts[0].trim();
    }

    private void luuPhieuNhap() {
        String maPhieu = txtMaPhieu.getText().trim();

        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu nhập.");
            return;
        }

        if (phieuNhapDAO.existsById(maPhieu)) {
            JOptionPane.showMessageDialog(this, "Mã phiếu nhập đã tồn tại.");
            return;
        }

        if (cboNhaCungCap.getSelectedItem() == null || cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp và người nhập.");
            return;
        }

        if (dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập phải có ít nhất 1 sản phẩm.");
            return;
        }

        String maNCC = layMaTuCombo(cboNhaCungCap.getSelectedItem().toString());
        String maNV = layMaTuCombo(cboNhanVien.getSelectedItem().toString());

        int tongTien;
        try {
            tongTien = Integer.parseInt(lblTongTien.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tổng tiền không hợp lệ.");
            return;
        }

        PhieuNhapHang pn = new PhieuNhapHang(
                maPhieu,
                maNCC,
                maNV,
                tongTien,
                new java.sql.Timestamp(System.currentTimeMillis())
        );

        for (ChiTietPNhap ct : dsChiTiet) {
            ct.setMaPhieu(maPhieu);
        }

        if (phieuNhapDAO.insertPhieuNhap(pn, dsChiTiet)) {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thành công.");
            loadPhieuNhapData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thất bại.");
        }
    }

    private void xoaPhieuNhap() {
        int row = tablePhieuNhap.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập cần xóa.");
            return;
        }

        String maPhieu = tablePhieuNhap.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa phiếu nhập " + maPhieu + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = phieuNhapDAO.xoaPhieuNhap(maPhieu);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thành công.");
            loadPhieuNhapData();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thất bại.");
        }
    }

    private void xuatPDFPhieuNhap() {
        int row = tablePhieuNhap.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu nhập trong bảng để xuất PDF.");
            return;
        }

        try {
            String maPhieu = modelPhieuNhap.getValueAt(row, 0).toString();
            String maNCC = modelPhieuNhap.getValueAt(row, 1).toString();
            String nguoiNhap = modelPhieuNhap.getValueAt(row, 2).toString();
            int tongTien = Integer.parseInt(modelPhieuNhap.getValueAt(row, 3).toString());
            java.sql.Timestamp thoiGian = java.sql.Timestamp.valueOf(modelPhieuNhap.getValueAt(row, 4).toString());

            PhieuNhapHang pn = new PhieuNhapHang(maPhieu, maNCC, nguoiNhap, tongTien, thoiGian);
            List<ChiTietPNhap> ds = phieuNhapDAO.getChiTietByMaPhieu(maPhieu);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
            fileChooser.setSelectedFile(new File("PhieuNhap_" + maPhieu + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                pdfPhieuNhapService.exportPhieuNhapToPDF(pn, ds, filePath);
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Xuất PDF thất bại.");
        }
    }

    private void lamMoiForm() {
        txtMaPhieu.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        lblTongTien.setText("0");

        if (cboNhaCungCap.getItemCount() > 0) {
            cboNhaCungCap.setSelectedIndex(0);
        }

        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }
    }
}