package es.jbp.ges.userapi.nosql;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio para los ojetos de consultas personalizadas
 * @author jorge
 */
public interface RepositorioEstadoConsulta extends MongoRepository<EstadoConsulta, String> {

    EstadoConsulta findByIdConsultaAndNombreUsuario(String idConsulta, String nombreUsuario);

    @Override
    EstadoConsulta save(EstadoConsulta consultaPersonalizada);

    List<EstadoConsulta> findByNombreUsuario(String nombreUsuario);
}
