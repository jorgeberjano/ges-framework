package es.jbp.ges.utilidades;

import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.TipoDato;
import es.jbp.comun.utiles.tiempo.FechaAbstracta;
import es.jbp.ges.entidad.CampoGes;

import java.util.Optional;

/**
 * Conversión de valores en formato BD, API y UI.
 *
 * @author jberjano
 */
public class ConversionValores {

    public static String aValorTexto(Object valor, CampoGes campo) {

        if (campo.tieneOpciones() && (campo.getTipoDato() == TipoDato.ENTERO || campo.getTipoDato() == TipoDato.BOOLEANO)) {
            return campo.formatearOpcion(valor);
        }
        return Conversion.toString(valor);
    }

    /**
     * Convierte el valor de un campo al valor JSON que maneja la API
     */
    public static Object aValorJson(Object valor, CampoGes campo) {

        TipoDato tipoDato = campo.getTipoDato();
        if (tipoDato == TipoDato.ENTERO && campo.tieneOpciones()) {
            if (valor instanceof String && campo.getOpcionesEnumerado().contieneTexto((String) valor)) {
                return valor;
            } else {
                return campo.formatearOpcion(valor);
            }
        } else if (tipoDato == TipoDato.BYTES && valor instanceof byte[]) {
            return Conversion.toString(valor);
        } else if (tipoDato == TipoDato.FECHA) {
            return Conversion.toFecha(valor);
        } else if (tipoDato == TipoDato.FECHA_HORA) {
            return Conversion.toFechaHora(valor);
        } else {
            return convertirValorSimple(valor, campo.getTipoDato());
        }
    }

    public static Object aValorBD(Object valor, CampoGes campo) {

        if (campo.tieneOpciones() && valor instanceof String) {
            String textoOpcion = (String) valor;
            if (campo.esOpcion(textoOpcion)) {
                valor = campo.parsearOpcion(textoOpcion);
            }
        } else if (campo.getTipoDato() == TipoDato.FECHA) {
            return Optional.ofNullable(Conversion.toFecha(valor))
                    .map(FechaAbstracta::getLocalDate)
                    .orElse(null);
        } else if (campo.getTipoDato() == TipoDato.FECHA_HORA) {
            return Optional.ofNullable(Conversion.toFecha(valor))
                    .map(FechaAbstracta::getLocalDateTime)
                    .orElse(null);
        }
        return convertirValorSimple(valor, campo.getTipoDato());
    }

    public static Object convertirValorSimple(Object valor, TipoDato tipoDato) {
        switch (tipoDato) {
            case CADENA:
                return Conversion.toString(valor);
            case ENTERO:
                return Conversion.toLong(valor);
            case REAL:
                return Conversion.toDouble(valor);
            case BOOLEANO:
                return Conversion.toBoolean(valor);
            default:
                return valor;
        }
    }

//    // TODO: esto debe ser dependiente del tipo de base de datos
//    public static String aFormatoSql(Object valor) {
//        if (valor == null) {
//            return "null";
//        } else if (valor instanceof Boolean) {
//            return (Boolean) valor ? "1" : "0";
//        } else if (valor instanceof Integer
//                || valor instanceof Long
//                || valor instanceof Double
//                || valor instanceof Float) {
//            return valor.toString();
//        } else if (valor instanceof FechaAbstracta) {
//            return "'" + valor.toString() + "'";
//        } else {
//            // Se sustituyen las comillas por dos comillas (código de escape)
//            return "'" + valor.toString().replace("'", "''") + "'";
//        }
//    }
}
