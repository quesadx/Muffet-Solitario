package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CompletedSequenceDto;
import cr.ac.una.muffetsolitario.model.CardContainer;
import cr.ac.una.muffetsolitario.model.GameDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameLogic {

    public GameDto gameDto;
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
        // validate if all the cards are faceup
        for (CardContainer card : sequence) {
            if (!card.getCardDto().isCardFaceUp()) {
                throw new IllegalArgumentException("No se puede mover una secuencia con cartas boca abajo.");
            }
        }
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

        checkAndRemoveCompletedSequence(toColumn);
        checkGameFinished();

    }
    public void dealFromDeck() {
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        List<CardContainer> deckCardList = gameDto.getDeckDto().getCardList();

        if (deckCardList == null || deckCardList.isEmpty()) {
            throw new IllegalStateException("No hay cartas en el mazo para repartir.");
        }

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
            checkGameFinished();
        }
    }

    private void checkAndRemoveCompletedSequence(BoardColumnDto column) {
        List<CardContainer> cards = column.getCardList();
        if (cards.size() < 13)
            return;

        List<CardContainer> last13 = new ArrayList<>(cards.subList(cards.size() - 13, cards.size()));

        if (rulesValidator.isValidSequence(last13) &&
                last13.get(0).getCardDto().getCardValue() == 13) {

            CompletedSequenceDto completed = new CompletedSequenceDto();
            completed.setCseqGameFk(gameDto.getGameId());
            completed.setCseqOrder(
                    gameDto.getGameCompletedSequences() != null ? gameDto.getGameCompletedSequences() + 1 : 1);
            for (CardContainer card : last13) {
                card.getCardDto().setCardCseqId(completed.getCseqId());
                completed.addCard(card);
            }

            if (gameDto.getCompletedSequenceList() == null) {
                gameDto.setCompletedSequenceList((FXCollections.observableArrayList()));
            }
            gameDto.getCompletedSequenceList().add(completed);

            cards.removeAll(last13);

            if (!cards.isEmpty()) {
                CardContainer newTop = cards.get(cards.size() - 1);
                newTop.getCardDto().setCardFaceUp(true);
            }
        }
    }

    public void checkGameFinished() {
        // Standard number of completed sequences
        final int TOTAL_SEQUENCES_TO_COMPLETE = 8;

        // Check if all sequences have been completed
        int completedSequences = (gameDto.getCompletedSequenceList() != null) ? gameDto.getCompletedSequenceList().size(): 0;

        boolean allSequencesCompleted = completedSequences >= TOTAL_SEQUENCES_TO_COMPLETE;

        // Check if there are no cards left in the deck
        boolean deckEmpty = gameDto.getDeckDto() == null
                || gameDto.getDeckDto().getCardList() == null
                || gameDto.getDeckDto().getCardList().isEmpty();

        if (allSequencesCompleted || (deckEmpty)) {
            // Update the game status (TODO: Confirm with Alex if "COMPLETED" is correct here)
            gameDto.setGameStatus("COMPLETED");
            gameDto.setGameTotalPoints(gameDto.getGameTotalPoints());
            // Notify the UI or user here (e.g., show victory/defeat dialog)
            // showGameEndDialog(allSequencesCompleted);

            // (Optional) Save final state, statistics, etc.
        }
    }

}
