package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CardContainer;
import cr.ac.una.muffetsolitario.model.GameDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameLogic {
    private GameDto gameDto;
    private final GameRuleValidator rulesValidator = new GameRuleValidator();


    public GameLogic() {
    }

    public GameLogic(GameDto gameDto) {
        this.gameDto = gameDto;
    }

    public void initColumns() {
        if (gameDto == null) {
            throw new IllegalStateException("GameDto no puede ser null");
        }

        ObservableList<BoardColumnDto> columns = gameDto.getBoardColumnList();
        if (columns == null) {
            columns = FXCollections.observableArrayList();
            gameDto.setBoardColumnList(columns);
        }
    }

    public void loadCardsToColumn() {
        if (gameDto == null || gameDto.getDeckDto() == null || gameDto.getBoardColumnList() == null) {
            throw new IllegalStateException("GameDto, Deck o BoardColumnList no pueden ser null");
        }

        List<CardContainer> deckCards = gameDto.getDeckDto().getCardList();
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();

        if (deckCards == null || deckCards.isEmpty()) {
            throw new IllegalStateException("El deck no tiene cartas para repartir");
        }

        int deckIndex = 0;
        for (int i = 0; i < columns.size() && i < 10; i++) {
            BoardColumnDto boardColumn = columns.get(i);
            int cardsToDeal = (i < 4) ? 5 : 6;
            for (int j = 0; j < cardsToDeal && deckIndex < deckCards.size(); j++) {
                CardContainer cardContainer = deckCards.get(deckIndex);
                deckIndex++;
                // Only the last card is turned face up
                cardContainer.getCardDto().setCardFaceUp((j == cardsToDeal - 1 ? true : false));
                // Assign the card to the column
                cardContainer.getCardDto().setCardBcolmnId(boardColumn.getBcolmnId());
                boardColumn.getCardList().add(cardContainer);
            }
        }
        // Remove dealt cards from the deckF
        if (deckIndex > 0) {
            deckCards.subList(0, deckIndex).clear();
        }

    }


    public void moveCardsBetweenColumns(int fromColumnIndex, int toColumnIndex, CardContainer firstCardOfSequence) {
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        BoardColumnDto fromColumn = columns.get(fromColumnIndex);
        BoardColumnDto toColumn = columns.get(toColumnIndex);

        List<CardContainer> fromCards = fromColumn.getCardList();
        List<CardContainer> toCards = toColumn.getCardList();

        int startIndex = fromCards.indexOf(firstCardOfSequence);

        List<CardContainer> sequence = new ArrayList<>(fromCards.subList(startIndex, fromCards.size()));

        // Validate that the sequence is descending and of the same suit
        if (!rulesValidator.isValidSequence(sequence)) {
            throw new IllegalArgumentException(
                    "La secuencia a mover no es válida (debe ser descendente y del mismo palo).");
        }

        if (!rulesValidator.isValidMove(firstCardOfSequence, toColumn)) {
            throw new IllegalArgumentException("Movimiento no permitido según las reglas del juego.");
        }
        toCards.addAll(sequence);
        for (CardContainer card : sequence) {
            card.getCardDto().setCardBcolmnId(toColumn.getBcolmnId());
        }
        fromCards.subList(startIndex, fromCards.size()).clear();
        if (!fromCards.isEmpty()) {
            CardContainer newTop = fromCards.get(fromCards.size() - 1);
            newTop.getCardDto().setCardFaceUp(true);
        }
    }

    // TODO: CREATE METHOD TO SHUFFLE CARDS

    public void dealFromDeck() {
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        List<CardContainer> deckCardList = gameDto.getDeckDto().getCardList();

        // Validate that all columns have at least one card
        for (BoardColumnDto column : columns) {
            if (column.getCardList().isEmpty()) {
                throw new IllegalStateException(
                        "Todas las columnas deben tener al menos una carta para repartir desde el mazo.");
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            if (!deckCardList.isEmpty()) {
                CardContainer card = deckCardList.remove(0);
                card.getCardDto().setCardFaceUp(true);
                card.getCardDto().setCardBcolmnId(columns.get(i).getBcolmnId());
                columns.get(i).getCardList().add(card);
            }
        }
    }

}
