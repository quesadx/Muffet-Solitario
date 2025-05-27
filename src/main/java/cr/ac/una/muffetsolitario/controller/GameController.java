/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.BoardColumn;
import cr.ac.una.muffetsolitario.model.Card;
import cr.ac.una.muffetsolitario.model.CardContainer;
import cr.ac.una.muffetsolitario.model.CardDto;
import cr.ac.una.muffetsolitario.model.Game;
import cr.ac.una.muffetsolitario.util.GameLogic;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author kendallbadilla
 */
public class GameController extends Controller implements Initializable {

    private Game currentGame;
    private GameLogic gameLogic;
    private List<Pane> columns;
    @FXML
    private Pane pnColumn0ne;
    @FXML
    private Pane pnColumnTwo;
    @FXML
    private Pane pnColumnThree;
    @FXML
    private Pane pnColumnFour;
    @FXML
    private Pane pnColumnFive;
    @FXML
    private Pane pnColumnSix;
    @FXML
    private Pane pnColumnSeven;
    @FXML
    private Pane pnColumnEight;
    @FXML
    private Pane pnColumnNine;
    @FXML
    private Pane pnColumnTen;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columns = List.of(
            pnColumn0ne, pnColumnTwo, pnColumnThree, pnColumnFour, pnColumnFive,
            pnColumnSix, pnColumnSeven, pnColumnEight, pnColumnNine, pnColumnTen);
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialize'");
    }

    public void setGame(Game game) {
        currentGame = game;
        // gameLogic= new
        // GameLogic(currentGame.getDeck(),currentGame.getBoardColumnList());//TODO:
        // Alex need fix logic in "Game" because we need only one deck, not a list
    }
    
    private void loadCardsImages(ObservableList<CardContainer> cardList){
        //method is only a test for EASY MODE, it need to be updated later to receive different suits
        //this method is to put all card face down
        for(int i = 0; i < 104; i++){
            CardDto cardDto = cardList.get(i).getCardDto();
            String imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Card_Back1.png";
            cardList.get(i).setImagePath(getClass().getResource(imagePath).toExternalForm());
        }
    }
    
    private void updateCardImage(CardContainer card){
        //this method is to update when a card change to face up
        String imagePath = "/cr/ac/una/muffetsolitario/resources/assets/Corazones/C_" + card.getCardDto().getCardValue() + ".png";
        card.setImagePath(imagePath);
        
    }

    private void renderBoard() {
        List<BoardColumn> boardColumns = currentGame.getBoardColumnList();
        double cardOffset = 30; // Distancia vertical entre cartas
        for (int i = 0; i < columns.size(); i++) {
            Pane pane = columns.get(i);
            pane.getChildren().clear();
            BoardColumn boardColumn = boardColumns.get(i);
            List<Card> cards = boardColumn.getCardList();
            for (int j = 0; j < cards.size(); j++) {
                Card card = cards.get(j);
               // CardContainer cardView = createCardImageView(card);
                //cardView.setLayoutY(j * cardOffset); // Escalonar verticalmente
                //pane.getChildren().add(cardView);
            }
        }
    }
    public void updateBoard(){
        renderBoard();
    }

     private Image getCardFrontImage(Card card) {
        return new Image("DIRECCION AQUI" + card.getCardSuit() + "_" + card.getCardValue() + ".png");
    }
    /*public void handlerMoveCards(int fromCol, int toCol, Card card) {
        gameLogic.moveCardsBetweenColumns(fromCol, toCol, card);
        updateBoard();
    }*/
}
