package es.jbp.ges.api.recursos;

import es.jbp.ges.excepciones.GesNotFoundExcepion;
import es.jbp.ges.servicio.IServicioMetadatos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador para los recursos
 * @author jorge
 */
@RestController
@RequestMapping("")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecursoMetadatos {

    @Autowired
    @Qualifier("metadatos")
    private IServicioMetadatos servicioMetadatos;

    private String idioma = "es";
    
    @GetMapping(value="/entidades", produces={ MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Obtener nombre de consultas de entidades", description = "Obtiene un array con los nombres de las consultas de entidades disponibles")
    public String obtenerConsultas() {
        return servicioMetadatos.getIdsConsultas(idioma);
    }
    
    @GetMapping(value="/metadatos", produces={ MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Obtener metadatos", description = "Obtiene los metadatos de todas las consultas")
    public String obtenerMetadatos() {

        try {
            return servicioMetadatos.getMetadatosConsultasJson(idioma);
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener metadatos de una consulta", description = "Obtiene los metadatos de una consulta de entidades indicando su identificador")
    @GetMapping(value="/metadatos/{idConsulta}", produces={ MediaType.APPLICATION_JSON_VALUE })
    public String obtenerMetadatosConsulta(
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {
        try {
            return servicioMetadatos.getMetadatosConsultaJson(idioma, idConsulta);
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }    
}
