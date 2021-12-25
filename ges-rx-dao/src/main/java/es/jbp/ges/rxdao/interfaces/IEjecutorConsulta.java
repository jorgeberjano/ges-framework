package es.jbp.ges.rxdao.interfaces;

import es.jbp.ges.rxdao.ConstructorReactivoEntidades;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IEjecutorConsulta {
    <T> Mono<T> obtenerEntidad(String sentencia, ConstructorReactivoEntidades<T> constructor);
    <T> Flux<T> obtenerEntidades(String sentencia, ConstructorReactivoEntidades<T> constructor);
    <T> Flux<T> obtenerPaginaEntidades(String sentencia, ConstructorReactivoEntidades<T> constructor,
                                              int indicePrimerElemento, int numeroElementos);
}
