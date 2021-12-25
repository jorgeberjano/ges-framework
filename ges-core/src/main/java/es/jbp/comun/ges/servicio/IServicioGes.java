package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.entidad.ConsultaGes;
import es.jbp.comun.ges.utilidades.GestorSimbolos;

import java.util.List;
import java.util.Map;
import es.jbp.comun.ges.conversion.IConversorValores;

/**
 * Contratro del servicio que proporciona la funcionalidad GES.
 * @author jberjano
 */
public interface IServicioGes {
       
    void crearGestor(String idioma, String archivoGes, Map<String, Object> simbolos);

    ConsultaGes getConsultaPorId(String idioma, String idConsulta);

    List<ConsultaGes> getConsultas(String idioma);    

    GestorSimbolos getGestorSimbolos(String idioma);    
    
    void definirSimbolo(String idioma, String nombre, String valor);

    void registrarServicioPersonalizado(String idConsulta, IServicioPersonalizado servicio);
    
    void registrarConversorValores(IConversorValores manipulador);
    
    IServicioPersonalizado obtenerServicioPersonalizado(String idConsulta);    

    IConversorValores obtenerConversorValores();
}
