package es.jbp.comun.ges.filtroyorden;

import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.entidad.Orden;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public class ExpresionOrden implements Orden {

    private List<CondicionOrden> listaCondiciones = new ArrayList<CondicionOrden>();

    public void agregarCondicion(CondicionOrden condicion) {
        if (condicion == null) {
            return;
        }
        listaCondiciones.add(condicion);
    }

    @Override
    public String getDescripcion() {
        return "...";
    }

    @Override
    public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta) {

        StringBuilder sql = new StringBuilder();
        boolean primero = true;
        for (CondicionOrden condicion : listaCondiciones) {
            if (!primero) {
                sql.append(", ");
            } else {
                primero = false;
            }
            sql.append(condicion.generarSql(formateadorSql, consulta));
        }
        return sql.toString();
    }
}
