package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mycompany.java.client.project.PlayerItemController;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.ServerConnection;
import dto.InvitationDTO;
import enums.InvitationStatus;
import enums.RequestType;
import java.util.Optional;

import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Ahmed_El_Sayyad
 */
public class InvitationService {

    private final ServerConnection conn;
    private final Gson gson;
    private final Consumer<InvitationDTO> onGameStart;

    private Timeline timeline;
    private Alert waitingAlert;
    private PlayerItemController pendingController;
    private int remainingSeconds;
    private static final int TIMEOUT_SECONDS = 10;

    public InvitationService(ServerConnection conn, Gson gson, Consumer<InvitationDTO> onGameStart) {
        this.conn = conn;
        this.gson = gson;
        this.onGameStart = onGameStart;
    }

    public void initiateInvitation(String receiverName, String currentUsername, PlayerItemController sourceController, Stage stage) {
        this.pendingController = sourceController;
        updateUIForPendingState();
        try {
            InvitationDTO inviteDTO = new InvitationDTO(currentUsername, receiverName, InvitationStatus.REQUEST);
            Request request = new Request(RequestType.INVITE, gson.toJsonTree(inviteDTO));
            conn.sendRequest(request);
            showWaitingPopup(receiverName, stage);
        } catch (Exception e) {
            handleInvitationFailure("Connection error: " + e.getMessage(), stage);
        }
    }

    private void showWaitingPopup(String receiver, Stage stage) {
        Platform.runLater(() -> {
            remainingSeconds = TIMEOUT_SECONDS;
            waitingAlert = new Alert(Alert.AlertType.INFORMATION);
            waitingAlert.setTitle("Game Request");

            waitingAlert.setHeaderText(null);
            waitingAlert.setGraphic(null);

            VBox vbox = new VBox(20);
            vbox.setAlignment(javafx.geometry.Pos.CENTER);
            vbox.setPadding(new javafx.geometry.Insets(25));
            vbox.setMaxWidth(Double.MAX_VALUE);

            ImageView waitingIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/xo.png")));
            waitingIcon.setFitHeight(80);
            waitingIcon.setFitWidth(80);

            Label contentLabel = new Label("Waiting for " + receiver + "\nto accept Challenge...\n" + remainingSeconds + "s");
            contentLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-alignment: center;");
            contentLabel.setAlignment(javafx.geometry.Pos.CENTER);
            contentLabel.setMaxWidth(Double.MAX_VALUE);

            vbox.getChildren().addAll(waitingIcon, contentLabel);

            DialogPane dialogPane = waitingAlert.getDialogPane();
            dialogPane.setContent(vbox);
            dialogPane.getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-waiting-pane");

            waitingAlert.getButtonTypes().clear();
            waitingAlert.getButtonTypes().add(ButtonType.CANCEL);

            ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
            buttonBar.getButtons().forEach(b -> ButtonBar.setButtonData(b, ButtonBar.ButtonData.OK_DONE));
            buttonBar.setStyle("-fx-alignment: center; -fx-padding: 0 0 20 0;");

            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                remainingSeconds--;
                if (remainingSeconds > 0) {
                    contentLabel.setText("Waiting for " + receiver + "\nto accept Challenge...\n" + remainingSeconds + "s");
                } else {
                    stopInvitationProcess();
                    showToast("Request to " + receiver + " timed out.", stage);
                }
            }));

            timeline.setCycleCount(TIMEOUT_SECONDS);
            waitingAlert.setOnCloseRequest(event -> stopInvitationProcess());

            stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/xo.png")));

            waitingAlert.show();
            timeline.play();
        });
    }

    private void showInviteDialog(InvitationDTO inviteDTO) {
        Alert inviteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        inviteAlert.setTitle("Game Challenge");
        inviteAlert.setHeaderText(null);
        inviteAlert.setGraphic(null);

        VBox vbox = createInviteContent(inviteDTO.getSenderUsername());

        DialogPane pane = inviteAlert.getDialogPane();
        pane.setContent(vbox);
        pane.getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
        pane.getStyleClass().add("custom-waiting-pane");

        ButtonType acceptBtn = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
        ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
        inviteAlert.getButtonTypes().setAll(acceptBtn, declineBtn);

        ButtonBar buttonBar = (ButtonBar) pane.lookup(".button-bar");
        buttonBar.setStyle("-fx-alignment: center; -fx-padding: 0 0 20 0;");

        Optional<ButtonType> result = inviteAlert.showAndWait();

        if (result.isPresent() && result.get() == acceptBtn) {

            sendInvitationResponse(inviteDTO, RequestType.ACCEPT);

        } else {
            sendInvitationResponse(inviteDTO, RequestType.REJECT);
        }
    }

    private VBox createInviteContent(String sender) {
        VBox vbox = new VBox(20);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setPadding(new javafx.geometry.Insets(25));
        vbox.setPrefWidth(400);

        ImageView xoLogo = new ImageView(new Image(getClass().getResourceAsStream("/assets/xo.png")));
        xoLogo.setFitHeight(70);
        xoLogo.setFitWidth(70);

        Label msgLabel = new Label(sender + " is challenging you!");
        msgLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subLabel = new Label("Do you want to play now?");
        subLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");

        vbox.getChildren().addAll(xoLogo, msgLabel, subLabel);
        return vbox;
    }

    public void handleReceivedInvite(JsonElement payload) {
        InvitationDTO inviteDTO = gson.fromJson(payload, InvitationDTO.class);
        Platform.runLater(() -> showInviteDialog(inviteDTO));
    }

    public void onInvitationAccepted(JsonElement payload) {
        InvitationDTO gameInfo = gson.fromJson(payload, InvitationDTO.class);

        Platform.runLater(() -> {
            stopInvitationProcess();
            if (onGameStart != null) {
                onGameStart.accept(gameInfo);
            }
        });
    }

    public void onInvitationRejected(Stage stage) {
        Platform.runLater(() -> {
            stopInvitationProcess();
            showToast("Challenge Rejected.", stage);
        });
    }

    public void stopInvitationProcess() {
        if (timeline != null) {
            timeline.stop();
        }
        Platform.runLater(() -> {
            if (waitingAlert != null && waitingAlert.isShowing()) {
                waitingAlert.close();
            }
            resetInviteButtons();
        });
    }

    private void sendInvitationResponse(InvitationDTO dto, RequestType type) {
        conn.sendRequest(new Request(type, gson.toJsonTree(dto)));
    }

    private void resetInviteButtons() {
        if (pendingController != null) {
            pendingController.getActionButton().setDisable(false);
            pendingController.setActionButtonName("Invite");
            pendingController = null;
        }
    }

    private void updateUIForPendingState() {
        if (pendingController != null) {
            pendingController.getActionButton().setDisable(true);
            pendingController.setActionButtonName("Wait...");
        }
    }

    private void handleInvitationFailure(String message, Stage stage) {
        stopInvitationProcess();
        showToast(message, stage);
    }

    private void showToast(String message, Stage stage) {
        Platform.runLater(() -> {
            try {
//                (Stage) onlinePlayersList.getScene().getWindow();
                Stage ownerStage = stage;
                Stage toastStage = new Stage();
                toastStage.initOwner(ownerStage);
                toastStage.initStyle(StageStyle.TRANSPARENT);

                Text text = new Text(message);
                text.setStyle("-fx-font-size: 14px; -fx-fill: white; -fx-font-weight: bold;");

                StackPane root = new StackPane(text);
                root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10 20;");
                root.setOpacity(0);

                Scene scene = new Scene(root);
                scene.setFill(null);
                toastStage.setScene(scene);

                toastStage.setX(ownerStage.getX() + (ownerStage.getWidth() / 2) - 100);
                toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);

                toastStage.show();

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setDelay(Duration.seconds(2));
                fadeOut.setOnFinished(e -> toastStage.close());

                fadeIn.play();
                fadeIn.setOnFinished(e -> fadeOut.play());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
