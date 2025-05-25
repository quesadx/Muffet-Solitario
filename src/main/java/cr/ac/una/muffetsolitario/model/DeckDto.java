/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DeckDto {
    
    private LongProperty deckId;
    private Long deckGameId;
    private ObservableList<CardDto> cardList;
    //Also could change to type CardContainer instead of CardDto
    
    public DeckDto() {
        this.deckId = new SimpleLongProperty();
        this.cardList = FXCollections.observableArrayList();
    }
    
    public DeckDto(Long deckId) {
        this();
        setDeckId(deckId);
    }
    
    public DeckDto(Long deckId, Long deckGameId) {
        this();
        setDeckId(deckId);
        setDeckGameId(deckGameId);
    }
    
    public LongProperty deckIdProperty() {
        return deckId;
    }
    
    public Long getDeckId() {
        return deckId.get();
    }
    
    public void setDeckId(Long deckId) {
        this.deckId.set(deckId);
    }
    
    public Long getDeckGameId() {
        return deckGameId;
    }
    
    public void setDeckGameId(Long deckGameId) {
        this.deckGameId = deckGameId;
    }
    
    public ObservableList<CardDto> getCardList() {
        return cardList;
    }
    
    public void setCardList(ObservableList<CardDto> cardList) {
        this.cardList = cardList;
    }
    
    // Utility methods for card management
    public void addCard(CardDto card) {
        if (card != null) {
            card.setCardDeckId(this.getDeckId());
            this.cardList.add(card);
        }
    }
    
    public void removeCard(CardDto card) {
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
    
    @Override
    public String toString() {
        return "DeckDto{" + "id=" + deckId + ", gameId=" + deckGameId + ", cardCount=" + getCardCount() + "}";
    }
}
