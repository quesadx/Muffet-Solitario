package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumn;
import cr.ac.una.muffetsolitario.model.Card;
import cr.ac.una.muffetsolitario.model.Deck;

public class GameLogic {
    private List<BoardColumn> columns;
    private Deck deck;
    private List<Card> deckCardList = deck.getCardList();

    public GameLogic() {
    }

    public GameLogic(Deck deck, List<BoardColumn> columns) {
        this.deck = deck;
        this.columns = columns;
        this.deckCardList = deck.getCardList();
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
                Card card = deckCardList.get(deskIndex);
                deskIndex++;
                if (j == cardsToDeal - 1) {
                    card.setCardFaceUp((short) 1);
                }   
                //TODO: FIX THIS
               // card.setCardBcolmnFk(boardColumn);
            }
        }
    }

    private boolean isValidMove(Card cardToMove, BoardColumn toColumn) {
        List<Card> toCards = toColumn.getCardList();
        if (toCards.isEmpty()) {
            // TODO:only move king to empty column
            return cardToMove.getCardValue() == 13; // 13 = king
        } else {
            Card topCard = toCards.get(toCards.size() - 1);
            //TODO: Maybe I need change this and use method "isValidSequence"
            return (cardToMove.getCardValue() == topCard.getCardValue() - 1) &&
                    (!cardToMove.getCardSuit().equals(topCard.getCardSuit()));
        }
    }

    public void moveCardsBetweenColumns(int fromColumnIndex, int toColumnIndex, Card firstCardOfSequence) {
        BoardColumn fromColumn = columns.get(fromColumnIndex);
        BoardColumn toColumn = columns.get(toColumnIndex);

        List<Card> fromCards = fromColumn.getCardList();
        List<Card> toCards = toColumn.getCardList();

        int startIndex = fromCards.indexOf(firstCardOfSequence);

        if (startIndex == -1) {
            throw new IllegalArgumentException("La carta no se encuentra en la columna origen.");
        }

        List<Card> sequence = new java.util.ArrayList<>(fromCards.subList(startIndex, fromCards.size()));

        // Validar que la secuencia sea descendente y del mismo palo
        if (!isValidSequence(sequence)) {
            throw new IllegalArgumentException("La secuencia a mover no es válida (debe ser descendente y del mismo palo).");
        }

        if (!isValidMove(firstCardOfSequence, toColumn)) {
            throw new IllegalArgumentException("Movimiento no permitido según las reglas del juego.");
        }
        toCards.addAll(sequence);
        for (Card card : sequence) {
            card.setCardBcolmnId(toColumn);
        }
        fromCards.subList(startIndex, fromCards.size()).clear();
        if (!fromCards.isEmpty()) {
            Card newTop = fromCards.get(fromCards.size() - 1);
            newTop.setCardFaceUp((short) 1);
        }
    }

    private boolean isValidSequence(List<Card> sequence) {
        if (sequence.isEmpty())
            return false;
        String suit = sequence.get(0).getCardSuit();
        int value = sequence.get(0).getCardValue();
        for (int i = 1; i < sequence.size(); i++) {
            Card card = sequence.get(i);
            if (!card.getCardSuit().equals(suit))
                return false;
            if (card.getCardValue() != value - 1)
                return false;
            value = card.getCardValue();
        }
        return true;
    }
    // TODO: CREATE METHOD TO SHUFFLE CARDS

    public void dealFromDeck() {
        for (BoardColumn column : columns) {
            if (column.getCardList().isEmpty()) {
                // TODO: CREATE MESSAGE HERE TO SAID "ALL COLUMNS NEED CARDS TO DO THIS"
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            if (!deckCardList.isEmpty()) {
                Card card = deckCardList.remove(0);
                card.setCardFaceUp((short) 1);
                //fix this line
                card.setCardBcolmnFk(columns.get(i));
            }
        }
    }
}
