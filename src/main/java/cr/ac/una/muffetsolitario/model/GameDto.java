package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameDto {
    
    private Long gameId;
    private IntegerProperty gameCompletedSequences;
    private IntegerProperty gameDealsRemaining;
    private IntegerProperty gameMoveCount;
    private StringProperty gameDifficulty;
    private IntegerProperty gameDurationSeconds;
    private IntegerProperty gameTotalPoints;
    private StringProperty gameStatus;
    private ObjectProperty<Date> gameCreatedDate;
    private ObjectProperty<Date> gameLastPlayed;
    private Long gameVersion;
    private DeckDto deckDto;
    private ObservableList<BoardColumnDto> boardColumnList;
    private ObservableList<CompletedSequenceDto> completedSequenceList;
    private Long gameUserFk;
    
    public GameDto() {
        this.gameCompletedSequences = new SimpleIntegerProperty(0);
        this.gameDealsRemaining = new SimpleIntegerProperty(0);
        this.gameMoveCount = new SimpleIntegerProperty(0);
        this.gameDifficulty = new SimpleStringProperty();
        this.gameDurationSeconds = new SimpleIntegerProperty(0);
        this.gameTotalPoints = new SimpleIntegerProperty(0);
        this.gameStatus = new SimpleStringProperty();
        this.gameCreatedDate = new SimpleObjectProperty<>();
        this.gameLastPlayed = new SimpleObjectProperty<>();
        this.deckDto = new DeckDto();
        this.boardColumnList = FXCollections.observableArrayList();
        this.completedSequenceList = FXCollections.observableArrayList();
    }
    
    public GameDto(Long gameId, Integer gameMoveCount, String gameDifficulty, 
                   Integer gameDurationSeconds, Integer gameTotalPoints, String gameStatus) {
        this();
        setGameId(gameId);
        setGameMoveCount(gameMoveCount);
        setGameDifficulty(gameDifficulty);
        setGameDurationSeconds(gameDurationSeconds);
        setGameTotalPoints(gameTotalPoints);
        setGameStatus(gameStatus);
        
    }
    
    public GameDto(Game game) {
        this();

        setGameId(game.getGameId());
        setGameCompletedSequences(game.getGameCompletedSequences());
        setGameDealsRemaining(game.getGameDealsRemaining());
        setGameMoveCount(game.getGameMoveCount());
        setGameDifficulty(game.getGameDifficulty());
        setGameDurationSeconds(game.getGameDurationSeconds());
        setGameTotalPoints(game.getGameTotalPoints());
        setGameStatus(game.getGameStatus());
        setGameCreatedDate(game.getGameCreatedDate());
        setGameLastPlayed(game.getGameLastPlayed());
        gameVersion = game.getGameVersion();
        setGameUserFk(game.getGameUserFk().getUserId());
        
        if (game.getDeck() != null) {
            this.deckDto = new DeckDto(game.getDeck());
        }
        
        if (game.getBoardColumnList() != null && !game.getBoardColumnList().isEmpty()) {
            boardColumnList.clear();
            for (BoardColumn boardColumn : game.getBoardColumnList()) {
                boardColumnList.add(new BoardColumnDto(boardColumn));
            }
        }
         
        if (game.getCompletedSequenceList() != null && !game.getCompletedSequenceList().isEmpty()) {
            this.completedSequenceList.clear();
            for (CompletedSequence completedSequence : game.getCompletedSequenceList()) {
                this.completedSequenceList.add(new CompletedSequenceDto(completedSequence));
            }
        }
    }
    

    public Long getGameId() {
        return gameId;
    }
    
    public void setGameId(Long gameId) {
        this.gameId=gameId;
    }
    
    public IntegerProperty gameCompletedSequencesProperty() {
        return gameCompletedSequences;
    }
    
    public Integer getGameCompletedSequences() {
        return gameCompletedSequences.get();
    }
    
    public void setGameCompletedSequences(Integer gameCompletedSequences) {
        this.gameCompletedSequences.set(gameCompletedSequences != null ? gameCompletedSequences : 0);
    }
    
    public IntegerProperty gameDealsRemainingProperty() {
        return gameDealsRemaining;
    }
    
    public Integer getGameDealsRemaining() {
        return gameDealsRemaining.get();
    }
    
    public void setGameDealsRemaining(Integer gameDealsRemaining) {
        this.gameDealsRemaining.set(gameDealsRemaining != null ? gameDealsRemaining : 0);
    }
    
    public IntegerProperty gameMoveCountProperty() {
        return gameMoveCount;
    }
    
    public Integer getGameMoveCount() {
        return gameMoveCount.get();
    }
    
    public void setGameMoveCount(Integer gameMoveCount) {
        this.gameMoveCount.set(gameMoveCount);
    }
    
    public StringProperty gameDifficultyProperty() {
        return gameDifficulty;
    }
    
    public String getGameDifficulty() {
        return gameDifficulty.get();
    }
    
    public void setGameDifficulty(String gameDifficulty) {
        this.gameDifficulty.set(gameDifficulty);
    }
    
    public IntegerProperty gameDurationSecondsProperty() {
        return gameDurationSeconds;
    }
    
    public Integer getGameDurationSeconds() {
        return gameDurationSeconds.get();
    }
    
    public void setGameDurationSeconds(Integer gameDurationSeconds) {
        this.gameDurationSeconds.set(gameDurationSeconds);
    }
    
    public IntegerProperty gameTotalPointsProperty() {
        return gameTotalPoints;
    }
    
    public Integer getGameTotalPoints() {
        return gameTotalPoints.get();
    }
    
    public void setGameTotalPoints(Integer gameTotalPoints) {
        this.gameTotalPoints.set(gameTotalPoints);
    }
    
    public StringProperty gameStatusProperty() {
        return gameStatus;
    }
    
    public String getGameStatus() {
        return gameStatus.get();
    }
    
    public void setGameStatus(String gameStatus) {
        this.gameStatus.set(gameStatus);
    }
    
    public ObjectProperty<Date> gameCreatedDateProperty() {
        return gameCreatedDate;
    }
    
    public Date getGameCreatedDate() {
        return gameCreatedDate.get();
    }
    
    public void setGameCreatedDate(Date gameCreatedDate) {
        this.gameCreatedDate.set(gameCreatedDate);
    }
    
    public ObjectProperty<Date> gameLastPlayedProperty() {
        return gameLastPlayed;
    }
    
    public Date getGameLastPlayed() {
        return gameLastPlayed.get();
    }
    
    public void setGameLastPlayed(Date gameLastPlayed) {
        this.gameLastPlayed.set(gameLastPlayed);
    }

    public Long getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(Long gameVersion) {
        this.gameVersion = gameVersion;
    }
    
    public DeckDto getDeckDto(){
        return deckDto;
    }
    
    public void setDeckDto(DeckDto deckDto){
        this.deckDto = deckDto;
    }
    
    public ObservableList<BoardColumnDto> getBoardColumnList(){
        return boardColumnList;
    }
    
    public void setBoardColumnList(ObservableList<BoardColumnDto> boardColumnList){
        this.boardColumnList = boardColumnList;
    }
    
    public ObservableList<CompletedSequenceDto> getCompletedSequenceList() {
        return completedSequenceList;
    }
    
    public void setCompletedSequenceList(ObservableList<CompletedSequenceDto> completedSequenceList) {
        this.completedSequenceList = completedSequenceList;
    }
    
    public Long getGameUserFk() {
        return gameUserFk;
    }
    
    public void setGameUserFk(Long gameUserFk) {
        this.gameUserFk = gameUserFk;
    }

    public void addBoardColumn(BoardColumnDto boardColumn) {
        if (boardColumn != null) {
            boardColumn.setBcolmnGameFk(this.getGameId());
            boardColumnList.add(boardColumn);
        }
    }

    public void removeBoardColumn(BoardColumnDto boardColumn) {
        boardColumnList.remove(boardColumn);
    }

    public void clearBoardColumns() {
        boardColumnList.clear();
    }

    public BoardColumnDto getBoardColumnByIndex(int index) {
        if (index >= 0 && index < boardColumnList.size()) {
            return boardColumnList.get(index);
        }
        return null;
    }

    public int getBoardColumnCount() {
        return boardColumnList.size();
    }

    public boolean hasBoardColumns() {
        return !boardColumnList.isEmpty();
    }

    public void sortBoardColumnsByIndex() {
        boardColumnList.sort((col1, col2) -> col1.getBcolmnIndex().compareTo(col2.getBcolmnIndex()));
    }

    public void initializeBoardColumns(int columnCount) {
        boardColumnList.clear();
        for (int i = 0; i < columnCount; i++) {
            BoardColumnDto column = new BoardColumnDto();
            column.setBcolmnIndex(i);
            column.setBcolmnGameFk(this.getGameId());
            boardColumnList.add(column);
        }
    }
    
    public void addCompletedSequence(CompletedSequenceDto completedSequence) {
        if (completedSequence != null) {
            completedSequence.setCseqGameFk(this.getGameId());
            completedSequenceList.add(completedSequence);
            updateCompletedSequencesCount();
        }
    }
    
    public void removeCompletedSequence(CompletedSequenceDto completedSequence) {
        completedSequenceList.remove(completedSequence);
        updateCompletedSequencesCount();
    }
    
    public void clearCompletedSequences() {
        completedSequenceList.clear();
        updateCompletedSequencesCount();
    }
    
    public CompletedSequenceDto getCompletedSequenceByOrder(Integer order) {
        return completedSequenceList.stream()
                .filter(seq -> seq.getCseqOrder().equals(order))
                .findFirst()
                .orElse(null);
    }
    
    public CompletedSequenceDto getCompletedSequenceById(Long id) {
        return completedSequenceList.stream()
                .filter(seq -> seq.getCseqId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    private void updateCompletedSequencesCount() {
        setGameCompletedSequences(completedSequenceList.size());
    }
    
    public String getFormattedDuration() {
        int totalSeconds = getGameDurationSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
   public boolean isGameInProgress() {
        return "ON".equals(getGameStatus());
    }
    
    public boolean isGameCompleted() {
        return "WON".equals(getGameStatus()) || "LOST".equals(getGameStatus());
    }
    
    public boolean isGamePaused() {
        return "SAVED".equals(getGameStatus());
    }
    
    public double getCompletionPercentage() {
        return (getGameCompletedSequences() / 8.0) * 100.0;
    }
    
    public void incrementMoveCount() {
        setGameMoveCount(getGameMoveCount() + 1);
    }
    
    public void incrementCompletedSequences() {
        setGameCompletedSequences(getGameCompletedSequences() + 1);
    }
    
    public void decrementDealsRemaining() {
        if (getGameDealsRemaining() > 0) {
            setGameDealsRemaining(getGameDealsRemaining() - 1);
        }
    }
    
    @Override
    public String toString() {
        return "GameDto{" + "id=" + gameId + ", status=" + gameStatus + 
               ", moves=" + gameMoveCount + ", points=" + gameTotalPoints + 
               ", duration=" + getFormattedDuration() + "}";
    }

}