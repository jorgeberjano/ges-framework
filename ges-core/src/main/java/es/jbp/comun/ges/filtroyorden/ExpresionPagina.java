package es.jbp.comun.ges.filtroyorden;

import es.jbp.comun.ges.entidad.Pagina;


/**
 * Expresion de pagina para obtener entidades.
 *
 * @author jberjano
 */
public class ExpresionPagina implements Pagina {

    private Integer pagina;
    private Integer limite;
    private Integer primero;
    private Integer ultimo;

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Integer getLimite() {        
        return limite;
    }

    public void setLimite(Integer limite) {
        this.limite = limite;
    }

    public Integer getPrimero() {
        return primero;
    }

    public void setPrimero(Integer primero) {
        this.primero = primero;
    }

    public Integer getUltimo() {
        return ultimo;
    }

    public void setUltimo(Integer ultimo) {
        this.ultimo = ultimo;
    }

    @Override
     public Integer getIndicePrimerElemento() {
        Integer lim = getLimite();
        if (primero == null && pagina != null && lim != null) {
            return pagina * lim;
        } else if (primero == null && ultimo != null && lim != null) {
            int prim = ultimo - lim;
            return prim >= 0 ? prim : 0;
        } else if (primero == null) {
            return 0;
        }
        return primero;
    }
     
    @Override
     public Integer getNumeroElementos() {
        if (limite == null) {
            return primero != null && ultimo != null ? ultimo - limite : null;
        }
        return limite;
     }
        
}
