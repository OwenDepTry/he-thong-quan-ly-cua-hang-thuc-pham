package service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import dao.SanPhamDAO;
import entity.ChiTietPNhap;
import entity.PhieuNhapHang;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PdfPhieuNhapService {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public void exportPhieuNhapToPDF(PhieuNhapHang pn, List<ChiTietPNhap> dsChiTiet, String filePath) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        BaseFont baseFont = loadUnicodeFont();

        Font shopFont = new Font(baseFont, 15, Font.BOLD, new BaseColor(46, 125, 50));
        Font titleFont = new Font(baseFont, 20, Font.BOLD);
        Font normalFont = new Font(baseFont, 11, Font.NORMAL);
        Font boldFont = new Font(baseFont, 11, Font.BOLD);
        Font totalFont = new Font(baseFont, 13, Font.BOLD, new BaseColor(21, 101, 192));
        Font italicFont = new Font(baseFont, 11, Font.ITALIC, BaseColor.DARK_GRAY);

        addStoreHeader(document, shopFont, normalFont);
        addTitle(document, "PHIẾU NHẬP HÀNG", titleFont);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 1f});
        infoTable.setSpacingAfter(12);

        addInfoCell(infoTable, "Mã phiếu nhập: " + safe(pn.getMaPhieu()), normalFont);
        addInfoCell(infoTable, "Mã nhà cung cấp: " + safe(pn.getMaNCCap()), normalFont);
        addInfoCell(infoTable, "Người nhập: " + safe(pn.getNguoiNhap()), normalFont);
        addInfoCell(infoTable, "Thời gian nhập: " + formatDateTime(pn.getThoiGian()), normalFont);

        document.add(infoTable);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 3.8f, 1.4f, 2.0f, 2.2f});
        table.setSpacingAfter(14);

        addHeaderCell(table, "Mã SP", boldFont);
        addHeaderCell(table, "Tên sản phẩm", boldFont);
        addHeaderCell(table, "Số lượng", boldFont);
        addHeaderCell(table, "Đơn giá nhập", boldFont);
        addHeaderCell(table, "Thành tiền", boldFont);

        int tongTien = 0;

        for (ChiTietPNhap ct : dsChiTiet) {
            String tenSP = sanPhamDAO.getTenSanPhamByMa(ct.getMaSP());

            addBodyCell(table, safe(ct.getMaSP()), normalFont, Element.ALIGN_CENTER);
            addBodyCell(table, safe(tenSP), normalFont, Element.ALIGN_LEFT);
            addBodyCell(table, String.valueOf(ct.getSoLuong()), normalFont, Element.ALIGN_CENTER);
            addBodyCell(table, formatMoney(ct.getDonGia()), normalFont, Element.ALIGN_RIGHT);
            addBodyCell(table, formatMoney(ct.getThanhTien()), normalFont, Element.ALIGN_RIGHT);

            tongTien += ct.getThanhTien();
        }

        document.add(table);

        PdfPTable moneyTable = new PdfPTable(2);
        moneyTable.setWidthPercentage(45);
        moneyTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        moneyTable.setWidths(new float[]{1.3f, 1f});
        moneyTable.setSpacingAfter(18);

        addMoneyRow(moneyTable, "Tổng tiền nhập:", formatMoney(tongTien), boldFont, totalFont);
        document.add(moneyTable);

        Paragraph note = new Paragraph("Phiếu nhập dùng để xác nhận hàng hóa đã được nhập vào kho.", italicFont);
        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingAfter(26);
        document.add(note);

        addSignArea(document, boldFont, normalFont, "Người giao hàng", "Người nhập hàng");

        document.close();
    }

    private BaseFont loadUnicodeFont() throws Exception {
        String[] fontPaths = {
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/tahoma.ttf",
            "C:/Windows/Fonts/times.ttf"
        };

        for (String path : fontPaths) {
            File f = new File(path);
            if (f.exists()) {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        }

        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }

    private void addStoreHeader(Document document, Font shopFont, Font normalFont) throws DocumentException {
        Paragraph shopName = new Paragraph("GREEN MART", shopFont);
        shopName.setAlignment(Element.ALIGN_CENTER);
        shopName.setSpacingAfter(4);

        Paragraph shopInfo = new Paragraph(
                "Hệ thống quản lý cửa hàng thực phẩm\nĐịa chỉ: TP. Hồ Chí Minh | Hotline: 0900 000 001",
                normalFont
        );
        shopInfo.setAlignment(Element.ALIGN_CENTER);
        shopInfo.setSpacingAfter(10);

        document.add(shopName);
        document.add(shopInfo);
    }

    private void addTitle(Document document, String text, Font titleFont) throws DocumentException {
        Paragraph title = new Paragraph(text, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(16);
        document.add(title);
    }

    private void addSignArea(Document document, Font boldFont, Font normalFont, String leftTitle, String rightTitle) throws DocumentException {
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[]{1f, 1f});

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p1 = new Paragraph(leftTitle, boldFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph("(Ký và ghi rõ họ tên)", normalFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        Paragraph p3 = new Paragraph("\n\n\n");

        left.addElement(p1);
        left.addElement(p3);
        left.addElement(p2);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p4 = new Paragraph(rightTitle, boldFont);
        p4.setAlignment(Element.ALIGN_CENTER);
        Paragraph p5 = new Paragraph("(Ký và ghi rõ họ tên)", normalFont);
        p5.setAlignment(Element.ALIGN_CENTER);
        Paragraph p6 = new Paragraph("\n\n\n");

        right.addElement(p4);
        right.addElement(p6);
        right.addElement(p5);

        signTable.addCell(left);
        signTable.addCell(right);

        document.add(signTable);
    }

    private void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(232, 245, 233));
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addMoneyRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setPadding(5);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setPadding(5);

        table.addCell(cell1);
        table.addCell(cell2);
    }

    private String formatMoney(int amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VNĐ";
    }

    private String formatDateTime(java.sql.Timestamp ts) {
        if (ts == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ts);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}