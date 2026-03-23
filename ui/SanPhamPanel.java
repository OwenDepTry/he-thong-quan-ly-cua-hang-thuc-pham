package ui;

import dao.SanPhamDAO;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SanPhamPanel extends AdminTablePanelBase {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private DefaultTableModel model;
    private JTable tb;
    private CrudToolbarPanel toolbar;

    public SanPhamPanel() {
        super();

        toolbar = new CrudToolbarPanel(
                "Thêm", "Xóa", "Sửa", "Chi tiết", "Xuất excel", "Nhập excel"
        );

        SearchPanel searchPanel = new SearchPanel(
                "Tìm thấy dữ liệu",
                "Mã", "Loại", "Tên"
        );

        buildTopBar(toolbar, searchPanel);
        initTable();
        loadTableData();
        bindEvents();
    }

    private void initTable() {
        String[] columns = {
            "Mã sản phẩm", "Mã nhóm", "Tên sản phẩm", "Loại",
            "Đơn vị tính", "Hạn sử dụng", "Mô tả", "Giá", "Số lượng tồn"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tb = new JTable(model);
        tb.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel panelSanPhamCu = new JPanel(new BorderLayout());
        panelSanPhamCu.setOpaque(false);
        panelSanPhamCu.add(createStyledTable(tb), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Quản lý sản phẩm", panelSanPhamCu);
        tabs.addTab("Quản lý loại sản phẩm", new LoaiSanPhamPanel());
        tabs.addTab("Quản lý hàng hóa", new HangHoaPanel());

        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(tabs, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void loadTableData() {
        model.setRowCount(0);
        List<Object[]> data = sanPhamDAO.findAllForTable();
        for (Object[] row : data) {
            model.addRow(row);
        }
    }

    private void bindEvents() {
        if (toolbar.getButton("Thêm") != null) {
            toolbar.getButton("Thêm").addActionListener(e -> themSanPham());
        }
        if (toolbar.getButton("Sửa") != null) {
            toolbar.getButton("Sửa").addActionListener(e -> suaSanPham());
        }
        if (toolbar.getButton("Xóa") != null) {
            toolbar.getButton("Xóa").addActionListener(e -> xoaSanPham());
        }
        if (toolbar.getButton("Xuất excel") != null) {
            toolbar.getButton("Xuất excel").addActionListener(e ->
                    ExportUtils.exportTableToCsv(this, tb, "danh_sach_san_pham"));
        }
        if (toolbar.getButton("Nhập excel") != null) {
            toolbar.getButton("Nhập excel").addActionListener(e ->
                    JOptionPane.showMessageDialog(
                            this,
                            "Tạm thời chưa nối nhập CSV cho sản phẩm. Nếu cần mình viết tiếp phần này.",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE
                    ));
        }
    }

    private void themSanPham() {
        JFrame parent = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
        SanPhamDialog dialog = new SanPhamDialog(parent, "Thêm sản phẩm");
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            return;
        }

        if (sanPhamDAO.existsById(dialog.getMaSanPham())) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại!");
            return;
        }

        boolean ok = sanPhamDAO.insert(
                dialog.getMaSanPham(),
                dialog.getMaNhom(),
                dialog.getTenSanPham(),
                dialog.getLoai(),
                dialog.getDonViTinh(),
                dialog.getHanSuDung(),
                dialog.getMoTa(),
                Double.parseDouble(dialog.getGia()),
                Integer.parseInt(dialog.getSoLuongTon())
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại!");
        }
    }

    private void suaSanPham() {
        int row = tb.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        Object[] selected = new Object[tb.getColumnCount()];
        for (int i = 0; i < tb.getColumnCount(); i++) {
            selected[i] = tb.getValueAt(row, i);
        }

        JFrame parent = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
        SanPhamDialog dialog = new SanPhamDialog(parent, "Sửa sản phẩm");
        dialog.setData(selected);
        dialog.setMaEditable(false);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            return;
        }

        boolean ok = sanPhamDAO.update(
                dialog.getMaSanPham(),
                dialog.getMaNhom(),
                dialog.getTenSanPham(),
                dialog.getLoai(),
                dialog.getDonViTinh(),
                dialog.getHanSuDung(),
                dialog.getMoTa(),
                Double.parseDouble(dialog.getGia()),
                Integer.parseInt(dialog.getSoLuongTon())
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại!");
        }
    }

    private void xoaSanPham() {
        int row = tb.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        String maSanPham = String.valueOf(tb.getValueAt(row, 0));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sản phẩm " + maSanPham + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = sanPhamDAO.deleteById(maSanPham);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại!");
        }
    }
}