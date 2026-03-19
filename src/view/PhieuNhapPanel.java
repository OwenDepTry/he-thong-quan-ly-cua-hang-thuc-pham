package view;

import dao.NhaCungCapSimpleDAO;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PhieuNhapPanel extends JPanel {

    private JTextField txtMaPhieu;
    private JComboBox<String> cboNCC;
    private JComboBox<String> cboNhanVien;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;
    private JLabel lblTongTien;

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;

    private JTable tablePhieuNhap;
    private DefaultTableModel modelPhieuNhap;

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final NhaCungCapSimpleDAO nhaCungCapDAO = new NhaCungCapSimpleDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

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
        cboNCC = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        lblTongTien = new JLabel("0");

        topForm.add(new JLabel("Mã phiếu:"));
        topForm.add(txtMaPhieu);
        topForm.add(new JLabel("Nhà cung cấp:"));
        topForm.add(cboNCC);

        topForm.add(new JLabel("Người nhập:"));
        topForm.add(cboNhanVien);
        topForm.add(new JLabel("Mã sản phẩm:"));
        topForm.add(txtMaSP);

        topForm.add(new JLabel("Số lượng:"));
        topForm.add(txtSoLuong);
        topForm.add(new JLabel("Tổng tiền:"));
        topForm.add(lblTongTien);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnThemSP = new JButton("Thêm sản phẩm");
        JButton btnXoaDong = new JButton("Xóa dòng");
        JButton btnLuuPN = new JButton("Lưu phiếu nhập");
        JButton btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThemSP);
        buttonPanel.add(btnXoaDong);
        buttonPanel.add(btnLuuPN);
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
        JScrollPane scrollPhieu = new JScrollPane(tablePhieuNhap);
        scrollPhieu.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));

        JPanel upper = new JPanel(new BorderLayout(10, 10));
        upper.add(topForm, BorderLayout.NORTH);
        upper.add(buttonPanel, BorderLayout.CENTER);
        upper.add(scrollChiTiet, BorderLayout.SOUTH);

        mainPanel.add(upper, BorderLayout.NORTH);
        mainPanel.add(scrollPhieu, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        btnThemSP.addActionListener(e -> themSanPhamVaoPhieuNhap());
        btnXoaDong.addActionListener(e -> xoaDongChiTiet());
        btnLuuPN.addActionListener(e -> luuPhieuNhap());
        btnLamMoi.addActionListener(e -> lamMoiForm());
    }

    private void loadComboData() {
        cboNCC.removeAllItems();
        for (NhaCungCap ncc : nhaCungCapDAO.getAll()) {
            cboNCC.addItem(ncc.getMaNCCap() + " - " + ncc.getTenNCCap());
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

        if (maSP.isEmpty() || soLuongText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm và số lượng.");
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongText);
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải > 0.");
                return;
            }

            SanPham sp = sanPhamDAO.findById(maSP);
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm.");
                return;
            }

            ChiTietPNhap ct = new ChiTietPNhap("", sp.getMaSP(), soLuong, sp.getGia());
            dsChiTiet.add(ct);

            modelChiTiet.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                soLuong,
                sp.getGia(),
                ct.getThanhTien()
            });

            capNhatTongTien();

            txtMaSP.setText("");
            txtSoLuong.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số.");
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
        return value.split(" - ")[0].trim();
    }

    private void luuPhieuNhap() {
        String maPhieu = txtMaPhieu.getText().trim();

        if (maPhieu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phiếu.");
            return;
        }

        if (cboNCC.getSelectedItem() == null || cboNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp và người nhập.");
            return;
        }

        if (dsChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập phải có ít nhất 1 sản phẩm.");
            return;
        }

        String maNCC = layMaTuCombo(cboNCC.getSelectedItem().toString());
        String maNV = layMaTuCombo(cboNhanVien.getSelectedItem().toString());
        int tongTien = Integer.parseInt(lblTongTien.getText());

        PhieuNhapHang pn = new PhieuNhapHang(
                maPhieu,
                maNCC,
                maNV,
                tongTien,
                new Timestamp(System.currentTimeMillis())
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

    private void lamMoiForm() {
        txtMaPhieu.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        lblTongTien.setText("0");

        if (cboNCC.getItemCount() > 0) cboNCC.setSelectedIndex(0);
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
    }
}