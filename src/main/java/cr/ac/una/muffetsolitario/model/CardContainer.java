/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 *
 * @author kendallbadilla
 */
public class CardContainer extends ImageView {
    private CardDto cardDto;
    private String imagePath;
    private Image cardImage;

    public CardContainer() {}

    public CardContainer(CardDto cardDto, Image image, String imagePath) {
        super(image);
        this.cardDto = cardDto;
        this.imagePath = imagePath;
        setFitHeight(120);
        setFitHeight(160);
    }

    public CardDto getCardDto() {
        return cardDto;
    }

    public void setCardDto(CardDto cardDto) {
        this.cardDto = cardDto;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        updateImage();
    }

    public void updateImage() {
        cardImage = new Image(imagePath);
    }
    
    public Image getCardImage(){
        return cardImage;
    }
    
    public void setCardImage(Image cardImage){
        this.cardImage = cardImage;
    }
    
}
