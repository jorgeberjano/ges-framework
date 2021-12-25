package es.jbp.comun.ges.entidad;

import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.compatibilidad.CompatibilidadSql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jorge
 */
public class OpcionesEnumerado {
    private List<ItemEnumerado> listaItems = new ArrayList<>();
    
    public void agregar(String opcion, Object valor) {
        listaItems.add(new ItemEnumerado(opcion, valor));
    }

    public void agregarEnumeradosBooleanos(List<String> lista) {

        if (lista.size() > 0) {
            listaItems.add(new ItemEnumerado(lista.get(0), false));
        }
        if (lista.size() > 1) {
            listaItems.add(new ItemEnumerado(lista.get(1), true));
        }
    }

    public void agregarEnumeradosEnteros(List<String> lista) {
        Long contador = 0L;
        for (String parte : lista) {
            ItemEnumerado item = new ItemEnumerado(parte);
            if (item.getValor() == null) {
                item.setValor(contador++);
            } else {
                Long valor = Conversion.toLong(item.getValor());
                contador = valor != null ? valor + 1 : contador + 1;                
            }
            listaItems.add(item);
        }
    }
    
    public Object getValor(String texto) {
        if (texto == null) {
            return null;
        }
        for (ItemEnumerado item : listaItems) {
            if (texto.equals(item.getTexto())) {
                return item.getValor();
            }
        }
        return null;
    }
    
    public String getTexto(Object valor) {
        if (valor == null) {
            return null;
        }

        for (ItemEnumerado item : listaItems) {
            if (valor.equals(item.getValor())) {
                return item.getTexto();
            }
        }
        return null;
    }

    public boolean contieneTexto(String texto) {
        if (texto == null) {
            return false;
        }
        for (ItemEnumerado item : listaItems) {
            if (texto.equals(item.getTexto())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getListaTextos() {
        return listaItems.stream().map(e -> e.getTexto()).collect(Collectors.toList());
    }
}
