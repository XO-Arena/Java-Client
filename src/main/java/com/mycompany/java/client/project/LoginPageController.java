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
import dto.LoginDTO;
import enums.RequestType;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import util.DialogUtil;

/**
 * FXML Controller class
 *
 * @author ANTER
 */
public class LoginPageController implements ServerListener, Initializable {

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
    private Gson gson;
    private ServerConnection conn;
    private boolean isSubmited = false;// the default value

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gson = new Gson();
        try {
            conn = ServerConnection.getConnection();
        } catch (IOException ex) {
            System.getLogger(LoginPageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        conn.setListener(this);
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

    @FXML
    private void handleLogin(ActionEvent event) {
        if (isSubmited) {
            return;
        }
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            DialogUtil.showAlert("Validation Error", "Please enter both username and password.", Alert.AlertType.WARNING, this);

            return;
        }
        // if not enter to this if statment
        isSubmited = true;
        loginButton.setDisable(true);
        try {
            LoginDTO loginDto = new LoginDTO(username, password);
            // to convert the json to the class => fromJson(//jsonelement , // class name)
            //  to convert the class to json => toJsonTree(// take object)
            Request rq = new Request(RequestType.LOGIN, gson.toJsonTree(loginDto));
            conn.sendRequest(rq);
        } catch (Exception e) {
            isSubmited = false;
            loginButton.setDisable(false);
            e.printStackTrace();
        }
    }

    private void handleLoginResponse(Response response) {
        Platform.runLater(() -> {
            isSubmited = false;
            loginButton.setDisable(false);
        });
        switch (response.getType()) {
            case LOGIN_SUCCESS: {
                Platform.runLater(() -> {
                    User user = gson.fromJson(
                            response.getPayload(),
                            User.class
                    );

                    // to save in app
                    App.setCurrentUser(user);
                    //showAlert("Login Success", "Login Successfully", Alert.AlertType.INFORMATION); // This is now safe
                    try {
                        App.setLoggedIn(true);
                        App.setRoot("homePage");
                    } catch (IOException ex) {
                        System.getLogger(LoginPageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                });

            }
            break;
            //LOGIN_FAILED
            case LOGIN_FAILED:
                DialogUtil.showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR, this); // This is now safe
                break;

            case ALREADY_LOGGED_IN:
                DialogUtil.showAlert("Login Failed", "User already logged in", Alert.AlertType.ERROR, this);
                break;

            case INVALID_DATA:
                DialogUtil.showAlert("Login Failed", "Invalid login data", Alert.AlertType.ERROR, this);
                break;

            case ERROR:
            default:
                DialogUtil.showAlert("Server Error", "Server error", Alert.AlertType.ERROR, this);
                break;
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

    @Override
    public void onMessage(Response response) {
        // to check it is not null and return the user
        // why toJsonTree because it is a response
        System.out.println("RECEIVED FROM SERVER: " + gson.toJson(response));
        handleLoginResponse(response);
    }

    @Override
    public void onDisconnect() {
        isSubmited = false;
        loginButton.setDisable(false);
        DialogUtil.showAlert("Server Offline", "The server is currently unreachable.", Alert.AlertType.ERROR, this);
    }
}
