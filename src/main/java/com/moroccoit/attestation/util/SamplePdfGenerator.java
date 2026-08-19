package com.moroccoit.attestation.util;

import com.moroccoit.attestation.config.DatabaseConfig;
import com.moroccoit.attestation.dao.CompanySettingsDao;
import com.moroccoit.attestation.dao.EmployeeDao;
import com.moroccoit.attestation.dao.HistoryDao;
import com.moroccoit.attestation.dao.TemplateDao;
import com.moroccoit.attestation.model.*;
import com.moroccoit.attestation.service.PdfGeneratorService;
import com.moroccoit.attestation.service.QrCodeService;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public class SamplePdfGenerator {

    public static void main(String[] args) {
        System.out.println("Initializing Database and Generating Sample PDFs...");
        DatabaseConfig.initializeDatabase();

        EmployeeDao employeeDao = new EmployeeDao();
        CompanySettingsDao companySettingsDao = new CompanySettingsDao();
        TemplateDao templateDao = new TemplateDao();
        HistoryDao historyDao = new HistoryDao();

        CompanySettings company = companySettingsDao.getSettings();
        List<Employee> employees = employeeDao.getAllEmployees();

        File outputDir = new File("output_samples");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        User dummyUser = new User(1, "admin", "admin123", "Administrateur Système", "ADMIN");

        for (Employee emp : employees) {
            DocType docType;
            String purpose;

            if (emp.isIntern()) {
                docType = DocType.STAGE;
                purpose = "Validation du diplôme d'ingénieur / Fin d'études";
            } else if ("Comptabilité".equals(emp.getDepartment())) {
                docType = DocType.SALAIRE;
                purpose = "Dossier de crédit immobilier bancaire";
            } else {
                docType = DocType.TRAVAIL;
                purpose = "Pour servir et valoir ce que de droit";
            }

            AttestationTemplate template = templateDao.getByDocType(docType);
            String refNum = historyDao.generateNextReferenceNumber();

            try {
                byte[] pdfBytes = PdfGeneratorService.generatePdf(
                    emp, company, template, refNum, purpose, dummyUser
                );

                String fileName = "output_samples/" + docType.name() + "_" + emp.getFullName().replaceAll(" ", "_") + ".pdf";
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    fos.write(pdfBytes);
                }
                System.out.println("Successfully generated: " + fileName);

                // Add to history
                AttestationHistory history = new AttestationHistory();
                history.setRefNumber(refNum);
                history.setDocType(docType);
                history.setEmployeeId(emp.getId());
                history.setEmployeeName(emp.getFullName());
                history.setEmployeeCin(emp.getCin());
                history.setGeneratedBy("admin");
                history.setGenerationDate(LocalDateTime.now());
                history.setVerificationHash(QrCodeService.computeVerificationHash(
                    refNum, emp.getCin(), docType.name(), LocalDateTime.now().toLocalDate().toString()
                ));
                history.setPdfFilePath(new File(fileName).getAbsolutePath());
                history.setPurpose(purpose);

                historyDao.addHistoryRecord(history);

            } catch (Exception e) {
                System.err.println("Error generating PDF for " + emp.getFullName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Sample PDF generation complete! Files stored in 'output_samples/' directory.");
        System.exit(0);
    }
}
