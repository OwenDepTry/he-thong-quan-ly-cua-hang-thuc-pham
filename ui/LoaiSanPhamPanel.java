package ui;

import dao.LoaiSanPhamDAO;
import entity.LoaiSanPham;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LoaiSanPhamPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private LoaiSanPhamDAO dao = new LoaiSanPhamDAO();

    public LoaiSanPhamPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Mã loại", "Tên loại"}, 0);
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(e -> load());

        add(btnLoad, BorderLayout.SOUTH);

        load();
    }

    private void load() {
        model.setRowCount(0);
        List<LoaiSanPham> list = dao.findAll();
        for (LoaiSanPham l : list) {
            model.addRow(new Object[]{l.getMaLoai(), l.getTenLoai()});
        }
    }
}