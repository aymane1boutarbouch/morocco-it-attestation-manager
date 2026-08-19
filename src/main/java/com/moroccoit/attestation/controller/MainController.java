package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.App;
import com.moroccoit.attestation.service.LanguageService;
import com.moroccoit.attestation.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Locale;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label userFullNameLabel;
    @FXML private Label userRoleBadge;
    @FXML private Label activeViewTitle;

    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnGenerator;
    @FXML private Button btnTemplates;
    @FXML private Button btnHistory;

    @FXML
    public void initialize() {
        if (LoginController.currentUser != null) {
            userFullNameLabel.setText(LoginController.currentUser.getFullName());
            userRoleBadge.setText(" " + LoginController.currentUser.getRole() + " ");
        } else {
            userFullNameLabel.setText("Utilisateur RH");
            userRoleBadge.setText(" ADMIN ");
        }

        // Open Dashboard by default
        openDashboard();
    }

    @FXML
    public void openDashboard() {
        loadView("/fxml/dashboard.fxml", "Tableau de Bord & Métriques", btnDashboard);
    }

    @FXML
    public void openEmployees() {
        loadView("/fxml/employee.fxml", "Base de Données Employés & Stagiaires", btnEmployees);
    }

    @FXML
    public void openGenerator() {
        loadView("/fxml/generator.fxml", "Générateur d'Attestations Officiel", btnGenerator);
    }

    @FXML
    public void openTemplates() {
        loadView("/fxml/template_editor.fxml", "Éditeur de Modèles & En-tête Société", btnTemplates);
    }

    @FXML
    public void openHistory() {
        loadView("/fxml/history.fxml", "Historique & Archive des Attestations", btnHistory);
    }

    @FXML
    private void toggleLanguageFR() {
        LanguageService.getInstance().setLocale(new Locale("fr", "FR"));
        AlertUtils.showInformation("Langue", "Langue modifiée", "La langue principale a été définie sur le Français.");
    }

    @FXML
    private void toggleLanguageAR() {
        LanguageService.getInstance().setLocale(new Locale("ar", "MA"));
        AlertUtils.showInformation("اللغة", "تغيير اللغة", "تم تفعيل اللغة العربية كخيار ثانوي.");
    }

    @FXML
    private void handleLogout() {
        if (AlertUtils.showConfirmation("Déconnexion", "Confirmer la déconnexion", "Voulez-vous vraiment vous déconnecter ?")) {
            LoginController.currentUser = null;
            try {
                App.setRoot("/fxml/login.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadView(String fxmlPath, String title, Button activeBtn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            activeViewTitle.setText(title);

            resetNavButtons();
            if (activeBtn != null) {
                activeBtn.getStyleClass().add("active");
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Erreur de Chargement", "Vue indisponible", "Impossible de charger la vue : " + fxmlPath);
        }
    }

    private void resetNavButtons() {
        btnDashboard.getStyleClass().remove("active");
        btnEmployees.getStyleClass().remove("active");
        btnGenerator.getStyleClass().remove("active");
        btnTemplates.getStyleClass().remove("active");
        btnHistory.getStyleClass().remove("active");
    }
}
