package com.mycompany.java.client.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import static javafx.application.Application.launch;
import models.User;

/**
 * JavaFX App
 */
public class App extends Application {

    private static boolean loggedIn = false;
    private static Scene scene;
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("homePage"), 950, 650);
        stage.setScene(scene);
        stage.show();
    }

    static FXMLLoader setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent parent = fxmlLoader.load();
        scene.setRoot(parent);
        return fxmlLoader;
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setLoggedIn(boolean mode) {
        loggedIn = mode;
    }

    public static boolean IsLoggedIn() {
        return loggedIn == true;
    }

    public static void main(String[] args) {
        launch();
    }

}
