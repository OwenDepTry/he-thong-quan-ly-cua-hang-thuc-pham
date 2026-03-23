package ui;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class KhachHangPanel extends AdminTablePanelBase {

    private static final String[] COLUMNS = {
        "Mã khách hàng", "Họ", "Tên lót", "Tên", "Phái",
        "Ngày sinh", "SDT", "Tỉnh", "Ngày tham gia", "Điểm", "Trạng thái"
    };

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final CrudToolbarPanel toolbar;
    private final SearchPanel searchPanel;
    private final DefaultTableModel tableModel;

    public KhachHangPanel() {
        super();

        toolbar = new CrudToolbarPanel(
                "Thêm", "Xóa", "Sửa", "Chi tiết", "Xuất excel", "Nhập excel"
        );

        searchPanel = new SearchPanel(
                "Dữ liệu khách hàng",
                "Mã", "Tên", "Họ", "Số điện thoại"
        );
        searchPanel.setSortOptions(
            "Mặc định",
            "Mã giảm dần",
            "Tên A-Z",
            "Tên Z-A",
            "Điểm tăng dần",
            "Điểm giảm dần"
        );

        buildTopBar(toolbar, searchPanel);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        centerPanel.removeAll();
        centerPanel.add(createStyledTable(table), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();

        bindEvents();
        loadTableData(khachHangDAO.findAllForTable());
    }

    private void bindEvents() {
        toolbar.getButton("Thêm").addActionListener(e -> themKhachHang());
        toolbar.getButton("Sửa").addActionListener(e -> suaKhachHang());
        toolbar.getButton("Xóa").addActionListener(e -> xoaKhachHang());
        toolbar.getButton("Chi tiết").addActionListener(e -> xemChiTietKhachHang());

        toolbar.getButton("Xuất excel").addActionListener(e ->
                ExportUtils.exportTableToCsv(this, table, "danh_sach_khach_hang"));

        toolbar.getButton("Nhập excel").addActionListener(e -> importKhachHangCsv());

        searchPanel.getBtnRefresh().addActionListener(e -> timKhachHang());
        searchPanel.getBtnReset().addActionListener(e -> datLaiTimKiem());
        searchPanel.getTxtKeyword().addActionListener(e -> timKhachHang());
        searchPanel.getCboSort().addActionListener(e -> timKhachHang());
    }

    private void importKhachHangCsv() {
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
                if (row.length < 11) {
                    fail++;
                    continue;
                }

                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(row[0]);
                kh.setHo(row[1]);
                kh.setTenLot(row[2]);
                kh.setTen(row[3]);
                kh.setPhai(row[4]);
                kh.setNgaySinh(row[5]);
                kh.setSoDienThoai(row[6]);
                kh.setTinh(row[7]);
                kh.setNgayThamGia(row[8]);
                kh.setDiem(parseIntSafe(row[9]));
                kh.setTrangThai(row[10]);

                if (khachHangDAO.existsById(kh.getMaKhachHang())
                        || khachHangDAO.existsPhone(kh.getSoDienThoai(), null)) {
                    fail++;
                    continue;
                }

                if (khachHangDAO.insert(kh)) {
                    success++;
                } else {
                    fail++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail++;
            }
        }

        loadTableData(khachHangDAO.findAllForTable());
        JOptionPane.showMessageDialog(this,
                "Nhập CSV hoàn tất.\nThành công: " + success + "\nThất bại: " + fail);
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadTableData(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
        searchPanel.getLblResult().setText("Dữ liệu khách hàng: " + data.size() + " dòng");
    }

    private void themKhachHang() {
        KhachHangDialog dialog = new KhachHangDialog(getParentFrame(), "Thêm khách hàng", null, true);
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        KhachHang kh = dialog.getKhachHang();
        if (khachHangDAO.existsById(kh.getMaKhachHang())) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (khachHangDAO.existsPhone(kh.getSoDienThoai(), null)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (khachHangDAO.insert(kh)) {
            loadTableData(khachHangDAO.findAllForTable());
            selectRowById(kh.getMaKhachHang());
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhachHang() {
        String maKhachHang = getSelectedMaKhachHang();
        if (maKhachHang == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang current = khachHangDAO.findById(maKhachHang);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhachHangDialog dialog = new KhachHangDialog(getParentFrame(), "Sửa khách hàng", current, false);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }

        KhachHang updated = dialog.getKhachHang();
        if (khachHangDAO.existsPhone(updated.getSoDienThoai(), updated.getMaKhachHang())) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại ở khách hàng khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (khachHangDAO.update(updated)) {
            loadTableData(khachHangDAO.findAllForTable());
            selectRowById(updated.getMaKhachHang());
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        String maKhachHang = getSelectedMaKhachHang();
        if (maKhachHang == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khách hàng " + maKhachHang + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (khachHangDAO.delete(maKhachHang)) {
            loadTableData(khachHangDAO.findAllForTable());
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công.");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Xóa khách hàng thất bại. Có thể khách hàng này đang được tham chiếu trong hóa đơn.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void xemChiTietKhachHang() {
        String maKhachHang = getSelectedMaKhachHang();
        if (maKhachHang == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xem chi tiết.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang kh = khachHangDAO.findById(maKhachHang);
        if (kh == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "Mã khách hàng: " + kh.getMaKhachHang()
                + "\nHọ tên: " + kh.getHoTen()
                + "\nPhái: " + kh.getPhai()
                + "\nNgày sinh: " + kh.getNgaySinh()
                + "\nSố điện thoại: " + kh.getSoDienThoai()
                + "\nTỉnh: " + kh.getTinh()
                + "\nNgày tham gia: " + kh.getNgayThamGia()
                + "\nĐiểm tích lũy: " + kh.getDiem()
                + "\nTrạng thái: " + kh.getTrangThai();

        JOptionPane.showMessageDialog(this, message, "Chi tiết khách hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void timKhachHang() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = searchPanel.getSelectedRadioText();
        List<Object[]> data = new ArrayList<>(khachHangDAO.searchForTable(field, keyword));
        sortData(data);
        loadTableData(data);
    }

    private void datLaiTimKiem() {
        searchPanel.getTxtKeyword().setText("");
        searchPanel.getCboSort().setSelectedIndex(0);
        timKhachHang();
    }

    private String getSelectedMaKhachHang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return String.valueOf(table.getValueAt(selectedRow, 0));
    }

    private void sortData(List<Object[]> data) {
        String option = String.valueOf(searchPanel.getCboSort().getSelectedItem());
        switch (option) {
            case "Mã giảm dần":
                data.sort((a, b) -> PanelSortUtils.compareCode(b[0], a[0]));
                break;
            case "Tên A-Z":
                data.sort((a, b) -> PanelSortUtils.compareText(fullName(a), fullName(b)));
                break;
            case "Tên Z-A":
                data.sort((a, b) -> PanelSortUtils.compareText(fullName(b), fullName(a)));
                break;
            case "Điểm tăng dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(a[9], b[9]));
                break;
            case "Điểm giảm dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(b[9], a[9]));
                break;
            default:
                data.sort((a, b) -> PanelSortUtils.compareCode(a[0], b[0]));
                break;
        }
    }

    private String fullName(Object[] row) {
        return String.valueOf(row[1]) + " " + String.valueOf(row[2]) + " " + String.valueOf(row[3]);
    }

    private void selectRowById(String maKhachHang) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (maKhachHang.equals(String.valueOf(tableModel.getValueAt(i, 0)))) {
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