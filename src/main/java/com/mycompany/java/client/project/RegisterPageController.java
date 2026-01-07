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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ANTER
 */
public class RegisterPageController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<?> genderComboBox;
    @FXML
    private Button registerButton;
    @FXML
    private Button backToLoginButton;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void navigateToLogin(ActionEvent event) {
        try {
            App.setRoot("loginPage");
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
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        Object gender = genderComboBox.getValue();

        // 1. Validation: Check if fields are empty
        if (username.isEmpty() || password.isEmpty() || gender == null) {
            showAlert("Validation Error", "Please fill in all fields.", Alert.AlertType.WARNING);
            return;
        }

        // 2. Connection and Server Logic
        try (ServerConnection conn = ServerConnection.getInstance()) {
            conn.sendRequest("REGISTER");
            conn.sendRequest(username);
            conn.sendRequest(password);
            conn.sendRequest(gender.toString());

            Object resp = conn.readResponse();

            if ("REGISTER_SUCCESS".equals(resp)) {
                showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                App.setRoot("loginPage");
            } else if ("USER_EXISTS".equals(resp)) {
                showAlert("Registration Failed", "This username is already taken.", Alert.AlertType.ERROR);
            } else {
                showAlert("Error", "Server error: " + resp, Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            // This catches "Server is off" (Connection Refused)
            showAlert("Server Offline", "Cannot connect to the server. Please check if it's running.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred.", Alert.AlertType.ERROR);
        }
    }
}
