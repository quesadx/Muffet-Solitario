/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.UserAccount;
import cr.ac.una.muffetsolitario.model.UserAccountDto;
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

/**
 *
 * @author Al3xz
 */
public class UserAccountService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;
    
    public Respuesta getUserAccountById(Long id){
        try {
            Query qryUser = em.createNamedQuery("UserAccount.findByUserId", UserAccount.class);
            qryUser.setParameter("userId", id);
            
            UserAccount userAccount = (UserAccount) qryUser.getSingleResult();
            UserAccountDto userAccountDto = new UserAccountDto(userAccount); 
            
            return new Respuesta(true, "Usuario encontrado", "getUserAccountById success", "UserAccount", userAccountDto);
            
        } catch (NoResultException ex) {
            return new Respuesta(false, "Usuario no encontrado", "getUserAccountById NoResult");
        } catch (Exception ex) {
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error obteniendo usuario por ID [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario", "getUserAccountById " + ex.getMessage());
        }
    }
    
    public Respuesta getUserAccountByNickname(String nickname) {
        try {
            Query qryUser = em.createNamedQuery("UserAccount.findByUserNickname", UserAccount.class);
            qryUser.setParameter("userNickname", nickname);
            
            UserAccount userAccount = (UserAccount) qryUser.getSingleResult();
            UserAccountDto userAccountDto = new UserAccountDto(userAccount); 
            
            return new Respuesta(true, "Usuario encontrado", "getUserAccountByNickname success", "UserAccount", userAccountDto);
            
        } catch (NoResultException ex) {
            return new Respuesta(false, "Usuario no encontrado", "getUserAccountByNickname NoResult");
        } catch (Exception ex) {
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error obteniendo usuario por nickname [" + nickname + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario", "getUserAccountByNickname " + ex.getMessage());
        }
    }
    
    public Respuesta validateUserCredentials(String nickname, String password) {
        try {
            Query qryUser = em.createQuery(
                "SELECT u FROM UserAccount u WHERE u.userNickname = :nickname AND u.userPassword = :password", 
                UserAccount.class);
            qryUser.setParameter("nickname", nickname);
            qryUser.setParameter("password", password);
            
            UserAccount userAccount = (UserAccount) qryUser.getSingleResult();
            UserAccountDto userAccountDto = new UserAccountDto(userAccount);

            return new Respuesta(true, "Login exitoso", "validateUserCredentials success", "UserAccount", userAccountDto);
            
        } catch (NoResultException ex) {
            return new Respuesta(false, "Credenciales inválidas", "validateUserCredentials InvalidCredentials");
        } catch (Exception ex) {
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error validando credenciales para usuario [" + nickname + "]", ex);
            return new Respuesta(false, "Error validando credenciales", "validateUserCredentials " + ex.getMessage());
        }
    }
    
    public Respuesta deleteUserAccountById(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            
            UserAccount userAccount = em.find(UserAccount.class, id);
            if (userAccount == null) {
                et.rollback();
                return new Respuesta(false, "Usuario no encontrado", "deleteUserAccountById UserNotFound");
            }
            
            em.remove(userAccount);
            et.commit();
            
            return new Respuesta(true, "Usuario eliminado exitosamente", "deleteUserAccountById success");
            
        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error eliminando usuario [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando el usuario", "deleteUserAccountById " + ex.getMessage());
        }
    }
    
    
    public Respuesta saveUserAccount(UserAccountDto userAccountDto) {
        try {
            et = em.getTransaction();
            et.begin();
            
            UserAccount userAccount;
            boolean isUpdate = false;
            if (userAccountDto.getUserId() != null) {
                userAccount = em.find(UserAccount.class, userAccountDto.getUserId());
                if (userAccount == null) {
                    et.rollback();
                    return new Respuesta(false, "Usuario no encontrado para actualizar", "saveUserAccount UserNotFound");
                }
                
                userAccount.update(userAccountDto);
                userAccount = em.merge(userAccount);
                isUpdate = true;
                
            } else {
                userAccount = new UserAccount(userAccountDto);
                em.persist(userAccount);
            }
            
            et.commit();
            
            String message = isUpdate ? "Usuario actualizado exitosamente" : "Usuario creado exitosamente";
            return new Respuesta(true, message, "saveUserAccount success", "UserAccount", userAccountDto);
            
        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error guardando usuario", ex);
            return new Respuesta(false, "Error guardando el usuario", "saveUserAccount " + ex.getMessage());
        }
    }
    
    public Respuesta updateUserStats(Long userId, boolean gameWon, int gameScore) {
        try {
            et = em.getTransaction();
            et.begin();
            
            UserAccount userAccount = em.find(UserAccount.class, userId);
            if (userAccount == null) {
                et.rollback();
                return new Respuesta(false, "Usuario no encontrado", "updateUserStats UserNotFound");
            }
            
            int totalGames = (userAccount.getUserTotalGames() != null ? userAccount.getUserTotalGames() : 0) + 1;
            int wonGames = userAccount.getUserWonGames() != null ? userAccount.getUserWonGames() : 0;
            int totalScore = (userAccount.getUserTotalScore() != null ? userAccount.getUserTotalScore() : 0) + gameScore;
            int bestScore = userAccount.getUserBestScore() != null ? userAccount.getUserBestScore() : 0;
            
            if (gameWon) {
                wonGames++;
            }
            
            if (gameScore > bestScore) {
                bestScore = gameScore;
            }
            
            userAccount.setUserTotalGames(totalGames);
            userAccount.setUserWonGames(wonGames);
            userAccount.setUserTotalScore(totalScore);
            userAccount.setUserBestScore(bestScore);
            
            et.commit();
            
            return new Respuesta(true, "Estadísticas actualizadas", "updateUserStats success");
            
        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error actualizando estadísticas del usuario [" + userId + "]", ex);
            return new Respuesta(false, "Error actualizando estadísticas", "updateUserStats " + ex.getMessage());
        }
    }
    
    public Respuesta getAllUsers() {
        try {
            Query qryUsers = em.createNamedQuery("UserAccount.findAll", UserAccount.class);
            List<UserAccount> users = qryUsers.getResultList();
            
            List<UserAccountDto> userDtos = new ArrayList<>();
            for (UserAccount user : users) {
                UserAccountDto userAccountDto = new UserAccountDto(user);
                userDtos.add(userAccountDto);
            }
            
            return new Respuesta(true, "Usuarios obtenidos", "getAllUsers success", "Users", userDtos);
            
        } catch (Exception ex) {
            Logger.getLogger(UserAccountService.class.getName()).log(Level.SEVERE, "Error obteniendo todos los usuarios", ex);
            return new Respuesta(false, "Error obteniendo usuarios", "getAllUsers " + ex.getMessage());
        }
    }
}