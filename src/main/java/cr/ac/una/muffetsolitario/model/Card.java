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
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Al3xz
 */
@Entity
@Table(name = "CARD")
@NamedQueries({
    @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c"),
    @NamedQuery(name = "Card.findByCardId", query = "SELECT c FROM Card c WHERE c.cardId = :cardId"),
    @NamedQuery(name = "Card.findByCardIndex", query = "SELECT c FROM Card c WHERE c.cardIndex = :cardIndex"),
    @NamedQuery(name = "Card.findByCardFaceUp", query = "SELECT c FROM Card c WHERE c.cardFaceUp = :cardFaceUp"),
    @NamedQuery(name = "Card.findByCardSuit", query = "SELECT c FROM Card c WHERE c.cardSuit = :cardSuit"),
    @NamedQuery(name = "Card.findByCardValue", query = "SELECT c FROM Card c WHERE c.cardValue = :cardValue")})
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "CARD_ID")
    private BigDecimal cardId;
    @Basic(optional = false)
    @Column(name = "CARD_INDEX")
    private String cardIndex;
    @Basic(optional = false)
    @Column(name = "CARD_FACE_UP")
    private short cardFaceUp;
    @Basic(optional = false)
    @Column(name = "CARD_SUIT")
    private String cardSuit;
    @Basic(optional = false)
    @Column(name = "CARD_VALUE")
    private BigInteger cardValue;
    @JoinColumn(name = "CARD_BCOLMN_FK", referencedColumnName = "BCOLMN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardColumn cardBcolmnFk;
    @JoinColumn(name = "CARD_CSEQ_FK", referencedColumnName = "CSEQ_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompletedSequence cardCseqFk;
    @JoinColumn(name = "CARD_DECK_FK", referencedColumnName = "DECK_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deck cardDeckFk;

    public Card() {
    }

    public Card(BigDecimal cardId) {
        this.cardId = cardId;
    }

    public Card(BigDecimal cardId, String cardIndex, short cardFaceUp, String cardSuit, BigInteger cardValue) {
        this.cardId = cardId;
        this.cardIndex = cardIndex;
        this.cardFaceUp = cardFaceUp;
        this.cardSuit = cardSuit;
        this.cardValue = cardValue;
    }

    public BigDecimal getCardId() {
        return cardId;
    }

    public void setCardId(BigDecimal cardId) {
        this.cardId = cardId;
    }

    public String getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(String cardIndex) {
        this.cardIndex = cardIndex;
    }

    public short getCardFaceUp() {
        return cardFaceUp;
    }

    public void setCardFaceUp(short cardFaceUp) {
        this.cardFaceUp = cardFaceUp;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(String cardSuit) {
        this.cardSuit = cardSuit;
    }

    public BigInteger getCardValue() {
        return cardValue;
    }

    public void setCardValue(BigInteger cardValue) {
        this.cardValue = cardValue;
    }

    public BoardColumn getCardBcolmnFk() {
        return cardBcolmnFk;
    }

    public void setCardBcolmnFk(BoardColumn cardBcolmnFk) {
        this.cardBcolmnFk = cardBcolmnFk;
    }

    public CompletedSequence getCardCseqFk() {
        return cardCseqFk;
    }

    public void setCardCseqFk(CompletedSequence cardCseqFk) {
        this.cardCseqFk = cardCseqFk;
    }

    public Deck getCardDeckFk() {
        return cardDeckFk;
    }

    public void setCardDeckFk(Deck cardDeckFk) {
        this.cardDeckFk = cardDeckFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardId != null ? cardId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Card)) {
            return false;
        }
        Card other = (Card) object;
        if ((this.cardId == null && other.cardId != null) || (this.cardId != null && !this.cardId.equals(other.cardId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.Card[ cardId=" + cardId + " ]";
    }
    
}
