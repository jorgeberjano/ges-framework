package es.jbp.ges.userapi.nosql;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de usuarios
 * @author Jorge Berjano
 */
@Repository
public interface RepositorioUsuarios extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByNombre(String nombre);
}
