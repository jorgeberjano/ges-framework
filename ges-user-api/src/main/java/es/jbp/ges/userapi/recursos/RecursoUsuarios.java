package es.jbp.ges.userapi.recursos;

import es.jbp.ges.userapi.dto.UsuarioRegistro;
import es.jbp.ges.userapi.excepciones.ApiException;
import es.jbp.ges.userapi.servicio.ServicioRegistroUsuarios;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
public class RecursoUsuarios {

    private ServicioRegistroUsuarios servicioUsuarios;

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Registrar usuario", description = "Registra un usario de una empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Éxito. La respuesta contiene el JSON con los valores de la entidad insertada"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor"),
            @ApiResponse(responseCode = "400", description = "Petición mal formada")
    })
    public void registrarUsuario(
            @Parameter(required = true, description = "Entidad en formato JSON")
            @RequestBody UsuarioRegistro usuarioRegistro) {

        try {
            servicioUsuarios.registrarUsuario(usuarioRegistro);
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido registrar el usuario", e);
        }


    }
}
