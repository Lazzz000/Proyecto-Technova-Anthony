package com.demo.controlador;

import com.demo.modelo.*;
import com.demo.modelo.enums.TipoComprobante;
import com.demo.service.ComprobanteService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/comprobante")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;
    @GetMapping("/descargar/{idComprobante}")
    public void descargarComprobante(@PathVariable Integer idComprobante,
                                     HttpServletResponse response) {
        try {
            // Buscar comprobante por idComprobante
            Comprobante comprobante = comprobanteService.buscarPorId(idComprobante); 

            if (comprobante == null) {
                throw new RuntimeException("Comprobante no encontrado");
            }

            // Configurar respuesta y generar PDF
            response.setContentType("application/pdf");
            String nombreArchivo = (comprobante.getTipoComprobante() == TipoComprobante.FACTURA ? "Factura_" : "Boleta_")
                    + comprobante.getSerie() + "-" + comprobante.getNumero() + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

            OutputStream out = response.getOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);


            // Fuentes
            com.itextpdf.kernel.font.PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            com.itextpdf.kernel.font.PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // --- Logo ---
            try (InputStream logoStream = getClass().getResourceAsStream("/static/img/logo.jpg")) {
                if (logoStream != null) {
                    Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()));
                    // Escalar proporcional
                    logo.scaleToFit(180f, 120f);
                    logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    logo.setMarginBottom(10f);
                    document.add(logo);
                }
            }

            // --- Título ---
            Paragraph titulo = new Paragraph("✔ Compra exitosa!")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setFontColor(ColorConstants.PINK)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginTop(10f)
                    .setMarginBottom(20f);
            document.add(titulo);

            Pedido pedido = comprobante.getPedido();

            // --- Resumen del pedido ---
            Paragraph resumenTitulo = new Paragraph("Resumen del pedido")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setMarginBottom(10f);
            document.add(resumenTitulo);

            Table resumen = new Table(new float[]{1, 2});
            resumen.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(HorizontalAlignment.LEFT);

            resumen.addCell(new Cell().add(new Paragraph("Pedido ID:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            resumen.addCell(new Cell().add(new Paragraph(String.valueOf(pedido.getIdPedido())).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            resumen.addCell(new Cell().add(new Paragraph("Fecha:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            resumen.addCell(new Cell().add(new Paragraph(comprobante.getFechaEmision()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).setFont(fontNormal)).setBorder(Border.NO_BORDER));
           
            resumen.addCell(new Cell().add(new Paragraph("Estado:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            resumen.addCell(new Cell().add(new Paragraph(pedido.getEstado().toString()).setFont(fontNormal)).setBorder(Border.NO_BORDER));
            

resumen.addCell(new Cell().add(new Paragraph("Método de pago:").setFont(fontBold)).setBorder(Border.NO_BORDER));
resumen.addCell(new Cell().add(new Paragraph(pedido.getMetodoPago().toString()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            resumen.addCell(new Cell().add(new Paragraph("Total:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            resumen.addCell(new Cell().add(new Paragraph("S/ " + comprobante.getTotal()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            document.add(resumen);
            document.add(new Paragraph(" "));

            // --- Detalles del pedido ---
            Paragraph detallesTitulo = new Paragraph("Detalles del pedido")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setMarginBottom(5f);
            document.add(detallesTitulo);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2})).useAllAvailableWidth();
            // Encabezados
            tabla.addHeaderCell(new Cell().add(new Paragraph("Producto").setFont(fontBold)).setBackgroundColor(ColorConstants.PINK));
            tabla.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setFont(fontBold)).setBackgroundColor(ColorConstants.PINK));
            tabla.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario").setFont(fontBold)).setBackgroundColor(ColorConstants.PINK));
            tabla.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setFont(fontBold)).setBackgroundColor(ColorConstants.PINK));

            // Items con filas alternadas
            boolean alternate = false;
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                Color bg = alternate ? ColorConstants.LIGHT_GRAY : ColorConstants.WHITE;
                Productos prod = detalle.getProducto();
                tabla.addCell(new Cell().add(new Paragraph(prod.getNombre()).setFont(fontNormal)).setBackgroundColor(bg));
                tabla.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad())).setFont(fontNormal)).setBackgroundColor(bg));
                tabla.addCell(new Cell().add(new Paragraph("S/ " + detalle.getPrecioUnitario()).setFont(fontNormal)).setBackgroundColor(bg));
                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                tabla.addCell(new Cell().add(new Paragraph("S/ " + subtotal).setFont(fontNormal)).setBackgroundColor(bg));
                alternate = !alternate;
            }
            document.add(tabla);
            document.add(new Paragraph(" "));

            // --- Datos de facturación ---
            Paragraph factTitulo = new Paragraph("Datos de facturación")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setMarginBottom(5f);
            document.add(factTitulo);

            DatosFacturacion datos = comprobante.getDatosFacturacion();
            Table datosTabla = new Table(new float[]{1, 2});
            datosTabla.setWidth(UnitValue.createPercentValue(70)).setHorizontalAlignment(HorizontalAlignment.LEFT);

            datosTabla.addCell(new Cell().add(new Paragraph("Tipo documento:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getTipoDocumento().toString()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            datosTabla.addCell(new Cell().add(new Paragraph("Número documento:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getNumeroDocumento()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            datosTabla.addCell(new Cell().add(new Paragraph("Nombre / Razón Social:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getNombreRazonSocial()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            datosTabla.addCell(new Cell().add(new Paragraph("Dirección:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getDireccion()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            datosTabla.addCell(new Cell().add(new Paragraph("Teléfono:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getTelefono()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            datosTabla.addCell(new Cell().add(new Paragraph("Correo:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            datosTabla.addCell(new Cell().add(new Paragraph(datos.getCorreo()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            document.add(datosTabla);
            document.add(new Paragraph(" "));

            // --- Comprobante ---
            Paragraph compTitulo = new Paragraph("Comprobante")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setMarginBottom(5f);
            document.add(compTitulo);

            Table compTabla = new Table(new float[]{1, 2});
            compTabla.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(HorizontalAlignment.LEFT);

            compTabla.addCell(new Cell().add(new Paragraph("Tipo:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            compTabla.addCell(new Cell().add(new Paragraph(comprobante.getTipoComprobante().toString()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            compTabla.addCell(new Cell().add(new Paragraph("Serie:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            compTabla.addCell(new Cell().add(new Paragraph(comprobante.getSerie()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            compTabla.addCell(new Cell().add(new Paragraph("Número:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            compTabla.addCell(new Cell().add(new Paragraph(String.valueOf(comprobante.getNumero())).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            compTabla.addCell(new Cell().add(new Paragraph("Total:").setFont(fontBold)).setBorder(Border.NO_BORDER));
            compTabla.addCell(new Cell().add(new Paragraph("S/ " + comprobante.getTotal()).setFont(fontNormal)).setBorder(Border.NO_BORDER));

            document.add(compTabla);
            document.add(new Paragraph(" "));

            // --- Pie de página ---
            document.add(new Paragraph("💻 TechNova - La mejor experiencia de tecnología, con promociones y preventas exclusivas para nuestros clientes.")
                    .setFont(fontNormal)
                    .setItalic()
                    .setFontSize(10)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.close();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

