package es.jbp.comun.ges.rxdao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

public abstract class ConstructorReactivoEntidades<T> {

    public abstract T obtenerEntidad(Row row, RowMetadata rowMetadata);
}
