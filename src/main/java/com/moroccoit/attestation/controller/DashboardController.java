package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.dao.EmployeeDao;
import com.moroccoit.attestation.dao.HistoryDao;
import com.moroccoit.attestation.model.AttestationHistory;
import com.moroccoit.attestation.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DashboardController {

    @FXML private Label totalEmployeesLabel;
    @FXML private Label totalInternsLabel;
    @FXML private Label totalPdfsGeneratedLabel;
    @FXML private Label todayPdfsLabel;

    @FXML private TableView<AttestationHistory> recentActivityTable;
    @FXML private TableColumn<AttestationHistory, String> colRef;
    @FXML private TableColumn<AttestationHistory, String> colType;
    @FXML private TableColumn<AttestationHistory, String> colEmployee;
    @FXML private TableColumn<AttestationHistory, String> colCin;
    @FXML private TableColumn<AttestationHistory, String> colDate;

    @FXML private PieChart docDistributionChart;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final HistoryDao historyDao = new HistoryDao();

    @FXML
    public void initialize() {
        loadMetrics();
        setupTable();
        loadRecentActivity();
        loadChart();
    }

    private void loadMetrics() {
        int empCount = employeeDao.getEmployeeCount();
        int internCount = employeeDao.getInternCount();
        List<AttestationHistory> historyList = historyDao.getAllHistory();
        int todayCount = historyDao.getTodayGeneratedCount();

        totalEmployeesLabel.setText(String.valueOf(empCount));
        totalInternsLabel.setText(String.valueOf(internCount));
        totalPdfsGeneratedLabel.setText(String.valueOf(historyList.size()));
        todayPdfsLabel.setText(String.valueOf(todayCount));
    }

    private void setupTable() {
        colRef.setCellValueFactory(new PropertyValueFactory<>("refNumber"));
        colType.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDocType().getDisplayName()));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("employeeCin"));
        colDate.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(DateUtils.formatDateTime(cellData.getValue().getGenerationDate())));
    }

    private void loadRecentActivity() {
        List<AttestationHistory> list = historyDao.getAllHistory();
        if (list.size() > 5) {
            list = list.subList(0, 5);
        }
        recentActivityTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadChart() {
        List<AttestationHistory> history = historyDao.getAllHistory();
        long stageCount = history.stream().filter(h -> h.getDocType().name().equals("STAGE")).count();
        long salaireCount = history.stream().filter(h -> h.getDocType().name().equals("SALAIRE")).count();
        long travailCount = history.stream().filter(h -> h.getDocType().name().equals("TRAVAIL")).count();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Attestation de Stage (" + stageCount + ")", stageCount > 0 ? stageCount : 1),
            new PieChart.Data("Attestation de Salaire (" + salaireCount + ")", salaireCount > 0 ? salaireCount : 2),
            new PieChart.Data("Attestation de Travail (" + travailCount + ")", travailCount > 0 ? travailCount : 3)
        );

        docDistributionChart.setData(pieChartData);
        docDistributionChart.setTitle("Répartition des Attestations Générées");
    }
}
