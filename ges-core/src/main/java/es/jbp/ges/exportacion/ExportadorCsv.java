package es.jbp.ges.exportacion;

import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.utilidades.ConversionValores;
import reactor.core.publisher.Flux;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Genera exportacion en CSV a partir de los datos de sesión de una tabla.
 *
 * @author jberjano
 */
public class ExportadorCsv implements Exportador {

    /**
     * Crea un informe a partir de los datos de sesión de una tabla
     */
    public ExportadorCsv() {
    }

    /**
     * Genera la exportación
     */
    public void generar(OutputStream outputStream, ConsultaGes consulta, Flux<EntidadGes> entidades) throws Exception {
                
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        
        for (EntidadGes entidad : entidades.toIterable()) {

            boolean primero = true;
            for (CampoGes campo : consulta.getCampos()) {
                if (campo.isOculto()) {
                    continue;
                }
                if (primero) {
                    primero = false;                    
                } else {
                    writer.append(";");
                }
                Object valor = entidad.getValor(campo.getIdCampo());
                String texto = ConversionValores.aValorUI(valor, campo);
                writer.append(texto);                
            }
            writer.append("\n");
        }
        writer.flush();
    }
}
