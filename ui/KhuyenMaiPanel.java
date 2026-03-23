package ui;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class KhuyenMaiPanel extends AdminTablePanelBase {

    private static final String[] COLUMNS = {
        "Mã khuyến mãi",
        "Tên khuyến mãi",
        "Điều kiện",
        "Phần trăm giảm",
        "Ngày bắt đầu",
        "Ngày kết thúc",
        "Trạng thái"
    };

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final CrudToolbarPanel toolbar;
    private final SearchPanel searchPanel;
    private final DefaultTableModel tableModel;

    public KhuyenMaiPanel() {
        super();

        toolbar = new CrudToolbarPanel(
                "Thêm", "Xóa", "Sửa", "Chi tiết", "Xuất excel", "Nhập excel"
        );

        searchPanel = new SearchPanel(
                "Dữ liệu khuyến mãi",
                "Mã", "Tên", "Trạng thái"
        );

        buildTopBar(toolbar, searchPanel);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        centerPanel.removeAll();
        centerPanel.add(createStyledTable(table), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();

        bindEvents();
        loadTableData(khuyenMaiDAO.findAllForTable());
    }

    private void bindEvents() {
        toolbar.getButton("Thêm").addActionListener(e -> themKhuyenMai());
        toolbar.getButton("Xóa").addActionListener(e -> xoaKhuyenMai());
        toolbar.getButton("Sửa").addActionListener(e -> suaKhuyenMai());
        toolbar.getButton("Chi tiết").addActionListener(e -> xemChiTiet());

        toolbar.getButton("Xuất excel").addActionListener(e ->
                ExportUtils.exportTableToCsv(this, table, "danh_sach_khuyen_mai"));

        toolbar.getButton("Nhập excel").addActionListener(e -> importKhuyenMaiCsv());

        searchPanel.getBtnRefresh().addActionListener(e -> timKhuyenMai());
        searchPanel.getBtnReset().addActionListener(e -> datLaiTimKiem());
        searchPanel.getTxtKeyword().addActionListener(e -> timKhuyenMai());
    }

    private void importKhuyenMaiCsv() {
        File file = ImportUtils.chooseCsvFile(this);
        if (file == null) return;

        List<String[]> rows = ImportUtils.readCsv(this, file);
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "File không có dữ liệu.");
            return;
        }

        int success = 0;
        int fail = 0;

        for (String[] row : rows) {
            try {
                if (row.length < 7) {
                    fail++;
                    continue;
                }

                KhuyenMai km = new KhuyenMai();
                km.setMaKhuyenMai(row[0]);
                km.setTenKhuyenMai(row[1]);
                km.setDieuKien(parseDoubleSafe(row[2]));
                km.setPhanTramGiam(parseDoubleSafe(row[3]));
                km.setNgayBatDau(row[4]);
                km.setNgayKetThuc(row[5]);
                km.setTrangThai(row[6]);

                if (khuyenMaiDAO.existsById(km.getMaKhuyenMai())) {
                    fail++;
                    continue;
                }

                if (khuyenMaiDAO.insert(km)) {
                    success++;
                } else {
                    fail++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail++;
            }
        }

        loadTableData(khuyenMaiDAO.findAllForTable());
        JOptionPane.showMessageDialog(this,
                "Nhập CSV hoàn tất.\nThành công: " + success + "\nThất bại: " + fail);
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim().replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadTableData(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
        searchPanel.getLblResult().setText("Dữ liệu khuyến mãi: " + data.size() + " dòng");
    }

    private void themKhuyenMai() {
        KhuyenMaiDialog dialog = new KhuyenMaiDialog(getParentFrame(), "Thêm khuyến mãi", null, true);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        KhuyenMai km = dialog.getKhuyenMai();

        if (khuyenMaiDAO.existsById(km.getMaKhuyenMai())) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (khuyenMaiDAO.insert(km)) {
            loadTableData(khuyenMaiDAO.findAllForTable());
            selectRowById(km.getMaKhuyenMai());
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        String ma = getSelectedMaKhuyenMai();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhuyenMai current = khuyenMaiDAO.findById(ma);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu khuyến mãi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhuyenMaiDialog dialog = new KhuyenMaiDialog(getParentFrame(), "Sửa khuyến mãi", current, false);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        KhuyenMai updated = dialog.getKhuyenMai();

        if (khuyenMaiDAO.update(updated)) {
            loadTableData(khuyenMaiDAO.findAllForTable());
            selectRowById(updated.getMaKhuyenMai());
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        String ma = getSelectedMaKhuyenMai();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khuyến mãi " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (khuyenMaiDAO.delete(ma)) {
            loadTableData(khuyenMaiDAO.findAllForTable());
            JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công.");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa thất bại. Có thể khuyến mãi đang được tham chiếu trong hóa đơn.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void xemChiTiet() {
        String ma = getSelectedMaKhuyenMai();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhuyenMai km = khuyenMaiDAO.findById(ma);
        if (km == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu khuyến mãi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "Mã khuyến mãi: " + km.getMaKhuyenMai()
                + "\nTên khuyến mãi: " + km.getTenKhuyenMai()
                + "\nĐiều kiện: " + km.getDieuKien()
                + "\nPhần trăm giảm: " + km.getPhanTramGiam()
                + "\nNgày bắt đầu: " + km.getNgayBatDau()
                + "\nNgày kết thúc: " + km.getNgayKetThuc()
                + "\nTrạng thái: " + km.getTrangThai();

        JOptionPane.showMessageDialog(this, message, "Chi tiết khuyến mãi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void timKhuyenMai() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = getSelectedSearchField();
        loadTableData(khuyenMaiDAO.searchForTable(field, keyword));
    }

    private void datLaiTimKiem() {
        searchPanel.getTxtKeyword().setText("");
        loadTableData(khuyenMaiDAO.findAllForTable());
    }

    private String getSelectedMaKhuyenMai() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return String.valueOf(table.getValueAt(selectedRow, 0));
    }

    private String getSelectedSearchField() {
        for (java.awt.Component component : searchPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (java.awt.Component child : panel.getComponents()) {
                    if (child instanceof JRadioButton) {
                        JRadioButton radio = (JRadioButton) child;
                        if (radio.isSelected()) {
                            return radio.getText();
                        }
                    }
                }
            }
        }
        return "Mã";
    }

    private void selectRowById(String ma) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (ma.equals(String.valueOf(tableModel.getValueAt(i, 0)))) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}