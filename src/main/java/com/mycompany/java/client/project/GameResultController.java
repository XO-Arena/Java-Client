package com.mycompany.java.client.project;

import com.google.gson.Gson;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.Response;
import com.mycompany.java.client.project.data.ServerConnection;
import com.mycompany.java.client.project.data.ServerListener;
import dto.GameSessionDTO;
import enums.GameResult;
import enums.RequestType;
import enums.ResponseType;
import enums.SessionType;
import models.GameSession;
import models.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

    private GameSession session;
    private Player player1;
    private Player player2;
    @FXML
    private Button leaveButton;

    // Images for crown and clown hat
    private Image crownImage;
    private Image clownHatImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load images
        try {
            crownImage = new Image(getClass().getResourceAsStream("/assets/crown.png"));
            clownHatImage = new Image(getClass().getResourceAsStream("/assets/clown.png"));
        } catch (Exception e) {
            System.err.println("Failed to load crown or clown hat images: " + e.getMessage());
        }
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
                // Player 1 wins - show crown
                player1Crown.setImage(crownImage);
                player1Crown.setVisible(true);
                // Player 2 loses - show clown hat
                player2Crown.setImage(clownHatImage);
                player2Crown.setVisible(true);
                break;

            case O_WIN:
                // Player 2 wins - show crown
                player2Crown.setImage(crownImage);
                player2Crown.setFitWidth(100);  // Set width
                player2Crown.setFitHeight(100); // Set height
                player2Crown.setVisible(true);
                // Player 1 loses - show clown hat
                player1Crown.setImage(clownHatImage);
                player1Crown.setFitWidth(100);  // Set width
                player1Crown.setFitHeight(100); // Set height
                player1Crown.setVisible(true);
                break;

            case DRAW:
                // No winner in a draw - no crowns or hats
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

    @Override
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
}
