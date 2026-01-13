package com.mycompany.java.client.project;

import models.GameRecord;
import com.google.gson.Gson;
import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.AIPlayer;
import models.Player;

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

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            App.setRoot("homePage");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

    private void loadRecordedGames() {
        gameRecords.clear();

        Gson gson = new Gson();
        Path gamesDir = Paths.get("games");

        if (!Files.exists(gamesDir) || !Files.isDirectory(gamesDir)) {
            return;
        }

        try {
            Files.list(gamesDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (FileReader reader = new FileReader(path.toFile())) {
                            GameRecord record = gson.fromJson(reader, GameRecord.class);
                            if (record != null) {
                                gameRecords.add(record);
                            }
                        } catch (Exception e) {}
                    });
        } catch (IOException e) {}
    }


    private void displayGames() {
        if (gameRecords == null || gameRecords.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            populateGamesList();
        }
    }

    private void showEmptyState() {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(true);
            emptyStateContainer.setManaged(true);
        }
    }

    private void hideEmptyState() {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(false);
            emptyStateContainer.setManaged(false);
        }
    }

    private void populateGamesList() {
        // Clear existing items (except empty state)
        gamesListContainer.getChildren().removeIf(node
                -> node != emptyStateContainer
        );

        // Add game cards
        for (GameRecord game : gameRecords) {
            addGameCard(game);
        }
    }

    private void addGameCard(GameRecord game) {
        try {
            // Load the game card FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameCardItem.fxml"));
            HBox gameCard = loader.load();

            // Get the controller and set data
            GameCardItemController controller = loader.getController();
            controller.setGameData(game);

            // Set the play callback
            controller.setRecGameController(this);

            // Add to container
            gamesListContainer.getChildren().add(gameCard);

        } catch (IOException e) {
            System.err.println("Error loading game card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handlePlayGame(GameRecord game) {
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            controller.initPlayers(new Player(game.getPlayer1(), UserGender.MALE, 300, PlayerType.LOCAL, PlayerSymbol.X),new Player(game.getPlayer2(), UserGender.MALE, 300, PlayerType.RECORDED, PlayerSymbol.O) );
            controller.initRecordedGame(game);
        } catch (IOException e) {
            System.err.println("Error loading game replay: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    void handleDeleteGame(GameRecord gameRecord) {
        
    }
}
