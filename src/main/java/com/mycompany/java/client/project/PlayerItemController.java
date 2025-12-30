package com.mycompany.java.client.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerItemController {
    @FXML
    public void initialize() {
        playerAvatar.setImage(
            new Image(getClass().getResourceAsStream(
                "/assets/avatar.png"
            ))
        );
    }
    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerStatusLabel;

    @FXML
    private Circle statusIndicator;

    @FXML
    private ImageView playerAvatar;


    // Setters
    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }

    public void setPlayerStatus(String status) {
        playerStatusLabel.setText(status);
        if (status.equalsIgnoreCase("Ready")) {
            statusIndicator.setStyle("-fx-fill: #55ff55;");
        } else {
            statusIndicator.setStyle("-fx-fill: #ff5555;");
        }
    }

   
}
