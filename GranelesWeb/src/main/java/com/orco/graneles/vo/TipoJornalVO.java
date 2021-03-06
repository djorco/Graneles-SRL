/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.salario.TipoJornal;
import java.math.BigDecimal;


/**
 *
 * @author orco
 */
public class TipoJornalVO {
    
    private TipoJornal tipoJornal;
    private BigDecimal total;

    public TipoJornalVO() {
    }

    public TipoJornalVO(TipoJornal tipoJornal) {
        this.tipoJornal = tipoJornal;
        this.total = BigDecimal.ZERO;
    }

    public TipoJornal getTipoJornal() {
        return tipoJornal;
    }

    public void setTipoJornal(TipoJornal tipoJornal) {
        this.tipoJornal = tipoJornal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public Boolean getMostrar(){
        return (this.getTotal() != null) && (this.getTotal().doubleValue() > 0.0);
    }
    
    
}
