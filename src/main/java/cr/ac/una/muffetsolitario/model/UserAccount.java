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
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Al3xz
 */
@Entity
@Table(name = "USER_ACCOUNT")
@NamedQueries({
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u"),
    @NamedQuery(name = "UserAccount.findByUserId", query = "SELECT u FROM UserAccount u WHERE u.userId = :userId"),
    @NamedQuery(name = "UserAccount.findByUserNickname", query = "SELECT u FROM UserAccount u WHERE u.userNickname = :userNickname"),
    @NamedQuery(name = "UserAccount.findByUserPassword", query = "SELECT u FROM UserAccount u WHERE u.userPassword = :userPassword"),
    @NamedQuery(name = "UserAccount.findByUserCardDesign", query = "SELECT u FROM UserAccount u WHERE u.userCardDesign = :userCardDesign"),
    @NamedQuery(name = "UserAccount.findByUserTotalGames", query = "SELECT u FROM UserAccount u WHERE u.userTotalGames = :userTotalGames"),
    @NamedQuery(name = "UserAccount.findByUserWonGames", query = "SELECT u FROM UserAccount u WHERE u.userWonGames = :userWonGames"),
    @NamedQuery(name = "UserAccount.findByUserTotalScore", query = "SELECT u FROM UserAccount u WHERE u.userTotalScore = :userTotalScore"),
    @NamedQuery(name = "UserAccount.findByUserBestScore", query = "SELECT u FROM UserAccount u WHERE u.userBestScore = :userBestScore")})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "USER_ID")
    private Long userId;
    @Basic(optional = false)
    @Column(name = "USER_NICKNAME", unique = true)
    private String userNickname;
    @Basic(optional = false)
    @Column(name = "USER_PASSWORD")
    private String userPassword;
    @Lob
    @Column(name = "USER_CARD_IMAGE")
    private Serializable userCardImage;
    @Basic(optional = false)
    @Column(name = "USER_CARD_DESIGN")
    private Integer userCardDesign;
    @Column(name = "USER_TOTAL_GAMES")
    private Integer userTotalGames;
    @Column(name = "USER_WON_GAMES")
    private Integer userWonGames;
    @Column(name = "USER_TOTAL_SCORE")
    private Integer userTotalScore;
    @Column(name = "USER_BEST_SCORE")
    private Integer userBestScore;
    @OneToOne(mappedBy = "gameUserFk", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Game game;

    public UserAccount() {
    }

    public UserAccount(Long userId) {
        this.userId = userId;
    }

    public UserAccount(Long userId, String userNickname, String userPassword, Integer userCardDesign) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userCardDesign = userCardDesign;
    }
    
    public UserAccount(UserAccountDto userDto) {
        if (userDto != null) {
            this.userId = userDto.getUserId();
            this.userNickname = userDto.getUserNickname();
            this.userPassword = userDto.getUserPassword();
            this.userCardImage = userDto.getUserCardImage();
            this.userCardDesign = userDto.getUserCardDesign();
            this.userTotalGames = userDto.getUserTotalGames();
            this.userWonGames = userDto.getUserWonGames();
            this.userTotalScore = userDto.getUserTotalScore();
            this.userBestScore = userDto.getUserBestScore();
        }
    }

    public void update(UserAccountDto userDto) {
        if (userDto != null) {
            this.userNickname = userDto.getUserNickname();
            this.userPassword = userDto.getUserPassword();
            this.userCardImage = userDto.getUserCardImage();
            this.userCardDesign = userDto.getUserCardDesign();
            this.userTotalGames = userDto.getUserTotalGames();
            this.userWonGames = userDto.getUserWonGames();
            this.userTotalScore = userDto.getUserTotalScore();
            this.userBestScore = userDto.getUserBestScore();
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Serializable getUserCardImage() {
        return userCardImage;
    }

    public void setUserCardImage(Serializable userCardImage) {
        this.userCardImage = userCardImage;
    }

    public Integer getUserCardDesign() {
        return userCardDesign;
    }

    public void setUserCardDesign(Integer userCardDesign) {
        this.userCardDesign = userCardDesign;
    }

    public Integer getUserTotalGames() {
        return userTotalGames;
    }

    public void setUserTotalGames(Integer userTotalGames) {
        this.userTotalGames = userTotalGames;
    }

    public Integer getUserWonGames() {
        return userWonGames;
    }

    public void setUserWonGames(Integer userWonGames) {
        this.userWonGames = userWonGames;
    }

    public Integer getUserTotalScore() {
        return userTotalScore;
    }

    public void setUserTotalScore(Integer userTotalScore) {
        this.userTotalScore = userTotalScore;
    }

    public Integer getUserBestScore() {
        return userBestScore;
    }

    public void setUserBestScore(Integer userBestScore) {
        this.userBestScore = userBestScore;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserAccount)) {
            return false;
        }
        UserAccount other = (UserAccount) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.UserAccount[ userId=" + userId + " ]";
    }
    
}
