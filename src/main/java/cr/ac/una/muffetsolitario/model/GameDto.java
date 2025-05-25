package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameDto {
    
    private LongProperty gameId;
    private IntegerProperty gameCompletedSequences;
    private IntegerProperty gameDealsRemaining;
    private IntegerProperty gameMoveCount;
    private StringProperty gameDifficulty;
    private IntegerProperty gameDurationSeconds;
    private IntegerProperty gameTotalPoints;
    private StringProperty gameStatus;
    private ObjectProperty<LocalDate> gameCreatedDate;
    private ObjectProperty<LocalDate> gameLastPlayed;
    private DeckDto deckDto;
    private ObservableList<BoardColumnDto> boardColumnList;
    private Long gameUserFk;
    
    public GameDto() {
        this.gameId = new SimpleLongProperty();
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
    
    public LongProperty gameIdProperty() {
        return gameId;
    }
    
    public Long getGameId() {
        return gameId.get();
    }
    
    public void setGameId(Long gameId) {
        this.gameId.set(gameId);
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
    
    public ObjectProperty<LocalDate> gameCreatedDateProperty() {
        return gameCreatedDate;
    }
    
    public LocalDate getGameCreatedDate() {
        return gameCreatedDate.get();
    }
    
    public void setGameCreatedDate(LocalDate gameCreatedDate) {
        this.gameCreatedDate.set(gameCreatedDate);
    }
    
    public ObjectProperty<LocalDate> gameLastPlayedProperty() {
        return gameLastPlayed;
    }
    
    public LocalDate getGameLastPlayed() {
        return gameLastPlayed.get();
    }
    
    public void setGameLastPlayed(LocalDate gameLastPlayed) {
        this.gameLastPlayed.set(gameLastPlayed);
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
    
    public Long getGameUserFk() {
        return gameUserFk;
    }
    
    public void setGameUserFk(Long gameUserFk) {
        this.gameUserFk = gameUserFk;
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