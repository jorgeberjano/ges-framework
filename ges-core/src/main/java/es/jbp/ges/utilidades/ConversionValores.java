package es.jbp.ges.utilidades;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.TipoDato;

/**
 * Conversión de valores en formato BD, API y UI.
 *
 * @author jberjano
 */
public class ConversionValores {

    public static String aValorUI(Object valor, CampoGes campo) {

        if (campo.tieneOpciones() && (campo.getTipoDato() == TipoDato.ENTERO || campo.getTipoDato() == TipoDato.BOOLEANO)) {
            return campo.formatearOpcion(valor);
        }
        return Conversion.toString(valor);
    }

    /**
     * Convierte el valor de un campo al valor que maneja la API Rest
     *
     * @param valor
     * @param campo
     * @return
     */
    public static Object aValorAPI(Object valor, CampoGes campo) {

        TipoDato tipoDato = campo.getTipoDato();
        if (tipoDato == TipoDato.ENTERO && campo.tieneOpciones()) {
            if (valor instanceof String && campo.getOpcionesEnumerado().contieneTexto((String) valor)) {
                return valor;
            } else {
                return campo.formatearOpcion(valor);
            }
        } else if (tipoDato == TipoDato.BYTES && valor instanceof byte[]) {
            return Conversion.toString(valor);
        }            
        return Conversion.convertirValor(valor, campo.getTipoDato());
    }

    public static Object aValorBD(Object valor, CampoGes campo) {

        if (campo.tieneOpciones() && valor instanceof String) {
            String textoOpcion = (String) valor;
            if (campo.esOpcion(textoOpcion)) {
                valor = campo.parsearOpcion(textoOpcion);
            }
        }
        return Conversion.convertirValor(valor, campo.getTipoDato());
    }
}
