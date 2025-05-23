package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumn;
import cr.ac.una.muffetsolitario.model.Card;
import cr.ac.una.muffetsolitario.model.Deck;

public class GameLogic {
    private List<BoardColumn> columns;
    private List<Card> deck;

    public GameLogic() {
    }

    public GameLogic(Deck deckInstance) {
        this.deck = deckInstance.getCardList();
        this.columns = new ArrayList<>();
        initColumns();
        loadCardsToColumn();
    }

    public void initColumns() {
        for (int i = 0; i < 10; i++) {
            columns.add(new BoardColumn());
        }
    }

    public void loadCardsToColumn() {
        int deskIndex = 0;
        for (int i = 0; i < 10; i++) {
            BoardColumn boardColumn = columns.get(i);
            int cardsToDeal = (i < 4) ? 5 : 6;
            for (int j = 0; j < cardsToDeal; j++) {
                Card card = deck.get(deskIndex);
                deskIndex++;
                if (j == cardsToDeal - 1) {
                    card.setCardFaceUp((short) 1);
                }
                card.setCardBcolmnFk(boardColumn);
            }
        }
    }
    // TODO:THIS METHOD WORK TO ONE CARD BUT PROBABLY IT'S BETTER USE OTHER METHOD
    // (SEQUENCE)

    /*public void moveCardBetweenColumns(int fromColumnIndex, int toColumnIndex,
            Card cardToMove) {
        if (fromColumnIndex < 0 || fromColumnIndex >= columns.size()) {
            throw new IllegalArgumentException("Índice de columna origen inválido.");
            // TODO:Create message here with class mensaje
        }
        if (toColumnIndex < 0 || toColumnIndex >= columns.size()) {
            throw new IllegalArgumentException("Índice de columna destino inválido.");
            // // TODO:Create message here with class mensaje
        }
        if (cardToMove == null) {
            throw new IllegalArgumentException("La carta a mover no puede ser nula.");
            // // TODO:Create message here with class mensaje
        }

        BoardColumn fromColumn = columns.get(fromColumnIndex);
        BoardColumn toColumn = columns.get(toColumnIndex);

        List<Card> fromCards = fromColumn.getCardList();
        List<Card> toCards = toColumn.getCardList();

        if (!isValidMove(cardToMove, toColumn)) {
            throw new IllegalArgumentException("El movimiento es invalido.");
        }
        if (!fromCards.contains(cardToMove)) {
            throw new IllegalArgumentException("La carta no se encuentra en la columna origen.");
        }

        fromCards.remove(cardToMove);
        toCards.add(cardToMove);
        cardToMove.setCardBcolmnFk(toColumn);
        if (!fromCards.isEmpty()) {
            Card newTop = fromCards.get(fromCards.size() - 1);
            newTop.setCardFaceUp((short) 1);
        }
    } */

    private boolean isValidMove(Card cardToMove, BoardColumn toColumn) {
        List<Card> toCards = toColumn.getCardList();
        if (toCards.isEmpty()) {
            // TODO:only move king to empty column
            return cardToMove.getCardValue() == 13; // 13 = king
        } else {
            Card topCard = toCards.get(toCards.size() - 1);
            // Ejemplo: valor descendente y color opuesto
            return (cardToMove.getCardValue() == topCard.getCardValue() - 1) &&
                    (!cardToMove.getCardSuit().equals(topCard.getCardSuit()));
        }
    }

    public void moveCardsBetweenColumns(int fromColumnIndex, int toColumnIndex, Card fistCardOfSequence) {
        BoardColumn fromColumn = columns.get(fromColumnIndex);
        BoardColumn toColumn = columns.get(toColumnIndex);

        List<Card> fromCards = fromColumn.getCardList();
        List<Card> toCards = toColumn.getCardList();

        int startIndex = fromCards.indexOf(fistCardOfSequence);

        List<Card> sequence = new ArrayList<>(fromCards.subList(startIndex, fromCards.size()));

        if (!isValidMove(fistCardOfSequence, toColumn)) {
            // TODO:Create message here with class mensaje
            throw new IllegalArgumentException("Movimiento no permitido según las reglas del juego.");
        }
        toCards.addAll(sequence);
        for (Card card : sequence) {
            card.setCardBcolmnFk(toColumn);
        }
        fromCards.subList(startIndex, fromCards.size()).clear();
        if (!fromCards.isEmpty()) {
            Card newTop = fromCards.get(fromCards.size() - 1);
            newTop.setCardFaceUp((short) 1);
        }
    }
    //TODO: CREATE METHOD TO SHUFFLE CARDS
}
