/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java.client.project;

import enums.Difficulty;
import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import models.AIPlayer;
import models.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author mohan
 */
public class DifficultySelectorController implements Initializable {

    @FXML
    private ImageView easyImg;
    @FXML
    private ImageView mediumImg;
    @FXML
    private ImageView hardImg;
    @FXML
    private ImageView impossibleImg;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        easyImg.setImage(new Image(getClass().getResourceAsStream("/assets/easy.png")));
        mediumImg.setImage(new Image(getClass().getResourceAsStream("/assets/medium.png")));
        hardImg.setImage(new Image(getClass().getResourceAsStream("/assets/hard.png")));
        impossibleImg.setImage(new Image(getClass().getResourceAsStream("/assets/impossible.png")));
    }    

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            App.setRoot("homePage");
        } catch (IOException ex) {
            System.getLogger(DifficultySelectorController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private void navigateToGameBoard(Difficulty difficulty) {
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            controller.initPlayers(
                    new Player("You", App.getCurrentUser().getGender(),App.getCurrentUser().getScore() , PlayerType.LOCAL, PlayerSymbol.X),
                    new AIPlayer(difficulty, PlayerSymbol.O));
        } catch (IOException ex) {
            System.getLogger(DifficultySelectorController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void selectEasy(MouseEvent event) {
        navigateToGameBoard(Difficulty.EASY);
    }

    @FXML
    private void selectMedium(MouseEvent event) {
        navigateToGameBoard(Difficulty.MEDIUM);
    }

    @FXML
    private void selectHard(MouseEvent event) {
        navigateToGameBoard(Difficulty.HARD);
    }

    @FXML
    private void selectImpossible(MouseEvent event) {
        navigateToGameBoard(Difficulty.IMPOSSIBLE);
    }
    
}
