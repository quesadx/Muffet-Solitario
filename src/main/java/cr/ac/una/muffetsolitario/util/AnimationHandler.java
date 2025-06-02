package cr.ac.una.muffetsolitario.util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class AnimationHandler {

    private static AnimationHandler INSTANCE;

    private final Map<Node, Timeline> levitationTimelines = new HashMap<>();
    private final Map<Node, Timeline> glitchTimelines = new HashMap<>();
    private final Map<ImageView, Timeline> heartTimelines = new HashMap<>();
    private final Random random = new Random();

    private double mouseX = -1000, mouseY = -1000; // Offscreen by default




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
     * Performs a multi-stage "crazy" exit animation on the center card:
     * 1. Earthquake (shake)
     * 2. Spin
     * 3. Zoom in
     * 4. Rip apart (split into left/right halves and move away)
     * After the animation, runs the provided callback (e.g., to show login).
     */
    
    public void animateCenterCardCrazyExit(ImageView centerCard, Runnable onFinished, Pane parentPane) {
    // 1. Earthquake (shake)
    Timeline earthquake = new Timeline();
    int shakes = 18;
    double shakeAmplitude = 10;
    int interval = 30;
    for (int i = 0; i < shakes; i++) {
        double offset = (i % 2 == 0 ? shakeAmplitude : -shakeAmplitude);
        earthquake.getKeyFrames().add(
            new KeyFrame(Duration.millis(i * interval),
                new KeyValue(centerCard.translateXProperty(), offset)
            )
        );
    }
    earthquake.getKeyFrames().add(
        new KeyFrame(Duration.millis(shakes * interval),
            new KeyValue(centerCard.translateXProperty(), 0)
        )
    );

    // 2. Slow double spin (720°)
    Timeline slowSpin = new Timeline(
        new KeyFrame(Duration.ZERO,
            new KeyValue(centerCard.rotateProperty(), 0)
        ),
        new KeyFrame(Duration.millis(500), 
            new KeyValue(centerCard.rotateProperty(), 720)
        )
    );

    // 3. Spin + Zoom (as before)
    Timeline spinZoom = new Timeline(
        new KeyFrame(Duration.ZERO,
            new KeyValue(centerCard.rotateProperty(), 0),
            new KeyValue(centerCard.scaleXProperty(), 1.0),
            new KeyValue(centerCard.scaleYProperty(), 1.0)
        ),
        new KeyFrame(Duration.millis(800),
            new KeyValue(centerCard.rotateProperty(), 1080), // 3 more spins (1080°)
            new KeyValue(centerCard.scaleXProperty(), 2.5),
            new KeyValue(centerCard.scaleYProperty(), 2.5)
        )
    );

    // 4. Rip apart (split into left/right halves and move away)
    spinZoom.setOnFinished(e -> {
        double w = centerCard.getBoundsInParent().getWidth();
        double h = centerCard.getBoundsInParent().getHeight();

        // Create left and right halves
        ImageView leftHalf = new ImageView(centerCard.getImage());
        leftHalf.setFitWidth(centerCard.getFitWidth());
        leftHalf.setFitHeight(centerCard.getFitHeight());
        leftHalf.setPreserveRatio(centerCard.isPreserveRatio());
        leftHalf.setSmooth(centerCard.isSmooth());
        Rectangle leftClip = new Rectangle(0, 0, w / 2, h);
        leftHalf.setClip(leftClip);

        ImageView rightHalf = new ImageView(centerCard.getImage());
        rightHalf.setFitWidth(centerCard.getFitWidth());
        rightHalf.setFitHeight(centerCard.getFitHeight());
        rightHalf.setPreserveRatio(centerCard.isPreserveRatio());
        rightHalf.setSmooth(centerCard.isSmooth());
        Rectangle rightClip = new Rectangle(w / 2, 0, w / 2, h);
        rightHalf.setClip(rightClip);

        // Position halves
        leftHalf.setLayoutX(centerCard.getLayoutX());
        leftHalf.setLayoutY(centerCard.getLayoutY());
        rightHalf.setLayoutX(centerCard.getLayoutX());
        rightHalf.setLayoutY(centerCard.getLayoutY());

        // Add to parent
        parentPane.getChildren().addAll(leftHalf, rightHalf);

        // Hide original
        centerCard.setVisible(false);

        // Animate halves moving apart
        Timeline rip = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(leftHalf.translateXProperty(), 0),
                new KeyValue(rightHalf.translateXProperty(), 0),
                new KeyValue(leftHalf.opacityProperty(), 1.0),
                new KeyValue(rightHalf.opacityProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(leftHalf.translateXProperty(), -w * 1.2),
                new KeyValue(rightHalf.translateXProperty(), w * 1.2),
                new KeyValue(leftHalf.opacityProperty(), 0.0),
                new KeyValue(rightHalf.opacityProperty(), 0.0)
            )
        );
        rip.setOnFinished(ev -> {
            parentPane.getChildren().removeAll(leftHalf, rightHalf);
            if (onFinished != null) onFinished.run();
        });
        rip.play();
    });

    // Play sequence: earthquake -> slowSpin -> spinZoom
    earthquake.setOnFinished(e -> slowSpin.play());
    slowSpin.setOnFinished(e -> spinZoom.play());
    earthquake.play();
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
     * Makes a heart tremble and flash red as if hit, then returns to normal.
     * @param heart The ImageView representing the heart.
     */
    public void playHeartHitEffect(ImageView heart) {
        // Tremble animation (quick shake)
        Timeline tremble = new Timeline();
        int shakes = 8;
        double amplitude = 8;
        int interval = 24;
        for (int i = 0; i < shakes; i++) {
            double offset = (i % 2 == 0 ? amplitude : -amplitude);
            tremble.getKeyFrames().add(
                new KeyFrame(Duration.millis(i * interval),
                    new KeyValue(heart.translateXProperty(), offset)
                )
            );
        }
        tremble.getKeyFrames().add(
            new KeyFrame(Duration.millis(shakes * interval),
                new KeyValue(heart.translateXProperty(), 0)
            )
        );

        // Color effect: flash red using ColorAdjust and Blend
        javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
        javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
        blend.setTopInput(colorAdjust);

        // Save previous effect to restore
        javafx.scene.effect.Effect prevEffect = heart.getEffect();

        // Animate color to red and back
        Timeline colorFlash = new Timeline(
            new KeyFrame(Duration.ZERO, evt -> {
                colorAdjust.setHue(-0.5); // Red tint
                colorAdjust.setSaturation(1.0);
                colorAdjust.setBrightness(0.2);
                heart.setEffect(blend);
            }),
            new KeyFrame(Duration.millis(shakes * interval / 2), evt -> {
                colorAdjust.setHue(0);
                colorAdjust.setSaturation(0);
                colorAdjust.setBrightness(0);
                heart.setEffect(prevEffect);
            })
        );

        // Play both animations
        tremble.play();
        colorFlash.play();
    }

     /**
     * Glitchy fade-in for all children of a Pane (VBox, HBox, etc).
     * Each child starts invisible, glitches in, and then becomes fully visible.
     * @param pane The parent container whose children will be animated.
     */
    public void glitchyFadeInChildren(Pane pane) {
        List<Node> children = new ArrayList<>(pane.getChildren());
        ParallelTransition glitchFadeIn = new ParallelTransition();

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            child.setOpacity(0.0);

            // Optionally, randomize the order for a more chaotic effect
            int delay = i * 60 + random.nextInt(40);

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
}