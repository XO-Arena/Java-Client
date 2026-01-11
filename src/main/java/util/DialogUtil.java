/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.mycompany.java.client.project.BrandedDialogController;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author mohannad
 */
public class DialogUtil {

    private static Stage currentDialogStage;

    public static void showAlert(String title, String content, Alert.AlertType type, Object object) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            // Apply the CSS to the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(object.getClass().getResource("/styles/dialog.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            alert.showAndWait();
        });
    }

    public static void showBrandedDialog(String title, String content, boolean showPrimary, boolean showSecondary, String primaryText, String secondaryText, Runnable onPrimary, Runnable onSecondary) {
        Platform.runLater(() -> {
            try {
                if (currentDialogStage != null) {
                    currentDialogStage.close();
                }

                FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource("/com/mycompany/java/client/project/brandedDialog.fxml"));
                AnchorPane root = loader.load();
                BrandedDialogController controller = loader.getController();

                controller.setTitle(title);
                controller.setContent(content);

                if (showPrimary) {
                    controller.setPrimaryButtonText(primaryText);
                    controller.setOnPrimaryAction(onPrimary);
                } else {
                    controller.hidePrimaryButton();
                }

                if (showSecondary) {
                    controller.setSecondaryButtonText(secondaryText);
                    controller.setOnSecondaryAction(onSecondary);
                } else {
                    controller.hideSecondaryButton();
                }

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.TRANSPARENT);
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                stage.setScene(scene);

                currentDialogStage = stage;
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void closeCurrentDialog() {
        Platform.runLater(() -> {
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
            }
        });
    }

}
