package es.jbp.comun.ges.rxdao;

import es.jbp.comun.ges.rxdao.conexion.GestorConexionesReactivas;

public class EjecutorReactivoUpdate extends EjecutorReactivoComando {

    public EjecutorReactivoUpdate(GestorConexionesReactivas gestorConexiones) {
        super(gestorConexiones);
    }

    @Override
    public void setTabla(String tabla) {
        sentenciaSql.update(tabla);
    }


}
