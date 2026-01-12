package com.mycompany.java.client.project;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for individual game card items
 * 
 * @author ANTER
 */
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
    private Consumer<GameRecord> onPlayCallback;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize if needed
    }
    
    /**
     * Sets the game data for this card
     */
    public void setGameData(GameRecord game) {
        this.gameRecord = game;
        updateUI();
    }
    
    /**
     * Sets the callback function to be called when play button is clicked
     */
    public void setOnPlayCallback(Consumer<GameRecord> callback) {
        this.onPlayCallback = callback;
    }
    
    /**
     * Updates the UI with game data
     */
    private void updateUI() {
        if (gameRecord == null) return;
        
        player1Label.setText(gameRecord.getPlayer1());
        player2Label.setText(gameRecord.getPlayer2());
        dateLabel.setText(gameRecord.getDate());
        movesLabel.setText(gameRecord.getMoves() + " moves");
        
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
        if (onPlayCallback != null && gameRecord != null) {
            onPlayCallback.accept(gameRecord);
        }
    }
    
    /**
     * Gets the game record
     */
    public GameRecord getGameRecord() {
        return gameRecord;
    }

    @FXML
    private void handleDelete(ActionEvent event) {
    }
}