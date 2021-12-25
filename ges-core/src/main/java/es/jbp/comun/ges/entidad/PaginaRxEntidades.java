package es.jbp.comun.ges.entidad;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Página de entidades que contiene la lista de entidades y el total de entidades
 * disponibles
 *
 * @author jberjano
 */
@Data
@Builder
public class PaginaRxEntidades<T> {

    private Flux<T> entidades;
    private Mono<Long> numeroTotalEntidades;
}


