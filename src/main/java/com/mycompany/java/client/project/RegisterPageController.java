/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

}
