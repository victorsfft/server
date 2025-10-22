package com.iesfernandoaguilar.solsonafuentes.service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iesfernandoaguilar.solsonafuentes.model.Estadistica;
import com.iesfernandoaguilar.solsonafuentes.model.Historial;
import com.iesfernandoaguilar.solsonafuentes.model.JornadaLaboral;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class InformeService {

    @Autowired
    private JornadaLaboralService jornadaLaboralService;

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private HistorialService historialService;

    /**
     * Genera un informe PDF de jornada laboral
     */
    public String generarInformeJornadaLaboral(
            Long idGrupo,
            Long idUsuario,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String rutaDestino) {

        try {
            String nombreArchivo = rutaDestino + "/informe_jornada_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Informe de Jornada Laboral")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha del informe
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Período
            document.add(new Paragraph("Período: " +
                    fechaDesde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                    fechaHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Obtener jornadas
            List<JornadaLaboral> jornadas;
            if (idUsuario != null) {
                jornadas = jornadaLaboralService.obtenerJornadasUsuario(idUsuario, fechaDesde, fechaHasta);
            } else {
                jornadas = jornadaLaboralService.obtenerJornadasGrupo(idGrupo, fechaDesde, fechaHasta);
            }

            // Tabla de jornadas
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 2}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            tabla.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Entrada")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Salida")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Horas")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Estado")).setBold());

            // Datos
            for (JornadaLaboral jornada : jornadas) {
                tabla.addCell(jornada.getUsuario().getNombre());
                tabla.addCell(jornada.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                tabla.addCell(jornada.getHoraEntrada() != null ? jornada.getHoraEntrada().toString() : "--");
                tabla.addCell(jornada.getHoraSalida() != null ? jornada.getHoraSalida().toString() : "--");
                tabla.addCell(String.format("%.2f", jornada.getHorasTrabajadas()));
                tabla.addCell(jornada.getEstado().toString());
            }

            document.add(tabla);

            // Cerrar documento
            document.close();

            return nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar informe de jornada laboral: " + e.getMessage());
        }
    }

    /**
     * Genera un informe PDF de estadísticas de tareas
     */
    public String generarInformeEstadisticas(
            Long idGrupo,
            Long idUsuario,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String rutaDestino) {

        try {
            String nombreArchivo = rutaDestino + "/informe_estadisticas_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Informe de Estadísticas de Tareas")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha del informe
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Período
            document.add(new Paragraph("Período: " +
                    fechaDesde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                    fechaHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Obtener estadísticas
            List<Estadistica> estadisticas;
            if (idUsuario != null) {
                estadisticas = estadisticaService.obtenerEstadisticasUsuarioPorRango(idUsuario, fechaDesde, fechaHasta);
            } else {
                estadisticas = estadisticaService.obtenerUltimasEstadisticasUsuariosDelGrupo(idGrupo);
            }

            // Tabla de estadísticas
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 2}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            tabla.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Completadas")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Pendientes")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Retrasadas")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Horas")).setBold());
            tabla.addHeaderCell(new Cell().add(new Paragraph("Cumplimiento %")).setBold());

            // Datos
            for (Estadistica estadistica : estadisticas) {
                if (estadistica.getUsuario() != null) {
                    tabla.addCell(estadistica.getUsuario().getNombre());
                } else {
                    tabla.addCell("GENERAL");
                }
                tabla.addCell(String.valueOf(estadistica.getTareasCompletadasTotales()));
                tabla.addCell(String.valueOf(estadistica.getTareasPendientesTotales()));
                tabla.addCell(String.valueOf(estadistica.getTareasRetrasadasTotales()));
                tabla.addCell(String.format("%.2f", estadistica.getHorasTotales()));
                tabla.addCell(String.format("%.2f%%", estadistica.getCumplimientoJornadaTotales()));
            }

            document.add(tabla);

            // Cerrar documento
            document.close();

            return nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar informe de estadísticas: " + e.getMessage());
        }
    }

    /**
     * Genera un informe PDF de resumen general (combina jornada y estadísticas)
     */
    public String generarInformeResumen(
            Long idGrupo,
            Long idUsuario,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String rutaDestino,
            String observaciones) {

        try {
            String nombreArchivo = rutaDestino + "/informe_resumen_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Informe de Resumen General")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha del informe
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT));

            // Período
            document.add(new Paragraph("Período: " +
                    fechaDesde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                    fechaHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Sección 1: Estadísticas
            document.add(new Paragraph("Estadísticas de Tareas")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20));

            List<Estadistica> estadisticas;
            if (idUsuario != null) {
                estadisticas = estadisticaService.obtenerEstadisticasUsuarioPorRango(idUsuario, fechaDesde, fechaHasta);
            } else {
                estadisticas = estadisticaService.obtenerUltimasEstadisticasUsuariosDelGrupo(idGrupo);
            }

            Table tablaEstadisticas = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}));
            tablaEstadisticas.setWidth(UnitValue.createPercentValue(100));
            tablaEstadisticas.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBold());
            tablaEstadisticas.addHeaderCell(new Cell().add(new Paragraph("Completadas")).setBold());
            tablaEstadisticas.addHeaderCell(new Cell().add(new Paragraph("Pendientes")).setBold());
            tablaEstadisticas.addHeaderCell(new Cell().add(new Paragraph("Retrasadas")).setBold());

            for (Estadistica estadistica : estadisticas) {
                if (estadistica.getUsuario() != null) {
                    tablaEstadisticas.addCell(estadistica.getUsuario().getNombre());
                } else {
                    tablaEstadisticas.addCell("GENERAL");
                }
                tablaEstadisticas.addCell(String.valueOf(estadistica.getTareasCompletadasTotales()));
                tablaEstadisticas.addCell(String.valueOf(estadistica.getTareasPendientesTotales()));
                tablaEstadisticas.addCell(String.valueOf(estadistica.getTareasRetrasadasTotales()));
            }

            document.add(tablaEstadisticas);

            // Sección 2: Jornadas Laborales
            document.add(new Paragraph("Jornadas Laborales")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20));

            List<JornadaLaboral> jornadas;
            if (idUsuario != null) {
                jornadas = jornadaLaboralService.obtenerJornadasUsuario(idUsuario, fechaDesde, fechaHasta);
            } else {
                jornadas = jornadaLaboralService.obtenerJornadasGrupo(idGrupo, fechaDesde, fechaHasta);
            }

            Table tablaJornadas = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2}));
            tablaJornadas.setWidth(UnitValue.createPercentValue(100));
            tablaJornadas.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBold());
            tablaJornadas.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());
            tablaJornadas.addHeaderCell(new Cell().add(new Paragraph("Entrada")).setBold());
            tablaJornadas.addHeaderCell(new Cell().add(new Paragraph("Salida")).setBold());
            tablaJornadas.addHeaderCell(new Cell().add(new Paragraph("Horas")).setBold());

            for (JornadaLaboral jornada : jornadas) {
                tablaJornadas.addCell(jornada.getUsuario().getNombre());
                tablaJornadas.addCell(jornada.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                tablaJornadas.addCell(jornada.getHoraEntrada() != null ? jornada.getHoraEntrada().toString() : "--");
                tablaJornadas.addCell(jornada.getHoraSalida() != null ? jornada.getHoraSalida().toString() : "--");
                tablaJornadas.addCell(String.format("%.2f", jornada.getHorasTrabajadas()));
            }

            document.add(tablaJornadas);

            // Observaciones
            if (observaciones != null && !observaciones.isEmpty()) {
                document.add(new Paragraph("Observaciones")
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(20));
                document.add(new Paragraph(observaciones));
            }

            // Cerrar documento
            document.close();

            return nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar informe de resumen: " + e.getMessage());
        }
    }
}
