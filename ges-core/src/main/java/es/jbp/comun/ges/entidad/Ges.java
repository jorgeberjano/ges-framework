package es.jbp.comun.ges.entidad;

import es.jbp.comun.ges.utilidades.GestorSimbolos;
import es.jbp.comun.utiles.sql.compatibilidad.CompatibilidadSql;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clase que contiene la información de un archivo .ges
 * @author Jorge Berjano
 */
public class Ges {

    public final static int GES_ESTILO_MAXIMIZAR = 0x0001;
    public final static int GES_ESTILO_LETRA_GRANDE = 0x0002;
    public final static int GES_ESTILO_CONSULTAS_NO_MODALES = 0x0004;

    private int version;
    private String nombreBaseDatos;
    private BaseDatosGes baseDatos;
    private int estilo;
    private List<ConsultaGes> consultasPantalla = new ArrayList();
    private List<ConsultaGes> consultasImpresora = new ArrayList();

    private GestorSimbolos gestorSimbolos = new GestorSimbolos();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNombreBaseDatos() {
        return nombreBaseDatos;
    }

    public void setNombreBaseDatos(String nombreBaseDatos) {
        this.nombreBaseDatos = nombreBaseDatos;
    }

    public BaseDatosGes getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(BaseDatosGes baseDatos) {
        this.baseDatos = baseDatos;
    }

    public int getEstilo() {
        return estilo;
    }

    public void setEstilo(int estilo) {
        this.estilo = estilo;
    }

    public List<ConsultaGes> getConsultasPantalla() {
        return consultasPantalla;
    }

    public void setConsultasPantalla(List<ConsultaGes> consultasPantalla) {
        this.consultasPantalla = consultasPantalla;
    }

    public List<ConsultaGes> getConsultasImpresora() {
        return consultasImpresora;
    }

    public void setConsultasImpresora(List<ConsultaGes> listaConsultasImpresora) {
        this.consultasImpresora = listaConsultasImpresora;
    }

    public ConsultaGes getConsultaPorId(String idConsulta) {
        for (ConsultaGes consulta : consultasPantalla) {
            if (idConsulta.equals(consulta.getIdConsulta())) {
                return consulta;
            }
        }
        return null;
    }

    public void definirSimbolo(String nombre, String valor) {
        gestorSimbolos.asignarValorSimbolo(nombre, valor);
    }

    public GestorSimbolos getGestorSimbolos() {
        return gestorSimbolos;
    }

    public void definirSimbolos(Map<String, Object> mapaSimbolos) {
        gestorSimbolos.agregarSimbolos(mapaSimbolos);
    }   
}
