package com.itu.vol.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itu.vol.dto.ReservationDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import com.itextpdf.layout.Style;
import com.itextpdf.kernel.colors.ColorConstants;

@Service
public class PdfGeneratorService {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        /**
         * Génère un PDF pour une réservation
         */
        public byte[] generateReservationPdf(ReservationDTO reservation) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdfDoc = new PdfDocument(writer);
                        Document document = new Document(pdfDoc, PageSize.A4);

                        document.setMargins(10, 10, 10, 10);

                        // ====== STYLES ======
                        Color primaryColor = new DeviceRgb(0, 51, 102); // Bleu navy
                        Color secondaryColor = new DeviceRgb(0, 122, 204); // Bleu ciel
                        Color accentColor = new DeviceRgb(255, 183, 0); // Jaune or
                        Color lightGray = new DeviceRgb(240, 240, 240);

                        // Styles de texte
                        Style titleStyle = new Style()
                                        .setFontSize(20)
                                        .setBold()
                                        .setFontColor(primaryColor)
                                        .setTextAlignment(TextAlignment.CENTER);

                        Style headerStyle = new Style()
                                        .setFontSize(16)
                                        .setBold()
                                        .setFontColor(primaryColor);

                        Style labelStyle = new Style()
                                        .setFontSize(12)
                                        .setBold()
                                        .setFontColor(ColorConstants.BLACK);

                        Style valueStyle = new Style()
                                        .setFontSize(12)
                                        .setFontColor(ColorConstants.GRAY);

                        Style priceStyle = new Style()
                                        .setFontSize(14)
                                        .setBold()
                                        .setFontColor(secondaryColor);

                        Style footerStyle = new Style()
                                        .setFontSize(10)
                                        .setFontColor(ColorConstants.GRAY)
                                        .setTextAlignment(TextAlignment.CENTER);

                        // ====== EN-TÊTE AVEC LOGO AVIATION ======
                        addHeader(document, primaryColor, accentColor);

                        // ====== TITRE PRINCIPAL ======
                        Paragraph title = new Paragraph("CONFIRMATION DE RÉSERVATION")
                                        .addStyle(titleStyle)
                                        .setMarginBottom(20);
                        document.add(title);

                        // ====== INFORMATIONS RÉSERVATION ======
                        addReservationInfo(document, reservation, labelStyle, valueStyle);

                        // ====== INFORMATIONS VOL ======
                        addVolInfo(document, reservation, headerStyle, primaryColor, labelStyle, valueStyle);

                        // ====== INFORMATIONS SIÈGE ======
                        addSiegeInfo(document, reservation, headerStyle, primaryColor, labelStyle, valueStyle);

                        // ====== INFORMATIONS PRIX ======
                        addPrixInfo(document, reservation, headerStyle, primaryColor, labelStyle, valueStyle,
                                        priceStyle);

                        // ====== PIED DE PAGE ======
                        addFooter(document, footerStyle, primaryColor);

                        document.close();

                        // Sauvegarde du fichier
                        String fileName = String.format("Reservation_%06d_%s.pdf",
                                        reservation.getIdReservation(),
                                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

                        Path outputDir = Paths.get("pdfs");
                        Files.createDirectories(outputDir);
                        Path outputPath = outputDir.resolve(fileName);
                        Files.write(outputPath, baos.toByteArray());

                        System.out.println("📂 Fichier PDF sauvegardé dans : " + outputPath.toAbsolutePath());

                        return baos.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Erreur lors de la génération du PDF", e);
                }
        }

        private void addHeader(Document document, Color primaryColor, Color accentColor) {
                Table headerTable = new Table(2);
                headerTable.setWidth(UnitValue.createPercentValue(100));

                // Logo aviation (texte simulé)
                Paragraph logo = new Paragraph("✈ AERO EXPRESS")
                                .setFontSize(18)
                                .setBold()
                                .setFontColor(primaryColor);

                // Date d'émission
                Paragraph date = new Paragraph(
                                "Émis le: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .setFontSize(10)
                                .setFontColor(ColorConstants.GRAY)
                                .setTextAlignment(TextAlignment.RIGHT);

                headerTable.addCell(new Cell().add(logo).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
                headerTable.addCell(new Cell().add(date).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.RIGHT));

                document.add(headerTable);
                document.add(new Paragraph(" "));
        }

        private void addSectionTitle(Document document, String title, Style headerStyle, Color primaryColor) {
                Paragraph sectionTitle = new Paragraph(title)
                                .addStyle(headerStyle)
                                .setMarginTop(15)
                                .setMarginBottom(10)
                                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                                .setPadding(8)
                                .setBorder(new SolidBorder(primaryColor, 1));

                document.add(sectionTitle);
        }

        /**
         * Ajoute les informations de base de la réservation
         */
        private void addReservationInfo(Document document, ReservationDTO reservation, Style labelStyle,
                        Style valueStyle) {
                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));
                addTableRow(table, "N° Réservation:", "RES-" + reservation.getIdReservation(), labelStyle, valueStyle);
                addTableRow(table, "Date Réservation:",
                                reservation.getDateReservation()
                                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                labelStyle, valueStyle);
                addTableRow(table, "Passager:", reservation.getUsernameUser(),
                                labelStyle, valueStyle);

                document.add(table);
        }

        /**
         * Ajoute les informations du vol avec style aviation
         */
        private void addVolInfo(Document document, ReservationDTO reservation, Style headerStyle, Color primaryColor,
                        Style labelStyle, Style valueStyle) {
                addSectionTitle(document, "DÉTAILS DU VOL", headerStyle, primaryColor);

                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));

                addTableRow(table, "Numéro de vol:",
                                reservation.getNumeroVol() != null ? reservation.getNumeroVol() : "Non défini",
                                labelStyle, valueStyle);

                addTableRow(table, "Destination:",
                                reservation.getVilleDestination() != null ? reservation.getVilleDestination()
                                                : "Non définie",
                                labelStyle, valueStyle);

                addTableRow(table, "Date de vol:",
                                reservation.getDateVol() != null ? reservation.getDateVol().format(DATE_FORMATTER)
                                                : "Non définie",
                                labelStyle, valueStyle);

                addTableRow(table, "Avion:",
                                reservation.getPseudoAvion() != null ? reservation.getPseudoAvion() : "Non défini",
                                labelStyle, valueStyle);

                document.add(table);
        }

        /**
         * Ajoute les informations des sièges avec style aviation
         */
        private void addSiegeInfo(Document document, ReservationDTO reservation, Style headerStyle, Color primaryColor,
                        Style labelStyle, Style valueStyle) {
                addSectionTitle(document, "INFORMATIONS SIÈGE", headerStyle, primaryColor);

                Table table = new Table(3);
                table.setWidth(UnitValue.createPercentValue(100));

                // En-têtes avec style aviation
                table.addCell(createHeaderCell("Type de siège", primaryColor));
                table.addCell(createHeaderCell("Nombre", primaryColor));
                table.addCell(createHeaderCell("Status", primaryColor));

                // Sièges Business
                table.addCell(createCell("Business Class", false));
                table.addCell(createCell(
                                reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness().toString()
                                                : "0",
                                false));
                table.addCell(createCell(reservation.hasBusinessSeats() ? "Confirmé" : "Disponible", false));

                // Sièges Économique
                table.addCell(createCell("Économique", false));
                table.addCell(
                                createCell(reservation.getSiegeEco() != null ? reservation.getSiegeEco().toString()
                                                : "0", false));
                table.addCell(createCell(reservation.hasEcoSeats() ? "Confirmé" : "Disponible", false));

                // Total
                Cell totalLabelCell = createCell("TOTAL PASSAGERS", true);
                totalLabelCell.setBackgroundColor(new DeviceRgb(240, 240, 240));

                Cell totalValueCell = createCell(reservation.getTotalSieges().toString(), true);
                totalValueCell.setBackgroundColor(new DeviceRgb(240, 240, 240));

                table.addCell(totalLabelCell);
                table.addCell(totalValueCell);
                table.addCell(createCell("", false).setBackgroundColor(new DeviceRgb(240, 240, 240)));

                document.add(table);
        }

        /**
         * Ajoute les informations de prix avec style aviation
         */
        private void addPrixInfo(Document document, ReservationDTO reservation, Style headerStyle, Color primaryColor,
                        Style labelStyle, Style valueStyle, Style priceStyle) {
                addSectionTitle(document, "DÉTAILS TARIFAIRE", headerStyle, primaryColor);

                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));

                // Détails du tarif
                if (reservation.getSiegeBusiness() != null && reservation.getSiegeBusiness() > 0) {
                        addTableRow(table, "Business Class:",
                                        reservation.getPrixBusiness() != null ? reservation.getPrixBusiness() + " €"
                                                        : "0 €",
                                        labelStyle, valueStyle);
                }

                if (reservation.getSiegeEco() != null && reservation.getSiegeEco() > 0) {
                        addTableRow(table, "Économique:",
                                        reservation.getPrixEco() != null ? reservation.getPrixEco() + " €" : "0 €",
                                        labelStyle, valueStyle);
                }

                addTableRow(table, "Frais de service:", "30 €", labelStyle, valueStyle);

                // Ligne de séparation
                table.addCell(createCell("", false).setBorderBottom(new SolidBorder(new DeviceRgb(200, 200, 200), 1)));
                table.addCell(createCell("", false).setBorderBottom(new SolidBorder(new DeviceRgb(200, 200, 200), 1)));

                // Total avec style spécial
                Cell totalLabelCell = new Cell().add(new Paragraph("TOTAL:").addStyle(labelStyle))
                                .setPadding(8)
                                .setBackgroundColor(new DeviceRgb(240, 240, 240));

                Cell totalValueCell = new Cell().add(new Paragraph(
                                reservation.getPrixTotal() != null ? reservation.getPrixTotal() + " €" : "Non défini")
                                .addStyle(priceStyle))
                                .setPadding(8)
                                .setBackgroundColor(new DeviceRgb(240, 240, 240));

                table.addCell(totalLabelCell);
                table.addCell(totalValueCell);

                document.add(table);
        }

        /**
         * Ajoute le pied de page avec style aviation
         */
        private void addFooter(Document document, Style footerStyle, Color primaryColor) {
                Paragraph footerText = new Paragraph()
                                .add("✈️ VolConfi - Votre partenaire de confiance pour tous vos voyages aériens\n")
                                .add("📞 Service client: +33 1 23 45 67 89 | ✉️ contact@volconfi.com\n")
                                .add("Document généré le " + LocalDateTime.now().format(DATE_FORMATTER))
                                .addStyle(footerStyle);

                document.add(footerText);
        }

        /**
         * Crée une cellule d'en-tête de tableau avec style aviation
         */
        private Cell createHeaderCell(String content, Color primaryColor) {
                return new Cell()
                                .add(new Paragraph(content)
                                                .setBold()
                                                .setFontColor(ColorConstants.WHITE))
                                .setBackgroundColor(primaryColor)
                                .setPadding(4)
                                .setTextAlignment(TextAlignment.CENTER);
        }

        /**
         * Crée une cellule de tableau avec les styles appropriés
         */
        private Cell createCell(String content, boolean isBold) {
                Paragraph paragraph = new Paragraph(content);
                if (isBold) {
                        paragraph.setBold();
                }
                return new Cell().add(paragraph).setPadding(4);
        }

        /**
         * Ajoute une ligne au tableau avec les styles appropriés
         */
        private void addTableRow(Table table, String label, String value, Style labelStyle, Style valueStyle) {
                table.addCell(new Cell().add(new Paragraph(label).addStyle(labelStyle)).setPadding(4));
                table.addCell(new Cell().add(new Paragraph(value).addStyle(valueStyle)).setPadding(4));
        }
}