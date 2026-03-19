package view;

import dao.HoaDonDAO;
import entity.HoaDon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class LichSuHoaDonPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private HoaDonDAO hoaDonDAO;

    public LichSuHoaDonPanel() {
        hoaDonDAO = new HoaDonDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("LỊCH SỬ HÓA ĐƠN", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã HD", "Mã KH", "Mã NV", "Mã KM", "Tiền giảm", "Tổng tiền", "Thời gian"},
                0
        );
        table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<HoaDon> list = hoaDonDAO.getAll();
            for (HoaDon hd : list) {
                model.addRow(new Object[]{
                    safe(getValue(hd, "getMaHD")),
                    safe(getValue(hd, "getMaKH")),
                    safe(getValue(hd, "getMaNV")),
                    safe(getValue(hd, "getMaKM")),
                    getValue(hd, "getTienGiam"),
                    getValue(hd, "getTongTien"),
                    safe(getValue(hd, "getThoiGian"))
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được lịch sử hóa đơn.\n" + e.getMessage());
        }
    }

    private Object getValue(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            return "";
        }
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}