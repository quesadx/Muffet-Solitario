package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.CompletedSequence;
import cr.ac.una.muffetsolitario.model.CompletedSequenceDto;
import cr.ac.una.muffetsolitario.util.EntityManagerHelper;
import cr.ac.una.muffetsolitario.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompletedSequenceService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getCompletedSequenceById(Long id) {
        try {
            Query qryCompletedSequence = em.createNamedQuery("CompletedSequence.findByCseqId", CompletedSequence.class);
            qryCompletedSequence.setParameter("cseqId", id);

            CompletedSequence completedSequence = (CompletedSequence) qryCompletedSequence.getSingleResult();
            CompletedSequenceDto completedSequenceDto = new CompletedSequenceDto(completedSequence);

            return new Respuesta(true, "Secuencia completada encontrada", "getCompletedSequenceById success", "CompletedSequence", completedSequenceDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe una secuencia completada con el id ingresado", "getCompletedSequenceById NoResultException");
        } catch (Exception ex) {
            Logger.getLogger(CompletedSequenceService.class.getName()).log(Level.SEVERE, "Error obteniendo la secuencia completada por ID", ex);
            return new Respuesta(false, "Error obteniendo la secuencia completada", "getCompletedSequenceById " + ex.getMessage());
        }
    }

    public Respuesta getAllCompletedSequencesByGameId(Long gameId) {
        try {
            Query qryCompletedSequences = em.createQuery(
                    "SELECT cs FROM CompletedSequence cs WHERE cs.cseqGameFk.gameId = :gameId ORDER BY cs.cseqOrder",
                    CompletedSequence.class);
            qryCompletedSequences.setParameter("gameId", gameId);

            List<CompletedSequence> completedSequences = qryCompletedSequences.getResultList();

            List<CompletedSequenceDto> completedSequenceDtos = new ArrayList<>();
            for (CompletedSequence completedSequence : completedSequences) {
                completedSequenceDtos.add(new CompletedSequenceDto(completedSequence));
            }

            return new Respuesta(true, "Secuencias completadas obtenidas exitosamente", "getAllCompletedSequencesByGameId success", "CompletedSequences", completedSequenceDtos);

        } catch (Exception ex) {
            Logger.getLogger(CompletedSequenceService.class.getName()).log(Level.SEVERE, "Error obteniendo todas las secuencias completadas del juego [" + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo las secuencias completadas", "getAllCompletedSequencesByGameId " + ex.getMessage());
        }
    }

    public Respuesta saveCompletedSequence(CompletedSequenceDto completedSequenceDto) {
        try {
            et = em.getTransaction();
            et.begin();

            CompletedSequence completedSequence;
            if (completedSequenceDto.getCseqId() != null) {
                completedSequence = em.find(CompletedSequence.class, completedSequenceDto.getCseqId());
                if (completedSequence == null) {
                    et.rollback();
                    return new Respuesta(false, "Secuencia completada no encontrada para actualizar", "saveCompletedSequence CompletedSequenceNotFound");
                }
                completedSequence.update(completedSequenceDto, em);
            } else {
                completedSequence = new CompletedSequence(completedSequenceDto, em);
                em.persist(completedSequence);
            }

            et.commit();

            CompletedSequenceDto savedCompletedSequenceDto = new CompletedSequenceDto(completedSequence);
            return new Respuesta(true, "Secuencia completada guardada exitosamente", "saveCompletedSequence success", "CompletedSequence", savedCompletedSequenceDto);

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(CompletedSequenceService.class.getName()).log(Level.SEVERE, "Error guardando secuencia completada", ex);
            return new Respuesta(false, "Error guardando la secuencia completada", "saveCompletedSequence " + ex.getMessage());
        }
    }

    public Respuesta deleteCompletedSequenceById(Long id) {
        try {
            et = em.getTransaction();
            et.begin();

            CompletedSequence completedSequence = em.find(CompletedSequence.class, id);
            if (completedSequence == null) {
                et.rollback();
                return new Respuesta(false, "Secuencia completada no encontrada", "deleteCompletedSequenceById CompletedSequenceNotFound");
            }

            em.remove(completedSequence);
            et.commit();

            return new Respuesta(true, "Secuencia completada eliminada exitosamente", "deleteCompletedSequenceById success");

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(CompletedSequenceService.class.getName()).log(Level.SEVERE, "Error eliminando secuencia completada [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando la secuencia completada", "deleteCompletedSequenceById " + ex.getMessage());
        }
    }
}