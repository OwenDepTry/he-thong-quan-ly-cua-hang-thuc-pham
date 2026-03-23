package ui;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportUtils {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0");

    private ExportUtils() {
    }

    public static void exportTableToCsv(Component parent, JTable table, String defaultName) {
        if (table == null || table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất Excel.");
            return;
        }

        File file = chooseSaveFile(parent, defaultName, "csv", "Excel/CSV (*.csv)");
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write('\uFEFF');

            for (int c = 0; c < table.getColumnCount(); c++) {
                writer.print(escapeCsv(table.getColumnName(c)));
                if (c < table.getColumnCount() - 1) writer.print(",");
            }
            writer.println();

            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    Object value = table.getValueAt(r, c);
                    writer.print(escapeCsv(value == null ? "" : String.valueOf(value)));
                    if (c < table.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
            }

            JOptionPane.showMessageDialog(parent, "Xuất Excel thành công:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Xuất Excel thất bại:\n" + e.getMessage());
        }
    }

    public static void exportHoaDonPdf(
            Component parent,
            String maHoaDon,
            String khachHang,
            String nhanVien,
            String thoiGian,
            String tongTien,
            String tienGiam,
            String thanhTien,
            String khuyenMai,
            JTable tableChiTiet
    ) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn hóa đơn để in PDF.");
            return;
        }

        if (tableChiTiet == null || tableChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu chi tiết hóa đơn để in.");
            return;
        }

        File file = chooseSaveFile(parent, "hoa_don_" + maHoaDon, "pdf", "PDF (*.pdf)");
        if (file == null) return;

        try {
            Document document = new Document(PageSize.A4, 50, 50, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            BaseFont bf = BaseFont.createFont(
                    "C:/Windows/Fonts/arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            Font storeFont = new Font(bf, 20, Font.BOLD, BaseColor.BLACK);
            Font titleFont = new Font(bf, 18, Font.BOLD, BaseColor.BLACK);
            Font sectionFont = new Font(bf, 16, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(bf, 12, Font.NORMAL, BaseColor.BLACK);
            Font boldFont = new Font(bf, 12, Font.BOLD, BaseColor.BLACK);
            Font thankFont = new Font(bf, 18, Font.BOLD, BaseColor.BLACK);

            Paragraph store = new Paragraph("CỬA HÀNG THỰC PHẨM", storeFont);
            store.setAlignment(Element.ALIGN_CENTER);
            store.setSpacingBefore(20);
            store.setSpacingAfter(6);
            document.add(store);

            Paragraph title = new Paragraph("Hóa Đơn Bán Hàng", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            document.add(new Paragraph("Mã hóa đơn: " + safe(maHoaDon), boldFont));
            document.add(new Paragraph("Ngày lập: " + safe(thoiGian), boldFont));
            document.add(new Paragraph("Nhân viên: " + simplifyName(nhanVien), boldFont));
            document.add(new Paragraph("Khách hàng: " + safe(khachHang), boldFont));
            document.add(new Paragraph("Khuyến mãi: " + safeKhuyenMai(khuyenMai), boldFont));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph section = new Paragraph("DANH SÁCH SẢN PHẨM MUA", sectionFont);
            section.setAlignment(Element.ALIGN_CENTER);
            section.setSpacingAfter(12);
            document.add(section);

            PdfPTable pdfTable = new PdfPTable(5);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{3.2f, 1.5f, 2.2f, 1.8f, 2.4f});

            addHeaderCell(pdfTable, "Tên sản phẩm", boldFont);
            addHeaderCell(pdfTable, "Số lượng", boldFont);
            addHeaderCell(pdfTable, "Đơn giá", boldFont);
            addHeaderCell(pdfTable, "Giảm giá", boldFont);
            addHeaderCell(pdfTable, "Thành tiền", boldFont);

            double tong = parseMoney(tongTien);
            double giam = parseMoney(tienGiam);
            int discountPercent = tong > 0 ? (int) Math.round((giam / tong) * 100.0) : 0;

            for (int r = 0; r < tableChiTiet.getRowCount(); r++) {
                String tenSP = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Tên sản phẩm", 1));
                String soLuong = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Số lượng", 2));
                String donGia = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Đơn giá", 3));
                String thanhTienRow = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Thành tiền", 4));

                addBodyCell(pdfTable, tenSP, normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, soLuong, normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, formatMoneyText(donGia), normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, discountPercent + "%", normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, formatMoneyText(thanhTienRow), normalFont, Element.ALIGN_LEFT);
            }

            document.add(pdfTable);
            document.add(Chunk.NEWLINE);

            Paragraph tongP = new Paragraph("Tổng tiền: " + formatMoneyValue(tong) + " VNĐ", boldFont);
            tongP.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongP);

            Paragraph giamP = new Paragraph("Tiền giảm: " + formatMoneyValue(giam) + " VNĐ", boldFont);
            giamP.setAlignment(Element.ALIGN_RIGHT);
            document.add(giamP);

            Paragraph thanhTienP = new Paragraph("Tổng tiền phải trả: " + formatMoneyValue(parseMoney(thanhTien)) + " VNĐ", boldFont);
            thanhTienP.setAlignment(Element.ALIGN_RIGHT);
            thanhTienP.setSpacingAfter(28);
            document.add(thanhTienP);

            Paragraph thank = new Paragraph("XIN CẢM ƠN QUÝ KHÁCH!", thankFont);
            thank.setAlignment(Element.ALIGN_CENTER);
            document.add(thank);

            document.close();
            JOptionPane.showMessageDialog(parent, "Xuất PDF hóa đơn thành công:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Xuất PDF thất bại:\n" + e.getMessage());
        }
    }

    public static void exportPhieuNhapPdf(
            Component parent,
            String maPhieuNhap,
            String nhaCungCap,
            String nhanVien,
            String thoiGian,
            String tongTien,
            JTable tableChiTiet
    ) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn phiếu nhập để in PDF.");
            return;
        }

        if (tableChiTiet == null || tableChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu chi tiết phiếu nhập để in.");
            return;
        }

        File file = chooseSaveFile(parent, "phieu_nhap_" + maPhieuNhap, "pdf", "PDF (*.pdf)");
        if (file == null) return;

        try {
            Document document = new Document(PageSize.A4, 50, 50, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            BaseFont bf = BaseFont.createFont(
                    "C:/Windows/Fonts/arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            Font storeFont = new Font(bf, 20, Font.BOLD, BaseColor.BLACK);
            Font titleFont = new Font(bf, 18, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(bf, 12, Font.NORMAL, BaseColor.BLACK);
            Font boldFont = new Font(bf, 12, Font.BOLD, BaseColor.BLACK);
            Font italicBoldFont = new Font(bf, 12, Font.BOLDITALIC, BaseColor.BLACK);

            Paragraph store = new Paragraph("CỬA HÀNG THỰC PHẨM", storeFont);
            store.setAlignment(Element.ALIGN_CENTER);
            store.setSpacingBefore(20);
            store.setSpacingAfter(6);
            document.add(store);

            Paragraph title = new Paragraph("Phiếu Nhập Hàng", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            document.add(new Paragraph("Mã phiếu: " + safe(maPhieuNhap), boldFont));
            document.add(new Paragraph("Ngày nhập: " + safe(thoiGian), boldFont));
            document.add(new Paragraph("Nhân viên nhập: " + simplifyName(nhanVien), boldFont));
            document.add(new Paragraph("Nhà cung cấp: " + simplifyName(nhaCungCap), boldFont));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{3.0f, 3.0f, 3.0f, 3.0f});

            addHeaderCell(pdfTable, "Mã hàng", boldFont);
            addHeaderCell(pdfTable, "Tên sản phẩm", boldFont);
            addHeaderCell(pdfTable, "Số lượng", boldFont);
            addHeaderCell(pdfTable, "Đơn giá", boldFont);

            for (int r = 0; r < tableChiTiet.getRowCount(); r++) {
                String maHang = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Mã SP", 0));
                String tenSP = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Tên sản phẩm", 1));
                String soLuong = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Số lượng", 2));
                String donGia = getCell(tableChiTiet, r, findColumn(tableChiTiet, "Đơn giá", 3));

                addBodyCell(pdfTable, maHang, normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, tenSP, normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, soLuong, normalFont, Element.ALIGN_LEFT);
                addBodyCell(pdfTable, formatMoneyText(donGia), normalFont, Element.ALIGN_LEFT);
            }

            document.add(pdfTable);
            document.add(Chunk.NEWLINE);

            Paragraph tongP = new Paragraph("Tổng tiền: " + formatMoneyValue(parseMoney(tongTien)) + " VNĐ", boldFont);
            tongP.setAlignment(Element.ALIGN_RIGHT);
            tongP.setSpacingAfter(30);
            document.add(tongP);

            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setWidths(new float[]{1f, 1f});

            PdfPCell left = createSignatureCell("Chữ ký của nhân viên nhập hàng", italicBoldFont, normalFont);
            PdfPCell right = createSignatureCell("Chữ ký của quản lý", italicBoldFont, normalFont);

            signTable.addCell(left);
            signTable.addCell(right);

            document.add(signTable);

            document.close();
            JOptionPane.showMessageDialog(parent, "Xuất PDF phiếu nhập thành công:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Xuất PDF thất bại:\n" + e.getMessage());
        }
    }

    private static PdfPCell createSignatureCell(String title, Font titleFont, Font normalFont) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Phrase(title, titleFont));
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(new Phrase("Họ và tên", normalFont));

        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(10);
        cell.setMinimumHeight(150);
        return cell;
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(220, 220, 220));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(text), font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static int findColumn(JTable table, String expectedName, int defaultIndex) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i);
            if (name != null && name.trim().equalsIgnoreCase(expectedName)) {
                return i;
            }
        }
        return Math.min(defaultIndex, Math.max(0, table.getColumnCount() - 1));
    }

    private static String getCell(JTable table, int row, int col) {
        if (table == null || row < 0 || row >= table.getRowCount() || col < 0 || col >= table.getColumnCount()) {
            return "";
        }
        Object value = table.getValueAt(row, col);
        return value == null ? "" : String.valueOf(value);
    }

    private static File chooseSaveFile(Component parent, String defaultName, String ext, String desc) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file");
        chooser.setSelectedFile(new File(defaultName + "." + ext));
        chooser.setFileFilter(new FileNameExtensionFilter(desc, ext));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith("." + ext)) {
            file = new File(file.getAbsolutePath() + "." + ext);
        }
        return file;
    }

    private static String escapeCsv(String value) {
        String text = value == null ? "" : value;
        text = text.replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String safeKhuyenMai(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Không có";
        }
        return value.trim();
    }

    private static String simplifyName(String value) {
        if (value == null) return "";
        String s = value.trim();
        int idx = s.indexOf(" - ");
        return idx >= 0 ? s.substring(idx + 3).trim() : s;
    }

    private static double parseMoney(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        String cleaned = s.replace("VNĐ", "")
                .replace("VND", "")
                .replace(",", "")
                .trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String formatMoneyText(String s) {
        return formatMoneyValue(parseMoney(s)) + " VNĐ";
    }

    private static String formatMoneyValue(double value) {
        return MONEY_FORMAT.format(value);
    }
}