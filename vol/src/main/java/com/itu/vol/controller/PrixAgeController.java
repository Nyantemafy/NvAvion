package com.itu.vol.controller;

import com.itu.vol.dto.*;
import com.itu.vol.model.CategorieAge;
import com.itu.vol.model.PrixAgeVol;
import com.example.service.PrixAgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prix-age")
@CrossOrigin(origins = "*")
public class PrixAgeController {

    @Autowired
    private PrixAgeService prixAgeService;

    /**
     * Calculer le prix pour un utilisateur selon son âge
     */
    @PostMapping("/calculer")
    public ResponseEntity<Map<String, Object>> calculerPrix(@RequestBody Map<String, Object> request) {
        try {
            Long idVol = Long.valueOf(request.get("idVol").toString());
            Long idTypeSiege = Long.valueOf(request.get("idTypeSiege").toString());
            String dateNaissanceStr = request.get("dateNaissance").toString();
            LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr);

            BigDecimal prix = prixAgeService.calculerPrixSelonAge(idVol, idTypeSiege, dateNaissance);
            int age = prixAgeService.calculerAge(dateNaissance);

            return ResponseEntity.ok(Map.of(
                    "prix", prix,
                    "age", age,
                    "dateNaissance", dateNaissance,
                    "categorieAge",
                    prixAgeService.trouverCategorieAge(age).map(CategorieAge::getNom).orElse("Inconnue")));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors du calcul: " + e.getMessage()));
        }
    }

    /**
     * Obtenir tous les prix pour un vol et un utilisateur
     */
    @GetMapping("/vol/{idVol}/user/{userId}")
    public ResponseEntity<List<PrixCalculeDTO>> getPrixPourVolEtUser(
            @PathVariable Long idVol,
            @PathVariable Long userId) {
        try {
            List<PrixCalculeDTO> prix = prixAgeService.getPrixPourVolEtUser(idVol, userId);
            return ResponseEntity.ok(prix);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtenir toutes les catégories d'âge
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategorieAge>> getCategoriesAge() {
        List<CategorieAge> categories = prixAgeService.getToutesCategoriesAge();
        return ResponseEntity.ok(categories);
    }

    /**
     * Créer ou mettre à jour un prix spécifique
     */
    @PostMapping("/prix")
    public ResponseEntity<PrixAgeVol> creerOuMettreAJourPrix(@RequestBody Map<String, Object> request) {
        try {
            Long idVol = Long.valueOf(request.get("idVol").toString());
            Long idTypeSiege = Long.valueOf(request.get("idTypeSiege").toString());
            Long idCategorieAge = Long.valueOf(request.get("idCategorieAge").toString());
            BigDecimal prixBase = new BigDecimal(request.get("prixBase").toString());

            PrixAgeVol prix = prixAgeService.creerOuMettreAJourPrixAge(idVol, idTypeSiege, idCategorieAge, prixBase);
            return ResponseEntity.ok(prix);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Initialiser les prix pour un vol
     */
    @PostMapping("/initialiser-vol")
    public ResponseEntity<Map<String, String>> initialiserPrixVol(@RequestBody Map<String, Object> request) {
        try {
            Long idVol = Long.valueOf(request.get("idVol").toString());
            BigDecimal prixEco = new BigDecimal(request.get("prixEco").toString());
            BigDecimal prixBusiness = new BigDecimal(request.get("prixBusiness").toString());

            prixAgeService.initialiserPrixPourVol(idVol, prixEco, prixBusiness);

            return ResponseEntity.ok(Map.of("message", "Prix initialisés avec succès pour le vol " + idVol));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Calculer l'âge à partir d'une date de naissance
     */
    @PostMapping("/calculer-age")
    public ResponseEntity<Map<String, Object>> calculerAge(@RequestBody Map<String, String> request) {
        try {
            LocalDate dateNaissance = LocalDate.parse(request.get("dateNaissance"));
            int age = prixAgeService.calculerAge(dateNaissance);

            return ResponseEntity.ok(Map.of(
                    "age", age,
                    "dateNaissance", dateNaissance,
                    "categorieAge",
                    prixAgeService.trouverCategorieAge(age).map(CategorieAge::getNom).orElse("Inconnue")));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Date invalide"));
        }
    }
}