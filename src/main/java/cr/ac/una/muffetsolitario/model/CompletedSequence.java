/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.muffetsolitario.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @Basic(optional = false)
    @Column(name = "CSEQ_ID")
    private Long cseqId;
    @Column(name = "CSEQ_ORDER")
    private Integer cseqOrder;
    @JoinColumn(name = "CSEQ_GAME_FK", referencedColumnName = "GAME_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Game cseqGameFk;
    @OneToMany(mappedBy = "cardCseqId", fetch = FetchType.LAZY)
    private List<Card> cardList;

    public CompletedSequence() {
    }

    public CompletedSequence(Long cseqId) {
        this.cseqId = cseqId;
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
