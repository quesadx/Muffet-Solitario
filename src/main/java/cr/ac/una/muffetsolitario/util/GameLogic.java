package cr.ac.una.muffetsolitario.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cr.ac.una.muffetsolitario.model.*;
import cr.ac.una.muffetsolitario.service.UserAccountService;
import javafx.collections.FXCollections;

public class GameLogic {

    public GameDto gameDto;
    private final GameRuleValidator rulesValidator = new GameRuleValidator();
    private final List<Move> moveHistory = new ArrayList<>();
    private SequenceCompletionCallback sequenceCompletionCallback;
    
    public interface SequenceCompletionCallback {
        void onSequenceCompleted(int columnIndex, List<CardContainer> completedSequence);
    }

    public GameLogic() {
    }

    public GameLogic(GameDto gameDto) {
        this.gameDto = gameDto;
    }
    
    public void setSequenceCompletionCallback(SequenceCompletionCallback callback) {
        this.sequenceCompletionCallback = callback;
    }

    private void updateCardPositions(List<CardContainer> cardList) {
        for (int i = 0; i < cardList.size(); i++) {
            cardList.get(i).getCardDto().setCardPositionInContainer(i);
        }
    }

    public void initializeDeck(GameDto gameDto) {
        if (gameDto == null)
            throw new IllegalArgumentException("GameDto no puede ser null");

        String[] suits;

        if ("D".equalsIgnoreCase(gameDto.getGameDifficulty())) {
            suits = new String[] { "C", "D", "P", "T" };
            gameDto.setGameDealsRemaining(5);
        } else if ("N".equalsIgnoreCase(gameDto.getGameDifficulty())) {
            suits = new String[] { "C", "D" };
            gameDto.setGameDealsRemaining(5);
        } else {
            suits = new String[] { "C" };
            gameDto.setGameDealsRemaining(5);
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
        updateCardPositions(cardContainers);

        if (gameDto.getDeckDto() == null) {
            gameDto.setDeckDto(new DeckDto());
        }
        gameDto.getDeckDto().setCardList(FXCollections.observableArrayList(cardContainers));
    }

    public void sortBoardColumnsByIndex(GameDto gameDto) {
        gameDto.getBoardColumnList().sort(Comparator.comparing(BoardColumnDto::getBcolmnIndex));
    }

    public void sortCardsInAllColumns(GameDto gameDto) {
        for (BoardColumnDto col : gameDto.getBoardColumnList()) {
            col.getCardList().sort(Comparator.comparing(c -> c.getCardDto().getCardPositionInContainer()));
        }
    }

    public void sortDeckCards(GameDto gameDto) {
        gameDto.getDeckDto().getCardList().sort(Comparator.comparing(c -> c.getCardDto().getCardPositionInContainer()));
    }

    public void sortCompletedSequences(GameDto gameDto) {
        for (CompletedSequenceDto seq : gameDto.getCompletedSequenceList()) {
            seq.getCardList().sort(Comparator.comparing(c -> c.getCardDto().getCardPositionInContainer()));
        }
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

        for (BoardColumnDto boardColumn : columns) {
            List<CardContainer> cardList = boardColumn.getCardList();
            updateCardPositions(cardList);
        }

        // ACTUALIZA POSICIONES EN EL DECK RESTANTE
        updateCardPositions(deckCards);
    }

    public void moveCardsBetweenColumns(int fromColumnIndex, int toColumnIndex, CardContainer firstCardOfSequence) {
        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        BoardColumnDto fromColumn = columns.get(fromColumnIndex);
        BoardColumnDto toColumn = columns.get(toColumnIndex);

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

        boolean lastCardFaceUpBeforeMove = false;
        if (startIndex > 0)
            lastCardFaceUpBeforeMove = fromCards.get(startIndex - 1).getCardDto().isCardFaceUp();

        toCards.addAll(sequence);
        for (CardContainer card : sequence)
            card.getCardDto().setCardBcolmnId(toColumn.getBcolmnId());
        fromCards.subList(startIndex, fromCards.size()).clear();
        if (!fromCards.isEmpty())
            fromCards.get(fromCards.size() - 1).getCardDto().setCardFaceUp(true);

        updateCardPositions(toCards);
        updateCardPositions(fromCards);

        moveHistory.add(new Move(fromColumnIndex, toColumnIndex, sequence, lastCardFaceUpBeforeMove));

        gameDto.incrementMoveCount();

        checkAndRemoveCompletedSequence(toColumn);
        checkGameFinished(gameDto);
    }

    public void dealFromDeck() {
        if (gameDto.getGameDealsRemaining() <= 0)
            throw new IllegalStateException("No quedan repartos disponibles.");

        List<BoardColumnDto> columns = gameDto.getBoardColumnList();
        List<CardContainer> deckCardList = gameDto.getDeckDto().getCardList();

        List<Integer> columnsDealtTo = new ArrayList<>();
        List<CardContainer> dealtCards = new ArrayList<>();

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

                columnsDealtTo.add(i);
                dealtCards.add(card);

            }
        }

        for (int i = 0; i < columns.size(); i++) {
            List<CardContainer> cardList = columns.get(i).getCardList();
            updateCardPositions(cardList);
        }

        gameDto.decrementDealsRemaining();         
        gameDto.setGameTotalPoints(gameDto.getGameTotalPoints()-1);
        moveHistory.add(new Move(columnsDealtTo, dealtCards));
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

            updateCardPositions(completed.getCardList());

            int columnIndex = gameDto.getBoardColumnList().indexOf(column);
            int completedSequenceIndex = gameDto.getCompletedSequenceList().size();
            int previousPoints = gameDto.getGameTotalPoints();
            moveHistory.add(new Move(columnIndex, last13Cards, completedSequenceIndex, previousPoints));

            gameDto.addCompletedSequence(completed);
            gameDto.incrementCompletedSequences();

            cards.removeAll(last13Cards);
            gameDto.setGameTotalPoints(gameDto.getGameTotalPoints() + 100);
            if (!cards.isEmpty())
                cards.get(cards.size() - 1).getCardDto().setCardFaceUp(true);
                
            // Trigger battle sequence callback
            if (sequenceCompletionCallback != null) {
                sequenceCompletionCallback.onSequenceCompleted(columnIndex, last13Cards);
            }
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

    public String suggestPossibleMoves(GameDto game) {
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
            return "No hay movimientos posibles entre columnas.";
        } else {
            // Sugerir el primer movimiento posible
            int[] move = moves.get(0);
            CardContainer fromCard = topFaceUpCards.get(move[0]);
            CardContainer toCard = topFaceUpCards.get(move[1]);
            
            if (fromCard == null || toCard == null) {
                return "No hay movimientos posibles entre columnas.";
            }

            return "Puedes mover la carta " +
                    fromCard.getCardDto().getCardValue() + fromCard.getCardDto().getCardSuit() +
                    " de la columna " + (move[0] + 1) + " a la columna " + (move[1] + 1) +
                    " sobre la carta " + toCard.getCardDto().getCardValue() + toCard.getCardDto().getCardSuit();
        }
    }

    private void setUserStats(UserAccountDto user, GameDto game) {
        if (user != null) {
            user.setUserTotalGames(user.getUserTotalGames() + 1);
            user.setUserTotalScore(user.getUserTotalScore() + game.getGameTotalPoints());
            user.setUserWonGames(user.getUserWonGames() + 1);
            user.setUserBestScore(game.getGameTotalPoints() > user.getUserBestScore() ? game.getGameTotalPoints() : 0);
        }
    }

    public String checkGameFinished(GameDto game) {
        // WIN: todas las secuencias completas han sido formadas
        UserAccountDto user = (UserAccountDto) AppContext.getInstance().get("LoggedInUser");
        if (game.getCompletedSequenceList() != null && game.getCompletedSequenceList().size() == 8) {
            game.setGameStatus("WIN");

            setUserStats(user, game);
            if(!user.isUserGuest()){
                UserAccountService userAccountService = new UserAccountService();
                Respuesta respuesta = userAccountService.saveUserAccount(user);

                if(respuesta.getEstado()){
                    System.out.println("Se actualizó el usuario y sus stats" + respuesta.getMensaje());
                } else {
                    System.out.println("No se actualizó el usuario" + respuesta.getMensaje());
                }
            }
            return "WIN";
        }

        boolean isBoardEmpty = game.getBoardColumnList().stream()
                .allMatch(column -> column.getCardList() == null || column.getCardList().isEmpty());
        boolean isDeckEmpty = game.getDeckDto() == null || game.getDeckDto().getCardList().isEmpty();

        if (isBoardEmpty && isDeckEmpty) {
            game.setGameStatus("WIN");
            setUserStats(user, game);
            System.out.println("¡Felicidades! Has ganado el juego (tablero y mazo vacíos).");
            return "WIN";
        }
        if (isDeckEmpty && !hasPossibleMoves(game)) {
            game.setGameStatus("LOST");

            if (user != null)
                user.setUserTotalGames(user.getUserTotalGames() + 1);

            System.out.println("¡Juego perdido! No hay más movimientos posibles y el mazo está vacío.");
            return "LOST";
        }
        return "";
    }

    public void undoLastMove() {
        if (moveHistory.isEmpty())
            return;
        Move lastMove = moveHistory.get(moveHistory.size() - 1);
    
        if (lastMove.getType() == Move.MoveType.COMPLETE_SEQUENCE) {

            undoSingleMove(); 
            if (!moveHistory.isEmpty()) {
                undoSingleMove(); 
            }
        } else {
            undoSingleMove();
        }
    }
    
    private void undoSingleMove() {
        if (moveHistory.isEmpty())
            return;
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
    
        if (lastMove.getType() == Move.MoveType.COLUMN_TO_COLUMN) {
            List<BoardColumnDto> columns = gameDto.getBoardColumnList();
            BoardColumnDto fromColumn = columns.get(lastMove.getFromColumnIndex());
            BoardColumnDto toColumn = columns.get(lastMove.getToColumnIndex());
            List<CardContainer> movedSequence = lastMove.getMovedSequence();
    
            int toSize = toColumn.getCardList().size();
            int seqSize = movedSequence.size();
            if (toSize >= seqSize) {
                List<CardContainer> removed = new ArrayList<>(toColumn.getCardList().subList(toSize - seqSize, toSize));
                toColumn.getCardList().subList(toSize - seqSize, toSize).clear();
    
                for (CardContainer card : removed) {
                    card.getCardDto().setCardBcolmnId(fromColumn.getBcolmnId());
                }
                fromColumn.getCardList().addAll(removed);
            }
    
            if (fromColumn.getCardList().size() > movedSequence.size()) {
                int idx = fromColumn.getCardList().size() - movedSequence.size() - 1;
                if (idx >= 0) {
                    fromColumn.getCardList().get(idx).getCardDto().setCardFaceUp(lastMove.wasLastCardFaceUpBeforeMove());
                }
            }
    
            updateCardPositions(fromColumn.getCardList());
            updateCardPositions(toColumn.getCardList());
    
            gameDto.setGameMoveCount(gameDto.getGameMoveCount() - 1);
    
            int newPoints = gameDto.getGameTotalPoints() - 1;
            gameDto.setGameTotalPoints(Math.max(newPoints, 0));
    
        } else if (lastMove.getType() == Move.MoveType.DEAL_FROM_DECK) {
            List<BoardColumnDto> columns = gameDto.getBoardColumnList();
            List<Integer> columnsDealtTo = lastMove.getColumnsDealtTo();
            List<CardContainer> dealtCards = lastMove.getDealtCards();
    
            for (int i = columnsDealtTo.size() - 1; i >= 0; i--) {
                int colIdx = columnsDealtTo.get(i);
                BoardColumnDto column = columns.get(colIdx);
                List<CardContainer> cardList = column.getCardList();
                if (!cardList.isEmpty()) {
                    CardContainer card = cardList.remove(cardList.size() - 1);
                    card.getCardDto().setCardBcolmnId(null);
                    card.getCardDto().setCardFaceUp(false);
                    gameDto.getDeckDto().getCardList().add(0, card); 
                }
                updateCardPositions(cardList);
            }
            updateCardPositions(gameDto.getDeckDto().getCardList());
    
            gameDto.setGameDealsRemaining(gameDto.getGameDealsRemaining() + 1);
            gameDto.setGameTotalPoints(gameDto.getGameTotalPoints() - 1);
        } else if (lastMove.getType() == Move.MoveType.COMPLETE_SEQUENCE) {
            List<BoardColumnDto> columns = gameDto.getBoardColumnList();
            BoardColumnDto column = columns.get(lastMove.getFromColumnIndex());
            List<CardContainer> sequence = lastMove.getMovedSequence();
    
            if (gameDto.getCompletedSequenceList().size() > lastMove.getCompletedSequenceIndex()) {
                gameDto.getCompletedSequenceList().remove(lastMove.getCompletedSequenceIndex());
            }
    
            for (CardContainer card : sequence) {
                card.getCardDto().setCardCseqId(null);
                card.getCardDto().setCardFaceUp(true); 
                card.getCardDto().setCardBcolmnId(column.getBcolmnId());
            }
            column.getCardList().addAll(sequence);
    
            updateCardPositions(column.getCardList());
    
            gameDto.setGameTotalPoints(Math.max(gameDto.getGameTotalPoints() - 100, 0));
            gameDto.setGameCompletedSequences(gameDto.getGameCompletedSequences() - 1);
        }
    }
    private static class Move {
        enum MoveType {
            COLUMN_TO_COLUMN, DEAL_FROM_DECK, COMPLETE_SEQUENCE
        }

        private final MoveType type;

        private final int fromColumnIndex;
        private final int toColumnIndex;
        private final List<CardContainer> movedSequence;
        private final boolean lastCardFaceUpBeforeMove;

        private final List<Integer> columnsDealtTo;
        private final List<CardContainer> dealtCards;

        private final int completedSequenceIndex;
        private final int previousPoints;

        public Move(int fromColumnIndex, int toColumnIndex, List<CardContainer> movedSequence,
                boolean lastCardFaceUpBeforeMove) {
            this.type = MoveType.COLUMN_TO_COLUMN;
            this.fromColumnIndex = fromColumnIndex;
            this.toColumnIndex = toColumnIndex;
            this.movedSequence = new ArrayList<>(movedSequence);
            this.lastCardFaceUpBeforeMove = lastCardFaceUpBeforeMove;
            this.columnsDealtTo = null;
            this.dealtCards = null;
            this.completedSequenceIndex = -1;
            this.previousPoints = -1;
        }

        public Move(List<Integer> columnsDealtTo, List<CardContainer> dealtCards) {
            this.type = MoveType.DEAL_FROM_DECK;
            this.fromColumnIndex = -1;
            this.toColumnIndex = -1;
            this.movedSequence = null;
            this.lastCardFaceUpBeforeMove = false;
            this.columnsDealtTo = new ArrayList<>(columnsDealtTo);
            this.dealtCards = new ArrayList<>(dealtCards);
            this.completedSequenceIndex = -1;
            this.previousPoints = -1;
        }

        public Move(int columnIndex, List<CardContainer> sequence, int completedSequenceIndex, int previousPoints) {
            this.type = MoveType.COMPLETE_SEQUENCE;
            this.fromColumnIndex = columnIndex;
            this.toColumnIndex = -1;
            this.movedSequence = new ArrayList<>(sequence);
            this.lastCardFaceUpBeforeMove = false;
            this.columnsDealtTo = null;
            this.dealtCards = null;
            this.completedSequenceIndex = completedSequenceIndex;
            this.previousPoints = previousPoints;
        }

        public MoveType getType() {
            return type;
        }

        public int getFromColumnIndex() {
            return fromColumnIndex;
        }

        public int getToColumnIndex() {
            return toColumnIndex;
        }

        public List<CardContainer> getMovedSequence() {
            return movedSequence;
        }

        public boolean wasLastCardFaceUpBeforeMove() {
            return lastCardFaceUpBeforeMove;
        }

        public List<Integer> getColumnsDealtTo() {
            return columnsDealtTo;
        }

        public List<CardContainer> getDealtCards() {
            return dealtCards;
        }

        public int getCompletedSequenceIndex() {
            return completedSequenceIndex;
        }

        public int getPreviousPoints() {
            return previousPoints;
        }
    }

}