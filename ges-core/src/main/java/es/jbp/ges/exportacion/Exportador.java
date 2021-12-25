package es.jbp.ges.exportacion;

import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.ConsultaGes;
import reactor.core.publisher.Flux;

import java.io.OutputStream;

/**
 * Exportador
 * @author jorge
 */
public interface Exportador {
    void generar(OutputStream outputStream, ConsultaGes consulta, Flux<EntidadGes> listaEntidades) throws Exception;
}
