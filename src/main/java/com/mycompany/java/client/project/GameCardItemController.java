package com.mycompany.java.client.project;

import models.GameRecord;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameCardItemController implements Initializable {

    @FXML
    private Label player1Label;
    
    @FXML
    private Label player2Label;
    
    @FXML
    private Label winnerLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label movesLabel;
    
    @FXML
    private Button playButton;
    
    private GameRecord gameRecord;
    private RecordedGamesController recGameController;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize if needed
    }
    
  
    public void setGameData(GameRecord game) {
        this.gameRecord = game;
        updateUI();
    }
    
  
    public void setRecGameController(RecordedGamesController controller) {
        this.recGameController = controller;
    }
    
    private void updateUI() {
        if (gameRecord == null) return;
        
        player1Label.setText(gameRecord.getPlayer1());
        player2Label.setText(gameRecord.getPlayer2());
        dateLabel.setText(gameRecord.getDate());
        movesLabel.setText(gameRecord.getMovesCount()+ " moves");
        
        // Set winner label and style
        String winner = gameRecord.getWinner();
        winnerLabel.setText(winner);
        
        // Apply different style for draw
        if ("Draw".equalsIgnoreCase(winner)) {
            winnerLabel.getStyleClass().remove("winner-label");
            winnerLabel.getStyleClass().add("draw-label");
        } else {
            winnerLabel.getStyleClass().remove("draw-label");
            winnerLabel.getStyleClass().add("winner-label");
        }
    }
    
    /**
     * Handles the play button click
     */
    @FXML
    private void handlePlay(ActionEvent event) {
        recGameController.handlePlayGame(gameRecord);
    }
    
    /**
     * Gets the game record
     */
    public GameRecord getGameRecord() {
        return gameRecord;
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        recGameController.handleDeleteGame(gameRecord);
    }
}