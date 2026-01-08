/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import Models.Player;
import java.io.IOException;
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
        try {
            App.setRoot("HomePage");
        } catch (IOException ex) {
            System.getLogger(LocalMultiplayerController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void handleSwap(ActionEvent event) {
    }

    @FXML
    private void handleStart(ActionEvent event) {
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            controller.initPlayers(
                    new Player(player1NameField.getText(), UserGender.Male, 300, PlayerType.LOCAL, PlayerSymbol.X),
                    new Player(player2NameField.getText(), UserGender.Male, 300, PlayerType.LOCAL, PlayerSymbol.O)
            );
        } catch (IOException ex) {
            System.getLogger(LocalMultiplayerController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
}
