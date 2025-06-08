/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.*;
import cr.ac.una.muffetsolitario.util.EntityManagerHelper;
import cr.ac.una.muffetsolitario.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    private void mapGameRelationsFromDto(Game game, GameDto gameDto, EntityManager em) {
        int totalCardsPersisted = 0;

        // --- Deck ---
        if (gameDto.getDeckDto() != null) {
            Deck deck;
            if (gameDto.getDeckDto().getDeckId() != null) {
                deck = em.find(Deck.class, gameDto.getDeckDto().getDeckId());
            } else {
                deck = new Deck();
                deck.setDeckVersion(gameDto.getDeckDto().getDeckVersion());
            }
            deck.setDeckGameFk(game);

            // --- Cards from Deck ---
            if (gameDto.getDeckDto().getCardList() != null) {
                List<Card> deckCards = new ArrayList<>();
                for (CardContainer cardContainer : gameDto.getDeckDto().getCardList()) {
                    CardDto cardDto = cardContainer.getCardDto();
                    Card card;
                    if (cardDto.getCardId() != null) {
                        card = em.find(Card.class, cardDto.getCardId());
                    } else {
                        card = new Card();
                        card.setCardFaceUp(cardDto.isCardFaceUp());
                        card.setCardSuit(cardDto.getCardSuit());
                        card.setCardValue(cardDto.getCardValue());
                        card.setCardPositionInContainer(cardDto.getCardPositionInContainer());
                        card.setCardVersion(cardDto.getCardVersion());
                        // ...asigna otros campos si es necesario
                    }
                    card.setCardDeckFk(deck); // Relación inversa
                    deckCards.add(card);
                }
                deck.setCardList(deckCards);
                totalCardsPersisted += deckCards.size();
            }
            game.setDeck(deck);
        }

        // --- CompletedSequences ---
        if (gameDto.getCompletedSequenceList() != null) {
            List<CompletedSequence> completedSequences = new ArrayList<>();
            for (CompletedSequenceDto csDto : gameDto.getCompletedSequenceList()) {
                CompletedSequence cs;
                if (csDto.getCseqId() != null) {
                    cs = em.find(CompletedSequence.class, csDto.getCseqId());
                } else {
                    cs = new CompletedSequence();
                    cs.setCseqOrder(csDto.getCseqOrder());
                    cs.setCseqVersion(csDto.getCseqVersion());
                }
                cs.setCseqGameFk(game);

                // --- Cards From Completed Sequence ---
                if (csDto.getCardList() != null) {
                    List<Card> csCards = new ArrayList<>();
                    for (CardContainer cardContainer : csDto.getCardList()) {
                        CardDto cardDto = cardContainer.getCardDto();
                        Card card;
                        if (cardDto.getCardId() != null) {
                            card = em.find(Card.class, cardDto.getCardId());
                        } else {
                            card = new Card();
                            card.setCardFaceUp(cardDto.isCardFaceUp());
                            card.setCardSuit(cardDto.getCardSuit());
                            card.setCardValue(cardDto.getCardValue());
                            card.setCardPositionInContainer(cardDto.getCardPositionInContainer());
                            card.setCardVersion(cardDto.getCardVersion());
                            // ...asigna otros campos si es necesario
                        }
                        card.setCardCseqFk(cs); // Relación inversa
                        csCards.add(card);
                    }
                    cs.setCardList(csCards);
                    totalCardsPersisted += csCards.size();
                }
                completedSequences.add(cs);
            }
            game.setCompletedSequenceList(completedSequences);
        }

        // --- BoardColumns ---
        if (gameDto.getBoardColumnList() != null) {
            List<BoardColumn> columns = new ArrayList<>();
            for (BoardColumnDto colDto : gameDto.getBoardColumnList()) {
                BoardColumn col;
                if (colDto.getBcolmnId() != null) {
                    col = em.find(BoardColumn.class, colDto.getBcolmnId());
                } else {
                    col = new BoardColumn();
                    col.setBcolmnIndex(colDto.getBcolmnIndex());
                    col.setBcolmnVersion(colDto.getBcolmnVersion());
                }
                col.setBcolmnGameFk(game); // Relación inversa

                // --- Cards From BoardColumn ---
                if (colDto.getCardList() != null) {
                    List<Card> colCards = new ArrayList<>();
                    for (CardContainer cardContainer : colDto.getCardList()) {
                        CardDto cardDto = cardContainer.getCardDto();
                        Card card;
                        if (cardDto.getCardId() != null) {
                            card = em.find(Card.class, cardDto.getCardId());
                        } else {
                            card = new Card();
                            card.setCardFaceUp(cardDto.isCardFaceUp());
                            card.setCardSuit(cardDto.getCardSuit());
                            card.setCardValue(cardDto.getCardValue());
                            card.setCardPositionInContainer(cardDto.getCardPositionInContainer());
                            card.setCardVersion(cardDto.getCardVersion());

                        }
                        card.setCardBcolmnFk(col);
                        colCards.add(card);
                    }
                    col.setCardList(colCards);
                    totalCardsPersisted += colCards.size();
                }
                columns.add(col);
            }
            game.setBoardColumnList(columns);
        }

        System.out.println("Total cartas asociadas a persistir: " + totalCardsPersisted);
    }

    public Respuesta saveGameDto(GameDto gameDto) {
        try {
            et = em.getTransaction();
            et.begin();

            Game game;
            boolean isUpdate = false;

            UserAccount userAccount = null;
            if (gameDto.getGameUserFk() != null) {
                userAccount = em.find(UserAccount.class, gameDto.getGameUserFk());
                if (userAccount == null) {
                    et.rollback();
                    return new Respuesta(false, "Usuario no encontrado para asociar al juego", "saveGame UserNotFound");
                }
            }

            if (gameDto.getGameId() != null) {
                game = em.find(Game.class, gameDto.getGameId());
                if (game == null) {
                    et.rollback();
                    return new Respuesta(false, "Juego no encontrado para actualizar", "saveGame GameNotFound");
                }

                game.update(gameDto);
                // Set relation with UserAccount if is necessary
                if (userAccount != null) {
                    game.setGameUserFk(userAccount);
                    userAccount.setGame(game); //keep relation bidirectional
                }
                mapGameRelationsFromDto(game, gameDto, em);
                game = em.merge(game);
                isUpdate = true;

            } else {
                game = new Game(gameDto);
                if (userAccount != null) {
                    game.setGameUserFk(userAccount);
                    userAccount.setGame(game);
                }
                mapGameRelationsFromDto(game, gameDto, em);

                em.persist(game);
            }

            et.commit();

            String message = isUpdate ? "Juego actualizado exitosamente" : "Juego creado exitosamente";
            return new Respuesta(true, message, "saveGame success", "Game", new GameDto(game));

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, "Error guardando juego", ex);
            return new Respuesta(false, "Error guardando el juego", "saveGame " + ex.getMessage());
        }
    }

    public Respuesta getGameById(Long gameId) {
        try {
            Query qryGame = em.createNamedQuery("Game.findByGameId");
            qryGame.setParameter("gameId", gameId);

            Game game = (Game) qryGame.getSingleResult();
            GameDto gameDto = new GameDto(game);

            return new Respuesta(true, "Juego encontrado", "getGameById success", "Game", gameDto);
            
        } catch(NoResultException ex) {
            return new Respuesta(false, "No existe el juego con el ID ingresado", "getGameById GameNotFound");
        } catch (Exception ex) {
            Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, "Error obteniendo juego por ID [" + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo el juego", "getGameById " + ex.getMessage());
        }
    }

    public Respuesta getGameByUserId(Long userId) {
        try {
            Query query = em.createQuery("SELECT g FROM Game g WHERE g.gameUserFk.userId = :userId");
            query.setParameter("userId", userId);

            Game game = (Game) query.getSingleResult();
            GameDto gameDto = new GameDto(game);

            return new Respuesta(true, "Juego del usuario encontrado", "getGameByUserId success", "Game", gameDto);

        } catch (NoResultException ex) {
            return new Respuesta(false, "El usuario no tiene juegos", "getGameByUserId NoGame");
        } catch (Exception ex) {
            Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, "Error obteniendo juego por usuario [" + userId + "]", ex);
            return new Respuesta(false, "Error obteniendo el juego del usuario", "getGameByUserId " + ex.getMessage());
        }
    }
    
    public Respuesta deleteGame(Long gameId) {
        try {
            et = em.getTransaction();
            et.begin();
            
            Game game = em.find(Game.class, gameId);
            if (game == null) {
                et.rollback();
                return new Respuesta(false, "Juego no encontrado para eliminar", "deleteGame GameNotFound");
            }
            
            em.remove(game);
            et.commit();
            
            return new Respuesta(true, "Juego eliminado exitosamente", "deleteGame success");
            
        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, "Error eliminando juego [" + gameId + "]", ex);
            return new Respuesta(false, "Error eliminando el juego", "deleteGame " + ex.getMessage());
        }
    }
}