package com.itu.vol.service;

import com.itu.vol.dto.*;
import com.itu.vol.model.*;
import com.itu.vol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class ConfigPrixAgeService {

    @Autowired
    private PrixAgeVolRepository prixAgeVolRepository;

    @Autowired
    private TypeSiegeRepository typeSiegeRepository;

    @Autowired
    private CategorieAgeRepository categorieAgeRepository;

    @Autowired
    private VolRepository volRepository;

    /**
     * Initialise les prix de base pour un vol donné.
     * Crée un prix pour chaque type de siège et chaque catégorie d'âge.
     */
    public int initialiserPrixPourVol(Long idVol, BigDecimal prixEcoBase, BigDecimal prixBusinessBase) {
        int nbPrixCrees = 0;

        Vol vol = volRepository.findById(idVol)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé pour l'id: " + idVol));

        List<TypeSiege> sieges = typeSiegeRepository.findAll();
        List<CategorieAge> categories = categorieAgeRepository.findByIsActiveTrue();

        for (TypeSiege siege : sieges) {
            for (CategorieAge cat : categories) {
                boolean exists = prixAgeVolRepository.existsByVolAndTypeSiegeAndCategorieAge(vol, siege, cat);

                if (!exists) {
                    PrixAgeVol prix = new PrixAgeVol();
                    prix.setVol(vol);
                    prix.setTypeSiege(siege);
                    prix.setCategorieAge(cat);

                    if ("Business".equalsIgnoreCase(siege.getRubrique())) {
                        prix.setPrixBase(prixBusinessBase);
                    } else {
                        prix.setPrixBase(prixEcoBase);
                    }

                    prix.setMultiplicateur(cat.getMultiplicateurPrix());
                    prix.setPrixFinal(prix.getPrixBase().multiply(prix.getMultiplicateur()));

                    prixAgeVolRepository.save(prix);
                    nbPrixCrees++;
                }
            }
        }

        return nbPrixCrees;
    }

    /**
     * Configure un prix spécifique pour un vol, type de siège et catégorie d'âge.
     */
    public void configurerPrix(Vol vol, TypeSiege siege, CategorieAge cat,
            BigDecimal prixBase, BigDecimal multiplicateur) {

        PrixAgeVol prix = prixAgeVolRepository
                .findByVolAndTypeSiegeAndCategorieAge(vol, siege, cat)
                .orElseGet(() -> {
                    PrixAgeVol newPrix = new PrixAgeVol();
                    newPrix.setVol(vol);
                    newPrix.setTypeSiege(siege);
                    newPrix.setCategorieAge(cat);
                    return newPrix;
                });

        prix.setPrixBase(prixBase);
        prix.setMultiplicateur(multiplicateur);
        prix.setPrixFinal(prix.getPrixBase().multiply(prix.getMultiplicateur()));

        prixAgeVolRepository.save(prix);
    }

    public CategorieAge updateCategorieAge(Long id, CategorieAgeDTO dto) throws Exception {
        Optional<CategorieAge> optional = categorieAgeRepository.findById(id);
        if (!optional.isPresent()) {
            throw new Exception("Catégorie non trouvée avec l'id : " + id);
        }

        CategorieAge categorie = optional.get();
        categorie.setNom(dto.getNom());
        categorie.setAgeMin(dto.getAgeMin());
        categorie.setAgeMax(dto.getAgeMax());
        categorie.setMultiplicateurPrix(dto.getMultiplicateurPrix());
        categorie.setDescription(dto.getDescription());

        return categorieAgeRepository.save(categorie);
    }

    public CategorieAge createCategorieAge(CategorieAgeDTO dto) {
        CategorieAge categorie = new CategorieAge();
        categorie.setNom(dto.getNom());
        categorie.setAgeMin(dto.getAgeMin());
        categorie.setAgeMax(dto.getAgeMax());
        categorie.setMultiplicateurPrix(dto.getMultiplicateurPrix());
        categorie.setDescription(dto.getDescription());

        return categorieAgeRepository.save(categorie);
    }

    public List<PrixAgeVolDTO> getPrixParVol(Vol vol) {
        List<PrixAgeVol> prixList = prixAgeVolRepository.findByVol(vol);

        return prixList.stream().map(p -> {
            PrixAgeVolDTO dto = new PrixAgeVolDTO();
            dto.setIdPrixAgeVol(p.getId());

            if (p.getVol() != null) {
                dto.setIdVol(p.getVol().getId());
                dto.setNumeroVol(p.getVol().getNumeroVol());
            }

            if (p.getTypeSiege() != null) {
                dto.setIdTypeSiege(p.getTypeSiege().getId().intValue());
                dto.setTypeSiege(p.getTypeSiege().getRubrique());
            }

            if (p.getCategorieAge() != null && p.getCategorieAge().getId() != null) {
                dto.setIdCategorieAge(p.getCategorieAge().getId().intValue());
                dto.setCategorieNom(p.getCategorieAge().getNom());
            }

            dto.setPrixBase(p.getPrixBase());
            dto.setMultiplicateur(p.getMultiplicateur());
            dto.setPrixFinal(p.getPrixFinal());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<CategorieAge> getAllCategoriesAge() {
        return categorieAgeRepository.findAll();
    }

    /**
     * Obtenir tous les types de sièges
     */
    public List<TypeSiege> getAllTypeSieges() {
        return typeSiegeRepository.findAll();
    }

    /**
     * Calculer le prix pour un âge spécifique
     */
    public BigDecimal calculerPrixPourAge(Long idVol, Integer idTypeSiege, Integer age) {
        Optional<CategorieAge> categorieOpt = categorieAgeRepository.findCategorieForAge(age);

        if (categorieOpt.isPresent()) {
            CategorieAge categorie = categorieOpt.get();

            Vol vol = volRepository.findById(idVol)
                    .orElseThrow(() -> new RuntimeException("Vol non trouvé pour l'id: " + idVol));

            TypeSiege siege = typeSiegeRepository.findById(idTypeSiege)
                    .orElseThrow(() -> new RuntimeException("Type de siège non trouvé pour l'id: " + idTypeSiege));

            Optional<PrixAgeVol> prixOpt = prixAgeVolRepository.findPrixForVolSiegeAndAge(
                    vol,
                    siege,
                    age);

            if (prixOpt.isPresent()) {
                return prixOpt.get().getPrixFinal();
            }
        }

        return getPrixStandardParDefaut(idTypeSiege);
    }

    /**
     * Obtenir la catégorie d'âge pour un âge donné
     */
    public String getCategorieForAge(Integer age) {
        return categorieAgeRepository.findCategorieForAge(age)
                .map(CategorieAge::getNom)
                .orElse("Adulte");
    }

    /**
     * Simuler les prix pour différents âges (0 à 100 ans)
     */
    public List<SimulationPrixDTO> simulerPrixParAge(Long idVol) {
        return IntStream.rangeClosed(0, 100)
                .filter(age -> age % 5 == 0) // Tous les 5 ans
                .mapToObj(age -> {
                    SimulationPrixDTO simulation = new SimulationPrixDTO();
                    simulation.setAge(age);
                    simulation.setCategorieAge(getCategorieForAge(age));

                    // Prix économique
                    simulation.setPrixEconomique(calculerPrixPourAge(idVol, 1, age));

                    // Prix business
                    simulation.setPrixBusiness(calculerPrixPourAge(idVol, 2, age));

                    // Multiplicateur
                    Optional<CategorieAge> categorie = categorieAgeRepository.findCategorieForAge(age);
                    simulation.setMultiplicateur(categorie.map(CategorieAge::getMultiplicateurPrix)
                            .orElse(BigDecimal.ONE));

                    return simulation;
                })
                .toList();
    }

    private BigDecimal getPrixStandardParDefaut(Integer idTypeSiege) {
        return idTypeSiege == 1L ? new BigDecimal("200") : new BigDecimal("500");
    }
}