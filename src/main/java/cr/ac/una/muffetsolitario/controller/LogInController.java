package cr.ac.una.muffetsolitario.controller;

import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.FlowController;
import cr.ac.una.muffetsolitario.util.SoundUtils;
import cr.ac.una.muffetsolitario.util.AppContext;
import cr.ac.una.muffetsolitario.service.UserAccountService;
import cr.ac.una.muffetsolitario.model.UserAccountDto;
import cr.ac.una.muffetsolitario.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class LogInController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;

    @FXML private ImageView imgCenterCard;
    @FXML private ImageView imgLeftCard;
    @FXML private ImageView imgRightCard;

    @FXML private ImageView imgHeart0;
    @FXML private ImageView imgHeart1;
    @FXML private ImageView imgHeart2;
    @FXML private ImageView imgSlash;
    @FXML private ImageView imgDog;

    @FXML private ImageView imgMuffetJumpscare1;
    @FXML private ImageView imgMuffetJumpscare0;
    
    @FXML private Label lblTitle;
    @FXML private Label lblTitleLogged;
    @FXML private Label lblPlay;
    @FXML private Label lblUsername;
    @FXML private Label lblDateCreated;
    @FXML private Label lbllDateLastPlayed;
    
    @FXML private MFXButton btnAbout;
    @FXML private MFXButton btnGuest;
    @FXML private MFXButton btnLogIn;
    @FXML private MFXButton btnSignUp;
    @FXML private MFXButton btnConfirmAlert;
    @FXML private MFXButton btnLogOut;
    @FXML private MFXButton btnStartGame;
    
    @FXML private MFXPasswordField psfPassword;
    @FXML private MFXTextField txfUser;

    @FXML private VBox vboxLoginDisplay;
    @FXML private VBox vboxStartDisplay;
    @FXML private VBox vboxMissingFieldsAlert;
    @FXML private VBox vboxWrongCredentials;
    @FXML private VBox vboxLoggedIn;
    @FXML private VBox vboxContinueOrDeleteGame;

    private final AnimationHandler animationHandler = AnimationHandler.getInstance();
    private final SoundUtils soundUtils = SoundUtils.getInstance();

    private javafx.animation.Timeline lblTitleGlitchTimeline;
    private javafx.animation.Timeline lblPlayGlitchTimeline;
    private javafx.animation.Timeline leftCardGlitchTimeline;
    private javafx.animation.Timeline centerCardGlitchTimeline;
    private javafx.animation.Timeline rightCardGlitchTimeline;
    private javafx.animation.Timeline btnLogInGlitchTimeline;
    private javafx.animation.Timeline dogMoveTimeline;

    private final Random random = new Random();
    private Timeline muffetJumpscareTimeline;

    private double dogOriginalX; 
    private boolean isLoggedIn = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database service with error handling
        try {
            userService = new UserAccountService();
            databaseAvailable = true;
        } catch (Exception e) {
            System.err.println("Database connection failed, running in offline mode: " + e.getMessage());
            databaseAvailable = false;
        }

        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());

        imgHeart0.setVisible(false);
        imgHeart1.setVisible(false);
        imgHeart2.setVisible(false);
        imgSlash.setVisible(false);

        btnAbout.setVisible(false);
        btnAbout.setManaged(false);
        btnGuest.setVisible(false);
        btnGuest.setManaged(false);
        btnSignUp.setVisible(false);
        btnSignUp.setManaged(false);

        vboxLoggedIn.setVisible(false);
        vboxLoggedIn.setManaged(false);

        // Hide jumpscares at startup
        imgMuffetJumpscare0.setVisible(false);
        imgMuffetJumpscare0.setManaged(false);
        imgMuffetJumpscare1.setVisible(false);
        imgMuffetJumpscare1.setManaged(false);

        animationHandler.startLevitationWithGlitch(imgLeftCard, 240);
        animationHandler.startLevitationWithGlitch(imgCenterCard, 120);
        animationHandler.startLevitationWithGlitch(imgRightCard, 0);

        imgHeart0.setOnMouseClicked(e -> {
            animationHandler.playHeartHitEffect(imgHeart0);
            animationHandler.playLightningEffect(root);
        });
        imgHeart1.setOnMouseClicked(e -> {
            animationHandler.playHeartHitEffect(imgHeart1);
            animationHandler.playLightningEffect(root);
        });
        imgHeart2.setOnMouseClicked(e -> {
            animationHandler.playHeartHitEffect(imgHeart2);
            animationHandler.playLightningEffect(root);
        });

        imgLeftCard.setOnMouseClicked(e -> {
            animationHandler.playHitEffect(imgLeftCard);
            animationHandler.playLightningEffect(root);
        });
        imgCenterCard.setOnMouseClicked(e -> {
            animationHandler.playHitEffect(imgCenterCard);
            animationHandler.playLightningEffect(root);
        });
        imgRightCard.setOnMouseClicked(e -> {
            animationHandler.playHitEffect(imgRightCard);
            animationHandler.playLightningEffect(root);
        });

        startLabelGlitch(lblTitle);
        startLabelGlitch(lblPlay);

        startNodeGlitch(imgLeftCard, "left");
        startNodeGlitch(imgCenterCard, "center");
        startNodeGlitch(imgRightCard, "right");

        startNodeGlitch(btnLogIn, "loginButton");

        if (imgDog != null) {
            dogOriginalX = imgDog.getLayoutX();
        }

        startMuffetJumpscareLoop();
        startRandomLightning();
    }

     /**
     * Call this method after a successful login to start the jumpscare loop.
     */
    private void startMuffetJumpscareLoop() {
        if (muffetJumpscareTimeline != null) {
            muffetJumpscareTimeline.stop();
        }
        scheduleNextMuffetJumpscare();
    }

    /**
     * Schedules the next jumpscare only if the user is logged in.
     * Interval is 15-20 seconds.
     */
    private void scheduleNextMuffetJumpscare() {
        if (!isLoggedIn) return;

        int delay = 4000 + random.nextInt(5000);

        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(e -> {
            if (isLoggedIn) {
                showRandomMuffetJumpscare();
            } else {
                // If not logged in anymore, do not schedule further
                if (muffetJumpscareTimeline != null) muffetJumpscareTimeline.stop();
            }
        });
        pause.play();
        muffetJumpscareTimeline = new Timeline(new KeyFrame(Duration.millis(delay)));
        muffetJumpscareTimeline.play();
    }

    /**
     * Shows a random Muffet jumpscare with glitchy effect, then schedules the next one.
     */
    private void showRandomMuffetJumpscare() {
        if (!isLoggedIn) return;

        boolean showZero = random.nextBoolean();
        ImageView jumpscare = showZero ? imgMuffetJumpscare0 : imgMuffetJumpscare1;

        jumpscare.setVisible(true);
        jumpscare.setManaged(true);

        Timeline glitch = new Timeline(
            new KeyFrame(Duration.ZERO, ev -> {
                animationHandler.playHitEffect(jumpscare);
                jumpscare.setOpacity(0.2 + random.nextDouble() * 0.2);
                jumpscare.setTranslateX(random.nextBoolean() ? random.nextInt(6) : -random.nextInt(6));
            }),
            new KeyFrame(Duration.millis(60), ev -> {
                animationHandler.playHitEffect(jumpscare);
                jumpscare.setOpacity(0.5 + random.nextDouble() * 0.2);
                jumpscare.setTranslateX(random.nextBoolean() ? random.nextInt(8) : -random.nextInt(8));
            }),
            new KeyFrame(Duration.millis(120), ev -> {
                animationHandler.playHitEffect(jumpscare);
                jumpscare.setOpacity(0.3 + random.nextDouble() * 0.2);
                jumpscare.setTranslateX(random.nextBoolean() ? random.nextInt(5) : -random.nextInt(5));
            }),
            new KeyFrame(Duration.millis(180), ev -> {
                animationHandler.playHitEffect(jumpscare);
                jumpscare.setOpacity(1.0);
                jumpscare.setTranslateX(0);
            })
        );
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), jumpscare);
        fadeIn.setFromValue(jumpscare.getOpacity());
        fadeIn.setToValue(1.0);

        SequentialTransition appear = new SequentialTransition(glitch, fadeIn);

        PauseTransition stay = new PauseTransition(Duration.millis(600 + random.nextInt(400)));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), jumpscare);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(ev -> {
            jumpscare.setVisible(false);
            jumpscare.setManaged(false);
            jumpscare.setOpacity(1.0);
            jumpscare.setTranslateX(0);
            // Schedule next jumpscare
            scheduleNextMuffetJumpscare();
        });

        SequentialTransition total = new SequentialTransition(appear, stay, fadeOut);
        total.play();
    }

    /**
     * Call this method when the user logs in.
     */
    private void onUserLoggedIn() {
        isLoggedIn = true;
        startMuffetJumpscareLoop();
    }

    /**
     * Call this method when the user logs out.
     */
    private void onUserLoggedOut() {
        isLoggedIn = false;
        if (muffetJumpscareTimeline != null) muffetJumpscareTimeline.stop();
        imgMuffetJumpscare0.setVisible(false);
        imgMuffetJumpscare0.setManaged(false);
        imgMuffetJumpscare1.setVisible(false);
        imgMuffetJumpscare1.setManaged(false);
    }

    /**
     * Starts random lightning effects in the background (much less frequent)
     */
    private void startRandomLightning() {
        Timeline lightningScheduler = new Timeline();
        lightningScheduler.setCycleCount(Timeline.INDEFINITE);
        
        lightningScheduler.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1), e -> {
                // Much lower chance for lightning (about 0.5% chance every second)
                if (random.nextDouble() < 0.005) {
                    animationHandler.playLightningEffect(root);
                }
                
                // Schedule next check with longer random interval
                double nextDelay = 15.0 + random.nextDouble() * 30.0; // 15-45 seconds
                lightningScheduler.stop();
                lightningScheduler.getKeyFrames().set(0,
                    new KeyFrame(Duration.seconds(nextDelay), ev -> {
                        if (random.nextDouble() < 0.03) { // 3% chance
                            animationHandler.playLightningEffect(root);
                        }
                    })
                );
                lightningScheduler.playFromStart();
            })
        );
        lightningScheduler.play();
    }

    @Override
    public void initialize() {
        // Not used, but required by superclass
    }

    @FXML
    void onMouseClickedLblPlay(MouseEvent event) {
        animationHandler.stopLevitationWithGlitch(imgLeftCard);
        animationHandler.stopLevitationWithGlitch(imgCenterCard);
        animationHandler.stopLevitationWithGlitch(imgRightCard);

        AnimationHandler.fadeOut(lblPlay, 200, null);

        AnimationHandler.flipAway(imgLeftCard, -3000.0, -15.0, null);
        AnimationHandler.flipAway(imgRightCard, 3000.0, 15.0, null);

        Pane parentPane = (Pane) imgCenterCard.getParent();
        animationHandler.animateCenterCardCrazyExit(imgCenterCard, () -> {
            imgSlash.setVisible(true);
            imgSlash.setImage(imgSlash.getImage());
            
            // Play attack sound when slash appears
            soundUtils.playAttackSound();

            PauseTransition pause = new PauseTransition(Duration.millis(400));
            pause.setOnFinished(e -> {
                imgSlash.setVisible(false);

                vboxStartDisplay.setManaged(false);
                vboxStartDisplay.setVisible(false);

                vboxLoginDisplay.setVisible(true);
                vboxLoginDisplay.setManaged(true);

                // Instead of animateLoginEntrance, use the new glitchyFadeIn for children
                animationHandler.glitchyFadeInChildren(vboxLoginDisplay);

                imgHeart0.setVisible(true);
                imgHeart1.setVisible(true);
                imgHeart2.setVisible(true);

                btnAbout.setVisible(true);
                btnAbout.setManaged(true);
                btnGuest.setVisible(true);
                btnGuest.setManaged(true);
                btnSignUp.setVisible(true);
                btnSignUp.setManaged(true);

                Pane heartPane = (Pane) imgHeart0.getParent();
                animationHandler.startUndertaleHeartBackground(
                    Arrays.asList(imgHeart0, imgHeart1, imgHeart2),
                    heartPane
                );

                // Start Undertale-inspired cinematic with hearts
                playUndertaleHeartsCinematic(Arrays.asList(imgHeart0, imgHeart1, imgHeart2), heartPane);
                
                // Start background music 1.5 seconds after slash
                PauseTransition musicDelay = new PauseTransition(Duration.millis(1500));
                musicDelay.setOnFinished(musicEvent -> soundUtils.playBGM());
                musicDelay.play();
            });
            pause.play();
        }, parentPane);
    }

    private void playUndertaleHeartsCinematic(List<ImageView> hearts, Pane heartPane) {
        // Stop any existing heart animations
        animationHandler.stopUndertaleHeartBackground();
        
        // EPIC UNDERTALE-INSPIRED CINEMATIC SEQUENCE
        SequentialTransition epicCinematic = new SequentialTransition();
        
        // === PHASE 1: HEART FORMATION (2 seconds) ===
        ParallelTransition formationPhase = createHeartFormationPhase(hearts);
        epicCinematic.getChildren().add(formationPhase);
        
        // === PHASE 2: ATTACK SEQUENCE 1 - BULLET HELL (3 seconds) ===
        ParallelTransition bulletHellPhase = createBulletHellPhase(hearts, heartPane);
        epicCinematic.getChildren().add(bulletHellPhase);
        
        // === PHASE 3: MUFFET JUMPSCARE + SLASH ATTACK (2.5 seconds) ===
        ParallelTransition muffetAttackPhase = createMuffetAttackPhase(hearts, heartPane);
        epicCinematic.getChildren().add(muffetAttackPhase);
        
        // === PHASE 4: FINAL PATTERN DANCE (3 seconds) ===
        ParallelTransition finalPhase = createFinalDancePhase(hearts, heartPane);
        epicCinematic.getChildren().add(finalPhase);
        
        // === FINALE: HEARTS SETTLE INTO PEACEFUL MOVEMENT ===
        epicCinematic.setOnFinished(e -> {
            // Reset hearts to normal state and start gentle background movement
            for (ImageView heart : hearts) {
                heart.setScaleX(1.0);
                heart.setScaleY(1.0);
                heart.setRotate(0);
                heart.setEffect(null);
            }
            
            // Start the normal Undertale heart background movement
            animationHandler.startUndertaleHeartBackground(hearts, heartPane);
        });
        
        epicCinematic.play();
    }
    
    private ParallelTransition createHeartFormationPhase(List<ImageView> hearts) {
        ParallelTransition formation = new ParallelTransition();
        
        // Hearts start scattered and move into triangle formation
        for (int i = 0; i < hearts.size(); i++) {
            ImageView heart = hearts.get(i);
            
            // Set random starting positions (off center)
            double startX = 200 + Math.random() * 800;
            double startY = 100 + Math.random() * 400;
            heart.setLayoutX(startX);
            heart.setLayoutY(startY);
            heart.setOpacity(0.3);
            heart.setScaleX(0.5);
            heart.setScaleY(0.5);
            
            // Target positions (triangle formation)
            double targetX = 640 + (i - 1) * 80; // Center heart at 640, others spread
            double targetY = 350 + (i == 0 ? -60 : 40); // Top heart higher
            
            // Create formation animation with dramatic easing
            Timeline heartFormation = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(heart.layoutXProperty(), startX),
                    new KeyValue(heart.layoutYProperty(), startY),
                    new KeyValue(heart.opacityProperty(), 0.3),
                    new KeyValue(heart.scaleXProperty(), 0.5),
                    new KeyValue(heart.scaleYProperty(), 0.5)
                ),
                new KeyFrame(Duration.millis(1500),
                    new KeyValue(heart.layoutXProperty(), targetX, Interpolator.EASE_OUT),
                    new KeyValue(heart.layoutYProperty(), targetY, Interpolator.EASE_OUT),
                    new KeyValue(heart.opacityProperty(), 1.0),
                    new KeyValue(heart.scaleXProperty(), 1.2),
                    new KeyValue(heart.scaleYProperty(), 1.2)
                ),
                new KeyFrame(Duration.millis(2000),
                    new KeyValue(heart.scaleXProperty(), 1.0),
                    new KeyValue(heart.scaleYProperty(), 1.0)
                )
            );
            
            formation.getChildren().add(heartFormation);
        }
        
        // Add screen flash effect at formation completion
        Timeline formationFlash = new Timeline(
            new KeyFrame(Duration.millis(1500), e -> {
                animationHandler.playLightningEffect(root);
                soundUtils.playAttackSound();
            })
        );
        formation.getChildren().add(formationFlash);
        
        return formation;
    }
    
    private ParallelTransition createBulletHellPhase(List<ImageView> hearts, Pane heartPane) {
        ParallelTransition bulletHell = new ParallelTransition();
        
        // Create bullet-like projectiles (using small rectangles)
        List<Rectangle> bullets = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Rectangle bullet = new Rectangle(8, 8, Color.LIGHTCYAN);
            bullet.setLayoutX(-50); // Start off-screen
            bullet.setLayoutY(100 + i * 50);
            bullet.setVisible(true);
            // Add glowing effect to bullets
            javafx.scene.effect.Glow bulletGlow = new javafx.scene.effect.Glow(0.8);
            bullet.setEffect(bulletGlow);
            
            root.getChildren().add(bullet);
            bullets.add(bullet);
            
            // Animate bullets moving across screen
            Timeline bulletMove = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(bullet.layoutXProperty(), -50)
                ),
                new KeyFrame(Duration.millis(2000 + i * 100),
                    new KeyValue(bullet.layoutXProperty(), 1330, Interpolator.LINEAR)
                )
            );
            bulletMove.setOnFinished(e -> root.getChildren().remove(bullet));
            bulletHell.getChildren().add(bulletMove);
        }
        
        // Hearts dodge the bullets with precise timing
        for (int i = 0; i < hearts.size(); i++) {
            ImageView heart = hearts.get(i);
            Timeline dodgeSequence = new Timeline();
            
            // Dodge pattern: move up, down, spiral
            dodgeSequence.getKeyFrames().addAll(
                // Initial dodge up
                new KeyFrame(Duration.millis(200),
                    new KeyValue(heart.translateYProperty(), -80, Interpolator.EASE_BOTH)
                ),
                // Quick dodge down
                new KeyFrame(Duration.millis(800),
                    new KeyValue(heart.translateYProperty(), 60, Interpolator.EASE_BOTH),
                    new KeyValue(heart.translateXProperty(), -40, Interpolator.EASE_BOTH)
                ),
                // Spiral dodge
                new KeyFrame(Duration.millis(1400),
                    new KeyValue(heart.translateYProperty(), -30, Interpolator.EASE_BOTH),
                    new KeyValue(heart.translateXProperty(), 50, Interpolator.EASE_BOTH),
                    new KeyValue(heart.rotateProperty(), 360)
                ),
                // Final dodge and stabilize
                new KeyFrame(Duration.millis(2200),
                    new KeyValue(heart.translateYProperty(), 20, Interpolator.EASE_BOTH),
                    new KeyValue(heart.translateXProperty(), -10, Interpolator.EASE_BOTH),
                    new KeyValue(heart.rotateProperty(), 0)
                ),
                new KeyFrame(Duration.millis(3000),
                    new KeyValue(heart.translateYProperty(), 0),
                    new KeyValue(heart.translateXProperty(), 0),
                    new KeyValue(heart.rotateProperty(), 0)
                )
            );
            
            bulletHell.getChildren().add(dodgeSequence);
        }
        
        // Add dramatic effects during bullet hell
        Timeline effects = new Timeline(
            new KeyFrame(Duration.millis(300), e -> {
                soundUtils.playAttackSound();
                animationHandler.playDramaticFlash(root, Color.CYAN, 200);
            }),
            new KeyFrame(Duration.millis(600), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(1200), e -> {
                soundUtils.playAttackSound();
                animationHandler.playEpicScreenShake(root, 300, 8);
            }),
            new KeyFrame(Duration.millis(1800), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(2400), e -> {
                soundUtils.playAttackSound();
                animationHandler.playDramaticFlash(root, Color.WHITE, 150);
            })
        );
        bulletHell.getChildren().add(effects);
        
        return bulletHell;
    }
    
    private ParallelTransition createMuffetAttackPhase(List<ImageView> hearts, Pane heartPane) {
        ParallelTransition muffetPhase = new ParallelTransition();
        
        // Show Muffet jumpscares with enhanced effects
        Timeline jumpscareSequence = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                imgMuffetJumpscare0.setVisible(true);
                imgMuffetJumpscare0.setOpacity(0);
                soundUtils.playAttackSound();
                animationHandler.playDramaticFlash(root, Color.PURPLE, 300);
            }),
            new KeyFrame(Duration.millis(50),
                new KeyValue(imgMuffetJumpscare0.opacityProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(400),
                new KeyValue(imgMuffetJumpscare0.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(500), e -> {
                imgMuffetJumpscare0.setVisible(false);
                imgMuffetJumpscare1.setVisible(true);
                imgMuffetJumpscare1.setOpacity(0);
                animationHandler.playLightningEffect(root);
                animationHandler.playEpicScreenShake(root, 400, 12);
            }),
            new KeyFrame(Duration.millis(550),
                new KeyValue(imgMuffetJumpscare1.opacityProperty(), 0.9)
            ),
            new KeyFrame(Duration.millis(900),
                new KeyValue(imgMuffetJumpscare1.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(1000), e -> {
                imgMuffetJumpscare1.setVisible(false);
                // Enhanced screen shake with dramatic flash
                animationHandler.playEpicScreenShake(root, 600, 15);
                animationHandler.playDramaticFlash(root, Color.RED, 400);
                soundUtils.playAttackSound();
            }),
            new KeyFrame(Duration.millis(1600), e -> {
                // Second wave of effects
                animationHandler.playDramaticFlash(root, Color.DARKMAGENTA, 300);
                soundUtils.playAttackSound();
            })
        );
        muffetPhase.getChildren().add(jumpscareSequence);
        
        // Hearts react to Muffet attacks with enhanced defensive patterns
        for (int i = 0; i < hearts.size(); i++) {
            ImageView heart = hearts.get(i);
            Timeline defensivePattern = new Timeline();
            
            // Hearts cluster together defensively, then spread out in panic
            defensivePattern.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200),
                    new KeyValue(heart.translateXProperty(), (i - 1) * 20, Interpolator.EASE_BOTH),
                    new KeyValue(heart.translateYProperty(), -20, Interpolator.EASE_BOTH),
                    new KeyValue(heart.scaleXProperty(), 0.8),
                    new KeyValue(heart.scaleYProperty(), 0.8)
                ),
                new KeyFrame(Duration.millis(500), e -> {
                    animationHandler.playHeartHitEffect(heart);
                    animationHandler.playEnhancedGlitchEffect(heart, 1.5, 200);
                }),
                new KeyFrame(Duration.millis(700),
                    new KeyValue(heart.translateXProperty(), (i - 1) * 15, Interpolator.EASE_BOTH),
                    new KeyValue(heart.translateYProperty(), 10, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(1000), e -> {
                    animationHandler.playHeartHitEffect(heart);
                    animationHandler.playEnhancedGlitchEffect(heart, 2.0, 300);
                }),
                new KeyFrame(Duration.millis(1200),
                    new KeyValue(heart.translateXProperty(), (i - 1) * 60, Interpolator.EASE_OUT),
                    new KeyValue(heart.translateYProperty(), (i % 2 == 0 ? -40 : 40), Interpolator.EASE_OUT),
                    new KeyValue(heart.scaleXProperty(), 1.1),
                    new KeyValue(heart.scaleYProperty(), 1.1)
                ),
                new KeyFrame(Duration.millis(1600), e -> {
                    // Hearts show fear/stress response
                    animationHandler.playEnhancedGlitchEffect(heart, 1.0, 400);
                }),
                new KeyFrame(Duration.millis(2000),
                    new KeyValue(heart.translateXProperty(), 0),
                    new KeyValue(heart.translateYProperty(), 0),
                    new KeyValue(heart.scaleXProperty(), 1.0),
                    new KeyValue(heart.scaleYProperty(), 1.0)
                )
            );
            
            muffetPhase.getChildren().add(defensivePattern);
        }
        
        return muffetPhase;
    }
    
    private ParallelTransition createFinalDancePhase(List<ImageView> hearts, Pane heartPane) {
        ParallelTransition finalDance = new ParallelTransition();
        
        // Hearts perform a synchronized victory dance
        for (int i = 0; i < hearts.size(); i++) {
            ImageView heart = hearts.get(i);
            Timeline dancePattern = new Timeline();
            
            // Different dance pattern for each heart
            double phaseOffset = i * 400; // Stagger the dance
            
            dancePattern.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(phaseOffset),
                    new KeyValue(heart.scaleXProperty(), 1.0),
                    new KeyValue(heart.scaleYProperty(), 1.0),
                    new KeyValue(heart.rotateProperty(), 0)
                ),
                new KeyFrame(Duration.millis(phaseOffset + 300),
                    new KeyValue(heart.scaleXProperty(), 1.3),
                    new KeyValue(heart.scaleYProperty(), 1.3),
                    new KeyValue(heart.rotateProperty(), 180, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(phaseOffset + 600),
                    new KeyValue(heart.scaleXProperty(), 0.8),
                    new KeyValue(heart.scaleYProperty(), 0.8),
                    new KeyValue(heart.rotateProperty(), 360, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(phaseOffset + 900),
                    new KeyValue(heart.scaleXProperty(), 1.2),
                    new KeyValue(heart.scaleYProperty(), 1.2),
                    new KeyValue(heart.rotateProperty(), 0, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(phaseOffset + 1200),
                    new KeyValue(heart.scaleXProperty(), 1.0),
                    new KeyValue(heart.scaleYProperty(), 1.0),
                    new KeyValue(heart.rotateProperty(), 0)
                )
            );
            
            dancePattern.setCycleCount(2); // Repeat the dance twice
            finalDance.getChildren().add(dancePattern);
        }
        
        // Add celebration effects
        Timeline celebrationEffects = new Timeline(
            new KeyFrame(Duration.millis(200), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(800), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(1400), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(2000), e -> animationHandler.playLightningEffect(root)),
            new KeyFrame(Duration.millis(2600), e -> animationHandler.playLightningEffect(root))
        );
        finalDance.getChildren().add(celebrationEffects);
        
        return finalDance;
    }

    private void startLabelGlitch(Label label) {
        javafx.animation.Timeline glitchTimeline = new javafx.animation.Timeline();
        glitchTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);

        glitchTimeline.getKeyFrames().add(
            new javafx.animation.KeyFrame(Duration.seconds(1), event -> {
                double nextDelay = 1.0 + Math.random() * 4;
                glitchTimeline.stop();
                animationHandler.playHitEffect(label);
                glitchTimeline.getKeyFrames().set(0,
                    new javafx.animation.KeyFrame(Duration.seconds(nextDelay), ev -> handleLabelGlitch(label, glitchTimeline)));
                glitchTimeline.playFromStart();
            })
        );
        glitchTimeline.play();

        if (label == lblTitle) {
            lblTitleGlitchTimeline = glitchTimeline;
        } else if (label == lblPlay) {
            lblPlayGlitchTimeline = glitchTimeline;
        }
    }

    private void handleLabelGlitch(Label label, javafx.animation.Timeline glitchTimeline) {
        animationHandler.playHitEffect(label);
    }

    private void startNodeGlitch(javafx.scene.Node node, String nodeName) {
        javafx.animation.Timeline glitchTimeline = new javafx.animation.Timeline();
        glitchTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);

        glitchTimeline.getKeyFrames().add(
            new javafx.animation.KeyFrame(Duration.seconds(1), event -> {
                double nextDelay = 1.0 + Math.random() * 8;
                glitchTimeline.stop();
                animationHandler.playHitEffect(node);
                glitchTimeline.getKeyFrames().set(0,
                    new javafx.animation.KeyFrame(Duration.seconds(nextDelay), ev -> handleNodeGlitch(node, glitchTimeline)));
                glitchTimeline.playFromStart();
            })
        );
        glitchTimeline.play();

        switch (nodeName) {
            case "left":
                leftCardGlitchTimeline = glitchTimeline;
                break;
            case "center":
                centerCardGlitchTimeline = glitchTimeline;
                break;
            case "right":
                rightCardGlitchTimeline = glitchTimeline;
                break;
            case "loginButton":
                btnLogInGlitchTimeline = glitchTimeline;
                break;
            default:
                btnLogInGlitchTimeline = glitchTimeline;
                break;
        }
    }

    private void handleNodeGlitch(javafx.scene.Node node, javafx.animation.Timeline glitchTimeline) {
        animationHandler.playHitEffect(node);
    }

    @FXML
    void onActionBtnAbout(ActionEvent event) {
        FlowController.getInstance().goView("AboutView");
    }

    @FXML
    void onActionBtnGuest(ActionEvent event) {
        isLoggedIn = true;
        animateLoginToLoggedIn();
        loggedInUser = new UserAccountDto();
        loggedInUser.setUserNickname("Invitado");
        loggedInUser.setUserIsMusicActive(true);
        loggedInUser.setUserGuest(true);
        AppContext.getInstance().set("LoggedInUser", loggedInUser);
        onUserLoggedIn(); 
    }

    @FXML
    void onActionBtnLogIn(ActionEvent event) {
        if(!confirmFields()) {
            vboxMissingFieldsAlert.setVisible(true);
            vboxMissingFieldsAlert.setManaged(true);
            return;
        }

        String nickname = txfUser.getText();
        String password = psfPassword.getText();

        if(!userExists(nickname)) {
            vboxWrongCredentials.setVisible(true);
            vboxWrongCredentials.setManaged(true);
            return;
        }

        if(validateCredentials(nickname, password)) {
            // Update UI for logged in state
            isLoggedIn = true;
            animateLoginToLoggedIn();
            onUserLoggedIn();
            // Update logged in display info
            lblUsername.setText(loggedInUser.getUserNickname());
            // TODO: Set date created and last played dates
        } else {
            vboxWrongCredentials.setVisible(true);
            vboxWrongCredentials.setManaged(true);
        }
    }

    private UserAccountService userService;
    private UserAccountDto loggedInUser = null;
    private boolean databaseAvailable = true;

    private boolean userExists(String nickname) {
        if (!databaseAvailable) {
            // In offline mode, allow any non-empty username
            return !nickname.trim().isEmpty();
        }
        try {
            Respuesta respuesta = userService.getUserAccountByNickname(nickname);
            return respuesta.getEstado();
        } catch (Exception e) {
            System.err.println("Database error checking user existence: " + e.getMessage());
            return !nickname.trim().isEmpty(); // Fallback to simple validation
        }
    }

    private boolean validateCredentials(String nickname, String password) {
        if (!databaseAvailable) {
            // In offline mode, create a temporary user

            //TODO: Set boolean to userAccount if is a temporaly user
            loggedInUser = new UserAccountDto();
            loggedInUser.setUserNickname(nickname);
            loggedInUser.setUserIsMusicActive(true);
            AppContext.getInstance().set("LoggedInUser", loggedInUser);
            return !nickname.trim().isEmpty() && !password.trim().isEmpty();
        }
        try {
            Respuesta respuesta = userService.validateUserCredentials(nickname, password);
            if (respuesta.getEstado()) {
                loggedInUser = (UserAccountDto) respuesta.getResultado("UserAccount");
                // Store logged in user in AppContext
                AppContext.getInstance().set("LoggedInUser", loggedInUser);
                // Set music state based on user preference
                soundUtils.setMuted(!loggedInUser.isUserIsMusicActive());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Database error validating credentials: " + e.getMessage());
            // Create temporary user in case of database error
            loggedInUser = new UserAccountDto();
            loggedInUser.setUserNickname(nickname);
            loggedInUser.setUserIsMusicActive(true);
            AppContext.getInstance().set("LoggedInUser", loggedInUser);
            return !nickname.trim().isEmpty() && !password.trim().isEmpty();
        }
    }

    @FXML
    void onActionBtnSignUp(ActionEvent event) {
        FlowController.getInstance().goView("SignUpView");
    }

    @FXML
    void onActionBtnLogOut(ActionEvent event) {
        // Clear logged in user from AppContext
        AppContext.getInstance().delete("LoggedInUser");
        loggedInUser = null;
        isLoggedIn = false;

        // Restart background music
        soundUtils.stopBGM();
        soundUtils.playBGM();

        
        // Reset login fields
        txfUser.clear();
        psfPassword.clear();

    // Hide logged-in display
    vboxLoggedIn.setVisible(false);
    vboxLoggedIn.setManaged(false);

    // Hide continue/delete game dialog if visible
    if (vboxContinueOrDeleteGame != null) {
        vboxContinueOrDeleteGame.setVisible(false);
        vboxContinueOrDeleteGame.setManaged(false);
    }

    // Hide hearts and slash
    imgHeart0.setVisible(false);
    imgHeart1.setVisible(false);
    imgHeart2.setVisible(false);
    imgSlash.setVisible(false);

    // Hide jumpscares
    imgMuffetJumpscare0.setVisible(false);
    imgMuffetJumpscare0.setManaged(false);
    imgMuffetJumpscare1.setVisible(false);
    imgMuffetJumpscare1.setManaged(false);

    // Hide alerts
    vboxMissingFieldsAlert.setVisible(false);
    vboxMissingFieldsAlert.setManaged(false);
    vboxWrongCredentials.setVisible(false);
    vboxWrongCredentials.setManaged(false);

    // Hide About, Guest, and SignUp buttons
    btnAbout.setVisible(false);
    btnAbout.setManaged(false);
    btnGuest.setVisible(false);
    btnGuest.setManaged(false);
    btnSignUp.setVisible(false);
    btnSignUp.setManaged(false);

    // Show start display, hide login display
    vboxStartDisplay.setVisible(true);
    vboxStartDisplay.setManaged(true);
    vboxLoginDisplay.setVisible(false);
    vboxLoginDisplay.setManaged(false);

    // Reset dog position and stop animation
    if (imgDog != null) {
        imgDog.setLayoutX(dogOriginalX);
        imgDog.setScaleX(1.0);
    }
    if (dogMoveTimeline != null) {
        dogMoveTimeline.stop();
    }

    // Reset cards and animations
    imgCenterCard.setVisible(true);
    imgLeftCard.setVisible(true);
    imgRightCard.setVisible(true);
    animationHandler.startLevitationWithGlitch(imgLeftCard, 240);
    animationHandler.startLevitationWithGlitch(imgCenterCard, 120);
    animationHandler.startLevitationWithGlitch(imgRightCard, 0);

    // Reset glitch animations for labels/buttons
    startLabelGlitch(lblTitle);
    startLabelGlitch(lblPlay);
    startNodeGlitch(imgLeftCard, "left");
    startNodeGlitch(imgCenterCard, "center");
    startNodeGlitch(imgRightCard, "right");
    startNodeGlitch(btnLogIn, "loginButton");

    // Reset logged-in state and stop jumpscare loop
    isLoggedIn = false;
    if (muffetJumpscareTimeline != null) muffetJumpscareTimeline.stop();

    FlowController.getInstance().limpiarLoader("LogInView");
    FlowController.getInstance().goView("LogInView");

    // Optionally, reset any other stateful fields or services here
}

    @FXML
    void onActionBtnStartGame(ActionEvent event) {
        // TODO: Check for user's active game
        // If there is a game then show the vbox
        // vboxContinueOrDeleteGame.setManaged(true);
        // vboxContinueOrDeleteGame.setVisible(true);
        // if not, then start a game with its service, but
        // for now just start it by going into its view:
        FlowController.getInstance().goView("PreGameView");
    }

    @FXML
    void onActionBtnSettings(ActionEvent event) {
        FlowController.getInstance().goView("SettingsView");
    }

    @FXML
    void onActionDebugSkip(ActionEvent event) {
        FlowController.getInstance().goView("GameView");
    }

    @FXML
    void onActionBtnExit(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).close();
    }

    @FXML
    void onActionBtnConfirmAlert(ActionEvent event) {
        // Get the alert box that's currently visible
        final VBox alertToHide;
        if (vboxMissingFieldsAlert.isVisible()) {
            alertToHide = vboxMissingFieldsAlert;
        } else if (vboxWrongCredentials.isVisible()) {
            alertToHide = vboxWrongCredentials;
        } else {
            return;
        }

        // Create fade out animation with glitch
        Timeline fadeOut = new Timeline();
        int glitchSteps = 3;
        double fadeDuration = 300;

        for (int i = 0; i <= glitchSteps; i++) {
            double progress = (double) i / glitchSteps;
            double offset = (Math.random() - 0.5) * 15;
            
            fadeOut.getKeyFrames().add(
                new KeyFrame(Duration.millis(progress * fadeDuration),
                    new KeyValue(alertToHide.translateXProperty(), offset),
                    new KeyValue(alertToHide.opacityProperty(), (1.0 - progress) * 0.8 + Math.random() * 0.2)
                )
            );
        }

        // Final state
        fadeOut.getKeyFrames().add(
            new KeyFrame(Duration.millis(fadeDuration),
                new KeyValue(alertToHide.translateXProperty(), 0),
                new KeyValue(alertToHide.opacityProperty(), 0)
            )
        );

        // Hide alert when animation completes
        fadeOut.setOnFinished(e -> {
            alertToHide.setManaged(false);
            alertToHide.setVisible(false);
            alertToHide.setTranslateX(0); // Reset translation
        });

        // Play animation and sound effect
        fadeOut.play();
        soundUtils.playAttackSound();
    }
    @FXML
    void onActionBtnContinueGame(ActionEvent event){

    }

     @FXML
    void onActionBtnDeleteGame(ActionEvent event){
        
    }

    @FXML
    void onActionBtnRanking(ActionEvent event) {
        FlowController.getInstance().goView("RankingsView");
    }
    

    private Boolean confirmFields() {
        if(txfUser.getText().isBlank() || psfPassword.getText().isBlank()) {
            return false;
        }
        return true;
    }

    
    /**
     * Animates all children of vboxLoginDisplay with a glitch/fade out, then hides the login box,
     * and animates in vboxLoggedIn with a glitchy fade-in animation.
     * After that, animates the dog image left and right with glitch effect.
     */
    private void animateLoginToLoggedIn() {
        List<Node> loginChildren = vboxLoginDisplay.getChildren();
        ParallelTransition glitchFadeOut = new ParallelTransition();

        for (Node child : loginChildren) {
            // Glitch effect
            Timeline glitch = new Timeline(
                new KeyFrame(Duration.ZERO, e -> animationHandler.playHitEffect(child)),
                new KeyFrame(Duration.millis(120), e -> animationHandler.playHitEffect(child)),
                new KeyFrame(Duration.millis(220), e -> animationHandler.playHitEffect(child))
            );
            // Fade out
            FadeTransition fade = new FadeTransition(Duration.millis(400), child);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            SequentialTransition seq = new SequentialTransition(glitch, fade);
            glitchFadeOut.getChildren().add(seq);
        }

        glitchFadeOut.setOnFinished(e -> {
            // Hide login display
            vboxLoginDisplay.setVisible(false);
            vboxLoginDisplay.setManaged(false);
            // Reset children opacity for next time
            for (Node child : loginChildren) {
                child.setOpacity(1.0);
            }

            // Show logged-in display with glitchy fade-in animation
            vboxLoggedIn.setVisible(true);
            vboxLoggedIn.setManaged(true);
            glitchyFadeInVBoxChildren(vboxLoggedIn);

            startLabelGlitch(lblTitleLogged);
            startNodeGlitch(btnStartGame, "loginButton");

            // Animate the dog image left and right with glitch
            startDogMoveAnimation();
        });

        glitchFadeOut.play();
    }
         /**
     * Animates the dog image walking left, returning to center, walking right (inverted), returning to center, and repeats.
     * The default image is looking left. When walking right, the image is inverted using scaleX = -1.
     * The inversion now happens at the borders, and the speed is consistent for each segment.
     */
    private void startDogMoveAnimation() {
        if (imgDog == null) return;

        // Ensure original position is set
        dogOriginalX = imgDog.getLayoutX();

        // If already running, stop previous
        if (dogMoveTimeline != null) {
            dogMoveTimeline.stop();
        }

        double leftX = dogOriginalX - 100;
        double rightX = dogOriginalX + 100;

        // Each segment duration (ms)
        double segmentDuration = 1500;

        dogMoveTimeline = new Timeline(
            // Start at center, looking left
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgDog.layoutXProperty(), dogOriginalX),
                new KeyValue(imgDog.scaleXProperty(), 1.0)
            ),
            // Move to left (look left)
            new KeyFrame(Duration.millis(segmentDuration),
                new KeyValue(imgDog.layoutXProperty(), leftX),
                new KeyValue(imgDog.scaleXProperty(), 1.0)
            ),
            // Flip to look right at left border
            new KeyFrame(Duration.millis(segmentDuration + 10),
                new KeyValue(imgDog.scaleXProperty(), -1.0)
            ),
            // Move to center (look right)
            new KeyFrame(Duration.millis(segmentDuration * 2),
                new KeyValue(imgDog.layoutXProperty(), dogOriginalX),
                new KeyValue(imgDog.scaleXProperty(), -1.0)
            ),
            // Move to right (look right)
            new KeyFrame(Duration.millis(segmentDuration * 3),
                new KeyValue(imgDog.layoutXProperty(), rightX),
                new KeyValue(imgDog.scaleXProperty(), -1.0)
            ),
            // Flip to look left at right border
            new KeyFrame(Duration.millis(segmentDuration * 3 + 10),
                new KeyValue(imgDog.scaleXProperty(), 1.0)
            ),
            // Move to center (look left)
            new KeyFrame(Duration.millis(segmentDuration * 4),
                new KeyValue(imgDog.layoutXProperty(), dogOriginalX),
                new KeyValue(imgDog.scaleXProperty(), 1.0)
            )
        );
        dogMoveTimeline.setCycleCount(Timeline.INDEFINITE);
        dogMoveTimeline.play();
    }

    /**
     * Animates all children of the given VBox with a glitchy fade-in effect.
     * Each child starts invisible, glitches in, and then becomes fully visible.
     */
    private void glitchyFadeInVBoxChildren(VBox vbox) {
        List<Node> children = vbox.getChildren();
        ParallelTransition glitchFadeIn = new ParallelTransition();

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            child.setOpacity(0.0);

            // Glitch effect: several quick flashes and playHitEffect
            Timeline glitch = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    animationHandler.playHitEffect(child);
                    child.setOpacity(0.2);
                }),
                new KeyFrame(Duration.millis(80), e -> {
                    animationHandler.playHitEffect(child);
                    child.setOpacity(0.5);
                }),
                new KeyFrame(Duration.millis(160), e -> {
                    animationHandler.playHitEffect(child);
                    child.setOpacity(0.3);
                })
            );
            // Fade in to full opacity
            FadeTransition fade = new FadeTransition(Duration.millis(350), child);
            fade.setFromValue(child.getOpacity());
            fade.setToValue(1.0);

            SequentialTransition seq = new SequentialTransition(
                new PauseTransition(Duration.millis(i * 60)), // staggered entrance
                glitch,
                fade
            );
            glitchFadeIn.getChildren().add(seq);
        }

        glitchFadeIn.play();
    }

    @FXML
    void onActionForgotPassword(ActionEvent event) {
        // clear the fields
        psfPassword.clear();
        txfUser.clear();
        FlowController.getInstance().goView("PasswordResetView");
    }

}
