package ui;

import dao.PhieuNhapDAO;
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

public class PhieuNhapPanel extends AdminTablePanelBase {

    private final PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JTable tblPhieuNhap;
    private JTable tblChiTiet;
    private DefaultTableModel modelPhieuNhap;
    private DefaultTableModel modelChiTiet;

    private JTextField txtMaPhieuNhap;
    private JTextField txtNhaCungCap;
    private JTextField txtNhanVien;
    private JTextField txtThoiGian;
    private JTextField txtTongTien;

    private CrudToolbarPanel toolbar;
    private SearchPanel searchPanel;

    public PhieuNhapPanel() {
        super();

        toolbar = new CrudToolbarPanel("Thêm", "Xóa", "Sửa", "Chi tiết", "In PDF", "Xuất excel");
        searchPanel = new SearchPanel("0 phiếu nhập", "Mã phiếu", "Mã NCC", "Mã NV");
        searchPanel.setSortOptions(
            "Mã mới nhất",
            "Mã cũ nhất",
            "Tổng tiền tăng dần",
            "Tổng tiền giảm dần",
            "Ngày mới nhất",
            "Ngày cũ nhất"
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
                btn.setForeground(Color.WHITE);
                btn.setBackground(colors[i]);
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
        panel.setPreferredSize(new Dimension(100, 360));

        String[] cols = {
            "Mã phiếu nhập", "Mã NCC", "Tên nhà cung cấp",
            "Mã NV", "Tên nhân viên", "Tổng tiền", "Ngày nhập"
        };

        modelPhieuNhap = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhieuNhap = new JTable(modelPhieuNhap);
        tblPhieuNhap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPhieuNhap.setRowHeight(25);
        tblPhieuNhap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tblPhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(130);
        tblPhieuNhap.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblPhieuNhap.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblPhieuNhap.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblPhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(210);
        tblPhieuNhap.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblPhieuNhap.getColumnModel().getColumn(6).setPreferredWidth(130);

        panel.add(createStyledTable(tblPhieuNhap), BorderLayout.CENTER);
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
        wrap.setPreferredSize(new Dimension(430, 100));

        JLabel lblTitle = new JLabel("Thông tin phiếu nhập", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrap.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(105, 205, 105), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        txtMaPhieuNhap = createReadOnlyField();
        txtNhaCungCap = createReadOnlyField();
        txtNhanVien = createReadOnlyField();
        txtThoiGian = createReadOnlyField();
        txtTongTien = createReadOnlyField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Mã phiếu nhập", txtMaPhieuNhap);
        addFormRow(form, gbc, 1, "Nhà cung cấp", txtNhaCungCap);
        addFormRow(form, gbc, 2, "Nhân viên", txtNhanVien);
        addFormRow(form, gbc, 3, "Ngày nhập", txtThoiGian);
        addFormRow(form, gbc, 4, "Tổng tiền", txtTongTien);

        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createDetailPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JLabel lblTitle = new JLabel("Chi tiết phiếu nhập", SwingConstants.CENTER);
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
        txt.setPreferredSize(new Dimension(240, 34));
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
                int row = tblPhieuNhap.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập để xóa.");
                    return;
                }

                String maPN = String.valueOf(tblPhieuNhap.getValueAt(row, 0));
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa phiếu nhập " + maPN + " không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (phieuNhapDAO.delete(maPN)) {
                        JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thành công.");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa phiếu nhập thất bại.");
                    }
                }
            });
        }

        if (btnSua != null) {
            btnSua.addActionListener(e -> JOptionPane.showMessageDialog(
                    this,
                    "Tạm thời mình đã nối xong chi tiết, PDF và Excel. Nếu cần mình sẽ làm tiếp chức năng sửa phiếu nhập.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            ));
        }

        if (btnChiTiet != null) {
            btnChiTiet.addActionListener(e -> showSelectedPhieuNhap());
        }

        if (btnInPdf != null) {
            btnInPdf.addActionListener(e -> {
                int row = tblPhieuNhap.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập để in PDF.");
                    return;
                }

                ExportUtils.exportPhieuNhapPdf(
                        this,
                        txtMaPhieuNhap.getText().trim(),
                        txtNhaCungCap.getText().trim(),
                        txtNhanVien.getText().trim(),
                        txtThoiGian.getText().trim(),
                        txtTongTien.getText().trim(),
                        tblChiTiet
                );
            });
        }

        if (btnXuatExcel != null) {
            btnXuatExcel.addActionListener(e ->
                    ExportUtils.exportTableToCsv(this, tblPhieuNhap, "danh_sach_phieu_nhap"));
        }

        searchPanel.getBtnRefresh().addActionListener(e -> searchData());

        searchPanel.getBtnReset().addActionListener(e -> {
            searchPanel.getTxtKeyword().setText("");
            loadData();
        });

        searchPanel.getTxtKeyword().addActionListener(e -> searchData());
        searchPanel.getCboSort().addActionListener(e -> searchData());

        tblPhieuNhap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedPhieuNhap();
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
            data = new ArrayList<>(phieuNhapDAO.findAllForTable());
        } else {
            data = new ArrayList<>(phieuNhapDAO.findAllForTableByNhanVien(AppSession.getMaNhanVien()));
        }
        sortData(data);
        fillPhieuNhapTable(data);
    }

    private void searchData() {
        String keyword = searchPanel.getTxtKeyword().getText().trim();
        String field = searchPanel.getSelectedRadioText();
        List<Object[]> data;
        if (keyword.isEmpty()) {
            loadData();
        } else {
            if (AppSession.isAdmin()) {
                data = new ArrayList<>(phieuNhapDAO.search(field, keyword));
            } else {
                data = new ArrayList<>(phieuNhapDAO.searchByNhanVien(field, keyword, AppSession.getMaNhanVien()));
            }
            sortData(data);
            fillPhieuNhapTable(data);
        }
    }

    private void sortData(List<Object[]> data) {
        String option = String.valueOf(searchPanel.getCboSort().getSelectedItem());
        switch (option) {
            case "Mã cũ nhất":
                data.sort((a, b) -> PanelSortUtils.compareCode(a[0], b[0]));
                break;
            case "Tổng tiền tăng dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(a[5], b[5]));
                break;
            case "Tổng tiền giảm dần":
                data.sort((a, b) -> PanelSortUtils.compareNumber(b[5], a[5]));
                break;
            case "Ngày mới nhất":
                data.sort((a, b) -> PanelSortUtils.compareText(b[6], a[6]));
                break;
            case "Ngày cũ nhất":
                data.sort((a, b) -> PanelSortUtils.compareText(a[6], b[6]));
                break;
            default:
                data.sort((a, b) -> PanelSortUtils.compareCode(b[0], a[0]));
                break;
        }
    }

    private void fillPhieuNhapTable(List<Object[]> list) {
        modelPhieuNhap.setRowCount(0);

        for (Object[] row : list) {
            modelPhieuNhap.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4],
                moneyFormat.format(toDouble(row[5])),
                row[6]
            });
        }

        searchPanel.getLblResult().setText(list.size() + " phiếu nhập");
        clearInfo();
        modelChiTiet.setRowCount(0);

        if (tblPhieuNhap.getRowCount() > 0) {
            tblPhieuNhap.setRowSelectionInterval(0, 0);
        }
    }

    private void showSelectedPhieuNhap() {
        int row = tblPhieuNhap.getSelectedRow();
        if (row < 0) {
            clearInfo();
            modelChiTiet.setRowCount(0);
            return;
        }

        String maPN = String.valueOf(tblPhieuNhap.getValueAt(row, 0));
        txtMaPhieuNhap.setText(maPN);
        txtNhaCungCap.setText(tblPhieuNhap.getValueAt(row, 1) + " - " + tblPhieuNhap.getValueAt(row, 2));
        txtNhanVien.setText(tblPhieuNhap.getValueAt(row, 3) + " - " + tblPhieuNhap.getValueAt(row, 4));
        txtTongTien.setText(String.valueOf(tblPhieuNhap.getValueAt(row, 5)));
        txtThoiGian.setText(String.valueOf(tblPhieuNhap.getValueAt(row, 6)));

        modelChiTiet.setRowCount(0);
        List<Object[]> details = phieuNhapDAO.findDetailsByPhieuNhap(maPN);
        for (Object[] d : details) {
            modelChiTiet.addRow(new Object[]{
                d[0], d[1], d[2],
                moneyFormat.format(toDouble(d[3])),
                moneyFormat.format(toDouble(d[4]))
            });
        }
    }

    private void clearInfo() {
        txtMaPhieuNhap.setText("");
        txtNhaCungCap.setText("");
        txtNhanVien.setText("");
        txtThoiGian.setText("");
        txtTongTien.setText("");
    }

    private void openAddDialog() {
        AddPhieuNhapDialog dialog = new AddPhieuNhapDialog(getParentFrame(), phieuNhapDAO);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private double toDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(value).replace(",", "").trim());
    }

    private static class AddPhieuNhapDialog extends JDialog {

        private final PhieuNhapDAO phieuNhapDAO;
        private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
        private boolean saved = false;

        private JTextField txtMaPhieuNhap;
        private JComboBox<String> cboNhaCungCap;
        private JComboBox<String> cboNhanVien;
        private JTextField txtNgayNhap;
        private JTextField txtTongTien;

        private JTable tblItems;
        private DefaultTableModel itemModel;

        AddPhieuNhapDialog(Frame owner, PhieuNhapDAO phieuNhapDAO) {
            super(owner, "Thêm phiếu nhập", true);
            this.phieuNhapDAO = phieuNhapDAO;

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

            txtMaPhieuNhap = new JTextField(phieuNhapDAO.generateNextMaPhieuNhap());
            cboNhaCungCap = new JComboBox<>();
            cboNhanVien = new JComboBox<>();
            txtNgayNhap = new JTextField(LocalDate.now().toString());
            txtTongTien = new JTextField("0");

            txtMaPhieuNhap.setEditable(false);
            txtTongTien.setEditable(false);

            txtMaPhieuNhap.setPreferredSize(new Dimension(250, 34));
            cboNhaCungCap.setPreferredSize(new Dimension(340, 34));
            cboNhanVien.setPreferredSize(new Dimension(300, 34));
            txtNgayNhap.setPreferredSize(new Dimension(220, 34));
            txtTongTien.setPreferredSize(new Dimension(220, 34));

            for (String item : phieuNhapDAO.getNhaCungCapOptions()) {
                cboNhaCungCap.addItem(item);
            }

            if (AppSession.isAdmin()) {
                for (String item : phieuNhapDAO.getNhanVienOptions()) {
                    cboNhanVien.addItem(item);
                }
            } else {
                String current = AppSession.getCurrentUser() == null
                        ? AppSession.getMaNhanVien()
                        : AppSession.getCurrentUser().getMaNhanVien() + " - "
                        + AppSession.getCurrentUser().getHoTen();
                cboNhanVien.addItem(current);
                cboNhanVien.setSelectedIndex(0);
                cboNhanVien.setEnabled(false);
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addFormCell(form, gbc, 0, 0, "Mã phiếu nhập", txtMaPhieuNhap);
            addFormCell(form, gbc, 2, 0, "Nhà cung cấp", cboNhaCungCap);
            addFormCell(form, gbc, 4, 0, "Nhân viên", cboNhanVien);

            addFormCell(form, gbc, 0, 1, "Ngày nhập (yyyy-MM-dd)", txtNgayNhap);
            addFormCell(form, gbc, 2, 1, "Tổng tiền", txtTongTien);

            return form;
        }

        private JPanel createTableArea() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá nhập", "Thành tiền"};
            itemModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0 || column == 2 || column == 3;
                }
            };

            tblItems = new JTable(itemModel);
            tblItems.setRowHeight(28);
            tblItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JComboBox<String> cboSanPham = new JComboBox<>();
            for (String item : phieuNhapDAO.getSanPhamOptions()) {
                cboSanPham.addItem(item);
            }
            tblItems.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cboSanPham));

            tblItems.getColumnModel().getColumn(0).setPreferredWidth(260);
            tblItems.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblItems.getColumnModel().getColumn(2).setPreferredWidth(110);
            tblItems.getColumnModel().getColumn(3).setPreferredWidth(150);
            tblItems.getColumnModel().getColumn(4).setPreferredWidth(170);

            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            tblItems.getColumnModel().getColumn(2).setCellRenderer(center);
            tblItems.getColumnModel().getColumn(3).setCellRenderer(center);
            tblItems.getColumnModel().getColumn(4).setCellRenderer(center);

            itemModel.addRow(new Object[]{"", "", 1, 0, 0});

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

            btnThemDong.addActionListener(e -> itemModel.addRow(new Object[]{"", "", 1, 0, 0}));

            btnXoaDong.addActionListener(e -> {
                int row = tblItems.getSelectedRow();
                if (row >= 0) {
                    itemModel.removeRow(row);
                    recalcTotals();
                }
            });

            btnTinhTien.addActionListener(e -> refreshAllRows());
            btnLuu.addActionListener(e -> savePhieuNhap());
            btnHuy.addActionListener(e -> dispose());

            return wrap;
        }

        private JButton createActionButton(String text, Color bg) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(bg);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(120, 40));
            return btn;
        }

        private void addFormCell(JPanel panel, GridBagConstraints gbc, int x, int y, String label, Component comp) {
            gbc.gridx = x;
            gbc.gridy = y;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            panel.add(lbl, gbc);

            gbc.gridx = x + 1;
            gbc.weightx = 1;
            panel.add(comp, gbc);
        }

        private void refreshAllRows() {
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

            // lấy tên SP
            String tenSP = phieuNhapDAO.getTenSanPham(maSP);

            // số lượng
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

            // đơn giá
            double donGia;
            try {
                Object dgObj = itemModel.getValueAt(row, 3);
                String dg = dgObj == null ? "" : String.valueOf(dgObj).replace(",", "").trim();

                donGia = dg.isEmpty() || "0".equals(dg)
                        ? phieuNhapDAO.getGiaNhapSanPham(maSP)
                        : Double.parseDouble(dg);
            } catch (Exception e) {
                donGia = phieuNhapDAO.getGiaNhapSanPham(maSP);
            }

            // thành tiền
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
                    tong += Double.parseDouble(String.valueOf(value).replace(",", "").trim());
                }
            }

            txtTongTien.setText(moneyFormat.format(tong));
        }

        private void savePhieuNhap() {
            try {
                refreshAllRows();

                String maPN = txtMaPhieuNhap.getText().trim();
                if (maPN.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo mã phiếu nhập. Vui lòng thử lại.");
                    return;
                }

                if (phieuNhapDAO.exists(maPN)) {
                    maPN = phieuNhapDAO.generateNextMaPhieuNhap();
                    txtMaPhieuNhap.setText(maPN);
                    if (phieuNhapDAO.exists(maPN)) {
                        JOptionPane.showMessageDialog(this, "Mã phiếu nhập đang bị trùng. Vui lòng thử lưu lại.");
                        return;
                    }
                }

                if (itemModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm.");
                    return;
                }

                for (int i = 0; i < itemModel.getRowCount(); i++) {
                    String maSP = extractCode(itemModel.getValueAt(i, 0));
                    if (maSP.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Dòng " + (i + 1) + " chưa chọn mã sản phẩm.");
                        return;
                    }
                }

                String maNCC = extractCode(cboNhaCungCap.getSelectedItem());
                String maNV = extractCode(cboNhanVien.getSelectedItem());

                boolean ok = phieuNhapDAO.insertPhieuNhap(
                        maPN,
                        maNCC,
                        maNV,
                        txtNgayNhap.getText().trim(),
                        txtTongTien.getText().replace(",", ""),
                        itemModel
                );

                if (ok) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Thêm phiếu nhập thành công.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thất bại. Kiểm tra console để xem lỗi chi tiết.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        }

        private String extractCode(Object obj) {
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
}   