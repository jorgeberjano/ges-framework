package es.jbp.comun.ges.utilidades;

import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.Plantilla;

import java.util.HashMap;
import java.util.Map;

/**
 * La clase que gestiona los símbolos que pueden ser sustituidos en las cadenas
 * (sentencias sql, formatos, ...) del archivo ges.
 * @author Jorge Berjano
 */
public class GestorSimbolos {
    
    private Map<String, Object> mapaSimbolos;

    public GestorSimbolos() {
        this.mapaSimbolos = new HashMap();
    }
    
    public GestorSimbolos(Map<String, Object> mapaSimbolos) {
        this.mapaSimbolos = mapaSimbolos;
    }

    public Map<String, Object> getMapaSimbolos() {
        return mapaSimbolos;
    }

    public void setMapaSimbolos(Map<String, Object> mapaSimbolos) {
        this.mapaSimbolos = mapaSimbolos;
    }
    
    public void asignarValorSimbolo(String simbolo, Object valor) {
        mapaSimbolos.put(simbolo, valor);
    }    
    
    public void asignarValorSimbolos(Map<String, String> mapa) {
        for (String simbolo : mapa.keySet()) {
            asignarValorSimbolo(simbolo, mapa.get(simbolo));
        }
    }
    
    public Object obtenerValorSimbolo(String simbolo) {
        return mapaSimbolos.get(simbolo);
    }
    
    public String sustituirSimbolosYParametros(String strTexto) {
        return sustituirSimbolos(strTexto, false);
    }
    
    public String sustituirSoloSimbolos(String strTexto) {
        return sustituirSimbolos(strTexto, true);
    }
    
    private String sustituirSimbolos(String strTexto, boolean soloSimbolos) {
        if (Conversion.isBlank(strTexto)) {
            return strTexto;
        }
        Plantilla plantilla = new Plantilla(strTexto, null);
        plantilla.setSoloSimbolos(soloSimbolos);
        mapaSimbolos.keySet().stream().forEach((k) -> plantilla.definirSimbolo(k, mapaSimbolos.get(k)));
        
        String resultado = plantilla.getResultado();
        return resultado;
    } 

    public void agregarSimbolos(Map<String, Object> mapaSimbolos) {
        if (mapaSimbolos == null) {
            return;
        }
        this.mapaSimbolos.putAll(mapaSimbolos);
    }
}
