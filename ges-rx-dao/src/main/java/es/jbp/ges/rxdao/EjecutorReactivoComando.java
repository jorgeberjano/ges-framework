package es.jbp.ges.rxdao;

import es.jbp.ges.rxdao.conexion.GestorConexionesReactivas;
import es.jbp.ges.rxdao.interfaces.IEjecutorComando;
import es.jbp.comun.utiles.sql.sentencia.SentenciaSql;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public abstract class EjecutorReactivoComando implements IEjecutorComando {

    protected final GestorConexionesReactivas gestorConexiones;
    protected final SentenciaSql sentenciaSql = new SentenciaSql();
    protected final Map<String, Object> mapaParametros = new HashMap<>();

    public EjecutorReactivoComando(GestorConexionesReactivas gestorConexiones) {
        this.gestorConexiones = gestorConexiones;
    }

    public <T> Flux<T> ejecutar(ConstructorReactivoEntidades<T> constructor) {
        return Mono.from(gestorConexiones.getConexion())
                .flatMapMany(c -> Flux.from(crearSentencia(c).execute())
                        .flatMap(result -> result.map(constructor::obtenerEntidad))
                );
    }

    protected Statement crearSentencia(Connection connection) {
        String sql = sentenciaSql.getSql();
        Statement statement = connection.createStatement(sql);
        mapaParametros.forEach(statement::bind);
        return statement;
    }

    @Override
    public void agregarCampo(String nombreSqlCampo, Object valor) {
        if (valor != null) {
            String nombreParametro = agregarParametro(valor);
            sentenciaSql.asignarValorSql(nombreSqlCampo, nombreParametro);
        } else {
            sentenciaSql.asignarValorSql(nombreSqlCampo, "null");
        }
    }

    public void agregarPk(String nombreSqlCampo, Object valor) {
        if (valor != null) {
            sentenciaSql.where(nombreSqlCampo + " = " + gestorConexiones.getFormateadorSql().formatear(valor));
        } else {
            sentenciaSql.where(nombreSqlCampo + " is null");
        }
    }

    public String agregarParametro(Object valor) {
        int indice = mapaParametros.keySet().size();
        String nombreParametro = gestorConexiones.getFormateadorSql().getNombreParametro(indice);
        mapaParametros.put(nombreParametro, valor);
        return nombreParametro;
    }

}
