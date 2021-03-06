/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Sueldo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
/**
 *
 * @author orco
 */
@Stateless
public class ItemsSueldoFacade extends AbstractFacade<ItemsSueldo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsSueldoFacade() {
        super(ItemsSueldo.class);
    }
    
    /**
     * Crea un itemSueldo para el concepto y el valor pedido
     */
    public void crearItemSueldo(ConceptoRecibo concepto,BigDecimal cantidad, BigDecimal valor, Sueldo sueldo) {
        //Item Sueldo Bruto
        ItemsSueldo is = new ItemsSueldo();
        is.setConceptoRecibo(concepto);
        is.setValorCalculado(valor.setScale(2, RoundingMode.HALF_DOWN));
        is.setValorIngresado(valor.setScale(2, RoundingMode.HALF_DOWN));
        is.setCantidad(cantidad);
        is.setSueldo(sueldo);
        if (sueldo.getItemsSueldoCollection() == null){
            sueldo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());
        }
        sueldo.getItemsSueldoCollection().add(is);
    }
    
}
