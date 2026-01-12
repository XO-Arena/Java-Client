package com.mycompany.java.client.project;

import enums.GameResult;
import enums.PlayerSymbol;
import models.GameSession;
import models.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import util.DialogUtil;

/**
 * FXML Controller class
 *
 * @author mohan
 */
public class GameResultController implements Initializable {

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
                break;

            case O_WIN:
                player2Crown.setVisible(true);
                break;

            case DRAW:
                // No winner in a draw
                break;

            default:
                break;
        }
    }

    @FXML
    private void handleRematch(ActionEvent event) {
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
}
