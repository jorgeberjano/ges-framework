package es.jbp.ges.userapi.dto;


/**
 * Clase que sirve para obtener los datos del formulario de la pagina de login
 *
 * @author jberjano
 */
public class UsuarioLogin {

    private String nombre;
    private String contrasena;

    public UsuarioLogin() {
    }

    public UsuarioLogin(String nombre, String contrasena) {
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
