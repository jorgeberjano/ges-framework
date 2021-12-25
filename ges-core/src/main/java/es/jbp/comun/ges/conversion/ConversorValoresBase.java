package es.jbp.comun.ges.conversion;

import es.jbp.comun.ges.entidad.EntidadGes;
import es.jbp.comun.ges.entidad.CampoGes;


/**
 * Implementación vacia de un manipulador de valores
 * @author jberjano
 */
public class ConversorValoresBase implements IConversorValores {

    @Override
    public void consultando(EntidadGes entidad, CampoGes campo) {        
    }

    @Override
    public void guardando(EntidadGes entidad, CampoGes campo) {        
    }

    @Override
    public Object aValorApi(Object valorBd, CampoGes campo) {
        return valorBd;
    }

    @Override
    public Object aValorBd(Object valorApi, CampoGes campo) {
        return valorApi;
    }
}
