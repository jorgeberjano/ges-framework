package es.jbp.ges.rxdao;

import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;

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
