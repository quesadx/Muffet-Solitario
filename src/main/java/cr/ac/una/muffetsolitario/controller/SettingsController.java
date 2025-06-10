package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.AppContext;
import cr.ac.una.muffetsolitario.util.FlowController;
import cr.ac.una.muffetsolitario.util.SoundUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import cr.ac.una.muffetsolitario.model.UserAccountDto;
import cr.ac.una.muffetsolitario.service.UserAccountService;
import cr.ac.una.muffetsolitario.util.Respuesta;
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

public class SettingsController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground, imgDropArea;
    @FXML private MFXButton btnBGM, btnDelete;
    @FXML private MFXButton btnConfirmNewUserName;
    @FXML private Label lblSignUpTitle;
    @FXML private MFXButton btnExit;
    @FXML private MFXTextField txfNewUsername;
    @FXML private MFXButton btnSelectEstilo1;
    @FXML private MFXButton btnSelectEstilo2;

    private final AnimationHandler animationHandler = AnimationHandler.getInstance();
    private final SoundUtils soundUtils = SoundUtils.getInstance();
    private Timeline glitchTimeline;
    @FXML
    private VBox vboxUserUpdated;
    @FXML
    private VBox vboxTakenUsername;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the background ImageView to the root pane
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());

        // Initialize BGM button text based on current state
        updateBGMButtonText();

        startGlitchEffect(lblSignUpTitle, true);
        startGlitchEffect(btnBGM, false);
        startGlitchEffect(btnConfirmNewUserName, false);
        startGlitchEffect(btnSelectEstilo1, false);
        startGlitchEffect(btnSelectEstilo2, false);

        initializeDragAndDrop();
    }

    @Override
    public void initialize() {
        // Required by Controller interface
    }

    @FXML 
    void onActionToggleBtnBGM(ActionEvent event) {
        boolean currentlyMuted = soundUtils.isMuted();
        soundUtils.setMuted(!currentlyMuted);
        
        // Update button text
        updateBGMButtonText();
        
        // Play or pause BGM
        if (currentlyMuted) {
            soundUtils.playBGM();
        } else {
            soundUtils.pauseBGM();
        }
        
        // Add glitch effect to the button
        animationHandler.playHitEffect(btnBGM);
    }

    private void updateBGMButtonText() {
        btnBGM.setText(soundUtils.isMuted() ? "NO" : "SI");
    }

    @FXML
    private void onActionBtnDeleteImage(ActionEvent event) {
        java.nio.file.Path customPath = java.nio.file.Paths.get(System.getProperty("user.dir"),
            "src/main/resources/cr/ac/una/muffetsolitario/resources/assets/CardStyles/v3/Card_Back1.png");
        try {
            java.nio.file.Files.deleteIfExists(customPath);
            imgDropArea.setImage(null);
            btnDelete.setDisable(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onActionBtnSelectEstilo1(ActionEvent event) {
        // Apply style 1 (default undertale style)
        root.getStylesheets().clear();
        root.getStylesheets().add(getClass().getResource("/cr/ac/una/muffetsolitario/view/styles.css").toExternalForm());
        UserAccountDto userAccountDto = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        userAccountDto.setUserCardDesign(0);
        animationHandler.playHitEffect(btnSelectEstilo1);

        saveUserChanges(userAccountDto);
    }

    @FXML
    private void onActionConfirmNewUserName(ActionEvent event) {
        String newUsername = txfNewUsername.getText();
        if (newUsername == null || newUsername.isBlank()) {
            // Show taken username alert as invalid input alert
            vboxTakenUsername.setVisible(true);
            vboxTakenUsername.setManaged(true);
            return;
        }

        UserAccountService userAccountService = new UserAccountService();
        // Check if username is taken by trying to get user by nickname
        Respuesta checkResponse = userAccountService.getUserAccountByNickname(newUsername);
        if (checkResponse.getEstado()) {
            // User with this nickname exists
            vboxTakenUsername.setVisible(true);
            vboxTakenUsername.setManaged(true);
            return;
        }

        // Update username in current user account
        var currentUser = (cr.ac.una.muffetsolitario.model.UserAccountDto) cr.ac.una.muffetsolitario.util.AppContext.getInstance().get("LoggedInUser");
        if (currentUser != null) {
            currentUser.setUserNickname(newUsername);
            Respuesta response = userAccountService.saveUserAccount(currentUser);
            if (response.getEstado()) {
                vboxUserUpdated.setVisible(true);
                vboxUserUpdated.setManaged(true);
            } else {
                // Show taken username alert on failure (could be improved)
                vboxTakenUsername.setVisible(true);
                vboxTakenUsername.setManaged(true);
            }
        }
    }

    @FXML
    private void onActionExitSettings(ActionEvent event) {
        FlowController.getInstance().goView("LogInView");
    }
    @FXML
    private void onActionBtnSelectEstilo2(ActionEvent event) {
        // Apply style 2 (alternative style - could be implemented later)
        UserAccountDto userAccountDto = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        userAccountDto.setUserCardDesign(1);
        animationHandler.playHitEffect(btnSelectEstilo2);

        saveUserChanges(userAccountDto);
    }

    private void saveUserChanges(UserAccountDto userAccountDto) {
        UserAccountService userAccountService = new UserAccountService();
        Respuesta checkResponse = userAccountService.saveUserAccount(userAccountDto);

        if(checkResponse.getEstado()){
            System.out.println("Cambios guardados del usuario: " + userAccountDto.getUserNickname());
        } else{
            System.out.println("Cambios no guardados del usuario: " + userAccountDto.getUserNickname());
        }
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

    @FXML
    private void onActionConfirmUserUpdated(ActionEvent event) {
        vboxUserUpdated.setVisible(false);
        vboxUserUpdated.setManaged(false);
        vboxTakenUsername.setVisible(false);
        vboxTakenUsername.setManaged(false);
    }

    @FXML
    private void initializeDragAndDrop() {
        imgDropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != imgDropArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });
        imgDropArea.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                java.io.File file = db.getFiles().get(0);
                try {
                    // Only allow image files
                    String lower = file.getName().toLowerCase();
                    if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                        java.nio.file.Path dest = java.nio.file.Paths.get(System.getProperty("user.dir"),
                            "src/main/resources/cr/ac/una/muffetsolitario/resources/assets/CardStyles/v3/Card_Back1.png");
                        java.nio.file.Files.copy(file.toPath(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        imgDropArea.setImage(new javafx.scene.image.Image(dest.toUri().toString()));
                        btnDelete.setDisable(false);
                        success = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        // On startup, check if custom image exists
        java.nio.file.Path customPath = java.nio.file.Paths.get(System.getProperty("user.dir"),
            "src/main/resources/cr/ac/una/muffetsolitario/resources/assets/CardStyles/v3/Card_Back1.png");
        if (java.nio.file.Files.exists(customPath)) {
            imgDropArea.setImage(new javafx.scene.image.Image(customPath.toUri().toString()));
            btnDelete.setDisable(false);
        } else {
            btnDelete.setDisable(true);
        }
    }
}
