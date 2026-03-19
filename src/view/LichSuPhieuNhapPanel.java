package view;

import dao.PhieuNhapDAO;
import entity.PhieuNhapHang;
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

public class LichSuPhieuNhapPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private PhieuNhapDAO phieuNhapDAO;

    public LichSuPhieuNhapPanel() {
        phieuNhapDAO = new PhieuNhapDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("LỊCH SỬ PHIẾU NHẬP", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã PN", "Mã NCC", "Mã NV", "Tổng tiền", "Thời gian"},
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
            List<PhieuNhapHang> list = phieuNhapDAO.getAll();
            for (PhieuNhapHang pn : list) {
                model.addRow(new Object[]{
                    pn.getMaPhieu(),
                    pn.getMaNCCap(),
                    pn.getNguoiNhap(),
                    pn.getTongTien(),
                    pn.getThoiGian()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được lịch sử phiếu nhập.\n" + e.getMessage());
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