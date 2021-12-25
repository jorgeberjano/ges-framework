package es.jbp.comun.ges.rxdao;

import es.jbp.comun.ges.rxdao.conexion.GestorConexionesReactivas;

public class EjecutorReactivoInsert extends EjecutorReactivoComando {

    public EjecutorReactivoInsert(GestorConexionesReactivas gestorConexiones) {
        super(gestorConexiones);
    }

    @Override
    public void setTabla(String tabla) {
        sentenciaSql.insert(tabla);
    }

    @Override
    public void agregarPk(String nombreSqlCampo, Object valor) {
    }
}
