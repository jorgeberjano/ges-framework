package es.jbp.comun.ges.entidad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import es.jbp.comun.utiles.conversion.Conversion;
import es.jbp.comun.utiles.sql.Campo;
import es.jbp.comun.utiles.sql.TipoDato;
import java.util.Base64;

/**
 * Representa un campo de una consulta GES
 *
 * @author Jorge
 */
public class CampoGes implements Campo, Serializable, Cloneable {

    // Estilos
    public final static int CAMPO_NO_EDITABLE = 0x000001;
    public final static int CAMPO_NO_ACTUALIZABLE_EN_EDICION = 0x000002;
    public final static int CAMPO_SIEMPRE_MAYUSCULAS = 0x000004;
    public final static int CAMPO_MOSTRAR_BOTON_LUPA = 0x000008;
    public final static int CAMPO_REQUERIDO = 0x000010;
    public final static int CAMPO_SELECCION_OBLIGATORIA = 0x000020;
    public final static int CAMPO_CLAVE = 0x000040;
    public final static int CAMPO_FORMATO_CON_UNIDAD = 0x000080;
    public final static int CAMPO_NO_EDITABLE_EN_SUBCONSULTA = 0x000100;
    public final static int CAMPO_SOLO_LECTURA = 0x000200;
    public final static int CAMPO_SELECCION_RECOMENDABLE = 0x000400;
    public final static int CAMPO_IMPRIMIR_ACUMULADO = 0x000800;
    public final static int CAMPO_ACUMULAR_MEDIA = 0x001000;
    public final static int CAMPO_OCULTO = 0x002000;
    public final static int CAMPO_FILTRO_PREVIO_OBLIGATORIO = 0x004000;
    public final static int CAMPO_OCULTAR_SUBTOTALES_DE_GRUPO = 0x008000;
    public final static int CAMPO_SALTO_PAGINA_AL_SUBTOTALIZAR = 0x010000;
    public final static int CAMPO_NO_MODIFICABLE = 0x020000;
    public final static int CAMPO_PROPONER_VALORES = 0x040000;
    public final static int CAMPO_NO_NULO = 0x080000;
    public final static int CAMPO_NO_SUBTOTALIZAR = 0x100000;
    public final static int CAMPO_ORDEN_INICIAL = 0x200000;
    public final static int CAMPO_SOLO_FILTRO = 0x400000;

    public final static int ALINEACION_IZQUIERDA = 0;
    public final static int ALINEACION_DERECHA = 1;
    public final static int ALINEACION_CENTRO = 2;

    // Propiedades del archivo ges
    private String idCampo;
    private String nombre;
    private String tabla;
    private String campo;
    private String titulo;
    private TipoDato tipoDato;
    private TipoRolGes tipoRol;
    private int alineacion;
    private int longitud;
    private String formato;
    private int tamano;
    private String unidad;
    private int decimales;
    private int estilo;
    private String expresionGT;
    private String formatoGT;
    private String consultaSeleccion;
    private String idCampoRelacion;
    private String idCampoSeleccion;
    private String valorNulo;
    private String valorPorDefecto;

//    private List<String> opciones;
//    private List<String> opcionesNetas;
    private OpcionesEnumerado opcionesEnumerado;

    private int indice;

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getNombreCompletoCampo() {
        if (Conversion.isBlank(tabla)) {
            return nombre;
        } else {
            return tabla + "." + nombre;
        }
    }

    public String getIdCampo() {
        return idCampo != null ? idCampo : nombre;
    }

    public void setIdCampo(String idCampo) {
        this.idCampo = idCampo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public boolean perteneceATabla(String tabla) {
        return tabla.compareToIgnoreCase(this.tabla) == 0;
    }

    @Deprecated
    public String getCampo() {
        return campo;
    }
    
    @Deprecated
    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public TipoDato getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDato tipoDato) {
        this.tipoDato = tipoDato;
    }

    public TipoRolGes getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(TipoRolGes tipoRol) {
        this.tipoRol = tipoRol;
    }

    public int getAlineacion() {
        return alineacion;
    }

    public void setAlineacion(int alineacion) {
        this.alineacion = alineacion;
    }

    public String getAlign() {
        switch (alineacion) {
            case 0:
                return "left";
            case 1:
                return "right";
            case 2:
                return "center";
            default:
                return "";
        }
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public int getDecimales() {
        return decimales;
    }

    public void setDecimales(int decimales) {
        this.decimales = decimales;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getEstilo() {
        return estilo;
    }

    public void setEstilo(int estilo) {
        this.estilo = estilo;
    }

    public void addEstilo(int mascaraEstilo) {
        estilo |= mascaraEstilo;
    }

    public boolean isClave() {
        return tieneEstilo(CAMPO_CLAVE);
    }

    /**
     * Indica que es una secuencia (máximo mas uno).
     * De momento todos los campos clave de tipo entero son secuencias.
     */
    public boolean isSecuencia() {
        return isClave() && tipoDato == TipoDato.ENTERO;
    }

    public boolean isRequerido() {
        return tieneEstilo(CAMPO_REQUERIDO);
    }

    public boolean isOculto() {
        return tieneEstilo(CAMPO_OCULTO) || tieneEstilo(CAMPO_SOLO_FILTRO);
    }

    public boolean isSiempreMayusculas() {
        return tieneEstilo(CAMPO_SIEMPRE_MAYUSCULAS);
    }

    public boolean isSoloLectura() {
        return tieneEstilo(CAMPO_SOLO_LECTURA);
    }

    public boolean tieneEstilo(int mascara) {
        return (estilo & mascara) != 0;
    }

    public String getExpresionGT() {
        return expresionGT;
    }

    public void setExpresionGT(String expresionGT) {
        this.expresionGT = expresionGT;
    }

    public String getFormatoGT() {
        return formatoGT;
    }

    public void setFormatoGT(String formatoGT) {
        this.formatoGT = formatoGT;
    }

    public String getConsultaSeleccion() {
        return consultaSeleccion;
    }

    public void setConsultaSeleccion(String consultaSeleccion) {
        this.consultaSeleccion = consultaSeleccion;
    }

    public String getIdCampoRelacion() {
        return idCampoRelacion;
    }

    public void setIdCampoRelacion(String idCampoRelacion) {
        this.idCampoRelacion = idCampoRelacion;
    }

    public String getIdCampoSeleccion() {
        return idCampoSeleccion;
    }

    public void setIdCampoSeleccion(String idCampoSeleccion) {
        this.idCampoSeleccion = idCampoSeleccion;
    }

    /*
     * Los siguientes metodos se mantienen para mantener la compatibilidad con el .ges
     */
    @Deprecated
    public String getNombreCampoRelacion() {
        return idCampoRelacion;
    }

    @Deprecated
    public void setNombreCampoRelacion(String nombreCampoRelacion) {
        this.idCampoRelacion = nombreCampoRelacion;
    }

    @Deprecated
    public String getNombreCampoSeleccion() {
        return idCampoSeleccion;
    }

    @Deprecated
    public void setNombreCampoSeleccion(String nombreCampoSeleccion) {
        this.idCampoSeleccion = nombreCampoSeleccion;
    }

    public String getValorNulo() {
        return valorNulo;
    }

    public void setValorNulo(String valorNulo) {
        this.valorNulo = valorNulo;
    }

    public String getValorPorDefecto() {
        return valorPorDefecto;
    }

    public void setValorPorDefecto(String valorPorDefecto) {
        this.valorPorDefecto = valorPorDefecto;
    }

    @Override
    public String toString() {
        return getIdCampo();
    }
    
    public String getNombreCompleto() {
        return Conversion.isBlank(tabla) ? nombre : tabla + "." + nombre;
    }

    public String getTipoDatoJava() {
        if (tipoDato == null) {
            return null;
        }
        switch (tipoDato) {
            case CADENA:
                return "java.lang.String";
            case ENTERO:
                return "java.lang.Integer";
            case REAL:
                return "java.lang.Double";
            case BOOLEANO:
                return "java.lang.Boolean";
            case FECHA:
                return "es.jbp.comun.utiles.tiempo.Fecha";
            case FECHA_HORA:
                return "es.jbp.comun.utiles.tiempo.FechaHora";
            case BYTES:
                return "byte[]";
            default:
                return null;
        }
    }
   
    public boolean esFormatoEnumerado() {
         return formato != null && formato.startsWith("#");
    }

    public OpcionesEnumerado getOpcionesEnumerado() {
        if (formato == null) {
            return null;
        }        
        boolean esEnumerado = esFormatoEnumerado();
        if (!esEnumerado && tipoDato != TipoDato.BOOLEANO) {
            return null;
        }
        
        if (opcionesEnumerado != null) {
            return opcionesEnumerado;
        }
                
        opcionesEnumerado = new OpcionesEnumerado();
        
        if (puedeSerNulo()) {
            opcionesEnumerado.agregar(valorNulo, null);
        }

        if (esEnumerado) {
            String cadenaOpciones;
            cadenaOpciones = formato.substring(1);
            String[] arrayOpciones = cadenaOpciones.split("#");
            if (tipoDato == TipoDato.BOOLEANO) {
                opcionesEnumerado.agregarEnumeradosBooleanos(Arrays.asList(arrayOpciones));
            } else {
                opcionesEnumerado.agregarEnumeradosEnteros(Arrays.asList(arrayOpciones));
            }
        } else if (tipoDato == TipoDato.BOOLEANO) {
            String[] split = formato.split("/");
            if (split.length > 1) {
                opcionesEnumerado.agregar(split[1], Boolean.TRUE);
            }
            if (split.length > 0) {
                opcionesEnumerado.agregar(split[0], Boolean.FALSE);
            }
        }
        return opcionesEnumerado;
    }
    
    public List<String> getOpciones() {
        if (!tieneOpciones()) {
            return null;
        }
        return opcionesEnumerado.getListaTextos();
    }
    
    public boolean tieneOpciones() {
        return getOpcionesEnumerado() != null;
    }

    public boolean puedeSerNulo() {
        return !tieneEstilo(CAMPO_NO_NULO);
    }
    
    public boolean esOpcion(String textoOpcion) {
        OpcionesEnumerado opciones = getOpcionesEnumerado();
        return opciones != null ? opciones.contieneTexto(textoOpcion) : false;
    }

    public Object parsearOpcion(String texto) {        
        OpcionesEnumerado opciones = getOpcionesEnumerado();
        return opciones != null ? opciones.getValor(texto) : null;
    }    

    public String formatearOpcion(Object valor) {
        OpcionesEnumerado opciones = getOpcionesEnumerado();
        return opciones != null ? opciones.getTexto(valor) : null;        
    }

    public String formatearValor(Object valor, boolean paraEdicion) {

        if (valor == null) {
            return "";
        }

        switch (tipoDato) {
            case CADENA:
                return Conversion.toString(valor);
            case ENTERO:
                if (tieneOpciones()) {
                    return formatearOpcion(Conversion.toLong(valor));
                } else if (Conversion.isBlank(formato)) {
                    return Conversion.toString(valor);
                } else {
                    return String.format(paraEdicion ? "%d" : formato, valor);
                }
            case REAL:
                if (Conversion.isBlank(formato)) {
                    String str = Conversion.formatearBigDecimal(valor, decimales);
                    return paraEdicion ? str : agregarUnidad(str);
                } else {
                    Conversion.formatearReal(paraEdicion ? "%f" : formato, Conversion.toDouble(valor));
                }
            case BOOLEANO:
                Boolean b = Conversion.toBoolean(valor);
                if (tieneOpciones()) {
                    return formatearOpcion(b == null ? null : (b ? 1 : 0));
                }
                return Conversion.toString(b);
            case FECHA:
                return Conversion.toString(Conversion.toFecha(valor));
            case FECHA_HORA:
                return Conversion.toString(Conversion.toFechaHora(valor));
            case BYTES:
                return Base64.getEncoder().encodeToString(Conversion.toByteArray(valor));
            default:
                return "";
        }
    }

    public boolean esTotalizable() {
        return tipoRol == TipoRolGes.FTOTAL || tipoRol == TipoRolGes.ITOTAL;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String agregarUnidad(String str) {
        if (str == null) {
            return null;
        }
        if (Conversion.isBlank(unidad)) {
            return str;
        }

        return str + " " + unidad;
    }

}
