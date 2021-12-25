package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.conversion.IConversorValores;
import es.jbp.comun.ges.entidad.*;
import es.jbp.comun.ges.consulta.BuilderConsulta;
import es.jbp.comun.ges.excepciones.GesBadRequestException;
import es.jbp.comun.ges.excepciones.GesNotFoundExcepion;
import es.jbp.comun.ges.filtroyorden.ExpresionFiltro;
import es.jbp.comun.ges.filtroyorden.ExpresionOrden;
import es.jbp.comun.ges.filtroyorden.ExpresionPagina;
import es.jbp.comun.ges.entidad.PaginaRxEntidades;
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

    ClavePrimaria crearClavePrimaria(Map<String, ? extends Object> clavePrimaria) throws GesBadRequestException;

    ClavePrimaria crearClavePrimaria(String... valoresClave) throws GesBadRequestException;

    Mono<EntidadGes> getEntidad(ClavePrimaria clavePrimaria) throws GesBadRequestException;

    Mono<EntidadGes> getEntidad(String id) throws GesBadRequestException;

    PaginaRxEntidades<EntidadGes> getPaginaEntidades(Map<String, String> parametros) throws GesBadRequestException;

    PaginaRxEntidades<EntidadGes> getPaginaEntidades(ExpresionFiltro filtro, ExpresionOrden orden, ExpresionPagina pagina) throws GesBadRequestException;

    Flux<EntidadGes> getEntidades(Map<String, String> parametros) throws GesBadRequestException;

    Flux<EntidadGes> getEntidades(ExpresionFiltro filtro, ExpresionOrden orden) throws GesBadRequestException;

    Flux<EntidadGes> getEntidades() throws GesBadRequestException;

    Mono<EntidadGes> insertarEntidad(EntidadGes entidad) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> insertarEntidadJson(String json) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> modificarEntidad(EntidadGes entidad) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> modificarEntidadJson(String json) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> modificarEntidadJson(ClavePrimaria clave, String json) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> modificarEntidadJson(String id, String json) throws GesBadRequestException, GesNotFoundExcepion;

    Mono<EntidadGes> borrarEntidad(ClavePrimaria clave) throws GesBadRequestException;

    Mono<EntidadGes> borrarEntidadPorId(String id) throws GesBadRequestException, GesNotFoundExcepion;

    void exportar(OutputStream out, String formato, Map<String, String> params) throws GesBadRequestException;

    BuilderConsulta builder();

    MapaValores convertirAValoresUI(Map<String, ? extends Object> mapaOriginal);

    MapaValores convertirAValoresBD(Map<String, ? extends Object> mapaOriginal);
}
