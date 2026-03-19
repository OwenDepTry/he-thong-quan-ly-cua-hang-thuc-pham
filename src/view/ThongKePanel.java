package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ThongKePanel extends JPanel {

    private JLabel lblTongDoanhThu;
    private JLabel lblTongHoaDon;
    private JLabel lblTongPhieuNhap;

    private JTextField txtNgay;
    private JTextField txtThang;
    private JTextField txtNam;
    private JLabel lblKetQuaThongKe;

    private JTable tableBanChay;
    private DefaultTableModel modelBanChay;

    private JTable tableTonKho;
    private DefaultTableModel modelTonKho;

    private JTable tableNhanVien;
    private DefaultTableModel modelNhanVien;

    private JTable tableKhachHang;
    private DefaultTableModel modelKhachHang;

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadAllData();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("THỐNG KÊ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(100, 55));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(createTongQuatPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(createLocPanel(), BorderLayout.NORTH);
        centerPanel.add(createTabbedTables(), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTongQuatPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 12));
        panel.setBorder(BorderFactory.createTitledBorder("Tổng quan"));

        lblTongDoanhThu = createValueLabel();
        lblTongHoaDon = createValueLabel();
        lblTongPhieuNhap = createValueLabel();

        panel.add(createCard("Tổng doanh thu", lblTongDoanhThu));
        panel.add(createCard("Tổng số hóa đơn", lblTongHoaDon));
        panel.add(createCard("Tổng số phiếu nhập", lblTongPhieuNhap));

        return panel;
    }

    private JPanel createLocPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê theo thời gian"));

        txtNgay = new JTextField(10);
        txtThang = new JTextField(5);
        txtNam = new JTextField(5);

        lblKetQuaThongKe = new JLabel("0 VNĐ");
        lblKetQuaThongKe.setFont(new Font("Arial", Font.BOLD, 16));
        lblKetQuaThongKe.setForeground(new Color(0, 102, 204));

        JButton btnThongKeNgay = new JButton("Theo ngày");
        JButton btnThongKeThang = new JButton("Theo tháng");
        JButton btnThongKeNam = new JButton("Theo năm");
        JButton btnLamMoi = new JButton("Làm mới");

        panel.add(new JLabel("Ngày (yyyy-MM-dd):"));
        panel.add(txtNgay);
        panel.add(btnThongKeNgay);

        panel.add(new JLabel("Tháng:"));
        panel.add(txtThang);

        panel.add(new JLabel("Năm:"));
        panel.add(txtNam);

        panel.add(btnThongKeThang);
        panel.add(btnThongKeNam);
        panel.add(btnLamMoi);

        panel.add(new JLabel("Kết quả:"));
        panel.add(lblKetQuaThongKe);

        btnThongKeNgay.addActionListener(e -> thongKeTheoNgay());
        btnThongKeThang.addActionListener(e -> thongKeTheoThang());
        btnThongKeNam.addActionListener(e -> thongKeTheoNam());
        btnLamMoi.addActionListener(e -> lamMoiThongKe());

        return panel;
    }

    private JTabbedPane createTabbedTables() {
        JTabbedPane tabs = new JTabbedPane();

        modelBanChay = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Tổng bán"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBanChay = new JTable(modelBanChay);

        modelTonKho = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Số lượng tồn", "Giá bán"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTonKho = new JTable(modelTonKho);

        modelNhanVien = new DefaultTableModel(
                new String[]{"Mã NV", "Tên nhân viên", "Số hóa đơn", "Doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableNhanVien = new JTable(modelNhanVien);

        modelKhachHang = new DefaultTableModel(
                new String[]{"Mã KH", "Tên khách hàng", "Số lần mua", "Tổng chi tiêu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableKhachHang = new JTable(modelKhachHang);

        JScrollPane scrollBanChay = new JScrollPane(tableBanChay);
        JScrollPane scrollTonKho = new JScrollPane(tableTonKho);
        JScrollPane scrollNhanVien = new JScrollPane(tableNhanVien);
        JScrollPane scrollKhachHang = new JScrollPane(tableKhachHang);

        tabs.addTab("Sản phẩm bán chạy", scrollBanChay);
        tabs.addTab("Tồn kho", scrollTonKho);
        tabs.addTab("Theo nhân viên", scrollNhanVien);
        tabs.addTab("Theo khách hàng", scrollKhachHang);

        return tabs;
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("0");
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        lbl.setForeground(new Color(46, 139, 87));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        return lbl;
    }

    private void loadAllData() {
        loadThongKeTongQuat();
        loadTopSanPhamBanChay();
        loadTonKhoSanPham();
        loadThongKeNhanVien();
        loadThongKeKhachHang();
    }

    private void loadThongKeTongQuat() {
        lblTongDoanhThu.setText(formatMoney(thongKeDAO.getTongDoanhThu()));
        lblTongHoaDon.setText(String.valueOf(thongKeDAO.getTongSoHoaDon()));
        lblTongPhieuNhap.setText(String.valueOf(thongKeDAO.getTongSoPhieuNhap()));
    }

    private void loadTopSanPhamBanChay() {
        modelBanChay.setRowCount(0);

        List<Object[]> list = thongKeDAO.getTopSanPhamBanChay();
        for (Object[] row : list) {
            modelBanChay.addRow(row);
        }
    }

    private void loadTonKhoSanPham() {
        modelTonKho.setRowCount(0);

        List<Object[]> list = thongKeDAO.getTonKhoSanPham();
        for (Object[] row : list) {
            Object[] newRow = new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((Integer) row[3])
            };
            modelTonKho.addRow(newRow);
        }
    }

    private void loadThongKeNhanVien() {
        modelNhanVien.setRowCount(0);

        List<Object[]> list = thongKeDAO.getThongKeTheoNhanVien();
        for (Object[] row : list) {
            Object[] newRow = new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((Integer) row[3])
            };
            modelNhanVien.addRow(newRow);
        }
    }

    private void loadThongKeKhachHang() {
        modelKhachHang.setRowCount(0);

        List<Object[]> list = thongKeDAO.getThongKeTheoKhachHang();
        for (Object[] row : list) {
            Object[] newRow = new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((Integer) row[3])
            };
            modelKhachHang.addRow(newRow);
        }
    }

    private void thongKeTheoNgay() {
        String ngay = txtNgay.getText().trim();

        if (ngay.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày theo dạng yyyy-MM-dd.");
            return;
        }

        int doanhThu = thongKeDAO.getDoanhThuTheoNgay(ngay);
        lblKetQuaThongKe.setText(formatMoney(doanhThu));
    }

    private void thongKeTheoThang() {
        String thangText = txtThang.getText().trim();
        String namText = txtNam.getText().trim();

        if (thangText.isEmpty() || namText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tháng và năm.");
            return;
        }

        try {
            int thang = Integer.parseInt(thangText);
            int nam = Integer.parseInt(namText);

            if (thang < 1 || thang > 12) {
                JOptionPane.showMessageDialog(this, "Tháng phải từ 1 đến 12.");
                return;
            }

            int doanhThu = thongKeDAO.getDoanhThuTheoThang(thang, nam);
            lblKetQuaThongKe.setText(formatMoney(doanhThu));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tháng và năm phải là số.");
        }
    }

    private void thongKeTheoNam() {
        String namText = txtNam.getText().trim();

        if (namText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập năm.");
            return;
        }

        try {
            int nam = Integer.parseInt(namText);
            int doanhThu = thongKeDAO.getDoanhThuTheoNam(nam);
            lblKetQuaThongKe.setText(formatMoney(doanhThu));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm phải là số.");
        }
    }

    private void lamMoiThongKe() {
        txtNgay.setText("");
        txtThang.setText("");
        txtNam.setText("");
        lblKetQuaThongKe.setText("0 VNĐ");
        loadAllData();
    }

    private String formatMoney(int amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VNĐ";
    }
}