package es.jbp.ges.serializacion;

import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.entidad.Ges;
import java.util.List;

import org.w3c.dom.Element;
import es.jbp.comun.utiles.xml.AsistenteXml;
import es.jbp.comun.utiles.reflexion.Reflexion;
import java.util.Map;

/**
 * Serializador de la estructura de datos GES en XML
 * @author Jorge
 */
public class SerializadorGesXml implements SerializadorGes {

    private boolean bSerializar;
    private AsistenteXml xml = new AsistenteXml();
    private Ges gestor;
    private Map<String, Object> mapaSimbolos;
    private String mensajeError;

    public SerializadorGesXml(Map<String, Object> mapaSimbolos) {
        this.mapaSimbolos = mapaSimbolos;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    @Override
    public void serializar(String nombreArchivo, Ges gestor) throws Exception {
        this.gestor = gestor;
        bSerializar = true;
        xml.crearNuevo("raiz");
        procesar(gestor);
        xml.guardarEnArchivo(nombreArchivo);
    }
    
//    @Deprecated
//    public Ges deserializar(String nombreArchivo) {
//        gestor =  new Ges();
//        deserializar(nombreArchivo, gestor);
//        return gestor;
//    }
    
    public Ges deserializarArchivo(String nombreArchivo) throws Exception {
        gestor = new Ges();
        gestor.definirSimbolos(mapaSimbolos);
        bSerializar = false;
        xml.recuperarDeArchivo(nombreArchivo);
        procesar(gestor);
        return gestor;        
    }
    
    @Override
    public Ges deserializarRecurso(String nombreRecurso) throws Exception {
        gestor = new Ges();
        gestor.definirSimbolos(mapaSimbolos);
        bSerializar = false;
        deserializarRecurso(nombreRecurso, gestor);
        return gestor;
    }
    
    private void deserializarRecurso(String nombreRecurso, Ges gestor) throws Exception {
        this.gestor = gestor;
        bSerializar = false;
        xml.recuperarDeRecurso(nombreRecurso);        
        procesar(gestor);        
    }

    private void procesar(Ges gestor) {
        Element raiz = xml.getElementoRaiz();
        procesarAtributo(raiz, "version", gestor);
        procesarAtributo(raiz, "nombreBaseDatos", gestor);
        procesarAtributo(raiz, "estilo", gestor);
        procesarConsultas(raiz, "consultaPantalla", gestor.getConsultasPantalla());
        procesarConsultas(raiz, "consultaImpresora", gestor.getConsultasImpresora());
    }

    private void procesarAtributo(Element elementoPadre, String nombreTag, Object objeto) {
        if (bSerializar) {
            Object obj = Reflexion.obtenerValorAtributoSimple(objeto, nombreTag);
            String texto = obj.toString().replace("\r\n", "\n");
            xml.escribirElementoTexto(elementoPadre, nombreTag, texto);
        } else {
            String texto = xml.leerElementoTexto(elementoPadre, nombreTag);
            texto = gestor.getGestorSimbolos().sustituirSoloSimbolos(texto);
            //System.out.println(nombreTag + "=" + texto);
            Reflexion.asignarValorAtributoSimple(objeto, nombreTag, texto);
        }
    }

    private void procesarConsultas(Element elementoPadre, String nombreTag, List<ConsultaGes> listaConsultas) {

        if (bSerializar) {
            for (ConsultaGes consulta : listaConsultas) {
                Element elemento = xml.escribirElemento(elementoPadre, nombreTag);
                procesarConsulta(elemento, consulta);
            }
        } else {
            List<Element> elementos = xml.leerElementos(elementoPadre, nombreTag);
            for (Element elemento : elementos) {
                ConsultaGes consulta = new ConsultaGes();
                procesarConsulta(elemento, consulta);
                listaConsultas.add(consulta);
            }
        }        
    }

    private void procesarConsulta(Element elementoPadre, ConsultaGes consulta) {
        procesarAtributo(elementoPadre, "idConsulta", consulta);
        procesarAtributo(elementoPadre, "nombreEnSingular", consulta);
        procesarAtributo(elementoPadre, "nombreEnPlural", consulta);
        procesarAtributo(elementoPadre, "tabla", consulta);
        procesarAtributo(elementoPadre, "sql", consulta);
        procesarAtributo(elementoPadre, "nombreSubconsultas", consulta);
        procesarAtributo(elementoPadre, "camposFiltroPrevio", consulta);
        procesarAtributo(elementoPadre, "imagen", consulta);
        procesarAtributo(elementoPadre, "estilo", consulta);
        procesarAtributo(elementoPadre, "camposPorDefecto", consulta);
        procesarAtributo(elementoPadre, "valoresPorDefecto", consulta);
        procesarAtributo(elementoPadre, "estiloImpresion", consulta);
        procesarAtributo(elementoPadre, "tituloImpresion", consulta);
        procesarAtributo(elementoPadre, "subtituloImpresion", consulta);
        procesarAtributo(elementoPadre, "encabezadoImpresion", consulta);
        procesarAtributo(elementoPadre, "pieImpresion", consulta);
        procesarAtributo(elementoPadre, "textoInicialImpresion", consulta);
        procesarAtributo(elementoPadre, "textoFinalImpresion", consulta);
        procesarAtributo(elementoPadre, "consultasSeleccionPrevia", consulta);

        procesarCampos(elementoPadre, "campo", consulta.getCampos(), consulta);

//        consulta.CrearListaRelaciones();
//        consulta.CrearListaAgregados();
    }

    private void procesarCampos(Element elementoPadre, String nombreTag, List<CampoGes> listaCampos, ConsultaGes consulta) {
        if (bSerializar) {

            for (CampoGes campo : listaCampos) {
                Element elemento = xml.escribirElemento(elementoPadre, nombreTag);
                procesarCampo(elemento, campo);
            }
        } else {
            List<Element> elementos = xml.leerElementos(elementoPadre, nombreTag);
            int indice = 0;
            for (Element elemento : elementos) {
                CampoGes campo = new CampoGes();
                procesarCampo(elemento, campo);
                campo.setIndice(indice++);
                ajustarCampoTrasDeserializar(campo, consulta);                
                listaCampos.add(campo);
            }
        }
    }
    
    private void ajustarCampoTrasDeserializar(CampoGes campo, ConsultaGes consulta) {
        if (!consulta.getTabla().equals(campo.getTabla())) {
            campo.setEstilo(CampoGes.CAMPO_SOLO_LECTURA);
        }
    }

    private void procesarCampo(Element elementoPadre, CampoGes campo) {
        procesarAtributo(elementoPadre, "idCampo", campo);
        procesarAtributo(elementoPadre, "nombre", campo);
        procesarAtributo(elementoPadre, "tabla", campo);
        procesarAtributo(elementoPadre, "campo", campo);
        procesarAtributo(elementoPadre, "titulo", campo);
        procesarAtributo(elementoPadre, "tipoDato", campo);
        procesarAtributo(elementoPadre, "tipoRol", campo);
        procesarAtributo(elementoPadre, "alineacion", campo);
        procesarAtributo(elementoPadre, "longitud", campo);
        procesarAtributo(elementoPadre, "formato", campo);
        procesarAtributo(elementoPadre, "tamano", campo);
        procesarAtributo(elementoPadre, "decimales", campo);
        procesarAtributo(elementoPadre, "unidad", campo);
        procesarAtributo(elementoPadre, "estilo", campo);
        procesarAtributo(elementoPadre, "expresionGT", campo);
        procesarAtributo(elementoPadre, "formatoGT", campo);
        procesarAtributo(elementoPadre, "consultaSeleccion", campo);
        procesarAtributo(elementoPadre, "valorNulo", campo);
        procesarAtributo(elementoPadre, "nombreCampoRelacion", campo);
        procesarAtributo(elementoPadre, "nombreCampoSeleccion", campo);
        procesarAtributo(elementoPadre, "valorPorDefecto", campo);
        procesarAtributo(elementoPadre, "tipoDato", campo);
    }
}
