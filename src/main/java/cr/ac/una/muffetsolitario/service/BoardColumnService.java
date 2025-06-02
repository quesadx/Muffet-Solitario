package cr.ac.una.muffetsolitario.service;

import cr.ac.una.muffetsolitario.model.BoardColumn;
import cr.ac.una.muffetsolitario.model.BoardColumnDto;
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

public class BoardColumnService {
    private EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getBoardColumnById(Long id) {
        try{
            Query qryBoardColumn = em.createNamedQuery("BoardColumn.findByBcolmnId", BoardColumn.class);
            qryBoardColumn.setParameter("bcolmnId", id);

            BoardColumn boardColumn = (BoardColumn) qryBoardColumn.getSingleResult();
            BoardColumnDto bColumnDto = new BoardColumnDto(boardColumn);

            return new Respuesta(true, "Columna encontrada", "getBoardColumnById success",  "BoardColumn", bColumnDto);
        } catch(NoResultException ex){
            return new Respuesta(false, "No existe una Columna con el id ingresado", "getBoardColumnById NoResultException");
        } catch(Exception ex){
            Logger.getLogger(BoardColumnService.class.getName()).log(Level.SEVERE, "Error obteniendo la Columna por ID", ex);
            return new Respuesta(false, "Error obteniendo la Columna", "getBoardColumnById " + ex.getMessage());
        }
    }

    public Respuesta saveBoardColumn(BoardColumnDto boardColumnDto) {
        try {
            et = em.getTransaction();
            et.begin();

            BoardColumn boardColumn;
            if (boardColumnDto.getBcolmnId() != null) {
                boardColumn = em.find(BoardColumn.class, boardColumnDto.getBcolmnId());
                if (boardColumn == null) {
                    et.rollback();
                    return new Respuesta(false, "Columna no encontrada para actualizar", "saveBoardColumn BoardColumnNotFound");
                }
                boardColumn.update(boardColumnDto, em);
            } else {
                boardColumn = new BoardColumn(boardColumnDto, em);
                em.persist(boardColumn);
            }

            et.commit();

            BoardColumnDto savedBoardColumnDto = new BoardColumnDto(boardColumn);
            return new Respuesta(true, "Columna guardada exitosamente", "saveBoardColumn success", "BoardColumn", savedBoardColumnDto);

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(BoardColumnService.class.getName()).log(Level.SEVERE, "Error guardando columna", ex);
            return new Respuesta(false, "Error guardando la columna", "saveBoardColumn " + ex.getMessage());
        }
    }

    public Respuesta deleteBoardColumnById(Long id) {
        try {
            et = em.getTransaction();
            et.begin();

            BoardColumn boardColumn = em.find(BoardColumn.class, id);
            if (boardColumn == null) {
                et.rollback();
                return new Respuesta(false, "Columna no encontrada", "deleteBoardColumnById BoardColumnNotFound");
            }

            em.remove(boardColumn);
            et.commit();

            return new Respuesta(true, "Columna eliminada exitosamente", "deleteBoardColumnById success");

        } catch (Exception ex) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            Logger.getLogger(BoardColumnService.class.getName()).log(Level.SEVERE, "Error eliminando columna [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando la columna", "deleteBoardColumnById " + ex.getMessage());
        }
    }

    public Respuesta getBoardColumnsByGameId(Long gameId) {
        try {
            Query qryBoardColumns = em.createQuery(
                    "SELECT bc FROM BoardColumn bc WHERE bc.bcolmnGameFk.gameId = :gameId ORDER BY bc.bcolmnIndex",
                    BoardColumn.class);
            qryBoardColumns.setParameter("gameId", gameId);

            List<BoardColumn> boardColumns = qryBoardColumns.getResultList();

            List<BoardColumnDto> boardColumnDtos = new ArrayList<>();
            for (BoardColumn boardColumn : boardColumns) {
                boardColumnDtos.add(new BoardColumnDto(boardColumn));
            }

            return new Respuesta(true, "Columnas del juego obtenidas exitosamente", "getBoardColumnsByGameId success", "BoardColumns", boardColumnDtos);

        } catch (Exception ex) {
            Logger.getLogger(BoardColumnService.class.getName()).log(Level.SEVERE, "Error obteniendo columnas del juego [" + gameId + "]", ex);
            return new Respuesta(false, "Error obteniendo las columnas del juego", "getBoardColumnsByGameId " + ex.getMessage());
        }
    }
}
