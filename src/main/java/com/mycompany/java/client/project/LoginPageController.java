/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import com.mycompany.java.client.project.data.ServerConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ANTER
 */
public class LoginPageController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private Button backToHomeButton;

    @FXML
    private Button togglePasswordButton;

    private boolean isPasswordVisible = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the text fields together so they stay in sync
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void navigateToRegister(ActionEvent event) {
        try {
            App.setRoot("registerPage");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void navigateToHome(ActionEvent event) {
        try {
            App.setRoot("homePage");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Apply the CSS to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please enter both username and password.", Alert.AlertType.WARNING);
            return;
        }

        try (ServerConnection conn = ServerConnection.getInstance()) {
            conn.sendRequest("LOGIN");
            conn.sendRequest(username);
            conn.sendRequest(password);

            Object response = conn.readResponse();
            if ("LOGIN_SUCCESS".equals(response)) {
                App.setRoot("homePage");
            } else {
                showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Server Offline", "The server is currently unreachable.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Show password as plain text
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🔒");
        } else {
            // Hide password
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }
}
