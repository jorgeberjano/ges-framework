package es.jbp.ges.exportacion;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import es.jbp.ges.entidad.EntidadGes;
import es.jbp.ges.entidad.CampoGes;
import es.jbp.ges.entidad.ConsultaGes;
import es.jbp.ges.utilidades.ConversionValores;
import es.jbp.comun.utiles.conversion.Conversion;
import reactor.core.publisher.Flux;

import java.io.OutputStream;

/**
 * Genera informes PDF a partir de los datos de sesión de una tabla.
 *
 * @author jberjano
 */
public class ExportadorPdf implements Exportador {

    private static final Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA, 26, Font.BOLDITALIC);
    private static final Font fuenteCabecera = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC);
    private static final Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

    private String titulo;

    class ListenerEvento extends PdfPageEventHelper {

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte contentByte = writer.getDirectContent();
            Phrase cabecera = new Phrase(titulo, fuenteCabecera);
            Phrase pie = new Phrase(String.format("Página %d", writer.getPageNumber()), fuenteCabecera);
            ColumnText.showTextAligned(contentByte, Element.ALIGN_RIGHT,
                    cabecera,
                    document.right(),
                    document.top() + 10, 0);
            ColumnText.showTextAligned(contentByte, Element.ALIGN_RIGHT,
                    pie,
                    document.right(),
                    document.bottom() - 10, 0);
        }
    }

    /**
     * Crea un informe a partir de los datos de sesión de una tabla
     */
    public ExportadorPdf() {
    }

    /**
     * Genera el informe en pdf
     */
    @Override
    public void generar(OutputStream outputStream, ConsultaGes consulta, Flux<EntidadGes> listaEntidades) throws Exception {

        Rectangle rect = PageSize.A4;
        if (consulta.tieneEstiloImpresion(ConsultaGes.CONSIMPR_ORIENTACION_HORIZONTAL)) {
            rect = rect.rotate();
        }
        Document document = new Document(rect);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setPageEvent(new ListenerEvento());
        document.open();

        titulo = consulta.getTituloImpresion();
        if (Conversion.isBlank(titulo)) {
            titulo = consulta.getNombreEnPlural();
        }

        String subtitulo = consulta.getSubtituloImpresion();

        // Metadatos
        document.addTitle(titulo);

//            document.addSubject("...");
//            document.addKeywords("...");
//            document.addAuthor("...");
//            document.addCreator("...");

        Chunk chunkTitulo = new Chunk(titulo + "\n", fuenteTitulo);
        Paragraph parrafoTitulo = new Paragraph(chunkTitulo);
        parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(parrafoTitulo);

        Chunk chunkSubtitulo = new Chunk(subtitulo + "\n", fuenteNormal);
        Paragraph parrafoSubtitulo = new Paragraph(chunkSubtitulo);
        parrafoSubtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(parrafoSubtitulo);

        PdfPTable tabla = generarTabla(consulta, listaEntidades);
        document.add(tabla);

        document.close();
    }

    private PdfPTable generarTabla(ConsultaGes consulta, Flux<EntidadGes> listaEntidades) throws DocumentException {

        int numeroColumnas = (int) consulta.getCampos().stream().filter(campo -> !campo.isOculto()).count();
        PdfPTable table = new PdfPTable(numeroColumnas);
        table.setHeaderRows(1);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100);

        int[] anchosRelativos = new int[numeroColumnas];
        int i = 0;
        PdfPCell columnHeader;
        for (CampoGes campo : consulta.getCampos()) {
            if (campo.isOculto()) {
                continue;
            }
            columnHeader = new PdfPCell(new Phrase(campo.getTitulo(), fuenteCabecera));
            columnHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            columnHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(columnHeader);
            anchosRelativos[i++] = campo.getLongitud();
        }
        table.setWidths(anchosRelativos);

        Acumulador acumulador = new Acumulador();
        for (EntidadGes entidad : listaEntidades.toIterable()) {
            for (CampoGes campo : consulta.getCampos()) {
                if (campo.isOculto()) {
                    continue;
                }

                if (campo.esTotalizable()) {
                    acumulador.acumularValor(campo.getIdCampo(), entidad.getValor(campo.getIdCampo()));
                }
                Object valor = entidad.getValor(campo.getIdCampo());
                String texto = ConversionValores.aValorUI(valor, campo);
                
                PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteNormal));
                celda.setHorizontalAlignment(getAlineacionPdf(campo.getAlineacion()));
                table.addCell(celda);
            }
        }

        if (acumulador.hayAcumulados()) {
            for (CampoGes campo : consulta.getCampos()) {
                if (campo.isOculto()) {
                    continue;
                }
                PdfPCell celda;
                if (campo.esTotalizable()) {
                    Double total = acumulador.getAcumulado(campo.getIdCampo());
                    Object valor = Conversion.convertirValor(total, campo.getTipoDato());
                    String texto = campo.formatearValor(valor, false);
                    celda = new PdfPCell(new Phrase(texto, fuenteNormal));
                } else {
                    celda = new PdfPCell(new Phrase("", fuenteNormal));
                }
                celda.setHorizontalAlignment(getAlineacionPdf(campo.getAlineacion()));
                table.addCell(celda);
            }
        }

        return table;
    }

    private int getAlineacionPdf(int alineacionCampo) {
        int alineacion = Element.ALIGN_LEFT;
        switch (alineacionCampo) {
            case CampoGes.ALINEACION_CENTRO:
                alineacion = Element.ALIGN_CENTER;
                break;
            case CampoGes.ALINEACION_DERECHA:
                alineacion = Element.ALIGN_RIGHT;
                break;
        }
        return alineacion;
    }
}
