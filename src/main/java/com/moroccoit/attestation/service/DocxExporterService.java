package com.moroccoit.attestation.service;

import com.moroccoit.attestation.model.*;
import com.moroccoit.attestation.util.DateUtils;
import com.moroccoit.attestation.util.NumberToWordsFR;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public class DocxExporterService {

    public static byte[] exportToDocx(Employee employee, CompanySettings company, AttestationTemplate template,
                                      String refNumber, String purpose) throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {

            // Header Title
            XWPFParagraph pCompany = doc.createParagraph();
            pCompany.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun rCompany = pCompany.createRun();
            rCompany.setText(company != null ? company.getCompanyName() : "MOROCCO IT S.A.R.L.");
            rCompany.setBold(true);
            rCompany.setFontSize(16);
            rCompany.setColor("0F172A");

            XWPFParagraph pRef = doc.createParagraph();
            pRef.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun rRef = pRef.createRun();
            rRef.setText("Réf : " + refNumber + " | Date : " + DateUtils.formatFrenchDate(LocalDate.now()));
            rRef.setFontSize(10);
            rRef.setColor("475569");

            // Title Box
            XWPFParagraph pTitle = doc.createParagraph();
            pTitle.setAlignment(ParagraphAlignment.CENTER);
            pTitle.setSpacingBefore(300);
            pTitle.setSpacingAfter(300);

            XWPFRun rTitle = pTitle.createRun();
            rTitle.setText(template != null ? template.getTitle() : "ATTESTATION");
            rTitle.setBold(true);
            rTitle.setFontSize(18);
            rTitle.setColor("D97706");
            rTitle.setUnderline(UnderlinePatterns.SINGLE);

            // Body Paragraphs
            String bodyText = buildText(employee, template);
            for (String line : bodyText.split("\n")) {
                XWPFParagraph pBody = doc.createParagraph();
                pBody.setSpacingAfter(150);
                XWPFRun rBody = pBody.createRun();
                rBody.setText(line);
                rBody.setFontSize(12);
                rBody.setFontFamily("Calibri");
            }

            // Signatory Statement
            XWPFParagraph pSign = doc.createParagraph();
            pSign.setAlignment(ParagraphAlignment.RIGHT);
            pSign.setSpacingBefore(400);

            XWPFRun rSignDate = pSign.createRun();
            rSignDate.setText("Fait à " + (company != null ? company.getCity() : "Casablanca") + ", le " + DateUtils.formatFrenchDate(LocalDate.now()));
            rSignDate.addBreak();
            rSignDate.setText("Pour MOROCCO IT S.A.R.L.");
            rSignDate.setBold(true);
            rSignDate.addBreak();
            rSignDate.setText(company != null ? company.getSignatoryTitle() : "Le Directeur des Ressources Humaines");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.write(baos);
            return baos.toByteArray();
        }
    }

    private static String buildText(Employee emp, AttestationTemplate tpl) {
        if (tpl == null) return "";
        String raw = tpl.getLegalTextTemplate();

        return raw.replace("{NOM}", emp.getFullName() != null ? emp.getFullName().toUpperCase() : "")
                  .replace("{CIN}", emp.getCin() != null ? emp.getCin() : "")
                  .replace("{CNSS}", emp.getCnssNum() != null ? emp.getCnssNum() : "N/A")
                  .replace("{POSTE}", emp.getJobTitle() != null ? emp.getJobTitle() : "")
                  .replace("{DEPARTEMENT}", emp.getDepartment() != null ? emp.getDepartment() : "")
                  .replace("{TYPE_CONTRAT}", emp.getContractType() != null ? emp.getContractType() : "")
                  .replace("{DATE_DEBUT}", DateUtils.formatFrenchDate(emp.getHireDate()))
                  .replace("{DATE_FIN}", emp.getEndDate() != null ? DateUtils.formatFrenchDate(emp.getEndDate()) : "ce jour")
                  .replace("{ECOLE}", emp.getSchoolName() != null ? emp.getSchoolName() : "Établissement")
                  .replace("{SUJET_STAGE}", emp.getInternshipTopic() != null ? emp.getInternshipTopic() : "Sujet de Stage")
                  .replace("{SALAIRE_BRUT}", String.format("%,.2f", emp.getMonthlySalaryGross()))
                  .replace("{SALAIRE_NET}", String.format("%,.2f", emp.getMonthlySalaryNet()))
                  .replace("{SALAIRE_BRUT_LETTRES}", NumberToWordsFR.convertAmount(emp.getMonthlySalaryGross()))
                  .replace("{SALAIRE_NET_LETTRES}", NumberToWordsFR.convertAmount(emp.getMonthlySalaryNet()));
    }
}
