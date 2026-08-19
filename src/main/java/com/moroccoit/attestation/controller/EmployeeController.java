package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.dao.EmployeeDao;
import com.moroccoit.attestation.model.Employee;
import com.moroccoit.attestation.util.AlertUtils;
import com.moroccoit.attestation.util.DateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Optional;

public class EmployeeController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> colRegNum;
    @FXML private TableColumn<Employee, String> colFullName;
    @FXML private TableColumn<Employee, String> colCin;
    @FXML private TableColumn<Employee, String> colJobTitle;
    @FXML private TableColumn<Employee, String> colDepartment;
    @FXML private TableColumn<Employee, String> colContractType;
    @FXML private TableColumn<Employee, String> colHireDate;
    @FXML private TableColumn<Employee, String> colSalary;
    @FXML private TableColumn<Employee, String> colStatus;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterContractCombo;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        loadEmployees();
        setupFilters();
    }

    private void setupTable() {
        colRegNum.setCellValueFactory(new PropertyValueFactory<>("registrationNum"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colContractType.setCellValueFactory(new PropertyValueFactory<>("contractType"));
        colHireDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(DateUtils.formatShortDate(cellData.getValue().getHireDate())));
        colSalary.setCellValueFactory(cellData -> {
            double sal = cellData.getValue().getMonthlySalaryNet();
            return new SimpleStringProperty(sal > 0 ? String.format("%,.2f DH", sal) : "N/A (Stage)");
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadEmployees() {
        employeeList.setAll(employeeDao.getAllEmployees());
        filteredData = new FilteredList<>(employeeList, p -> true);
        employeeTable.setItems(filteredData);
    }

    private void setupFilters() {
        filterContractCombo.setItems(FXCollections.observableArrayList("Tous les Contrats", "CDI", "CDD", "Stage", "Anapec"));
        filterContractCombo.setValue("Tous les Contrats");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        filterContractCombo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
    }

    private void applyFilter() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String selectedContract = filterContractCombo.getValue();

        filteredData.setPredicate(emp -> {
            boolean matchesSearch = emp.getFullName().toLowerCase().contains(searchText)
                    || emp.getCin().toLowerCase().contains(searchText)
                    || emp.getRegistrationNum().toLowerCase().contains(searchText)
                    || emp.getJobTitle().toLowerCase().contains(searchText)
                    || emp.getDepartment().toLowerCase().contains(searchText);

            boolean matchesContract = selectedContract == null || "Tous les Contrats".equals(selectedContract)
                    || emp.getContractType().equalsIgnoreCase(selectedContract);

            return matchesSearch && matchesContract;
        });
    }

    @FXML
    private void handleAddEmployee() {
        showEmployeeDialog(null);
    }

    @FXML
    private void handleEditEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Modification", "Sélection requise", "Veuillez sélectionner un employé ou stagiaire dans la liste.");
            return;
        }
        showEmployeeDialog(selected);
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Suppression", "Sélection requise", "Veuillez sélectionner un employé à supprimer.");
            return;
        }

        if (AlertUtils.showConfirmation("Suppression", "Confirmer la suppression",
                "Voulez-vous vraiment supprimer " + selected.getFullName() + " (CIN: " + selected.getCin() + ") ?")) {
            if (employeeDao.deleteEmployee(selected.getId())) {
                AlertUtils.showInformation("Succès", "Employé supprimé", "La fiche a été supprimée avec succès.");
                loadEmployees();
            } else {
                AlertUtils.showError("Erreur", "Suppression échouée", "Impossible de supprimer la fiche employé.");
            }
        }
    }

    private void showEmployeeDialog(Employee existing) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Ajouter un Employé / Stagiaire" : "Modifier la Fiche Employé");
        dialog.setHeaderText(existing == null ? "Saisissez les informations de la nouvelle fiche" : "Mettre à jour la fiche de " + existing.getFullName());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField regNumField = new TextField(existing != null ? existing.getRegistrationNum() : "EMP-2026-0" + (employeeList.size() + 1));
        TextField nameField = new TextField(existing != null ? existing.getFullName() : "");
        TextField cinField = new TextField(existing != null ? existing.getCin() : "");
        TextField cnssField = new TextField(existing != null ? existing.getCnssNum() : "");
        TextField titleField = new TextField(existing != null ? existing.getJobTitle() : "");
        ComboBox<String> deptCombo = new ComboBox<>(FXCollections.observableArrayList("Comptabilité", "Audit", "IT & Digital", "RH", "Conseil Juridique"));
        deptCombo.setValue(existing != null ? existing.getDepartment() : "Comptabilité");

        ComboBox<String> contractCombo = new ComboBox<>(FXCollections.observableArrayList("CDI", "CDD", "Stage", "Anapec"));
        contractCombo.setValue(existing != null ? existing.getContractType() : "CDI");

        DatePicker hireDatePicker = new DatePicker(existing != null && existing.getHireDate() != null ? existing.getHireDate() : LocalDate.now());
        DatePicker endDatePicker = new DatePicker(existing != null ? existing.getEndDate() : null);

        TextField grossSalaryField = new TextField(existing != null ? String.valueOf(existing.getMonthlySalaryGross()) : "0");
        TextField netSalaryField = new TextField(existing != null ? String.valueOf(existing.getMonthlySalaryNet()) : "0");

        TextField schoolField = new TextField(existing != null ? existing.getSchoolName() : "");
        TextField topicField = new TextField(existing != null ? existing.getInternshipTopic() : "");

        grid.add(new Label("N° Matricule :"), 0, 0); grid.add(regNumField, 1, 0);
        grid.add(new Label("Nom & Prénom :"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("N° CIN :"), 0, 2); grid.add(cinField, 1, 2);
        grid.add(new Label("N° CNSS :"), 0, 3); grid.add(cnssField, 1, 3);
        grid.add(new Label("Poste / Fonction :"), 0, 4); grid.add(titleField, 1, 4);
        grid.add(new Label("Département :"), 0, 5); grid.add(deptCombo, 1, 5);
        grid.add(new Label("Type de Contrat :"), 0, 6); grid.add(contractCombo, 1, 6);
        grid.add(new Label("Date Début :"), 0, 7); grid.add(hireDatePicker, 1, 7);
        grid.add(new Label("Date Fin (Stage/CDD) :"), 0, 8); grid.add(endDatePicker, 1, 8);
        grid.add(new Label("Salaire Brut (DH) :"), 0, 9); grid.add(grossSalaryField, 1, 9);
        grid.add(new Label("Salaire Net (DH) :"), 0, 10); grid.add(netSalaryField, 1, 10);
        grid.add(new Label("École (Stagiaire) :"), 0, 11); grid.add(schoolField, 1, 11);
        grid.add(new Label("Sujet de Stage :"), 0, 12); grid.add(topicField, 1, 12);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Employee emp = existing != null ? existing : new Employee();
                emp.setRegistrationNum(regNumField.getText().trim());
                emp.setFullName(nameField.getText().trim());
                emp.setCin(cinField.getText().trim());
                emp.setCnssNum(cnssField.getText().trim());
                emp.setJobTitle(titleField.getText().trim());
                emp.setDepartment(deptCombo.getValue());
                emp.setContractType(contractCombo.getValue());
                emp.setHireDate(hireDatePicker.getValue());
                emp.setEndDate(endDatePicker.getValue());
                try {
                    emp.setMonthlySalaryGross(Double.parseDouble(grossSalaryField.getText().trim()));
                    emp.setMonthlySalaryNet(Double.parseDouble(netSalaryField.getText().trim()));
                } catch (NumberFormatException ignored) {}
                emp.setSchoolName(schoolField.getText().trim());
                emp.setInternshipTopic(topicField.getText().trim());
                emp.setStatus("Actif");
                return emp;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        result.ifPresent(emp -> {
            if (existing == null) {
                if (employeeDao.addEmployee(emp)) {
                    AlertUtils.showInformation("Succès", "Employé Ajouté", "Nouvelle fiche enregistrée avec succès.");
                }
            } else {
                if (employeeDao.updateEmployee(emp)) {
                    AlertUtils.showInformation("Succès", "Fiche Mise à Jour", "Les modifications ont été enregistrées.");
                }
            }
            loadEmployees();
        });
    }
}
