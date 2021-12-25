package es.jbp.comun.ges.rxdao.conexion;

import es.jbp.comun.ges.servicio.IServicioConexion;
import es.jbp.comun.utiles.depuracion.GestorLog;
import es.jbp.comun.utiles.sql.GestorConexiones;
import es.jbp.comun.utiles.sql.PoolConexiones;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona las conexiones a la BD
 * @author jorge
 */
@Service
public class ServicioGestorConexionesReactivas implements IServicioConexion {
    
    private GestorConexionesReactivas gestorConexiones;
    
    @Override
    public void inicializarConexion(String driver, String uri, String host, String database, String user, String password) {
        gestorConexiones = new GestorConexionesReactivas(driver, host, database, user, password);
    }
    
    public GestorConexionesReactivas getGestorConexiones() {
        return gestorConexiones;
    }
    
}
