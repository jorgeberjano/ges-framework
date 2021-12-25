package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.MapaValores;

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
