package es.jbp.ges.userapi.recursos;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.excepciones.GesNotFoundExcepion;
import es.jbp.ges.servicio.IServicioGes;
import es.jbp.ges.userapi.nosql.EstadoConsulta;
import es.jbp.ges.userapi.servicio.ServicioEstadosConsultas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecursoEstadosConsultas {

    private ServicioEstadosConsultas servicioConsultasPersonalizadas;

    private IServicioGes servicioGes;

    @GetMapping(value="/estados_consultas", produces={ MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Obtener los estados de las consultas",
            description = "Obtiene los estados de todas las consulta personalizadas por un usuario")
    public List<EstadoConsulta> obtenerEstadosConsultas() {
        try {
           return servicioConsultasPersonalizadas.obtenerEstadosConsultas(getNombreUsuario());
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/estados_consultas/{idConsulta}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Obtener el estado de una consulta",
            description = "Obtiene el estado de una consulta personalizada para un usuario")
    public EstadoConsulta obtenerEstadoConsulta(
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {

        try {
            return servicioConsultasPersonalizadas.obtenerConsultaPersonalizada(getNombreUsuario(), idConsulta);
        } catch (GesNotFoundExcepion e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/estados_consultas/{idConsulta}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Asigna el estado de una consulta",
            description = "Asigna el estado de una consulta personalizada para un usuario")
    public void asignarEstadoConsulta(
            @RequestBody EstadoConsulta estadoConsulta,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {

        servicioConsultasPersonalizadas.guardarEstadoConsulta(getNombreUsuario(), idConsulta, estadoConsulta);
    }

    @PutMapping(value = "/estados_consultas/{idConsulta}/campos", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Asigna los campos que se muestran en una consulta",
            description = "Asigna los campos de una consulta personalizada para un usuario")
    public void asignarCamposEstadoConsulta(
            @RequestBody List<CampoGes> campos,
            @Parameter(required = true, description = "Identificador de la consulta")
            @PathVariable final String idConsulta) {

        servicioConsultasPersonalizadas.guardarCamposEstadoConsulta(getNombreUsuario(), idConsulta, campos);
    }

    private String getNombreUsuario() {
        Optional<String> optionalUsuario = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(Object::toString);

        if (optionalUsuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se ha identificado al usuario");
        }
        return optionalUsuario.get();
    }

    // TODO: implementar idioma por usuario
    private String getIdiomaUsuario() {
        return "es";
    }



}
