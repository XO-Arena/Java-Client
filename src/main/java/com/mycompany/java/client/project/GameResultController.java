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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
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
        this.session = gameSession;
        this.player1 = p1;
        this.player2 = p2;

        if (session.getSessionType() == SessionType.ONLINE) {
            try {
                ServerConnection.getConnection().setListener(this);
                if (session.isOpponentLeft()) {
                    if (rematchButton != null) {
                        rematchButton.setDisable(true);
                        rematchButton.setText("Opponent Left");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        displayGameResult();
    }

    private void displayGameResult() {
        GameResult result = session.getLastResult();

        // Hide crowns initially
        player1Crown.setVisible(false);
        player2Crown.setVisible(false);

        // Determine winner and loser
        Player winnerPlayer = null;
        Player loserPlayer = null;

        switch (result) {
            case X_WIN:
                winnerPlayer = player1;  // X player wins
                loserPlayer = player2;   // O player loses
                this.winner = player1.getUsername();
                break;

            case O_WIN:
                winnerPlayer = player2;  // O player wins
                loserPlayer = player1;   // X player loses
                this.winner = player2.getUsername();
                break;

            case DRAW:
                this.winner = "DRAW";
                // Display both players normally without crowns
                displayPlayer(player1, player2Name, player1Symbol, player1Avatar, player1Crown, null);
                displayPlayer(player2, player1Name, player2Symbol, player2Avatar, player2Crown, null);
                return;
        }

        // Display winner on left with crown
        displayPlayer(winnerPlayer, player2Name, player1Symbol, player1Avatar, player1Crown, crownImage);

        // Display loser on right with clown hat
        displayPlayer(loserPlayer, player1Name, player2Symbol, player2Avatar, player2Crown, clownHatImage);
    }

    private void displayPlayer(Player player, Label nameLabel, Label symbolLabel,
            Circle avatarCircle, ImageView crownView, Image crownImage) {
        // Set player name
        nameLabel.setText(player.getUsername());

        // Set player symbol
        symbolLabel.setText(player.getSymbol().toString());

        // Set avatar image in circle
        setAvatarImage(player, avatarCircle);

        // Set crown/hat if provided
        if (crownImage != null) {
            crownView.setImage(crownImage);
            crownView.setFitWidth(70);
            crownView.setFitHeight(70);
            crownView.setVisible(true);
        } else {
            crownView.setVisible(false);
        }
    }

    private void setAvatarImage(Player player, Circle avatarCircle) {
        try {
            // Get avatar path based on player gender
            String avatarPath = getAvatarPath(player);

            if (avatarPath != null) {
                Image avatarImage = new Image(getClass().getResourceAsStream(avatarPath));
                avatarCircle.setFill(new ImagePattern(avatarImage));
            }
        } catch (Exception e) {
            System.err.println("Failed to load avatar for " + player.getUsername() + ": " + e.getMessage());
            // Keep default fill if image fails to load
        }
    }

    private String getAvatarPath(Player player) {
        // Customize this based on your avatar naming convention
        // Example: /assets/male_avatar.png or /assets/female_avatar.png

        switch (player.getGender()) {
            case MALE:
                return "/assets/boy.png";
            case FEMALE:
                return "/assets/girl.png";
            default:
                return "/assets/boy.png";
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
                player1.getUsername(),
                player2.getUsername(),
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
                        if (session.getSessionType() == SessionType.ONLINE) {
                            Request req = new Request(RequestType.LEAVE_GAME, new Gson().toJsonTree(session.getSessionId()));
                            ServerConnection.getConnection().sendRequest(req);
                        }

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
        } else if (response.getType() == ResponseType.OPPONENT_LEFT) {
            Platform.runLater(() -> {
                if (rematchButton != null) {
                    rematchButton.setDisable(true);
                    rematchButton.setText("Opponent Left");
                }
                Pane parent = (Pane) player1Name.getScene().getRoot();
                Label msg = new Label("Opponent has left the game.");
                msg.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-background-radius: 5px;");
                msg.setLayoutX(parent.getWidth() / 2 - 100);
                msg.setLayoutY(parent.getHeight() - 150);
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
