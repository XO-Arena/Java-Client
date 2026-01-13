package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mycompany.java.client.project.App;
import com.mycompany.java.client.project.PlayerItemController;
import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.ServerConnection;
import dto.InvitationDTO;
import enums.InvitationStatus;
import enums.RequestType;
import java.util.Optional;

import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBase;
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
import util.GameTimer;

/**
 *
 * @author Ahmed_El_Sayyad
 */
public class InvitationService {

    private final ServerConnection conn;
    private final Gson gson;
    private final Consumer<InvitationDTO> onGameStart;
    private Alert inviteAlert;
    private Alert waitingAlert;
    private PlayerItemController pendingController;
    private static final int TIMEOUT_SECONDS = 10;
    private GameTimer gameTimer;

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

    public void handleReceivedInvite(JsonElement payload, Stage stage) {
        InvitationDTO inviteDTO = gson.fromJson(payload, InvitationDTO.class);
        Platform.runLater(() -> showInviteDialog(inviteDTO, stage));
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
        if (gameTimer != null) {
            gameTimer.stop();
        }

        Platform.runLater(() -> {
            if (waitingAlert != null && waitingAlert.isShowing()) {
                waitingAlert.close();
            }
            if (inviteAlert != null && inviteAlert.isShowing()) {
                inviteAlert.close();
            }
            resetInviteButtons();
        });
    }

    private void sendInvitationResponse(InvitationDTO dto, RequestType type) {
        conn.sendRequest(new Request(type, gson.toJsonTree(dto)));
    }

    private void handleInvitationFailure(String message, Stage stage) {
        stopInvitationProcess();
        showToast(message, stage);
    }

    ///////////////////////////////////////////////////////////////////////////////
    
    private void showWaitingPopup(String receiver, Stage stage) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        Platform.runLater(() -> {
            Label contentLabel = new Label();
            VBox vbox = createStandardVBox("/assets/xo.png", contentLabel);

            waitingAlert = new Alert(Alert.AlertType.INFORMATION);
            waitingAlert.setTitle("Game Request");
            waitingAlert.getDialogPane().setContent(vbox);
            waitingAlert.getButtonTypes().setAll(ButtonType.CANCEL);

            applyStandardStyles(waitingAlert, true);

            gameTimer = new GameTimer(
                    TIMEOUT_SECONDS,
                    (seconds) -> Platform.runLater(()
                            -> contentLabel.setText("Waiting for " + receiver + "...\n" + seconds + "s")),
                    () -> Platform.runLater(() -> {
                        cancelSentInvitation(receiver);
//                        showToast("Request to " + receiver + " timed out.", stage);
                    })
            );

            waitingAlert.resultProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == ButtonType.CANCEL) {
                    cancelSentInvitation(receiver);
                }
            });

            waitingAlert.show();
            gameTimer.start();
        });
    }

    private void showInviteDialog(InvitationDTO inviteDTO, Stage stage) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        Platform.runLater(() -> {
            Label msgLabel = new Label(inviteDTO.getSenderUsername() + " is challenging you!");
            Label timerLabel = new Label();

            msgLabel.getStyleClass().add("dialog-label-main");
            timerLabel.getStyleClass().add("dialog-label-sub");

            VBox vbox = createStandardVBox("/assets/xo.png", msgLabel, timerLabel);

            inviteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            inviteAlert.setTitle("Game Challenge");
            inviteAlert.getDialogPane().setContent(vbox);

            applyStandardStyles(inviteAlert, false);

            ButtonType acceptBtn = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
            ButtonType declineBtn = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
            inviteAlert.getButtonTypes().setAll(acceptBtn, declineBtn);

            gameTimer = new GameTimer(
                    TIMEOUT_SECONDS,
                    (seconds) -> Platform.runLater(()
                            -> timerLabel.setText("You have " + seconds + "s to respond...")),
                    () -> Platform.runLater(() -> {
                        if (inviteAlert.isShowing()) {
                            inviteAlert.close();
//                            showToast("timed out to Response to " + inviteDTO.getSenderUsername(), stage);

                            sendInvitationResponse(inviteDTO, RequestType.REJECT);
                        }
                    })
            );

            gameTimer.start();

            Optional<ButtonType> result = inviteAlert.showAndWait();

            gameTimer.stop();

            if (result.isPresent() && result.get() == acceptBtn) {
                sendInvitationResponse(inviteDTO, RequestType.ACCEPT);
            } else if (result.isPresent() && result.get() == declineBtn) {
                sendInvitationResponse(inviteDTO, RequestType.REJECT);
            }
        });
    }

    private void cancelSentInvitation(String receiver) {

        InvitationDTO invitationDTO = new InvitationDTO(
                App.getCurrentUser().getUsername(),
                receiver,
                InvitationStatus.CANCELED
        );
        sendInvitationResponse(invitationDTO, RequestType.CANCEL);

        stopInvitationProcess();

//        System.out.println("Invitation to " + invitationDTO.getReceiverUsername() + " has been canceled.");
    }

    public void handleIncomingCancellation(JsonElement payload, Stage stage) {
        InvitationDTO inviteDTO = gson.fromJson(payload, InvitationDTO.class);
        String senderName = inviteDTO.getSenderUsername();
        Platform.runLater(() -> {
            stopInvitationProcess();

            showToast("Challenge from " + senderName + " was canceled.", stage);
        });
    }

    private VBox createStandardVBox(String iconPath, Node... nodes) {
        VBox vbox = new VBox(20);
        vbox.getStyleClass().add("custom-vbox");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitHeight(80);
        icon.setFitWidth(80);

        vbox.getChildren().add(icon);
        vbox.getChildren().addAll(nodes);

        for (Node node : nodes) {
            if (node instanceof Label) {
                node.getStyleClass().add("dialog-label-main");
            }
        }
        return vbox;
    }

    private void applyStandardStyles(Alert alert, boolean isWaiting) {
        DialogPane pane = alert.getDialogPane();
        pane.setHeaderText(null);
        pane.setGraphic(null);

        pane.setPrefWidth(450);
        pane.setPrefHeight(350);
        pane.setMinSize(450, 350);
        pane.setMaxSize(450, 350);

        pane.getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());
        pane.getStyleClass().add("dialog-pane");

        Stage alertStage = (Stage) pane.getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        alertStage.setResizable(false);

        ButtonBar buttonBar = (ButtonBar) pane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setStyle("-fx-alignment: center; -fx-padding: 0 0 20 0;");
            buttonBar.getButtons().forEach(node -> {
                if (node instanceof ButtonBase) {
                    ((ButtonBase) node).setMinWidth(100);
                }
            });
        }
    }

    private void showToast(String message, Stage stage) {
        Platform.runLater(() -> {
            try {
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
}
