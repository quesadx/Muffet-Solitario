/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.Game;
import cr.ac.una.muffetsolitario.model.GameDto;
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
    
    public Respuesta saveGame(GameDto gameDto) {
        try {
            et = em.getTransaction();
            et.begin();
            
            Game game;
            boolean isUpdate = false;
            
            if (gameDto.getGameId() != null) {
                game = em.find(Game.class, gameDto.getGameId());
                if (game == null) {
                    et.rollback();
                    return new Respuesta(false, "Juego no encontrado para actualizar", "saveGame GameNotFound");
                }
                
                game.update(gameDto);
                game = em.merge(game);
                isUpdate = true;
                
            } else {
                game = new Game(gameDto);
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
            Game game = em.find(Game.class, gameId);
            if (game == null) {
                return new Respuesta(false, "Juego no encontrado", "getGameById GameNotFound");
            }
            
            GameDto gameDto = new GameDto(game);
            return new Respuesta(true, "Juego encontrado", "getGameById success", "Game", gameDto);
            
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