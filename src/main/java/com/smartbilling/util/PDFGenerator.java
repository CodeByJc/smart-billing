package com.smartbilling.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.smartbilling.model.Invoice;
import com.smartbilling.model.InvoiceItem;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * PDF invoice generator using iTextPDF.
 * Creates professional, printable invoices with product details, GST breakdown,
 * and totals.
 */
public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD,
            new BaseColor(44, 62, 80));
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
            BaseColor.WHITE);
    private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL,
            BaseColor.DARK_GRAY);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,
            BaseColor.DARK_GRAY);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL,
            BaseColor.GRAY);
    private static final Font TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD,
            new BaseColor(39, 174, 96));

    private static final BaseColor PRIMARY_COLOR = new BaseColor(52, 73, 94);
    private static final BaseColor LIGHT_GRAY = new BaseColor(245, 245, 245);

    /**
     * Generate a PDF invoice and write it to the given output stream.
     *
     * @param invoice the invoice data (must include items)
     * @param out     the output stream to write the PDF to
     * @throws Exception if PDF generation fails
     */
    public static void generateInvoice(Invoice invoice, OutputStream out) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(document, out);
        document.open();

        // Company Header
        addCompanyHeader(document);

        // Invoice Info
        addInvoiceInfo(document, invoice);

        // Line separator
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(PRIMARY_COLOR);
        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);

        // Items Table
        addItemsTable(document, invoice);

        // Totals
        addTotals(document, invoice);

        // Footer
        addFooter(document);

        document.close();
    }

    private static void addCompanyHeader(Document document) throws DocumentException {
        Paragraph company = new Paragraph("Smart Billing", TITLE_FONT);
        company.setAlignment(Element.ALIGN_CENTER);
        document.add(company);

        Paragraph subtitle = new Paragraph("Billing & Inventory Management System", SMALL_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(Chunk.NEWLINE);
    }

    private static void addInvoiceInfo(Document document, Invoice invoice) throws DocumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(10);

        // Left column: Customer info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("Bill To:", BOLD_FONT));
        leftCell.addElement(new Paragraph(invoice.getCustomerName(), BODY_FONT));
        leftCell.addElement(new Paragraph("Payment: " + invoice.getPaymentType(), BODY_FONT));
        infoTable.addCell(leftCell);

        // Right column: Invoice info
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph invNum = new Paragraph("Invoice: " + invoice.getInvoiceNumber(), BOLD_FONT);
        invNum.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(invNum);
        Paragraph dateP = new Paragraph("Date: " +
                (invoice.getCreatedAt() != null ? sdf.format(invoice.getCreatedAt()) : "N/A"), BODY_FONT);
        dateP.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(dateP);
        infoTable.addCell(rightCell);

        document.add(infoTable);
    }

    private static void addItemsTable(Document document, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[] { 1, 4, 2, 2, 2, 2, 3 });
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Table headers
        String[] headers = { "#", "Product", "Price", "Qty", "GST %", "GST Amt", "Total" };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(PRIMARY_COLOR);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(PRIMARY_COLOR);
            table.addCell(cell);
        }

        // Table rows
        int index = 1;
        for (InvoiceItem item : invoice.getItems()) {
            BaseColor rowColor = (index % 2 == 0) ? LIGHT_GRAY : BaseColor.WHITE;

            addBodyCell(table, String.valueOf(index), rowColor, Element.ALIGN_CENTER);
            addBodyCell(table,
                    item.getProductName() != null ? item.getProductName() : "Product #" + item.getProductId(),
                    rowColor, Element.ALIGN_LEFT);
            addBodyCell(table, formatCurrency(item.getPrice()), rowColor, Element.ALIGN_RIGHT);
            addBodyCell(table, String.valueOf(item.getQuantity()), rowColor, Element.ALIGN_CENTER);
            addBodyCell(table, item.getGstPercentage() + "%", rowColor, Element.ALIGN_CENTER);
            addBodyCell(table, formatCurrency(item.getGstAmount()), rowColor, Element.ALIGN_RIGHT);
            addBodyCell(table, formatCurrency(item.getTotal()), rowColor, Element.ALIGN_RIGHT);

            index++;
        }

        document.add(table);
    }

    private static void addBodyCell(PdfPTable table, String text, BaseColor bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private static void addTotals(Document document, Invoice invoice) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(50);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setSpacingBefore(10);

        addTotalRow(totalsTable, "Subtotal:", formatCurrency(invoice.getSubtotal()), BOLD_FONT);
        addTotalRow(totalsTable, "GST Amount:", formatCurrency(invoice.getGstAmount()), BOLD_FONT);

        // Grand total
        PdfPCell labelCell = new PdfPCell(new Phrase("Grand Total:", TOTAL_FONT));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setBorderColor(PRIMARY_COLOR);
        labelCell.setPadding(8);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        totalsTable.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatCurrency(invoice.getTotalAmount()), TOTAL_FONT));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setBorderColor(PRIMARY_COLOR);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.addCell(valueCell);

        document.add(totalsTable);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(separator));

        Paragraph footer = new Paragraph("Thank you for your business!", BODY_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);

        Paragraph generated = new Paragraph("Generated by Smart Billing System", SMALL_FONT);
        generated.setAlignment(Element.ALIGN_CENTER);
        document.add(generated);
    }

    private static String formatCurrency(BigDecimal amount) {
        if (amount == null)
            return "₹0.00";
        return "₹" + String.format("%,.2f", amount);
    }
}
