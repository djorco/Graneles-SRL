package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.miscelaneos.TipoTurnoFactura;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.facturacion.FacturaCalculadoraFacade;
import com.orco.graneles.model.facturacion.FacturaFacade;
import com.orco.graneles.model.facturacion.LineaFacturaFacade;
import com.orco.graneles.model.facturacion.TurnoFacturadoFacade;
import com.orco.graneles.reports.FacturaReport;
import com.orco.graneles.reports.LibroIVA;
import com.orco.graneles.reports.TurnosFacturados;
import com.orco.graneles.vo.Calculadora;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.joda.time.DateTime;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "facturaController")
@SessionScoped
public class FacturaController implements Serializable {

    private Factura current;
    private DataModel items = null;
    @EJB
    private FacturaFacade ejbFacade;
    @EJB
    private CargaTurnoFacade cargaTurnoF;
    @EJB
    private LineaFacturaFacade lineaFacturaF;
    @EJB
    private TurnoFacturadoFacade turnoFacturadoF;
    @EJB
    private FacturaCalculadoraFacade facturaCalculadoraF;
    
    private Boolean tabBusquedaAbierto;
    private Boolean tabSeleccionPlanillasAbrierto;
    private Boolean tabCalculoAbierto;
    private Boolean tabCalculadoraAbierto;
    
    private int selectedItemIndex;
    
    private DualListModel<CargaTurno> turnosASeleccionarModel;
    private DataModel lineasFacturaModel;
    private List<LineaFactura> lineasFactura;
    private List<TurnoFacturado> turnosFacturados;
    private DataModel turnosFacturadosModel;
    
    //LINEAS EN EL MOMENTO DE LA CONFIRMACION
    private LineaFactura lineaAdministracion;
    private List<LineaFactura> lineasTarifa;
    private List<LineaFactura> lineasConceptos;
    private DataModel lineasTarifaModel;
    private DataModel lineasConceptosModel;
    
    private String lnkFactura;
    private String lnkTurnosFacturados;
    
    private Calculadora calculadora;
    
    //LIBOR DE IVA
    private Integer mesLibro;
    private Integer anioLibro;
    private String nombreLibro;
    private String lnkLibroIVA;
    private Integer nroPrimeraPagina;
    
    public FacturaController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }
    
    public static final String STEP_SELECCION_EMBARQUE = "seleccionEmbarqueStep";
    public static final String STEP_SELECCION_TURNOS = "seleccionTurnosStep";
    public static final String STEP_SETEO_VALORES = "seteoValoresStep";
    public static final String STEP_CALCULADORA = "calculadoraStep";
    public static final String STEP_CONFIRMAR = "confirmarStep";
    
     public String onFlowProcess(FlowEvent event) {  
        
        if (event.getOldStep().equals(STEP_SELECCION_EMBARQUE) && event.getNewStep().equals(STEP_SELECCION_TURNOS)){
            seleccionarEmbarqueYProveedor();
        } else if (event.getOldStep().equals(STEP_SELECCION_TURNOS) && event.getNewStep().equals(STEP_SETEO_VALORES)){
            seleccionarCargaTurnos();
        } else if (event.getOldStep().equals(STEP_SETEO_VALORES) && event.getNewStep().equals(STEP_CALCULADORA)){
            generarRegistrosCalculadora();
            if (calculadora.getCalculadorasTurno().isEmpty()){
                generarLineasFactura();
                return STEP_CONFIRMAR;
            }
        } else if (event.getOldStep().equals(STEP_CALCULADORA) && event.getNewStep().equals(STEP_CONFIRMAR)){
        //} else if (event.getOldStep().equals(STEP_SETEO_VALORES) && event.getNewStep().equals(STEP_CONFIRMAR)){
            generarLineasFactura();
        } else if  (event.getOldStep().equals(STEP_CONFIRMAR) && event.getNewStep().equals(STEP_CALCULADORA)){
            return STEP_SETEO_VALORES;
        }
                
        return event.getNewStep();  
    }  

    public void seleccionarEmbarqueYProveedor(){
        if (getSelected().getExportador() == null){
            JsfUtil.addErrorMessage("Seleccione un Exportador");
        }
        
        if (getSelected().getEmbarque() == null){
            JsfUtil.addErrorMessage("Seleccione un Embarque");
        }
        
        if (getSelected().getExportador() != null && getSelected().getEmbarque() != null){
            turnosASeleccionarModel = new DualListModel<CargaTurno>();
            List<CargaTurno> cargaTurnosDelExportador = cargaTurnoF.obtenerCargasSinFacturar(getSelected().getEmbarque(), getSelected().getExportador());
            Collections.sort(cargaTurnosDelExportador);
            turnosASeleccionarModel.setSource(new ArrayList<CargaTurno>(cargaTurnosDelExportador));
            turnosASeleccionarModel.setTarget(new ArrayList<CargaTurno>());
                        
            lineasFacturaModel = null;
            lineasFactura = null;
        }        
    }
    
    public void seleccionarCargaTurnos(){
        if (turnosASeleccionarModel != null){
            turnosFacturados = turnoFacturadoF.crearLineas(turnosASeleccionarModel.getTarget(), getSelected());
            turnosFacturadosModel = new ListDataModel(turnosFacturados);
            getSelected().setTurnosFacturadosCollection(turnosFacturados);
        }
    }
    
    public void actualizarTurnosFacturados(){
        TurnoFacturado tfSeleccionado = turnosFacturados.get(turnosFacturadosModel.getRowIndex());
        if (tfSeleccionado != null){
            turnoFacturadoF.actualizarLinea(tfSeleccionado, current);
        }
    }
    
    public void generarLineasFactura(){
        facturaCalculadoraF.aplicarCalculadora(current, calculadora);
        lineasTarifa = lineaFacturaF.crearLineasTarifa(current);
        lineaAdministracion = lineaFacturaF.crearLineaAdministracion(current, calculadora);
        
        lineasConceptos = new ArrayList<LineaFactura>();
        lineasTarifaModel = new ListDataModel(lineasTarifa);
        lineasConceptosModel = new ListDataModel(lineasConceptos);
        actualizarLineas();
    }
    
    public void actualizarLineasCompleto(){
        for (TurnoFacturado tf : turnosFacturados){
            turnoFacturadoF.actualizarLinea(tf, current);
        }
        actualizarLineas();
    }
    
    public void actualizarLineas(){
        List<LineaFactura> lineasFactura = new ArrayList<LineaFactura>();
        if (lineasTarifa != null){
            lineasFactura.addAll(lineasTarifa);
        }
        if (lineaAdministracion != null){
            lineasFactura.add(lineaAdministracion);
        }
        if (lineasConceptos != null) {
            lineasFactura.addAll(lineasConceptos);
        }
        
        int lineaFactura = 1;
        for (LineaFactura lf : lineasFactura){
            if (lf != null){
                lf.setFactura(current);
                lf.setNroLinea(lineaFactura);
                lineaFactura++;
            }
        }
        
        getSelected().setLineaFacturaCollection(lineasFactura);
    }
    
    private void generarRegistrosCalculadora(){
        this.calculadora = facturaCalculadoraF.generarCalculadoraNueva(current);
        /*
        for (TurnoFacturado tf : turnosFacturados){
            if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION)
                ||tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO))
            {
                facturaCalculadoraF.agregarTurno(calculadora, tf, current);
            }
        }
        * */
    }
    
    public void agregarConcepto(){
        lineasConceptos.add(new LineaFactura());
        actualizarLineas();
    }
    
    public void quitarConcepto(){
        lineasConceptos.remove(lineasConceptosModel.getRowIndex());
        actualizarLineas();
    }
    
    public void seleccionarTipoConcepto(){
        LineaFactura lfSeleccionada = lineasConceptos.get(lineasConceptosModel.getRowIndex());
        if (lfSeleccionada.getTipoLinea() != null){
            lfSeleccionada.setImporte(lfSeleccionada.getTipoLinea().getValorDefecto());
        }
        actualizarLineas();
    }
    
    public void generarFactura(){
        lnkFactura = (new FacturaReport(current)).obtenerReportePDF();
    }
    
    public void generarTurnosFacturadosReport(){
        lnkTurnosFacturados = (new TurnosFacturados(current)).obtenerReportePDF();
    }
    
    public Factura getSelected() {
        if (current == null) {
            current = new Factura();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    private FacturaFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        setViewElements();
        return "View";
    }

    public String prepareCreate() {
        current = new Factura();
        selectedItemIndex = -1;
        lineasFactura = null;
        lineasFacturaModel = null;
        turnosFacturados = null;
        turnosFacturadosModel = null;
        return "Create";
    }

    public String create() {
        try {
            //current.setFacturaCalculadoraCollection(facturaCalculadoraF.cleanCalculadora(calculadora));
            
            getFacade().create(current);
        
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaCreated"));
          
            setViewElements();
        
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        performDestroy();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        //updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
        }
    }

    public DataModel getItems() {
        if (items == null) {
            List<Factura> facturas = getFacade().findAll();
            Collections.sort(facturas);
            Collections.reverse(facturas);
            items = new ListDataModel(facturas);
        }
        return items;
    }

    private void recreateModel() {
        items = null;
        turnosASeleccionarModel = null;
        lineasFacturaModel = null;
        lineasFactura = null;
        lnkFactura = null;
        lnkTurnosFacturados = null;
        calculadora = null;
    }
    
    public void crearLibroVentas() {
        try {
            List<Factura> facturasPeriodo = ejbFacade.getFacturasPeriodo(this.mesLibro, this.anioLibro);
            String libroDescripcion = this.anioLibro + "-" + (this.mesLibro < 10? "0" : "") + this.mesLibro;

            this.lnkLibroIVA = (new LibroIVA(facturasPeriodo, libroDescripcion, this.nroPrimeraPagina)).obtenerReportePDF();

            this.nombreLibro = "Descarga Libro Iva Ventas " + libroDescripcion;     
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, e.getMessage());
        }
       
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    private void setViewElements() {
        turnosFacturados = new ArrayList<TurnoFacturado>(current.getTurnosFacturadosCollection());
        Collections.sort(turnosFacturados);
        turnosFacturadosModel = new ListDataModel(turnosFacturados);
        calculadora = facturaCalculadoraF.generarCalculadoraDeFactura(current);
        lineasFactura = new ArrayList<LineaFactura>(current.getLineaFacturaCollection());
        Collections.sort(lineasFactura);
        lineasFacturaModel = new ListDataModel(lineasFactura);
    }

    @FacesConverter(forClass = Factura.class)
    public static class FacturaControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FacturaController controller = (FacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "facturaController");
            return controller.ejbFacade.find(getKey(value));
        }

        Long getKey(String value) {
            Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Factura) {
                Factura o = (Factura) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public DualListModel<CargaTurno> getTurnosASeleccionarModel() {
        return turnosASeleccionarModel;
    }

    public void setTurnosASeleccionarModel(DualListModel<CargaTurno> turnosASeleccionarModel) {
        this.turnosASeleccionarModel = turnosASeleccionarModel;
    }
    
    public DataModel getLineasFacturaModel() {
        return lineasFacturaModel;
    }
    
    public DataModel getTurnosFacturadosModel(){
        return turnosFacturadosModel;
    }

    public Boolean getTabBusquedaAbierto() {
        return tabBusquedaAbierto;
    }

    public void setTabBusquedaAbierto(Boolean tabBusquedaAbierto) {
        this.tabBusquedaAbierto = tabBusquedaAbierto;
    }

    public Boolean getTabSeleccionPlanillasAbrierto() {
        return tabSeleccionPlanillasAbrierto;
    }

    public void setTabSeleccionPlanillasAbrierto(Boolean tabSeleccionPlanillasAbrierto) {
        this.tabSeleccionPlanillasAbrierto = tabSeleccionPlanillasAbrierto;
    }

    public Boolean getTabCalculoAbierto() {
        return tabCalculoAbierto;
    }

    public void setTabCalculoAbierto(Boolean tabCalculoAbierto) {
        this.tabCalculoAbierto = tabCalculoAbierto;
    }

    public Boolean getTabCalculadoraAbierto() {
        return tabCalculadoraAbierto;
    }

    public void setTabCalculadoraAbierto(Boolean tabCalculadoraAbierto) {
        this.tabCalculadoraAbierto = tabCalculadoraAbierto;
    }

    public String getSTEP_SELECCION_EMBARQUE() {
        return STEP_SELECCION_EMBARQUE;
    }

    public String getSTEP_SELECCION_TURNOS() {
        return STEP_SELECCION_TURNOS;
    }

    public String getSTEP_SETEO_VALORES() {
        return STEP_SETEO_VALORES;
    }

    public String getSTEP_CONFIRMAR() {
        return STEP_CONFIRMAR;
    }
    
    public String getSTEP_CALCULADORA() {
        return STEP_CALCULADORA;
    }

    public LineaFactura getLineaAdministracion() {
        return lineaAdministracion;
    }

    public void setLineaAdministracion(LineaFactura lineaAdministracion) {
        this.lineaAdministracion = lineaAdministracion;
    }

    public DataModel getLineasTarifaModel() {
        return lineasTarifaModel;
    }

    public void setLineasTarifaModel(DataModel lineasTarifaModel) {
        this.lineasTarifaModel = lineasTarifaModel;
    }

    public DataModel getLineasConceptosModel() {
        return lineasConceptosModel;
    }

    public void setLineasConceptosModel(DataModel lineasConceptosModel) {
        this.lineasConceptosModel = lineasConceptosModel;
    }

    public String getLnkFactura() {
        return lnkFactura;
    }
    
    public String getLnkTurnosFacturados(){
        return lnkTurnosFacturados;
    }

    public Calculadora getCalculadora() {
        return calculadora;
    }

    public void setCalculadora(Calculadora calculadora) {
        this.calculadora = calculadora;
    }

    public Integer getMesLibro() {
        if (mesLibro == null) {
            mesLibro = (new DateTime()).getMonthOfYear();
        }
        return mesLibro;
    }

    public void setMesLibro(Integer mesLibro) {
        this.mesLibro = mesLibro;
    }

    public Integer getAnioLibro() {
        if (anioLibro == null) {
            anioLibro = (new DateTime()).getYear();
        }
        return anioLibro;
    }

    public void setAnioLibro(Integer anioLibro) {
        this.anioLibro = anioLibro;
    }

    public String getNombreLibro() {
        return nombreLibro;
    }

    public Integer getNroPrimeraPagina() {
        if (nroPrimeraPagina == null) {
            nroPrimeraPagina = 1;
        }
        return nroPrimeraPagina;
    }

    public void setNroPrimeraPagina(Integer nroPrimeraPagina) {
        this.nroPrimeraPagina = nroPrimeraPagina;
    }

    public String getLnkLibroIVA() {
        return lnkLibroIVA;
    }
    
    public FacturaControllerConverter getStaticConverter(){
        return new FacturaControllerConverter();
    }
    
}