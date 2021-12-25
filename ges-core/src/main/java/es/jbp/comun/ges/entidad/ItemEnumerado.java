package es.jbp.comun.ges.entidad;

import es.jbp.comun.utiles.conversion.Conversion;
import java.io.Serializable;

/**
 *
 * @author jberjano
 */
public class ItemEnumerado implements Serializable {
    private Object valor;
    private String texto;

    public ItemEnumerado(String texto, Object valor) {
        this.texto = texto;
        this.valor = valor;
    }

    public ItemEnumerado(String cadena) {
        int indice = cadena.indexOf('=');
        if (indice < 0) {
            texto = cadena;
        } else {
            texto = cadena.substring(0, indice);
            valor = Conversion.toLong(cadena.substring(indice + 1));
        }
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
