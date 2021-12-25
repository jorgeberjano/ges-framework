package es.jbp.ges.servicio;

import es.jbp.ges.excepciones.GesNotFoundExcepion;

/**
 *
 * @author jorge
 */
public interface IServicioMetadatos {

    public String getMetadatosConsultasJson(String idioma) throws GesNotFoundExcepion;

    public String getMetadatosConsultaJson(String idioma, String idConsulta) throws GesNotFoundExcepion;
    
    public String getIdsConsultas(String idioma);
}
