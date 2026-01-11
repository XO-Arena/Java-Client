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
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javax.imageio.ImageIO;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.DialogUtil;

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
    private Pane gridOverlay;

    private Line winLine;
    @FXML
    private StackPane player1Container;
    @FXML
    private Label player2Score;
    @FXML
    private StackPane player2Container;
    @FXML
    private TextField label_turn;

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

    public void continueSession(GameSession existingSession, Player p1, Player p2) {
        this.session = existingSession;
        this.player1 = p1;
        this.player2 = p2;

        // Start a new game in the same session
        session.startNewGame();

        player1Name.setText(player1.getUsername());
        player2Name.setText(player2.getUsername());

        // Reset the board UI
        resetBoard();
        updateBoardUI();
        updateScoreUI();
        highlightCurrentPlayer();
    }

    // is that important too ??
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
                navigateToGameResult();
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

        String winningClassStyleName = "winning-cell-p1";
        if (session.getLastResult() != GameResult.X_WIN) {

            winningClassStyleName = "winning-cell-p2";
        }

        System.out.println("WinCode: " + winCode);

        List<Button> winningButtons = new ArrayList<>();
        for (int i = 0; i < winCode.length(); i += 2) {
            Button cell = buttonsMap.get(winCode.substring(i, i + 2));
            cell.getStyleClass().add(winningClassStyleName);
            winningButtons.add(cell);
        }

        drawWinningLine(winningButtons);
        // TODO: navigate to the game result 
        // now we want to navigate the names with its score 
        navigateToGameResult();
    }

    private void navigateToGameResult() {

        // make a little delay to show the winning line
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            try {
                GameResultController controller = App.setRoot("gameResult").getController();
                controller.initGameResult(session, player1, player2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    private void drawWinningLine(List<Button> cells) {

        if (cells.size() < 2) {
            return;
        }

        gridOverlay.getChildren().removeIf(n -> n instanceof Line);

        Platform.runLater(() -> {

            Button first = cells.get(0);
            Button last = cells.get(cells.size() - 1);

            Line line = new Line();
            line.setStrokeWidth(6);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setMouseTransparent(true);

            line.startXProperty().bind(
                    Bindings.createDoubleBinding(() -> {
                        Point2D p = first.localToScene(first.getWidth() / 2, first.getHeight() / 2);
                        return gridOverlay.sceneToLocal(p).getX();
                    },
                            first.layoutBoundsProperty(),
                            first.localToSceneTransformProperty(),
                            gridOverlay.layoutBoundsProperty()
                    ));

            line.startYProperty().bind(
                    Bindings.createDoubleBinding(() -> {
                        Point2D p = first.localToScene(first.getWidth() / 2, first.getHeight() / 2);
                        return gridOverlay.sceneToLocal(p).getY();
                    },
                            first.layoutBoundsProperty(),
                            first.localToSceneTransformProperty(),
                            gridOverlay.layoutBoundsProperty()
                    ));

            line.endXProperty().bind(
                    Bindings.createDoubleBinding(() -> {
                        Point2D p = last.localToScene(last.getWidth() / 2, last.getHeight() / 2);
                        return gridOverlay.sceneToLocal(p).getX();
                    },
                            last.layoutBoundsProperty(),
                            last.localToSceneTransformProperty(),
                            gridOverlay.layoutBoundsProperty()
                    ));

            line.endYProperty().bind(
                    Bindings.createDoubleBinding(() -> {
                        Point2D p = last.localToScene(last.getWidth() / 2, last.getHeight() / 2);
                        return gridOverlay.sceneToLocal(p).getY();
                    },
                            last.layoutBoundsProperty(),
                            last.localToSceneTransformProperty(),
                            gridOverlay.layoutBoundsProperty()
                    ));

            GameResult result = session.getGame().checkResult();
            if (result == GameResult.X_WIN) {
                line.setStroke(Color.web("#2E5BFFE6"));
            } else if (result == GameResult.O_WIN) {
                line.setStroke(Color.web("#FF5E7EE6"));
            }

            gridOverlay.getChildren().add(line);
        });
    }

    private void highlightCurrentPlayer() {
        player1Container.getStyleClass().removeAll("p1-glow", "p2-glow");
        player2Container.getStyleClass().removeAll("p1-glow", "p2-glow");

        Player current = session.getCurrentPlayer();

        label_turn.getStyleClass().removeAll("turn-x", "turn-o");

        if (current == player1) {
            player1Container.getStyleClass().add("p1-glow");
            label_turn.setText("PLAYER X TURN");
            label_turn.getStyleClass().add("turn-x");
        } else if (current == player2) {
            player2Container.getStyleClass().add("p2-glow");
            label_turn.setText("PLAYER O TURN");
            label_turn.getStyleClass().add("turn-o");
        }
    }

    private void disableBoard() {
        for (Button btn : buttonsMap.values()) {
            btn.setDisable(true);
        }
    }

    private void resetBoard() {
        // Clear the winning line if it exists
        if (gridOverlay != null) {
            gridOverlay.getChildren().removeIf(n -> n instanceof Line);
        }

        // Re-enable and clear all buttons
        for (Button btn : buttonsMap.values()) {
            btn.setDisable(false);
            btn.setText("");
            btn.getStyleClass().removeAll("x-style", "o-style", "win-cell", "winning-cell-p1", "winning-cell-p2");
        }
    }

    // is it important ??
    private void showAlert(String message) {
        System.out.println(message);
    }

    @FXML
    private void leaveGame(ActionEvent event) {

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
