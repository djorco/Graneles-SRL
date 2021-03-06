/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "carga_turno")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargaTurno.findAll", query = "SELECT c FROM CargaTurno c"),
    @NamedQuery(name = "CargaTurno.findById", query = "SELECT c FROM CargaTurno c WHERE c.id = :id"),
    @NamedQuery(name = "CargaTurno.findByEmbarqueYCargadorSinFacturar",
        query = "SELECT c FROM CargaTurno c"
                + " WHERE c.cargador = :cargador"
                + " AND c.turnoEmbarque.embarque = :embarque"
                + " AND c.turnoFacturado IS NULL ")
})
public class CargaTurno implements Serializable, Comparable<CargaTurno> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "turno_embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TurnoEmbarque turnoEmbarque;
    
    @JoinColumn(name = "coordinador", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa cargador;
    
    @Column(name = "cantidad_lineas")
    private Integer cantidadLineas;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cargaTurno", orphanRemoval = true)
    private Collection<CargaTurnoCargas> cargasCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cargaTurno", orphanRemoval = true)
    private Collection<MovimientoCtaCteTons> movimientoCtaCteCollection;
    
    @JoinColumn(name = "turno_facturado", referencedColumnName = "id")
    @ManyToOne()
    private TurnoFacturado turnoFacturado;
    
    public BigDecimal getTotalCargado(){
        BigDecimal total = BigDecimal.ZERO;
        
        for (CargaTurnoCargas ctc : this.getCargasCollection()){
            if (ctc.getCarga() != null){
                total = total.add(ctc.getCarga());
            }
        }
        
        return total;
    }
    
    /**
     * Devuelve el total a bordo para esa carga turno.
     * @return 
     */
    public BigDecimal getTotalEmbarcadoInicio() {
        BigDecimal total = this.getTurnoEmbarque().getTotalEmbarcadoAlInicio();
        
        for (CargaTurno ct : this.getTurnoEmbarque().getCargaTurnoCollection()){
            if (ct.compareTo(this) < 0){
                total = total.add(ct.getTotalCargado());
            }
        }

        return total;
    }
    
    public CargaTurno() {
    }

    public CargaTurno(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TurnoEmbarque getTurnoEmbarque() {
        return turnoEmbarque;
    }

    public void setTurnoEmbarque(TurnoEmbarque turnoEmbarque) {
        this.turnoEmbarque = turnoEmbarque;
    }

    public Empresa getCargador() {
        return cargador;
    }

    public void setCargador(Empresa cargador) {
        this.cargador = cargador;
    }

    public Collection<CargaTurnoCargas> getCargasCollection() {
        return cargasCollection;
    }

    public List<CargaTurnoCargas> getCargasSorted(){
        List<CargaTurnoCargas> cargas = new ArrayList<CargaTurnoCargas>();
        if (this.getCargasCollection() != null){
            cargas.addAll(this.getCargasCollection());
        }
        Collections.sort(cargas);
        return cargas;
    }
    
    public void setCargasCollection(Collection<CargaTurnoCargas> cargasCollection) {
        this.cargasCollection =  cargasCollection;
    }

    public TurnoFacturado getTurnoFacturado() {
        return turnoFacturado;
    }

    public void setTurnoFacturado(TurnoFacturado turnoFacturado) {
        this.turnoFacturado = turnoFacturado;
    }

    public Integer getCantidadLineas() {
        return cantidadLineas;
    }

    public void setCantidadLineas(Integer cantidadLineas) {
        this.cantidadLineas = cantidadLineas;
    }

    public Collection<MovimientoCtaCteTons> getMovimientoCtaCteCollection() {
        return movimientoCtaCteCollection;
    }

    public void setMovimientoCtaCteCollection(Collection<MovimientoCtaCteTons> movimientoCtaCteCollection) {
        this.movimientoCtaCteCollection = movimientoCtaCteCollection;
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
        if (!(object instanceof CargaTurno)) {
            return false;
        }
        CargaTurno other = (CargaTurno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.CargaTurno[ id=" + id + " ]";
    }

    @Override
    public int compareTo(CargaTurno o) {
        if (this.getTurnoEmbarque().equals(o.getTurnoEmbarque())){
            return this.getCargador().getNombre().compareTo(o.getCargador().getNombre());
        } else {
            return this.getTurnoEmbarque().compareTo(o.getTurnoEmbarque());
        }      
    }
    
}
