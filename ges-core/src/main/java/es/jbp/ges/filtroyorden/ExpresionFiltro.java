package es.jbp.ges.filtroyorden;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.Filtro;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

/**
 * Expresión de un filtro mediante una lista de condiciones que se evaluarán 
 * mediante un operador logico OR
 * @author jberjano
 */
public class ExpresionFiltro implements Filtro {

    private final CondicionCompuestaFiltro condiciones = new CondicionCompuestaFiltro("AND");
    private final IConversorValores conversorValores;
    
    public ExpresionFiltro(IConversorValores conversorValores) {
        this.conversorValores = conversorValores;
    }

    @Override
    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta) {
        return condiciones.generarSql(formateadorSql, consulta, conversorValores);
    }

    public void agregarCondicion(CondicionFiltro condicion) {
        condiciones.agregarCondicion(condicion);
    }

    @Override
    public String getDescripcion() {
        return "...";
    }

    @Override
    public String getMensajeError() {
        return condiciones.getMensajeError();
    }
}
