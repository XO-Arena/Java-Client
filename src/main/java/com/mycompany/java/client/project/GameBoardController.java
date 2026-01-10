package com.mycompany.java.client.project;

import enums.GameResult;
import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import models.GameSession;
import models.Move;
import models.Player;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.shape.Circle;
import javafx.embed.swing.SwingFXUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javax.imageio.ImageIO;

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
    @FXML
    private Pane gridOverlay;

    private Line winLine;

    public void initialize() {
        initButtonsMap();
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

    public void initPlayers(Player player, Player opponent) {

        player1 = player;
        player2 = opponent;

        session = new GameSession(player1, player2, player2.getType().getSessionType());

        player1Name.setText(player1.getUsername());
        player2Name.setText(player2.getUsername());

        updateBoardUI();
        updateScoreUI();
        highlightCurrentPlayer();
    }

    public void initDummyPlayers(PlayerType type) {
        initPlayers(
                new Player(
                        "Player 1",
                        UserGender.MALE,
                        300,
                        PlayerType.LOCAL,
                        PlayerSymbol.X
                ),
                new Player(
                        "Player 2",
                        UserGender.MALE,
                        300,
                        type,
                        PlayerSymbol.O
                )
        );
    }

    private void endTurn() {
        updateBoardUI();
        handleResult();
        handleNextTurn();
    }

    @FXML
    private void handleMove(ActionEvent event) {

        if (session.isGameEnded()) {
            return;
        }

        Player current = session.getCurrentPlayer();

        if (current.getType() != PlayerType.LOCAL) {
            return;
        }

        Button clickedBtn = (Button) event.getSource();
        String id = clickedBtn.getId();

        int row = Character.getNumericValue(id.charAt(3));
        int col = Character.getNumericValue(id.charAt(4));

        boolean played = session.playMove(row, col);
        if (!played) {
            return;
        }

        endTurn();
    }

    private void handleNextTurn() {

        if (session.isGameEnded()) {
            return;
        }

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

        Move move = session.getMoveProvider().getNextMove();
        session.playMove(move.row, move.col);

        endTurn();
    }

    private void waitForServerMove() {
        System.out.println("Waiting for server move...");
    }

    public void onServerMove(int row, int col) {

        session.playMove(row, col);

        endTurn();
    }

    private void updateBoardUI() {

        PlayerSymbol[][] cells = session.getGame().getBoard().getCells();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                Button btn = buttonsMap.get("" + r + c);

                btn.getStyleClass().removeAll(
                        "x-style", "o-style", "win-cell"
                );

                if (cells[r][c] == PlayerSymbol.X) {
                    btn.setText("X");
                    btn.getStyleClass().add("x-style");
                    btn.setDisable(true);

                } else if (cells[r][c] == PlayerSymbol.O) {
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
                return;

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
        Platform.runLater(this::takeBoardScreenshot);
    }

    private void takeBoardScreenshot() {
        try {
            WritableImage image = gameArea.snapshot(new SnapshotParameters(), null);

            File file = new File("board.png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            System.getLogger(GameBoardController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void highlightWinningCells() {
        String winCode = session.getGame().getBoard().getWinCode();
        if (winCode == null) {
            return;
        }

        String winningClassStyleName = ".winning-cell-p1";
        if (session.getLastResult() != GameResult.X_WIN) {

            winningClassStyleName = ".winning-cell-p2";
        }

        System.out.println("WinCode: " + winCode);

        List<Button> winningButtons = new ArrayList<>();
        for (int i = 0; i < winCode.length(); i += 2) {
            Button cell = buttonsMap.get(winCode.substring(i, i + 2));
            cell.getStyleClass().add(winningClassStyleName);
            winningButtons.add(cell);
        }

        drawWinningLine(winningButtons);
    }

//    private void drawWinningLine(List<Button> cells) {
//        if (cells.size() < 2) return;
//
//        Button first = cells.get(0);
//        Button last  = cells.get(cells.size() - 1);
//
//        Bounds start = first.getBoundsInParent();
//        Bounds end   = last.getBoundsInParent();
//
//        double startX = start.getMinX() + start.getWidth() / 2;
//        double startY = start.getMinY() + start.getHeight() / 2;
//        double endX   = end.getMinX()   + end.getWidth() / 2;
//        double endY   = end.getMinY()   + end.getHeight() / 2;
//
//        Line line = new Line(startX, startY, endX, endY);
//        line.setStrokeWidth(6);
//        line.setStrokeLineCap(StrokeLineCap.ROUND);
//        line.setMouseTransparent(true);
//
//        line.setStroke(
//            session.getGame().checkResult() == GameResult.X_WIN
//                ? Color.web("#2e5bff")
//                : Color.web("#ff5e7e")
//        );
//
//        gridOverlay.getChildren().add(line);
//    }
    private void drawWinningLine(List<Button> cells) {
        if (cells.size() < 2) {
            return;
        }

        // إزالة أي خطوط سابقة
        gridOverlay.getChildren().removeIf(node -> node instanceof Line);

        Platform.runLater(() -> {
            Button first = cells.get(0);
            Button last = cells.get(cells.size() - 1);

            // تحويل المركز لكل زر لإحداثيات overlay
            Point2D startPoint = first.localToScene(first.getWidth() / 2, first.getHeight() / 2);
            Point2D endPoint = last.localToScene(last.getWidth() / 2, last.getHeight() / 2);

            Point2D startInOverlay = gridOverlay.sceneToLocal(startPoint);
            Point2D endInOverlay = gridOverlay.sceneToLocal(endPoint);

            Line line = new Line(startInOverlay.getX(), startInOverlay.getY(),
                    endInOverlay.getX(), endInOverlay.getY());
            line.setStrokeWidth(6);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setMouseTransparent(true);

            GameResult result = session.getGame().checkResult();
            if (result == GameResult.X_WIN) {
                line.setStroke(Color.web("#2e5bff"));
            } else if (result == GameResult.O_WIN) {
                line.setStroke(Color.web("#ff5e7e"));
            } else {
                return;
            }

            gridOverlay.getChildren().add(line);
        });
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
