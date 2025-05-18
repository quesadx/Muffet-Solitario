/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
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
    @NamedQuery(name = "UserAccount.findByUserPassword", query = "SELECT u FROM UserAccount u WHERE u.userPassword = :userPassword")})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "USER_ID")
    private BigDecimal userId;
    @Basic(optional = false)
    @Column(name = "USER_NICKNAME")
    private String userNickname;
    @Basic(optional = false)
    @Column(name = "USER_PASSWORD")
    private String userPassword;
    @Lob
    @Column(name = "USER_CARD_IMAGE")
    private Serializable userCardImage;
    @OneToMany(mappedBy = "gameUserFk", fetch = FetchType.LAZY)
    private List<Game> gameList;

    public UserAccount() {
    }

    public UserAccount(BigDecimal userId) {
        this.userId = userId;
    }

    public UserAccount(BigDecimal userId, String userNickname, String userPassword) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
    }

    public BigDecimal getUserId() {
        return userId;
    }

    public void setUserId(BigDecimal userId) {
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

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
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
