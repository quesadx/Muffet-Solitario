package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CardContainer;
import cr.ac.una.muffetsolitario.model.CardDto;
import cr.ac.una.muffetsolitario.model.DeckDto;
import cr.ac.una.muffetsolitario.model.GameDto;
import cr.ac.una.muffetsolitario.util.GameLogic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class GameController extends Controller implements Initializable {

    private GameDto currentGameDto;
    private GameLogic gameLogic;
    private List<Pane> columns;

    // Variables para drag & drop visual
    private CardContainer draggedCard = null;
    private double dragOffsetX, dragOffsetY;
    private int fromColIdx = -1;
    private int fromCardIdx = -1;

    // Dificultad seleccionada (por defecto: fácil)
    private GameLogic.Difficulty currentDifficulty = GameLogic.Difficulty.EASY;

    @FXML
    private Pane pnColumn0ne;
    @FXML
    private Pane pnColumnTwo;
    @FXML
    private Pane pnColumnThree;
    @FXML
    private Pane pnColumnFour;
    @FXML
    private Pane pnColumnFive;
    @FXML
    private Pane pnColumnSix;
    @FXML
    private Pane pnColumnSeven;
    @FXML
    private Pane pnColumnEight;
    @FXML
    private Pane pnColumnNine;
    @FXML
    private Pane pnColumnTen;
    @FXML
    private AnchorPane root;
    @FXML
    private Pane pnDeck;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columns = List.of(
                pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
                pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen);
        // Handler to deck, this can be improved
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
    }

    // TODO: This´s temporal
    public void setDifficulty(GameLogic.Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public void startNewGame() {
        currentGameDto = new GameDto();
        gameLogic = new GameLogic(currentGameDto);
        currentGameDto.initializeBoardColumns(10);
        gameLogic.initializeDeck(currentDifficulty);
        gameLogic.loadCardsToColumn();

        for (BoardColumnDto col : currentGameDto.getBoardColumnList()) {
            List<CardContainer> cardList = col.getCardList();
            if (!cardList.isEmpty()) {
                CardContainer last = cardList.get(cardList.size() - 1);
                last.getCardDto().setCardFaceUp(true);
            }
        }
        renderDeck();
        renderBoard();
    }

    private void updateCardImage(CardContainer cardContainer) {
        CardDto cardDto = cardContainer.getCardDto();
        String imagePath;
        if (cardDto.isCardFaceUp()) {
            switch (cardDto.getCardSuit()) {
                case "C":
                    imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Corazones/" + cardDto.getCardSuit() + "_"
                            + cardDto.getCardValue() + ".png";
                    break;
                case "T":
                    imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Treboles/" + cardDto.getCardSuit() + "_"
                            + cardDto.getCardValue() + ".png";
                    break;
                case "P":
                    imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Picas/" + cardDto.getCardSuit() + "_"
                            + cardDto.getCardValue() + ".png";
                    ;
                    break;
                case "D":
                    imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Diamantes/" + cardDto.getCardSuit() + "_"
                            + cardDto.getCardValue() + ".png";
                    break;
                default:
                    System.out.println("Error cargando las imagenes");
                    imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Card_Back1.png";
                    break;
            }
        } else {
            imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Card_Back1.png";
        }
        try {
            URL resource = getClass().getResource(imagePath);
            if (resource == null) {
                throw new IllegalArgumentException("No se encontró la imagen: " + imagePath);
            }
            cardContainer.setImagePath(resource.toExternalForm());
            cardContainer.setImage(new Image(resource.toExternalForm()));
        } catch (Exception e) {
            System.err.println("Error cargando imagen de carta: No se encontró la imagen: " + imagePath);
        }
    }

    private void renderBoard() {
        List<BoardColumnDto> boardColumns = currentGameDto.getBoardColumnList();
        if (boardColumns == null || boardColumns.size() < columns.size()) {
            showAlert("Error de renderizado", "No hay suficientes columnas para renderizar el tablero.");
            return;
        }
        double cardOffset = 25; // Distance between cards
        for (int i = 0; i < columns.size(); i++) {
            final int columnIdx = i;
            Pane pane = columns.get(i);
            pane.getChildren().clear();
            BoardColumnDto boardColumn = boardColumns.get(i);
            List<CardContainer> cards = boardColumn.getCardList();
            for (int j = 0; j < cards.size(); j++) {
                final int cardIdx = j;
                CardContainer cardContainer = cards.get(j);
                updateCardImage(cardContainer);
                cardContainer.setFitWidth(120);
                cardContainer.setFitHeight(160);
                cardContainer.setLayoutY(j * cardOffset);

                // Clear handlers
                cardContainer.setOnMousePressed(null);
                cardContainer.setOnMouseDragged(null);
                cardContainer.setOnMouseReleased(null);

                // DRAG & DROP VISUAL
                cardContainer.setOnMousePressed(event -> {
                    draggedCard = cardContainer;
                    dragOffsetX = event.getSceneX() - cardContainer.getLayoutX();
                    dragOffsetY = event.getSceneY() - cardContainer.getLayoutY();
                    fromColIdx = columnIdx;
                    fromCardIdx = cardIdx;
                    cardContainer.toFront();
                });

                cardContainer.setOnMouseDragged(event -> {
                    if (draggedCard != null) {
                        double newX = event.getSceneX() - dragOffsetX;
                        double newY = event.getSceneY() - dragOffsetY;
                        draggedCard.setLayoutX(newX);
                        draggedCard.setLayoutY(newY);
                        draggedCard.toFront();
                    }
                });

                cardContainer.setOnMouseReleased(event -> {
                    if (draggedCard != null) {
                        int targetColIdx = getTargetColumnIndex(event.getSceneX(), event.getSceneY());
                        if (targetColIdx != -1) {
                            try {
                                handleMoveCards(fromColIdx, targetColIdx, draggedCard);
                            } catch (Exception e) {
                                showAlert("Movimiento inválido", e.getMessage());
                            }
                        }
                        draggedCard = null;
                        fromColIdx = -1;
                        fromCardIdx = -1;
                        updateBoard();
                    }
                });

                cardContainer.setLayoutX(0);
                pane.getChildren().add(cardContainer);
            }
        }
    }

    private int getTargetColumnIndex(double sceneX, double sceneY) {
        for (int i = 0; i < columns.size(); i++) {
            Pane pane = columns.get(i);
            double layoutX = pane.localToScene(0, 0).getX();
            double layoutY = pane.localToScene(0, 0).getY();
            double width = pane.getWidth();
            double height = pane.getHeight();
            if (sceneX >= layoutX && sceneX <= layoutX + width &&
                    sceneY >= layoutY && sceneY <= layoutY + height) {
                return i;
            }
        }
        return -1;
    }

    public void updateBoard() {
        renderBoard();
        renderDeck();
    }

    public void handleMoveCards(int fromCol, int toCol, CardContainer cardContainer) {
        try {
            gameLogic.moveCardsBetweenColumns(fromCol, toCol, cardContainer);
            updateBoard();
        } catch (IllegalArgumentException e) {
            showAlert("Movimiento inválido", e.getMessage());
        }
    }

    //
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

        double offsetX = 18; // Espace between Cards
        double baseX = 0;
        double baseY = 0;

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

            card.setLayoutX(baseX + i * offsetX);
            card.setLayoutY(baseY);

            card.setOnMousePressed(null);
            card.setOnMouseDragged(null);
            card.setOnMouseReleased(null);

            pnDeck.getChildren().add(card);
        }
    }
}