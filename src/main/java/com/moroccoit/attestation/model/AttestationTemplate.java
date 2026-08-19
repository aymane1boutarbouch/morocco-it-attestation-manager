package com.moroccoit.attestation.model;

public class AttestationTemplate {
    private int id;
    private DocType docType;
    private String title;
    private String legalTextTemplate;
    private String headerText;
    private String footerText;

    public AttestationTemplate() {}

    public AttestationTemplate(int id, DocType docType, String title, String legalTextTemplate, String headerText, String footerText) {
        this.id = id;
        this.docType = docType;
        this.title = title;
        this.legalTextTemplate = legalTextTemplate;
        this.headerText = headerText;
        this.footerText = footerText;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public DocType getDocType() { return docType; }
    public void setDocType(DocType docType) { this.docType = docType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLegalTextTemplate() { return legalTextTemplate; }
    public void setLegalTextTemplate(String legalTextTemplate) { this.legalTextTemplate = legalTextTemplate; }

    public String getHeaderText() { return headerText; }
    public void setHeaderText(String headerText) { this.headerText = headerText; }

    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }
}
