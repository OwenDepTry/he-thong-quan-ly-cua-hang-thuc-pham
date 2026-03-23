package ui;

import dao.HoaDonDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HoaDonPanel extends AdminTablePanelBase {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JTable tblHoaDon;
    private JTable tblChiTiet;
    private DefaultTableModel modelHoaDon;
    private DefaultTableModel modelChiTiet;

    private JTextField txtMaHoaDon;
    private JTextField txtKhachHang;
    private JTextField txtNhanVien;
    private JTextField txtThoiGian;
    private JTextField txtTongTien;
    private JTextField txtTienGiam;
    private JTextField txtThanhTien;
    private JTextField txtKhuyenMai;

    private CrudToolbarPanel toolbar;
    private SearchPanel searchPanel;

    public HoaDonPanel() {
        super();

        toolbar = new CrudToolbarPanel("Thêm", "Xóa", "Sửa", "Chi tiết", "In PDF", "Xuất excel");
        searchPanel = new SearchPanel("0 hóa đơn", "Mã", "Mã KH", "Mã NV");
        searchPanel.setSortOptions(
            "Mã mới nhất",
            "Mã cũ nhất",
            "Thành tiền tăng dần",
            "Thành tiền giảm dần",
            "Thời gian mới nhất",
            "Thời gian cũ nhất"
        );

        buildTopBar(toolbar, searchPanel);
        beautifyToolbar();

        initUI();
        bindEvents();
        applyRolePermissions();
        loadData();
    }

    private void beautifyToolbar() {
        String[] names = {"Thêm", "Xóa", "Sửa", "Chi tiết", "In PDF", "Xuất excel"};
        Color[] colors = {
            new Color(46, 204, 113),
            new Color(255, 99, 99),
            new Color(240, 173, 78),
            new Color(91, 192, 222),
            new Color(255, 109, 87),
            new Color(55, 196, 108)
        };

        for (int i = 0; i < names.length; i++) {
            JButton btn = toolbar.getButton(names[i]);
            if (btn != null) {
                btn.setFocusPainted(false);
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.setForeground(Color.WHITE);
                btn.setBackground(colors[i]);
                btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            }
        }
    }

    private void initUI() {
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.setBackground(AppTheme.BG_MAIN);

        centerPanel.add(createTopTablePanel(), BorderLayout.NORTH);
        centerPanel.add(createBottomPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 390));

        String[] cols = {
            "Mã hóa đơn", "Mã KH", "Tên khách hàng", "Mã NV", "Tên nhân viên",
            "Tổng tiền", "Tiền giảm", "Thành tiền", "Thời gian", "KM"
        };

        modelHoaDon = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(modelHoaDon);
        tblHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHoaDon.setRowHeight(25);
        tblHoaDon.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblHoaDon.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblHoaDon.getColumnModel().getColumn(4).setPreferredWidth(210);
        tblHoaDon.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblHoaDon.getColumnModel().getColumn(8).setPreferredWidth(130);
        tblHoaDon.getColumnModel().getColumn(9).setPreferredWidth(90);

        panel.add(createStyledTable(tblHoaDon), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.add(createInfoPanel(), BorderLayout.WEST);
        panel.add(createDetailPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(420, 100));

        JLabel lblTitle = new JLabel("Thông tin hóa đơn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrap.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(105, 205, 105), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        txtMaHoaDon = createReadOnlyField();
        txtKhachHang = createReadOnlyField();
        txtNhanVien = createReadOnlyField();
        txtThoiGian = createReadOnlyField();
        txtTongTien = createReadOnlyField();
        txtTienGiam = createReadOnlyField();
        txtThanhTien = createReadOnlyField();
        txtKhuyenMai = createReadOnlyField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Mã hóa đơn", txtMaHoaDon);
        addFormRow(form, gbc, 1, "Khách hàng", txtKhachHang);
        addFormRow(form, gbc, 2, "Nhân viên", txtNhanVien);
        addFormRow(form, gbc, 3, "Thời gian", txtThoiGian);
        addFormRow(form, gbc, 4, "Tổng tiền", txtTongTien);
        addFormRow(form, gbc, 5, "Tiền giảm", txtTienGiam);
        addFormRow(form, gbc, 6, "Thành tiền", txtThanhTien);
        addFormRow(form, gbc, 7, "Khuyến mãi", txtKhuyenMai);

        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createDetailPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JLabel lblTitle = new JLabel("Chi tiết hóa đơn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrap.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(24);
        tblChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(140);

        wrap.add(createStyledTable(tblChiTiet), BorderLayout.CENTER);
        return wrap;
    }

    private JTextField createReadOnlyField() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setFont(new Font("Arial", Font.PLAIN, 15));
        txt.setBackground(Color.WHITE);
        txt.setPreferredSize(new Dimension(230, 34));
        return txt;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void bindEvents() {
        JButton btnThem = toolbar.getButton("Thêm");
        JButton btnXoa = toolbar.getButton("Xóa");
        JButton btnSua = toolbar.getButton("Sửa");
        JButton btnChiTiet = toolbar.getButton("Chi tiết");
        JButton btnInPdf = toolbar.getButton("In PDF");
        JButton btnXuatExcel = toolbar.getButton("Xuất excel");

        if (btnThem != null) {
            btnThem.addActionListener(e -> openAddDialog());
        }

        if (btnXoa != null) {
            btnXoa.addActionListener(e -> {
                int row = tblHoaDon.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa.");
                    return;
                }

                String maHD = String.valueOf(tblHoaDon.getValueAt(row, 0));
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa hóa đơn " + maHD + " không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (hoaDonDAO.delete(maHD)) {
                        JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công.");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại.");
                    }
                }
            });
        }

        if (btnSua != null) {
            btnSua.addActionListener(e -> openEditDialog());
        }

        if (btnChiTiet != null) {
            btnChiTiet.addActionListener(e -> openDetailDialog());
        }

        if (btnInPdf != null) {
            btnInPdf.addActionListener(e -> {
                int row = tblHoaDon.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để in PDF.");
                    return;
                }

                ExportUtils.exportHoaDonPdf(
                        this,
                        txtMaHoaDon.getText().trim(),
                        txtKhachHang.getText().trim(),
                        txtNhanVien.getText().trim(),
                        txtThoiGian.getText().trim(),
                        txtTongTien.getText().trim(),
                        txtTienGiam.getText().trim(),
                        txtThanhTien.getText().trim(),
                        txtKhuyenMai.getText().trim(),
                        tblChiTiet
                );
            });
        }

        if (btnXuatExcel != null) {
            btnXuatExcel.addActionListener(e ->
                    ExportUtils.exportTableToCsv(this, tblHoaDon, "danh_sach_hoa_don"));
        }

        if (searchPanel.getBtnRefresh() != null) {
            searchPanel.getBtnRefresh().addActionListener(e -> searchData());
        }

        if (searchPanel.getBtnReset() != null) {
            searchPanel.getBtnReset().addActionListener(e -> {
                searchPanel.getTxtKeyword().setText("");
                loadData();
            });
        }

        if (searchPanel.getTxtKeyword() != null) {
            searchPanel.getTxtKeyword().addActionListener(e -> searchData());
        }

        if (searchPanel.getCboSort() != null) {
            searchPanel.getCboSort().addActionListener(e -> searchData());
        }

        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedInvoice();
            }
        });
    }

    private void applyRolePermissions() {
        if (!AppSession.isAdmin()) {
            JButton btnXoa = toolbar.getButton("Xóa");
            JButton btnSua = toolbar.getButton("Sửa");

            if (btnXoa != null) btnXoa.setEnabled(false);
            if (btnSua != null) btnSua.setEnabled(false);
        }
    }

    private void loadData() {
        List<Object[]> data;
        if (AppSession.isAdmin()) {
            data = new ArrayList<>(hoaDonDAO.findAllForTable());
        } else {
            data = new ArrayList<>(hoaDonDAO.findAllForTableByNhanVien(AppSession.getMaNhanVien()));
        }
        sortData(data);
        fillHoaDonTable(data);
    }

    private void searchData() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = searchPanel.getSelectedRadioText();
        List<Object[]> data;

        if (keyword.isEmpty()) {
            loadData();
        } else {
            if (AppSession.isAdmin()) {
                data = new ArrayList<>(hoaDonDAO.search(field, keyword));
            } else {
                data = new ArrayList<>(hoaDonDAO.searchByNhanVien(field, keyword, AppSession.getMaNhanVien()));
            }
            sortData(data);
            fillHoaDonTable(data);
        }
    }

    private void sortData(List<Object[]> data) {
        String option = String.valueOf(searchPanel.getCboSort().getSelectedItem());
        switch (option) {
            case "Mã cũ nhất":
                data.sort((a, b) -> PanelSortUtils.compareCode(a[0], b[0]));
                break;
            case "Thành tiền tăng dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(a[7], b[7]));
                break;
            case "Thành tiền giảm dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(b[7], a[7]));
                break;
            case "Thời gian mới nhất":
                data.sort((a, b) -> PanelSortUtils.compareText(b[8], a[8]));
                break;
            case "Thời gian cũ nhất":
                data.sort((a, b) -> PanelSortUtils.compareText(a[8], b[8]));
                break;
            default:
                data.sort((a, b) -> PanelSortUtils.compareCode(b[0], a[0]));
                break;
        }
    }

    private void fillHoaDonTable(List<Object[]> list) {
        modelHoaDon.setRowCount(0);

        for (Object[] row : list) {
            modelHoaDon.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4],
                moneyFormat.format(toDouble(row[5])),
                moneyFormat.format(toDouble(row[6])),
                moneyFormat.format(toDouble(row[7])),
                row[8], row[9]
            });
        }

        searchPanel.getLblResult().setText(list.size() + " hóa đơn");
        clearInfo();
        modelChiTiet.setRowCount(0);

        if (tblHoaDon.getRowCount() > 0) {
            tblHoaDon.setRowSelectionInterval(0, 0);
        }
    }

    private void showSelectedInvoice() {
        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            clearInfo();
            modelChiTiet.setRowCount(0);
            return;
        }

        String maHD = String.valueOf(tblHoaDon.getValueAt(row, 0));
        txtMaHoaDon.setText(maHD);
        txtKhachHang.setText(tblHoaDon.getValueAt(row, 1) + " - " + tblHoaDon.getValueAt(row, 2));
        txtNhanVien.setText(tblHoaDon.getValueAt(row, 3) + " - " + tblHoaDon.getValueAt(row, 4));
        txtTongTien.setText(String.valueOf(tblHoaDon.getValueAt(row, 5)));
        txtTienGiam.setText(String.valueOf(tblHoaDon.getValueAt(row, 6)));
        txtThanhTien.setText(String.valueOf(tblHoaDon.getValueAt(row, 7)));
        txtThoiGian.setText(String.valueOf(tblHoaDon.getValueAt(row, 8)));
        txtKhuyenMai.setText(String.valueOf(tblHoaDon.getValueAt(row, 9)));

        modelChiTiet.setRowCount(0);
        List<Object[]> details = hoaDonDAO.findDetailsByHoaDon(maHD);
        for (Object[] d : details) {
            modelChiTiet.addRow(new Object[]{
                d[0], d[1], d[2],
                moneyFormat.format(toDouble(d[3])),
                moneyFormat.format(toDouble(d[4]))
            });
        }
    }

    private void clearInfo() {
        txtMaHoaDon.setText("");
        txtKhachHang.setText("");
        txtNhanVien.setText("");
        txtThoiGian.setText("");
        txtTongTien.setText("");
        txtTienGiam.setText("");
        txtThanhTien.setText("");
        txtKhuyenMai.setText("");
    }

    private void openAddDialog() {
        AddInvoiceDialog dialog = new AddInvoiceDialog(getParentFrame(), hoaDonDAO);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openEditDialog() {
        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để sửa.");
            return;
        }

        String maHD = String.valueOf(tblHoaDon.getValueAt(row, 0));
        EditInvoiceDialog dialog = new EditInvoiceDialog(getParentFrame(), hoaDonDAO, maHD);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openDetailDialog() {
        int row = tblHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết.");
            return;
        }

        String maHD = String.valueOf(tblHoaDon.getValueAt(row, 0));
        InvoiceDetailDialog dialog = new InvoiceDetailDialog(getParentFrame(), hoaDonDAO, maHD);
        dialog.setVisible(true);
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private double toDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(value).replace(",", "").trim());
    }

    private static class AddInvoiceDialog extends JDialog {

        private final HoaDonDAO hoaDonDAO;
        private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

        protected boolean saved = false;
        protected JTextField txtMaHoaDon;
        protected JComboBox<String> cboKhachHang;
        protected JComboBox<String> cboNhanVien;
        protected JComboBox<String> cboKhuyenMai;
        protected JTextField txtThoiGian;
        protected JTextField txtTongTien;
        protected JTextField txtTienGiam;
        protected JTextField txtThanhTien;
        protected JTable tblItems;
        protected DefaultTableModel itemModel;

        AddInvoiceDialog(Frame owner, HoaDonDAO hoaDonDAO) {
            super(owner, "Thêm hóa đơn", true);
            this.hoaDonDAO = hoaDonDAO;

            setLayout(new BorderLayout(12, 12));
            setSize(1280, 780);
            setLocationRelativeTo(owner);

            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
            add(root, BorderLayout.CENTER);

            root.add(createTopForm(), BorderLayout.NORTH);
            root.add(createTableArea(), BorderLayout.CENTER);
            root.add(createBottomActions(), BorderLayout.SOUTH);
        }

        private JPanel createTopForm() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 210)),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));
            form.setBackground(new Color(248, 248, 248));

            txtMaHoaDon = new JTextField(hoaDonDAO.generateNextMaHoaDon());
            cboKhachHang = new JComboBox<>();
            cboNhanVien = new JComboBox<>();
            cboKhuyenMai = new JComboBox<>();
            txtThoiGian = new JTextField(LocalDate.now().toString());
            txtTongTien = new JTextField("0");
            txtTienGiam = new JTextField("0");
            txtThanhTien = new JTextField("0");

            txtMaHoaDon.setEditable(false);
            txtTongTien.setEditable(false);
            txtTienGiam.setEditable(false);
            txtThanhTien.setEditable(false);

            txtMaHoaDon.setPreferredSize(new Dimension(250, 34));
            cboKhachHang.setPreferredSize(new Dimension(300, 34));
            cboNhanVien.setPreferredSize(new Dimension(250, 34));
            cboKhuyenMai.setPreferredSize(new Dimension(250, 34));
            txtThoiGian.setPreferredSize(new Dimension(220, 34));
            txtTongTien.setPreferredSize(new Dimension(180, 34));
            txtTienGiam.setPreferredSize(new Dimension(180, 34));
            txtThanhTien.setPreferredSize(new Dimension(180, 34));

            for (String item : hoaDonDAO.getKhachHangOptions()) cboKhachHang.addItem(item);

            if (AppSession.isAdmin()) {
                for (String item : hoaDonDAO.getNhanVienOptions()) cboNhanVien.addItem(item);
            } else {
                String current = AppSession.getCurrentUser() == null
                        ? AppSession.getMaNhanVien()
                        : AppSession.getCurrentUser().getMaNhanVien() + " - "
                        + AppSession.getCurrentUser().getHoTen();
                cboNhanVien.addItem(current);
                cboNhanVien.setSelectedIndex(0);
                cboNhanVien.setEnabled(false);
            }

            cboKhuyenMai.addItem("(Không áp dụng)");
            for (String item : hoaDonDAO.getKhuyenMaiOptions()) cboKhuyenMai.addItem(item);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addFormCell(form, gbc, 0, 0, "Mã hóa đơn", txtMaHoaDon);
            addFormCell(form, gbc, 2, 0, "Khách hàng", cboKhachHang);
            addFormCell(form, gbc, 4, 0, "Nhân viên", cboNhanVien);

            addFormCell(form, gbc, 0, 1, "Khuyến mãi", cboKhuyenMai);
            addFormCell(form, gbc, 2, 1, "Thời gian (yyyy-MM-dd)", txtThoiGian);
            addFormCell(form, gbc, 4, 1, "Tổng tiền", txtTongTien);

            addFormCell(form, gbc, 0, 2, "Tiền giảm", txtTienGiam);
            addFormCell(form, gbc, 2, 2, "Thành tiền", txtThanhTien);

            cboKhuyenMai.addActionListener(e -> recalcTotals());

            return form;
        }

        private JPanel createTableArea() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
            itemModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0 || column == 2;
                }
            };

            tblItems = new JTable(itemModel);
            tblItems.setRowHeight(28);
            tblItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JComboBox<String> cboSanPham = new JComboBox<>();
            for (String item : hoaDonDAO.getSanPhamOptions()) {
                cboSanPham.addItem(item);
            }
            tblItems.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cboSanPham));

            tblItems.getColumnModel().getColumn(0).setPreferredWidth(240);
            tblItems.getColumnModel().getColumn(1).setPreferredWidth(280);
            tblItems.getColumnModel().getColumn(2).setPreferredWidth(120);
            tblItems.getColumnModel().getColumn(3).setPreferredWidth(150);
            tblItems.getColumnModel().getColumn(4).setPreferredWidth(170);

            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            tblItems.getColumnModel().getColumn(2).setCellRenderer(center);
            tblItems.getColumnModel().getColumn(3).setCellRenderer(center);
            tblItems.getColumnModel().getColumn(4).setCellRenderer(center);

            itemModel.addRow(new Object[]{"", "", 1, "0", "0"});

            tblItems.addPropertyChangeListener(evt -> {
                if ("tableCellEditor".equals(evt.getPropertyName()) && !tblItems.isEditing()) {
                    SwingUtilities.invokeLater(this::refreshAllRows);
                }
            });

            panel.add(new JScrollPane(tblItems), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createBottomActions() {
            JPanel wrap = new JPanel(new BorderLayout());

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            JButton btnThemDong = createActionButton("Thêm dòng", new Color(70, 130, 180));
            JButton btnXoaDong = createActionButton("Xóa dòng", new Color(220, 80, 80));
            JButton btnTinhTien = createActionButton("Tính tiền", new Color(46, 139, 87));

            left.add(btnThemDong);
            left.add(btnXoaDong);
            left.add(btnTinhTien);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            JButton btnLuu = createActionButton("Lưu", new Color(46, 204, 113));
            JButton btnHuy = createActionButton("Hủy", new Color(120, 120, 120));

            right.add(btnLuu);
            right.add(btnHuy);

            wrap.add(left, BorderLayout.WEST);
            wrap.add(right, BorderLayout.EAST);

            btnThemDong.addActionListener(e -> itemModel.addRow(new Object[]{"", "", 1, "0", "0"}));

            btnXoaDong.addActionListener(e -> {
                int row = tblItems.getSelectedRow();
                if (row >= 0) {
                    itemModel.removeRow(row);
                    recalcTotals();
                }
            });

            btnTinhTien.addActionListener(e -> refreshAllRows());
            btnLuu.addActionListener(e -> saveInvoice());
            btnHuy.addActionListener(e -> dispose());

            return wrap;
        }

        private JButton createActionButton(String text, Color bg) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(bg);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(120, 36));
            return btn;
        }

        private void addFormCell(JPanel panel, GridBagConstraints gbc, int x, int y, String label, Component comp) {
            gbc.gridx = x;
            gbc.gridy = y;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(lbl, gbc);

            gbc.gridx = x + 1;
            gbc.weightx = 1;
            panel.add(comp, gbc);
        }

        protected void refreshAllRows() {
            for (int i = 0; i < itemModel.getRowCount(); i++) {
                updateRow(i);
            }
            recalcTotals();
        }

        private void updateRow(int row) {
            Object maSpObj = itemModel.getValueAt(row, 0);
            if (maSpObj == null) return;

            String maSP = extractCode(String.valueOf(maSpObj));
            if (maSP.isBlank()) return;

            String tenSP = hoaDonDAO.getTenSanPham(maSP);
            double donGia = hoaDonDAO.getGiaSanPham(maSP);

            int soLuong = 1;
            try {
                Object slObj = itemModel.getValueAt(row, 2);
                if (slObj != null && !String.valueOf(slObj).trim().isEmpty()) {
                    soLuong = Integer.parseInt(String.valueOf(slObj).trim());
                }
                if (soLuong < 1) soLuong = 1;
            } catch (Exception e) {
                soLuong = 1;
            }

            double thanhTien = soLuong * donGia;

            itemModel.setValueAt(tenSP, row, 1);
            itemModel.setValueAt(soLuong, row, 2);
            itemModel.setValueAt(moneyFormat.format(donGia), row, 3);
            itemModel.setValueAt(moneyFormat.format(thanhTien), row, 4);
        }

        private void recalcTotals() {
            double tong = 0;

            for (int i = 0; i < itemModel.getRowCount(); i++) {
                Object value = itemModel.getValueAt(i, 4);
                if (value != null && !String.valueOf(value).trim().isEmpty()) {
                    tong += parseMoney(String.valueOf(value));
                }
            }

            double tienGiam = 0;
            String maKM = extractCode(cboKhuyenMai.getSelectedItem());

            if (!"(Không áp dụng)".equals(maKM) && !maKM.isBlank()) {
                double phanTram = hoaDonDAO.getPhanTramKhuyenMai(maKM);
                tienGiam = tong * phanTram / 100.0;
            }

            txtTongTien.setText(moneyFormat.format(tong));
            txtTienGiam.setText(moneyFormat.format(tienGiam));
            txtThanhTien.setText(moneyFormat.format(tong - tienGiam));
        }

        protected void saveInvoice(){
            try {
                refreshAllRows();

                String maHD = txtMaHoaDon.getText().trim();
                if (maHD.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo mã hóa đơn. Vui lòng thử lại.");
                    return;
                }

                if (hoaDonDAO.exists(maHD)) {
                    maHD = hoaDonDAO.generateNextMaHoaDon();
                    txtMaHoaDon.setText(maHD);
                    if (hoaDonDAO.exists(maHD)) {
                        JOptionPane.showMessageDialog(this, "Mã hóa đơn đang bị trùng. Vui lòng thử lưu lại.");
                        return;
                    }
                }

                if (itemModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Hóa đơn chưa có sản phẩm.");
                    return;
                }

                for (int i = 0; i < itemModel.getRowCount(); i++) {
                    String maSP = extractCode(itemModel.getValueAt(i, 0));
                    if (maSP.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Dòng " + (i + 1) + " chưa chọn mã sản phẩm.");
                        return;
                    }
                }

                String maKH = extractCode(cboKhachHang.getSelectedItem());
                String maNV = extractCode(cboNhanVien.getSelectedItem());
                String maKM = extractCode(cboKhuyenMai.getSelectedItem());
                if ("(Không áp dụng)".equals(maKM)) {
                    maKM = null;
                }

                boolean ok = hoaDonDAO.insertHoaDon(
                        maHD,
                        maKH,
                        maNV,
                        txtThoiGian.getText().trim(),
                        maKM,
                        txtTongTien.getText().replace(",", ""),
                        txtTienGiam.getText().replace(",", ""),
                        itemModel
                );

                if (ok) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        }

        protected String extractCode(Object obj){
            if (obj == null) return "";
            String s = String.valueOf(obj).trim();  
            int idx = s.indexOf(" - ");
            return idx >= 0 ? s.substring(0, idx).trim() : s;
        }

        private double parseMoney(String s) {
            return Double.parseDouble(s.replace(",", "").trim());
        }

        public boolean isSaved() {
            return saved;
        }
    }

    private static class EditInvoiceDialog extends AddInvoiceDialog {

    private final HoaDonDAO hoaDonDAO;
    private final String maHoaDon;

    EditInvoiceDialog(Frame owner, HoaDonDAO hoaDonDAO, String maHoaDon) {
        super(owner, hoaDonDAO);
        this.hoaDonDAO = hoaDonDAO;
        this.maHoaDon = maHoaDon;

        setTitle("Sửa hóa đơn");
        loadDataForEdit();
    }

    private void loadDataForEdit() {
            txtMaHoaDon.setText(maHoaDon);
            txtMaHoaDon.setEditable(false);

            List<Object[]> ds = hoaDonDAO.findAllForTable();
            for (Object[] row : ds) {
                if (maHoaDon.equals(String.valueOf(row[0]))) {
                    selectComboItem(cboKhachHang, String.valueOf(row[1]));
                    selectComboItem(cboNhanVien, String.valueOf(row[3]));
                    txtTongTien.setText(String.valueOf(row[5]));
                    txtTienGiam.setText(String.valueOf(row[6]));
                    txtThanhTien.setText(String.valueOf(row[7]));
                    txtThoiGian.setText(String.valueOf(row[8]));
                    selectComboItem(cboKhuyenMai, String.valueOf(row[9]));
                    break;
                }
            }

            itemModel.setRowCount(0);
            List<Object[]> details = hoaDonDAO.findDetailsByHoaDon(maHoaDon);
            for (Object[] d : details) {
                itemModel.addRow(new Object[]{
                        d[0] + " - " + d[1],
                        d[1],
                        d[2],
                        d[3],
                        d[4]
                });
            }

            refreshAllRows();
        }

        private void selectComboItem(JComboBox<String> combo, String code) {
            if (code == null || code.trim().isEmpty()) {
                combo.setSelectedIndex(0);
                return;
            }

            for (int i = 0; i < combo.getItemCount(); i++) {
                String item = combo.getItemAt(i);
                if (item != null && item.startsWith(code + " - ")) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }

        @Override
        protected void saveInvoice() {
            try {
                refreshAllRows();

                String maKH = extractCode(cboKhachHang.getSelectedItem());
                String maNV = extractCode(cboNhanVien.getSelectedItem());
                String maKM = cboKhuyenMai.getSelectedIndex() <= 0
                        ? null
                        : extractCode(cboKhuyenMai.getSelectedItem());

                boolean ok = hoaDonDAO.updateHoaDon(
                        maHoaDon,
                        maKH,
                        maNV,
                        txtThoiGian.getText().trim(),
                        maKM,
                        txtTongTien.getText().replace(",", ""),
                        txtTienGiam.getText().replace(",", ""),
                        itemModel
                );

                if (ok) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thất bại.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    private static class InvoiceDetailDialog extends JDialog {

        InvoiceDetailDialog(Frame owner, HoaDonDAO hoaDonDAO, String maHoaDon) {
            super(owner, "Chi tiết hóa đơn", true);
            setSize(980, 650);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));

            JPanel info = new JPanel(new GridBagLayout());
            info.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 210)),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            info.setBackground(Color.WHITE);

            JTextField txtMaHD = createReadOnly();
            JTextField txtKH = createReadOnly();
            JTextField txtNV = createReadOnly();
            JTextField txtTime = createReadOnly();
            JTextField txtTong = createReadOnly();
            JTextField txtGiam = createReadOnly();
            JTextField txtThanh = createReadOnly();
            JTextField txtKM = createReadOnly();

            List<Object[]> ds = hoaDonDAO.findAllForTable();
            for (Object[] row : ds) {
                if (maHoaDon.equals(String.valueOf(row[0]))) {
                    txtMaHD.setText(String.valueOf(row[0]));
                    txtKH.setText(String.valueOf(row[1]) + " - " + String.valueOf(row[2]));
                    txtNV.setText(String.valueOf(row[3]) + " - " + String.valueOf(row[4]));
                    txtTong.setText(String.valueOf(row[5]));
                    txtGiam.setText(String.valueOf(row[6]));
                    txtThanh.setText(String.valueOf(row[7]));
                    txtTime.setText(String.valueOf(row[8]));
                    txtKM.setText(String.valueOf(row[9]));
                    break;
                }
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addRow(info, gbc, 0, "Mã hóa đơn", txtMaHD);
            addRow(info, gbc, 1, "Khách hàng", txtKH);
            addRow(info, gbc, 2, "Nhân viên", txtNV);
            addRow(info, gbc, 3, "Thời gian", txtTime);
            addRow(info, gbc, 4, "Tổng tiền", txtTong);
            addRow(info, gbc, 5, "Tiền giảm", txtGiam);
            addRow(info, gbc, 6, "Thành tiền", txtThanh);
            addRow(info, gbc, 7, "Khuyến mãi", txtKM);

            String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable table = new JTable(model);
            table.setRowHeight(26);

            List<Object[]> details = hoaDonDAO.findDetailsByHoaDon(maHoaDon);
            for (Object[] d : details) {
                model.addRow(new Object[]{d[0], d[1], d[2], d[3], d[4]});
            }

            JButton btnDong = new JButton("Đóng");
            btnDong.addActionListener(e -> dispose());

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.add(btnDong);

            add(info, BorderLayout.NORTH);
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);
        }

        private static JTextField createReadOnly() {
            JTextField txt = new JTextField();
            txt.setEditable(false);
            txt.setBackground(Color.WHITE);
            txt.setPreferredSize(new Dimension(260, 32));
            return txt;
        }

        private static void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            panel.add(new JLabel(label), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panel.add(field, gbc);
        }
    }

}
