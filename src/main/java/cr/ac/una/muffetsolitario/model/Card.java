/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
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
    private boolean cardFaceUp;
    @Basic(optional = false)
    @Column(name = "CARD_SUIT")
    private String cardSuit;
    @Basic(optional = false)
    @Column(name = "CARD_VALUE")
    private Integer cardValue;
    @Basic(optional = false)
    @Column(name = "CARD_POSITION_IN_CONTAINER")
    private Integer cardPositionInContainer;
    @Basic(optional = false)
    @Column(name = "CARD_VERSION")
    private Long cardVersion;
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

    public Card(Long cardId) {
        this.cardId = cardId;
    }

    public Card(Long cardId, boolean cardFaceUp, String cardSuit, Integer cardValue, Integer cardPositionInContainer) {
        this.cardId = cardId;
        this.cardFaceUp = cardFaceUp;
        this.cardSuit = cardSuit;
        this.cardValue = cardValue;
        this.cardPositionInContainer = cardPositionInContainer;
    }
    
    public Card(CardDto cardDto, EntityManager em) {
        update(cardDto);

        if (cardDto.getCardBcolmnId() != null) {
            this.cardBcolmnFk = em.getReference(BoardColumn.class, cardDto.getCardBcolmnId());
        } else if (cardDto.getCardCseqId() != null) {
            this.cardCseqFk = em.getReference(CompletedSequence.class, cardDto.getCardCseqId());
        } else if (cardDto.getCardDeckId() != null) {
            this.cardDeckFk = em.getReference(Deck.class, cardDto.getCardDeckId());
        }
    }

    public void update(CardDto cardDto){
        cardId = cardDto.getCardId();
        cardFaceUp = cardDto.isCardFaceUp();
        cardSuit = cardDto.getCardSuit();
        cardValue = cardDto.getCardValue();
        cardPositionInContainer = cardDto.getCardPositionInContainer();
        cardVersion = cardDto.getCardVersion();
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public boolean isCardFaceUp() {
        return cardFaceUp;
    }

    public void setCardFaceUp(boolean cardFaceUp) {
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

    public Long getCardVersion() {
        return cardVersion;
    }

    public void setCardVersion(Long cardVersion) {
        this.cardVersion = cardVersion;
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
        return "cr.ac.una.muffetsolitario.model.Card[ cardId=" + cardId + "cardFaceUp=" + cardFaceUp + " ]";
    }
}
