package com.mycompany.java.client.project;

import enums.PlayerType;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HomePageController {

    @FXML
    private ImageView logoImage;
    @FXML
    private ListView<AnchorPane> roomsList;
    @FXML
    private ListView<AnchorPane> recordedGamesList;

    public void initialize() {

        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/xo.png")));

        // إضافة البيانات
        addDummyPlayers();
        addDummyLeaderboard();
    }

    private void addDummyPlayers() {
        for (int i = 1; i <= 3; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerItem.fxml"));
                AnchorPane playerItem = loader.load();

                PlayerItemController controller = loader.getController();
                controller.setPlayerName("Player " + i);
                controller.setPlayerStatus("Ready");
                playerItem.setPrefWidth(roomsList.getWidth() - 10);
                roomsList.getItems().add(playerItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDummyLeaderboard() {
        String[] topPlayers = {"Ahmed", "Sara", "John", "Esraa"};
        String[] scores = {"2500 XP", "1800 XP", "1200 XP", "1000 px"};

        for (int i = 0; i < topPlayers.length; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LeaderboardItem.fxml"));
                AnchorPane leaderboardItem = loader.load();

                LeaderboardItemController controller = loader.getController();

                int rank = i + 1;
                controller.setRank("#" + rank);
                controller.setPlayerName(topPlayers[i]);
                controller.setScore(scores[i]);
                leaderboardItem.setPrefWidth(recordedGamesList.getWidth() - 10);

                recordedGamesList.getItems().add(leaderboardItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    
    @FXML
    private void navigateToLoginPage(ActionEvent event) {
        try {
            App.setRoot("loginPage");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void navigateToOnlineGameBoardPage(ActionEvent event) {
        try {
                GameBoardController controller = App.setRoot("GameBoardPage").getController();
                controller.initDummyPlayers(PlayerType.ONLINE);
            } catch (IOException ex) {
                System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
    private void onPlayVsPcClicked(ActionEvent event) {
        try {
            App.setRoot("difficultySelector");
        } catch (IOException ex) {
            System.getLogger(HomePageController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

}
