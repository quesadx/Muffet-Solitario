package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cr.ac.una.muffetsolitario.model.BoardColumnDto;
import cr.ac.una.muffetsolitario.model.CompletedSequenceDto;
import cr.ac.una.muffetsolitario.model.CardContainer;
import cr.ac.una.muffetsolitario.model.CardDto;
import cr.ac.una.muffetsolitario.model.DeckDto;
import cr.ac.una.muffetsolitario.model.GameDto;
import javafx.collections.FXCollections;

public class GameLogic {

    public GameDto gameDto;
    private final GameRuleValidator rulesValidator = new GameRuleValidator();

    public GameLogic() {
    }

    public GameLogic(GameDto gameDto) {
        this.gameDto = gameDto;
    }

    public void initializeDeck(GameDto gameDto) {
        if (gameDto == null)
            throw new IllegalArgumentException("GameDto no puede ser null");

        String difficultyStr = gameDto.getGameDifficulty();
        String[] suits;
        int deals;

        if ("H".equalsIgnoreCase(difficultyStr)) {
            suits = new String[] { "C", "D", "P", "T" };
            deals = 5;
        } else if ("M".equalsIgnoreCase(difficultyStr)) {
            suits = new String[] { "C", "D" };
            deals = 5;
        } else {
            suits = new String[] { "C" };
            deals = 5;
        }

        int maxValueCard = 13;
        int totalCards = 104;
        int numSuits = suits.length;
        int cardsPerSuit = totalCards / numSuits;
        List<CardContainer> cardContainers = new ArrayList<>();

        for (String suit : suits) {
            int repeats = cardsPerSuit / maxValueCard;
            for (int r = 0; r < repeats; r++) {
                for (int cardValue = 1; cardValue <= maxValueCard; cardValue++) {
                    CardDto cardDto = new CardDto();
                    cardDto.setCardSuit(suit);
                    cardDto.setCardValue(cardValue);
                    cardDto.setCardFaceUp(false);

                    CardContainer cardContainer = new CardContainer();
                    cardContainer.setCardDto(cardDto);
                    cardContainers.add(cardContainer);
                }
            }
        }

        Collections.shuffle(cardContainers);

        if (gameDto.getDeckDto() == null) {
            gameDto.setDeckDto(new DeckDto());
        }
        gameDto.getDeckDto().setCardList(FXCollections.observableArrayList(cardContainers));
        gameDto.setGameDealsRemaining(deals);
    }

    public void loadCardsToColumn() {
        if (gameDto == null || gameDto.getDeckDto() == null || gameDto.getBoardColumnList() == null)
            throw new IllegalStateException("GameDto, Deck o BoardColumnList no pueden ser null");

        List<CardContainer> deckCards = gameDto.getDeckDto().getCardList();
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();

        if (deckCards == null || deckCards.isEmpty())
            throw new IllegalStateException("El deck no tiene cartas para repartir");

        int deckIndex = 0;
        for (int i = 0; i < columns.size() && i < 10; i++) {
            BoardColumnDto boardColumn = columns.get(i);
            int cardsToDeal = (i > 4) ? 5 : 6;
            for (int j = 0; j < cardsToDeal && deckIndex < deckCards.size(); j++) {
                CardContainer cardContainer = deckCards.get(deckIndex++);
                cardContainer.getCardDto().setCardFaceUp(j == cardsToDeal - 1);
                cardContainer.getCardDto().setCardBcolmnId(boardColumn.getBcolmnId());
                boardColumn.getCardList().add(cardContainer);
            }
        }
        if (deckIndex > 0)
            deckCards.subList(0, deckIndex).clear();
    }

    public void moveCardsBetweenColumns(int fromColumnIndex, int toColumnIndex, CardContainer firstCardOfSequence) {
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        BoardColumnDto fromColumn = columns.get(fromColumnIndex);
        BoardColumnDto toColumn = columns.get(toColumnIndex);

        if (fromColumnIndex == toColumnIndex)
            throw new IllegalArgumentException("No se puede mover cartas a la misma columna.");

        List<CardContainer> fromCards = fromColumn.getCardList();
        List<CardContainer> toCards = toColumn.getCardList();

        int startIndex = fromCards.indexOf(firstCardOfSequence);
        if (startIndex == -1)
            throw new IllegalArgumentException("La carta seleccionada no se encuentra en la columna de origen.");

        List<CardContainer> sequence = new ArrayList<>(fromCards.subList(startIndex, fromCards.size()));
        for (CardContainer card : sequence)
            if (!card.getCardDto().isCardFaceUp())
                throw new IllegalArgumentException("No se puede mover una secuencia con cartas boca abajo.");

        if (!rulesValidator.isValidSequence(sequence))
            throw new IllegalArgumentException(
                    "La secuencia a mover no es válida (debe ser descendente y del mismo palo).");

        if (!rulesValidator.isValidMove(firstCardOfSequence, toColumn))
            throw new IllegalArgumentException("Movimiento no permitido según las reglas del juego.");

        toCards.addAll(sequence);
        for (CardContainer card : sequence)
            card.getCardDto().setCardBcolmnId(toColumn.getBcolmnId());
        fromCards.subList(startIndex, fromCards.size()).clear();
        if (!fromCards.isEmpty())
            fromCards.get(fromCards.size() - 1).getCardDto().setCardFaceUp(true);

        gameDto.incrementMoveCount();
        checkAndRemoveCompletedSequence(toColumn);
        checkGameFinished(gameDto);
    }

    public void dealFromDeck() {
        if (gameDto.getGameDealsRemaining() <= 0)
            throw new IllegalStateException("No quedan repartos disponibles.");

        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        List<CardContainer> deckCardList = gameDto.getDeckDto().getCardList();

        if (deckCardList == null || deckCardList.isEmpty())
            throw new IllegalStateException("No hay cartas en el mazo para repartir.");

        for (BoardColumnDto column : columns)
            if (column.getCardList().isEmpty())
                throw new IllegalStateException(
                        "Todas las columnas deben tener al menos una carta para repartir desde el mazo.");

        for (int i = 0; i < columns.size(); i++) {
            if (!deckCardList.isEmpty()) {
                CardContainer card = deckCardList.remove(0);
                card.getCardDto().setCardFaceUp(true);
                card.getCardDto().setCardBcolmnId(columns.get(i).getBcolmnId());
                columns.get(i).getCardList().add(card);
            }
        }
        gameDto.decrementDealsRemaining(); // Usar método del DTO si existe
        checkGameFinished(gameDto);
    }

    private void checkAndRemoveCompletedSequence(BoardColumnDto column) {
        List<CardContainer> cards = column.getCardList();
        if (cards.size() < 13)
            return;

        List<CardContainer> last13Cards = new ArrayList<>(cards.subList(cards.size() - 13, cards.size()));

        if (rulesValidator.isValidSequence(last13Cards) && last13Cards.get(0).getCardDto().getCardValue() == 13) {
            CompletedSequenceDto completed = new CompletedSequenceDto();
            completed.setCseqGameFk(gameDto.getGameId());
            completed.setCseqOrder(gameDto.getCompletedSequenceList().size() + 1);
            for (CardContainer card : last13Cards) {
                card.getCardDto().setCardCseqId(completed.getCseqId());
                completed.addCard(card);
            }

            gameDto.addCompletedSequence(completed);
            gameDto.incrementCompletedSequences();

            cards.removeAll(last13Cards);

            if (!cards.isEmpty())
                cards.get(cards.size() - 1).getCardDto().setCardFaceUp(true);
        }
    }

    private List<int[]> getPossibleMoves(GameDto game) {
        List<BoardColumnDto> columns = game.getBoardColumnList();
        List<CardContainer> topFaceUpCards = new ArrayList<>();
        List<int[]> possibleMoves = new ArrayList<>();

        for (BoardColumnDto column : columns) {
            CardContainer lastFaceUp = null;
            List<CardContainer> cards = column.getCardList();
            for (int i = cards.size() - 1; i >= 0; i--) {
                if (cards.get(i).getCardDto().isCardFaceUp()) {
                    lastFaceUp = cards.get(i);
                    break;
                }
            }
            topFaceUpCards.add(lastFaceUp);
        }

        for (int i = 0; i < topFaceUpCards.size(); i++) {
            CardContainer fromCard = topFaceUpCards.get(i);
            if (fromCard == null)
                continue;
            for (int j = 0; j < topFaceUpCards.size(); j++) {
                if (i == j)
                    continue;
                CardContainer toCard = topFaceUpCards.get(j);
                if (toCard == null)
                    continue;

                CardDto fromDto = fromCard.getCardDto();
                CardDto toDto = toCard.getCardDto();

                if (fromDto.getCardSuit().equals(toDto.getCardSuit())
                        && fromDto.getCardValue() == toDto.getCardValue() - 1) {
                    possibleMoves.add(new int[] { i, j });
                }
            }
        }
        return possibleMoves;
    }

    public boolean hasPossibleMoves(GameDto game) {
        return !getPossibleMoves(game).isEmpty();
    }

    public void suggestPossibleMoves(GameDto game) {
        List<BoardColumnDto> columns = game.getBoardColumnList();
        List<CardContainer> topFaceUpCards = new ArrayList<>();
        for (BoardColumnDto column : columns) {
            CardContainer lastFaceUp = null;
            List<CardContainer> cards = column.getCardList();
            for (int i = cards.size() - 1; i >= 0; i--) {
                if (cards.get(i).getCardDto().isCardFaceUp()) {
                    lastFaceUp = cards.get(i);
                    break;
                }
            }
            topFaceUpCards.add(lastFaceUp);
        }

        List<int[]> moves = getPossibleMoves(game);
        if (moves.isEmpty()) {
            // TODO:MATTEO DEBE MANDAR AQUI A UN MENSAJE EN PANTALLA

            System.out.println("No hay movimientos posibles entre columnas.");
        } else {
            for (int[] move : moves) {
                CardContainer fromCard = topFaceUpCards.get(move[0]);
                CardContainer toCard = topFaceUpCards.get(move[1]);
                System.out.println("Sugerencia: Puedes mover la carta " +
                        fromCard.getCardDto().getCardValue() + fromCard.getCardDto().getCardSuit() +
                        " de la columna " + (move[0] + 1) + " a la columna " + (move[1] + 1) +
                        " sobre la carta " + toCard.getCardDto().getCardValue() + toCard.getCardDto().getCardSuit());
            }
        }
    }

    public void checkGameFinished(GameDto game) {
        // WIN: todas las secuencias completas han sido formadas
        if (game.getCompletedSequenceList() != null && game.getCompletedSequenceList().size() == 8) {
            game.setGameStatus("WIN");
            // TODO:MATTEO DEBE MANDAR AQUI A UNA VISTA DE GANADOR O A UNA ANIMACION
            return;
        }

        boolean isBoardEmpty = game.getBoardColumnList().stream()
                .allMatch(column -> column.getCardList() == null || column.getCardList().isEmpty());
        boolean isDeckEmpty = game.getDeckDto() == null || game.getDeckDto().getCardList().isEmpty();

        if (isBoardEmpty && isDeckEmpty) {
            game.setGameStatus("WIN");
            // TODO:MATTEO DEBE MANDAR AQUI A UNA VISTA DE GANADOR O A UNA ANIMACION
            System.out.println("¡Felicidades! Has ganado el juego (tablero y mazo vacíos).");
            return;
        }
        if (isDeckEmpty && !hasPossibleMoves(game)) {
            game.setGameStatus("LOST");
            // TODO:MATTEO DEBE MANDAR AQUI A UNA VISTA DE PERDEDOR O A UNA ANIMACION
            System.out.println("¡Juego perdido! No hay más movimientos posibles y el mazo está vacío.");
        }
    }
}