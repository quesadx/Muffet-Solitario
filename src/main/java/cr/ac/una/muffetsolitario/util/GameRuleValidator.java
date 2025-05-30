package cr.ac.una.muffetsolitario.util;

import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CardContainer;

public class GameRuleValidator {

    /**
     * Solo permite mover una carta o secuencia si la carta superior de la columna destino
     * es exactamente un número mayor que la carta que se va a colocar.
     * Si la columna destino está vacía, solo se puede colocar un rey (valor 13).
     */
    public boolean isValidMove(CardContainer firstCardOfSequence, BoardColumnDto toColumn) {
        List<CardContainer> toCards = toColumn.getCardList();

        if (toCards.isEmpty()) {
            // Solo se puede colocar un rey en una columna vacía
            return firstCardOfSequence.getCardDto().getCardValue() == 13;
        } else {
            CardContainer topCard = toCards.get(toCards.size() - 1);
            int topValue = topCard.getCardDto().getCardValue();
            int movingValue = firstCardOfSequence.getCardDto().getCardValue();

            // Solo se permite si la carta que se mueve es exactamente un número menor
            return movingValue == topValue - 1;
        }
    }

    /**
     * Valida que la secuencia sea descendente y del mismo palo.
     * (Este método puede mantenerse igual si ya cumple con las reglas del juego)
     */
    public boolean isValidSequence(List<CardContainer> sequence) {
        if (sequence == null || sequence.isEmpty()) return false;
        String suit = sequence.get(0).getCardDto().getCardSuit();
        int prevValue = sequence.get(0).getCardDto().getCardValue();

        for (int i = 1; i < sequence.size(); i++) {
            CardContainer card = sequence.get(i);
            if (!card.getCardDto().getCardSuit().equals(suit)) return false;
            int value = card.getCardDto().getCardValue();
            if (value != prevValue - 1) return false;
            prevValue = value;
        }
        return true;
    }
}