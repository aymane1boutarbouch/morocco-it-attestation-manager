package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.dao.CompanySettingsDao;
import com.moroccoit.attestation.dao.TemplateDao;
import com.moroccoit.attestation.model.AttestationTemplate;
import com.moroccoit.attestation.model.CompanySettings;
import com.moroccoit.attestation.model.DocType;
import com.moroccoit.attestation.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TemplateEditorController {

    // Company Settings Fields
    @FXML private TextField companyNameField;
    @FXML private TextField iceField;
    @FXML private TextField ifField;
    @FXML private TextField rcField;
    @FXML private TextField cnssField;
    @FXML private TextField capitalField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField websiteField;
    @FXML private TextField signatoryNameField;
    @FXML private TextField signatoryTitleField;
    @FXML private CheckBox watermarkCheckBox;

    // Template Fields
    @FXML private ComboBox<DocType> docTypeSelector;
    @FXML private TextField templateTitleField;
    @FXML private TextArea legalTextTemplateArea;
    @FXML private TextField headerTextField;
    @FXML private TextField footerTextField;

    private final CompanySettingsDao companySettingsDao = new CompanySettingsDao();
    private final TemplateDao templateDao = new TemplateDao();

    private CompanySettings currentCompany;

    @FXML
    public void initialize() {
        loadCompanySettings();
        setupTemplateTab();
    }

    private void loadCompanySettings() {
        currentCompany = companySettingsDao.getSettings();
        if (currentCompany != null) {
            companyNameField.setText(currentCompany.getCompanyName());
            iceField.setText(currentCompany.getIce());
            ifField.setText(currentCompany.getIfNum());
            rcField.setText(currentCompany.getRcNum());
            cnssField.setText(currentCompany.getCnssCompany());
            capitalField.setText(currentCompany.getCapital());
            addressField.setText(currentCompany.getAddress());
            cityField.setText(currentCompany.getCity());
            phoneField.setText(currentCompany.getPhone());
            emailField.setText(currentCompany.getEmail());
            websiteField.setText(currentCompany.getWebsite());
            signatoryNameField.setText(currentCompany.getSignatoryName());
            signatoryTitleField.setText(currentCompany.getSignatoryTitle());
            watermarkCheckBox.setSelected(currentCompany.isWatermarkEnabled());
        }
    }

    private void setupTemplateTab() {
        docTypeSelector.setItems(FXCollections.observableArrayList(DocType.values()));
        docTypeSelector.setValue(DocType.TRAVAIL);
        loadSelectedTemplate(DocType.TRAVAIL);

        docTypeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSelectedTemplate(newVal);
            }
        });
    }

    private void loadSelectedTemplate(DocType docType) {
        AttestationTemplate template = templateDao.getByDocType(docType);
        if (template != null) {
            templateTitleField.setText(template.getTitle());
            legalTextTemplateArea.setText(template.getLegalTextTemplate());
            headerTextField.setText(template.getHeaderText());
            footerTextField.setText(template.getFooterText());
        }
    }

    @FXML
    private void handleSaveCompanySettings() {
        if (currentCompany == null) currentCompany = new CompanySettings();

        currentCompany.setCompanyName(companyNameField.getText().trim());
        currentCompany.setIce(iceField.getText().trim());
        currentCompany.setIfNum(ifField.getText().trim());
        currentCompany.setRcNum(rcField.getText().trim());
        currentCompany.setCnssCompany(cnssField.getText().trim());
        currentCompany.setCapital(capitalField.getText().trim());
        currentCompany.setAddress(addressField.getText().trim());
        currentCompany.setCity(cityField.getText().trim());
        currentCompany.setPhone(phoneField.getText().trim());
        currentCompany.setEmail(emailField.getText().trim());
        currentCompany.setWebsite(websiteField.getText().trim());
        currentCompany.setSignatoryName(signatoryNameField.getText().trim());
        currentCompany.setSignatoryTitle(signatoryTitleField.getText().trim());
        currentCompany.setWatermarkEnabled(watermarkCheckBox.isSelected());

        if (companySettingsDao.updateSettings(currentCompany)) {
            AlertUtils.showInformation("Enregistrement", "Paramètres Société Mis à Jour",
                "Les coordonnées légales de Morocco IT ont été sauvegardées.");
        } else {
            AlertUtils.showError("Erreur", "Sauvegarde échouée", "Impossible de mettre à jour les paramètres société.");
        }
    }

    @FXML
    private void handleSaveTemplate() {
        DocType docType = docTypeSelector.getValue();
        if (docType == null) return;

        AttestationTemplate template = new AttestationTemplate();
        template.setDocType(docType);
        template.setTitle(templateTitleField.getText().trim());
        template.setLegalTextTemplate(legalTextTemplateArea.getText().trim());
        template.setHeaderText(headerTextField.getText().trim());
        template.setFooterText(footerTextField.getText().trim());

        if (templateDao.updateTemplate(template)) {
            AlertUtils.showInformation("Enregistrement", "Modèle Mis à Jour",
                "Le modèle d'attestation legal pour [" + docType.getDisplayName() + "] a été enregistré.");
        } else {
            AlertUtils.showError("Erreur", "Sauvegarde échouée", "Impossible d'enregistrer le modèle.");
        }
    }
}
