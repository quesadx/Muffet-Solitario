/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.*;

import java.io.Serializable;
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
    private Long deckId;
    @JoinColumn(name = "DECK_GAME_ID", referencedColumnName = "GAME_ID", unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    private Game deckGameId;

    @OneToMany(mappedBy = "cardDeckId", fetch = FetchType.LAZY)
    private List<Card> cardList;

    public Deck() {
    }

    public Deck(Long deckId) {
        this.deckId = deckId;
    }

    public Deck(DeckDto deckDto, EntityManager em) {
        deckId = deckDto.getDeckId();

        if (deckDto.getDeckGameId() != null) {
            deckGameId = em.getReference(Game.class, deckDto.getDeckGameId());
        }
    }

    public void update(DeckDto deckDto, EntityManager em) {
        deckId = deckDto.getDeckId();
        if (deckDto.getDeckGameId() != null) {
            deckGameId = em.getReference(Game.class, deckDto.getDeckGameId());
        }
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public Game getDeckGameId() {
        return deckGameId;
    }

    public void setDeckGameId(Game deckGameId) {
        this.deckGameId = deckGameId;
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
