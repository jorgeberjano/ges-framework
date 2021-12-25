package es.jbp.ges.filtroyorden;

/**
 * Operadores de las condiciones que forman las expresiones de los filtros.
 * @author jorge
 */
public enum OperadorFiltro {
    EQ("="),
    NEQ("<>"),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),   
    LIKE("like"),
    IS("is");
    
    private String simbolo;

    private OperadorFiltro(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }
}
