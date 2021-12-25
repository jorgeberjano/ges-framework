package es.jbp.comun.ges.rxdao;

import es.jbp.comun.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.comun.ges.rxdao.interfaces.IEjecutorSentencia;
import es.jbp.comun.utiles.sql.*;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import es.jbp.comun.ges.rxdao.interfaces.IEjecutorComando;

import java.io.IOException;
import java.net.URL;

/**
 * Clase base para los objetos de acceso a datos.
 *
 * @author jberjano
 */
public abstract class DaoReactivo {

    protected GestorConexionesReactivas gestorConexiones;
    private static Listener listener;
    private static boolean trazaSoloErrores = true;

    public interface Listener {
        void trazaSql(String sql);
    }

    public DaoReactivo(GestorConexionesReactivas gestorConexiones) {
        this.gestorConexiones = gestorConexiones;
    }

    protected static void trazaSql(final String sql, boolean exito) {
        if (listener == null) {
            return;
        }
        if (trazaSoloErrores && exito) {
            return;
        }
        listener.trazaSql(sql);
    }

    protected final FormateadorSql getFormateadorSql() {
        return gestorConexiones.getFormateadorSql();
    }

    protected EjecutorReactivoConsulta crearEjecutorSentenciaSelect() {
        return new EjecutorReactivoConsulta(gestorConexiones);
    }

    protected IEjecutorComando crearEjecutorSentenciaInsert() {
        return new EjecutorReactivoInsert(gestorConexiones);
    }

    protected IEjecutorComando crearEjecutorSentenciaUpdate() {
        return new EjecutorReactivoUpdate(gestorConexiones);
    }

    protected IEjecutorComando crearEjecutorSentenciaDelete() {
        return new EjecutorReactivoDelete(gestorConexiones);
    }

    protected IEjecutorSentencia crearEjecutorSentenciaSimple() {
        return new IEjecutorSentencia() {
            @Override
            public boolean ejecutar() throws Exception {
                return false;
            }
        };
    }
    
    public Plantilla crearPlantillaSql(String sql) {
        return new Plantilla(sql, gestorConexiones.getFormateadorSql());
    }
    
    public Plantilla crearPlantillaSql(URL url) throws IOException {
        return new Plantilla(url, gestorConexiones.getFormateadorSql());
    }

}
