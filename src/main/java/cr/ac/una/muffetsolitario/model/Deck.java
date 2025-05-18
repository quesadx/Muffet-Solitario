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
@Table(name = "DECK")
@NamedQueries({
    @NamedQuery(name = "Deck.findAll", query = "SELECT d FROM Deck d"),
    @NamedQuery(name = "Deck.findByDeckId", query = "SELECT d FROM Deck d WHERE d.deckId = :deckId")})
public class Deck implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "DECK_ID")
    private BigDecimal deckId;
    @JoinColumn(name = "DECK_GAME_FK", referencedColumnName = "GAME_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Game deckGameFk;
    @OneToMany(mappedBy = "cardDeckFk", fetch = FetchType.LAZY)
    private List<Card> cardList;

    public Deck() {
    }

    public Deck(BigDecimal deckId) {
        this.deckId = deckId;
    }

    public BigDecimal getDeckId() {
        return deckId;
    }

    public void setDeckId(BigDecimal deckId) {
        this.deckId = deckId;
    }

    public Game getDeckGameFk() {
        return deckGameFk;
    }

    public void setDeckGameFk(Game deckGameFk) {
        this.deckGameFk = deckGameFk;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deckId != null ? deckId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Deck)) {
            return false;
        }
        Deck other = (Deck) object;
        if ((this.deckId == null && other.deckId != null) || (this.deckId != null && !this.deckId.equals(other.deckId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.Deck[ deckId=" + deckId + " ]";
    }
    
}
