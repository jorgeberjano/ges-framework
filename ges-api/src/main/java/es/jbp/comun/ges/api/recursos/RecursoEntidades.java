package es.jbp.comun.ges.api.recursos;

import es.jbp.comun.ges.entidad.MapaValores;
import es.jbp.comun.ges.entidad.PaginaRxEntidades;
import es.jbp.comun.ges.excepciones.GesBadRequestException;
import es.jbp.comun.ges.excepciones.GesNotFoundExcepion;
import es.jbp.comun.ges.servicio.IServicioEntidad;
import es.jbp.comun.ges.entidad.ClavePrimaria;
import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.utiles.conversion.Conversion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/{idioma}/entidades/{idConsulta}")
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "X-Total-Count")
public class RecursoEntidades {

    @Autowired
    IServicioEntidad servicio;

    /**
     * Creates a new instance of RecursoEntidad
     */
    public RecursoEntidades() {
    }

    /**
     * Obtiene una lista de entidades.
     */
    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidades", description = "Obtiene la lista de entidades de una consulta según los parámetros de filtro, orden y paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con la lista de entidades"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public Flux<MapaValores> obtenerEntidades(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros de filtro, orden y paginación")
            @RequestParam Map<String, String> params,
            HttpServletResponse response) {

        servicio.asignarConsulta(idioma, idConsulta);

        PaginaRxEntidades<EntidadGes> entidades = null;
        try {
            entidades = servicio.getPaginaEntidades(params);
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        response.addHeader("X-Total-Count", Conversion.toString(entidades.getNumeroTotalEntidades().block()));
        return extraerValores(entidades.getEntidades());
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidad", description = "Obtiene una entidad por su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con la entidad"),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> obtenerEntidadPorId(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.getEntidad(id));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Mono<MapaValores> extraerValores(Mono<EntidadGes> entidad) {
        return entidad.map(EntidadGes::getValores).map(servicio::convertirAValoresUI);
    }

    private Flux<MapaValores> extraerValores(Flux<EntidadGes> entidad) {
        return entidad.map(EntidadGes::getValores);
    }

    @GetMapping(value = "/{id1}/{id2}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener entidad", description = "Obtiene una entidad por los valores de su clave primaria doble.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con la entidad"),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> obtenerEntidadPorIdDoble(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.getEntidad(servicio.crearClavePrimaria(id1, id2)));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificada por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con los valores de la entidad"),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador."),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public Mono<MapaValores> modificarEntidadPorId(
            @RequestBody String content,
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.modificarEntidadJson(id, content));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id1}/{id2}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificando su clave primaria doble.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con los valores de la entidad"),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria."),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> modificarEntidadPorIdDoble(
            @RequestBody String content,
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.modificarEntidadJson(servicio.crearClavePrimaria(id1, id2), content));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Modificar entidad", description = "Modifica una entidad especificando valores de sus campos en el cuerpo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con los valores de la entidad"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> modificarEntidad(
            @Parameter(required = true, description = "Entidad en formato JSON")
            @RequestBody String entidadJson,
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros que indican los valores de los campos clave")
            @RequestParam Map<String, String> params) {

        servicio.asignarConsulta(idioma, idConsulta);

        ClavePrimaria clavePrimaria = null;
        try {
            clavePrimaria = servicio.crearClavePrimaria(params);

            if (clavePrimaria.isEmpty()) {
                return extraerValores(servicio.modificarEntidadJson(entidadJson));
            } else {
                return extraerValores(servicio.modificarEntidadJson(clavePrimaria, entidadJson));
            }
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Insertar entidad", description = "Inserta una entidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con los valores de la entidad insertada"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> insertarEntidad(
            @Parameter(required = true, description = "Entidad en formato JSON")
            @RequestBody String entidadJson,
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.insertarEntidadJson(entidadJson));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Borrar entidad", description = "Borra una entidad especificada por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito."),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con ese identificador."),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public Mono<MapaValores> borrarEntidadPorId(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Identificador de la entidad")
            @PathVariable final String id) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.borrarEntidadPorId(id));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id1}/{id2}")
    @Operation(summary = "Borrar entidad", description = "Borra una entidad especificada por su clave primaria doble")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito."),
            @ApiResponse(responseCode = "404", description = "No existe la entidad con esa clave primaria."),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> borrarEntidadPorIdDoble(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Primer valor de la clave primaria")
            @PathVariable final String id1,
            @Parameter(required = true, description = "Segundo valor de la clave primaria")
            @PathVariable final String id2) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            return extraerValores(servicio.borrarEntidad(servicio.crearClavePrimaria(id1, id2)));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(value = "")
    @Operation(summary = "Borrar entidad con clave multiple", description = "Borra una entidad especificando valores de filtro de uno o varios campos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito."),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public Mono<MapaValores> borrarEntidadPorFiltro(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Parámetros que indican los valores de los campos clave")
            @RequestParam Map<String, String> params) {

        servicio.asignarConsulta(idioma, idConsulta);

        try {
            ClavePrimaria clave = servicio.crearClavePrimaria(params);
            return extraerValores(servicio.borrarEntidad(clave));
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Exportar entidades", description = "Obtiene un archivo en un formato determinado con los datos de una consulta indicando filtro y orden")
    @GetMapping(value = "/exportacion/{formato}", produces = {MediaType.APPLICATION_PDF_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. En el cuerpo de la respuesta va el archivo de exportación"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public void exportar(
            @Parameter(required = true, description = "Idioma en el que se proporcionan los datos que estén internacionalizados (es, en, ...)")
            @PathVariable final String idioma,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta,
            @Parameter(required = true, description = "Formato a exportar (pdf, csv, xlsx)")
            @PathVariable final String formato,
            @Parameter(required = true, description = "Parámetros de filtro y orden")
            @RequestParam Map<String, String> params,
            HttpServletResponse response) {

        servicio.asignarConsulta(idioma, idConsulta);

        ServletOutputStream out;
        try {
            response.setContentType("application/" + formato);
            response.setHeader("Content-disposition", "attachment; filename=" + idConsulta + "." + formato);
            out = response.getOutputStream();
        } catch (IOException ex) {
            return;
        }
        try {
            servicio.exportar(out, formato, params);
        } catch (GesBadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
