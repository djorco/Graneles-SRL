/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.facturacion.Empresa;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "embarque_cargadores", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EmbarqueCargador.findAll", query = "SELECT e FROM EmbarqueCargador e"),
    @NamedQuery(name = "EmbarqueCargador.findById", query = "SELECT e FROM EmbarqueCargador e WHERE e.id = :id"),
    @NamedQuery(name = "EmbarqueCargador.findByMinimo", query = "SELECT e FROM EmbarqueCargador e WHERE e.minimo = :minimo"),
    @NamedQuery(name = "EmbarqueCargador.findByMaximo", query = "SELECT e FROM EmbarqueCargador e WHERE e.maximo = :maximo")})
public class EmbarqueCargador implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "minimo")
    private BigDecimal minimo;
    
    
    @Column(name = "maximo")
    private BigDecimal maximo;
    
    @JoinColumn(name = "embarque", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Embarque embarque;
    
    @JoinColumn(name = "cargador", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Empresa cargador;
    
    @JoinColumn(name = "mercaderia", referencedColumnName = "id", nullable = true)
    @ManyToOne()
    private Mercaderia mercaderia;
    
    @Column(name = "comienzo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date comienzo;
    
    @Column(name = "termino")
    @Temporal(TemporalType.TIMESTAMP)
    private Date termino;
    
    @Column(name = "destino")
    private String destino;
    
    @Column(name = "es_cliente")
    private Boolean esCliente = Boolean.TRUE;
    
    private transient String urlDeclaracionJurada;
    

    public EmbarqueCargador() {
    }

    public EmbarqueCargador(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public void setMinimo(BigDecimal minimo) {
        this.minimo = minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    public Embarque getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Embarque embarque) {
        this.embarque = embarque;
    }

    public Empresa getCargador() {
        return cargador;
    }

    public void setCargador(Empresa cargador) {
        this.cargador = cargador;
    }

    public Date getComienzo() {
        return comienzo;
    }

    public void setComienzo(Date comienzo) {
        this.comienzo = comienzo;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Mercaderia getMercaderia() {
        return mercaderia;
    }

    public void setMercaderia(Mercaderia mercaderia) {
        this.mercaderia = mercaderia;
    }

    public Boolean getEsCliente() {
        return esCliente;
    }

    public void setEsCliente(Boolean esCliente) {
        this.esCliente = esCliente;
    }
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmbarqueCargador)) {
            return false;
        }
        EmbarqueCargador other = (EmbarqueCargador) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer descSb = new StringBuffer(cargador.getNombre());
        if (mercaderia != null){
            descSb = descSb.append(" - ").append(mercaderia.getDescripcion());
        }
        return descSb.toString();
    }

    public String getUrlDeclaracionJurada() {
        return urlDeclaracionJurada;
    }

    public void setUrlDeclaracionJurada(String urlDeclaracionJurada) {
        this.urlDeclaracionJurada = urlDeclaracionJurada;
    }
    
    
}
