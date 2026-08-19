package com.moroccoit.attestation.model;

import java.time.LocalDateTime;

public class AttestationHistory {
    private int id;
    private String refNumber;      // e.g. AT-2026-08-0042
    private DocType docType;
    private int employeeId;
    private String employeeName;
    private String employeeCin;
    private String generatedBy;    // Username who created it
    private LocalDateTime generationDate;
    private String verificationHash; // MD5/SHA256 for QR verification
    private String pdfFilePath;     // Relative or absolute path to generated PDF
    private String purpose;         // Purpose (e.g., Dossier Visa, Banque, etc.)

    public AttestationHistory() {}

    public AttestationHistory(int id, String refNumber, DocType docType, int employeeId,
                              String employeeName, String employeeCin, String generatedBy,
                              LocalDateTime generationDate, String verificationHash,
                              String pdfFilePath, String purpose) {
        this.id = id;
        this.refNumber = refNumber;
        this.docType = docType;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeCin = employeeCin;
        this.generatedBy = generatedBy;
        this.generationDate = generationDate;
        this.verificationHash = verificationHash;
        this.pdfFilePath = pdfFilePath;
        this.purpose = purpose;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRefNumber() { return refNumber; }
    public void setRefNumber(String refNumber) { this.refNumber = refNumber; }

    public DocType getDocType() { return docType; }
    public void setDocType(DocType docType) { this.docType = docType; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCin() { return employeeCin; }
    public void setEmployeeCin(String employeeCin) { this.employeeCin = employeeCin; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public LocalDateTime getGenerationDate() { return generationDate; }
    public void setGenerationDate(LocalDateTime generationDate) { this.generationDate = generationDate; }

    public String getVerificationHash() { return verificationHash; }
    public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

    public String getPdfFilePath() { return pdfFilePath; }
    public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
