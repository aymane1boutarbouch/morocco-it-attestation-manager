package com.moroccoit.attestation.service;

import com.moroccoit.attestation.model.*;
import com.moroccoit.attestation.util.DateUtils;
import com.moroccoit.attestation.util.NumberToWordsFR;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PdfGeneratorService {

    private static final PDFont FONT_REGULAR = PDType1Font.HELVETICA;
    private static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont FONT_ITALIC = PDType1Font.HELVETICA_OBLIQUE;

    private static final Color NAVY_PRIMARY = new Color(15, 23, 42);      // #0F172A
    private static final Color GOLD_ACCENT = new Color(217, 119, 6);       // #D97706
    private static final Color SLATE_GRAY = new Color(71, 85, 105);        // #475569
    private static final Color LIGHT_BG = new Color(248, 250, 252);        // #F8FAFC
    private static final Color BORDER_GRAY = new Color(226, 232, 240);     // #E2E8F0

    public static byte[] generatePdf(Employee employee, CompanySettings company, AttestationTemplate template,
                                   String refNumber, String purpose, User user) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            String verifHash = QrCodeService.computeVerificationHash(
                refNumber, employee.getCin(), template.getDocType().name(), LocalDate.now().toString()
            );

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();   // 595.27
                float pageHeight = page.getMediaBox().getHeight(); // 841.89

                // 1. Watermark (if enabled)
                if (company != null && company.isWatermarkEnabled()) {
                    drawWatermark(cs, pageWidth, pageHeight, company.getCompanyName());
                }

                // 2. Decorative Top Accent Bars
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.addRect(0, pageHeight - 8, pageWidth, 8);
                cs.fill();

                cs.setNonStrokingColor(GOLD_ACCENT);
                cs.addRect(0, pageHeight - 12, pageWidth, 4);
                cs.fill();

                // 3. Header Section (Company Branding + Metadata)
                float currentY = pageHeight - 45;

                // Left Header text
                cs.beginText();
                cs.setFont(FONT_BOLD, 18);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(40, currentY);
                cs.showText(company != null ? company.getCompanyName() : "MOROCCO IT S.A.R.L.");
                cs.endText();

                cs.beginText();
                cs.setFont(FONT_REGULAR, 9);
                cs.setNonStrokingColor(SLATE_GRAY);
                cs.newLineAtOffset(40, currentY - 14);
                cs.showText("Cabinet d'Expertise Comptable, Conseil & Ingénierie");
                cs.newLineAtOffset(0, -12);
                cs.showText(company != null ? company.getAddress() + ", " + company.getCity() : "Casablanca, Maroc");
                cs.endText();

                // Right Header Metadata Box
                float rightBoxX = pageWidth - 200;
                cs.setNonStrokingColor(LIGHT_BG);
                cs.addRect(rightBoxX - 10, currentY - 30, 170, 45);
                cs.fill();
                cs.setStrokingColor(BORDER_GRAY);
                cs.setLineWidth(1);
                cs.addRect(rightBoxX - 10, currentY - 30, 170, 45);
                cs.stroke();

                cs.beginText();
                cs.setFont(FONT_BOLD, 9);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(rightBoxX, currentY - 2);
                cs.showText("N° Réf : ");
                cs.setFont(FONT_BOLD, 9);
                cs.setNonStrokingColor(GOLD_ACCENT);
                cs.showText(refNumber);

                cs.setFont(FONT_REGULAR, 8);
                cs.setNonStrokingColor(SLATE_GRAY);
                cs.newLineAtOffset(0, -14);
                cs.showText("Date : ");
                cs.setFont(FONT_BOLD, 8);
                cs.showText(DateUtils.formatFrenchDate(LocalDate.now()));

                cs.setFont(FONT_REGULAR, 8);
                cs.newLineAtOffset(0, -12);
                cs.showText("Ville : ");
                cs.setFont(FONT_BOLD, 8);
                cs.showText(company != null ? company.getCity() : "Casablanca");
                cs.endText();

                // Divider Line
                currentY -= 50;
                cs.setStrokingColor(NAVY_PRIMARY);
                cs.setLineWidth(1.5f);
                cs.moveTo(40, currentY);
                cs.lineTo(pageWidth - 40, currentY);
                cs.stroke();

                // 4. Document Title Banner
                currentY -= 55;
                float bannerWidth = pageWidth - 80;
                float bannerHeight = 40;

                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.addRect(40, currentY, bannerWidth, bannerHeight);
                cs.fill();

                cs.setStrokingColor(GOLD_ACCENT);
                cs.setLineWidth(2);
                cs.addRect(43, currentY + 3, bannerWidth - 6, bannerHeight - 6);
                cs.stroke();

                String docTitle = template != null ? template.getTitle() : template.getDocType().getDisplayName().toUpperCase();
                float titleWidth = FONT_BOLD.getStringWidth(docTitle) / 1000 * 16;
                float titleX = 40 + (bannerWidth - titleWidth) / 2;

                cs.beginText();
                cs.setFont(FONT_BOLD, 16);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(titleX, currentY + 13);
                cs.showText(docTitle);
                cs.endText();

                // 5. Purpose Badge (if specified)
                currentY -= 30;
                if (purpose != null && !purpose.trim().isEmpty()) {
                    String purposeText = "Motif / Destination : " + purpose;
                    cs.beginText();
                    cs.setFont(FONT_ITALIC, 9);
                    cs.setNonStrokingColor(SLATE_GRAY);
                    cs.newLineAtOffset(40, currentY);
                    cs.showText(purposeText);
                    cs.endText();
                    currentY -= 15;
                }

                // 6. Main Legal Body Paragraphs
                currentY -= 20;
                String bodyText = buildLegalBodyText(employee, template, company);
                currentY = drawParagraphs(cs, bodyText, 40, currentY, pageWidth - 80, FONT_REGULAR, FONT_BOLD, 11, 17);

                // 7. Official Closing Statement
                currentY -= 25;
                cs.beginText();
                cs.setFont(FONT_REGULAR, 10);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(40, currentY);
                cs.showText("En foi de quoi, la présente attestation est délivrée à l'intéressé(e) pour servir et valoir ce que de droit.");
                cs.endText();

                // 8. Signatory & Stamp Box (Bottom Right) and QR Box (Bottom Left)
                float bottomBoxY = 150;

                // QR Verification Box (Left)
                String qrText = "MOROCCO IT VERIFICATION\nRef: " + refNumber + "\nCIN: " + employee.getCin() + "\nNom: " + employee.getFullName() + "\nHash: " + verifHash;
                BufferedImage qrImage = QrCodeService.generateQrCodeImage(qrText, 100, 100);
                PDImageXObject pdQrImage = LosslessFactory.createFromImage(document, qrImage);

                cs.drawImage(pdQrImage, 40, bottomBoxY, 80, 80);

                cs.setStrokingColor(BORDER_GRAY);
                cs.setLineWidth(1);
                cs.addRect(35, bottomBoxY - 10, 185, 105);
                cs.stroke();

                cs.beginText();
                cs.setFont(FONT_BOLD, 8);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(128, bottomBoxY + 55);
                cs.showText("VERIFICATION QR");
                cs.setFont(FONT_REGULAR, 7);
                cs.setNonStrokingColor(SLATE_GRAY);
                cs.newLineAtOffset(0, -12);
                cs.showText("Document Officiel");
                cs.newLineAtOffset(0, -10);
                cs.showText("Code: " + verifHash);
                cs.newLineAtOffset(0, -10);
                cs.showText("Scannez pour vérifier");
                cs.endText();

                // Signature & Stamp Box (Right)
                float sigBoxX = pageWidth - 230;
                cs.beginText();
                cs.setFont(FONT_BOLD, 10);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(sigBoxX, bottomBoxY + 75);
                cs.showText("Fait à " + (company != null ? company.getCity() : "Casablanca") + ", le " + DateUtils.formatFrenchDate(LocalDate.now()));
                cs.newLineAtOffset(0, -15);
                cs.setFont(FONT_BOLD, 10);
                cs.showText("Pour " + (company != null ? company.getCompanyName() : "MOROCCO IT S.A.R.L."));
                cs.newLineAtOffset(0, -14);
                cs.setFont(FONT_ITALIC, 9);
                cs.setNonStrokingColor(SLATE_GRAY);
                cs.showText(company != null ? company.getSignatoryTitle() : "Le Directeur des Ressources Humaines");
                cs.endText();

                // Draw Cachet Stamp Mockup / Image
                cs.setStrokingColor(GOLD_ACCENT);
                cs.setLineWidth(1.5f);
                cs.addRect(sigBoxX, bottomBoxY - 5, 180, 60);
                cs.stroke();

                cs.beginText();
                cs.setFont(FONT_BOLD, 9);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(sigBoxX + 15, bottomBoxY + 30);
                cs.showText(company != null ? company.getSignatoryName() : "Aymane BOUTARBOUCH");
                cs.setFont(FONT_ITALIC, 8);
                cs.setNonStrokingColor(GOLD_ACCENT);
                cs.newLineAtOffset(0, -14);
                cs.showText("[ Signature & Cachet Officiel ]");
                cs.endText();

                // 9. Footer Line & Legal Metadata
                cs.setStrokingColor(NAVY_PRIMARY);
                cs.setLineWidth(1);
                cs.moveTo(40, 50);
                cs.lineTo(pageWidth - 40, 50);
                cs.stroke();

                String footerContent = company != null ?
                    String.format("%s | Capital: %s | RC: %s | IF: %s | ICE: %s | CNSS: %s",
                        company.getCompanyName(), company.getCapital(), company.getRcNum(),
                        company.getIfNum(), company.getIce(), company.getCnssCompany())
                    : "MOROCCO IT S.A.R.L. | Capital: 500.000 DH | RC: 512039 | IF: 40291823 | ICE: 002145893000045";

                cs.beginText();
                cs.setFont(FONT_REGULAR, 7);
                cs.setNonStrokingColor(SLATE_GRAY);
                float footerWidth = FONT_REGULAR.getStringWidth(footerContent) / 1000 * 7;
                cs.newLineAtOffset((pageWidth - footerWidth) / 2, 38);
                cs.showText(footerContent);
                cs.newLineAtOffset(0, -10);
                String contactLine = "Siège social : " + (company != null ? company.getAddress() : "Casablanca") + " - Tél : " + (company != null ? company.getPhone() : "+212 5 22 48 90 12");
                float contactWidth = FONT_REGULAR.getStringWidth(contactLine) / 1000 * 7;
                cs.newLineAtOffset((footerWidth - contactWidth) / 2, 0);
                cs.showText(contactLine);
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private static String buildLegalBodyText(Employee emp, AttestationTemplate tpl, CompanySettings company) {
        if (tpl == null) return "";

        String raw = tpl.getLegalTextTemplate();

        String hireDateStr = DateUtils.formatFrenchDate(emp.getHireDate());
        String endDateStr = DateUtils.formatFrenchDate(emp.getEndDate());
        String grossStr = String.format("%,.2f", emp.getMonthlySalaryGross()).replace('\u202F', ' ').replace('\u00A0', ' ');
        String netStr = String.format("%,.2f", emp.getMonthlySalaryNet()).replace('\u202F', ' ').replace('\u00A0', ' ');
        String grossWords = NumberToWordsFR.convertAmount(emp.getMonthlySalaryGross());
        String netWords = NumberToWordsFR.convertAmount(emp.getMonthlySalaryNet());

        String result = raw.replace("{NOM}", emp.getFullName() != null ? emp.getFullName().toUpperCase() : "")
                  .replace("{CIN}", emp.getCin() != null ? emp.getCin() : "")
                  .replace("{CNSS}", emp.getCnssNum() != null ? emp.getCnssNum() : "N/A")
                  .replace("{POSTE}", emp.getJobTitle() != null ? emp.getJobTitle() : "")
                  .replace("{DEPARTEMENT}", emp.getDepartment() != null ? emp.getDepartment() : "")
                  .replace("{TYPE_CONTRAT}", emp.getContractType() != null ? emp.getContractType() : "")
                  .replace("{DATE_DEBUT}", hireDateStr)
                  .replace("{DATE_FIN}", endDateStr != null && !endDateStr.isEmpty() ? endDateStr : "ce jour")
                  .replace("{ECOLE}", emp.getSchoolName() != null ? emp.getSchoolName() : "Établissement d'Enseignement")
                  .replace("{SUJET_STAGE}", emp.getInternshipTopic() != null ? emp.getInternshipTopic() : "Sujet de Stage Pratique")
                  .replace("{SALAIRE_BRUT}", grossStr)
                  .replace("{SALAIRE_NET}", netStr)
                  .replace("{SALAIRE_BRUT_LETTRES}", grossWords)
                  .replace("{SALAIRE_NET_LETTRES}", netWords);

        return result.replace('\u202F', ' ').replace('\u00A0', ' ');
    }

    private static float drawParagraphs(PDPageContentStream cs, String text, float startX, float startY,
                                       float maxWidth, PDFont fontRegular, PDFont fontBold,
                                       float fontSize, float leading) throws IOException {
        float currentY = startY;
        String[] paragraphs = text.split("\n");

        for (String para : paragraphs) {
            if (para.trim().isEmpty()) {
                currentY -= leading;
                continue;
            }

            List<String> lines = wrapText(para, maxWidth, fontRegular, fontSize);
            for (String line : lines) {
                cs.beginText();
                cs.setFont(fontRegular, fontSize);
                cs.setNonStrokingColor(NAVY_PRIMARY);
                cs.newLineAtOffset(startX, currentY);
                cs.showText(line);
                cs.endText();
                currentY -= leading;
            }
        }
        return currentY;
    }

    private static List<String> wrapText(String text, float maxWidth, PDFont font, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String trial = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(trial) / 1000 * fontSize;
            if (width > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(currentLine.length() == 0 ? "" : " ").append(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private static void drawWatermark(PDPageContentStream cs, float pageWidth, float pageHeight, String text) throws IOException {
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setNonStrokingAlphaConstant(0.06f);
        cs.setGraphicsStateParameters(gs);

        cs.beginText();
        cs.setFont(FONT_BOLD, 42);
        cs.setNonStrokingColor(SLATE_GRAY);
        float width = FONT_BOLD.getStringWidth(text) / 1000 * 42;
        cs.newLineAtOffset((pageWidth - width) / 2, pageHeight / 2);
        cs.showText(text);
        cs.endText();

        gs.setNonStrokingAlphaConstant(1.0f);
        cs.setGraphicsStateParameters(gs);
    }
}
