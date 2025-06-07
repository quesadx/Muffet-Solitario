package cr.ac.una.muffetsolitario.model;

import javafx.beans.property.*;

public class CardDto {
    
    private Long cardId;
    private BooleanProperty cardFaceUp;
    private StringProperty cardSuit;
    private IntegerProperty cardValue;
    private IntegerProperty cardPositionInContainer;
    private Long cardVersion;
    
    private Long cardBcolmnId;
    private Long cardCseqId;
    private Long cardDeckId;
    
    private BooleanProperty selected;
    private BooleanProperty draggable;
    private BooleanProperty highlighted;
    
    public CardDto() {
        this.cardFaceUp = new SimpleBooleanProperty();
        this.cardSuit = new SimpleStringProperty();
        this.cardValue = new SimpleIntegerProperty();
        this.cardPositionInContainer = new SimpleIntegerProperty();
        this.cardVersion = 1L;
        this.selected = new SimpleBooleanProperty(false);
        this.draggable = new SimpleBooleanProperty(true);
        this.highlighted = new SimpleBooleanProperty(false);
    }
    
    public CardDto(Card card) {
       this();
       setCardId(card.getCardId());
       setCardFaceUp(card.isCardFaceUp());
       setCardSuit(card.getCardSuit());
       setCardValue(card.getCardValue());
       setCardPositionInContainer(card.getCardPositionInContainer());
       cardVersion = card.getCardVersion() != null ? card.getCardVersion() : 1L;
    }

    public Long getCardId() {
        return cardId;
    }
    
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
    
    public BooleanProperty cardFaceUpProperty() {
        return cardFaceUp;
    }
    
    public boolean isCardFaceUp() {
        return cardFaceUp.get();
    }
    
    public void setCardFaceUp(boolean cardFaceUp) {
        this.cardFaceUp.set(cardFaceUp);
    }
    
    public StringProperty cardSuitProperty() {
        return cardSuit;
    }
    
    public String getCardSuit() {
        return cardSuit.get();
    }
    
    public void setCardSuit(String cardSuit) {
        this.cardSuit.set(cardSuit);
    }
    
    public IntegerProperty cardValueProperty() {
        return cardValue;
    }
    
    public Integer getCardValue() {
        return cardValue.get();
    }
    
    public void setCardValue(Integer cardValue) {
        this.cardValue.set(cardValue);
    }
    
    public IntegerProperty cardPositionInContainerProperty() {
        return cardPositionInContainer;
    }
    
    public Integer getCardPositionInContainer() {
        return cardPositionInContainer.get();
    }
    
    public void setCardPositionInContainer(Integer cardPositionInContainer) {
        this.cardPositionInContainer.set(cardPositionInContainer);
    }

    public Long getCardVersion() {
        return cardVersion;
    }

    public void setCardVersion(Long cardVersion) {
        this.cardVersion = cardVersion;
    }
    
    // Properties only for UI
    public BooleanProperty selectedProperty() {
        return selected;
    }
    
    public boolean isSelected() {
        return selected.get();
    }
    
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
    
    public BooleanProperty draggableProperty() {
        return draggable;
    }
    
    public boolean isDraggable() {
        return draggable.get();
    }
    
    public void setDraggable(boolean draggable) {
        this.draggable.set(draggable);
    }
    
    public BooleanProperty highlightedProperty() {
        return highlighted;
    }
    
    public boolean isHighlighted() {
        return highlighted.get();
    }
    
    public void setHighlighted(boolean highlighted) {
        this.highlighted.set(highlighted);
    }
    
    public Long getCardBcolmnId() {
        return cardBcolmnId;
    }
    
    public void setCardBcolmnId(Long cardBcolmnId) {
        this.cardBcolmnId = cardBcolmnId;
    }
    
    public Long getCardCseqId() {
        return cardCseqId;
    }
    
    public void setCardCseqId(Long cardCseqId) {
        this.cardCseqId = cardCseqId;
    }
    
    public Long getCardDeckId() {
        return cardDeckId;
    }
    
    public void setCardDeckId(Long cardDeckId) {
        this.cardDeckId = cardDeckId;
    }
    
    @Override
    public String toString() {
        return "CardDto{" + "id=" + cardId + "value=" + cardValue + "suit=" + cardSuit + "}";
    }

}