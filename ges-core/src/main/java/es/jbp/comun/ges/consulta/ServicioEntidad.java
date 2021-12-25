package es.jbp.comun.ges.consulta;

import es.jbp.comun.ges.conversion.IConversorValores;
import es.jbp.comun.ges.entidad.ConsultaGes;

public interface ServicioEntidad {

    IConversorValores getConversorValores();

    ConsultaGes getConsulta();
}
