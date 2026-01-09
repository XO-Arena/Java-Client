/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import com.google.gson.Gson;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.Response;
import com.mycompany.java.client.project.data.ServerConnection;
import com.mycompany.java.client.project.data.ServerListener;
import dto.RegisterDTO;
import enums.RequestType;
import enums.UserGender;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
public class RegisterPageController implements Initializable, ServerListener {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<UserGender> genderComboBox;

    @FXML
    private Button registerButton;
    @FXML
    private Button backToLoginButton;

    /**
     * Initializes the controller class.
     */
    private ServerConnection conn;
    private Gson gson;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gson = new Gson();
        try {
            conn = ServerConnection.getConnection();
        } catch (IOException ex) {
            System.getLogger(LoginPageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        conn.setListener(this);
        genderComboBox.getItems().setAll(UserGender.values());
    }

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
        UserGender gender = genderComboBox.getValue();
        if (username.isEmpty() || password.isEmpty() || gender == null) {
            showAlert("Validation Error", "Please fill in all fields.", Alert.AlertType.WARNING);
            return;
        }
        try {
            RegisterDTO registerDto = new RegisterDTO(username, password, gender);
            Request rq = new Request(RequestType.REGISTER, gson.toJsonTree(registerDto));
            conn.sendRequest(rq);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleRegisterResponse(Response response) {
        switch (response.getType()) {

            case REGISTER_SUCCESS:
                Platform.runLater(() -> {
                    showAlert("Registration Success", "Account created successfully", Alert.AlertType.INFORMATION);
                });
                 {
                    try {
                        App.setRoot("loginPage");
                    } catch (IOException ex) {
                        System.getLogger(RegisterPageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                }
                break;

            case USER_EXISTS:
                Platform.runLater(() -> {
                    showAlert("Registration Error", "Username already exists", Alert.AlertType.ERROR);
                });
                break;

            case INVALID_DATA:
                Platform.runLater(() -> {
                    showAlert("Registration Error", "Invalid registration data", Alert.AlertType.ERROR);
                });

                break;
            case ERROR:
            default:
                Platform.runLater(() -> {
                    showAlert("Server Error", "Server error occurred", Alert.AlertType.ERROR);
                });
                break;
        }
    }

    @Override
    public void onMessage(Response response) {
        handleRegisterResponse(response);
    }

    @Override
    public void onDisconnect() {
        showAlert("Server Offline", "Cannot connect to the server. Please check if it's running.", Alert.AlertType.ERROR);
    }
}
