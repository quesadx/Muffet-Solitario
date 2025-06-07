package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.*;
import cr.ac.una.muffetsolitario.service.GameService;
import cr.ac.una.muffetsolitario.util.AppContext;
import cr.ac.una.muffetsolitario.util.GameLogic;
import cr.ac.una.muffetsolitario.util.Respuesta;
import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.SoundUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.FadeTransition;
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
    private int elapsedSeconds = 0;

    // Lightning effect timer
    private Timeline lightningTimer;
    private Timeline gameTimer;

    @FXML
    private Pane pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
            pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen;
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
    private VBox vboxAlert;
    @FXML
    private Label lblAlertMessage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columns = List.of(pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
                pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen);
        sequencePanes = List.of(pnSequence1, pnSequence2, pnSequence3, pnSequence4,
                pnSequence5, pnSequence6, pnSequence7, pnSequence8);
        pnDeck.setOnMouseClicked(event -> {
            try {
                soundUtils.playAttackSound();
                gameLogic.dealFromDeck();
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

    public void startGame() {
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
        GameService gameService = new GameService();
        Respuesta respuesta = gameService.getGameByUserId(userAccountDto.getUserId());
        if (respuesta.getEstado()) {
            currentGameDto = (GameDto) respuesta.getResultado("Game");
            if (currentGameDto == null) {
                showAlert("Error", "No se pudo obtener la partida guardada.");
                return;
            }
            System.out.println("Partida cargada desde la base de datos.");
        } else {
            System.out.println("No hay partida guardada, se crea una nueva.");
            currentGameDto = new GameDto();
            currentGameDto.setGameUserFk(userAccountDto.getUserId());
            userAccountDto.setGameId(currentGameDto.getGameId()); // TODO: CHANGE GAMESERVICE TO MAKE THIS WORK
            currentGameDto.setGameDifficulty(difficultySelected);
            currentGameDto.setGameCreatedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            System.out.println("dia de creacion: " + currentGameDto.getGameCreatedDate());
            currentGameDto.setGameTotalPoints(500);
        }
        gameLogic = new GameLogic(currentGameDto);
    }

    private void updateCardImage(CardContainer cardContainer) {
        CardDto cardDto = cardContainer.getCardDto();
        String imagePath = cardDto.isCardFaceUp()
                ? "/cr/ac/una/muffetsolitario/resources/assets/" +
                        (cardDto.getCardSuit().equals("C") ? "Corazones"
                                : cardDto.getCardSuit().equals("T") ? "Treboles"
                                        : cardDto.getCardSuit().equals("P") ? "Picas" : "Diamantes")
                        +
                        "/" + cardDto.getCardSuit() + "_" + cardDto.getCardValue() + ".png"
                : "/cr/ac/una/muffetsolitario/resources/assets/Card_Back1.png";
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

        // Adjust columns to match available board columns
        int columnsToRender = Math.min(boardColumns.size(), columns.size());

        // Clear all columns first to prevent duplicate children
        columns.forEach(pane -> pane.getChildren().clear());

        for (int i = 0; i < columnsToRender; i++) {
            final int columnIdx = i;
            Pane pane = columns.get(i);
            BoardColumnDto boardColumn = boardColumns.get(i);
            List<CardContainer> cards = boardColumn.getCardList();

            // Skip if no cards in this column
            if (cards == null || cards.isEmpty()) {
                continue;
            }

            // Create new card containers for each card
            for (int j = 0; j < cards.size(); j++) {
                CardContainer cardContainer = cards.get(j);

                // Ensure card is not already in a parent
                if (cardContainer.getParent() != null) {
                    ((Pane) cardContainer.getParent()).getChildren().remove(cardContainer);
                }
                updateCardImage(cardContainer);
                cardContainer.setFitWidth(120);
                cardContainer.setFitHeight(160);
                cardContainer.setLayoutX(0);
                cardContainer.setLayoutY(j * CARD_OFFSET);

                cardContainer.setOnMousePressed(event -> {
                    if (!cardContainer.getCardDto().isCardFaceUp())
                        return;

                    List<CardContainer> cardsInColumn = boardColumn.getCardList();
                    int idx = cardsInColumn.indexOf(cardContainer);

                    // Check if we can pick up this sequence (Spider Solitaire rules)
                    List<CardContainer> potentialSequence = cardsInColumn.subList(idx, cardsInColumn.size());
                    if (!isValidSequenceToPickUp(potentialSequence)) {
                        showAlert("Secuencia inválida", "Solo puedes mover secuencias descendentes del mismo palo.");
                        return;
                    }

                    draggedSequence = new ArrayList<>(potentialSequence);
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

                        // Simple column highlighting
                        int potentialTargetCol = getTargetColumnIndex(mouseX, mouseY);
                        columns.forEach(col -> col.setStyle(""));
                        if (potentialTargetCol != -1) {
                            columns.get(potentialTargetCol).setStyle(
                                    "-fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-style: dashed;");
                        }

                        // Animate dragged cards
                        for (int k = 0; k < draggedSequence.size(); k++) {
                            CardContainer c = draggedSequence.get(k);

                            // Simple wiggle effect
                            double wiggleAmount = Math.min((k + 1) * 2.0, 8.0); // Cap maximum wiggle
                            double wiggleX = Math.sin(System.currentTimeMillis() * 0.01) * wiggleAmount;
                            double wiggleY = Math.cos(System.currentTimeMillis() * 0.008) * (wiggleAmount * 0.5);

                            // Update card position with reduced vertical spacing
                            double reducedOffset = CARD_OFFSET * 0.6; // Reduce spacing by 40%
                            c.setLayoutX(mouseX - dragOffsetsX[k] + wiggleX);
                            c.setLayoutY(mouseY - dragOffsetsY[k] + k * reducedOffset + wiggleY);

                            // Gentle rotation
                            double rotation = Math.sin(System.currentTimeMillis() * 0.005) * wiggleAmount;
                            c.setRotate(rotation);

                            c.toFront();
                        }
                    }
                });

                cardContainer.setOnMouseReleased(mouseEvent -> {
                    if (draggedSequence != null) {
                        // Clear all column highlighting
                        columns.forEach(col -> col.setStyle(""));

                        // Reset rotation for all cards in sequence
                        for (CardContainer card : draggedSequence) {
                            card.setRotate(0);
                        }

                        int targetColIdx = getTargetColumnIndex(mouseEvent.getSceneX(), mouseEvent.getSceneY());
                        boolean moved = false;
                        if (targetColIdx != -1) {
                            try {
                                handleMoveCards(fromColIdx, targetColIdx, draggedSequence.get(0));
                                moved = true;
                                // Add glitch effect to target column on successful move
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
            showAlert("Posible movimiento", gameLogic.suggestPossibleMoves(currentGameDto));
        } catch (IllegalArgumentException e) {
            showAlert("Movimiento inválido", e.getMessage());
        }
    }

    private void showAlert(String titulo, String mensaje) {
        // Use Platform.runLater to avoid issues during animation processing
        javafx.application.Platform.runLater(() -> {
            // Set message and make vbox visible
            lblAlertMessage.setText(mensaje);
            vboxAlert.setVisible(true);
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
        for (int i = 0; i < cantCardsToShow; i++) {
            int cardIdx = deckCards.size() - 1 - i;
            CardContainer card = deckCards.get(cardIdx);
            String imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Card_Back1.png";
            try {
                URL resource = getClass().getResource(imagePath);
                if (resource != null) {
                    card.setImagePath(resource.toExternalForm());
                    card.setImage(new Image(resource.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Error cargando imagen del mazo: " + imagePath);
            }
            card.setFitWidth(120);
            card.setFitHeight(160);
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
                card.setFitWidth(120);
                card.setFitHeight(160);
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

        for (int i = 1; i < sequence.size(); i++) {
            CardDto currentCard = sequence.get(i).getCardDto();
            // Must be same suit and value must be exactly one less than previous
            if (!currentCard.getCardSuit().equals(suit) ||
                    currentCard.getCardValue() != prevValue - 1) {
                return false;
            }
            prevValue = currentCard.getCardValue();
        }

        return true;
    }

    @FXML
    private void onActionBtnHint(ActionEvent event) {
        showAlert("Posible movimiento", gameLogic.suggestPossibleMoves(currentGameDto));
    }
}