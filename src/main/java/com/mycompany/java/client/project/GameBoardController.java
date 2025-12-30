package com.mycompany.java.client.project;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class GameBoardController {

    @FXML
    private Button btn00, btn01, btn02,
                   btn10, btn11, btn12, 
                   btn20, btn21, btn22;
    
    @FXML
    private Label player1Wins, player2Wins, drawsCount;
    
    @FXML
    private ListView<String> spectatorsList;

    private boolean xTurn = true;
    private int[][] boardState = new int[3][3];
    private int p1Score = 0, p2Score = 0, draws = 0;

    @FXML
    private Circle player1Avatar;
    @FXML
    private Label player1Name;
    @FXML
    private Label player1Rank;
    @FXML
    private Label player1Score;
    
    @FXML
    private Circle player2Avatar;
    @FXML
    private Label player2Name;
    @FXML
    private Label player2Rank;
    @FXML
    private Label player2Score1;


    @FXML
    private StackPane gameArea;
    @FXML
    private GridPane gridPane;

    public void initialize() {

        try {
            if (spectatorsList != null) {
                spectatorsList.getItems().addAll("Ahmed", "Sara", "Guest_77", "Bot_Alpha");
            }

            Image img2 = new Image(getClass().getResourceAsStream("/assets/girl.png"));
            player2Avatar.setFill(new ImagePattern(img2));

            Image img1 = new Image(getClass().getResourceAsStream("/assets/boy.png"));
            player1Avatar.setFill(new ImagePattern(img1));

        } catch (Exception e) {
            System.out.println("Resource not found: " + e.getMessage());
        }

        resetBoard();
    }

    @FXML
    private void handleMove(ActionEvent event) {

        Button clickedBtn = (Button) event.getSource();
        String id = clickedBtn.getId();
        int row = Character.getNumericValue(id.charAt(3));
        int col = Character.getNumericValue(id.charAt(4));

        if (boardState[row][col] != 0) {
            return;
        }

        if (xTurn) {
            clickedBtn.setText("X");
            clickedBtn.getStyleClass().add("x-style");
            boardState[row][col] = 1;
        } else {
            clickedBtn.setText("O");
            clickedBtn.getStyleClass().add("o-style");
            boardState[row][col] = 2;
        }

        xTurn = !xTurn;
        checkGameStatus();
    }

    private void checkGameStatus() {
        int winner = checkWinner();
        if (winner != 0) {
            if (winner == 1) {
                p1Score++;
            } else {
                p2Score++;
            }
            updateUI();
            resetBoard();
        } else if (isBoardFull()) {
            draws++;
            updateUI();
            resetBoard();
        }
    }

    private int checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (boardState[i][0] != 0 && boardState[i][0] == boardState[i][1] && boardState[i][1] == boardState[i][2]) {
                return boardState[i][0];
            }
            if (boardState[0][i] != 0 && boardState[0][i] == boardState[1][i] && boardState[1][i] == boardState[2][i]) {
                return boardState[0][i];
            }
        }
        if (boardState[1][1] != 0) {
            if (boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]) {
                return boardState[1][1];
            }
            if (boardState[0][2] == boardState[1][1] && boardState[1][1] == boardState[2][0]) {
                return boardState[1][1];
            }
        }
        return 0;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateUI() {
        player1Wins.setText(String.valueOf(p1Score));
        player2Wins.setText(String.valueOf(p2Score));
        drawsCount.setText(String.valueOf(draws));
    }

    private void resetBoard() {
        Button[] btns = {btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22};
        for (Button b : btns) {
            if (b != null) {
                b.setText("");
                b.getStyleClass().removeAll("x-style", "o-style");
            }
        }
        boardState = new int[3][3];
        xTurn = true;
    }

    @FXML
    private void leaveGame(ActionEvent event) {
         try {
                App.setRoot("homePage");
            } catch (IOException ex) {
                System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
    }
}
