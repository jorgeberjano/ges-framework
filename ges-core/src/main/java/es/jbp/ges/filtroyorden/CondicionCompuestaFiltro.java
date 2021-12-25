package es.jbp.ges.filtroyorden;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa condición de filtro compuesta por otras condiciones que se evaluarán
 * mediante un operador logico AND u OR.
 * @author jberjano
 */
public class CondicionCompuestaFiltro implements CondicionFiltro {
    private final List<CondicionFiltro> listaCondiciones = new ArrayList<>();
    private final String operador;
    private String mensajeError;

    public CondicionCompuestaFiltro(String operador) {
        this.operador = operador;
    }    
        
    public void agregarCondicion(CondicionFiltro condicion) {
        if (condicion == null) {
            return;
        }
        listaCondiciones.add(condicion);
    }
    
    @Override
    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta, IConversorValores conversorValores) {
        
        if (listaCondiciones.isEmpty()) {
            return "";
        }
        
        StringBuilder sql = new StringBuilder();
        if (listaCondiciones.size() > 1) {
            sql.append("(");
        }
        boolean primero = true;
        for (CondicionFiltro condicion : listaCondiciones) {
            String condicionSql = condicion.generarSql(formateadorSql, consulta, conversorValores);
            if (condicionSql == null) {
                mensajeError = condicion.getMensajeError();
                return null;                
            }
            if (!Conversion.isBlank(condicionSql)) {
                if (!primero) {
                    sql.append(" ");
                    sql.append(operador);
                    sql.append(" ");
                } else {
                    primero = false;
                }
                sql.append(condicionSql);
                
            }
        }
        if (listaCondiciones.size() > 1) {
            sql.append(")");
        }
        return sql.toString();
    }

    public String getMensajeError() {
        return mensajeError;
    }
}
