/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import java.io.Serializable;

public class UserAccountDto {
    
    private Long userId;
    private StringProperty userNickname;
    private StringProperty userPassword;
    private Serializable userCardImage;
    private IntegerProperty userCardDesign;
    private IntegerProperty userTotalGames;
    private IntegerProperty userWonGames;
    private IntegerProperty userTotalScore;
    private IntegerProperty userBestScore;
    private StringProperty userFavWord;
    private BooleanProperty userIsMusicActive;
    private Long userVersion;

    private boolean isUserGuest;
    private Long gameId;
    
    public UserAccountDto() {
        this.userNickname = new SimpleStringProperty();
        this.userPassword = new SimpleStringProperty();
        this.userCardDesign = new SimpleIntegerProperty();
        this.userTotalGames = new SimpleIntegerProperty();
        this.userWonGames = new SimpleIntegerProperty();
        this.userTotalScore = new SimpleIntegerProperty();
        this.userBestScore = new SimpleIntegerProperty();
        this.userFavWord = new SimpleStringProperty();
        this.userIsMusicActive = new SimpleBooleanProperty();
        this.setUserGuest(false);
        this.userVersion = 1L;
    }
    
    public UserAccountDto(Long userId) {
        this();
        setUserId(userId);
    }
    
    public UserAccountDto(Long userId, String userNickname, String userPassword, Integer userCardDesign) {
        this();
        setUserId(userId);
        setUserNickname(userNickname);
        setUserPassword(userPassword);
        setUserCardDesign(userCardDesign);
    }
    
    public UserAccountDto(UserAccount userAccount) {
        this();

        setUserId(userAccount.getUserId());
        setUserNickname(userAccount.getUserNickname());
        setUserPassword(userAccount.getUserPassword());
        setUserCardImage(userAccount.getUserCardImage());
        setUserCardDesign(userAccount.getUserCardDesign());
        setUserTotalGames(userAccount.getUserTotalGames() != null ? userAccount.getUserTotalGames() : 0);
        setUserWonGames(userAccount.getUserWonGames() != null ? userAccount.getUserWonGames() : 0);
        setUserTotalScore(userAccount.getUserTotalScore() != null ? userAccount.getUserTotalScore() : 0);
        setUserBestScore(userAccount.getUserBestScore() != null ? userAccount.getUserBestScore() : 0);
        setUserFavWord(userAccount.getUserFavWord() != null ? userAccount.getUserFavWord() : "-" );
        setUserIsMusicActive(userAccount.isMusicActive());
        userVersion = userAccount.getUserVersion() != null ? userAccount.getUserVersion() : 1L;
        
        if (userAccount.getGame() != null) {
            setGameId(userAccount.getGame().getGameId());
        }
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public StringProperty userNicknameProperty() {
        return userNickname;
    }
    
    public String getUserNickname() {
        return userNickname.get();
    }
    
    public void setUserNickname(String userNickname) {
        this.userNickname.set(userNickname);
    }
    
    public StringProperty userPasswordProperty() {
        return userPassword;
    }
    
    public String getUserPassword() {
        return userPassword.get();
    }
    
    public void setUserPassword(String userPassword) {
        this.userPassword.set(userPassword);
    }
    
    public Serializable getUserCardImage() {
        return userCardImage;
    }
    
    public void setUserCardImage(Serializable userCardImage) {
        this.userCardImage = userCardImage;
    }
    
    public IntegerProperty userCardDesignProperty() {
        return userCardDesign;
    }
    
    public Integer getUserCardDesign() {
        return userCardDesign.get();
    }
    
    public void setUserCardDesign(Integer userCardDesign) {
        this.userCardDesign.set(userCardDesign);
    }
    
    public IntegerProperty userTotalGamesProperty() {
        return userTotalGames;
    }
    
    public Integer getUserTotalGames() {
        return userTotalGames.get();
    }
    
    public void setUserTotalGames(Integer userTotalGames) {
        this.userTotalGames.set(userTotalGames);
    }
    
    public IntegerProperty userWonGamesProperty() {
        return userWonGames;
    }
    
    public Integer getUserWonGames() {
        return userWonGames.get();
    }
    
    public void setUserWonGames(Integer userWonGames) {
        this.userWonGames.set(userWonGames);
    }
    
    public IntegerProperty userTotalScoreProperty() {
        return userTotalScore;
    }
    
    public Integer getUserTotalScore() {
        return userTotalScore.get();
    }
    
    public void setUserTotalScore(Integer userTotalScore) {
        this.userTotalScore.set(userTotalScore);
    }
    
    public IntegerProperty userBestScoreProperty() {
        return userBestScore;
    }
    
    public Integer getUserBestScore() {
        return userBestScore.get();
    }
    
    public void setUserBestScore(Integer userBestScore) {
        this.userBestScore.set(userBestScore);
    }

    public StringProperty userFavWordProperty() {
        return userFavWord;
    }

    public String getUserFavWord() {
        return userFavWord.get();
    }

    public void setUserFavWord(String userFavWord) {
        this.userFavWord.set(userFavWord);
    }

    public BooleanProperty userIsMusicActiveProperty() {
        return userIsMusicActive;
    }

    public boolean isUserIsMusicActive() {
        return userIsMusicActive.get();
    }

    public void setUserIsMusicActive(boolean userIsMusicActive) {
        this.userIsMusicActive.set(userIsMusicActive);
    }

    public Long getUserVersion() {
        return userVersion;
    }

    public void setUserVersion(Long userVersion) {
        this.userVersion = userVersion;
    }

    public boolean isUserGuest() {
        return isUserGuest;
    }

    public void setUserGuest(boolean userGuest) {
        isUserGuest = userGuest;
    }

    public Long getGameId() {
        return gameId;
    }
    
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    
    // Utility methods for statistics
    public double getWinPercentage() {
        if (userTotalGames.get() == 0) {
            return 0.0;
        }
        return (double) userWonGames.get() / userTotalGames.get() * 100;
    }
    
    public double getAverageScore() {
        if (userTotalGames.get() == 0) {
            return 0.0;
        }
        return (double) userTotalScore.get() / userTotalGames.get();
    }
    
    @Override
    public String toString() {
        return "UserAccountDto{" + 
               "id=" + userId + 
               ", nickname=" + userNickname + 
               ", totalGames=" + userTotalGames + 
               ", wonGames=" + userWonGames + 
               ", bestScore=" + userBestScore + 
               "}";
    }
}
