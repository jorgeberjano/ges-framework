package es.jbp.ges.servicio;

import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.excepciones.GesNotFoundExcepion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jorge
 */
@Service(value = "metadatos")
public class ServicioMetadatos implements IServicioMetadatos {

    private IServicioGes servicioGes;
    private IServicioJson servicioJson;

    public ServicioMetadatos(IServicioGes servicioGes, IServicioJson servicioJson) {
        this.servicioGes = servicioGes;
        this.servicioJson = servicioJson;
    }

    @Override
    public String getMetadatosConsultasJson(String idioma) throws GesNotFoundExcepion {
        List<ConsultaGes> consultas = servicioGes.getConsultas(idioma);
        if (consultas == null || consultas.size() == 0) {         
            throw new GesNotFoundExcepion("No se han definido consultas para el idioma "+ idioma);
        }
        
        return servicioJson.toJson(consultas);
    }

    @Override
    public String getMetadatosConsultaJson(String idioma, String idConsulta) throws GesNotFoundExcepion {
        ConsultaGes consulta = servicioGes.getConsultaPorId(idioma, idConsulta);
        if (consulta == null) {         
            throw new GesNotFoundExcepion("No existe la consulta " + idConsulta);
        }
        
        return servicioJson.toJson(consulta);
    }

    @Override
    public String getIdsConsultas(String idioma) {
        List<String> idsConsultas = servicioGes.getConsultas(idioma).stream()
                .map(ConsultaGes::getIdConsulta)
                .collect(Collectors.toList());
        return servicioJson.toJson(idsConsultas);
    }
}
