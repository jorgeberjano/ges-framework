package es.jbp.ges.consulta;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.filtroyorden.*;

/**
 * Builder para consulta de entidades
 * @author jorge
 */
public class BuilderConsulta {

    private ExpresionFiltro expresionFiltro;
    private ExpresionOrden expresionOrden;
    private ExpresionPagina expresionPagina;
    
    private IConversorValores conversorValores;
    private ConsultaGes consulta;
    
    public BuilderConsulta(ConsultaGes consulta, IConversorValores conversorValores) {
        this.conversorValores = conversorValores;
        this.consulta = consulta;
    }

    public BuilderConsulta filtrar(String idCampo, OperadorFiltro operadorFiltro, Object valor) {
        if (expresionFiltro == null) {
            expresionFiltro = new ExpresionFiltro(conversorValores);
        }
        CampoGes campo = consulta.getCampoPorId(idCampo);
        CondicionSimpleFiltro condicion = new CondicionSimpleFiltro(campo, operadorFiltro, valor);
        expresionFiltro.agregarCondicion(condicion);
        return this;
    }
    
    public BuilderConsulta ordenar(String idCampo, boolean descendente) {
        if (expresionOrden == null) {
            expresionOrden = new ExpresionOrden();            
        }
        CampoGes campo = consulta.getCampoPorId(idCampo);
        CondicionOrden condicion = new CondicionOrden(campo, descendente);
        expresionOrden.agregarCondicion(condicion);
        return this;
    }

    public BuilderConsulta limite(Integer limite) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setLimite(limite);
        return this;
    }
    
    public BuilderConsulta pagina(Integer numeroPagina) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setLimite(numeroPagina);
        return this;
    }
    
    public BuilderConsulta primero(Integer indice) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setPrimero(indice);
        return this;
    }
    
    public BuilderConsulta ultimo(Integer indice) {
        if (expresionPagina == null) {
            expresionPagina = new ExpresionPagina(); 
        }
        expresionPagina.setUltimo(indice);
        return this;
    }

    public ExpresionFiltro getExpresionFiltro() {
        return expresionFiltro;
    }

    public ExpresionOrden getExpresionOrden() {
        return expresionOrden;
    }
    
    public ExpresionPagina getExpresionPagina() {
        return expresionPagina;
    }

    public ExpresionConsulta getExpresionConsulta() {
        return ExpresionConsulta.builder()
                .expresionFiltro(expresionFiltro)
                .expresionOrden(expresionOrden)
                .expresionPagina(expresionPagina)
                .build();
    }
}
