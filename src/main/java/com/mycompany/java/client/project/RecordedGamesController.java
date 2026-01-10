package com.mycompany.java.client.project;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class for Recorded Games page
 *
 * @author ANTER
 */
public class RecordedGamesController implements Initializable {

    @FXML
    private Button backButton;
    
    @FXML
    private ScrollPane gamesScrollPane;
    
    @FXML
    private VBox gamesListContainer;
    
    @FXML
    private VBox emptyStateContainer;
    
    private List<GameRecord> gameRecords;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup smooth scrolling
        setupSmoothScrolling();
        
        // Initialize game records list
        gameRecords = new ArrayList<>();
        
        // Load recorded games from database or storage
        loadRecordedGames();
        
        // Display games or empty state
        displayGames();
    }
    
    /**
     * Sets up smooth scrolling for the scroll pane
     */
    private void setupSmoothScrolling() {
        if (gamesScrollPane != null) {
            gamesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            gamesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            gamesScrollPane.setFitToWidth(true);
            
            // Smooth scrolling with mouse wheel
            gamesScrollPane.setOnScroll(event -> {
                double deltaY = event.getDeltaY() * 0.003;
                gamesScrollPane.setVvalue(gamesScrollPane.getVvalue() - deltaY);
            });
        }
    }
    
    /**
     * Handles the back button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the previous scene (e.g., main menu or dashboard)
            // Replace "MainMenu.fxml" with your actual previous screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading previous screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Method to load recorded games from database or storage
     */
    private void loadRecordedGames() {
        // TODO: Replace this with actual database loading
        // Example: gameRecords = GameDatabase.getAllRecordedGames();
        
        // Sample data for testing (remove this when you have real data)
        gameRecords.add(new GameRecord(1, "Ahmed", "Mohannad", "Ahmed", "Jan 1, 2026", 12, ""));
        gameRecords.add(new GameRecord(2, "Esraa", "Ahmed", "Draw", "Dec 31, 2025", 9, ""));
        gameRecords.add(new GameRecord(3, "Mohannad", "Esraa", "Esraa", "Dec 30, 2025", 15, ""));
        gameRecords.add(new GameRecord(4, "Ahmed", "Esraa", "Ahmed", "Dec 29, 2025", 10, ""));
        gameRecords.add(new GameRecord(5, "Mohannad", "Ahmed", "Mohannad", "Dec 28, 2025", 13, ""));
    }
    
    /**
     * Displays the games or shows empty state if no games
     */
    private void displayGames() {
        if (gameRecords == null || gameRecords.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            populateGamesList();
        }
    }
    
    /**
     * Shows the empty state message
     */
    private void showEmptyState() {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(true);
            emptyStateContainer.setManaged(true);
        }
    }
    
    /**
     * Hides the empty state message
     */
    private void hideEmptyState() {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(false);
            emptyStateContainer.setManaged(false);
        }
    }
    
    /**
     * Populates the games list with game cards
     */
    private void populateGamesList() {
        // Clear existing items (except empty state)
        gamesListContainer.getChildren().removeIf(node -> 
            node != emptyStateContainer
        );
        
        // Add game cards
        for (GameRecord game : gameRecords) {
            addGameCard(game);
        }
    }
    
    /**
     * Adds a single game card to the list
     */
    private void addGameCard(GameRecord game) {
        try {
            // Load the game card FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameCardItem.fxml"));
            HBox gameCard = loader.load();
            
            // Get the controller and set data
            GameCardItemController controller = loader.getController();
            controller.setGameData(game);
            
            // Set the play callback
            controller.setOnPlayCallback(this::handlePlayGame);
            
            // Add to container
            gamesListContainer.getChildren().add(gameCard);
            
        } catch (IOException e) {
            System.err.println("Error loading game card: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles playing a specific game
     */
    private void handlePlayGame(GameRecord game) {
        try {
            System.out.println("Playing game: " + game);
            
            // Load the game replay screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameReplay.fxml"));
            Parent root = loader.load();
            
            // Pass game data to the replay controller
            // GameReplayController controller = loader.getController();
            // controller.setGameData(game);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) gamesScrollPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading game replay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Public method to add a new game record (can be called from other controllers)
     */
    public void addGameRecord(GameRecord game) {
        if (gameRecords == null) {
            gameRecords = new ArrayList<>();
        }
        gameRecords.add(0, game); // Add to beginning
        displayGames();
    }
    
    /**
     * Public method to refresh the games list
     */
    public void refreshGamesList() {
        loadRecordedGames();
        displayGames();
    }
    
    /**
     * Public method to clear all games
     */
    public void clearAllGames() {
        if (gameRecords != null) {
            gameRecords.clear();
        }
        displayGames();
    }
    
    /**
     * Gets the list of game records
     */
    public List<GameRecord> getGameRecords() {
        return gameRecords;
    }
}