package es.jbp.ges.filtroyorden;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

/**
 * Contrato para las clases que representan condiciones de filtro
 * @author jberjano
 */
public interface CondicionFiltro {
    String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta, IConversorValores manipuladorValores);
    String getMensajeError();
}
