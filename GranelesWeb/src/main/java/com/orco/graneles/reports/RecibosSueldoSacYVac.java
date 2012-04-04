/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.vo.ItemSueldoVO;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase que genera el reporte de libro de sueldos
 * @author orco
 */
public class RecibosSueldoSacYVac extends ReporteGenerico {
    
    private List<ItemSueldoVO> dataSource;
    private Periodo periodo;
    
    public RecibosSueldoSacYVac(Periodo periodo, List<Sueldo> sueldos, boolean oficial){
        this.periodo = periodo;
        dataSource = new ArrayList<ItemSueldoVO>();
        
        //Agrego todos los items del periodo para el reporte
        for(Sueldo s : sueldos){
            for (ItemsSueldo is : s.getItemsSueldoCollection()){
                if (!oficial || is.getConceptoRecibo().getOficial()){
                    dataSource.add(new ItemSueldoVO(is, oficial));
                }
            }
        }
                
        //Ordeno antes de devolver todo
        Collections.sort(dataSource, new ComparadorItems());
    }

    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        params.put("sac", Boolean.TRUE);
        
        return printGenerico(ds, "ReciboSueldos", "RecibosSacYVac_"+ periodo.getDescripcion());
    }
    
    
    private class ComparadorItems implements Comparator<ItemSueldoVO>{
        /*
         * Criterio de Ordenamiento, si es la misma persona ordeno por el orden del concepto, sino ordento por el apellido y nombre de la persona
         */
        @Override
        public int compare(ItemSueldoVO o1, ItemSueldoVO o2) {
            if (!o1.getSueldo().getPersonal().getId().equals(o2.getSueldo().getPersonal().getId())){
                return o1.getSueldo().getPersonal().getApellido().compareTo(o2.getSueldo().getPersonal().getApellido());
            } else {
                return o1.getConceptoRecibo().getOrden().compareTo(o2.getConceptoRecibo().getOrden());
            }            
        }
    } 
    
}
