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
    private String mensajeError;

    @Override
    public String getMensajeError() {
        return mensajeError;
    }

    @Override
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
    
    @Override
    public void setServicioEntidad(IServicioEntidad servicio) {
        this.servicioEntidad = servicio;
    }
    
    @Override
    public boolean validar(EntidadGes entidad, OperacionCrud operacion) {
        return validar(entidad);
    }
    
    public boolean validar(EntidadGes entidad) {
        return true;
    }

    @Override
    public EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion) {
        switch (operacion) {            
            case INSERCCION:
            case MODIFICACION:
                return guardando(entidad);
            default:
                return entidad;
        }
    }

    @Override
    public EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion) {        
        switch (operacion) {
            case CONSULTA:
                return consultando(entidad);
                
            case INSERCCION:
                inserccionRealizada(entidad);
                break;
            case MODIFICACION:
                modificionRealizada(entidad);
                break;            
        }
        return entidad;
    }
    
    public EntidadGes consultando(EntidadGes entidad) {
        return entidad;
    }

    @Deprecated
    public EntidadGes guardando(EntidadGes entidad) {
        return entidad;
    }

    public void inserccionRealizada(EntidadGes entidad) {        
    }
    
    public void modificionRealizada(EntidadGes entidad) {
    }
}
