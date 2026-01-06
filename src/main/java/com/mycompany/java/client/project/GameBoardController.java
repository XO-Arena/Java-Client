package com.mycompany.java.client.project;

import Enums.GameResult;
import Enums.PlayerSymbol;
import Enums.PlayerType;
import Enums.SessionType;
import Enums.UserGender;
import Models.ComputerMoveProvider;
import Models.GameSession;
import Models.Move;
import Models.MoveProvider;
import Models.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameBoardController {

    @FXML
    private Button btn00, btn01, btn02,
                   btn10, btn11, btn12,
                   btn20, btn21, btn22;

    @FXML
    private Label player1Wins, player2Wins, drawsCount;
    @FXML
    private Label player1Name, player2Name;
    @FXML
    private Label player1Rank, player2Rank;

    @FXML
    private Circle player1Avatar, player2Avatar;

    @FXML
    private ListView<String> spectatorsList;


    private Map<String, Button> buttonsMap;
    private GameSession session;
    private Player player1;
    private Player player2;
    @FXML
    private Label player1Score;
    @FXML
    private StackPane gameArea;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label player2Score1;

   

    public void initialize() {

        initButtonsMap();
        initPlayers();
        initSession(SessionType.AI); 

        updateBoardUI();
        updateScoreUI();
        highlightCurrentPlayer();
    }

    private void initButtonsMap() {
        buttonsMap = new HashMap<>();
        buttonsMap.put("00", btn00);
        buttonsMap.put("01", btn01);
        buttonsMap.put("02", btn02);
        buttonsMap.put("10", btn10);
        buttonsMap.put("11", btn11);
        buttonsMap.put("12", btn12);
        buttonsMap.put("20", btn20);
        buttonsMap.put("21", btn21);
        buttonsMap.put("22", btn22);
    }

    private void initPlayers() {

        player1 = new Player(
                "Player 1",
                UserGender.Male,
                0,
                PlayerType.LOCAL,
                PlayerSymbol.X_SYMBOL
        );

        player2 = new Player(
                "Computer",
                UserGender.Female,
                0,
                PlayerType.ONLINE,
                PlayerSymbol.O_SYMBOL
        );

        player1Name.setText(player1.getUserName());
        player2Name.setText(player2.getUserName());
    }

    private void initSession(SessionType type) {
        session = new GameSession(player1, player2, type);
    }

    

    @FXML
    private void handleMove(ActionEvent event) {

        if (session.isGameEnded()) return;

        Player current = session.getCurrentPlayer();
        
        if (current.getType() != PlayerType.LOCAL) return;

        Button clickedBtn = (Button) event.getSource();
        String id = clickedBtn.getId();

        int row = Character.getNumericValue(id.charAt(3));
        int col = Character.getNumericValue(id.charAt(4));

        boolean played = session.playMove(row, col);
        if (!played) return;

        updateBoardUI();
        handleResult();
        handleNextTurn();
    }

    private void handleNextTurn() {

        if (session.isGameEnded()) return;

        highlightCurrentPlayer();

        Player current = session.getCurrentPlayer();

        switch (current.getType()) {

            case COMPUTER:
                playComputerMove();
                break;

            case ONLINE:
                waitForServerMove();
                break;

            case LOCAL:
                break;
        }
    }

    private void playComputerMove() {

        MoveProvider provider =
                new ComputerMoveProvider(session.getGame().getBoard());

        Move move = provider.getNextMove();
        session.playMove(move.row, move.col);

        updateBoardUI();
        handleResult();
        handleNextTurn();
    }

    private void waitForServerMove() {
        System.out.println("Waiting for server move...");
    }

    public void onServerMove(int row, int col) {

        session.playMove(row, col);

        updateBoardUI();
        handleResult();
        handleNextTurn();
    }

    

    private void updateBoardUI() {

        int[][] cells = session.getGame().getBoard().getCells();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                Button btn = buttonsMap.get("" + r + c);

                btn.getStyleClass().removeAll(
                        "x-style", "o-style", "win-cell"
                );

                if (cells[r][c] == 1) {
                    btn.setText("X");
                    btn.getStyleClass().add("x-style");
                    btn.setDisable(true);

                } else if (cells[r][c] == 2) {
                    btn.setText("O");
                    btn.getStyleClass().add("o-style");
                    btn.setDisable(true);
                }
            }
        }
    }

    private void updateScoreUI() {
        player1Wins.setText(String.valueOf(session.getPlayer1Wins()));
        player2Wins.setText(String.valueOf(session.getPlayer2Wins()));
        drawsCount.setText(String.valueOf(session.getDrawCount()));
    }

    
    private void handleResult() {

        GameResult result = session.getLastResult();

        switch (result) {

            case NONE:
                break;

            case X_WIN:
            case O_WIN:
                highlightWinningCells();
//                playWinSound();
                disableBoard();
                updateScoreUI();
                break;

            case DRAW:
                disableBoard();
                updateScoreUI();
                break;
        }
    }

    private void highlightWinningCells() {

    }

    private void highlightCurrentPlayer() {
    
        player1Avatar.getStyleClass().remove("avatar-fancy");
        
        player2Avatar.getStyleClass().remove("avatar-fancy-p2");

        Player current = session.getCurrentPlayer();

        if (current == player1) {
            player1Avatar.getStyleClass().add("avatar-fancy");
        } else {
            player2Avatar.getStyleClass().add("avatar-fancy-p2");
        }
    }

    private void disableBoard() {
        for (Button btn : buttonsMap.values()) {
            btn.setDisable(true);
        }
    }


    private void showAlert(String message) {
        System.out.println(message);
    }

    @FXML
    private void leaveGame(ActionEvent event) {
        try {
            App.setRoot("homePage");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
