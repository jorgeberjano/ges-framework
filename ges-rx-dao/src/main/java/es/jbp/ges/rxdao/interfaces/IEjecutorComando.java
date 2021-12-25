package es.jbp.ges.rxdao.interfaces;

import es.jbp.ges.rxdao.ConstructorReactivoEntidades;
import reactor.core.publisher.Flux;

public interface IEjecutorComando {

    <T> Flux<T> ejecutar(ConstructorReactivoEntidades<T> constructor);

    void setTabla(String tabla);

    void agregarPk(String nombreSqlCampo, Object valor);

    void agregarCampo(String nombreSqlCampo, Object valor);

}
