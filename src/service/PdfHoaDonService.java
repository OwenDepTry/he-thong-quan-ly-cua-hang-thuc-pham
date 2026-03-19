package service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.SanPhamDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PdfHoaDonService {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public void exportHoaDonToPDF(HoaDon hoaDon, List<ChiTietHoaDon> dsChiTiet, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font titleFont = new Font(baseFont, 21, Font.BOLD);
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font totalFont = new Font(baseFont, 14, Font.BOLD);
        Font thanksFont = new Font(baseFont, 12, Font.ITALIC);

        Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        // ===== THÔNG TIN HÓA ĐƠN: 2 CỘT CHO ĐỀU =====
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 1f});
        infoTable.setSpacingAfter(15);

        addInfoCell(infoTable, "Mã hóa đơn: " + safe(hoaDon.getMaHD()), normalFont);
        addInfoCell(infoTable, "Mã khách hàng: " + safe(hoaDon.getMaKH()), normalFont);
        addInfoCell(infoTable, "Mã nhân viên: " + safe(hoaDon.getMaNV()), normalFont);
        addInfoCell(infoTable, "Mã khuyến mãi: " + (hoaDon.getMaKM() == null || hoaDon.getMaKM().isBlank() ? "Không áp dụng" : hoaDon.getMaKM()), normalFont);
        addInfoCell(infoTable, "Thời gian: " + String.valueOf(hoaDon.getThoiGian()), normalFont);
        addInfoCell(infoTable, "", normalFont);

        document.add(infoTable);

        // ===== BẢNG CHI TIẾT =====
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4.0f, 1.7f, 2.2f, 2.4f});
        table.setSpacingAfter(18);

        addHeaderCell(table, "Sản phẩm", boldFont);
        addHeaderCell(table, "Số lượng", boldFont);
        addHeaderCell(table, "Đơn giá", boldFont);
        addHeaderCell(table, "Thành tiền", boldFont);

        int tongTienHang = 0;

        for (ChiTietHoaDon ct : dsChiTiet) {
            String tenSP = sanPhamDAO.getTenSanPhamByMa(ct.getMaSP());
            String hienThiSP = ct.getMaSP();
            if (tenSP != null && !tenSP.isBlank()) {
                hienThiSP += " - " + tenSP;
            }

            addBodyCell(table, hienThiSP, normalFont, Element.ALIGN_LEFT);
            addBodyCell(table, String.valueOf(ct.getSoLuong()), normalFont, Element.ALIGN_CENTER);
            addBodyCell(table, formatMoney(ct.getDonGia()), normalFont, Element.ALIGN_RIGHT);
            addBodyCell(table, formatMoney(ct.getThanhTien()), normalFont, Element.ALIGN_RIGHT);

            tongTienHang += ct.getThanhTien();
        }

        document.add(table);

        // ===== KHỐI TỔNG TIỀN GỌN VÀ ĐỀU =====
        PdfPTable moneyTable = new PdfPTable(2);
        moneyTable.setWidthPercentage(60); // hoặc 70 nếu muốn rộng hơn
        moneyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        moneyTable.setWidths(new float[]{1.3f, 1f});
        moneyTable.setSpacingAfter(22);

        addMoneyRow(moneyTable, "Tổng tiền hàng:", formatMoney(tongTienHang), boldFont, normalFont);
        addMoneyRow(moneyTable, "Tiền giảm:", formatMoney(hoaDon.getTienGiam()), boldFont, normalFont);
        addMoneyRow(moneyTable, "Tổng thanh toán:", formatMoney(hoaDon.getTongTien()), totalFont, totalFont);

        document.add(moneyTable);

        Paragraph thanks = new Paragraph("Cảm ơn quý khách đã mua hàng!", thanksFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingAfter(35);
        document.add(thanks);

        // ===== CHỮ KÝ CÂN 2 BÊN =====
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[]{1f, 1f});

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setHorizontalAlignment(Element.ALIGN_CENTER);
        left.setPaddingTop(10);
        Paragraph p1 = new Paragraph("Người bán hàng", boldFont);
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
        right.setPaddingTop(10);
        Paragraph p4 = new Paragraph("Khách hàng", boldFont);
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

        document.close();
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
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
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

    private String safe(String value) {
        return value == null ? "" : value;
    }
}