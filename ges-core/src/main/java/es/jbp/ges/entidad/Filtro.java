package es.jbp.ges.entidad;

import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

/**
 * Filtro para los accesos a listas de entidades.
 * @author Jorge Berjano
 */
public interface Filtro {
    String getDescripcion();
    String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta);
    String getMensajeError();
}
