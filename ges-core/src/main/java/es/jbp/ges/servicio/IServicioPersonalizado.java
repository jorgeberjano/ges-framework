package es.jbp.ges.servicio;

import es.jbp.ges.crud.OperacionCrud;
import es.jbp.ges.entidad.EntidadGes;

/**
 * Contrato para los servicios personalizados.
 * Sirven para personalizar la forma en la que se obtienen las entidades de la base de datos y en la que se graban
 * definiendo métodos que se pueden sobreescribir y que serán invocados antes y después de cada operación.
 *
 * @author jberjano
 */
public interface IServicioPersonalizado {

    String getIdConsulta();

    void setServicioEntidad(IServicioEntidad servicio);

    void validar(EntidadGes entidad, OperacionCrud operacion);

    EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion);

    EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion);

}
