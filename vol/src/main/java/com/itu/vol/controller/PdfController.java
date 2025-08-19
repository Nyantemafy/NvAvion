package com.itu.vol.controller;

import com.itu.vol.dto.ReservationDTO;
import com.itu.vol.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/pdf")
@Validated
@CrossOrigin(origins = "*") // Permet l'accès depuis votre projet principal
public class PdfController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Génère et retourne un PDF de réservation
     */
    @PostMapping("/reservation")
    public ResponseEntity<byte[]> generateReservationPdf(@Valid @RequestBody ReservationDTO reservation) {
        try {
            System.out.println("📋 Génération du PDF pour la réservation ID: " + reservation.getIdReservation());

            // Générer le PDF
            byte[] pdfContent = pdfGeneratorService.generateReservationPdf(reservation);

            // Nom du fichier
            String fileName = String.format("Reservation_%06d_%s.pdf",
                    reservation.getIdReservation(),
                    LocalDateTime.now().format(FILE_DATE_FORMATTER));

            // Headers HTTP pour le téléchargement
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            System.out.println("✅ PDF généré avec succès: " + fileName + " (" + pdfContent.length + " bytes)");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PDF Service is running");
    }

    /**
     * Endpoint de test avec des données exemple
     */
    @GetMapping("/test")
    public ResponseEntity<byte[]> generateTestPdf() {
        try {
            // Créer des données de test
            ReservationDTO testReservation = createTestReservation();

            // Générer le PDF
            byte[] pdfContent = pdfGeneratorService.generateReservationPdf(testReservation);

            // Headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Test_Reservation.pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF de test:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée une réservation de test
     */
    private ReservationDTO createTestReservation() {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setIdReservation(123456L);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setPrixTotal(new java.math.BigDecimal("750.50"));
        reservation.setIdVol(1L);
        reservation.setIdUser(1L);
        reservation.setSiegeBusiness(1);
        reservation.setSiegeEco(2);

        // Propriétés étendues
        reservation.setNumeroVol("AF1234");
        reservation.setDateVol(LocalDateTime.now().plusDays(15));
        reservation.setVilleDestination("Paris");
        reservation.setUsernameUser("john.doe");
        reservation.setPseudoAvion("Boeing 737-800");

        return reservation;
    }
}