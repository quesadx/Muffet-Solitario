package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;

import cr.ac.una.muffetsolitario.model.*;
import cr.ac.una.muffetsolitario.service.GameService;
import cr.ac.una.muffetsolitario.util.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.geometry.Point2D;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameController extends Controller implements Initializable {

    private GameDto currentGameDto;
    private GameLogic gameLogic;
    private List<Pane> columns;
    private List<Pane> sequencePanes;
    private final AnimationHandler animationHandler = AnimationHandler.getInstance();
    private final SoundUtils soundUtils = SoundUtils.getInstance();

    // Drag & drop variables
    private List<CardContainer> draggedSequence = null;
    private double[] dragOffsetsX, dragOffsetsY;
    private int fromColIdx = -1, fromCardIdx = -1;
    private static final double CARD_OFFSET = 25;
    
    // Fixed card dimensions for consistent sizing across all assets
    private static final double CARD_WIDTH = CardContainer.CARD_WIDTH;
    private static final double CARD_HEIGHT = CardContainer.CARD_HEIGHT;
    
    private int elapsedSeconds = 0;

    // Lightning effect timer
    private Timeline lightningTimer;
    private Timeline gameTimer;

    // Battle system variables
    private boolean battleActive = false;
    private boolean isRecovering = false; // New variable to track recovery state
    private Timeline battleTimer;
    private Timeline heartMovementTimer;
    private List<Rectangle> activeProjectiles = new ArrayList<>();
    private List<Timeline> projectileTimelines = new ArrayList<>();
    private int currentAttackPhase = 0;
    private int maxAttackPhases = 5; // Battle ends after 5 attacks
    private int playerLives = 2; // Will be set based on difficulty
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private static final double HEART_SPEED = 180.0; // pixels per second
    private Random battleRandom = new Random();
    private Set<Double> usedPillarPositions = new HashSet<>();
    
    // Dynamic battle area sizing variables
    private static final double DEFAULT_BATTLE_AREA_WIDTH = 200.0;
    private static final double DEFAULT_BATTLE_AREA_HEIGHT = 200.0;
    private static final double RECTANGULAR_BATTLE_AREA_WIDTH = 280.0;
    private static final double RECTANGULAR_BATTLE_AREA_HEIGHT = 160.0;
    private Timeline battleAreaResizeTimeline;
    
    // Battle trigger callback
    public interface SequenceCompletionCallback {
        void onSequenceCompleted(int columnIndex, List<CardContainer> completedSequence);
    }

    @FXML
    private Pane pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
            pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen, pnBattleArea;
    @FXML
    private AnchorPane root;
    @FXML
    private Pane pnDeck, pnSequence1, pnSequence2, pnSequence3, pnSequence4,
            pnSequence5, pnSequence6, pnSequence7, pnSequence8;
    @FXML
    private Label lbDifficulty, lbUserPoints, lbTime;
    @FXML
    private MFXButton btnUndo;
    @FXML
    private VBox vboxAlert, vboxBattle, vboxGameFinished;
    @FXML
    private Label lblAlertMessage, lblLifesRemaining;
    @FXML private ImageView imgSans, imgBattleHeart;

    GameRuleValidator gameRuleValidator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FlowController.getInstance().limpiarLoader("PreGameView");
        columns = List.of(pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
                pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen);
        sequencePanes = List.of(pnSequence1, pnSequence2, pnSequence3, pnSequence4,
                pnSequence5, pnSequence6, pnSequence7, pnSequence8);
        gameRuleValidator = new GameRuleValidator();
        pnDeck.setOnMouseClicked(event -> {
            try {
                soundUtils.playAttackSound();
                gameLogic.dealFromDeck();
                updateGameInfo();
                List<BoardColumnDto> columns = currentGameDto.getBoardColumnList();
                for (int i = 0; i < columns.size(); i++) {
                    BoardColumnDto column = columns.get(i);
                    if (!column.getCardList().isEmpty()) {
                        CardContainer topCard = column.getCardList().get(column.getCardList().size() - 1);
                        Point2D deckPos = pnDeck.localToScene(60, 80);
                        Point2D columnPos = this.columns.get(i).localToScene(60, 400);
                        Point2D rootDeckPos = root.sceneToLocal(deckPos);
                        Point2D rootColumnPos = root.sceneToLocal(columnPos);
                        DropShadow shadow = new DropShadow();
                        shadow.setColor(Color.rgb(0, 0, 0, 0.50));
                        shadow.setRadius(20);
                        shadow.setSpread(0.4);
                        topCard.setEffect(shadow);
                        topCard.setLayoutX(rootDeckPos.getX());
                        topCard.setLayoutY(rootDeckPos.getY());
                        topCard.setOpacity(1);
                        topCard.setScaleX(0.8);
                        topCard.setScaleY(0.8);
                        if (topCard.getParent() != null) {
                            ((Pane) topCard.getParent()).getChildren().remove(topCard);
                        }
                        root.getChildren().add(topCard);
                        topCard.toFront();
                        topCard.setViewOrder(-1000);
                        final int columnIndex = i;
                        Timeline flyAnimation = new Timeline(
                                new KeyFrame(Duration.millis(columnIndex * 100),
                                        new KeyValue(topCard.layoutXProperty(), rootDeckPos.getX()),
                                        new KeyValue(topCard.layoutYProperty(), rootDeckPos.getY()),
                                        new KeyValue(topCard.scaleXProperty(), 0.8),
                                        new KeyValue(topCard.scaleYProperty(), 0.8)),
                                new KeyFrame(Duration.millis(columnIndex * 100 + 300),
                                        new KeyValue(topCard.layoutXProperty(), rootColumnPos.getX()),
                                        new KeyValue(topCard.layoutYProperty(), rootColumnPos.getY()),
                                        new KeyValue(topCard.scaleXProperty(), 1.1),
                                        new KeyValue(topCard.scaleYProperty(), 1.1)),
                                new KeyFrame(Duration.millis(columnIndex * 100 + 400),
                                        new KeyValue(topCard.scaleXProperty(), 1.0),
                                        new KeyValue(topCard.scaleYProperty(), 1.0)));
                        flyAnimation.setOnFinished(e -> {
                            root.getChildren().remove(topCard);
                            topCard.setViewOrder(0);
                            topCard.setScaleX(1.0);
                            topCard.setScaleY(1.0);
                            updateBoard();
                        });
                        flyAnimation.play();
                    }
                }
            } catch (Exception e) {
                showAlert("No se puede repartir", e.getMessage());
            }
        });
        String difficultySelected = (String) AppContext.getInstance().get("GameDifficulty");
        initializeGame(difficultySelected);
        if (currentGameDto != null) {
            startGame();
            startRandomLightning();
        } else {
            showAlert("Error", "No se pudo inicializar la partida.");
        }
        startRandomLightning();

    }

    @Override
    public void initialize() {
        // Requerido por la herencia, no eliminar.
    }

    @FXML
    void onActionStartBattle(ActionEvent event) {
        startSansBattle();
    }

    public void startGame() {

        if (currentGameDto.isGameLoaded()) {
            gameLogic.sortBoardColumnsByIndex(currentGameDto);
            gameLogic.sortCardsInAllColumns(currentGameDto);
            gameLogic.sortDeckCards(currentGameDto);
            gameLogic.sortCompletedSequences(currentGameDto);
        }

        if (currentGameDto.getBoardColumnList() == null || currentGameDto.getBoardColumnList().isEmpty()) {
            currentGameDto.initializeBoardColumns(10);
            gameLogic.initializeDeck(currentGameDto);
            gameLogic.loadCardsToColumn();
            for (BoardColumnDto col : currentGameDto.getBoardColumnList()) {
                List<CardContainer> cardList = col.getCardList();
                if (!cardList.isEmpty()) {
                    CardContainer topCard = cardList.get(cardList.size() - 1);
                    topCard.getCardDto().setCardFaceUp(true);
                    animateCardFlip(topCard);
                }
            }
        }
        animationHandler.playLightningEffect(root);
        soundUtils.playAttackSound();
        updateBoard();
        startGameTimer();
        updateGameInfo();
        animateGameStart();
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        elapsedSeconds = currentGameDto.getGameDurationSeconds(); // Recupera el tiempo guardado si existe
        updateTimerLabel();
        gameTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    elapsedSeconds++;
                    currentGameDto.setGameDurationSeconds(elapsedSeconds); // <-- Sincroniza el modelo
                    updateTimerLabel();
                }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        lbTime.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateGameInfo() {
        // Mostrar dificultad
        String dificultad = currentGameDto.getGameDifficulty();
        if (dificultad == null) {
            dificultad = "Desconocida";
        }
        lbDifficulty.setText("Dificultad: " + dificultad);

        // Mostrar puntos
        Integer puntos = currentGameDto.getGameTotalPoints();
        if (puntos == null) {
            puntos = 0;
        }
        lbUserPoints.setText("Puntos: " + puntos);
    }

    /**
     * Starts random lightning effects in the background
     */
    private void startRandomLightning() {
        if (lightningTimer != null) {
            lightningTimer.stop();
        }

        lightningTimer = new Timeline();
        lightningTimer.setCycleCount(Timeline.INDEFINITE);

        lightningTimer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), e -> {
                    // Much lower chance for lightning (about 0.5% chance every second)
                    if (Math.random() < 0.005) {
                        animationHandler.playLightningEffect(root);
                    }

                    // Schedule next check with much longer random interval
                    double nextDelay = 20.0 + Math.random() * 40.0; // 20-60 seconds
                    lightningTimer.stop();
                    lightningTimer.getKeyFrames().set(0,
                            new KeyFrame(Duration.seconds(nextDelay), ev -> {
                                if (Math.random() < 0.02) { // 2% chance
                                    animationHandler.playLightningEffect(root);
                                }
                            }));
                    lightningTimer.playFromStart();
                }));
        lightningTimer.play();
    }

    /**
     * Animates the initial card dealing with enhanced effects
     */
    private void animateGameStart() {
        // Simple animation for all cards
        List<CardContainer> allCards = new ArrayList<>();

        // Collect all cards from columns
        for (int i = 0; i < columns.size(); i++) {
            BoardColumnDto boardColumn = currentGameDto.getBoardColumnList().get(i);
            if (boardColumn != null && boardColumn.getCardList() != null) {
                allCards.addAll(boardColumn.getCardList());
            }
        }

        // Animate each card
        for (CardContainer card : allCards) {
            // Set initial position
            card.setOpacity(0);
            card.setScaleX(0.8);
            card.setScaleY(0.8);

            // Create quick fade-in and scale animation
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(Duration.ZERO,
                            new javafx.animation.KeyValue(card.opacityProperty(), 0),
                            new javafx.animation.KeyValue(card.scaleXProperty(), 0.8),
                            new javafx.animation.KeyValue(card.scaleYProperty(), 0.8)),
                    new javafx.animation.KeyFrame(Duration.millis(200),
                            new javafx.animation.KeyValue(card.opacityProperty(), 1),
                            new javafx.animation.KeyValue(card.scaleXProperty(), 1.1),
                            new javafx.animation.KeyValue(card.scaleYProperty(), 1.1)),
                    new javafx.animation.KeyFrame(Duration.millis(300),
                            new javafx.animation.KeyValue(card.scaleXProperty(), 1.0),
                            new javafx.animation.KeyValue(card.scaleYProperty(), 1.0)));
            timeline.play();
        }

        // Play sound effect
        soundUtils.playAttackSound();
    }

    private void initializeGame(String difficultySelected) {
        UserAccountDto userAccountDto = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        if (userAccountDto == null) {
            showAlert("Error", "No hay usuario logueado.");
            return;
        }
        currentGameDto = (GameDto) AppContext.getInstance().get("GameLoaded");

        if (currentGameDto != null) {
            currentGameDto.setGameLoaded(true);
            System.out.println("Partida cargada desde la base de datos.");
        } else {
            System.out.println("No hay partida guardada, se crea una nueva.");
            currentGameDto = new GameDto();
            currentGameDto.setGameLoaded(false);
            currentGameDto.setGameUserFk(userAccountDto.getUserId());
            userAccountDto.setGameId(currentGameDto.getGameId()); // TODO: CHANGE GAMESERVICE TO MAKE THIS WORK
            currentGameDto.setGameDifficulty(difficultySelected);
            currentGameDto
                    .setGameCreatedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            System.out.println("dia de creacion: " + currentGameDto.getGameCreatedDate());
            currentGameDto.setGameTotalPoints(500);
        }
        gameLogic = new GameLogic(currentGameDto);
        
        // Set up battle trigger callback for sequence completion
        gameLogic.setSequenceCompletionCallback(this::onSequenceCompleted);
    }

    private void updateCardImage(CardContainer cardContainer) {
        CardDto cardDto = cardContainer.getCardDto();
        // Obtener el usuario logueado desde el contexto
        UserAccountDto userAccountDto = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        int userDesign = 1; // Valor por defecto
        if (userAccountDto != null) {
            userDesign = userAccountDto.getUserCardDesign();
        }
        String versionFolder = "v" + (userDesign + 1);

        String imagePath;
        if (cardDto.isCardFaceUp()) {
            String suitFolder = cardDto.getCardSuit().equals("C") ? "Corazones"
                    : cardDto.getCardSuit().equals("T") ? "Treboles"
                            : cardDto.getCardSuit().equals("P") ? "Picas"
                                    : "Diamantes";
            imagePath = "/cr/ac/una/muffetsolitario/resources/assets/CardStyles/"
                    + versionFolder + "/" + suitFolder + "/"
                    + cardDto.getCardSuit() + "_" + cardDto.getCardValue() + ".png";
        } else {
            imagePath = "/cr/ac/una/muffetsolitario/resources/assets/CardStyles/"
                    + versionFolder + "/Card_Back1.png";
        }
        try {
            URL resource = getClass().getResource(imagePath);
            cardContainer.setImagePath(resource != null ? resource.toExternalForm() : "");
            cardContainer.setImage(new Image(resource != null ? resource.toExternalForm() : ""));
        } catch (Exception e) {
            System.err.println("Error cargando imagen de carta: " + imagePath);
        }
    }

    private void moveSequenceToRoot(List<CardContainer> sequence) {
        for (CardContainer card : sequence) {
            Point2D scenePos = card.localToScene(0, 0);
            Point2D rootPos = root.sceneToLocal(scenePos);

            // Remove from current parent
            if (card.getParent() != null) {
                ((Pane) card.getParent()).getChildren().remove(card);
            }
            // Add to root with glitch effect
            root.getChildren().add(card);
            card.setLayoutX(rootPos.getX());
            card.setLayoutY(rootPos.getY());
            card.toFront();

            // Add glitch effect when moving
            animationHandler.playHitEffect(card);
        }
        // Play sound effect
        soundUtils.playAttackSound();
    }

    private void moveSequenceToColumn(List<CardContainer> sequence, Pane columnPane) {
        // Calculate correct base position based on existing cards in column
        double baseY = 0;
        for (javafx.scene.Node node : columnPane.getChildren()) {
            if (node instanceof CardContainer) {
                baseY += CARD_OFFSET;
            }
        }

        for (int k = 0; k < sequence.size(); k++) {
            CardContainer card = sequence.get(k);
            if (card.getParent() != null) {
                ((Pane) card.getParent()).getChildren().remove(card);
            }
            root.getChildren().remove(card);

            // Set correct position immediately
            double targetX = 0;
            double targetY = baseY + k * CARD_OFFSET;
            card.setLayoutX(targetX);
            card.setLayoutY(targetY);

            // Add to column if not already there
            if (!columnPane.getChildren().contains(card)) {
                columnPane.getChildren().add(card);
            }
        }

        // Remove excessive lightning effects
        // Only very rarely add lightning
        if (Math.random() < 0.02) { // 2% chance
            animationHandler.playLightningEffect(root);
        }
    }

    private void renderBoard() {
        List<BoardColumnDto> boardColumns = currentGameDto.getBoardColumnList();
        if (boardColumns == null) {
            System.err.println("Error: BoardColumnList is null");
            return;
        }

        int columnsToRender = Math.min(boardColumns.size(), columns.size());
        columns.forEach(pane -> pane.getChildren().clear());

        for (int i = 0; i < columnsToRender; i++) {
            final int columnIdx = i;
            Pane pane = columns.get(i);
            BoardColumnDto boardColumn = boardColumns.get(i);
            List<CardContainer> cards = boardColumn.getCardList();

            if (cards == null || cards.isEmpty()) {
                continue;
            }

            for (int j = 0; j < cards.size(); j++) {
                CardContainer cardContainer = cards.get(j);

                // Remove previous handlers
                cardContainer.setOnMousePressed(null);
                cardContainer.setOnMouseDragged(null);
                cardContainer.setOnMouseReleased(null);

                // Remove from previous parent if needed
                if (cardContainer.getParent() != null) {
                    ((Pane) cardContainer.getParent()).getChildren().remove(cardContainer);
                }

                updateCardImage(cardContainer);
                cardContainer.setFitWidth(120);
                cardContainer.setFitHeight(160);
                cardContainer.setLayoutX(0);
                cardContainer.setPreserveRatio(true);
                cardContainer.setLayoutY(j * CARD_OFFSET);

                if (cardContainer.getCardDto().isCardFaceUp()) {
                    // Construir la secuencia potencial desde esta carta hacia abajo
                    List<CardContainer> cardsInColumn = boardColumn.getCardList();
                    List<CardContainer> potentialSequence = new ArrayList<>();
                    for (int k = j; k < cardsInColumn.size(); k++) {
                        potentialSequence.add(cardsInColumn.get(k));
                    }

                    if (gameRuleValidator.isValidSequence(potentialSequence)) {
                        cardContainer.setDisable(false);
                        cardContainer.setOnMousePressed(event -> {

                            List<BoardColumnDto> boardColumnsActual = currentGameDto.getBoardColumnList();
                            BoardColumnDto boardColumnActual = boardColumnsActual.get(columnIdx);
                            List<CardContainer> cardsInColumnActual = boardColumnActual.getCardList();

                            int idx = -1;
                            for (int k = 0; k < cardsInColumnActual.size(); k++) {
                                if (cardsInColumnActual.get(k) == cardContainer) {
                                    idx = k;
                                    break;
                                }
                            }
                            if (idx == -1) {
                                for (int k = 0; k < cardsInColumnActual.size(); k++) {
                                    if (cardsInColumnActual.get(k).getCardDto().getCardId()
                                            .equals(cardContainer.getCardDto().getCardId())) {
                                        idx = k;
                                        break;
                                    }
                                }
                            }
                            if (idx == -1) {
                                return;
                            }

                            // Crear la secuencia real desde la carta seleccionada hacia abajo
                            List<CardContainer> sequenceToDrag = new ArrayList<>();
                            for (int k = idx; k < cardsInColumnActual.size(); k++) {
                                sequenceToDrag.add(cardsInColumnActual.get(k));
                            }

                            if (!gameRuleValidator.isValidSequence(sequenceToDrag)) {
                                showAlert("Secuencia inválida", "Solo puedes mover secuencias descendentes del mismo palo.");
                                return;
                            }
                            draggedSequence = new ArrayList<>(sequenceToDrag);
                            dragOffsetsX = new double[draggedSequence.size()];
                            dragOffsetsY = new double[draggedSequence.size()];
                            Point2D mouseScene = new Point2D(event.getSceneX(), event.getSceneY());
                            for (int k = 0; k < draggedSequence.size(); k++) {
                                CardContainer c = draggedSequence.get(k);
                                Point2D cardScene = c.localToScene(0, 0);
                                dragOffsetsX[k] = mouseScene.getX() - cardScene.getX();
                                dragOffsetsY[k] = mouseScene.getY() - cardScene.getY();
                            }
                            moveSequenceToRoot(draggedSequence);
                            fromColIdx = columnIdx;
                            fromCardIdx = idx;
                        });

                        cardContainer.setOnMouseDragged(event -> {
                            if (draggedSequence != null) {
                                double mouseX = event.getSceneX(), mouseY = event.getSceneY();

                                int potentialTargetCol = getTargetColumnIndex(mouseX, mouseY);
                                columns.forEach(col -> col.setStyle(""));
                                if (potentialTargetCol != -1) {
                                    columns.get(potentialTargetCol).setStyle(
                                            "-fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-style: dashed;");
                                }

                                for (int k = 0; k < draggedSequence.size(); k++) {
                                    CardContainer c = draggedSequence.get(k);
                                    double wiggleAmount = Math.min((k + 1) * 2.0, 8.0);
                                    double wiggleX = Math.sin(System.currentTimeMillis() * 0.01) * wiggleAmount;
                                    double wiggleY = Math.cos(System.currentTimeMillis() * 0.008) * (wiggleAmount * 0.5);
                                    double reducedOffset = CARD_OFFSET * 0.6;
                                    c.setLayoutX(mouseX - dragOffsetsX[k] + wiggleX);
                                    c.setLayoutY(mouseY - dragOffsetsY[k] + k * reducedOffset + wiggleY);
                                    double rotation = Math.sin(System.currentTimeMillis() * 0.005) * wiggleAmount;
                                    c.setRotate(rotation);
                                    c.toFront();
                                }
                            }
                        });

                        cardContainer.setOnMouseReleased(mouseEvent -> {
                            if (draggedSequence != null) {
                                columns.forEach(col -> col.setStyle(""));
                                for (CardContainer card : draggedSequence) {
                                    card.setRotate(0);
                                }

                                int targetColIdx = getTargetColumnIndex(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                                boolean moved = false;
                                if (targetColIdx != -1) {
                                    try {
                                        handleMoveCards(fromColIdx, targetColIdx, draggedSequence.get(0));
                                        moved = true;
                                        addColumnGlitchEffect(targetColIdx);
                                    } catch (Exception e) {
                                        showAlert("Movimiento inválido", e.getMessage());
                                    }
                                }
                                Pane targetPane = columns.get((moved && targetColIdx != -1) ? targetColIdx : fromColIdx);
                                moveSequenceToColumn(draggedSequence, targetPane);
                                draggedSequence = null;
                                dragOffsetsX = null;
                                dragOffsetsY = null;
                                fromColIdx = -1;
                                fromCardIdx = -1;
                                updateBoard();
                            }
                        });
                    } else {
                        cardContainer.setDisable(true);
                    }
                } else {
                    cardContainer.setDisable(true);
                }

                pane.getChildren().add(cardContainer);
            }
        }
    }

    private int getTargetColumnIndex(double sceneX, double sceneY) {
        for (int i = 0; i < columns.size(); i++) {
            Pane pane = columns.get(i);
            Point2D paneScene = pane.localToScene(0, 0);
            double width = pane.getWidth(), height = pane.getHeight();
            if (sceneX >= paneScene.getX() && sceneX <= paneScene.getX() + width &&
                    sceneY >= paneScene.getY() && sceneY <= paneScene.getY() + height) {
                return i;
            }
        }
        return -1;
    }

    public void updateBoard() {
        renderBoard();
        renderDeck();
        renderCompletedSequences();
    }

    public void handleMoveCards(int fromCol, int toCol, CardContainer cardContainer) {
        try {
            // Store the source column before the move
            BoardColumnDto sourceColumn = currentGameDto.getBoardColumnList().get(fromCol);
            List<CardContainer> sourceCards = new ArrayList<>(sourceColumn.getCardList());

            // Perform the move
            gameLogic.moveCardsBetweenColumns(fromCol, toCol, cardContainer);
            currentGameDto.setGameTotalPoints(currentGameDto.getGameTotalPoints() - 1);
            updateGameInfo();
            // Check if we need to flip a card in the source column
            if (!sourceCards.isEmpty() && sourceCards.size() > 1) {
                CardContainer cardToFlip = sourceCards.get(sourceCards.size() - 2); // Card that was under the moved
                                                                                    // card
                if (!cardToFlip.getCardDto().isCardFaceUp()) {
                    cardToFlip.getCardDto().setCardFaceUp(true);

                    // Delay the flip animation slightly for dramatic effect
                    Timeline flipDelay = new Timeline(
                            new KeyFrame(Duration.millis(300), e -> {
                                animateCardFlip(cardToFlip);
                            }));
                    flipDelay.play();
                }
            }

            // Update board after a short delay to ensure animations complete
            Timeline updateDelay = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> {
                        updateBoard();
                    }));
            updateDelay.play();
        } catch (IllegalArgumentException e) {
            showAlert("Movimiento inválido", e.getMessage());
        }
    }

    private void showAlert(String titulo, String mensaje) {
        // Use Platform.runLater to avoid issues during animation processing
        javafx.application.Platform.runLater(() -> {
            lblAlertMessage.setText(mensaje);
            vboxAlert.setVisible(true);
            vboxAlert.toFront(); // Ensure alert is always on top
            vboxAlert.setOpacity(0);
            vboxAlert.setScaleY(0.7);

            // Create pop-in animation
            Timeline popInTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(vboxAlert.opacityProperty(), 0),
                            new KeyValue(vboxAlert.scaleYProperty(), 0.7)),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(vboxAlert.opacityProperty(), 1),
                            new KeyValue(vboxAlert.scaleYProperty(), 1.1)),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(vboxAlert.scaleYProperty(), 1.0)));

            // Play glitch effect and sound
            animationHandler.playHitEffect(vboxAlert);

            // Small delay before playing sound
            Timeline soundDelay = new Timeline(new KeyFrame(Duration.millis(10), e -> {
                soundUtils.playDialogSound();
            }));

            // Create pop-out animation
            Timeline popOutTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(vboxAlert.opacityProperty(), 1),
                            new KeyValue(vboxAlert.scaleYProperty(), 1)),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(vboxAlert.opacityProperty(), 0),
                            new KeyValue(vboxAlert.scaleYProperty(), 0.7)));

            // Schedule pop-out after 1.5 seconds
            Timeline hideDelay = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                animationHandler.playHitEffect(vboxAlert);
                popOutTimeline.play();
            }));

            // Chain the animations
            popInTimeline.play();
            soundDelay.play();
            hideDelay.play();

            // Clean up
            popOutTimeline.setOnFinished(e -> {
                vboxAlert.setVisible(false);
            });
        });
    }

    private void renderDeck() {
        pnDeck.getChildren().clear();
        DeckDto deckDto = currentGameDto.getDeckDto();
        if (deckDto == null || deckDto.getCardList().isEmpty())
            return;
        List<CardContainer> deckCards = deckDto.getCardList();
        int cantCardsToShow = Math.min(3, deckCards.size());
        double offsetX = 18;

        // Obtener el usuario logueado y su versión de cartas
        UserAccountDto userAccountDto = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        int userDesign = 1; // Valor por defecto
        if (userAccountDto != null) {
            userDesign = userAccountDto.getUserCardDesign(); // Debe retornar 1, 2 o 3
        }
        String versionFolder = "v" + (userDesign+1);

        // Ruta dinámica para el reverso de carta según la versión
        String imagePath = "/cr/ac/una/muffetsolitario/resources/assets/CardStyles/"
                + versionFolder + "/Card_Back1.png";

        for (int i = 0; i < cantCardsToShow; i++) {
            int cardIdx = deckCards.size() - 1 - i;
            CardContainer card = deckCards.get(cardIdx);
            try {
                URL resource = getClass().getResource(imagePath);
                if (resource != null) {
                    card.setImagePath(resource.toExternalForm());
                    card.setImage(new Image(resource.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Error cargando imagen del mazo: " + imagePath);
            }
            card.applyFixedCardSizing();
            card.setLayoutX(i * offsetX);
            card.setLayoutY(0);
            card.setDisable(true);
            pnDeck.getChildren().add(card);
        }
    }

    private void renderCompletedSequences() {
        // Safety check for sequence panes
        if (sequencePanes == null) {
            System.err.println("Warning: sequencePanes is null");
            return;
        }

        // Clear all sequence panes first
        sequencePanes.forEach(pane -> {
            if (pane != null) {
                pane.getChildren().clear();
            }
        });

        // Get completed sequences
        List<CompletedSequenceDto> completedSequences = currentGameDto.getCompletedSequenceList();
        if (completedSequences == null || completedSequences.isEmpty()) {
            return;
        }

        double cardOffset = 30;
        int maxCardsToShow = 3;

        // Render only up to available sequence panes
        int maxSequences = Math.min(completedSequences.size(), sequencePanes.size());
        for (int i = 0; i < maxSequences; i++) {
            Pane pane = sequencePanes.get(i);
            if (pane == null)
                continue;

            List<CardContainer> cards = completedSequences.get(i).getCardList();
            if (cards == null || cards.isEmpty())
                continue;

            // Show only up to maxCardsToShow cards per sequence
            int cardsToShow = Math.min(maxCardsToShow, cards.size());
            for (int j = 0; j < cardsToShow; j++) {
                CardContainer card = cards.get(j);
                if (card == null)
                    continue;

                // Remove from previous parent if needed
                if (card.getParent() != null) {
                    ((Pane) card.getParent()).getChildren().remove(card);
                }

                card.getCardDto().setCardFaceUp(true);
                updateCardImage(card);
                card.applyFixedCardSizing();
                card.setLayoutX(j * cardOffset);
                card.setLayoutY(0);
                card.setDisable(true);
                pane.getChildren().add(card);
            }
        }
    }

    @FXML
    private void onActionBtnUndo(ActionEvent event) {
        // Add sound effect for undo action (no lightning)
        soundUtils.playAttackSound();
        gameLogic.undoLastMove();
        updateGameInfo();
        updateBoard();
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
        UserAccountDto user = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        GameService gameService = new GameService();

        if(!user.isUserGuest()){
            currentGameDto.setGameStatus("SAVED");
            Respuesta respuesta = gameService.saveGameDto(currentGameDto);

            if(respuesta.getEstado()){
                System.out.println(respuesta.getMensaje());
            }
        }
        FlowController.getInstance().limpiarLoader("PreGameController");
        FlowController.getInstance().goView("LogInView");
        FlowController.getInstance().limpiarLoader("GameController");
    }

    /**
     * Animates a card flipping from face down to face up
     */
    private void animateCardFlip(CardContainer card) {
        // Simple scale flip animation
        Timeline flipAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(card.scaleXProperty(), 0.0)));

        // When card is fully scaled down, update the image and scale back up
        flipAnimation.setOnFinished(e -> {
            updateCardImage(card);

            Timeline completeFlip = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(card.scaleXProperty(), 0.0)),
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(card.scaleXProperty(), 1.0)));
            completeFlip.play();
        });

        flipAnimation.play();
    }

    /**
     * Adds glitch effect to a column when a card is successfully placed
     */
    private void addColumnGlitchEffect(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.size()) {
            Pane column = columns.get(columnIndex);
            animationHandler.playHitEffect(column);

            // Add temporary glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.CYAN);
            glow.setRadius(15);
            glow.setSpread(0.6);
            column.setEffect(glow);

            // Remove glow after short time
            Timeline removeGlow = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> {
                        column.setEffect(null);
                    }));
            removeGlow.play();
        }
    }

    /**
     * Checks if a sequence of cards is valid to pick up according to Spider
     * Solitaire rules.
     * Cards must be in descending order and of the same suit.
     */
    private boolean isValidSequenceToPickUp(List<CardContainer> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }

        // Single card is always valid
        if (sequence.size() == 1) {
            return true;
        }

        // Check descending order and same suit
        String suit = sequence.get(0).getCardDto().getCardSuit();
        int prevValue = sequence.get(0).getCardDto().getCardValue();

        // Debug info
        System.out.println("Validating sequence starting with: " + prevValue + suit);

        for (int i = 1; i < sequence.size(); i++) {
            CardDto currentCard = sequence.get(i).getCardDto();
            // Must be same suit and value must be exactly one less than previous
            if (!currentCard.getCardSuit().equals(suit) ||
                    currentCard.getCardValue() != prevValue - 1) {
                System.out.println("Invalid sequence at position " + i + ": expected " + (prevValue - 1) + suit + 
                                 " but got " + currentCard.getCardValue() + currentCard.getCardSuit());
                return false;
            }
            prevValue = currentCard.getCardValue();
        }

        System.out.println("Valid sequence of " + sequence.size() + " cards");
        return true;
    }

    @FXML
    private void onActionBtnHint(ActionEvent event) {
        showAlert("Posible movimiento", gameLogic.suggestPossibleMoves(currentGameDto));
    }

    // ====================== SANS BATTLE SYSTEM ======================

    /**
     * Starts the Sans battle minigame
     */
    private void startSansBattle() {
        if (battleActive) return;
        
        battleActive = true;
        isRecovering = false; // Reset recovery state
        currentAttackPhase = 0;
        
        // Initialize player lives based on difficulty
        String difficulty = currentGameDto.getGameDifficulty();
        playerLives = getLivesForDifficulty(difficulty);
        updateLifeDisplay();
        
        // Debug: Print difficulty and lives for verification
        System.out.println("Battle started - Difficulty: " + difficulty + ", Lives: " + playerLives);
        
        // Clear any previous pillar positions
        usedPillarPositions.clear();
        
        // Show battle UI
        vboxBattle.setVisible(true);
        vboxBattle.toFront();
        
        // Initialize battle area to default square size
        resetBattleAreaSize();
        
        // Initialize heart position and ensure it's fully visible
        double centerX = pnBattleArea.getPrefWidth() / 2 - imgBattleHeart.getFitWidth() / 2;
        double centerY = pnBattleArea.getPrefHeight() / 2 - imgBattleHeart.getFitHeight() / 2;
        imgBattleHeart.setLayoutX(centerX);
        imgBattleHeart.setLayoutY(centerY);
        imgBattleHeart.setOpacity(1.0); // Ensure heart is fully visible at start
        
        // Setup key handling for heart movement
        setupBattleKeyHandling();
        
        // Start heart movement system
        startHeartMovement();
        
        // Play dramatic battle entrance effect instead of shaking
        animationHandler.playDramaticFlash(root, Color.BLUE, 800);
        animationHandler.playRedGlitchEffect(imgSans, 800);
        soundUtils.playAttackSound();
        
        // Add neon blue particles when battle starts
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 25);
        
        // Start first attack after a brief delay
        Timeline startDelay = new Timeline(
            new KeyFrame(Duration.millis(1500), e -> startNextAttack())
        );
        startDelay.play();
    }

    /**
     * Sets up keyboard input handling for the battle
     */
    private void setupBattleKeyHandling() {
        root.setFocusTraversable(true);
        root.requestFocus();
        
        root.setOnKeyPressed(event -> {
            if (!battleActive) return;
            
            KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP ||
                code == KeyCode.A || code == KeyCode.LEFT ||
                code == KeyCode.S || code == KeyCode.DOWN ||
                code == KeyCode.D || code == KeyCode.RIGHT) {
                
                pressedKeys.add(code);
                event.consume();
            }
        });
        
        root.setOnKeyReleased(event -> {
            if (!battleActive) return;
            
            KeyCode code = event.getCode();
            pressedKeys.remove(code);
            event.consume();
        });
    }

    /**
     * Starts the heart movement system (60 FPS)
     */
    private void startHeartMovement() {
        heartMovementTimer = new Timeline(
            new KeyFrame(Duration.millis(16), e -> updateHeartPosition())
        );
        heartMovementTimer.setCycleCount(Timeline.INDEFINITE);
        heartMovementTimer.play();
    }

    /**
     * Updates heart position based on pressed keys
     */
    private void updateHeartPosition() {
        if (!battleActive) return;
        
        double deltaTime = 0.016; // 16ms = ~60 FPS
        double moveDistance = HEART_SPEED * deltaTime;
        
        double currentX = imgBattleHeart.getLayoutX();
        double currentY = imgBattleHeart.getLayoutY();
        double newX = currentX;
        double newY = currentY;
        
        // Calculate movement
        if (pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP)) {
            newY -= moveDistance;
        }
        if (pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN)) {
            newY += moveDistance;
        }
        if (pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT)) {
            newX -= moveDistance;
        }
        if (pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT)) {
            newX += moveDistance;
        }
        
        // Apply boundary constraints (keep heart inside battle area)
        double minX = 5;
        double maxX = pnBattleArea.getPrefWidth() - imgBattleHeart.getFitWidth() - 5;
        double minY = 5;
        double maxY = pnBattleArea.getPrefHeight() - imgBattleHeart.getFitHeight() - 5;
        
        newX = Math.max(minX, Math.min(maxX, newX));
        newY = Math.max(minY, Math.min(maxY, newY));
        
        imgBattleHeart.setLayoutX(newX);
        imgBattleHeart.setLayoutY(newY);
        
        // Check collision with projectiles
        checkProjectileCollisions();
    }

    /**
     * Starts the next attack phase
     */
    private void startNextAttack() {
        if (!battleActive) return;
        
        // Check if we've reached the maximum number of attacks (5 attacks total)
        if (currentAttackPhase >= maxAttackPhases) {
            // Battle ends after 5 attacks, player wins by surviving
            endBattle(true);
            return;
        }
        
        clearAllProjectiles();
        
        // Get difficulty multiplier
        String difficulty = currentGameDto.getGameDifficulty();
        double difficultyMultiplier = getDifficultyMultiplier(difficulty);
        
        // Select random attack pattern (now includes pillar attack)
        int attackType = battleRandom.nextInt(4);
        
        switch (attackType) {
            case 0:
                startVerticalAttack(difficultyMultiplier);
                break;
            case 1:
                startHorizontalAttack(difficultyMultiplier);
                break;
            case 2:
                startSpiralAttack(difficultyMultiplier);
                break;
            case 3:
                startPillarAttack(difficultyMultiplier);
                break;
        }
        
        currentAttackPhase++;
    }

    /**
     * Gets the number of lives based on difficulty
     */
    private int getLivesForDifficulty(String difficulty) {
        if (difficulty == null) return 1;
        
        switch (difficulty.toUpperCase()) {
            case "F": // Easy (Fácil)
                return 2; // Easy: 2 lives (can survive 2 hits)
            case "N": // Medium (Normal)
                return 1; // Medium: 1 life (can survive 1 hit)
            case "D": // Hard (Difícil)
                return 0; // Hard: 0 lives (instant death on hit)
            default:
                return 1;
        }
    }
    
    /**
     * Updates the life counter display
     */
    private void updateLifeDisplay() {
        if (playerLives > 0) {
            lblLifesRemaining.setText("Vidas: " + playerLives);
        } else {
            lblLifesRemaining.setText("Vidas: 0 (¡Sin vidas restantes!)");
        }
    }

    /**
     * Gets difficulty multiplier for projectile speed and count
     */
    private double getDifficultyMultiplier(String difficulty) {
        if (difficulty == null) return 1.0;
        
        switch (difficulty.toUpperCase()) {
            case "F": // Easy (Fácil)
                return 1.0; // 3 projectiles base
            case "N": // Medium (Normal)
                return 1.33; // 4 projectiles (3 * 1.33 ≈ 4)
            case "D": // Hard (Difícil)
                return 1.67; // 5 projectiles (3 * 1.67 ≈ 5)
            default:
                return 1.0;
        }
    }

    /**
     * Attack Pattern 1: Vertical projectiles from top
     */
    private void startVerticalAttack(double difficultyMultiplier) {
        // Set battle area to square shape for optimal vertical attack patterns
        setBattleAreaSquare();
        
        // Add neon blue particles when Sans attacks
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 12);
        
        // Use AnimationHandler for attack pattern
        animationHandler.createVerticalAttack(pnBattleArea, difficultyMultiplier, activeProjectiles, projectileTimelines, battleRandom);
        
        // Schedule next attack or end battle
        Timeline nextPhase = new Timeline(
            new KeyFrame(Duration.millis(3000), e -> {
                if (currentAttackPhase >= maxAttackPhases) {
                    endBattle(true);
                } else {
                    startNextAttack();
                }
            })
        );
        nextPhase.play();
    }

    /**
     * Attack Pattern 2: Horizontal projectiles from sides
     */
    private void startHorizontalAttack(double difficultyMultiplier) {
        // Set battle area to rectangular shape for optimal horizontal attack patterns
        setBattleAreaRectangular();
        
        // Add neon blue particles when Sans attacks
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 15);
        
        // Use AnimationHandler for attack pattern
        animationHandler.createHorizontalAttack(pnBattleArea, difficultyMultiplier, activeProjectiles, projectileTimelines, battleRandom);
        
        Timeline nextPhase = new Timeline(
            new KeyFrame(Duration.millis(3500), e -> {
                if (currentAttackPhase >= maxAttackPhases) {
                    endBattle(true);
                } else {
                    startNextAttack();
                }
            })
        );
        nextPhase.play();
    }

    /**
     * Attack Pattern 3: Spiral projectiles
     */
    private void startSpiralAttack(double difficultyMultiplier) {
        // Set battle area to square shape for optimal spiral attack patterns
        setBattleAreaSquare();
        
        // Add neon blue particles when Sans attacks
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 20);
        
        // Use AnimationHandler for attack pattern
        animationHandler.createSpiralAttack(pnBattleArea, difficultyMultiplier, activeProjectiles, projectileTimelines, battleRandom);
        
        Timeline nextPhase = new Timeline(
            new KeyFrame(Duration.millis(2800), e -> {
                if (currentAttackPhase >= maxAttackPhases) {
                    endBattle(true);
                } else {
                    startNextAttack();
                }
            })
        );
        nextPhase.play();
    }

    /**
     * Attack Pattern 4: Directional wall pillar (Undertale-style)
     */
    private void startPillarAttack(double difficultyMultiplier) {
        // Set battle area to rectangular shape for optimal pillar attack patterns
        setBattleAreaRectangular();
        
        // Add neon blue particles when Sans attacks
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 25);
        
        // Use AnimationHandler for attack pattern
        animationHandler.createPillarAttack(pnBattleArea, difficultyMultiplier, activeProjectiles, projectileTimelines, battleRandom);
        
        Timeline nextPhase = new Timeline(
            new KeyFrame(Duration.millis(4000), e -> {
                if (currentAttackPhase >= maxAttackPhases) {
                    endBattle(true);
                } else {
                    startNextAttack();
                }
            })
        );
        nextPhase.play();
    }









    /**
     * Checks for collisions between heart and projectiles
     */
    private void checkProjectileCollisions() {
        // Skip collision detection if player is recovering from a hit
        if (isRecovering) return;
        
        double heartX = imgBattleHeart.getLayoutX();
        double heartY = imgBattleHeart.getLayoutY();
        double heartWidth = imgBattleHeart.getFitWidth();
        double heartHeight = imgBattleHeart.getFitHeight();
        
        for (Rectangle projectile : new ArrayList<>(activeProjectiles)) {
            double projX = projectile.getLayoutX();
            double projY = projectile.getLayoutY();
            double projWidth = projectile.getWidth();
            double projHeight = projectile.getHeight();
            
            // Simple bounding box collision detection
            if (heartX < projX + projWidth &&
                heartX + heartWidth > projX &&
                heartY < projY + projHeight &&
                heartY + heartHeight > projY) {
                
                // Collision detected!
                onHeartHit();
                break;
            }
        }
    }

    /**
     * Handles when the heart gets hit
     */
    private void onHeartHit() {
        // Prevent multiple hits during recovery
        if (isRecovering) return;
        
        // Set recovery state
        isRecovering = true;
        
        // Play hit effect on heart
        animationHandler.playHeartHitEffect(imgBattleHeart);
        soundUtils.playAttackSound();
        
        // IMMEDIATELY stop all current attacks and clear projectiles
        clearAllProjectiles();
        
        // Reduce player lives
        if (playerLives > 0) {
            playerLives--;
            updateLifeDisplay();
            
            if (playerLives > 0) {
                // Player still has lives, continue battle
                showAlert("¡GOLPEADO!", "Te han golpeado. Vidas restantes: " + playerLives + ". Recuperándose...");
                
                // Reset heart position to center
                double centerX = pnBattleArea.getPrefWidth() / 2 - imgBattleHeart.getFitWidth() / 2;
                double centerY = pnBattleArea.getPrefHeight() / 2 - imgBattleHeart.getFitHeight() / 2;
                imgBattleHeart.setLayoutX(centerX);
                imgBattleHeart.setLayoutY(centerY);
                
                // Add visual recovery effect (flashing heart)
                addRecoveryEffect();
                
                // Wait 1 second for recovery, then continue with next attack
                Timeline recoveryDelay = new Timeline(
                    new KeyFrame(Duration.millis(1000), e -> {
                        isRecovering = false;
                        startNextAttack();
                    })
                );
                recoveryDelay.play();
            } else {
                // No lives left, end battle with loss
                isRecovering = false;
                endBattle(false);
            }
        } else {
            // Hard difficulty or no lives remaining
            isRecovering = false;
            endBattle(false);
        }
    }
    
    /**
     * Adds a visual recovery effect to the heart (flashing)
     */
    private void addRecoveryEffect() {
        Timeline flashEffect = new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(imgBattleHeart.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(150), new KeyValue(imgBattleHeart.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(300), new KeyValue(imgBattleHeart.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(450), new KeyValue(imgBattleHeart.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(600), new KeyValue(imgBattleHeart.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(750), new KeyValue(imgBattleHeart.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(900), new KeyValue(imgBattleHeart.opacityProperty(), 1.0))
        );
        flashEffect.play();
    }

    /**
     * Ends the battle
     */
    private void endBattle(boolean victory) {
        battleActive = false;
        isRecovering = false; // Reset recovery state
        
        // Stop all timers
        if (heartMovementTimer != null) heartMovementTimer.stop();
        if (battleTimer != null) battleTimer.stop();
        
        // Clear projectiles and pillar positions
        clearAllProjectiles();
        usedPillarPositions.clear();
        
        // Stop any ongoing battle area resize animations
        if (battleAreaResizeTimeline != null) {
            battleAreaResizeTimeline.stop();
        }
        
        // Reset battle area to default size
        resetBattleAreaSize();
        
        // Remove key handlers
        root.setOnKeyPressed(null);
        root.setOnKeyReleased(null);
        
        // Ensure heart is fully visible
        imgBattleHeart.setOpacity(1.0);
        
        // Show result
        String message;
        if (victory) {
            if (currentAttackPhase >= maxAttackPhases) {
                message = "¡HAS SOBREVIVIDO A TODOS LOS ATAQUES DE SANS!";
            } else {
                message = "¡HAS DERROTADO A SANS!";
            }
        } else {
            message = "SANS TE HA DERROTADO...";
        }
        
        showAlert(victory ? "¡VICTORIA!" : "DERROTA", message);
        
        // Implement battle rewards system
        if (victory) {
            // Victory: Continue without extra points (just the sequence completion bonus)
            showAlert("¡VICTORIA!", "Has sobrevivido al ataque de Sans. Continúa jugando.");
        } else {
            // Loss: Give small point bonus as consolation
            int consolationBonus = 25; // Small bonus for trying
            currentGameDto.setGameTotalPoints(currentGameDto.getGameTotalPoints() + consolationBonus);
            updateGameInfo();
            showAlert("DERROTA", "Sans te ha derrotado, pero recibes " + consolationBonus + " puntos de consolación.");
        }
        
        // Reset battle variables for next battle
        currentAttackPhase = 0;
        playerLives = 0;
        
        // Hide battle UI after delay
        Timeline hideDelay = new Timeline(
            new KeyFrame(Duration.millis(2000), e -> {
                vboxBattle.setVisible(false);
                pressedKeys.clear();
            })
        );
        hideDelay.play();
    }

    /**
     * Clears all active projectiles
     */
    private void clearAllProjectiles() {
        // Stop all projectile animations
        for (Timeline timeline : projectileTimelines) {
            timeline.stop();
        }
        projectileTimelines.clear();
        
        // Remove all projectiles from battle area
        pnBattleArea.getChildren().removeIf(node -> activeProjectiles.contains(node));
        activeProjectiles.clear();
    }
    
    /**
     * Smoothly transitions the battle area size to accommodate different attack patterns
     * @param targetWidth The target width for the battle area
     * @param targetHeight The target height for the battle area
     * @param duration The duration of the transition in milliseconds
     */
    private void resizeBattleArea(double targetWidth, double targetHeight, double duration) {
        // Stop any existing resize animation
        if (battleAreaResizeTimeline != null) {
            battleAreaResizeTimeline.stop();
        }
        
        double currentWidth = pnBattleArea.getPrefWidth();
        double currentHeight = pnBattleArea.getPrefHeight();
        
        // Add brief visual feedback when area changes significantly
        if (Math.abs(targetWidth - currentWidth) > 20 || Math.abs(targetHeight - currentHeight) > 20) {
            // Brief border flash to indicate area change
            javafx.scene.effect.DropShadow resizeGlow = new javafx.scene.effect.DropShadow();
            resizeGlow.setColor(Color.CYAN);
            resizeGlow.setRadius(10);
            resizeGlow.setSpread(0.6);
            pnBattleArea.setEffect(resizeGlow);
            
            // Remove glow after short time
            Timeline removeGlow = new Timeline(
                new KeyFrame(Duration.millis(400), e -> pnBattleArea.setEffect(null))
            );
            removeGlow.play();
        }
        
        // Create smooth transition animation
        battleAreaResizeTimeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(pnBattleArea.prefWidthProperty(), currentWidth),
                new KeyValue(pnBattleArea.prefHeightProperty(), currentHeight)
            ),
            new KeyFrame(Duration.millis(duration),
                new KeyValue(pnBattleArea.prefWidthProperty(), targetWidth),
                new KeyValue(pnBattleArea.prefHeightProperty(), targetHeight)
            )
        );
        
        battleAreaResizeTimeline.setOnFinished(e -> {
            // Battle area resize complete - no automatic heart repositioning
            // The heart should stay where the player positioned it
            // Only reposition if heart is outside new boundaries
            double heartX = imgBattleHeart.getLayoutX();
            double heartY = imgBattleHeart.getLayoutY();
            double heartWidth = imgBattleHeart.getFitWidth();
            double heartHeight = imgBattleHeart.getFitHeight();
            
            double minX = 5;
            double maxX = pnBattleArea.getPrefWidth() - heartWidth - 5;
            double minY = 5;
            double maxY = pnBattleArea.getPrefHeight() - heartHeight - 5;
            
            // Only adjust position if heart is outside new boundaries
            double newX = Math.max(minX, Math.min(maxX, heartX));
            double newY = Math.max(minY, Math.min(maxY, heartY));
            
            // Only move if necessary (boundary violation)
            if (newX != heartX || newY != heartY) {
                Timeline boundaryAdjust = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(imgBattleHeart.layoutXProperty(), heartX),
                        new KeyValue(imgBattleHeart.layoutYProperty(), heartY)
                    ),
                    new KeyFrame(Duration.millis(200),
                        new KeyValue(imgBattleHeart.layoutXProperty(), newX),
                        new KeyValue(imgBattleHeart.layoutYProperty(), newY)
                    )
                );
                boundaryAdjust.play();
            }
        });
        
        battleAreaResizeTimeline.play();
    }
    
    /**
     * Sets battle area to square shape for vertical and spiral attacks
     */
    private void setBattleAreaSquare() {
        resizeBattleArea(DEFAULT_BATTLE_AREA_WIDTH, DEFAULT_BATTLE_AREA_HEIGHT, 800);
    }
    
    /**
     * Sets battle area to rectangular shape for horizontal and pillar attacks
     */
    private void setBattleAreaRectangular() {
        resizeBattleArea(RECTANGULAR_BATTLE_AREA_WIDTH, RECTANGULAR_BATTLE_AREA_HEIGHT, 800);
    }
    
    /**
     * Resets battle area to default square size
     */
    private void resetBattleAreaSize() {
        resizeBattleArea(DEFAULT_BATTLE_AREA_WIDTH, DEFAULT_BATTLE_AREA_HEIGHT, 500);
    }

    /**
     * Called when a sequence (K to A) is completed. Triggers column damage animation and Sans battle.
     */
    private void onSequenceCompleted(int columnIndex, List<CardContainer> completedSequence) {
        System.out.println("Sequence completed in column " + columnIndex + " with " + completedSequence.size() + " cards");
        
        // First, show visual effect on all columns taking damage
        playColumnDamageAnimation(() -> {
            // After damage animation completes, start the battle
            startSansBattle();
        });
    }
    
    /**
     * Plays damage animation on all columns before battle starts
     */
    private void playColumnDamageAnimation(Runnable onComplete) {
        System.out.println("Playing column damage animation...");
        
        // Create simultaneous damage effects on all columns
        List<Timeline> columnAnimations = new ArrayList<>();
        
        for (int i = 0; i < columns.size(); i++) {
            Pane column = columns.get(i);
            
            // Create damage effect for this column
            Timeline columnDamage = new Timeline();
            
            // Red flash effect
            DropShadow redGlow = new DropShadow();
            redGlow.setColor(Color.RED);
            redGlow.setRadius(20);
            redGlow.setSpread(0.8);
            
            // Shake effect with color flash
            double originalLayoutX = column.getLayoutX();
            columnDamage.getKeyFrames().addAll(
                // Initial flash
                new KeyFrame(Duration.ZERO,
                    new KeyValue(column.effectProperty(), null)),
                new KeyFrame(Duration.millis(100),
                    new KeyValue(column.effectProperty(), redGlow),
                    new KeyValue(column.layoutXProperty(), originalLayoutX + 5)),
                new KeyFrame(Duration.millis(200),
                    new KeyValue(column.layoutXProperty(), originalLayoutX - 5)),
                new KeyFrame(Duration.millis(300),
                    new KeyValue(column.layoutXProperty(), originalLayoutX + 3)),
                new KeyFrame(Duration.millis(400),
                    new KeyValue(column.layoutXProperty(), originalLayoutX - 2)),
                new KeyFrame(Duration.millis(500),
                    new KeyValue(column.layoutXProperty(), originalLayoutX),
                    new KeyValue(column.effectProperty(), null))
            );
            
            columnAnimations.add(columnDamage);
        }
        
        // Play all column animations simultaneously
        for (Timeline animation : columnAnimations) {
            animation.play();
        }
        
        // Add screen shake and lightning effects
        animationHandler.playDramaticFlash(root, Color.RED, 1000);
        animationHandler.playLightningEffect(root);
        soundUtils.playAttackSound();
        
        // Create particles from Sans position
        double sansX = imgSans.getLayoutX() + imgSans.getFitWidth() / 2;
        double sansY = imgSans.getLayoutY() + imgSans.getFitHeight() / 2;
        animationHandler.createNeonBlueParticles(root, sansX, sansY, 30);
        
        // Wait for animation to complete, then trigger callback
        Timeline completeCallback = new Timeline(
            new KeyFrame(Duration.millis(1200), e -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            })
        );
        completeCallback.play();
    }

    private void showGameFinishedBox() {
        javafx.application.Platform.runLater(() -> {
            vboxGameFinished.setVisible(true);
            vboxGameFinished.toFront();
            vboxGameFinished.setOpacity(0);
            vboxGameFinished.setScaleY(0.7);
            Timeline popInTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(vboxGameFinished.opacityProperty(), 0),
                    new KeyValue(vboxGameFinished.scaleYProperty(), 0.7)),
                new KeyFrame(Duration.millis(200),
                    new KeyValue(vboxGameFinished.opacityProperty(), 1),
                    new KeyValue(vboxGameFinished.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.millis(300),
                    new KeyValue(vboxGameFinished.scaleYProperty(), 1.0))
            );
            popInTimeline.play();
        });
    }

    // Call this after game ends (win or lose)
    private void handleGameEnd() {
        showGameFinishedBox();
    }
}