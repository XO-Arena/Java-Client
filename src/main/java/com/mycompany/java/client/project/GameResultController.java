package com.mycompany.java.client.project;

import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.Response;
import com.mycompany.java.client.project.data.ServerConnection;
import com.mycompany.java.client.project.data.ServerListener;
import dto.GameSessionDTO;
import enums.RequestType;
import enums.ResponseType;
import enums.SessionType;
import models.GameRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.GameResult;
import java.io.FileWriter;
import models.GameSession;
import models.Player;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import util.DialogUtil;

/**
 * FXML Controller class
 *
 * @author mohan
 */
public class GameResultController implements Initializable, ServerListener {
    @FXML
    private Circle player1Avatar;
    @FXML
    private ImageView player1Crown;
    @FXML
    private Label player1Name;
    @FXML
    private Label player1Symbol;
    @FXML
    private Circle player2Avatar;
    @FXML
    private ImageView player2Crown;
    @FXML
    private Label player2Name;
    @FXML
    private Label player2Symbol;
    @FXML
    private Button leaveButton;
    @FXML
    private Button rematchButton;
    
    private GameSession session;
    private Player player1;
    private Player player2;
    private String winner;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize if needed
    }

    public void initGameResult(GameSession gameSession, Player p1, Player p2) {
        // why ? to return the session that or to containu the session with rematch
        this.session = gameSession;
        this.player1 = p1;
        this.player2 = p2;
        
        if (session.getSessionType() == SessionType.ONLINE) {
             try {
                ServerConnection.getConnection().setListener(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        displayPlayerInfo();
        displayGameResult();
    }

    private void displayPlayerInfo() {
        player1Name.setText(player1.getUsername());
        player2Name.setText(player2.getUsername());
        player1Symbol.setText(player1.getSymbol().toString());
        player2Symbol.setText(player2.getSymbol().toString());
    }

    private void displayGameResult() {
        GameResult result = session.getLastResult();

        player1Crown.setVisible(false);
        player2Crown.setVisible(false);

        switch (result) {
            case X_WIN:
                player1Crown.setVisible(true);
                winner = session.getPlayer1().getUsername();
                break;

            case O_WIN:
                player2Crown.setVisible(true);
                winner = session.getPlayer2().getUsername();
                break;

            case DRAW:
                winner = result.name();
                break;

            default:
                break;
        }
    }

    @FXML
    private void handleRematch(ActionEvent event) {
        if (session.getSessionType() == SessionType.ONLINE) {
            Button btn = (Button) event.getSource();
            btn.setDisable(true);
            btn.setText("Waiting...");
            
            try {
                Request req = new Request(RequestType.REMATCH_REQUEST, new Gson().toJsonTree(session.getSessionId()));
                ServerConnection.getConnection().sendRequest(req);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            // send the current session to increase the wins and loses
            controller.continueSession(session, player1, player2);
        } catch (IOException ex) {
            System.getLogger(GameResultController.class.getName())
                    .log(System.Logger.Level.ERROR, "Failed to start rematch", ex);
        }
    }

    @FXML
    private void handleSaveGame(ActionEvent event) {
        leaveButton.setDisable(true); 
        rematchButton.setDisable(true);
        ((Button) event.getSource()).setDisable(true);
        GameRecord record = new GameRecord(
                System.currentTimeMillis(),
                session.getPlayer1().getUsername(),
                session.getPlayer2().getUsername(),
                winner,
                LocalDate.now().toString(),
                session.getGame().getMoves()
        );

        boolean saved = saveGameToFile(record);

        if (saved) {
            DialogUtil.showInfoDialog(
                    "Game Saved",
                    "The game was saved successfully 🎉"
            ); 
            ((Button) event.getSource()).setText("Saved!");
            ((Button) event.getSource()).setDisable(true);
        } else {
            DialogUtil.showErrorDialog(
                    "Save Failed",
                    "Something went wrong while saving the game."
            );
        }
        leaveButton.setDisable(false); 
        rematchButton.setDisable(false);
    }

    @FXML
    private void handleLeaveMatch(ActionEvent event) {

        DialogUtil.showBrandedDialog(
                "Leave Game",
                "Are you sure you want to leave this match?",
                true, // show primary
                true, // show secondary

                "Leave",
                "Cancel",
                () -> { // Primary action
                    try {
                        DialogUtil.closeCurrentDialog();
                        App.setRoot("homePage");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                () -> { // Secondary action
                    DialogUtil.closeCurrentDialog();
                }
        );

    }

    public void onMessage(Response response) {
        if (response.getType() == ResponseType.REMATCH_REQUESTED) {
            Platform.runLater(() -> {
                 Pane parent = (Pane) player1Name.getScene().getRoot();
                 Label msg = new Label("Opponent wants a rematch!");
                 msg.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-background-radius: 5px;");
                 msg.setLayoutX(parent.getWidth() / 2 - 100);
                 msg.setLayoutY(parent.getHeight() - 100);
                 parent.getChildren().add(msg);
            });
        } else if (response.getType() == ResponseType.GAME_STARTED || response.getType() == ResponseType.GAME_UPDATE) {
             GameSessionDTO dto = new Gson().fromJson(response.getPayload(), GameSessionDTO.class);
             Platform.runLater(() -> {
                 try {
                    GameBoardController controller = App.setRoot("GameBoardPage").getController();
                    controller.initOnlineGame(dto);
                 } catch (IOException ex) {
                    ex.printStackTrace();
                 }
             });
        }
    }

    @Override
    public void onDisconnect() {
        // Handle disconnect if needed
    }

    private boolean saveGameToFile(GameRecord record) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path dir = Paths.get("games");

        try {
            Files.createDirectories(dir);

            Path filePath = dir.resolve(record.getId() + ".json");
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(record, writer);
            }
            return true;

        } catch (IOException ex) {
            System.getLogger(GameResultController.class.getName())
                    .log(System.Logger.Level.ERROR, "Failed to save game", ex);
            return false;
        }
    }

}
