package es.jbp.ges.serializacion;

import es.jbp.ges.entidad.Ges;

/**
 *
 * @author jorge
 */
public interface SerializadorGes {
    public void serializar(String nombreArchivo, Ges gestor) throws Exception;
    public Ges deserializarArchivo(String nombreArchivo) throws Exception;
    public Ges deserializarRecurso(String nombreRecurso) throws Exception;
}
