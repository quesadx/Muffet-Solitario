/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CompletedSequenceDto {
    
    private LongProperty cseqId;
    private IntegerProperty cseqOrder;
    private Long cseqGameFk;
    private ObservableList<CardContainer> cardList;
    
    public CompletedSequenceDto() {
        this.cseqId = new SimpleLongProperty();
        this.cseqOrder = new SimpleIntegerProperty();
        this.cardList = FXCollections.observableArrayList();
    }
    
    public CompletedSequenceDto(Long cseqId) {
        this();
        setCseqId(cseqId);
    }
    
    public CompletedSequenceDto(Long cseqId, Integer cseqOrder, Long cseqGameFk) {
        this();
        setCseqId(cseqId);
        setCseqOrder(cseqOrder);
        setCseqGameFk(cseqGameFk);
    }
    
    public CompletedSequenceDto(CompletedSequence completedSequence) {
        this();
        if (completedSequence != null) {
            setCseqId(completedSequence.getCseqId());
            setCseqOrder(completedSequence.getCseqOrder());
            if (completedSequence.getCseqGameFk() != null) {
                setCseqGameFk(completedSequence.getCseqGameFk().getGameId());
            }
            if (completedSequence.getCardList() != null) {
                for (Card card : completedSequence.getCardList()) {
                    CardDto cardDto = new CardDto(card);
                    CardContainer container = new CardContainer();
                    container.setCardDto(cardDto);
                    this.cardList.add(container);
                }
            }
        }
    }
    
    public LongProperty cseqIdProperty() {
        return cseqId;
    }
    
    public Long getCseqId() {
        return cseqId.get();
    }
    
    public void setCseqId(Long cseqId) {
        this.cseqId.set(cseqId);
    }
    
    public IntegerProperty cseqOrderProperty() {
        return cseqOrder;
    }
    
    public Integer getCseqOrder() {
        return cseqOrder.get();
    }
    
    public void setCseqOrder(Integer cseqOrder) {
        this.cseqOrder.set(cseqOrder);
    }
    
    public Long getCseqGameFk() {
        return cseqGameFk;
    }
    
    public void setCseqGameFk(Long cseqGameFk) {
        this.cseqGameFk = cseqGameFk;
    }
    
    public ObservableList<CardContainer> getCardList() {
        return cardList;
    }
    
    public void setCardList(ObservableList<CardContainer> cardList) {
        this.cardList = cardList;
    }
    
    // Utility methods for card management
    public void addCard(CardContainer card) {
        if (card != null) {
            card.getCardDto().setCardCseqId(this.getCseqId());
            this.cardList.add(card);
        }
    }
    
    public void removeCard(CardContainer card) {
        this.cardList.remove(card);
    }
    
    public void clearCards() {
        this.cardList.clear();
    }
    
    public int getCardCount() {
        return cardList.size();
    }
    
    public boolean isEmpty() {
        return cardList.isEmpty();
    }
    
    public boolean isComplete() {
        return cardList.size() == 13;
    }
    
    public CardContainer getTopCard() {
        if (cardList.isEmpty()) {
            return null;
        }
        return cardList.get(cardList.size() - 1);
    }
    
    public CardContainer getBottomCard() {
        if (cardList.isEmpty()) {
            return null;
        }
        return cardList.get(0);
    }
    
    @Override
    public String toString() {
        return "CompletedSequenceDto{" + 
               "id=" + cseqId + 
               ", order=" + cseqOrder + 
               ", gameId=" + cseqGameFk + 
               ", cardCount=" + getCardCount() + 
               "}";
    }
}