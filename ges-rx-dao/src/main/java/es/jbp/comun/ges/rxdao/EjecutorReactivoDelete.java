package es.jbp.comun.ges.rxdao;

import es.jbp.comun.ges.rxdao.conexion.GestorConexionesReactivas;

public class EjecutorReactivoDelete extends EjecutorReactivoComando {

    public EjecutorReactivoDelete(GestorConexionesReactivas gestorConexiones) {
        super(gestorConexiones);
    }

    @Override
    public void setTabla(String tabla) {
        sentenciaSql.delete(tabla);
    }

    @Override
    public void agregarCampo(String nombreSqlCampo, Object valor) {
    }
}
