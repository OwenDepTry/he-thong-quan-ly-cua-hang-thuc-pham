package ui;

import dao.HangHoaDAO;
import entity.HangHoa;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HangHoaPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private HangHoaDAO dao = new HangHoaDAO();

    public HangHoaPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
            new String[]{"Mã hàng", "Mã SP", "Số lượng", "Ngày nhập", "HSD"}, 0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        load();
    }

    private void load() {
        model.setRowCount(0);
        List<HangHoa> list = dao.findAll();
        for (HangHoa h : list) {
            model.addRow(new Object[]{
                h.getMaHang(),
                h.getMaSanPham(),
                h.getSoLuong(),
                h.getNgayNhap(),
                h.getHanSuDung()
            });
        }
    }
}