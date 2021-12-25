package es.jbp.ges.rxdao;

import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.ges.rxdao.interfaces.IEjecutorConsulta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ejecutor reactivo de sentencias select.
 *
 * @author jorge
 */
public class EjecutorReactivoConsulta implements IEjecutorConsulta {

    private final GestorConexionesReactivas gestorConexiones;

    public EjecutorReactivoConsulta(GestorConexionesReactivas gestorConexiones) {
        this.gestorConexiones = gestorConexiones;
    }

    public <T> Mono<T> obtenerEntidad(String sentencia, ConstructorReactivoEntidades<T> constructor) {

        return obtenerEntidades(sentencia, constructor)
                .take(1)
                .next()
                .onErrorResume(t -> procesarError(t, sentencia));
    }

    private <T> Mono<? extends T> procesarError(Throwable throwable, String sentencia) {
        System.out.println(sentencia);
        return Mono.error(throwable);
    }

    public <T> Flux<T> obtenerEntidades(String sentencia, ConstructorReactivoEntidades<T> constructor) {
        return Mono.from(gestorConexiones.getConexion())
                .flatMapMany(connection -> connection
                        .createStatement(sentencia)
                        .execute())
                .flatMap(result -> result.map(constructor::obtenerEntidad));
    }

    public <T> Flux<T> obtenerPaginaEntidades(String sentencia, ConstructorReactivoEntidades<T> constructor,
                                              int indicePrimerElemento, int numeroElementos) {
        return obtenerEntidades(sentencia, constructor).skip(indicePrimerElemento).take(numeroElementos);
    }
}
