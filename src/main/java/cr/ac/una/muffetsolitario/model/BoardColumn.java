/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Al3xz
 */
@Entity
@Table(name = "BOARD_COLUMN")
@NamedQueries({
    @NamedQuery(name = "BoardColumn.findAll", query = "SELECT b FROM BoardColumn b"),
    @NamedQuery(name = "BoardColumn.findByBcolmnId", query = "SELECT b FROM BoardColumn b WHERE b.bcolmnId = :bcolmnId"),
    @NamedQuery(name = "BoardColumn.findByBcolmnIndex", query = "SELECT b FROM BoardColumn b WHERE b.bcolmnIndex = :bcolmnIndex")})
public class BoardColumn implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "BCOLMN_ID")
    private Long bcolmnId;
    @Basic(optional = false)
    @Column(name = "BCOLMN_INDEX")
    private Integer bcolmnIndex;
    @Basic(optional = false)
    @Column(name = "BCOLMN_VERSION")
    private Long bcolmnVersion;
    @JoinColumn(name = "BCOLMN_GAME_FK", referencedColumnName = "GAME_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Game bcolmnGameFk;
    @OneToMany(mappedBy = "cardBcolmnFk", fetch = FetchType.LAZY)
    private List<Card> cardList;

    public BoardColumn() {
    }

    public BoardColumn(Long bcolmnId) {
        this.bcolmnId = bcolmnId;
    }

    public BoardColumn(Long bcolmnId, Integer bcolmnIndex) {
        this.bcolmnId = bcolmnId;
        this.bcolmnIndex = bcolmnIndex;
    }

    public BoardColumn(BoardColumnDto boardColumnDto) {
        bcolmnId = boardColumnDto.getBcolmnId();
        bcolmnIndex = boardColumnDto.getBcolmnIndex();
        bcolmnVersion = boardColumnDto.getBcolmnVersion();
    }

    public BoardColumn(BoardColumnDto boardColumnDto, EntityManager em) {
        update(boardColumnDto);
        if (boardColumnDto.getBcolmnGameFk() != null) {
            bcolmnGameFk = em.getReference(Game.class, boardColumnDto.getBcolmnGameFk());
        }
    }

    public void update(BoardColumnDto boardColumnDto) {
        bcolmnId = boardColumnDto.getBcolmnId();
        bcolmnIndex = boardColumnDto.getBcolmnIndex();
        bcolmnVersion = boardColumnDto.getBcolmnVersion();
    }

    public void update(BoardColumnDto boardColumnDto, EntityManager em) {
        bcolmnId = boardColumnDto.getBcolmnId();
        bcolmnIndex = boardColumnDto.getBcolmnIndex();
        bcolmnVersion = boardColumnDto.getBcolmnVersion();

        if (boardColumnDto.getBcolmnGameFk() != null) {
            this.bcolmnGameFk = em.getReference(Game.class, boardColumnDto.getBcolmnGameFk());
        }
    }

    public Long getBcolmnId() {
        return bcolmnId;
    }

    public void setBcolmnId(Long bcolmnId) {
        this.bcolmnId = bcolmnId;
    }

    public Integer getBcolmnIndex() {
        return bcolmnIndex;
    }

    public void setBcolmnIndex(Integer bcolmnIndex) {
        this.bcolmnIndex = bcolmnIndex;
    }

    public Long getBcolmnVersion() {
        return bcolmnVersion;
    }

    public void setBcolmnVersion(Long bcolmnVersion) {
        this.bcolmnVersion = bcolmnVersion;
    }

    public Game getBcolmnGameFk() {
        return bcolmnGameFk;
    }

    public void setBcolmnGameFk(Game bcolmnGameFk) {
        this.bcolmnGameFk = bcolmnGameFk;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bcolmnId != null ? bcolmnId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BoardColumn)) {
            return false;
        }
        BoardColumn other = (BoardColumn) object;
        if ((this.bcolmnId == null && other.bcolmnId != null) || (this.bcolmnId != null && !this.bcolmnId.equals(other.bcolmnId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.BoardColumn[ bcolmnId=" + bcolmnId + " ]";
    }
}
