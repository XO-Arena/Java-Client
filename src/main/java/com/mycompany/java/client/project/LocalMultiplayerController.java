/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author mohan
 */
public class LocalMultiplayerController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private Circle player2Avatar;
    @FXML
    private TextField player2NameField;
    @FXML
    private Button swapButton;
    @FXML
    private Circle player1Avatar;
    @FXML
    private TextField player1NameField;
    @FXML
    private Button startButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleBack(ActionEvent event) {
    }

    @FXML
    private void handleSwap(ActionEvent event) {
    }

    @FXML
    private void handleStart(ActionEvent event) {
    }
    
}
