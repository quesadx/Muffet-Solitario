/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DeckDto {
    
    private Long deckId;
    private Long deckVersion;
    private Long deckGameId;
    private ObservableList<CardContainer> cardList;
    
    
    public DeckDto() {
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
    
    public DeckDto(Deck deck) {
        this();

        setDeckId(deck.getDeckId());
        setDeckGameId(deck.getDeckGameFk().getGameId());
        deckVersion = deck.getDeckVersion();

        if (deck.getCardList() != null) {
            for (Card card : deck.getCardList()) {
                CardDto cardDto = new CardDto(card);
                CardContainer container = new CardContainer();
                container.setCardDto(cardDto);
                this.cardList.add(container);
            }
        }
    }
    
    public Long getDeckId() {
        return deckId;
    }
    
    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public Long getDeckVersion() {
        return deckVersion;
    }

    public void setDeckVersion(Long deckVersion) {
        this.deckVersion = deckVersion;
    }
    
    public Long getDeckGameId() {
        return deckGameId;
    }
    
    public void setDeckGameId(Long deckGameId) {
        this.deckGameId = deckGameId;
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
            card.getCardDto().setCardDeckId(this.getDeckId());
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
    
    @Override
    public String toString() {
        return "DeckDto{" + "id=" + deckId + ", gameId=" + deckGameId + ", cardCount=" + getCardCount() + "}";
    }
}
