package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.Deck;
import cr.ac.una.muffetsolitario.model.DeckDto;
import cr.ac.una.muffetsolitario.util.EntityManagerHelper;
import cr.ac.una.muffetsolitario.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeckService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getDeckById(Long id) {
        try {
            Query qryDeck = em.createNamedQuery("Deck.findByDeckId", Deck.class);
            qryDeck.setParameter("deckId", id);

            Deck deck = (Deck) qryDeck.getSingleResult();
            DeckDto deckDto = new DeckDto(deck);

            return new Respuesta(true, "Deck encontrado", "getDeckById success", "Deck", deckDto);

        } catch (NoResultException ex) {
            return new Respuesta(false, "Deck no encontrado", "getDeckById NoResultException");
        } catch (Exception ex) {
            Logger.getLogger(DeckService.class.getName()).log(Level.SEVERE, "Error obteniendo deck [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el deck", "getDeckById " + ex.getMessage());
        }
    }

    public Respuesta getDeckByGameId(Long gameId) {
        try {
            Query qryDeck = em.createQuery("SELECT d FROM Deck d WHERE d.deckGameId.gameId = :gameId", Deck.class);
            qryDeck.setParameter("gameId", gameId);

            Deck deck = (Deck) qryDeck.getSingleResult();
            DeckDto deckDto = new DeckDto(deck);

            return new Respuesta(true, "Deck encontrado", "getDeckByGameId success", "Deck", deckDto);

        } catch (NoResultException ex) {
            return new Respuesta(false, "Deck no encontrado para la partida", "getDeckByGameId NoResultException");
        } catch (Exception ex) {
            Logger.getLogger(DeckService.class.getName()).log(Level.SEVERE, "Error obteniendo deck por gameId [" + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo el deck", "getDeckByGameId " + ex.getMessage());
        }
    }

    public Respuesta saveDeck(DeckDto deckDto) {
        try {
            et = em.getTransaction();
            et.begin();

            Deck deck;
            boolean isUpdate = false;

            if (deckDto.getDeckId() != null) {
                deck = em.find(Deck.class, deckDto.getDeckId());

                if (deck == null) {
                    et.rollback();
                    return new Respuesta(false, "Deck no encontrado para actualizar", "saveDeck DeckNotFound");
                }
                deck.update(deckDto, em);
                deck = em.merge(deck);

                isUpdate = true;

            } else {
                deck = new Deck(deckDto, em);
                em.persist(deck);
            }

            et.commit();

            String message = isUpdate ? "Deck actualizado exitosamente" : "Deck creado exitosamente";
            return new Respuesta(true, message, "saveDeck success", "Deck", new DeckDto(deck));

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(DeckService.class.getName()).log(Level.SEVERE, "Error guardando deck", ex);
            return new Respuesta(false, "Error guardando el deck", "saveDeck " + ex.getMessage());
        }
    }

    public Respuesta deleteDeckById(Long id) {
        try {
            et = em.getTransaction();
            et.begin();

            Deck deck = em.find(Deck.class, id);

            if (deck == null) {
                et.rollback();
                return new Respuesta(false, "Deck no encontrado", "deleteDeckById DeckNotFound");
            }
            em.remove(deck);
            et.commit();

            return new Respuesta(true, "Deck eliminado exitosamente", "deleteDeckById success");

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(DeckService.class.getName()).log(Level.SEVERE, "Error eliminando deck [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando el deck", "deleteDeckById " + ex.getMessage());
        }
    }
}

