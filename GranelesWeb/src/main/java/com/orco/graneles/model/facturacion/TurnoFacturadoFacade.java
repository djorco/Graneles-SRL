/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Mercaderia;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.Tarifa;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.miscelaneos.TipoTurnoFactura;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.vo.Calculadora;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class TurnoFacturadoFacade extends AbstractFacade<TurnoFacturado> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    TarifaFacade tarifaF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public TurnoFacturadoFacade() {
        super(TurnoFacturado.class);
    }
    
    /**
     * Metodo que compelta las linea de factura para los turnos correspondientes
     * El metodo hace calculos para los sueldos de administracion, tarifa, costo, etc
     * @param cargaTurnos
     * @return 
     */
    public List<TurnoFacturado> crearLineas(List<CargaTurno> cargaTurnos, Factura factura){
        List<TurnoFacturado> lineas = new ArrayList<TurnoFacturado>();
        
        for (CargaTurno ct : cargaTurnos){
            TurnoFacturado tf = new TurnoFacturado();
            tf.setFactura(factura);
            tf.setCargaTurno(ct);
            tf.setValor(BigDecimal.ZERO);
            
            tf.setTotalBruto(ct.getTurnoEmbarque().getTotalBruto());
            tf.setCosto(calcularCosto(ct));
            tf.setTarifa(calcularTarifa(ct));
            
            tf.setAdministracion(calcularAdministracion(ct, tf.getFactura().getPorcentajeAdministracion()));
            
            lineas.add(tf);
        }
        
        factura.setTurnosFacturadosCollection(lineas);
        return lineas;
    }
    
    public void actualizarLineas(List<TurnoFacturado> lineas, Factura factura){
        for(TurnoFacturado tf : lineas){
            actualizarLinea(tf, factura);
        }
    }
    
    public BigDecimal calcularCosto(CargaTurno ct){
        //TODO: Modificar este metodo para que tome el valor de la configuracion
        BigDecimal multiplicador = new BigDecimal(1.9);
        return ct.getTurnoEmbarque().getTotalBruto().multiply(multiplicador);
    }
    
    
    public BigDecimal calcularTarifa(CargaTurno ct){
        BigDecimal resultado = BigDecimal.ZERO;
        
        for (CargaTurnoCargas ctc : ct.getCargasCollection()){
            if (ctc.getCarga().compareTo(BigDecimal.ZERO) > 0){
                Tarifa tActiva = tarifaF.obtenerTarifaActiva(ct.getTurnoEmbarque().getTipo(), ctc.getMercaderiaBodega().getGrupoFacturacion(), ct.getTurnoEmbarque().getFecha());
                
                if (tActiva != null){
                    resultado = resultado.add(tActiva.getValor().multiply(ctc.getCarga()));
                }
            }
        }
        
        return resultado;
    }
    
    public BigDecimal calcularTarifa(Mercaderia m, CargaTurno ct){
        BigDecimal resultado = BigDecimal.ZERO;
        
        Tarifa tActiva = null;
        
        for (CargaTurnoCargas ctc : ct.getCargasCollection()){
            if (ctc.getMercaderiaBodega().equals(m) && ctc.getCarga().compareTo(BigDecimal.ZERO) > 0){
                if (tActiva == null){
                    tActiva = tarifaF.obtenerTarifaActiva(ct.getTurnoEmbarque().getTipo(), ctc.getMercaderiaBodega().getGrupoFacturacion(), ct.getTurnoEmbarque().getFecha());
                }
                
                if (tActiva != null){
                    resultado = resultado.add(tActiva.getValor().multiply(ctc.getCarga()));
                }
            }
        }
        
        return resultado;
    }
    
    public BigDecimal calcularAdministracion(CargaTurno ct, BigDecimal porcentaje){
        return ct.getTurnoEmbarque().getTotalBruto()
                .add(ct.getTurnoEmbarque().getTotalBruto()
                    .multiply(porcentaje)
                    .divide(new BigDecimal(100)
               ));
    }

    public void actualizarLinea(TurnoFacturado tf, Factura factura) {
        if (factura.getPorcentajeAdministracion() == null){
            factura.setPorcentajeAdministracion(BigDecimal.ZERO);
        }
        
        if (tf.getTipoTurnoFacturado() != null){
            switch (tf.getTipoTurnoFacturado().getId()){
                case TipoTurnoFactura.ADMINISTRACION :
                    tf.setAdministracion(calcularAdministracion(tf.getCargaTurno(), factura.getPorcentajeAdministracion()));
                    tf.setValor(tf.getAdministracion());
                    break;
                case TipoTurnoFactura.TARIFA :
                    tf.setValor(tf.getTarifa());
                    break;
                case TipoTurnoFactura.MIXTO :
                    tf.setAdministracion(calcularAdministracion(tf.getCargaTurno(), factura.getPorcentajeAdministracion()));
                    tf.setValor(tf.getTarifa().add(tf.getAdministracion()));
                    break;                    
            }
        }
    }
    
    
    
}
