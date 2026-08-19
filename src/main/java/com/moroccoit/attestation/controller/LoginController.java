package com.moroccoit.attestation.controller;

import com.moroccoit.attestation.App;
import com.moroccoit.attestation.dao.UserDao;
import com.moroccoit.attestation.model.User;
import com.moroccoit.attestation.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserDao userDao = new UserDao();
    public static User currentUser = null;

    @FXML
    public void initialize() {
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showError("Connexion", "Champs requis", "Veuillez saisir votre nom d'utilisateur et mot de passe.");
            return;
        }

        User user = userDao.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            try {
                App.setRoot("/fxml/main.fxml");
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Erreur", "Navigation échouée", "Impossible de charger le tableau de bord: " + e.getMessage());
            }
        } else {
            AlertUtils.showError("Erreur d'authentification", "Identifiants incorrects", "Nom d'utilisateur ou mot de passe invalide.");
        }
    }

    @FXML
    private void fillAdminDemo() {
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }

    @FXML
    private void fillRhDemo() {
        usernameField.setText("rh");
        passwordField.setText("rh123");
    }
}
