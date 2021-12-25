package es.jbp.ges.rxdao;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.EntidadGes;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.List;

public class ConstructorReactivoEntidadesGes extends ConstructorReactivoEntidades<EntidadGes> {
    private ConsultaGes consulta;

    public ConstructorReactivoEntidadesGes(ConsultaGes consulta) {
        this.consulta = consulta;
    }

    @Override
    public EntidadGes obtenerEntidad(Row row, RowMetadata rowMetadata) {
        EntidadGes entidad = new EntidadGes();

        List<CampoGes> campos = consulta.getCampos();

        for (CampoGes campo : campos) {
            if (campo.tieneEstilo(CampoGes.CAMPO_SOLO_FILTRO)) {
                continue;
            }
            String idCampo = campo.getIdCampo();
            Object valor = row.get(campo.getNombre());
            entidad.setValor(idCampo, valor);
            if (campo.isClave()) {
                entidad.setValorClavePrimaria(idCampo, valor);
            }
        }

        return entidad;
    }
}
