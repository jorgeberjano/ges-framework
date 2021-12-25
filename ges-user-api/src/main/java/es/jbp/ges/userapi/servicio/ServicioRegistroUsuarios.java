package es.jbp.ges.userapi.servicio;

import es.jbp.ges.userapi.dto.UsuarioRegistro;
import es.jbp.ges.userapi.excepciones.ApiException;
import es.jbp.ges.userapi.nosql.RepositorioUsuarios;
import es.jbp.ges.userapi.nosql.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Funcionalidad de servicio para registro de usuario
 */
@Service
@AllArgsConstructor
public class ServicioRegistroUsuarios  {

    private RepositorioUsuarios repositorioUsuarios;

    private PasswordEncoder passwordEncoder;

    public boolean registrarUsuario(UsuarioRegistro usuarioRegistro) throws ApiException {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioRegistro.getNombre());
        usuario.setContrasena(passwordEncoder.encode(usuarioRegistro.getContrasena()));
        usuario.setCodigoEmpresa(usuarioRegistro.getEmpresa());
        usuario = repositorioUsuarios.save(usuario);
        if (usuario == null) {
            throw new ApiException("No se ha podido registrar el usuario");
        }
        return true;
    }

//    public boolean existeUsuario(String nombre) {
//        Usuario usuario = repositorioUsuarios.findByNombre(nombre);
//        return usuario != null;
//    }

}
