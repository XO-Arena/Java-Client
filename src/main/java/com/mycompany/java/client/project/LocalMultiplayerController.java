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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import util.DialogUtil;

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
    @FXML
    private ComboBox<String> genderComboPlayer1;
    @FXML
    private ComboBox<String> genderComboPlayer2;
    private UserGender player1Gender;
    private UserGender player2Gender;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderComboPlayer1.getItems().addAll("Male", "Female");
        genderComboPlayer2.getItems().addAll("Male", "Female");

        genderComboPlayer1.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                return;
            }

            player1Gender = newVal.equals("Male") ? UserGender.MALE : UserGender.FEMALE;
            setAvatarImage(
                    player1Avatar,
                    newVal.equals("Male") ? "/assets/boy.png" : "/assets/girl.png"
            );
        });

        genderComboPlayer2.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                return;
            }

            player2Gender = newVal.equals("Male") ? UserGender.MALE : UserGender.FEMALE;
            setAvatarImage(
                    player2Avatar,
                    newVal.equals("Male") ? "/assets/boy.png" : "/assets/girl.png"
            );
        });
        // Load and set images for player avatars
        setAvatarImage(player1Avatar, "/assets/boy.png");
        setAvatarImage(player2Avatar, "/assets/boy.png");
    }

    private boolean validateInputs() {
        String player1Name = player1NameField.getText().trim();
        String player2Name = player2NameField.getText().trim();

        if (player1Name.isEmpty() || player2Name.isEmpty()) {
            DialogUtil.showAlert("Validatation Error", "Please enter names for both players.", Alert.AlertType.ERROR, this);
            return false;
        }

        if (player1Name.equals(player2Name)) {
            DialogUtil.showAlert("Validatation Error", "Players must have different names.", Alert.AlertType.ERROR, this);
            return false;
        }
        return true;
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
        if (player1Gender == null || player2Gender == null) {
            DialogUtil.showAlert(
                    "Validation Error",
                    "Please select gender for both players.",
                    AlertType.ERROR,
                    this
            );
        }
        if (!validateInputs()) {
            return;
        }

        try {
            GameBoardController controller = App.setRoot("GameBoardPage").getController();
            controller.initPlayers(
                    new Player(player1NameField.getText().trim(), player1Gender, 300, PlayerType.LOCAL, PlayerSymbol.X),
                    new Player(player2NameField.getText().trim(), player2Gender, 300, PlayerType.LOCAL, PlayerSymbol.O)
            );
        } catch (IOException ex) {
            System.getLogger(LocalMultiplayerController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
