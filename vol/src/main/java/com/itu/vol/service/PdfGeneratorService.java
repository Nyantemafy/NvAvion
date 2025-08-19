package com.itu.vol.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itu.vol.dto.ReservationDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Génère un PDF pour une réservation
     */
    public byte[] generateReservationPdf(ReservationDTO reservation) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Créer le document PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Titre
            Paragraph title = new Paragraph("DÉTAILS DE RÉSERVATION")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30);
            document.add(title);

            // Informations de la réservation
            addReservationInfo(document, reservation);

            // Informations du vol
            addVolInfo(document, reservation);

            // Informations des sièges
            addSiegeInfo(document, reservation);

            // Prix
            addPrixInfo(document, reservation);

            // Pied de page
            addFooter(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Ajoute les informations de base de la réservation
     */
    private void addReservationInfo(Document document, ReservationDTO reservation) {
        Paragraph sectionTitle = new Paragraph("Informations de la réservation")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(2).useAllAvailableWidth();

        table.addCell(createCell("Numéro de réservation:", true));
        table.addCell(createCell("RES-" + String.format("%06d", reservation.getIdReservation()), false));

        table.addCell(createCell("Date de réservation:", true));
        String dateRes = reservation.getDateReservation() != null
                ? reservation.getDateReservation().format(DATE_FORMATTER)
                : "Non définie";
        table.addCell(createCell(dateRes, false));

        table.addCell(createCell("Passager:", true));
        table.addCell(createCell(reservation.getUsernameUser() != null ? reservation.getUsernameUser() : "Non défini",
                false));

        document.add(table);
    }

    /**
     * Ajoute les informations du vol
     */
    private void addVolInfo(Document document, ReservationDTO reservation) {
        Paragraph sectionTitle = new Paragraph("Informations du vol")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(2).useAllAvailableWidth();

        table.addCell(createCell("Numéro de vol:", true));
        table.addCell(
                createCell(reservation.getNumeroVol() != null ? reservation.getNumeroVol() : "Non défini", false));

        table.addCell(createCell("Destination:", true));
        table.addCell(createCell(
                reservation.getVilleDestination() != null ? reservation.getVilleDestination() : "Non définie", false));

        table.addCell(createCell("Date de vol:", true));
        String dateVol = reservation.getDateVol() != null ? reservation.getDateVol().format(DATE_FORMATTER)
                : "Non définie";
        table.addCell(createCell(dateVol, false));

        table.addCell(createCell("Avion:", true));
        table.addCell(
                createCell(reservation.getPseudoAvion() != null ? reservation.getPseudoAvion() : "Non défini", false));

        document.add(table);
    }

    /**
     * Ajoute les informations des sièges
     */
    private void addSiegeInfo(Document document, ReservationDTO reservation) {
        Paragraph sectionTitle = new Paragraph("Informations des sièges")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(3).useAllAvailableWidth();

        // En-têtes
        table.addCell(createCell("Type de siège", true));
        table.addCell(createCell("Nombre", true));
        table.addCell(createCell("Status", true));

        // Sièges Business
        table.addCell(createCell("Business", false));
        table.addCell(createCell(
                reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness().toString() : "0", false));
        table.addCell(createCell(reservation.hasBusinessSeats() ? "Réservé" : "Non réservé", false));

        // Sièges Économique
        table.addCell(createCell("Économique", false));
        table.addCell(
                createCell(reservation.getSiegeEco() != null ? reservation.getSiegeEco().toString() : "0", false));
        table.addCell(createCell(reservation.hasEcoSeats() ? "Réservé" : "Non réservé", false));

        // Total
        table.addCell(createCell("TOTAL", true));
        table.addCell(createCell(reservation.getTotalSieges().toString(), true));
        table.addCell(createCell("", false));

        document.add(table);
    }

    /**
     * Ajoute les informations de prix
     */
    private void addPrixInfo(Document document, ReservationDTO reservation) {
        Paragraph sectionTitle = new Paragraph("Informations tarifaires")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(2).useAllAvailableWidth();

        table.addCell(createCell("Prix total:", true));
        String prix = reservation.getPrixTotal() != null ? reservation.getPrixTotal() + " €" : "Non défini";
        table.addCell(createCell(prix, false).setFontSize(16).setBold());

        document.add(table);
    }

    /**
     * Ajoute le pied de page
     */
    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("Document généré automatiquement le " +
                java.time.LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);
    }

    /**
     * Crée une cellule de tableau avec les styles appropriés
     */
    private Cell createCell(String content, boolean isBold) {
        Paragraph paragraph = new Paragraph(content);
        if (isBold) {
            paragraph.setBold();
        }
        return new Cell().add(paragraph).setPadding(8);
    }
}