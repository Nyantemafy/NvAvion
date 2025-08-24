package com.itu.vol.service;

import com.example.model.*;
import com.example.dto.PrixCalculeDTO;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrixAgeService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategorieAgeRepository categorieAgeRepository;

    @Autowired
    private PrixAgeVolRepository prixAgeVolRepository;

    /**
     * Calculer le prix selon l'âge de l'utilisateur
     */
    public BigDecimal calculerPrixSelonAge(Long idVol, Long idTypeSiege, LocalDate dateNaissance) {
        if (dateNaissance == null) {
            // Si pas de date de naissance, utiliser le prix adulte par défaut
            return getPrixStandard(idVol, idTypeSiege);
        }

        // Utiliser la fonction SQL pour calculer le prix
        String sql = "SELECT calculer_prix_age(:idVol, :idTypeSiege, :dateNaissance)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idVol", idVol);
        query.setParameter("idTypeSiege", idTypeSiege);
        query.setParameter("dateNaissance", dateNaissance);

        BigDecimal prix = (BigDecimal) query.getSingleResult();
        return prix != null ? prix : BigDecimal.ZERO;
    }

    /**
     * Calculer l'âge depuis une date de naissance
     */
    public int calculerAge(LocalDate dateNaissance) {
        if (dateNaissance == null)
            return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /**
     * Trouver la catégorie d'âge pour un âge donné
     */
    public Optional<CategorieAge> trouverCategorieAge(int age) {
        String jpql = "SELECT ca FROM CategorieAge ca WHERE ca.ageMin <= :age " +
                "AND (ca.ageMax IS NULL OR ca.ageMax >= :age) AND ca.isActive = true";

        List<CategorieAge> categories = entityManager.createQuery(jpql, CategorieAge.class)
                .setParameter("age", age)
                .getResultList();

        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    /**
     * Obtenir tous les prix pour un vol et un utilisateur
     */
    public List<PrixCalculeDTO> getPrixPourVolEtUser(Long idVol, Long userId) {
        String sql = """
                SELECT
                    v.id_vol as idVol,
                    v.numero_vol_ as numeroVol,
                    ts.id_type_siege as idTypeSiege,
                    ts.rubrique as typeSiege,
                    u.id as userId,
                    u.username,
                    u.date_naissance as dateNaissance,
                    EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) as age,
                    ca.nom as categorieAge,
                    COALESCE(pav.prix_base, psv.prix_) as prixBase,
                    COALESCE(pav.prix_final, psv.prix_) as prixFinal,
                    COALESCE(ca.multiplicateur_prix, 1.0) as multiplicateur
                FROM vol v
                CROSS JOIN type_siege ts
                JOIN users u ON u.id = :userId
                LEFT JOIN categorie_age ca ON (
                    EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) >= ca.age_min
                    AND (ca.age_max IS NULL OR EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) <= ca.age_max)
                    AND ca.is_active = TRUE
                )
                LEFT JOIN prix_age_vol pav ON (
                    pav.id_vol = v.id_vol
                    AND pav.id_type_siege = ts.id_type_siege
                    AND pav.id_categorie_age = ca.id_categorie_age
                )
                LEFT JOIN prix_siege_vol_ psv ON (
                    psv.id_vol = v.id_vol
                    AND psv.id_type_siege = ts.id_type_siege
                )
                WHERE v.id_vol = :idVol
                ORDER BY ts.id_type_siege
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idVol", idVol);
        query.setParameter("userId", userId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream().map(row -> new PrixCalculeDTO(
                ((Number) row[0]).longValue(), // idVol
                (String) row[1], // numeroVol
                ((Number) row[2]).longValue(), // idTypeSiege
                (String) row[3], // typeSiege
                ((Number) row[4]).longValue(), // userId
                (String) row[5], // username
                row[6] != null ? ((java.sql.Date) row[6]).toLocalDate() : null, // dateNaissance
                row[7] != null ? ((Number) row[7]).intValue() : null, // age
                (String) row[8], // categorieAge
                (BigDecimal) row[9], // prixBase
                (BigDecimal) row[10], // prixFinal
                (BigDecimal) row[11] // multiplicateur
        )).toList();
    }

    /**
     * Créer ou mettre à jour un prix spécifique par âge
     */
    public PrixAgeVol creerOuMettreAJourPrixAge(Long idVol, Long idTypeSiege, Long idCategorieAge,
            BigDecimal prixBase) {
        Optional<PrixAgeVol> existant = prixAgeVolRepository
                .findByIdVolAndIdTypeSiegeAndIdCategorieAge(idVol, idTypeSiege, idCategorieAge);

        PrixAgeVol prixAgeVol;
        if (existant.isPresent()) {
            prixAgeVol = existant.get();
            prixAgeVol.setPrixBase(prixBase);
        } else {
            prixAgeVol = new PrixAgeVol(idVol, idTypeSiege, idCategorieAge, prixBase);
        }

        return prixAgeVolRepository.save(prixAgeVol);
    }

    /**
     * Obtenir le prix standard (sans spécificité d'âge)
     */
    private BigDecimal getPrixStandard(Long idVol, Long idTypeSiege) {
        String sql = "SELECT prix_ FROM prix_siege_vol_ WHERE id_vol = :idVol AND id_type_siege = :idTypeSiege";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idVol", idVol);
        query.setParameter("idTypeSiege", idTypeSiege);

        try {
            return (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Obtenir toutes les catégories d'âge actives
     */
    public List<CategorieAge> getToutesCategoriesAge() {
        return categorieAgeRepository.findByIsActiveTrueOrderByAgeMin();
    }

    /**
     * Initialiser les prix par âge pour un vol
     */
    public void initialiserPrixPourVol(Long idVol, BigDecimal prixBaseEco, BigDecimal prixBaseBusiness) {
        List<CategorieAge> categories = getToutesCategoriesAge();

        // Pour siège éco (supposons id = 1)
        for (CategorieAge categorie : categories) {
            creerOuMettreAJourPrixAge(idVol, 1L, categorie.getId(), prixBaseEco);
        }

        // Pour siège business (supposons id = 2)
        for (CategorieAge categorie : categories) {
            creerOuMettreAJourPrixAge(idVol, 2L, categorie.getId(), prixBaseBusiness);
        }
    }
}
