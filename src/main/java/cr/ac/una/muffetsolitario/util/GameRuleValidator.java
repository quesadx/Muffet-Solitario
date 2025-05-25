package cr.ac.una.muffetsolitario.util;

import java.util.List;
import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CardContainer;

public class GameRuleValidator {
    public GameRuleValidator() {}

    public boolean isValidMove(CardContainer cardToMove, BoardColumnDto toColumn) {
        List<CardContainer> toCards = toColumn.getCardList();
        // Only allows moving a king to an empty column
        if (toCards.isEmpty()) {
            // A king can only be moved to an empty column
            return cardToMove.getCardDto().getCardValue() == 13; // 13 = king
        }
        // If the column is not empty, it does not allow the move
        return false;
    }

    public boolean isValidSequence(List<CardContainer> sequence) {
        if (sequence.isEmpty())
            return false;
        String suit = sequence.get(0).getCardDto().getCardSuit();
        int value = sequence.get(0).getCardDto().getCardValue();
        for (int i = 1; i < sequence.size(); i++) {
            CardContainer card = sequence.get(i);
            if (!card.getCardDto().getCardSuit().equals(suit))
                return false;
            if (card.getCardDto().getCardValue() != value - 1)
                return false;
            value = card.getCardDto().getCardValue();
        }
        return true;
    }
}
