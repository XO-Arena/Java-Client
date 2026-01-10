package com.mycompany.java.client.project;

import enums.UserState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerItemController {
     @FXML
    private Label playerNameLabel;
    
    @FXML
    private Button actionButton;

    @FXML
    private Label playerStatusLabel;

    @FXML
    private Circle statusIndicator;

    @FXML
    private ImageView playerAvatar;

    private java.util.function.Consumer<String> onInviteHandler;

    @FXML
    public void initialize() {
        playerAvatar.setImage(
            new Image(getClass().getResourceAsStream(
                "/assets/avatar.png"
            ))
        );
        
        actionButton.setOnAction(event -> {
            if ("Invite".equals(actionButton.getText()) && onInviteHandler != null) {
                onInviteHandler.accept(playerNameLabel.getText());
            }
        });
    }

    public void setOnInviteHandler(java.util.function.Consumer<String> handler) {
        this.onInviteHandler = handler;
    }
   

    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }
    
    public void setButtonText(UserState state) {
        switch(state){
            case ONLINE:
                actionButton.setText("Invite");
                break;
            case IN_GAME:
                actionButton.setText("Watch");
                break;
            default:
                break;
        }
    }

    public void setPlayerStatus(UserState state) {
         switch(state){
            case ONLINE:
                playerStatusLabel.setText("ready");
                statusIndicator.setStyle("-fx-fill: #55ff55;");
                break;
            case IN_GAME:
                actionButton.setText("Watch");
                statusIndicator.setStyle("-fx-fill: #ff5555;");
                break;
            default:
                break;
        }
    }

   
}
