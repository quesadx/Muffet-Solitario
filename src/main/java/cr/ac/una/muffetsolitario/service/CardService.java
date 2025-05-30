/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.Card;
import cr.ac.una.muffetsolitario.model.CardDto;
import cr.ac.una.muffetsolitario.model.Game;
import cr.ac.una.muffetsolitario.util.EntityManagerHelper;
import cr.ac.una.muffetsolitario.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CardService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et; 
    
    public Respuesta getAllCardsByGame(Long gameId) {
        try {
            Query qryCards = em.createQuery(
                "SELECT c FROM Card c WHERE " +
                "(c.cardDeckId IS NOT NULL AND c.cardDeckId.deckGameId.gameId = :gameId) OR " +
                "(c.cardBcolmnId IS NOT NULL AND c.cardBcolmnId.bcolmnGameFk.gameId = :gameId) OR " +
                "(c.cardCseqId IS NOT NULL AND c.cardCseqId.cseqGameFk.gameId = :gameId)", 
                Card.class);
            qryCards.setParameter("gameId", gameId);
        
            List<Card> cards = qryCards.getResultList();
        
            List<CardDto> cardDtos = new ArrayList<>();
            for(Card card : cards) {
                cardDtos.add(new CardDto(card));
            }
        
            return new Respuesta(true, "Cards", cardDtos);
        
        } catch (Exception ex) {
            Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error obteniendo todas las cartas del juego [" + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo todas las cartas del juego.", "getAllCardsByGame " + ex.getMessage());
        }
    }
    
    public Respuesta getCardsByDeck(Long gameId) {
        try {
            Query qryCards = em.createQuery(
                "SELECT c FROM Card c " +
                "WHERE c.cardDeckId IS NOT NULL AND c.cardDeckId.deckGameId.gameId = :gameId " +
                "ORDER BY c.cardPositionInContainer", 
                Card.class);
            qryCards.setParameter("gameId", gameId);
    
            List<Card> cards = qryCards.getResultList();
    
            List<CardDto> cardDtos = new ArrayList<>();
            for(Card card : cards) {
                cardDtos.add(new CardDto(card));
            }
    
            return new Respuesta(true, "Cartas del deck", cardDtos);
    
        } catch (Exception ex) {
            Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error obteniendo cartas del deck [gameId: " + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo cartas del deck.", "getCardsByDeck " + ex.getMessage());
        }
    }

    public Respuesta getCardsByBoardColumn(Long gameId, Long boardColumnId) {
        try {
            Query qryCards = em.createQuery(
                "SELECT c FROM Card c " +
                "WHERE c.cardBcolmnId IS NOT NULL " +
                "AND c.cardBcolmnId.bcolmnGameFk.gameId = :gameId " +
                "AND c.cardBcolmnId.bcolmnId = :boardColumnId " +
                "ORDER BY c.cardPositionInContainer", 
                Card.class);
            qryCards.setParameter("gameId", gameId);
            qryCards.setParameter("boardColumnId", boardColumnId);
    
            List<Card> cards = qryCards.getResultList();
    
            List<CardDto> cardDtos = new ArrayList<>();
            for(Card card : cards) {
                cardDtos.add(new CardDto(card));
            }
    
            return new Respuesta(true, "Cartas de la columna " + boardColumnId, cardDtos);
    
        } catch (Exception ex) {
            Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error obteniendo cartas de la columna [gameId: " + gameId + ", columnId: " + boardColumnId + "]", ex);
            return new Respuesta(false, "Error obteniendo cartas de la columna.", "getCardsByBoardColumn " + ex.getMessage());
        }
    }

    public Respuesta getCardsByCompletedSequence(Long gameId, Long completedSequenceId) {
        try {
            Query qryCards = em.createQuery(
                "SELECT c FROM Card c " +
                "WHERE c.cardCseqId IS NOT NULL " +
                "AND c.cardCseqId.cseqGameFk.gameId = :gameId " +
                "AND c.cardCseqId.cseqId = :completedSequenceId " +
                "ORDER BY c.cardPositionInContainer", 
                Card.class);
            qryCards.setParameter("gameId", gameId);
            qryCards.setParameter("completedSequenceId", completedSequenceId);
    
            List<Card> cards = qryCards.getResultList();
    
            List<CardDto> cardDtos = new ArrayList<>();
            for(Card card : cards) {
                cardDtos.add(new CardDto(card));
            }
    
            return new Respuesta(true, "Cartas de la secuencia completada " + completedSequenceId, cardDtos);
    
        } catch (Exception ex) {
            Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error obteniendo cartas de la secuencia completada [gameId: " + gameId + ", sequenceId: " + completedSequenceId + "]", ex);
            return new Respuesta(false, "Error obteniendo cartas de la secuencia completada.", "getCardsByCompletedSequence " + ex.getMessage());
        }
    }
    
    public Respuesta deleteCardsByGame(Long gameId) {
        try {
            et = em.getTransaction();
            et.begin();
        
            Query qryDeleteAllCards = em.createQuery(
                "DELETE FROM Card c WHERE " +
                "(c.cardDeckId IS NOT NULL AND c.cardDeckId.deckGameId.gameId = :gameId) OR " +
                "(c.cardBcolmnId IS NOT NULL AND c.cardBcolmnId.bcolmnGameFk.gameId = :gameId) OR " +
                "(c.cardCseqId IS NOT NULL AND c.cardCseqId.cseqGameFk.gameId = :gameId)");
            qryDeleteAllCards.setParameter("gameId", gameId);
            int totalDeleted = qryDeleteAllCards.executeUpdate();
        
            et.commit();
        
            String mensaje = "Se eliminaron " + totalDeleted + " cartas de la partida.";
        
            return new Respuesta(true, mensaje, "eliminarCartasDePartida success");
        
        } catch (Exception ex) {
            if(et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error eliminando cartas de la partida [" + gameId + "]", ex);
            return new Respuesta(false, "Error eliminando las cartas de la partida.", "eliminarCartasDePartida " + ex.getMessage());
        }
    }
    
    public Respuesta saveCardsStateByGame(Long gameId, List<CardDto> cardsInDeck,
            List<CardDto> cardsInColumns,
            List<CardDto> cardsCsequences) {
    try {
        Game game = em.find(Game.class, gameId);
        if(game == null) {
            return new Respuesta(false, "No se encontró la partida especificada.", "saveCardsStateByGame GameNotFound");
        }
        
        et = em.getTransaction();
        et.begin();
         
        Query qryDeleteAllCards = em.createQuery(
            "DELETE FROM Card c WHERE " +
            "(c.cardDeckId IS NOT NULL AND c.cardDeckId.deckGameId.gameId = :gameId) OR " +
            "(c.cardBcolmnId IS NOT NULL AND c.cardBcolmnId.bcolmnGameFk.gameId = :gameId) OR " +
            "(c.cardCseqId IS NOT NULL AND c.cardCseqId.cseqGameFk.gameId = :gameId)");
        qryDeleteAllCards.setParameter("gameId", gameId);
        qryDeleteAllCards.executeUpdate();
        
        int totalSaved = 0;
        
        for(CardDto cardDto : cardsInDeck) {
            Card card = new Card(cardDto, em);
            em.persist(card);
            totalSaved++;
        }
        
        for(CardDto cardDto : cardsInColumns) {
            Card card = new Card(cardDto, em);
            em.persist(card);
            totalSaved++;
        }
        
        for(CardDto cardDto : cardsCsequences) {
            Card card = new Card(cardDto, em);
            em.persist(card);
            totalSaved++;
        }
        
        et.commit();
        
        return new Respuesta(true, "Estado del juego guardado. Total de cartas: " + totalSaved, "saveCardsStateByGame success");
        
    } catch (Exception ex) {
        if(et != null && et.isActive()) {
            et.rollback();
        }
        Logger.getLogger(CardService.class.getName()).log(Level.SEVERE, "Error guardando el estado de las cartas en el juego [" 
                + gameId + "]", ex);
        return new Respuesta(false, "Error guardando el estado de las cartas en el juego.", "saveCardsStateByGame " + ex.getMessage());
    }
}
    
}
