package com.mycompany.java.client.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BrandedDialogController {

    @FXML private AnchorPane rootPane;
    @FXML private ImageView dialogIcon;
    @FXML private Label titleLabel;
    @FXML private Label contentLabel;
    @FXML private HBox buttonBox;
    @FXML private Button primaryButton;
    @FXML private Button secondaryButton;

    private Runnable onPrimaryAction;
    private Runnable onSecondaryAction;
    
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        dialogIcon.setImage(new Image(getClass().getResourceAsStream("/assets/xo.png")));
        
        primaryButton.setOnAction(e -> {
            if (onPrimaryAction != null) onPrimaryAction.run();
            // Usually we close, but maybe the action wants to keep it open? 
            // For now, let's assume we close unless logic dictates otherwise.
            // But for "Accept" invite, we might want to close immediately.
            closeDialog();
        });
        
        secondaryButton.setOnAction(e -> {
            if (onSecondaryAction != null) onSecondaryAction.run();
            closeDialog();
        });
        
        makeDraggable();
    }
    
    private void makeDraggable() {
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setContent(String content) {
        contentLabel.setText(content);
    }
    
    public void setPrimaryButtonText(String text) {
        primaryButton.setText(text);
        primaryButton.setVisible(true);
        primaryButton.setManaged(true);
    }
    
    public void setSecondaryButtonText(String text) {
        secondaryButton.setText(text);
        secondaryButton.setVisible(true);
        secondaryButton.setManaged(true);
    }
    
    public void hidePrimaryButton() {
        primaryButton.setVisible(false);
        primaryButton.setManaged(false);
    }

    public void hideSecondaryButton() {
        secondaryButton.setVisible(false);
        secondaryButton.setManaged(false);
    }

    public void setOnPrimaryAction(Runnable action) {
        this.onPrimaryAction = action;
    }

    public void setOnSecondaryAction(Runnable action) {
        this.onSecondaryAction = action;
    }

    public void closeDialog() {
        if (primaryButton.getScene() != null && primaryButton.getScene().getWindow() != null) {
            Stage stage = (Stage) primaryButton.getScene().getWindow();
            stage.close();
        }
    }
}
