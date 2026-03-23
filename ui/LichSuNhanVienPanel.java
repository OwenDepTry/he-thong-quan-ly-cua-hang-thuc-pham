package ui;

import dao.HoaDonDAO;
import dao.PhieuNhapDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class LichSuNhanVienPanel extends JPanel {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private DefaultTableModel modelHoaDon;
    private DefaultTableModel modelPhieuNhap;
    private JLabel lblHeader;

    public LichSuNhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        lblHeader = new JLabel("Lịch sử làm việc của nhân viên", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        add(lblHeader, BorderLayout.NORTH);

        add(createContent(), BorderLayout.CENTER);

        loadData();
    }

    private JSplitPane createContent() {
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createTitledBorder("Lịch sử hóa đơn"));

        modelHoaDon = new DefaultTableModel(
                new String[]{"Mã hóa đơn", "Mã KH", "Tên khách hàng", "Tổng tiền", "Tiền giảm", "Thành tiền", "Thời gian"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblHoaDon = new JTable(modelHoaDon);
        tblHoaDon.setRowHeight(24);
        top.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createTitledBorder("Lịch sử phiếu nhập"));

        modelPhieuNhap = new DefaultTableModel(
                new String[]{"Mã phiếu nhập", "Mã NCC", "Tên nhà cung cấp", "Tổng tiền", "Ngày nhập"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblPhieuNhap = new JTable(modelPhieuNhap);
        tblPhieuNhap.setRowHeight(24);
        bottom.add(new JScrollPane(tblPhieuNhap), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setDividerLocation(300);
        split.setResizeWeight(0.55);
        return split;
    }

    private void loadData() {
        String maNV = AppSession.getMaNhanVien();
        lblHeader.setText("Lịch sử làm việc - " + maNV);

        modelHoaDon.setRowCount(0);
        List<Object[]> dsHD = hoaDonDAO.findAllForTableByNhanVien(maNV);
        for (Object[] row : dsHD) {
            modelHoaDon.addRow(new Object[]{
                row[0], row[1], row[2],
                moneyFormat.format(toDouble(row[5])),
                moneyFormat.format(toDouble(row[6])),
                moneyFormat.format(toDouble(row[7])),
                row[8]
            });
        }

        modelPhieuNhap.setRowCount(0);
        List<Object[]> dsPN = phieuNhapDAO.findAllForTableByNhanVien(maNV);
        for (Object[] row : dsPN) {
            modelPhieuNhap.addRow(new Object[]{
                row[0], row[1], row[2],
                moneyFormat.format(toDouble(row[5])),
                row[6]
            });
        }
    }

    private double toDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(value).replace(",", "").trim());
    }
}