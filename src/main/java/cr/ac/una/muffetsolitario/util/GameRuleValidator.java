package cr.ac.una.muffetsolitario.util;

import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CardContainer;

public class GameRuleValidator {

    public boolean isValidMove(CardContainer firstCardOfSequence, BoardColumnDto toColumn) {
        List<CardContainer> toCards = toColumn.getCardList();

        if (toCards.isEmpty()) {
            //Only can put king iun column Empty 
            return firstCardOfSequence.getCardDto().getCardValue() == 13;
        } else {
            CardContainer topCard = toCards.get(toCards.size() - 1);
            int topValue = topCard.getCardDto().getCardValue();
            int movingValue = firstCardOfSequence.getCardDto().getCardValue();
            return movingValue == topValue - 1;
        }
    }

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