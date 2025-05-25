/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BoardColumnDto {
   
    private LongProperty bcolmnId;
    private IntegerProperty bcolmnIndex; 
    private Long bcolmnGameFk;
    private ObservableList<CardContainer> cardList;
    //the type could change to CardContainer instead of CardDto -> @Kendall!!
    //Or just added next to it as cardViews
    
    public BoardColumnDto() {
        this.bcolmnId = new SimpleLongProperty();
        this.bcolmnIndex = new SimpleIntegerProperty();
        this.cardList = FXCollections.observableArrayList();
    }
    
    public BoardColumnDto(Long bcolmnId, Integer bcolmnIndex) {
        this();
        setBcolmnId(bcolmnId);
        setBcolmnIndex(bcolmnIndex);
    }
    
    // Getters y Setters para Properties
    public LongProperty bcolmnIdProperty() {
        return bcolmnId;
    }
    
    public Long getBcolmnId() {
        return bcolmnId.get();
    }
    
    public void setBcolmnId(Long bcolmnId) {
        this.bcolmnId.set(bcolmnId);
    }
    
    public IntegerProperty bcolmnIndexProperty() {
        return bcolmnIndex;
    }
    
    public Integer getBcolmnIndex() {
        return bcolmnIndex.get();
    }
    
    public void setBcolmnIndex(Integer bcolmnIndex) {
        this.bcolmnIndex.set(bcolmnIndex);
    }
    
    // Getter y Setter simple para ID de relación
    public Long getBcolmnGameFk() {
        return bcolmnGameFk;
    }
    
    public void setBcolmnGameFk(Long bcolmnGameFk) {
        this.bcolmnGameFk = bcolmnGameFk;
    }
    
    // Getter y Setter para lista observable de cartas
    public ObservableList<CardContainer> getCardList() {
        return cardList;
    }
    
    public void setCardList(ObservableList<CardContainer> cardList) {
        this.cardList = cardList;
    }
    
    //methods useful for game UI data
    public boolean isEmpty() {
        return cardList == null || cardList.isEmpty();
    }
    
    public int getCardCount() {
        return cardList != null ? cardList.size() : 0;
    }
    
    public CardContainer getTopCard() {
        if (isEmpty()) {
            return null;
        }
        return cardList.get(cardList.size() - 1);
    }
    
    public CardContainer getBottomCard() {
        if (isEmpty()) {
            return null;
        }
        return cardList.get(0);
    }
    
    public void addCard(CardContainer card) {
        if (cardList != null && card != null) {
            cardList.add(card);
        }
    }
    
    public CardContainer removeTopCard() {
        if (isEmpty()) {
            return null;
        }
        return cardList.remove(cardList.size() - 1);
    }
    
    public void clearColumn() {
        if (cardList != null) {
            cardList.clear();
        }
    }
    
    @Override
    public String toString() {
        return "BoardColumnDTO{" + "id=" + getBcolmnId() + ", index=" + getBcolmnIndex() + 
               ", cardCount=" + getCardCount() + "}";
    }
}