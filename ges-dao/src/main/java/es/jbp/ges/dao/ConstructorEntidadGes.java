package es.jbp.ges.dao;

import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.comun.utiles.sql.AdaptadorResultSet;
import es.jbp.comun.utiles.sql.ConstructorEntidad;

import java.util.List;

/**
 *
 * @author jberjano
 */
public class ConstructorEntidadGes extends ConstructorEntidad<EntidadGes> {

    private ConsultaGes consulta;

    public ConstructorEntidadGes(ConsultaGes consulta) {
        this.consulta = consulta;
    }

    @Override
    protected EntidadGes construirEntidad(AdaptadorResultSet rs) throws Exception {
        EntidadGes entidad = new EntidadGes();
        
        List<CampoGes> campos = consulta.getCampos();
        
        for (CampoGes campo : campos) {
            if (campo.tieneEstilo(CampoGes.CAMPO_SOLO_FILTRO)) {
                continue;
            }
            String idCampo = campo.getIdCampo();
            Object valor = rs.get(campo);
            entidad.setValor(idCampo, valor);
            if (campo.isClave()) {
                entidad.setValorClavePrimaria(idCampo, valor);
            }
        }
       
        return entidad;
    }
}
