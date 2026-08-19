package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.dao.HistoryDao;
import com.moroccoit.attestation.model.AttestationHistory;
import com.moroccoit.attestation.util.AlertUtils;
import com.moroccoit.attestation.util.DateUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class HistoryController {

    @FXML private TableView<AttestationHistory> historyTable;
    @FXML private TableColumn<AttestationHistory, String> colRef;
    @FXML private TableColumn<AttestationHistory, String> colType;
    @FXML private TableColumn<AttestationHistory, String> colEmpName;
    @FXML private TableColumn<AttestationHistory, String> colEmpCin;
    @FXML private TableColumn<AttestationHistory, String> colGeneratedBy;
    @FXML private TableColumn<AttestationHistory, String> colDate;
    @FXML private TableColumn<AttestationHistory, String> colPurpose;
    @FXML private TableColumn<AttestationHistory, String> colHash;

    @FXML private TextField searchField;

    private final HistoryDao historyDao = new HistoryDao();
    private final ObservableList<AttestationHistory> historyList = FXCollections.observableArrayList();
    private FilteredList<AttestationHistory> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        loadHistory();
        setupSearch();
    }

    private void setupTable() {
        colRef.setCellValueFactory(new PropertyValueFactory<>("refNumber"));
        colType.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDocType().getDisplayName()));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colEmpCin.setCellValueFactory(new PropertyValueFactory<>("employeeCin"));
        colGeneratedBy.setCellValueFactory(new PropertyValueFactory<>("generatedBy"));
        colDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(DateUtils.formatDateTime(cellData.getValue().getGenerationDate())));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        colHash.setCellValueFactory(new PropertyValueFactory<>("verificationHash"));
    }

    private void loadHistory() {
        List<AttestationHistory> list = historyDao.getAllHistory();
        historyList.setAll(list);
        filteredData = new FilteredList<>(historyList, p -> true);
        historyTable.setItems(filteredData);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.toLowerCase().trim();
            filteredData.setPredicate(h ->
                h.getRefNumber().toLowerCase().contains(query) ||
                h.getEmployeeName().toLowerCase().contains(query) ||
                h.getEmployeeCin().toLowerCase().contains(query) ||
                h.getDocType().getDisplayName().toLowerCase().contains(query) ||
                (h.getPurpose() != null && h.getPurpose().toLowerCase().contains(query))
            );
        });
    }

    @FXML
    private void handleViewPdf() {
        AttestationHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Visualisation", "Aucun élément sélectionné", "Veuillez choisir un enregistrement dans l'historique.");
            return;
        }

        File pdfFile = new File(selected.getPdfFilePath());
        if (pdfFile.exists()) {
            try {
                Desktop.getDesktop().open(pdfFile);
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Erreur", "Ouverture impossible", "Impossible d'ouvrir le fichier PDF : " + e.getMessage());
            }
        } else {
            AlertUtils.showError("Fichier Introuvable", "Document introuvable",
                "Le fichier PDF archivé est introuvable au chemin : " + selected.getPdfFilePath());
        }
    }

    @FXML
    private void handleReExportPdf() {
        AttestationHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Ré-exportation", "Aucun élément sélectionné", "Veuillez choisir un enregistrement à ré-exporter.");
            return;
        }

        File srcFile = new File(selected.getPdfFilePath());
        if (!srcFile.exists()) {
            AlertUtils.showError("Erreur", "Fichier introuvable", "Le fichier PDF original n'existe plus sur le disque.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ré-exporter le Document PDF");
        fileChooser.setInitialFileName(selected.getRefNumber() + "_" + selected.getEmployeeName().replaceAll(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents PDF (*.pdf)", "*.pdf"));

        File destFile = fileChooser.showSaveDialog(null);
        if (destFile != null) {
            try {
                Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                AlertUtils.showInformation("Succès", "Fichier Copié", "Le document PDF a été ré-exporté avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Erreur", "Copie impossible", "Erreur lors de la copie du fichier : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteRecord() {
        AttestationHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Suppression", "Aucun élément sélectionné", "Veuillez choisir un enregistrement à supprimer.");
            return;
        }

        if (AlertUtils.showConfirmation("Suppression", "Confirmer la suppression d'archive",
                "Voulez-vous vraiment supprimer l'historique du document N° " + selected.getRefNumber() + " ?")) {
            if (historyDao.deleteHistoryRecord(selected.getId())) {
                AlertUtils.showInformation("Succès", "Enregistrement Supprimé", "L'archive a été retirée du système.");
                loadHistory();
            } else {
                AlertUtils.showError("Erreur", "Suppression échouée", "Impossible de supprimer l'enregistrement d'archive.");
            }
        }
    }
}
