package view;

import dao.HoaDonDAO;
import dao.KhachHangSimpleDAO;
import dao.NhanVienSimpleDAO;
import dao.SanPhamDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
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

public class HoaDonPanel extends JPanel {

    private JTextField txtMaHD;
    private JComboBox<String> cboKhachHang;
    private JComboBox<String> cboNhanVien;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;
    private JLabel lblTongTien;

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;

    private JTable tableHoaDon;
    private DefaultTableModel modelHoaDon;

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangSimpleDAO khachHangDAO = new KhachHangSimpleDAO();
    private final NhanVienSimpleDAO nhanVienDAO = new NhanVienSimpleDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private final List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

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

        JPanel topForm = new JPanel(new GridLayout(3, 4, 10, 10));
        topForm.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        txtMaHD = new JTextField();
        cboKhachHang = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        lblTongTien = new JLabel("0");

        topForm.add(new JLabel("Mã hóa đơn:"));
        topForm.add(txtMaHD);
        topForm.add(new JLabel("Khách hàng:"));
        topForm.add(cboKhachHang);

        topForm.add(new JLabel("Nhân viên:"));
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
        JButton btnLuuHD = new JButton("Lưu hóa đơn");
        JButton btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThemSP);
        buttonPanel.add(btnXoaDong);
        buttonPanel.add(btnLuuHD);
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
        scrollChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        modelHoaDon = new DefaultTableModel(
                new String[]{"Mã HD", "Mã KH", "Mã NV", "Tổng tiền", "Thời gian"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHoaDon = new JTable(modelHoaDon);
        JScrollPane scrollHoaDon = new JScrollPane(tableHoaDon);
        scrollHoaDon.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        JPanel upper = new JPanel(new BorderLayout(10, 10));
        upper.add(topForm, BorderLayout.NORTH);
        upper.add(buttonPanel, BorderLayout.CENTER);
        upper.add(scrollChiTiet, BorderLayout.SOUTH);

        mainPanel.add(upper, BorderLayout.NORTH);
        mainPanel.add(scrollHoaDon, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        btnThemSP.addActionListener(e -> themSanPhamVaoHoaDon());
        btnXoaDong.addActionListener(e -> xoaDongChiTiet());
        btnLuuHD.addActionListener(e -> luuHoaDon());
        btnLamMoi.addActionListener(e -> lamMoiForm());
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
    }

    private void loadHoaDonData() {
        modelHoaDon.setRowCount(0);
        for (HoaDon hd : hoaDonDAO.getAll()) {
            modelHoaDon.addRow(new Object[]{
                hd.getMaHD(),
                hd.getMaKH(),
                hd.getMaNV(),
                hd.getTongTien(),
                hd.getThoiGian()
            });
        }
    }

    private void themSanPhamVaoHoaDon() {
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

            ChiTietHoaDon ct = new ChiTietHoaDon("", sp.getMaSP(), soLuong, sp.getGia());
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
        for (ChiTietHoaDon ct : dsChiTiet) {
            tong += ct.getThanhTien();
        }
        lblTongTien.setText(String.valueOf(tong));
    }

    private String layMaTuCombo(String value) {
        return value.split(" - ")[0].trim();
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
            JOptionPane.showMessageDialog(this, "Hóa đơn phải có ít nhất 1 sản phẩm.");
            return;
        }

        String maKH = layMaTuCombo(cboKhachHang.getSelectedItem().toString());
        String maNV = layMaTuCombo(cboNhanVien.getSelectedItem().toString());
        int tongTien = Integer.parseInt(lblTongTien.getText());

        HoaDon hd = new HoaDon(
                maHD,
                maKH,
                maNV,
                tongTien,
                new Timestamp(System.currentTimeMillis())
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

    private void lamMoiForm() {
        txtMaHD.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        dsChiTiet.clear();
        modelChiTiet.setRowCount(0);
        lblTongTien.setText("0");

        if (cboKhachHang.getItemCount() > 0) cboKhachHang.setSelectedIndex(0);
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
    }
}