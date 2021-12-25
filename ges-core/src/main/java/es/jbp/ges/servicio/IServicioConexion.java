package es.jbp.ges.servicio;

/**
 *
 * @author jorge
 */
public interface IServicioConexion {
    void inicializarConexion(String driver, String uri, String host, String database, String username, String password);
}
