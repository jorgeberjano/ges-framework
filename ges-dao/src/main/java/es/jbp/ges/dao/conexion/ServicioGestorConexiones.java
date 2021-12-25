package es.jbp.ges.dao.conexion;

import es.jbp.ges.servicio.IServicioConexion;
import es.jbp.comun.utiles.depuracion.GestorLog;
import es.jbp.comun.utiles.sql.GestorConexiones;
import es.jbp.comun.utiles.sql.PoolConexiones;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona las conexiones a la BD
 * @author jorge
 */
@Service
public class ServicioGestorConexiones implements IServicioConexion {
    
    private PoolConexiones gestorConexiones;

    @Override
    public void inicializarConexion(String driver, String uri, String host, String database, String username, String password) {
        gestorConexiones = new PoolConexiones(driver, uri, username, password);

        try {
            gestorConexiones.inicializar();
        } catch (ClassNotFoundException ex) {
            GestorLog.error("No se ha podido inicializar el gestor de conexiones", ex);
        }
    }
    
    public GestorConexiones getGestorConexiones() {
        return gestorConexiones;
    }


}
