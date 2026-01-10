package com.mycompany.java.client.project;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.Response;
import com.mycompany.java.client.project.data.ServerConnection;
import com.mycompany.java.client.project.data.ServerListener;
import dto.PlayerDTO;
import enums.PlayerType;
import enums.RequestType;
import static enums.ResponseType.JOIN_GAME;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

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
    private ListView<AnchorPane> recordedGamesList;

    private ServerConnection con;

    @FXML
    public void initialize() {

        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        con = ServerConnection.getConnection();
        con.setListener(this);

        updateUIState();

        ServerConnection conn = ServerConnection.getConnection();
        conn.setListener(this);
        conn.sendRequest(new Request(RequestType.GET_ONLINE_PLAYERS, null));
        conn.sendRequest(new Request(RequestType.GET_LEADERBOARD, null));

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
        PlayerDTO[] availablePlayers = new Gson().fromJson(payload, PlayerDTO[].class);
        for (PlayerDTO availablePlayer : availablePlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerItem.fxml"));
                AnchorPane playerItem = loader.load();
                PlayerItemController controller = loader.getController();
                controller.setPlayerName(availablePlayer.getUsername());
                controller.setPlayerStatus(availablePlayer.getState());
                controller.setButtonText(availablePlayer.getState());
                playerItem.setPrefWidth(onlinePlayersList.getPrefWidth() - 10);

                onlinePlayersList.getItems().add(playerItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLeaderboard(JsonElement payload) {
        PlayerDTO[] players = new Gson().fromJson(payload, PlayerDTO[].class);
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
    }

    @FXML
    private void navigateToLoginPage(ActionEvent event) {
        if (App.IsLoggedIn()) {
            ServerConnection.getConnection().sendRequest(new Request(RequestType.LOGOUT, null));
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
        con.sendRequest(request);
        showAlert("Looking for a player...", "You will enter the game in a while...", Alert.AlertType.WARNING);
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
                handleGameJoin(response.getPayload());
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
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            System.out.println("Join game:\n" + json);
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
