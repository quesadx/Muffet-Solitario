package cr.ac.una.muffetsolitario.controller;

import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.FlowController;
import cr.ac.una.muffetsolitario.util.SoundUtils;
import cr.ac.una.muffetsolitario.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class PreGameController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;
    @FXML private MFXButton btnEasy;
    @FXML private MFXButton btnMed;
    @FXML private MFXButton btnHard;
    @FXML private MFXProgressBar pbGamingLoadingBar;

    private final AnimationHandler animationHandler = AnimationHandler.getInstance();
    private final SoundUtils soundUtils = SoundUtils.getInstance();
    private String selectedDifficulty = "EASY";
    private Timeline loadingTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind background
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());

        // Style the progress bar
        pbGamingLoadingBar.getStyleClass().add("mfx-progress-bar-styled");
        pbGamingLoadingBar.setProgress(0.0);
        pbGamingLoadingBar.setVisible(false);

        // Add glitch effects to buttons
        startGlitchEffect(btnEasy);
        startGlitchEffect(btnMed);
        startGlitchEffect(btnHard);

        // Set default selection
        selectDifficulty("EASY");
    }

    @Override
    public void initialize() {
        // Required by Controller interface
    }

    @FXML
    private void onActionBtnEasy(ActionEvent event) {
        selectDifficulty("EASY");
        animationHandler.playHitEffect(btnEasy);
    }

    @FXML
    private void onActionBtnMed(ActionEvent event) {
        selectDifficulty("MEDIUM");
        animationHandler.playHitEffect(btnMed);
    }

    @FXML
    private void onActionBtnHard(ActionEvent event) {
        selectDifficulty("HARD");
        animationHandler.playHitEffect(btnHard);
    }

    @FXML
    private void onActionBtnStartGame(ActionEvent event) {
        startGameLoading();
    }

    private void selectDifficulty(String difficulty) {
        selectedDifficulty = difficulty;
        
        // Reset all button styles
        btnEasy.getStyleClass().removeAll("mfx-button-selected");
        btnMed.getStyleClass().removeAll("mfx-button-selected");
        btnHard.getStyleClass().removeAll("mfx-button-selected");
        
        // Highlight selected button
        switch (difficulty) {
            case "EASY":
                btnEasy.getStyleClass().add("mfx-button-selected");
                break;
            case "MEDIUM":
                btnMed.getStyleClass().add("mfx-button-selected");
                break;
            case "HARD":
                btnHard.getStyleClass().add("mfx-button-selected");
                break;
        }
    }

    private void startGameLoading() {
        // Show loading progress bar
        pbGamingLoadingBar.setVisible(true);
        pbGamingLoadingBar.setProgress(0.0);
        
        // Disable buttons during loading
        btnEasy.setDisable(true);
        btnMed.setDisable(true);
        btnHard.setDisable(true);
        
        // Create loading timeline (3-4 seconds)
        loadingTimeline = new Timeline();
        double loadingDuration = 3500; // 3.5 seconds
        int steps = 100;
        
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            double time = progress * loadingDuration;
            
            loadingTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(time), e -> {
                    pbGamingLoadingBar.setProgress(progress);
                    
                    // Add glitch effect occasionally
                    if (Math.random() < 0.1) {
                        animationHandler.playHitEffect(pbGamingLoadingBar);
                    }
                })
            );
        }
        
        // When loading completes, go to game view
        loadingTimeline.setOnFinished(e -> {
            // Store selected difficulty in AppContext
            AppContext.getInstance().set("GameDifficulty", selectedDifficulty);
            
            // Transition to game view
            FlowController.getInstance().goView("GameView");
        });
        
        loadingTimeline.play();
    }

    private void startGlitchEffect(javafx.scene.Node node) {
        Timeline glitchTimeline = new Timeline();
        glitchTimeline.setCycleCount(Timeline.INDEFINITE);

        glitchTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1), event -> {
                double nextDelay = 2.0 + Math.random() * 6;
                glitchTimeline.stop();
                animationHandler.playHitEffect(node);
                glitchTimeline.getKeyFrames().set(0,
                    new KeyFrame(Duration.seconds(nextDelay), ev -> {
                        animationHandler.playHitEffect(node);
                    }));
                glitchTimeline.playFromStart();
            })
        );
        glitchTimeline.play();
    }
}
