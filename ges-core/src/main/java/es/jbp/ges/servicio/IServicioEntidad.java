package es.jbp.ges.servicio;

import es.jbp.ges.conversion.IConversorValores;
import es.jbp.ges.consulta.BuilderConsulta;
import es.jbp.ges.entidad.*;
import es.jbp.ges.excepciones.GesBadRequestException;
import es.jbp.ges.excepciones.GesNotFoundExcepion;
import es.jbp.ges.filtroyorden.ExpresionFiltro;
import es.jbp.ges.filtroyorden.ExpresionOrden;
import es.jbp.ges.filtroyorden.ExpresionPagina;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.util.Map;

/**
 * Contratro del servicio para manipulación de entidades.
 *
 * @author jberjano
 */
public interface IServicioEntidad {

    void asignarConsulta(String idioma, String nombre);

    ConsultaGes getConsulta();

    IConversorValores getConversorValores();

    ClavePrimaria crearClavePrimaria(Map<String, ? extends Object> clavePrimaria);

    ClavePrimaria crearClavePrimaria(String... valoresClave);

    Mono<EntidadGes> getEntidad(ClavePrimaria clavePrimaria);

    Mono<EntidadGes> getEntidad(String id);

    PaginaRxEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros);

    PaginaRxEntidades<EntidadGes> getPaginaEntidades(ExpresionFiltro filtro, ExpresionOrden orden, ExpresionPagina pagina);

    Flux<EntidadGes> getEntidades(Map<String, String> parametros);

    Flux<EntidadGes> getEntidades(ExpresionFiltro filtro, ExpresionOrden orden);

    Flux<EntidadGes> getEntidades() throws GesBadRequestException;

    Mono<EntidadGes> insertarEntidad(EntidadGes entidad);

    Mono<EntidadGes> insertarEntidadJson(String json);

    Mono<EntidadGes> modificarEntidad(EntidadGes entidad);

    Mono<EntidadGes> modificarEntidadJson(String json);

    Mono<EntidadGes> modificarEntidadJson(ClavePrimaria clave, String json);

    Mono<EntidadGes> modificarEntidadJson(String id, String json);

    Mono<EntidadGes> borrarEntidad(ClavePrimaria clave);

    Mono<EntidadGes> borrarEntidadPorId(String id);

    void exportar(OutputStream out, String formato, Map<String, String> params);

    BuilderConsulta builder();

    MapaValores convertirAValoresTexto(Map<String, ? extends Object> mapaOriginal);

    MapaValores convertirAValoresBD(Map<String, ? extends Object> mapaOriginal);

    MapaValores convertirAValoresJson(Map<String, ? extends Object> mapaOriginal);
}
