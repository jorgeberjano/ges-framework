package es.jbp.ges.userapi.servicio;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.userapi.nosql.EstadoConsulta;
import es.jbp.ges.userapi.nosql.RepositorioEstadoConsulta;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Funcionalidad de servicio para consultas personalizadas.
 */
@Service
@AllArgsConstructor
public class ServicioEstadosConsultas {

    private RepositorioEstadoConsulta repositorioConsultaPersonalizada;

    public void guardarEstadoConsulta(String nombreUsuario, String idConsulta, EstadoConsulta estadoConsulta) {
        if (nombreUsuario == null || repositorioConsultaPersonalizada == null) {
            return;
        }
        repositorioConsultaPersonalizada.save(estadoConsulta);
    }

    public void guardarCamposEstadoConsulta(String nombreUsuario, String idConsulta, List<CampoGes> campos) {
        if (nombreUsuario == null || repositorioConsultaPersonalizada == null) {
            return;
        }
        EstadoConsulta estadoConsulta
                = repositorioConsultaPersonalizada.findByIdConsultaAndNombreUsuario(idConsulta, nombreUsuario);
        if (estadoConsulta == null) {
            estadoConsulta = new EstadoConsulta();
        }
        estadoConsulta.setIdConsulta(idConsulta);
        estadoConsulta.setNombreUsuario(nombreUsuario);
        estadoConsulta.setCampos(campos);
        repositorioConsultaPersonalizada.save(estadoConsulta);
    }

    public EstadoConsulta obtenerConsultaPersonalizada(String nombreUsuario, String idConsulta) {
        if (nombreUsuario == null || repositorioConsultaPersonalizada == null) {
            return null;
        }
        EstadoConsulta consultaPersonalizada
                = repositorioConsultaPersonalizada.findByIdConsultaAndNombreUsuario(idConsulta, nombreUsuario);
        if (consultaPersonalizada == null) {
            return null;
        }
        return consultaPersonalizada;
    }

    public List<EstadoConsulta> obtenerEstadosConsultas(String nombreUsuario) {
        if (nombreUsuario == null || repositorioConsultaPersonalizada == null) {
            return null;
        }
        List<EstadoConsulta> consultaPersonalizada
                = repositorioConsultaPersonalizada.findByNombreUsuario(nombreUsuario);
        if (consultaPersonalizada == null) {
            return null;
        }
        return consultaPersonalizada;
    }


}
