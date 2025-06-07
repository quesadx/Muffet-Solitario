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
@Table(name = "COMPLETED_SEQUENCE")
@NamedQueries({
    @NamedQuery(name = "CompletedSequence.findAll", query = "SELECT c FROM CompletedSequence c"),
    @NamedQuery(name = "CompletedSequence.findByCseqId", query = "SELECT c FROM CompletedSequence c WHERE c.cseqId = :cseqId"),
    @NamedQuery(name = "CompletedSequence.findByCseqOrder", query = "SELECT c FROM CompletedSequence c WHERE c.cseqOrder = :cseqOrder")})
public class CompletedSequence implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name = "SEQUENCE_COMPLETED_ID_GENERATOR", sequenceName = "COMPLETED_SEQUENCE_SQ01", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPLETED_SEQUENCE_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "CSEQ_ID")
    private Long cseqId;
    @Column(name = "CSEQ_ORDER")
    private Integer cseqOrder;
    @Basic(optional = false)
    @Column(name = "CSEQ_VERSION")
    private Long cseqVersion;
    @JoinColumn(name = "CSEQ_GAME_FK", referencedColumnName = "GAME_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Game cseqGameFk;
    @OneToMany(mappedBy = "cardCseqFk", fetch = FetchType.LAZY)
    private List<Card> cardList;

    public CompletedSequence() {
    }

    public CompletedSequence(Long cseqId) {
        this.cseqId = cseqId;
    }

    public CompletedSequence(CompletedSequenceDto completedSequenceDto, EntityManager em) {
        update(completedSequenceDto);

        if (completedSequenceDto.getCseqGameFk() != null) {
            cseqGameFk = em.getReference(Game.class, completedSequenceDto.getCseqGameFk());
        }
    }

    public void update(CompletedSequenceDto completedSequenceDto) {
        cseqId = completedSequenceDto.getCseqId();
        cseqOrder = completedSequenceDto.getCseqOrder();
        cseqVersion = completedSequenceDto.getCseqVersion();
    }

    public void update(CompletedSequenceDto completedSequenceDto, EntityManager em) {
        cseqId = completedSequenceDto.getCseqId();
        cseqOrder = completedSequenceDto.getCseqOrder();
        cseqVersion = completedSequenceDto.getCseqVersion();

        if (completedSequenceDto.getCseqGameFk() != null) {
            cseqGameFk = em.getReference(Game.class, completedSequenceDto.getCseqGameFk());
        }
    }

    public Long getCseqId() {
        return cseqId;
    }

    public void setCseqId(Long cseqId) {
        this.cseqId = cseqId;
    }

    public Integer getCseqOrder() {
        return cseqOrder;
    }

    public void setCseqOrder(Integer cseqOrder) {
        this.cseqOrder = cseqOrder;
    }

    public Long getCseqVersion() {
        return cseqVersion;
    }

    public void setCseqVersion(Long cseqVersion) {
        this.cseqVersion = cseqVersion;
    }

    public Game getCseqGameFk() {
        return cseqGameFk;
    }

    public void setCseqGameFk(Game cseqGameFk) {
        this.cseqGameFk = cseqGameFk;
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
        hash += (cseqId != null ? cseqId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CompletedSequence)) {
            return false;
        }
        CompletedSequence other = (CompletedSequence) object;
        if ((this.cseqId == null && other.cseqId != null) || (this.cseqId != null && !this.cseqId.equals(other.cseqId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.muffetsolitario.model.CompletedSequence[ cseqId=" + cseqId + " ]";
    }
}
