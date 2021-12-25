package es.jbp.comun.ges.rxdao.interfaces;

import es.jbp.comun.ges.rxdao.ConstructorReactivoEntidades;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface IEjecutorComando {

    <T> Flux<T> ejecutar(ConstructorReactivoEntidades<T> constructor);

    void setTabla(String tabla);

    void agregarPk(String nombreSqlCampo, Object valor);

    void agregarCampo(String nombreSqlCampo, Object valor);

}
