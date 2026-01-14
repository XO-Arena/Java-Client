package com.mycompany.java.client.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class LeaderboardItemController {

    @FXML private ImageView playerAvatar;
    @FXML private Label rankLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label scoreLabel;

    @FXML
    public void initialize() {
       
        Circle clip = new Circle(22.5, 22.5, 22.5);
        playerAvatar.setClip(clip);

      
        try {
            playerAvatar.setImage(new Image(getClass().getResourceAsStream("/assets/avatar.png")));
        } catch (Exception e) {
            System.out.println("Default avatar not found.");
        }
    }

  
    
    public void setRank(String rank) {
        rankLabel.setText(rank);
        
       
        switch (rank) {
            case "#1": rankLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); break; 
            case "#2": rankLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;"); break; 
            case "#3": rankLabel.setStyle("-fx-text-fill: #b45309; -fx-font-weight: bold;"); break; 
            default:   rankLabel.setStyle("-fx-text-fill: #64748b;"); break;
        }
    }

    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }

    public void setScore(String score) {
        scoreLabel.setText(score);
    }
    
    public void setAvatar(String imagePath) {
        try {
            playerAvatar.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
        }
    }
}