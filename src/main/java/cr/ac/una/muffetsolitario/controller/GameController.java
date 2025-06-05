package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.*;
import cr.ac.una.muffetsolitario.util.GameLogic;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

public class GameController extends Controller implements Initializable {

    private GameDto currentGameDto;
    private GameLogic gameLogic;
    private List<Pane> columns;
    private List<Pane> sequencePanes;

    // Drag & drop variables
    private List<CardContainer> draggedSequence = null;
    private double[] dragOffsetsX, dragOffsetsY;
    private int fromColIdx = -1, fromCardIdx = -1;
    private static final double CARD_OFFSET = 25;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columns = List.of(pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
                pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen);
        sequencePanes = List.of(pnSequence1, pnSequence2, pnSequence3, pnSequence4,
                pnSequence5, pnSequence6, pnSequence7, pnSequence8);
        pnDeck.setOnMouseClicked(event -> {
            try {
                gameLogic.dealFromDeck();
                updateBoard();
            } catch (Exception e) {
                showAlert("No se puede repartir", e.getMessage());
            }
        });
        startNewGame();
    }

    @Override
    public void initialize() {
        // Requerido por la herencia, no eliminar.
    }

    public void startNewGame() {
        currentGameDto = new GameDto();
        gameLogic = new GameLogic(currentGameDto);
        currentGameDto.initializeBoardColumns(10);
        gameLogic.initializeDeck(currentGameDto);
        gameLogic.loadCardsToColumn();
        for (BoardColumnDto col : currentGameDto.getBoardColumnList()) {
            List<CardContainer> cardList = col.getCardList();
            if (!cardList.isEmpty())
                cardList.get(cardList.size() - 1).getCardDto().setCardFaceUp(true);
        }
        updateBoard();
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

            // Remueve de su padre actual
            if (card.getParent() != null) {
                ((Pane) card.getParent()).getChildren().remove(card);
            }
            // Añade al root y coloca en la posición absoluta
            root.getChildren().add(card);
            card.setLayoutX(rootPos.getX());
            card.setLayoutY(rootPos.getY());
            card.toFront();
        }
    }

    private void moveSequenceToColumn(List<CardContainer> sequence, Pane columnPane) {
        double baseY = columnPane.getChildren().size() * CARD_OFFSET;
        for (int k = 0; k < sequence.size(); k++) {
            CardContainer card = sequence.get(k);
            if (card.getParent() != null) {
                ((Pane) card.getParent()).getChildren().remove(card);
            }
            root.getChildren().remove(card);
            if (!columnPane.getChildren().contains(card)) {
                card.setLayoutX(0);
                card.setLayoutY(baseY + k * CARD_OFFSET);
                columnPane.getChildren().add(card);
            }
        }
    }

    private void renderBoard() {
        List<BoardColumnDto> boardColumns = currentGameDto.getBoardColumnList();
        if (boardColumns == null || boardColumns.size() < columns.size()) {
            showAlert("Error de renderizado", "No hay suficientes columnas para renderizar el tablero.");
            return;
        }
        for (int i = 0; i < columns.size(); i++) {
            final int columnIdx = i;
            Pane pane = columns.get(i);
            pane.getChildren().clear();
            BoardColumnDto boardColumn = boardColumns.get(i);
            List<CardContainer> cards = boardColumn.getCardList();
            for (int j = 0; j < cards.size(); j++) {
                CardContainer cardContainer = cards.get(j);
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
                    draggedSequence = new ArrayList<>(cardsInColumn.subList(idx, cardsInColumn.size()));
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
                        for (int k = 0; k < draggedSequence.size(); k++) {
                            CardContainer c = draggedSequence.get(k);
                            c.setLayoutX(mouseX - dragOffsetsX[k]);
                            c.setLayoutY(mouseY - dragOffsetsY[k] + k * CARD_OFFSET);
                            c.toFront();
                        }
                    }
                });

                cardContainer.setOnMouseReleased(event -> {
                    if (draggedSequence != null) {
                        int targetColIdx = getTargetColumnIndex(event.getSceneX(), event.getSceneY());
                        boolean moved = false;
                        if (targetColIdx != -1) {
                            try {
                                handleMoveCards(fromColIdx, targetColIdx, draggedSequence.get(0));
                                moved = true;
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
            gameLogic.moveCardsBetweenColumns(fromCol, toCol, cardContainer);
            updateBoard();
            gameLogic.suggestPossibleMoves(currentGameDto);
        } catch (IllegalArgumentException e) {
            showAlert("Movimiento inválido", e.getMessage());
        }
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
        for (Pane pane : sequencePanes)
            pane.getChildren().clear();
        List<CompletedSequenceDto> completedSequences = currentGameDto.getCompletedSequenceList();
        if (completedSequences == null)
            return;
        double cardOffset = 30;
        int maxCardsToShow = 3;
        for (int i = 0; i < completedSequences.size() && i < sequencePanes.size(); i++) {
            Pane pane = sequencePanes.get(i);
            List<CardContainer> cards = completedSequences.get(i).getCardList();
            for (int j = 0; j < Math.min(maxCardsToShow, cards.size()); j++) {
                CardContainer card = cards.get(j);
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
        System.out.println("Mae si me estoy presionando");
        gameLogic.undoLastMove();
        updateBoard();

    }
}