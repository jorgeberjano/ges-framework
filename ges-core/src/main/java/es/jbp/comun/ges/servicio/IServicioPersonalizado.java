package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.crud.OperacionCrud;
import es.jbp.comun.ges.entidad.EntidadGes;

/**
 * Contrato para los manipuladores de entidades.
 * Sirven para personalizar la forma en la que se obtienen las entidades de la base de datos y en la que se graban
 * definiendo métodos que se pueden sobreescribir y que seran invocados antes y despues de cada operación.
 *
 * @author jberjano
 */
public interface IServicioPersonalizado {

    void setServicioEntidad(IServicioEntidad servicio);

    boolean validar(EntidadGes entidad, OperacionCrud operacion);

    EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion);

    EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion);

    void setMensajeError(String mensaje);

    String getMensajeError();


}
