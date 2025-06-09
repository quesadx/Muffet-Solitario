package cr.ac.una.muffetsolitario.util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class AnimationHandler {

    private static AnimationHandler INSTANCE;

    private final Map<Node, Timeline> levitationTimelines = new HashMap<>();
    private final Map<Node, Timeline> glitchTimelines = new HashMap<>();
    private final Map<ImageView, Timeline> heartTimelines = new HashMap<>();
    private final Random random = new Random();

    private AnimationHandler() {}

    public static AnimationHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnimationHandler();
        }
        return INSTANCE;
    }

    // --- Levitation Animation ---
    public void startLevitation(Node node, int phaseOffsetMs) {
        stopLevitation(node);
        double amplitude = 15.0;
        double duration = 8000.0;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.translateYProperty(), 0)),
                new KeyFrame(Duration.millis(duration / 2), new KeyValue(node.translateYProperty(), -amplitude)),
                new KeyFrame(Duration.millis(duration), new KeyValue(node.translateYProperty(), 0))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.setDelay(Duration.millis(phaseOffsetMs));
        timeline.play();
        levitationTimelines.put(node, timeline);
    }

    public void stopLevitation(Node node) {
        Timeline t = levitationTimelines.remove(node);
        if (t != null) t.stop();
        node.setTranslateY(0);
    }

    public void stopAllLevitation() {
        for (Timeline t : levitationTimelines.values()) t.stop();
        levitationTimelines.clear();
    }

    // --- Glitch Animation ---
    public void startGlitch(Node node) {
        stopGlitch(node);
        Timeline timeline = new Timeline();
        scheduleNextGlitch(node, timeline);
        glitchTimelines.put(node, timeline);
        timeline.play();
    }

    public void stopGlitch(Node node) {
        Timeline t = glitchTimelines.remove(node);
        if (t != null) t.stop();
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1.0);
        node.setScaleY(1.0);
    }

    public void stopAllGlitch() {
        for (Timeline t : glitchTimelines.values()) t.stop();
        for (Node n : glitchTimelines.keySet()) {
            n.setTranslateX(0);
            n.setTranslateY(0);
            n.setScaleX(1.0);
            n.setScaleY(1.0);
        }
        glitchTimelines.clear();
    }

    private void scheduleNextGlitch(Node node, Timeline timeline) {
        double delay = 1200 + random.nextInt(2300);
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delay), e -> {
            applyGlitch(node, timeline);
        }));
        timeline.playFromStart();
    }

    private void applyGlitch(Node node, Timeline timeline) {
        double glitchX = (random.nextBoolean() ? 1 : -1) * (3 + random.nextDouble() * 7);
        double glitchY = (random.nextBoolean() ? 1 : -1) * (2 + random.nextDouble() * 6);
        double glitchScale = 1.0 + (random.nextBoolean() ? 1 : -1) * (0.01 + random.nextDouble() * 0.04);
        double glitchDuration = 60 + random.nextInt(60);
        double restoreDuration = 80 + random.nextInt(80);

        Timeline glitch = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.translateXProperty(), node.getTranslateX()),
                        new KeyValue(node.translateYProperty(), node.getTranslateY()),
                        new KeyValue(node.scaleXProperty(), node.getScaleX()),
                        new KeyValue(node.scaleYProperty(), node.getScaleY())
                ),
                new KeyFrame(Duration.millis(glitchDuration),
                        new KeyValue(node.translateXProperty(), node.getTranslateX() + glitchX),
                        new KeyValue(node.translateYProperty(), node.getTranslateY() + glitchY),
                        new KeyValue(node.scaleXProperty(), glitchScale),
                        new KeyValue(node.scaleYProperty(), glitchScale)
                ),
                new KeyFrame(Duration.millis(glitchDuration + restoreDuration),
                        new KeyValue(node.translateXProperty(), node.getTranslateX()),
                        new KeyValue(node.translateYProperty(), node.getTranslateY()),
                        new KeyValue(node.scaleXProperty(), 1.0),
                        new KeyValue(node.scaleYProperty(), 1.0)
                )
        );
        glitch.setOnFinished(e -> scheduleNextGlitch(node, timeline));
        glitch.play();
    }

    // --- Combined Levitation + Glitch ---
    public void startLevitationWithGlitch(Node node, int phaseOffsetMs) {
        startLevitation(node, phaseOffsetMs);
        startGlitch(node);
    }

    public void stopLevitationWithGlitch(Node node) {
        stopLevitation(node);
        stopGlitch(node);
    }

    public void stopAllLevitationWithGlitch() {
        stopAllLevitation();
        stopAllGlitch();
    }

    // --- Sick Entrance Animation for Login Display ---
    /**
     * Animates all children of the given Pane into place with a bouncy or glitchy effect.
     * Each child starts from its current position (or offscreen if you wish) and animates to layoutX/layoutY = 0.
     * Optionally, you can randomize the order and add a little glitch at the end.
     */
    public void animateLoginEntrance(Pane container) {
        List<Node> children = new ArrayList<>(container.getChildren());
        Random rand = new Random();

        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);

            // Start each node slightly offset and invisible
            double startX = (rand.nextBoolean() ? 1 : -1) * (60 + rand.nextInt(80));
            double startY = (rand.nextBoolean() ? 1 : -1) * (40 + rand.nextInt(60));
            node.setTranslateX(startX);
            node.setTranslateY(startY);
            node.setOpacity(0);

            // Animate to position with bounce and fade in
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(node.translateXProperty(), startX),
                            new KeyValue(node.translateYProperty(), startY),
                            new KeyValue(node.opacityProperty(), 0)
                    ),
                    new KeyFrame(Duration.millis(400 + rand.nextInt(200)),
                            new KeyValue(node.opacityProperty(), 1)
                    ),
                    new KeyFrame(Duration.millis(700 + rand.nextInt(200)),
                            new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_OUT),
                            new KeyValue(node.translateYProperty(), 0, Interpolator.EASE_OUT)
                    )
            );
            timeline.setDelay(Duration.millis(i * 80 + rand.nextInt(60)));
            timeline.setOnFinished(e -> {
                // Optional: add a quick glitch at the end for extra "sick" effect
                if (rand.nextDouble() < 0.7) {
                    Timeline glitch = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(node.scaleXProperty(), 1.0),
                                    new KeyValue(node.scaleYProperty(), 1.0)
                            ),
                            new KeyFrame(Duration.millis(60),
                                    new KeyValue(node.scaleXProperty(), 1.05 + rand.nextDouble() * 0.07),
                                    new KeyValue(node.scaleYProperty(), 0.95 + rand.nextDouble() * 0.07)
                            ),
                            new KeyFrame(Duration.millis(120),
                                    new KeyValue(node.scaleXProperty(), 1.0),
                                    new KeyValue(node.scaleYProperty(), 1.0)
                            )
                    );
                    glitch.play();
                }
            });
            timeline.play();
        }
    }

    // --- Existing Animations (translate, scale, popScale, flipAway, pixelSnap, fadeOut) ---
    public static void translateExponential(Node node, Float x, Float y) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(600), node);
        transition.setToX(x);
        transition.setToY(y);
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition.play();
    }

    public static void translate(Node node, Float x, Float y) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(600), node);
        transition.setToX(x);
        transition.setToY(y);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.play();
    }

    public static void scaleExponential(Node node, Float scaleFactor) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(600), node);
        transition.setToX(scaleFactor);
        transition.setToY(scaleFactor);
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition.play();
    }

    public static void popScale(Node node, double scaleTo, Runnable onFinished) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.scaleXProperty(), 1.0), new KeyValue(node.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(80), new KeyValue(node.scaleXProperty(), scaleTo), new KeyValue(node.scaleYProperty(), scaleTo)),
            new KeyFrame(Duration.millis(160), new KeyValue(node.scaleXProperty(), 1.1), new KeyValue(node.scaleYProperty(), 1.1)),
            new KeyFrame(Duration.millis(240), new KeyValue(node.scaleXProperty(), 1.0), new KeyValue(node.scaleYProperty(), 1.0))
        );
        timeline.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        timeline.play();
    }

    public static void flipAway(Node node, double translateX, double rotateBy, Runnable onFinished) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(0),
                new KeyValue(node.translateXProperty(), 0),
                new KeyValue(node.rotateProperty(), 0)
            ),
            new KeyFrame(Duration.millis(80),
                new KeyValue(node.translateXProperty(), translateX * 0.3),
                new KeyValue(node.rotateProperty(), rotateBy * 0.5)
            ),
            new KeyFrame(Duration.millis(160),
                new KeyValue(node.translateXProperty(), translateX * 0.7),
                new KeyValue(node.rotateProperty(), rotateBy)
            ),
            new KeyFrame(Duration.millis(240),
                new KeyValue(node.translateXProperty(), translateX),
                new KeyValue(node.rotateProperty(), rotateBy)
            )
        );
        timeline.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        timeline.play();
    }

    public static void pixelSnap(Node node, double amplitude, int steps, Runnable onFinished) {
        int interval = 60;
        Timeline timeline = new Timeline();
        for (int i = 0; i < steps; i++) {
            double offset = (i % 2 == 0 ? amplitude : -amplitude);
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * interval),
                    new KeyValue(node.translateYProperty(), offset)
                )
            );
        }
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(steps * interval),
                new KeyValue(node.translateYProperty(), 0)
            )
        );
        timeline.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        timeline.play();
    }

    public static void fadeOut(Node node, double durationMillis, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMillis), node);
        fade.setToValue(0);
        fade.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        fade.play();
    }

    /**
     * Enhanced "crazy" exit animation on the center card with improved effects:
     * 1. Earthquake (shake)
     * 2. Smooth morphing spin with scale pulsing
     * 3. Particle explosion effect
     * 4. Rip apart with enhanced visual effects
     */
    public void animateCenterCardCrazyExit(ImageView centerCard, Runnable onFinished, Pane parentPane) {
        // Play attack sound
        cr.ac.una.muffetsolitario.util.SoundUtils.getInstance().playAttackSound();
        
        // 1. Enhanced earthquake with varying intensity
        Timeline earthquake = new Timeline();
        int shakes = 24;
        double maxShakeAmplitude = 15;
        int interval = 25;
        
        for (int i = 0; i < shakes; i++) {
            double intensity = Math.sin((double) i / shakes * Math.PI); // Sine wave intensity
            double offset = (i % 2 == 0 ? intensity * maxShakeAmplitude : -intensity * maxShakeAmplitude);
            earthquake.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * interval),
                    new KeyValue(centerCard.translateXProperty(), offset),
                    new KeyValue(centerCard.translateYProperty(), offset * 0.3)
                )
            );
        }
        earthquake.getKeyFrames().add(
            new KeyFrame(Duration.millis(shakes * interval),
                new KeyValue(centerCard.translateXProperty(), 0),
                new KeyValue(centerCard.translateYProperty(), 0)
            )
        );

        // 2. Pixel-art style tilting animation
        Timeline pixelTilt = new Timeline();
        int tiltFrames = 20;
        double tiltDuration = 800;
        
        // Create a stepped/pixelated effect by using fewer frames
        for (int i = 0; i <= tiltFrames; i++) {
            double progress = (double) i / tiltFrames;
            
            // Tilt back and forth instead of spinning
            double tiltAngle = Math.sin(progress * Math.PI * 3) * 25; // Tilt ±25 degrees
            
            // Stepped scaling for pixel-art feel
            double scaleProgress = Math.floor(progress * 4) / 4.0; // Steps of 0.25
            double scale = 1.0 + Math.sin(scaleProgress * Math.PI * 2) * 0.2; // Smaller scale range
            
            // Pixelated opacity changes
            double opacityProgress = Math.floor(progress * 5) / 5.0; // Steps of 0.2
            double opacity = 1.0 - Math.sin(opacityProgress * Math.PI) * 0.15; // Subtle opacity
            
            pixelTilt.getKeyFrames().add(
                new KeyFrame(Duration.millis(progress * tiltDuration),
                    new KeyValue(centerCard.rotateProperty(), tiltAngle, Interpolator.DISCRETE),
                    new KeyValue(centerCard.scaleXProperty(), scale, Interpolator.DISCRETE),
                    new KeyValue(centerCard.scaleYProperty(), scale, Interpolator.DISCRETE),
                    new KeyValue(centerCard.opacityProperty(), opacity, Interpolator.DISCRETE)
                )
            );
        }

        // 3. Final explosion and rip
        pixelTilt.setOnFinished(e -> {
            // Create particle explosion effect
            createParticleExplosion(centerCard, parentPane);
            
            // Enhanced rip effect
            Timeline finalExplosion = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(centerCard.scaleXProperty(), 2.5),
                    new KeyValue(centerCard.scaleYProperty(), 2.5),
                    new KeyValue(centerCard.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(200),
                    new KeyValue(centerCard.scaleXProperty(), 4.0),
                    new KeyValue(centerCard.scaleYProperty(), 4.0),
                    new KeyValue(centerCard.opacityProperty(), 0.0)
                )
            );
            
            finalExplosion.setOnFinished(ev -> {
                centerCard.setVisible(false);
                if (onFinished != null) onFinished.run();
            });
            finalExplosion.play();
        });

        // Play sequence
        earthquake.setOnFinished(e -> pixelTilt.play());
        earthquake.play();
    }

    /**
     * Creates a particle explosion effect around the given node
     */
    private void createParticleExplosion(ImageView centerNode, Pane parentPane) {
        double centerX = centerNode.getLayoutX() + centerNode.getFitWidth() / 2;
        double centerY = centerNode.getLayoutY() + centerNode.getFitHeight() / 2;
        
        for (int i = 0; i < 12; i++) {
            ImageView particle = new ImageView(centerNode.getImage());
            particle.setFitWidth(20);
            particle.setFitHeight(20);
            particle.setLayoutX(centerX - 10);
            particle.setLayoutY(centerY - 10);
            
            double angle = (2 * Math.PI * i) / 12;
            double distance = 150 + random.nextDouble() * 100;
            double targetX = centerX + Math.cos(angle) * distance;
            double targetY = centerY + Math.sin(angle) * distance;
            
            parentPane.getChildren().add(particle);
            
            Timeline particleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(particle.layoutXProperty(), centerX - 10),
                    new KeyValue(particle.layoutYProperty(), centerY - 10),
                    new KeyValue(particle.opacityProperty(), 1.0),
                    new KeyValue(particle.scaleXProperty(), 1.0),
                    new KeyValue(particle.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(600),
                    new KeyValue(particle.layoutXProperty(), targetX - 10),
                    new KeyValue(particle.layoutYProperty(), targetY - 10),
                    new KeyValue(particle.opacityProperty(), 0.0),
                    new KeyValue(particle.scaleXProperty(), 0.2),
                    new KeyValue(particle.scaleYProperty(), 0.2)
                )
            );
            
            particleAnim.setOnFinished(ev -> parentPane.getChildren().remove(particle));
            particleAnim.play();
        }
    }
      /**
     * Starts Undertale-style heart movement for the given hearts within the given area.
     * Hearts will move randomly, bounce off edges, and avoid overlapping each other.
     * @param hearts List of ImageViews representing hearts.
     * @param areaPane The Pane that defines the movement area (should be the parent of the hearts).
     */
    public void startUndertaleHeartBackground(List<ImageView> hearts, Pane areaPane) {
        stopUndertaleHeartBackground(); // Clean up any previous

        // Store heart info: position, velocity
        class HeartState {
            double x, y, vx, vy, size;
            ImageView node;
            HeartState(ImageView node) {
                this.node = node;
                this.size = Math.max(node.getFitWidth(), node.getFitHeight());
                this.x = node.getLayoutX();
                this.y = node.getLayoutY();
                // Random initial velocity
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 60 + random.nextDouble() * 40; // px/sec
                this.vx = Math.cos(angle) * speed;
                this.vy = Math.sin(angle) * speed;
            }
        }

        List<HeartState> states = new ArrayList<>();
        for (ImageView heart : hearts) {
            states.add(new HeartState(heart));
        }

        double paneW = areaPane.getWidth();
        double paneH = areaPane.getHeight();

        // If pane size is not set yet, wait for layout pass
        if (paneW == 0 || paneH == 0) {
            Platform.runLater(() -> startUndertaleHeartBackground(hearts, areaPane));
            return;
        }

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        final double dt = 0.016; // ~60 FPS

        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(dt), e -> {
            // Update pane size in case of resize
            double w = areaPane.getWidth();
            double h = areaPane.getHeight();

            // Move each heart
            for (int i = 0; i < states.size(); i++) {
                HeartState hi = states.get(i);

                // Randomly "hit" (change direction) sometimes
                if (random.nextDouble() < 0.01) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double speed = 60 + random.nextDouble() * 40;
                    hi.vx = Math.cos(angle) * speed;
                    hi.vy = Math.sin(angle) * speed;
                }

                // Move
                hi.x += hi.vx * dt;
                hi.y += hi.vy * dt;

                // Bounce off walls
                if (hi.x < 0) { hi.x = 0; hi.vx = Math.abs(hi.vx); }
                if (hi.x > w - hi.size) { hi.x = w - hi.size; hi.vx = -Math.abs(hi.vx); }
                if (hi.y < 0) { hi.y = 0; hi.vy = Math.abs(hi.vy); }
                if (hi.y > h - hi.size) { hi.y = h - hi.size; hi.vy = -Math.abs(hi.vy); }

                // Avoid overlap with other hearts (simple repulsion)
                for (int j = 0; j < states.size(); j++) {
                    if (i == j) continue;
                    HeartState hj = states.get(j);
                    double dx = hi.x - hj.x;
                    double dy = hi.y - hj.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    double minDist = (hi.size + hj.size) * 0.7;
                    if (dist < minDist && dist > 1) {
                        // Repel
                        double overlap = minDist - dist;
                        hi.x += (dx / dist) * (overlap / 2);
                        hi.y += (dy / dist) * (overlap / 2);
                        hj.x -= (dx / dist) * (overlap / 2);
                        hj.y -= (dy / dist) * (overlap / 2);
                        // Also tweak velocities a bit
                        hi.vx += (dx / dist) * 10;
                        hi.vy += (dy / dist) * 10;
                        hj.vx -= (dx / dist) * 10;
                        hj.vy -= (dy / dist) * 10;
                    }
                }
            }

            // Apply positions to nodes
            for (HeartState hs : states) {
                hs.node.setLayoutX(hs.x);
                hs.node.setLayoutY(hs.y);
            }
        }));

        timeline.play();

        // Store one timeline for all hearts (for easy stop)
        for (ImageView heart : hearts) {
            heartTimelines.put(heart, timeline);
        }
    }

    /**
     * Stops all Undertale-style heart background animations.
     */
    public void stopUndertaleHeartBackground() {
        for (Timeline t : new HashSet<>(heartTimelines.values())) {
            t.stop();
        }
        heartTimelines.clear();
    }

        /**
     * Makes a heart tremble and flash red with two distinct hits for more visibility.
     * @param heart The ImageView representing the heart.
     */
    public void playHeartHitEffect(ImageView heart) {
        // First hit animation
        Timeline firstHit = new Timeline();
        int shakes = 6;
        double amplitude = 12; // Increased amplitude
        int interval = 20; // Faster shaking
        
        // First hit shake
        for (int i = 0; i < shakes; i++) {
            double offset = (i % 2 == 0 ? amplitude : -amplitude);
            firstHit.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * interval),
                    new KeyValue(heart.translateXProperty(), offset),
                    new KeyValue(heart.translateYProperty(), offset * 0.5) // Add vertical movement
                )
            );
        }
        firstHit.getKeyFrames().add(
            new KeyFrame(Duration.millis(shakes * interval),
                new KeyValue(heart.translateXProperty(), 0),
                new KeyValue(heart.translateYProperty(), 0)
            )
        );

        // Second hit animation (after a brief pause)
        Timeline secondHit = new Timeline();
        for (int i = 0; i < shakes; i++) {
            double offset = (i % 2 == 0 ? amplitude * 0.8 : -amplitude * 0.8);
            secondHit.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * interval),
                    new KeyValue(heart.translateXProperty(), -offset), // Opposite direction
                    new KeyValue(heart.translateYProperty(), offset * 0.5)
                )
            );
        }
        secondHit.getKeyFrames().add(
            new KeyFrame(Duration.millis(shakes * interval),
                new KeyValue(heart.translateXProperty(), 0),
                new KeyValue(heart.translateYProperty(), 0)
            )
        );

        // Enhanced color effect with two flashes
        javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
        javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
        blend.setTopInput(colorAdjust);

        // Save previous effect to restore
        javafx.scene.effect.Effect prevEffect = heart.getEffect();

        // Two color flashes
        Timeline colorFlash = new Timeline(
            // First flash
            new KeyFrame(Duration.ZERO, evt -> {
                colorAdjust.setHue(-0.5);
                colorAdjust.setSaturation(1.0);
                colorAdjust.setBrightness(0.3); // Brighter flash
                heart.setEffect(blend);
            }),
            new KeyFrame(Duration.millis(shakes * interval), evt -> {
                heart.setEffect(prevEffect);
            }),
            // Second flash
            new KeyFrame(Duration.millis(shakes * interval + 100), evt -> {
                colorAdjust.setHue(-0.5);
                colorAdjust.setSaturation(1.0);
                colorAdjust.setBrightness(0.3);
                heart.setEffect(blend);
            }),
            new KeyFrame(Duration.millis(shakes * interval * 2 + 100), evt -> {
                heart.setEffect(prevEffect);
            })
        );

        // Play animations in sequence
        firstHit.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(p -> secondHit.play());
            pause.play();
        });
        
        firstHit.play();
        colorFlash.play();
    }

     /**
     * Glitchy fade-in for all children of a Pane (VBox, HBox, etc).
     * Each child starts invisible, glitches in, and then becomes fully visible.
     * @param pane The parent container whose children will be animated.
     */
    public void glitchyFadeInChildren(Pane pane, double... delays) {
        List<Node> children = new ArrayList<>(pane.getChildren());
        ParallelTransition glitchFadeIn = new ParallelTransition();

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            child.setOpacity(0.0);

            // Optionally, randomize the order for a more chaotic effect
            int delay = (int) (i * 60 + (delays.length > i ? delays[i] : random.nextInt(40)));

            Timeline glitch = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    playHitEffect(child);
                    child.setOpacity(0.15 + random.nextDouble() * 0.2);
                    child.setTranslateX(random.nextBoolean() ? random.nextInt(6) : -random.nextInt(6));
                }),
                new KeyFrame(Duration.millis(60), e -> {
                    playHitEffect(child);
                    child.setOpacity(0.4 + random.nextDouble() * 0.2);
                    child.setTranslateX(random.nextBoolean() ? random.nextInt(8) : -random.nextInt(8));
                }),
                new KeyFrame(Duration.millis(120), e -> {
                    playHitEffect(child);
                    child.setOpacity(0.25 + random.nextDouble() * 0.2);
                    child.setTranslateX(random.nextBoolean() ? random.nextInt(5) : -random.nextInt(5));
                }),
                new KeyFrame(Duration.millis(180), e -> {
                    playHitEffect(child);
                    child.setOpacity(0.5 + random.nextDouble() * 0.2);
                    child.setTranslateX(0);
                })
            );
            FadeTransition fade = new FadeTransition(Duration.millis(350), child);
            fade.setFromValue(child.getOpacity());
            fade.setToValue(1.0);

            SequentialTransition seq = new SequentialTransition(
                new PauseTransition(Duration.millis(delay)),
                glitch,
                fade
            );
            glitchFadeIn.getChildren().add(seq);
        }
        glitchFadeIn.play();
    }

    /**
     * Makes a node tremble and flash red as if hit, then returns to normal.
     * @param node The Node to animate.
     */
    public void playHitEffect(Node node) {
        // Tremble
        Timeline tremble = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), -2)),
            new KeyFrame(Duration.millis(40), new KeyValue(node.translateXProperty(), 2)),
            new KeyFrame(Duration.millis(80), new KeyValue(node.translateXProperty(), -1)),
            new KeyFrame(Duration.millis(120), new KeyValue(node.translateXProperty(), 0))
        );
        // Flash red
        Timeline colorFlash = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.styleProperty(), "-fx-effect: dropshadow(gaussian, red, 8, 0.7, 0, 0);")),
            new KeyFrame(Duration.millis(100), new KeyValue(node.styleProperty(), ""))
        );
        tremble.play();
        colorFlash.play();
    }

    /**
     * Creates a lightning flash effect on the background
     * @param root The root AnchorPane containing the background
     */
    public void playLightningEffect(AnchorPane root) {
        Rectangle flash = new Rectangle();
        flash.setFill(Color.WHITE);
        flash.widthProperty().bind(root.widthProperty());
        flash.heightProperty().bind(root.heightProperty());
        flash.setOpacity(0);
        
        root.getChildren().add(flash);
        root.getChildren().get(root.getChildren().size()-1).toFront();

        Timeline lightning = new Timeline(
            // First quick flash
            new KeyFrame(Duration.ZERO, new KeyValue(flash.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(50), new KeyValue(flash.opacityProperty(), 0.8)),
            new KeyFrame(Duration.millis(100), new KeyValue(flash.opacityProperty(), 0.2)),
            // Second stronger flash
            new KeyFrame(Duration.millis(150), new KeyValue(flash.opacityProperty(), 0.9)),
            new KeyFrame(Duration.millis(200), new KeyValue(flash.opacityProperty(), 0.3)),
            // Final fade out
            new KeyFrame(Duration.millis(250), new KeyValue(flash.opacityProperty(), 0.7)),
            new KeyFrame(Duration.millis(300), new KeyValue(flash.opacityProperty(), 0))
        );

        lightning.setOnFinished(e -> root.getChildren().remove(flash));
        lightning.play();
    }

    /**
     * Animates a card being moved from one position to another with a glitchy effect
     * @param card The ImageView representing the card
     * @param targetX The target X coordinate
     * @param targetY The target Y coordinate
     * @param onFinished Callback to run when animation completes
     */
    public void animateCardMove(ImageView card, double targetX, double targetY, Runnable onFinished) {
        double startX = card.getLayoutX();
        double startY = card.getLayoutY();
        double distance = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));
        double duration = Math.min(600, Math.max(300, distance * 2)); // Duration based on distance

        // Create glitch effect during movement
        Timeline glitchMove = new Timeline();
        int steps = (int)(duration / 50); // One glitch every 50ms
        
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            double currentX = startX + (targetX - startX) * progress;
            double currentY = startY + (targetY - startY) * progress;
            
            // Add random offset for glitch effect
            double glitchX = (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 5;
            double glitchY = (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 5;
            
            glitchMove.getKeyFrames().add(
                new KeyFrame(Duration.millis(progress * duration),
                    new KeyValue(card.layoutXProperty(), currentX + glitchX),
                    new KeyValue(card.layoutYProperty(), currentY + glitchY),
                    new KeyValue(card.rotateProperty(), random.nextDouble() * 4 - 2) // Slight rotation
                )
            );
        }

        // Final position
        glitchMove.getKeyFrames().add(
            new KeyFrame(Duration.millis(duration),
                new KeyValue(card.layoutXProperty(), targetX),
                new KeyValue(card.layoutYProperty(), targetY),
                new KeyValue(card.rotateProperty(), 0)
            )
        );

        glitchMove.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });
        glitchMove.play();
    }

    /**
     * Animates dealing cards with a glitchy effect
     * @param cards List of card ImageViews to animate
     * @param targetPositions List of target positions (x,y coordinates)
     * @param onFinished Callback to run when all cards are dealt
     */
    public void animateCardDeal(List<ImageView> cards, List<double[]> targetPositions, Runnable onFinished) {
        if (cards.size() != targetPositions.size()) {
            throw new IllegalArgumentException("Cards and positions lists must be the same size");
        }

        SequentialTransition dealSequence = new SequentialTransition();
        
        for (int i = 0; i < cards.size(); i++) {
            ImageView card = cards.get(i);
            double[] target = targetPositions.get(i);
            
            // Create parallel animation for each card (movement + effects)
            ParallelTransition cardAnim = new ParallelTransition();
            
            // Movement with slight arc
            double startX = card.getLayoutX();
            double startY = card.getLayoutY();
            double endX = target[0];
            double endY = target[1];
            
            Timeline movement = new Timeline();
            int steps = 10;
            
            for (int step = 0; step <= steps; step++) {
                double progress = (double) step / steps;
                double currentX = startX + (endX - startX) * progress;
                double currentY = startY + (endY - startY) * progress;
                
                // Add arc effect and random glitch
                double arcHeight = -50 * Math.sin(Math.PI * progress);
                double glitchX = random.nextDouble() * 4 - 2;
                double glitchY = random.nextDouble() * 4 - 2;
                
                movement.getKeyFrames().add(
                    new KeyFrame(Duration.millis(progress * 300),
                        new KeyValue(card.layoutXProperty(), currentX + glitchX),
                        new KeyValue(card.layoutYProperty(), currentY + arcHeight + glitchY),
                        new KeyValue(card.rotateProperty(), random.nextDouble() * 4 - 2)
                    )
                );
            }
            
            // Final position
            movement.getKeyFrames().add(
                new KeyFrame(Duration.millis(300),
                    new KeyValue(card.layoutXProperty(), endX),
                    new KeyValue(card.layoutYProperty(), endY),
                    new KeyValue(card.rotateProperty(), 0)
                )
            );
            
            cardAnim.getChildren().add(movement);
            dealSequence.getChildren().add(cardAnim);
        }
        
        dealSequence.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });
        dealSequence.play();
    }

    /**
     * Creates an epic screen shake effect that gradually reduces in intensity
     * @param root The root AnchorPane to shake
     * @param duration Total duration of the shake effect in milliseconds
     * @param intensity Maximum shake intensity
     */
    public void playEpicScreenShake(AnchorPane root, double duration, double intensity) {
        Timeline shakeTimeline = new Timeline();
        int steps = (int)(duration / 30); // Shake every 30ms
        
        for (int i = 0; i < steps; i++) {
            double progress = (double) i / steps;
            double currentIntensity = intensity * (1.0 - progress); // Fade out intensity
            
            double shakeX = (random.nextBoolean() ? 1 : -1) * random.nextDouble() * currentIntensity;
            double shakeY = (random.nextBoolean() ? 1 : -1) * random.nextDouble() * currentIntensity * 0.6;
            
            shakeTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * 30),
                    new KeyValue(root.translateXProperty(), shakeX),
                    new KeyValue(root.translateYProperty(), shakeY)
                )
            );
        }
        
        // Return to center
        shakeTimeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(duration),
                new KeyValue(root.translateXProperty(), 0),
                new KeyValue(root.translateYProperty(), 0)
            )
        );
        
        shakeTimeline.play();
    }

    /**
     * Creates a dramatic flash effect with color tinting
     * @param root The root AnchorPane
     * @param flashColor The color of the flash
     * @param duration Duration of the flash effect
     */
    public void playDramaticFlash(AnchorPane root, Color flashColor, double duration) {
        Rectangle flashOverlay = new Rectangle(1280, 720, flashColor);
        flashOverlay.setOpacity(0);
        root.getChildren().add(flashOverlay);
        
        Timeline flashEffect = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(flashOverlay.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(duration * 0.1),
                new KeyValue(flashOverlay.opacityProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(duration * 0.3),
                new KeyValue(flashOverlay.opacityProperty(), 0.4)
            ),
            new KeyFrame(Duration.millis(duration),
                new KeyValue(flashOverlay.opacityProperty(), 0)
            )
        );
        
        flashEffect.setOnFinished(e -> root.getChildren().remove(flashOverlay));
        flashEffect.play();
    }

    /**
     * Creates an enhanced glitch effect for dramatic moments
     * @param node The node to glitch
     * @param intensity Intensity of the glitch (1.0 = normal, 2.0 = double intensity)
     * @param duration Duration of the glitch effect
     */
    public void playEnhancedGlitchEffect(Node node, double intensity, double duration) {
        Timeline glitchEffect = new Timeline();
        int glitchSteps = (int)(duration / 40); // Glitch every 40ms
        
        // Store original values
        double origX = node.getTranslateX();
        double origY = node.getTranslateY();
        double origScaleX = node.getScaleX();
        double origScaleY = node.getScaleY();
        double origRotate = node.getRotate();
        
        for (int i = 0; i < glitchSteps; i++) {
            double glitchX = origX + (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 10 * intensity;
            double glitchY = origY + (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 8 * intensity;
            double glitchScaleX = origScaleX + (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 0.1 * intensity;
            double glitchScaleY = origScaleY + (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 0.1 * intensity;
            double glitchRotate = origRotate + (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 5 * intensity;
            
            glitchEffect.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * 40),
                    new KeyValue(node.translateXProperty(), glitchX),
                    new KeyValue(node.translateYProperty(), glitchY),
                    new KeyValue(node.scaleXProperty(), glitchScaleX),
                    new KeyValue(node.scaleYProperty(), glitchScaleY),
                    new KeyValue(node.rotateProperty(), glitchRotate)
                )
            );
        }
        
        // Restore original values
        glitchEffect.getKeyFrames().add(
            new KeyFrame(Duration.millis(duration),
                new KeyValue(node.translateXProperty(), origX),
                new KeyValue(node.translateYProperty(), origY),
                new KeyValue(node.scaleXProperty(), origScaleX),
                new KeyValue(node.scaleYProperty(), origScaleY),
                new KeyValue(node.rotateProperty(), origRotate)
            )
        );
        
        glitchEffect.play();
    }

    /**
     * Creates a red glitch effect similar to LoginController transitions
     * @param node The node to apply the red glitch effect to
     * @param duration Duration of the effect in milliseconds
     */
    public void playRedGlitchEffect(Node node, double duration) {
        Timeline redGlitch = new Timeline(
            new KeyFrame(Duration.ZERO, ev -> {
                playHitEffect(node);
                node.setOpacity(0.2 + random.nextDouble() * 0.2);
                node.setTranslateX(random.nextBoolean() ? random.nextInt(6) : -random.nextInt(6));
            }),
            new KeyFrame(Duration.millis(duration * 0.2), ev -> {
                playHitEffect(node);
                node.setOpacity(0.5 + random.nextDouble() * 0.2);
                node.setTranslateX(random.nextBoolean() ? random.nextInt(8) : -random.nextInt(8));
            }),
            new KeyFrame(Duration.millis(duration * 0.4), ev -> {
                playHitEffect(node);
                node.setOpacity(0.3 + random.nextDouble() * 0.2);
                node.setTranslateX(random.nextBoolean() ? random.nextInt(5) : -random.nextInt(5));
            }),
            new KeyFrame(Duration.millis(duration * 0.6), ev -> {
                playHitEffect(node);
                node.setOpacity(0.7 + random.nextDouble() * 0.2);
                node.setTranslateX(random.nextBoolean() ? random.nextInt(4) : -random.nextInt(4));
            }),
            new KeyFrame(Duration.millis(duration), ev -> {
                playHitEffect(node);
                node.setOpacity(1.0);
                node.setTranslateX(0);
            })
        );
        redGlitch.play();
    }

    /**
     * Creates neon blue particles that emanate from a source position
     * @param parentPane The container to add particles to
     * @param sourceX Source X position
     * @param sourceY Source Y position
     * @param particleCount Number of particles to create
     */
    public void createNeonBlueParticles(Pane parentPane, double sourceX, double sourceY, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            // Create neon blue rectangle particles
            Rectangle particle = new Rectangle(4, 4, Color.LIGHTCYAN);
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(1.0);
            particle.setEffect(glow);
            
            particle.setLayoutX(sourceX);
            particle.setLayoutY(sourceY);
            
            // Random direction and distance
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 40 + random.nextDouble() * 60;
            double targetX = sourceX + Math.cos(angle) * distance;
            double targetY = sourceY + Math.sin(angle) * distance;
            
            parentPane.getChildren().add(particle);
            
            Timeline particleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(particle.layoutXProperty(), sourceX),
                    new KeyValue(particle.layoutYProperty(), sourceY),
                    new KeyValue(particle.opacityProperty(), 1.0),
                    new KeyValue(particle.scaleXProperty(), 1.0),
                    new KeyValue(particle.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(400 + random.nextInt(200)),
                    new KeyValue(particle.layoutXProperty(), targetX),
                    new KeyValue(particle.layoutYProperty(), targetY),
                    new KeyValue(particle.opacityProperty(), 0.0),
                    new KeyValue(particle.scaleXProperty(), 0.1),
                    new KeyValue(particle.scaleYProperty(), 0.1)
                )
            );
            
            particleAnim.setOnFinished(e -> parentPane.getChildren().remove(particle));
            particleAnim.play();
        }
    }
    
    // ====================== SANS BATTLE ATTACK PATTERNS ======================
    
    /**
     * Creates vertical projectiles attack pattern
     */
    public void createVerticalAttack(Pane battleArea, double difficultyMultiplier, List<Rectangle> activeProjectiles, 
                                   List<Timeline> projectileTimelines, Random battleRandom) {
        int projectileCount = (int)(3 * difficultyMultiplier); // Base 3, scales to 4-5
        double speed = 180.0 * difficultyMultiplier * 1.2;
        
        // Create waves of projectiles
        for (int wave = 0; wave < 2; wave++) {
            for (int i = 0; i < projectileCount; i++) {
                final int delayIndex = i + (wave * projectileCount);
                Timeline delay = new Timeline(
                    new KeyFrame(Duration.millis(delayIndex * 300 + wave * 1200), 
                        e -> createVerticalProjectile(battleArea, speed, activeProjectiles, projectileTimelines, battleRandom))
                );
                delay.play();
            }
        }
    }
    
    /**
     * Creates horizontal projectiles attack pattern
     */
    public void createHorizontalAttack(Pane battleArea, double difficultyMultiplier, List<Rectangle> activeProjectiles, 
                                     List<Timeline> projectileTimelines, Random battleRandom) {
        int projectileCount = (int)(3 * difficultyMultiplier); // Base 3, scales to 4-5
        double speed = 180.0 * difficultyMultiplier * 1.3;
        
        // Alternating sides attack pattern
        for (int i = 0; i < projectileCount * 2; i++) {
            final int delayIndex = i;
            Timeline delay = new Timeline(
                new KeyFrame(Duration.millis(delayIndex * 400), 
                    e -> createHorizontalProjectile(battleArea, speed, activeProjectiles, projectileTimelines, battleRandom))
            );
            delay.play();
        }
    }
    
    /**
     * Creates spiral projectiles attack pattern
     */
    public void createSpiralAttack(Pane battleArea, double difficultyMultiplier, List<Rectangle> activeProjectiles, 
                                 List<Timeline> projectileTimelines, Random battleRandom) {
        int projectileCount = (int)(3 * difficultyMultiplier); // Base 3, scales to 4-5
        double speed = 180.0 * difficultyMultiplier * 1.1;
        
        for (int i = 0; i < projectileCount; i++) {
            final int delayIndex = i;
            Timeline delay = new Timeline(
                new KeyFrame(Duration.millis(delayIndex * 500), 
                    e -> createSpiralProjectile(battleArea, speed, delayIndex, activeProjectiles, projectileTimelines, battleRandom))
            );
            delay.play();
        }
    }
    
    /**
     * Creates directional pillar attack pattern
     */
    public void createPillarAttack(Pane battleArea, double difficultyMultiplier, List<Rectangle> activeProjectiles, 
                                 List<Timeline> projectileTimelines, Random battleRandom) {
        createDirectionalPillar(battleArea, difficultyMultiplier, activeProjectiles, projectileTimelines, battleRandom);
    }
    
    // ====================== INDIVIDUAL PROJECTILE CREATORS ======================
    
    /**
     * Creates a single vertical projectile
     */
    private void createVerticalProjectile(Pane battleArea, double speed, List<Rectangle> activeProjectiles, 
                                        List<Timeline> projectileTimelines, Random battleRandom) {
        Rectangle projectile = new Rectangle(10, 20, Color.CYAN);
        projectile.setLayoutX(battleRandom.nextDouble() * (battleArea.getPrefWidth() - 10));
        projectile.setLayoutY(-20);
        
        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
        projectile.setEffect(glow);
        
        battleArea.getChildren().add(projectile);
        activeProjectiles.add(projectile);
        
        Timeline movement = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(projectile.layoutYProperty(), -20)),
            new KeyFrame(Duration.millis(2500), new KeyValue(projectile.layoutYProperty(), battleArea.getPrefHeight() + 20))
        );
        
        movement.setOnFinished(e -> {
            battleArea.getChildren().remove(projectile);
            activeProjectiles.remove(projectile);
        });
        
        projectileTimelines.add(movement);
        movement.play();
    }
    
    /**
     * Creates a single horizontal projectile
     */
    private void createHorizontalProjectile(Pane battleArea, double speed, List<Rectangle> activeProjectiles, 
                                          List<Timeline> projectileTimelines, Random battleRandom) {
        Rectangle projectile = new Rectangle(20, 10, Color.LIGHTCYAN);
        boolean fromLeft = battleRandom.nextBoolean();
        
        projectile.setLayoutX(fromLeft ? -20 : battleArea.getPrefWidth() + 20);
        projectile.setLayoutY(battleRandom.nextDouble() * (battleArea.getPrefHeight() - 10));
        
        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
        projectile.setEffect(glow);
        
        battleArea.getChildren().add(projectile);
        activeProjectiles.add(projectile);
        
        Timeline movement = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(projectile.layoutXProperty(), projectile.getLayoutX())),
            new KeyFrame(Duration.millis(2200), new KeyValue(projectile.layoutXProperty(), 
                fromLeft ? battleArea.getPrefWidth() + 20 : -20))
        );
        
        movement.setOnFinished(e -> {
            battleArea.getChildren().remove(projectile);
            activeProjectiles.remove(projectile);
        });
        
        projectileTimelines.add(movement);
        movement.play();
    }
    
    /**
     * Creates a single spiral projectile
     */
    private void createSpiralProjectile(Pane battleArea, double speed, int index, List<Rectangle> activeProjectiles, 
                                      List<Timeline> projectileTimelines, Random battleRandom) {
        Rectangle projectile = new Rectangle(8, 8, Color.YELLOW);
        
        double centerX = battleArea.getPrefWidth() / 2;
        double centerY = battleArea.getPrefHeight() / 2;
        
        double startAngle = (index * Math.PI / 2);
        double startRadius = 120;
        double startX = centerX + Math.cos(startAngle) * startRadius;
        double startY = centerY + Math.sin(startAngle) * startRadius;
        
        projectile.setLayoutX(startX);
        projectile.setLayoutY(startY);
        
        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(1.0);
        projectile.setEffect(glow);
        
        battleArea.getChildren().add(projectile);
        activeProjectiles.add(projectile);
        
        Timeline movement = new Timeline();
        int steps = 60;
        
        for (int i = 0; i <= steps; i++) {
            final int step = i;
            double progress = (double) step / steps;
            double angle = startAngle + progress * Math.PI * 3;
            double radius = startRadius * (1.0 - progress * 0.7);
            
            double x = centerX + Math.cos(angle) * radius;
            double y = centerY + Math.sin(angle) * radius;
            
            movement.getKeyFrames().add(
                new KeyFrame(Duration.millis(step * 40),
                    new KeyValue(projectile.layoutXProperty(), x),
                    new KeyValue(projectile.layoutYProperty(), y))
            );
        }
        
        movement.setOnFinished(e -> {
            battleArea.getChildren().remove(projectile);
            activeProjectiles.remove(projectile);
        });
        
        projectileTimelines.add(movement);
        movement.play();
    }
    
    /**
     * Creates a directional pillar
     */
    private void createDirectionalPillar(Pane battleArea, double difficultyMultiplier, List<Rectangle> activeProjectiles, 
                                       List<Timeline> projectileTimelines, Random battleRandom) {
        int direction = battleRandom.nextInt(4);
        
        Rectangle pillar;
        double startX, startY, endX, endY;
        double speed = 2000 + (1000 / difficultyMultiplier);
        
        switch (direction) {
            case 0: // Top to bottom
                pillar = new Rectangle(20, battleArea.getPrefHeight() + 40, Color.WHITE);
                startX = battleArea.getPrefWidth() * 0.3 + battleRandom.nextDouble() * battleArea.getPrefWidth() * 0.4;
                startY = -battleArea.getPrefHeight() - 20;
                endX = startX;
                endY = battleArea.getPrefHeight() + 20;
                break;
            case 1: // Bottom to top
                pillar = new Rectangle(20, battleArea.getPrefHeight() + 40, Color.WHITE);
                startX = battleArea.getPrefWidth() * 0.3 + battleRandom.nextDouble() * battleArea.getPrefWidth() * 0.4;
                startY = battleArea.getPrefHeight() + 20;
                endX = startX;
                endY = -battleArea.getPrefHeight() - 20;
                break;
            case 2: // Left to right
                pillar = new Rectangle(battleArea.getPrefWidth() + 40, 20, Color.WHITE);
                startX = -battleArea.getPrefWidth() - 20;
                startY = battleArea.getPrefHeight() * 0.3 + battleRandom.nextDouble() * battleArea.getPrefHeight() * 0.4;
                endX = battleArea.getPrefWidth() + 20;
                endY = startY;
                break;
            default: // Right to left
                pillar = new Rectangle(battleArea.getPrefWidth() + 40, 20, Color.WHITE);
                startX = battleArea.getPrefWidth() + 20;
                startY = battleArea.getPrefHeight() * 0.3 + battleRandom.nextDouble() * battleArea.getPrefHeight() * 0.4;
                endX = -battleArea.getPrefWidth() - 20;
                endY = startY;
                break;
        }
        
        pillar.setLayoutX(startX);
        pillar.setLayoutY(startY);
        pillar.setStroke(Color.WHITE);
        pillar.setStrokeWidth(2);
        pillar.setFill(Color.WHITE);
        
        battleArea.getChildren().add(pillar);
        activeProjectiles.add(pillar);
        
        Timeline movement = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(pillar.layoutXProperty(), startX),
                new KeyValue(pillar.layoutYProperty(), startY)),
            new KeyFrame(Duration.millis(speed),
                new KeyValue(pillar.layoutXProperty(), endX),
                new KeyValue(pillar.layoutYProperty(), endY))
        );
        
        movement.setOnFinished(e -> {
            battleArea.getChildren().remove(pillar);
            activeProjectiles.remove(pillar);
        });
        
        projectileTimelines.add(movement);
        movement.play();
    }
}