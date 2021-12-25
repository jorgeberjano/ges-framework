package es.jbp.comun.ges.exportacion;

import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.ConsultaGes;
import reactor.core.publisher.Flux;

import java.io.OutputStream;

/**
 * Exportador
 * @author jorge
 */
public interface Exportador {
    void generar(OutputStream outputStream, ConsultaGes consulta, Flux<EntidadGes> listaEntidades) throws Exception;
}
