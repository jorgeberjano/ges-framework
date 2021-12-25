package es.jbp.ges.dao.utilidades;

import es.jbp.ges.dao.AccesoEntidadesGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.Filtro;
import es.jbp.ges.entidad.Orden;
import es.jbp.ges.entidad.Pagina;
import es.jbp.ges.utilidades.GestorSimbolos;
import java.util.List;
import es.jbp.comun.utiles.sql.GestorConexiones;
import es.jbp.comun.utiles.sql.PaginaEntidades;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import es.jbp.comun.utiles.swing.tabla.ModeloTablaAbstracto;

/**
 * Modelo generico para tablas.
 *
 * @author jberjano
 */
public class ModeloTablaGes extends ModeloTablaAbstracto {

    private List<EntidadGes> listaCompleta;
    private List<EntidadGes> listaActual;
    private ConsultaGes consulta;
    private final GestorConexiones gestorConexiones;
    private final GestorSimbolos gestorSimbolos = new GestorSimbolos();
    private Filtro filtro;
    private Orden orden;

    private class PaginaModelo implements Pagina {

        @Override
        public Integer getIndicePrimerElemento() {
            return 0;
        }

        @Override
        public Integer getNumeroElementos() {
            return 100;
        }
    }

    public ModeloTablaGes(ConsultaGes consulta, GestorConexiones gestorConexiones) {
        this.consulta = consulta;
        this.gestorConexiones = gestorConexiones;
        actualizar();
    }

    public List<EntidadGes> getListaObjetos() {
        return this.listaCompleta;
    }

    public EntidadGes getFila(int indice) {
        if (listaActual == null || indice < 0 || indice >= listaActual.size()) {
            return null;
        }
        return listaActual.get(indice);
    }

    @Override
    public String getColumnName(int nColumna) {
        List<CampoGes> listaColumnas = consulta.getCampos();
        if (nColumna < 0 || nColumna >= listaColumnas.size()) {
            return "";
        }
        return listaColumnas.get(nColumna).getTitulo();
    }

    @Override
    public int getRowCount() {
        return listaActual != null ? listaActual.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return consulta.getCampos().size();
    }

    private boolean esColumnaValida(int nColumna) {
        return nColumna >= 0 && nColumna < consulta.getCampos().size();
    }

    public String getIdCampo(int nColumna) {
        return esColumnaValida(nColumna) ? consulta.getCampos().get(nColumna).getIdCampo() : null;
    }

    @Override
    public String getTitulo(int nColumna) {
        return esColumnaValida(nColumna) ? consulta.getCampos().get(nColumna).getTitulo() : "";
    }

    @Override
    public Integer getAncho(int nColumna) {
        return esColumnaValida(nColumna) ? consulta.getCampos().get(nColumna).getLongitud() * 6 : null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        EntidadGes fila = getFila(rowIndex);
        if (fila == null) {
            return null;
        }
        String idCampo = getIdCampo(columnIndex);
        return fila.getValor(idCampo);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

//    public Object getObjectAt(int rowIndex) {
//        Object objeto = null;
//        if (listaActual != null && rowIndex >= 0 && listaActual.size() > rowIndex) {
//            objeto = listaActual.get(rowIndex);
//        }
//        return objeto;
//    }
    public void ordenarPor(int indice, boolean ordenAscendente) {
        Orden orden = new Orden() {
            @Override
            public String getDescripcion() {
                return null;
            }

            @Override
            public String generarSql(FormateadorSql formateadorSql, ConsultaGes consulta) {
                return "ORDER BY " + indice;
            }
        };
        actualizar();
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public void setFiltro(Filtro filtro) {
        this.filtro = filtro;
    }

    public void actualizar() {
        if (consulta == null || gestorConexiones == null || gestorSimbolos == null) {
            return;
        }
        AccesoEntidadesGes acceso = new AccesoEntidadesGes(consulta, gestorConexiones, gestorSimbolos);
        PaginaEntidades<EntidadGes> pagina = acceso.getPagina(filtro, orden, new PaginaModelo());
        listaCompleta = pagina.getListaEntidades();
        listaActual = listaCompleta;

        fireTableDataChanged();
    }
}
