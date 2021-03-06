/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Sueldo;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author orco
 */
public class ItemSueldoVO {
    
    private ItemsSueldo item;
    private Boolean oficial;
    
    private Date periodoDesde;
    private Date periodoHasta;
    
    private String conceptoCodigo;
    private String conceptoDescripcion;
    
    public ItemSueldoVO(ItemsSueldo itemSueldo, Boolean oficial){
        item = itemSueldo;
        this.conceptoCodigo = itemSueldo.getConceptoRecibo().getCodigo();
        this.conceptoDescripcion = itemSueldo.getConceptoRecibo().getConcepto();
        this.oficial = oficial;
    }
    
    
    public ItemSueldoVO(ItemsSueldo itemSueldo, Boolean oficial, Date desde, Date hasta){
        item = itemSueldo;
        this.conceptoCodigo = itemSueldo.getConceptoRecibo().getCodigo();
        this.conceptoDescripcion = itemSueldo.getConceptoRecibo().getConcepto();
        this.oficial = oficial;
        this.periodoDesde = desde;
        this.periodoHasta = hasta;
    }
    
    public ItemSueldoVO(ItemsSueldo itemSueldo, Boolean oficial, Date desde, Date hasta, String codigoConcepto, String descripcionConcepto){
        item = itemSueldo;
        this.conceptoCodigo = codigoConcepto;
        this.conceptoDescripcion = descripcionConcepto;
        this.oficial = oficial;
        this.periodoDesde = desde;
        this.periodoHasta = hasta;
    }
    
    
    
    public Integer getOrdenConcepto(){
        return item.getConceptoRecibo().getOrden();
    }
    
    public BigInteger getNroPrimeraHoja(){
        return item.getSueldo().getPeriodo().getNroPrimeraHoja();
    }

    public String getNroTomo(){
        return item.getSueldo().getPeriodo().getFolioLibro();
    }
    
    public String getApellidoNombre(){
        return item.getSueldo().getPersonal().getApellido();
    }
    
    public String getDireccion(){
        return item.getSueldo().getPersonal().getDomicilio();
    }
   
    public String getLocalidad(){
        return item.getSueldo().getPersonal().getLocalidad();
    }
    
    public Date getFechaIngreso(){
        return item.getSueldo().getPersonal().getIngreso();
    }
   
    public String getCategoria(){
        return (item.getSueldo().getPersonal().getCategoriaPrincipal() != null)
                    ?item.getSueldo().getPersonal().getCategoriaPrincipal().getDescripcion()
                : null;
    }
    
    public String getDescripcionPeriodo(){
        return item.getSueldo().getPeriodo().getDescripcion();
    }
    
    public String getCuil(){
        return item.getSueldo().getPersonal().getCuil();
    }
    
    public String getSexo(){
        return "M";
    }
    
    public String getDocumentoYTipo(){
        return item.getSueldo().getPersonal().getDocumento();
    }
    
    public Date getFechaEgreso(){
        return item.getSueldo().getPersonal().getBaja();
    }
    
    public String getEstadoCivil(){
        if (item.getSueldo().getPersonal().getEstadoCivil() != null) {
            return item.getSueldo().getPersonal().getEstadoCivil().getDescripcion();
        } else {
            return null;
        }
    }
    
    public String getConceptoCodigo(){
        return conceptoCodigo;
    }
    
    public String getConceptoDescripcion(){
        return conceptoDescripcion;
    }
    
    public BigDecimal getConceptoCantidad(){
        return (item.getCantidad() != null && item.getCantidad().doubleValue() > 0.009)? item.getCantidad() : null;
    }
    
    public String getLegajo(){
        return item.getSueldo().getPersonal().getRegistro();
    }
    
    public String getSufijoCantidad(){
        return item.getConceptoRecibo().getSufijoCantidad();
    }
    
    public Boolean getConceptoOficial(){
        return item.getConceptoRecibo().getOficial();
    }
    
    public BigDecimal getValorRemunerativo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorDeductivo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.DEDUCTIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorNoRemunerativo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.NO_REMUNERATIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorNoRemunerativoNegativo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.NO_REMUNERATIVO_NEGATIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorPositivo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)
            || item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.NO_REMUNERATIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorNegativo(){
        if (item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.DEDUCTIVO)
            || item.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.NO_REMUNERATIVO_NEGATIVO)){
            return item.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getTotalRemunerativo(){
        return item.getSueldo().getTotalRemunerativo(oficial);
    }
    
    public BigDecimal getTotalDeductivo(){
        return item.getSueldo().getTotalDeductivo(oficial);
    }
    
    public BigDecimal getTotalNoRemunerativo(){
        return item.getSueldo().getTotalNoRemunerativo(oficial);
    }
    
    public BigDecimal getTotalNoRemunerativoNegativo(){
        return item.getSueldo().getTotalNoRemunerativoNegativo(oficial);
    }
    
    public BigDecimal getTotalesPositivos(){
        return getTotalRemunerativo().add(getTotalNoRemunerativo());
    }
    
    public BigDecimal getTotalesNegativos(){
        return getTotalDeductivo().add(getTotalNoRemunerativoNegativo());
    }
            
    public BigDecimal getTotalSueldoNeto(){
        return item.getSueldo().getTotalSueldoNeto(oficial);
    }
    
    public String getTotalSueldoNetoString(){
        return NumberToStringConverter.decimalACastellano(getTotalSueldoNeto(), 2, "pesos con ", "ctvos.");
    }
   
    public Integer getIDConceptoRecibo(){
        return item.getConceptoRecibo().getId();
    }
    
    public Long getIDSueldo(){
        return item.getSueldo().getId();
    }

    public Long getId() {
        return item.getId();
    }

    public BigDecimal getValorIngresado() {
        return item.getValorIngresado();
    }

    public BigDecimal getValorCalculado() {
        return item.getValorCalculado();
    }
    
    public Sueldo getSueldo(){
        return item.getSueldo();
    }
    
    public ConceptoRecibo getConceptoRecibo(){
        return item.getConceptoRecibo();
    }
    
    public Date getPeriodoDesde(){
        if (this.periodoDesde != null) {
            return this.periodoDesde;
        } else {
            return item.getSueldo().getPeriodo().getDesde();
        }
    }
    
    public Date getPeriodoHasta(){
        if (this.periodoHasta != null) {
            return this.periodoHasta;
        } else {
            return item.getSueldo().getPeriodo().getHasta();
        }
    }
}
