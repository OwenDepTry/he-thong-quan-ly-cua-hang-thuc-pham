package ui;

import dao.NhanVienDAO;
import entity.NhanVien;
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

public class NhanVienPanel extends AdminTablePanelBase {

    private static final String[] COLUMNS = {
        "Mã NV", "Họ", "Tên lót", "Tên", "Phái", "Ngày sinh",
        "SDT", "Tỉnh", "Địa chỉ", "Lương", "Chức vụ", "Trạng thái"
    };

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final CrudToolbarPanel toolbar;
    private final SearchPanel searchPanel;
    private final DefaultTableModel tableModel;

    public NhanVienPanel() {
        super();

        toolbar = new CrudToolbarPanel(
                "Thêm", "Xóa", "Sửa", "Chi tiết", "Xuất excel", "Nhập excel"
        );

        searchPanel = new SearchPanel(
                "Dữ liệu nhân viên",
                "Mã", "Tên", "SDT", "Chức vụ"
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
        loadTableData(nhanVienDAO.findAllForTable());
    }

    private void bindEvents() {
        toolbar.getButton("Thêm").addActionListener(e -> themNhanVien());
        toolbar.getButton("Sửa").addActionListener(e -> suaNhanVien());
        toolbar.getButton("Xóa").addActionListener(e -> xoaNhanVien());
        toolbar.getButton("Chi tiết").addActionListener(e -> xemChiTiet());

        toolbar.getButton("Xuất excel").addActionListener(e ->
                ExportUtils.exportTableToCsv(this, table, "danh_sach_nhan_vien"));

        toolbar.getButton("Nhập excel").addActionListener(e -> importNhanVienCsv());

        searchPanel.getBtnRefresh().addActionListener(e -> timNhanVien());
        searchPanel.getBtnReset().addActionListener(e -> datLaiTimKiem());
        searchPanel.getTxtKeyword().addActionListener(e -> timNhanVien());
    }

    private void importNhanVienCsv() {
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
                if (row.length < 12) {
                    fail++;
                    continue;
                }

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(row[0]);
                nv.setHo(row[1]);
                nv.setTenLot(row[2]);
                nv.setTen(row[3]);
                nv.setPhai(row[4]);
                nv.setNgaySinh(row[5]);
                nv.setSoDienThoai(row[6]);
                nv.setTinh(row[7]);
                nv.setDiaChi(row[8]);
                nv.setLuong(parseDoubleSafe(row[9]));
                nv.setChucVu(row[10]);
                nv.setTrangThai(row[11]);

                if (row.length >= 13) {
                    nv.setMatKhau(row[12]);
                } else {
                    nv.setMatKhau("123");
                }

                if (nhanVienDAO.existsById(nv.getMaNhanVien())
                        || nhanVienDAO.existsPhone(nv.getSoDienThoai(), null)) {
                    fail++;
                    continue;
                }

                if (nhanVienDAO.insert(nv)) {
                    success++;
                } else {
                    fail++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail++;
            }
        }

        loadTableData(nhanVienDAO.findAllForTable());
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
        searchPanel.getLblResult().setText("Dữ liệu nhân viên: " + data.size() + " dòng");
    }

    private void themNhanVien() {
        NhanVienDialog dialog = new NhanVienDialog(getParentFrame(), "Thêm nhân viên", null, true);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        NhanVien nv = dialog.getNhanVien();

        if (nhanVienDAO.existsById(nv.getMaNhanVien())) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhanVienDAO.existsPhone(nv.getSoDienThoai(), null)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhanVienDAO.insert(nv)) {
            loadTableData(nhanVienDAO.findAllForTable());
            selectRowById(nv.getMaNhanVien());
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhanVien() {
        String ma = getSelectedMaNhanVien();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhanVien current = nhanVienDAO.findById(ma);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NhanVienDialog dialog = new NhanVienDialog(getParentFrame(), "Sửa nhân viên", current, false);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        NhanVien updated = dialog.getNhanVien();

        if (nhanVienDAO.existsPhone(updated.getSoDienThoai(), updated.getMaNhanVien())) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại ở nhân viên khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhanVienDAO.update(updated)) {
            loadTableData(nhanVienDAO.findAllForTable());
            selectRowById(updated.getMaNhanVien());
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        String ma = getSelectedMaNhanVien();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhân viên " + ma + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (nhanVienDAO.delete(ma)) {
            loadTableData(nhanVienDAO.findAllForTable());
            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa thất bại. Có thể nhân viên đang được tham chiếu trong dữ liệu khác.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void xemChiTiet() {
        String ma = getSelectedMaNhanVien();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhanVien nv = nhanVienDAO.findById(ma);
        if (nv == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "Mã nhân viên: " + nv.getMaNhanVien()
                + "\nHọ tên: " + nv.getHoTen()
                + "\nPhái: " + nv.getPhai()
                + "\nNgày sinh: " + nv.getNgaySinh()
                + "\nSố điện thoại: " + nv.getSoDienThoai()
                + "\nTỉnh: " + nv.getTinh()
                + "\nĐịa chỉ: " + nv.getDiaChi()
                + "\nLương: " + nv.getLuong()
                + "\nChức vụ: " + nv.getChucVu()
                + "\nTrạng thái: " + nv.getTrangThai()
                + "\nMật khẩu: " + nv.getMatKhau();

        JOptionPane.showMessageDialog(this, message, "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void timNhanVien() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = getSelectedSearchField();
        loadTableData(nhanVienDAO.searchForTable(field, keyword));
    }

    private void datLaiTimKiem() {
        searchPanel.getTxtKeyword().setText("");
        loadTableData(nhanVienDAO.findAllForTable());
    }

    private String getSelectedMaNhanVien() {
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