package es.jbp.ges.rxdao;

import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;

public class EjecutorReactivoUpdate extends EjecutorReactivoComando {

    public EjecutorReactivoUpdate(GestorConexionesReactivas gestorConexiones) {
        super(gestorConexiones);
    }

    @Override
    public void setTabla(String tabla) {
        sentenciaSql.update(tabla);
    }


}
