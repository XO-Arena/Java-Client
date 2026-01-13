package com.mycompany.java.client.project;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.Response;
import com.mycompany.java.client.project.data.ServerConnection;
import com.mycompany.java.client.project.data.ServerListener;
import dto.InvitationDTO;
import dto.PlayerDTO;
import dto.UserDTO;
import dto.GameSessionDTO;
import enums.RequestType;
import static enums.ResponseType.JOIN_GAME;
import java.io.IOException;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Player;
import javafx.util.Duration;
import services.InvitationService;
import util.DialogUtil;

public class HomePageController implements ServerListener {

    @FXML
    private ImageView logoImage;
    @FXML
    private GridPane sidePanel;
    @FXML
    private ListView<AnchorPane> onlinePlayersList;
    @FXML
    private ListView<AnchorPane> leaderboardList;
    @FXML
    private Button recordedGamesButton;
    @FXML
    private Button PlayVsComputerButton;
    @FXML
    private Button localMultiplayerButton;
    @FXML
    private Button quickGameButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label statusText;
     @FXML
    private Label welcomeText;
     
    private ListView<AnchorPane> recordedGamesList;
    private Gson gson;
    private ServerConnection con;
    private Stage currentDialogStage;
    private InvitationService invitationService;

    @FXML
    public void initialize() {

        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/xo.png")));
        gson = new Gson();

        try {
            con = ServerConnection.getConnection();
            gson = new Gson();
            this.invitationService = new InvitationService(con, gson, this::showLoadingAndTransition);
            con.setListener(this);
            con.sendRequest(new Request(RequestType.GET_ONLINE_PLAYERS, null));
            con.sendRequest(new Request(RequestType.GET_LEADERBOARD, null));
        } catch (IOException e) {
            Platform.runLater(() -> {
                loginButton.setDisable(true);
                handleServerOffline();
            });
        }

        updateUIState();
    }

    private void updateUIState() {
        Platform.runLater(() -> {
            if (App.IsLoggedIn()) {
                loginButton.setText("Logout");
                loginButton.getStyleClass().removeAll("login-button");
                loginButton.getStyleClass().add("logout-button");

                statusText.setText("status: Online");
                statusText.getStyleClass().removeAll("status-offline");
                statusText.getStyleClass().add("status-online");
                welcomeText.setText("Welcome, "+App.getCurrentUser().getUsername()+"!");
                sidePanel.setVisible(true);
                sidePanel.setManaged(true);
                quickGameButton.setDisable(false);
            } else {
                loginButton.setText("Login");
                loginButton.getStyleClass().removeAll("logout-button");
                loginButton.getStyleClass().add("login-button");

                statusText.setText("status: Offline");
                statusText.getStyleClass().removeAll("status-online");
                statusText.getStyleClass().add("status-offline");

                handleServerOffline();
            }
        });
    }

    private void updateOnlinePlayersList(JsonElement payload) {
        // Clear list first to avoid duplicates if needed, assuming payload is full list
        Platform.runLater(() -> {
            onlinePlayersList.getItems().clear();
            UserDTO[] availablePlayers = gson.fromJson(payload, UserDTO[].class);
            for (UserDTO availablePlayer : availablePlayers) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerItem.fxml"));
                    AnchorPane playerItem = loader.load();
                    PlayerItemController controller = loader.getController();
                    controller.setPlayerName(availablePlayer.getUsername());
                    controller.setPlayerStatus(availablePlayer.getState());
                    controller.setButtonText(availablePlayer.getState());
                    controller.setHomeController(this);

                    playerItem.setPrefWidth(onlinePlayersList.getPrefWidth() - 10);

                    onlinePlayersList.getItems().add(playerItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleInvite(String playerName) {
    }

    private void updateLeaderboard(JsonElement payload) {
        Platform.runLater(() -> {
            leaderboardList.getItems().clear();
            UserDTO[] players = gson.fromJson(payload, UserDTO[].class);
            for (int i = 0; i < players.length; i++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("LeaderboardItem.fxml"));
                    AnchorPane leaderboardItem = loader.load();

                    LeaderboardItemController controller = loader.getController();

                    int rank = i + 1;
                    controller.setRank("#" + rank);
                    controller.setPlayerName(players[i].getUsername());
                    controller.setScore(players[i].getScore() + "px");
                    leaderboardItem.setPrefWidth(leaderboardList.getPrefWidth() - 10);

                    leaderboardList.getItems().add(leaderboardItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void navigateToLoginPage(ActionEvent event) {
        if (App.IsLoggedIn()) {
            try {
                ServerConnection.getConnection().sendRequest(new Request(RequestType.LOGOUT, null));
            } catch (IOException ex) {
                System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            App.setLoggedIn(false);
            updateUIState();
        } else {
            try {
                ServerConnection.getConnection();
                App.setRoot("loginPage");
            } catch (IOException e) {
                showAlert("Server Offline", "Cannot connect to server", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void navigateToOnlineGameBoardPage(ActionEvent event) {
        Request request = new Request(RequestType.QUICK_GAME);
        if (con.sendRequest(request)) {
            DialogUtil.showBrandedDialog("Quick Game",
                    "Looking for a player...\nYou will enter the game in a while...",
                    false, true,
                    "", "Cancel",
                    null,
                    () -> {
                        con.sendRequest(new Request(RequestType.LEAVE_QUEUE));
                    }
            );
        } else {
            DialogUtil.showBrandedDialog("Connection failed",
                    "Unable to connect to the server.",
                    true, true,
                    "Retry", "Cancel",
                    () -> {
                        navigateToOnlineGameBoardPage(event);
                    },
                    () -> {

                    });
        }
    }

    @FXML
    private void navigateToLocalMultiplayer(ActionEvent event) {
        try {
            App.setRoot("localMultiplayer");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void navigateToRecordedGames(ActionEvent event) {
        try {
            App.setRoot("recordedGames");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void onPlayVsPcClicked(ActionEvent event) {
        try {
            App.setRoot("difficultySelector");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public void onMessage(Response response) {
        if (response.getType() == null) {
            return;
        }
        switch (response.getType()) {

            case ONLINE_PLAYERS:
                updateOnlinePlayersList(response.getPayload());
                break;

            case LEADERBOARD:
                updateLeaderboard(response.getPayload());
                break;
            case JOIN_GAME:
                DialogUtil.closeCurrentDialog();
                handleGameJoin(response.getPayload());
                break;
            case GAME_STARTED:
                DialogUtil.closeCurrentDialog();
                handleGameStarted(response.getPayload());
                break;
                
            case GAME_INVITE:
                invitationService.handleReceivedInvite(response.getPayload());
                break;

            case INVITE_ACCEPTED:
                invitationService.onInvitationAccepted(response.getPayload());
                break;

            case INVITE_REJECTED:
                Stage stage = (Stage) onlinePlayersList.getScene().getWindow();
                invitationService.onInvitationRejected(stage);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDisconnect() {
        App.setLoggedIn(false);
        loginButton.setDisable(true);
        updateUIState();
        handleServerOffline();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        javafx.stage.Stage stage = (javafx.stage.Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    private void handleServerOffline() {

        Platform.runLater(() -> {
            sidePanel.setVisible(false);
            sidePanel.setManaged(false);
            quickGameButton.setDisable(true);
        });
    }

    @FXML
    private void onChangeIP(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("127.0.0.1");
        dialog.setTitle("Server Settings");
        dialog.setHeaderText("Server Configuration");
        dialog.setContentText("Enter Server IP:");

        javafx.stage.Stage stage = (javafx.stage.Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        ImageView dialogLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/xo.png")));
        dialogLogo.setFitHeight(50);
        dialogLogo.setFitWidth(50);
        dialog.setGraphic(dialogLogo);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        dialog.showAndWait().ifPresent(this::attemptConnection);
    }

    private void attemptConnection(String ip) {
        new Thread(() -> {
            try {
                ServerConnection.changeServer(ip, 4646);
                ServerConnection conn = ServerConnection.getConnection();
                conn.reconnect();
                conn.setListener(this);

                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    updateUIState();
                    showSuccessAlert("Connected", "Successfully connected to " + ip);
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(true);
                    App.setLoggedIn(false);
                    updateUIState();
                    showAlert("Connection Failed", "Server at " + ip + " is unreachable.", Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        javafx.stage.Stage stage = (javafx.stage.Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        ImageView successIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/xo.png")));
        successIcon.setFitHeight(40);
        successIcon.setFitWidth(40);
        alert.setGraphic(successIcon);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        alert.showAndWait();
    }

    private void handleGameJoin(JsonElement json) {
        Platform.runLater(() -> {

            try {
                GameBoardController controller = App.setRoot("GameBoardPage").getController();
                PlayerDTO[] playerDTO = gson.fromJson(json, PlayerDTO[].class);
                controller.initPlayers(Player.fromPlayerDto(playerDTO[0]), Player.fromPlayerDto(playerDTO[1]));
                System.out.println("Join game:\n" + json);
            } catch (IOException ex) {
                System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
    
    private void handleGameStarted(JsonElement json) {
        Platform.runLater(() -> {
            try {
                GameBoardController controller = App.setRoot("GameBoardPage").getController();
                GameSessionDTO dto = gson.fromJson(json, GameSessionDTO.class);
                controller.initOnlineGame(dto);
            } catch (IOException ex) {
                System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
    
    public void sendInvite(String receiverName, PlayerItemController itemController) {
        Stage stage = (Stage) onlinePlayersList.getScene().getWindow();
        String currentUsername = "hunter";

        invitationService.initiateInvitation(receiverName, currentUsername, itemController, stage);
    }

    private void showLoadingAndTransition(InvitationDTO gameInfo) {
        Platform.runLater(() -> {
            Alert loadingAlert = new Alert(Alert.AlertType.NONE);
            loadingAlert.setTitle("Starting Game");

            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setStyle("-fx-progress-color: #22c55e;");
            progressIndicator.setPrefSize(60, 60);

            Label statusLabel = new Label("Joining match against " + gameInfo.getSenderUsername() + "...");
            statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            VBox loadingVBox = new VBox(25, progressIndicator, statusLabel);
            loadingVBox.setAlignment(javafx.geometry.Pos.CENTER);
            loadingVBox.setPadding(new javafx.geometry.Insets(40));
            loadingVBox.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 15;");

            loadingAlert.getDialogPane().setContent(loadingVBox);
            loadingAlert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
            loadingAlert.getDialogPane().getStyleClass().add("custom-waiting-pane");

            loadingAlert.show();

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                loadingAlert.setResult(ButtonType.OK);
                loadingAlert.hide();
                loadingAlert.close();

                navigateToGameBoard(gameInfo);
            });
            pause.play();
        });
    }

    private void navigateToGameBoard(InvitationDTO gameInfo) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameBoardPage.fxml"));
                Parent root = loader.load();

                GameBoardController controller = loader.getController();

                if (controller != null) {
//                    controller.initGameData(gameInfo);
                }

                App.setRoot(root);

            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Error navigating to GameBoard: " + ex.getMessage());
            }
        });
    }

}
