/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.mycompany.java.client.project.LoginPageController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

/**
 *
 * @author mohannad
 */
public class AlertUtil {

    public static void showAlert(String title, String content, Alert.AlertType type, LoginPageController loginPageController) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            // Apply the CSS to the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(loginPageController.getClass().getResource("/styles/dialog.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            alert.showAndWait();
        });
    }

}
