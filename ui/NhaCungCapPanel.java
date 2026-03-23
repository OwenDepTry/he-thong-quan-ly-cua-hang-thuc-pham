package ui;

import dao.NhaCungCapDAO;
import entity.NhaCungCap;
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

public class NhaCungCapPanel extends AdminTablePanelBase {

    private static final String[] COLUMNS = {
        "Mã nhà cung cấp", "Tên", "Tên liên hệ", "SDT", "Tỉnh", "Địa chỉ", "Trạng thái"
    };

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final CrudToolbarPanel toolbar;
    private final SearchPanel searchPanel;
    private final DefaultTableModel tableModel;

    public NhaCungCapPanel() {
        super();

        toolbar = new CrudToolbarPanel(
                "Thêm", "Xóa", "Sửa", "Chi tiết", "Xuất excel", "Nhập excel"
        );

        searchPanel = new SearchPanel(
                "Dữ liệu nhà cung cấp",
                "Mã", "Tên", "Tên liên hệ", "SDT"
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
        loadTableData(nhaCungCapDAO.findAllForTable());
    }

    private void bindEvents() {
        toolbar.getButton("Thêm").addActionListener(e -> themNhaCungCap());
        toolbar.getButton("Sửa").addActionListener(e -> suaNhaCungCap());
        toolbar.getButton("Xóa").addActionListener(e -> xoaNhaCungCap());
        toolbar.getButton("Chi tiết").addActionListener(e -> xemChiTiet());

        toolbar.getButton("Xuất excel").addActionListener(e ->
                ExportUtils.exportTableToCsv(this, table, "danh_sach_nha_cung_cap"));

        toolbar.getButton("Nhập excel").addActionListener(e -> importNhaCungCapCsv());

        searchPanel.getBtnRefresh().addActionListener(e -> timNhaCungCap());
        searchPanel.getBtnReset().addActionListener(e -> datLaiTimKiem());
        searchPanel.getTxtKeyword().addActionListener(e -> timNhaCungCap());
    }

    private void importNhaCungCapCsv() {
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

                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNhaCungCap(row[0]);
                ncc.setTen(row[1]);
                ncc.setTenLienHe(row[2]);
                ncc.setSoDienThoai(row[3]);
                ncc.setTinh(row[4]);
                ncc.setDiaChi(row[5]);
                ncc.setTrangThai(row[6]);

                if (nhaCungCapDAO.existsById(ncc.getMaNhaCungCap())
                        || nhaCungCapDAO.existsPhone(ncc.getSoDienThoai(), null)) {
                    fail++;
                    continue;
                }

                if (nhaCungCapDAO.insert(ncc)) {
                    success++;
                } else {
                    fail++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail++;
            }
        }

        loadTableData(nhaCungCapDAO.findAllForTable());
        JOptionPane.showMessageDialog(this,
                "Nhập CSV hoàn tất.\nThành công: " + success + "\nThất bại: " + fail);
    }

    private void loadTableData(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
        searchPanel.getLblResult().setText("Dữ liệu nhà cung cấp: " + data.size() + " dòng");
    }

    private void themNhaCungCap() {
        NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), "Thêm nhà cung cấp", null, true);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        NhaCungCap ncc = dialog.getNhaCungCap();

        if (nhaCungCapDAO.existsById(ncc.getMaNhaCungCap())) {
            JOptionPane.showMessageDialog(this, "Mã nhà cung cấp đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhaCungCapDAO.existsPhone(ncc.getSoDienThoai(), null)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhaCungCapDAO.insert(ncc)) {
            loadTableData(nhaCungCapDAO.findAllForTable());
            selectRowById(ncc.getMaNhaCungCap());
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhaCungCap() {
        String ma = getSelectedMaNCC();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap current = nhaCungCapDAO.findById(ma);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu nhà cung cấp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NhaCungCapDialog dialog = new NhaCungCapDialog(getParentFrame(), "Sửa nhà cung cấp", current, false);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        NhaCungCap updated = dialog.getNhaCungCap();

        if (nhaCungCapDAO.existsPhone(updated.getSoDienThoai(), updated.getMaNhaCungCap())) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại ở nhà cung cấp khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhaCungCapDAO.update(updated)) {
            loadTableData(nhaCungCapDAO.findAllForTable());
            selectRowById(updated.getMaNhaCungCap());
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhaCungCap() {
        String ma = getSelectedMaNCC();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhà cung cấp " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (nhaCungCapDAO.delete(ma)) {
            loadTableData(nhaCungCapDAO.findAllForTable());
            JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công.");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa thất bại. Có thể nhà cung cấp đang được tham chiếu trong phiếu nhập.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void xemChiTiet() {
        String ma = getSelectedMaNCC();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap ncc = nhaCungCapDAO.findById(ma);
        if (ncc == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu nhà cung cấp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "Mã nhà cung cấp: " + ncc.getMaNhaCungCap()
                + "\nTên: " + ncc.getTen()
                + "\nTên liên hệ: " + ncc.getTenLienHe()
                + "\nSố điện thoại: " + ncc.getSoDienThoai()
                + "\nTỉnh: " + ncc.getTinh()
                + "\nĐịa chỉ: " + ncc.getDiaChi()
                + "\nTrạng thái: " + ncc.getTrangThai();

        JOptionPane.showMessageDialog(this, message, "Chi tiết nhà cung cấp", JOptionPane.INFORMATION_MESSAGE);
    }

    private void timNhaCungCap() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = getSelectedSearchField();
        loadTableData(nhaCungCapDAO.searchForTable(field, keyword));
    }

    private void datLaiTimKiem() {
        searchPanel.getTxtKeyword().setText("");
        loadTableData(nhaCungCapDAO.findAllForTable());
    }

    private String getSelectedMaNCC() {
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