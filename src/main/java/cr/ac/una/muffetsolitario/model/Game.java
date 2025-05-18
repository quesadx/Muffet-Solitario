/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author Al3xz
 */
@Entity
@Table(name = "GAME")
@NamedQueries({
    @NamedQuery(name = "Game.findAll", query = "SELECT g FROM Game g"),
    @NamedQuery(name = "Game.findByGameId", query = "SELECT g FROM Game g WHERE g.gameId = :gameId"),
    @NamedQuery(name = "Game.findByGameMoveCount", query = "SELECT g FROM Game g WHERE g.gameMoveCount = :gameMoveCount"),
    @NamedQuery(name = "Game.findByGameDifficulty", query = "SELECT g FROM Game g WHERE g.gameDifficulty = :gameDifficulty"),
    @NamedQuery(name = "Game.findByGameDurationSeconds", query = "SELECT g FROM Game g WHERE g.gameDurationSeconds = :gameDurationSeconds"),
    @NamedQuery(name = "Game.findByGameTotalPoints", query = "SELECT g FROM Game g WHERE g.gameTotalPoints = :gameTotalPoints")})
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "GAME_ID")
    private Long gameId;
    @Basic(optional = false)
    @Column(name = "GAME_MOVE_COUNT")
    private Integer gameMoveCount;
    @Basic(optional = false)
    @Column(name = "GAME_DIFFICULTY")
    private String gameDifficulty;
    @Basic(optional = false)
    @Column(name = "GAME_DURATION_SECONDS")
    private Integer gameDurationSeconds;
    @Basic(optional = false)
    @Column(name = "GAME_TOTAL_POINTS")
    private Integer gameTotalPoints;
    @JoinColumn(name = "GAME_USER_FK", referencedColumnName = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount gameUserFk;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deckGameFk", fetch = FetchType.LAZY)
    private List<Deck> deckList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cseqGameFk", fetch = FetchType.LAZY)
    private List<CompletedSequence> completedSequenceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bcolmnGameFk", fetch = FetchType.LAZY)
    private List<BoardColumn> boardColumnList;

    public Game() {
    }

    public Game(Long gameId) {
        this.gameId = gameId;
    }

    public Game(Long gameId, Integer gameMoveCount, String gameDifficulty, Integer gameDurationSeconds, Integer gameTotalPoints) {
        this.gameId = gameId;
        this.gameMoveCount = gameMoveCount;
        this.gameDifficulty = gameDifficulty;
        this.gameDurationSeconds = gameDurationSeconds;
        this.gameTotalPoints = gameTotalPoints;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getGameMoveCount() {
        return gameMoveCount;
    }

    public void setGameMoveCount(Integer gameMoveCount) {
        this.gameMoveCount = gameMoveCount;
    }

    public String getGameDifficulty() {
        return gameDifficulty;
    }

    public void setGameDifficulty(String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public Integer getGameDurationSeconds() {
        return gameDurationSeconds;
    }

    public void setGameDurationSeconds(Integer gameDurationSeconds) {
        this.gameDurationSeconds = gameDurationSeconds;
    }

    public Integer getGameTotalPoints() {
        return gameTotalPoints;
    }

    public void setGameTotalPoints(Integer gameTotalPoints) {
        this.gameTotalPoints = gameTotalPoints;
    }

    public UserAccount getGameUserFk() {
        return gameUserFk;
    }

    public void setGameUserFk(UserAccount gameUserFk) {
        this.gameUserFk = gameUserFk;
    }

    public List<Deck> getDeckList() {
        return deckList;
    }

    public void setDeckList(List<Deck> deckList) {
        this.deckList = deckList;
    }

    public List<CompletedSequence> getCompletedSequenceList() {
        return completedSequenceList;
    }

    public void setCompletedSequenceList(List<CompletedSequence> completedSequenceList) {
        this.completedSequenceList = completedSequenceList;
    }

    public List<BoardColumn> getBoardColumnList() {
        return boardColumnList;
    }

    public void setBoardColumnList(List<BoardColumn> boardColumnList) {
        this.boardColumnList = boardColumnList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gameId != null ? gameId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Game)) {
            return false;
        }
        Game other = (Game) object;
        if ((this.gameId == null && other.gameId != null) || (this.gameId != null && !this.gameId.equals(other.gameId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.Game[ gameId=" + gameId + " ]";
    }
    
}
