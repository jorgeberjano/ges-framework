package es.jbp.ges.userapi.servicio;

import es.jbp.ges.userapi.nosql.RepositorioUsuarios;
import es.jbp.ges.userapi.nosql.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Funcionalidad de servicio para login de usuario
 */
@Service
@AllArgsConstructor
public class ServicioLoginUsuario implements UserDetailsService {

    private RepositorioUsuarios repositorioUsuarios;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Usuario> usuario = repositorioUsuarios.findByNombre(username);

        if (usuario != null && usuario.isPresent()) {
            return usuario.map(this::construirUserDetails).get();
        } else {
            return comprobarAdmin(username);
        }
    }

    private UserDetails comprobarAdmin(String username) {
        if (Objects.equals(username, "admin")) {
            return User.builder()
                    .username(username)
                    .password("$2a$12$Y0qth9P/OTLRUFmNs21RNuzc/f0.CCxnADv0WB0866ylGEfJaIQJ2") // capis1993 encriptado
                    .authorities(emptyList())
                    .build();
        }
        throw new UsernameNotFoundException("El usuario " + username + " no existe");
    }

    private UserDetails construirUserDetails(Usuario usuario) {
        return User.builder()
                .username(usuario.getNombre())
                .password(usuario.getContrasena())
                .authorities(emptyList())
                .build();
    }
}
