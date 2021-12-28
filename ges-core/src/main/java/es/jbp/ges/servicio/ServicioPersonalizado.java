package es.jbp.ges.servicio;

import es.jbp.ges.crud.OperacionCrud;
import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.utilidades.ConversionEntidades;
import reactor.core.publisher.Mono;

/**
 * Implementación base de un servicio personalizado
 *
 * @author jberjano
 */
public abstract class ServicioPersonalizado<T>  implements IServicioPersonalizado {
    private final Class clazz;
    protected IServicioEntidad servicioEntidad;

    public ServicioPersonalizado(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setServicioEntidad(IServicioEntidad servicio) {
        this.servicioEntidad = servicio;
    }
    
    @Override
    public void validar(EntidadGes entidadGes, OperacionCrud operacion) {
        T entidadObjeto = ConversionEntidades.crearEntidadObjeto(entidadGes, clazz);
        validar(entidadObjeto, operacion);
    }

    public void validar(T entidad, OperacionCrud operacion) {
    }

    @Override
    public EntidadGes preOperacion(EntidadGes entidad, OperacionCrud operacion) {
        T entidadObjeto = ConversionEntidades.crearEntidadObjeto(entidad, clazz);
        entidadObjeto = preOperacion(entidadObjeto, operacion);
        ConversionEntidades.deEntidadObjetoAEntidadGes(entidadObjeto, entidad);
        return entidad;
    }

    protected T preOperacion(T entidadObjeto, OperacionCrud operacion) {
        return entidadObjeto;
    }

    @Override
    public EntidadGes postOperacion(EntidadGes entidad, OperacionCrud operacion) {
        T entidadObjeto = ConversionEntidades.crearEntidadObjeto(entidad, clazz);
        entidadObjeto = postOperacion(entidadObjeto, operacion);
        ConversionEntidades.deEntidadObjetoAEntidadGes(entidadObjeto, entidad);
        return entidad;
    }

    protected T postOperacion(T entidadObjeto, OperacionCrud operacion) {
        return entidadObjeto;
    }

    public T getEntidad(String id) {
        Mono<EntidadGes> entidadGes = servicioEntidad.getEntidad(id);
        return ConversionEntidades.crearEntidadObjeto(entidadGes.block(), clazz);
    }
}
