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
    private Card card;

    public CardContainer(){}

    public CardContainer(Card card, Image image) {
        super(image);
        this.card = card;
        setFitWidth(80); // Tamaño estándar, ajusta según tu diseño
        setFitHeight(120);
        setPreserveRatio(true);
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void updateImage(Image image) {
        setImage(image);
    }
}
