package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SignUpController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private MFXButton btnCancel;
    @FXML private MFXButton btnConfirmSignUp;
    @FXML private Label lblSignUpTitle;
    @FXML private MFXPasswordField psfNewConfirmPassword;
    @FXML private MFXPasswordField psfNewPassword;
    @FXML private MFXTextField txfNewFavoriteWord;
    @FXML private MFXTextField txfNewUser;
    @FXML private MFXButton btnConfirmAlert;
    @FXML private VBox vboxMissingFieldsAlert;
    @FXML private VBox vboxUserAlreadyExists;
    @FXML private ImageView imgBackground;

    private Timeline lblSignUpTitleGlitchTimeline;
    private Timeline btnConfirmSignUpGlitchTimeline;
    private final AnimationHandler animationHandler = AnimationHandler.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the background ImageView to the root pane
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());
        
        startGlitchEffect(lblSignUpTitle, true);
        startGlitchEffect(btnConfirmSignUp, false);
    }

    @Override
    public void initialize() {
        // Not used
    }
    
    @FXML
    void onActionBtnCancel(ActionEvent event) {
        clearTextFields();
        FlowController.getInstance().goView("LogInView");
    }

    @FXML
    void onActionBtnConfirmSignUp(ActionEvent event) {
        if(!confirmFields()){
            vboxMissingFieldsAlert.setManaged(true);
            vboxMissingFieldsAlert.setVisible(true);
            return;
        }
        if(userExists(txfNewUser.getText())){
            vboxUserAlreadyExists.setManaged(true);
            vboxUserAlreadyExists.setVisible(true);
            return;
        }
    }

    @FXML
    void onActionBtnConfirmAlert(ActionEvent event) {
        vboxMissingFieldsAlert.setManaged(false);
        vboxMissingFieldsAlert.setVisible(false);
        vboxUserAlreadyExists.setManaged(false);
        vboxUserAlreadyExists.setVisible(false);
    }

    private void clearTextFields(){
        psfNewConfirmPassword.clear();
        psfNewPassword.clear();
        txfNewUser.clear();
        txfNewFavoriteWord.clear();
    }

    private Boolean confirmFields() {
        if(txfNewUser.getText().isBlank() || txfNewFavoriteWord.getText().isBlank() || psfNewPassword.getText().isBlank() || psfNewConfirmPassword.getText().isBlank()){
            return false;
        }
        return true;
    }

    private Boolean userExists(String user){
        return true; // debug
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

        if (isTitle) {
            lblSignUpTitleGlitchTimeline = glitchTimeline;
        } else {
            btnConfirmSignUpGlitchTimeline = glitchTimeline;
        }
    }

    private void handleGlitch(javafx.scene.Node node, Timeline glitchTimeline) {
        animationHandler.playHitEffect(node);
    }
}