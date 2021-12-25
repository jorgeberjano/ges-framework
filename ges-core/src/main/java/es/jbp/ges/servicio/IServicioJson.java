package es.jbp.ges.servicio;

import es.jbp.ges.entidad.ClavePrimaria;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.EntidadGes;

/**
 * Contratro del servicio para serialización Json
 * @author jberjano
 */
public interface IServicioJson {
    //MapaValores toMapaValores(String json);

    String toJson(Object objeto);

    EntidadGes toEntidad(String json, ConsultaGes consulta);

    EntidadGes toEntidad(ClavePrimaria clave, String json, ConsultaGes consulta);
}
