package es.jbp.comun.ges.servicio;

import es.jbp.comun.ges.excepciones.GesNotFoundExcepion;

/**
 *
 * @author jorge
 */
public interface IServicioMetadatos {

    public String getMetadatosConsultasJson(String idioma) throws GesNotFoundExcepion;

    public String getMetadatosConsultaJson(String idioma, String idConsulta) throws GesNotFoundExcepion;
    
    public String getIdsConsultas(String idioma);
}
