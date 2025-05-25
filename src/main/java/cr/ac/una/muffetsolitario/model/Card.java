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

/**
 *
 * @author Al3xz
 */
@Entity
@Table(name = "CARD")
@NamedQueries({
    @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c"),
    @NamedQuery(name = "Card.findByCardId", query = "SELECT c FROM Card c WHERE c.cardId = :cardId"),
    @NamedQuery(name = "Card.findByCardFaceUp", query = "SELECT c FROM Card c WHERE c.cardFaceUp = :cardFaceUp"),
    @NamedQuery(name = "Card.findByCardSuit", query = "SELECT c FROM Card c WHERE c.cardSuit = :cardSuit"),
    @NamedQuery(name = "Card.findByCardValue", query = "SELECT c FROM Card c WHERE c.cardValue = :cardValue"),
    @NamedQuery(name = "Card.findByCardPositionInContainer", query = "SELECT c FROM Card c WHERE c.cardPositionInContainer = :cardPositionInContainer")})
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "CARD_ID")
    private Long cardId;
    @Basic(optional = false)
    @Column(name = "CARD_FACE_UP")
    private short cardFaceUp;
    @Basic(optional = false)
    @Column(name = "CARD_SUIT")
    private String cardSuit;
    @Basic(optional = false)
    @Column(name = "CARD_VALUE")
    private Integer cardValue;
    @Basic(optional = false)
    @Column(name = "CARD_POSITION_IN_CONTAINER")
    private Integer cardPositionInContainer;
    @JoinColumn(name = "CARD_BCOLMN_ID", referencedColumnName = "BCOLMN_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardColumn cardBcolmnId;
    @JoinColumn(name = "CARD_CSEQ_ID", referencedColumnName = "CSEQ_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompletedSequence cardCseqId;
    @JoinColumn(name = "CARD_DECK_ID", referencedColumnName = "DECK_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deck cardDeckId;

    public Card() {
    }

    public Card(Long cardId) {
        this.cardId = cardId;
    }

    public Card(Long cardId, short cardFaceUp, String cardSuit, Integer cardValue, Integer cardPositionInContainer) {
        this.cardId = cardId;
        this.cardFaceUp = cardFaceUp;
        this.cardSuit = cardSuit;
        this.cardValue = cardValue;
        this.cardPositionInContainer = cardPositionInContainer;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public short isCardFaceUp() {
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

    public Integer getCardValue() {
        return cardValue;
    }

    public void setCardValue(Integer cardValue) {
        this.cardValue = cardValue;
    }

    public Integer getCardPositionInContainer() {
        return cardPositionInContainer;
    }

    public void setCardPositionInContainer(Integer cardPositionInContainer) {
        this.cardPositionInContainer = cardPositionInContainer;
    }

    public BoardColumn getCardBcolmnId() {
        return cardBcolmnId;
    }

    public void setCardBcolmnId(BoardColumn cardBcolmnId) {
        this.cardBcolmnId = cardBcolmnId;
    }

    public CompletedSequence getCardCseqId() {
        return cardCseqId;
    }

    public void setCardCseqId(CompletedSequence cardCseqId) {
        this.cardCseqId = cardCseqId;
    }

    public Deck getCardDeckId() {
        return cardDeckId;
    }

    public void setCardDeckId(Deck cardDeckId) {
        this.cardDeckId = cardDeckId;
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
