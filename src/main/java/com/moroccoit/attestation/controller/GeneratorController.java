package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.dao.CompanySettingsDao;
import com.moroccoit.attestation.dao.EmployeeDao;
import com.moroccoit.attestation.dao.HistoryDao;
import com.moroccoit.attestation.dao.TemplateDao;
import com.moroccoit.attestation.model.*;
import com.moroccoit.attestation.service.DocxExporterService;
import com.moroccoit.attestation.service.PdfGeneratorService;
import com.moroccoit.attestation.service.QrCodeService;
import com.moroccoit.attestation.util.AlertUtils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public class GeneratorController {

    @FXML private ComboBox<Employee> employeeCombo;
    @FXML private ComboBox<DocType> docTypeCombo;
    @FXML private TextField purposeField;
    @FXML private TextField refNumField;

    @FXML private Label empNameLabel;
    @FXML private Label empCinLabel;
    @FXML private Label empJobLabel;
    @FXML private Label empContractLabel;
    @FXML private Label empDeptLabel;

    @FXML private ImageView pdfPreviewImageView;
    @FXML private VBox previewContainer;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final CompanySettingsDao companySettingsDao = new CompanySettingsDao();
    private final TemplateDao templateDao = new TemplateDao();
    private final HistoryDao historyDao = new HistoryDao();

    private byte[] lastGeneratedPdfBytes = null;

    @FXML
    public void initialize() {
        loadEmployees();
        setupDocTypes();
        refNumField.setText(historyDao.generateNextReferenceNumber());

        employeeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateEmployeeDetails(newVal);
            refreshPreview();
        });

        docTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            refreshPreview();
        });

        purposeField.textProperty().addListener((obs, oldVal, newVal) -> refreshPreview());
    }

    private void loadEmployees() {
        List<Employee> list = employeeDao.getAllEmployees();
        employeeCombo.getItems().setAll(list);
        if (!list.isEmpty()) {
            employeeCombo.setValue(list.get(0));
        }
    }

    private void setupDocTypes() {
        docTypeCombo.getItems().setAll(DocType.values());
        docTypeCombo.setValue(DocType.TRAVAIL);
    }

    private void updateEmployeeDetails(Employee emp) {
        if (emp != null) {
            empNameLabel.setText(emp.getFullName());
            empCinLabel.setText("CIN : " + emp.getCin());
            empJobLabel.setText("Poste : " + emp.getJobTitle());
            empContractLabel.setText("Contrat : " + emp.getContractType());
            empDeptLabel.setText("Dépt : " + emp.getDepartment());
        }
    }

    @FXML
    private void refreshPreview() {
        Employee selectedEmp = employeeCombo.getValue();
        DocType selectedDocType = docTypeCombo.getValue();
        if (selectedEmp == null || selectedDocType == null) return;

        try {
            CompanySettings company = companySettingsDao.getSettings();
            AttestationTemplate template = templateDao.getByDocType(selectedDocType);
            String refNum = refNumField.getText().trim();
            String purpose = purposeField.getText().trim();

            lastGeneratedPdfBytes = PdfGeneratorService.generatePdf(
                selectedEmp, company, template, refNum, purpose, LoginController.currentUser
            );

            renderPdfPreview(lastGeneratedPdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderPdfPreview(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 120); // 120 DPI preview
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            pdfPreviewImageView.setImage(fxImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateAndSave() {
        if (lastGeneratedPdfBytes == null) {
            refreshPreview();
        }

        Employee selectedEmp = employeeCombo.getValue();
        DocType selectedDocType = docTypeCombo.getValue();
        if (selectedEmp == null || selectedDocType == null) {
            AlertUtils.showError("Génération", "Sélection incomplète", "Veuillez choisir un employé et un type d'attestation.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'Attestation Officielle (PDF)");
        fileChooser.setInitialFileName(refNumField.getText() + "_" + selectedEmp.getFullName().replaceAll(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents PDF (*.pdf)", "*.pdf"));

        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(lastGeneratedPdfBytes);

                // Save into History DAO
                AttestationHistory history = new AttestationHistory();
                history.setRefNumber(refNumField.getText().trim());
                history.setDocType(selectedDocType);
                history.setEmployeeId(selectedEmp.getId());
                history.setEmployeeName(selectedEmp.getFullName());
                history.setEmployeeCin(selectedEmp.getCin());
                history.setGeneratedBy(LoginController.currentUser != null ? LoginController.currentUser.getUsername() : "RH");
                history.setGenerationDate(LocalDateTime.now());
                history.setVerificationHash(QrCodeService.computeVerificationHash(
                    refNumField.getText().trim(), selectedEmp.getCin(), selectedDocType.name(), LocalDateTime.now().toLocalDate().toString()
                ));
                history.setPdfFilePath(saveFile.getAbsolutePath());
                history.setPurpose(purposeField.getText().trim());

                historyDao.addHistoryRecord(history);

                AlertUtils.showInformation("Succès", "Document Généré",
                    "L'attestation officielle N° " + refNumField.getText() + " a été générée et enregistrée avec succès.");

                refNumField.setText(historyDao.generateNextReferenceNumber());
                refreshPreview();

            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Erreur", "Enregistrement échoué", "Impossible de sauvegarder le fichier PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportDocx() {
        Employee selectedEmp = employeeCombo.getValue();
        DocType selectedDocType = docTypeCombo.getValue();
        if (selectedEmp == null || selectedDocType == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'Attestation en Word (DOCX)");
        fileChooser.setInitialFileName(refNumField.getText() + "_" + selectedEmp.getFullName().replaceAll(" ", "_") + ".docx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents Word (*.docx)", "*.docx"));

        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            try {
                CompanySettings company = companySettingsDao.getSettings();
                AttestationTemplate template = templateDao.getByDocType(selectedDocType);

                byte[] docxBytes = DocxExporterService.exportToDocx(
                    selectedEmp, company, template, refNumField.getText(), purposeField.getText()
                );

                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                    fos.write(docxBytes);
                }

                AlertUtils.showInformation("Succès", "Export Word Réussi", "Document Word (.docx) exporté avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Erreur", "Export DOCX échoué", "Erreur lors de l'export Word : " + e.getMessage());
            }
        }
    }
}
