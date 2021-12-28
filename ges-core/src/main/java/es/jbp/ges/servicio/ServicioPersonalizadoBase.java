package es.jbp.ges.servicio;

import es.jbp.ges.crud.OperacionCrud;
import es.jbp.ges.entidad.EntidadGes;

/**
 * Implementación base de un servicio personalizado
 *
 * @author jberjano
 */
public class ServicioPersonalizadoBase implements IServicioPersonalizado {
    protected IServicioEntidad servicioEntidad;

    @Override
    public String getIdConsulta() {
        return "";
    }

    @Override
    public void setServicioEntidad(IServicioEntidad servicio) {
        this.servicioEntidad = servicio;
    }
    
    @Override
    public void validar(EntidadGes entidad, OperacionCrud operacion) {
    }


    @Override
    public EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion) {
        return entidad;
    }

    @Override
    public EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion) {        
        return entidad;
    }
}
