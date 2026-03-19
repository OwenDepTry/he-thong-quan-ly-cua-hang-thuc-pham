package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ThongKePanel extends JPanel {

    private JLabel lblTongDoanhThu;
    private JLabel lblTongHoaDon;
    private JLabel lblTongPhieuNhap;

    private JTable tableBanChay;
    private DefaultTableModel modelBanChay;

    private JTable tableTonKho;
    private DefaultTableModel modelTonKho;

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
    JLabel lblTitle = new JLabel("THỐNG KÊ HỆ THỐNG", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
    lblTitle.setOpaque(true);
    lblTitle.setBackground(new Color(46, 139, 87));
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setPreferredSize(new Dimension(100, 55));
    add(lblTitle, BorderLayout.NORTH);

    JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
    summaryPanel.setPreferredSize(new Dimension(100, 90));

    lblTongDoanhThu = createSummaryLabel("Tổng doanh thu: 0");
    lblTongHoaDon = createSummaryLabel("Tổng hóa đơn: 0");
    lblTongPhieuNhap = createSummaryLabel("Tổng phiếu nhập: 0");

    summaryPanel.add(lblTongDoanhThu);
    summaryPanel.add(lblTongHoaDon);
    summaryPanel.add(lblTongPhieuNhap);

    modelBanChay = new DefaultTableModel(
            new String[]{"Mã SP", "Tên sản phẩm", "Tổng bán"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tableBanChay = new JTable(modelBanChay);
    JScrollPane scrollBanChay = new JScrollPane(tableBanChay);
    scrollBanChay.setBorder(BorderFactory.createTitledBorder("Sản phẩm bán chạy"));

    modelTonKho = new DefaultTableModel(
            new String[]{"Mã SP", "Tên sản phẩm", "Giá", "Số lượng tồn"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tableTonKho = new JTable(modelTonKho);
    JScrollPane scrollTonKho = new JScrollPane(tableTonKho);
    scrollTonKho.setBorder(BorderFactory.createTitledBorder("Tồn kho sản phẩm"));

    JPanel tablePanel = new JPanel(new GridLayout(2, 1, 10, 10));
    tablePanel.add(scrollBanChay);
    tablePanel.add(scrollTonKho);

    JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
    contentPanel.add(summaryPanel, BorderLayout.NORTH);
    contentPanel.add(tablePanel, BorderLayout.CENTER);

    add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createSummaryLabel(String text) {
    JLabel lbl = new JLabel(text, SwingConstants.CENTER);
    lbl.setOpaque(true);
    lbl.setBackground(new Color(240, 248, 255));
    lbl.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
    lbl.setFont(new Font("Arial", Font.BOLD, 18));
    lbl.setPreferredSize(new Dimension(100, 70));
    return lbl;
    }

    private void loadData() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        int tongDoanhThu = thongKeDAO.getTongDoanhThu();
        int tongHoaDon = thongKeDAO.getTongHoaDon();
        int tongPhieuNhap = thongKeDAO.getTongPhieuNhap();

        lblTongDoanhThu.setText("Tổng doanh thu: " + nf.format(tongDoanhThu) + " VNĐ");
        lblTongHoaDon.setText("Tổng hóa đơn: " + tongHoaDon);
        lblTongPhieuNhap.setText("Tổng phiếu nhập: " + tongPhieuNhap);

        modelBanChay.setRowCount(0);
        List<Object[]> dsBanChay = thongKeDAO.getTopSanPhamBanChay();
        for (Object[] row : dsBanChay) {
            modelBanChay.addRow(row);
        }

        modelTonKho.setRowCount(0);
        List<Object[]> dsTonKho = thongKeDAO.getTonKho();
        for (Object[] row : dsTonKho) {
            modelTonKho.addRow(row);
        }
    }
}