package com.mycompany.java.client.project;
import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import models.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author mohan
 */
public class LocalMultiplayerController implements Initializable {
    @FXML
    private Button backButton;
    @FXML
    private Circle player2Avatar;
    @FXML
    private TextField player2NameField;
    
    @FXML
    private Circle player1Avatar;
    @FXML
    private TextField player1NameField;
    @FXML
    private Button startButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load and set images for player avatars
        setAvatarImage(player1Avatar, "/assets/boy.png");
        setAvatarImage(player2Avatar, "/assets/boy.png");
    }    
    
    private void setAvatarImage(Circle circle, String imagePath) {
        try {
            // Load the image from resources
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            
            // Create an ImagePattern and set it as the circle's fill
            ImagePattern pattern = new ImagePattern(image);
            circle.setFill(pattern);
            
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            App.setRoot("HomePage");
        } catch (IOException ex) {
            System.getLogger(LocalMultiplayerController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @FXML
    private void handleStart(ActionEvent event) {
        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            controller.initPlayers(
                    new Player(player1NameField.getText(), UserGender.MALE, 300, PlayerType.LOCAL, PlayerSymbol.X),
                    new Player(player2NameField.getText(), UserGender.MALE, 300, PlayerType.LOCAL, PlayerSymbol.O)
            );
        } catch (IOException ex) {
            System.getLogger(LocalMultiplayerController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}