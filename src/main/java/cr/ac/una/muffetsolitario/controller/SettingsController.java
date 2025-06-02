/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class SettingsController extends Controller implements Initializable  {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;
    
    @FXML private MFXButton btnBGM;
    @FXML private MFXButton btnConfirmNewUserName;
    @FXML private Label lblSignUpTitle;
    @FXML private MFXTextField txfNewUsername;

    private final AnimationHandler animationHandler = AnimationHandler.getInstance();
    private Timeline glitchTimeline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the background ImageView to the root pane
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());

        startGlitchEffect(lblSignUpTitle, true);
        startGlitchEffect(btnBGM, false);
        startGlitchEffect(btnConfirmNewUserName, false);
    }

    @Override
    public void initialize() {
        
    }

    @FXML void onActionConfirmNewUserName(ActionEvent event) {
        // TODO: Revisar si es que no lo cambió y revisar si está disponible
    }

    @FXML
    void onActionExitSettings(ActionEvent event) {
        FlowController.getInstance().goView("LogInView");
    }

    @FXML
    void onActionToggleBtnBGM(ActionEvent event) {
        // TODO: Obtener el estado actual del ajuste y setear el toggler
    }

        /**
     * Starts a random glitch effect for the given node (Label or Button).
     * Each node gets its own Timeline.
     */
    private void startGlitchEffect(javafx.scene.Node node, boolean isTitle) {
        Timeline glitchTimeline = new Timeline();
        glitchTimeline.setCycleCount(Timeline.INDEFINITE);

        glitchTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1), event -> {
                double nextDelay = 1.0 + Math.random() * 8;
                glitchTimeline.stop();
                animationHandler.playHitEffect(node);
                glitchTimeline.getKeyFrames().set(0,
                    new KeyFrame(Duration.seconds(nextDelay), ev -> handleGlitch(node, glitchTimeline)));
                glitchTimeline.playFromStart();
            })
        );
        glitchTimeline.play();
    }

    private void handleGlitch(javafx.scene.Node node, Timeline glitchTimeline) {
        animationHandler.playHitEffect(node);
    }
    
}
