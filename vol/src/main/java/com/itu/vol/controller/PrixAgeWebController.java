package com.itu.vol.controller;

import com.itu.vol.dto.*;
import com.itu.vol.model.*;
import com.itu.vol.repository.*;
import com.itu.vol.service.ConfigPrixAgeService;
import com.itu.vol.service.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/prix-age")
public class PrixAgeWebController {

    @Autowired
    private ConfigPrixAgeService configPrixAgeService;

    @Autowired
    private VolService volService;

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private TypeSiegeRepository typeSiegeRepository;

    @Autowired
    private CategorieAgeRepository categorieAgeRepository;

    /**
     * Page principale - Dashboard de configuration
     */
    @GetMapping
    public String index(Model model) {
        try {
            List<CategorieAge> categories = configPrixAgeService.getAllCategoriesAge();
            List<Vol> vols = volService.getAllVols();

            // Statistiques
            model.addAttribute("totalCategories", categories.size());
            model.addAttribute("totalVols", vols.size());
            model.addAttribute("categories", categories);
            model.addAttribute("vols", vols);

            return "prix-age/index";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement: " + e.getMessage());
            return "prix-age/index";
        }
    }

    /**
     * Gestion des catégories d'âge
     */
    @GetMapping("/categories")
    public String categories(Model model) {
        try {
            List<CategorieAge> categories = configPrixAgeService.getAllCategoriesAge();
            model.addAttribute("categories", categories);
            model.addAttribute("categorieForm", new CategorieAgeForm());

            return "prix-age/categories";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des catégories");
            return "prix-age/categories";
        }
    }

    /**
     * Créer une nouvelle catégorie d'âge
     */
    @PostMapping("/categories/create")
    public String createCategorie(@Valid @ModelAttribute("categorieForm") CategorieAgeForm form,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Données invalides");
            return "redirect:/prix-age/categories";
        }

        try {
            CategorieAge categorie = configPrixAgeService.createCategorieAge(form.toDTO());
            redirectAttributes.addFlashAttribute("success",
                    "Catégorie '" + categorie.getNom() + "' créée avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la création: " + e.getMessage());
        }

        return "redirect:/prix-age/categories";
    }

    /**
     * Mettre à jour une catégorie
     */
    @PostMapping("/categories/{id}/update")
    public String updateCategorie(@PathVariable Long id,
            @Valid @ModelAttribute CategorieAgeForm form,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Données invalides");
            return "redirect:/prix-age/categories";
        }

        try {
            configPrixAgeService.updateCategorieAge(id, form.toDTO());
            redirectAttributes.addFlashAttribute("success", "Catégorie mise à jour");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la mise à jour: " + e.getMessage());
        }

        return "redirect:/prix-age/categories";
    }

    /**
     * Configuration des prix pour un vol spécifique
     */
    @GetMapping("/vol/{idVol}")
    public String volPrix(@PathVariable Long idVol, Model model) {
        try {
            Vol vol = volService.getVolById(idVol);
            if (vol == null) {
                model.addAttribute("error", "Vol non trouvé");
                return "redirect:/prix-age";
            }

            List<PrixAgeVolDTO> prixVol = configPrixAgeService.getPrixParVol(vol);
            List<CategorieAge> categories = configPrixAgeService.getAllCategoriesAge();
            List<TypeSiege> typeSieges = configPrixAgeService.getAllTypeSieges();

            model.addAttribute("vol", vol);
            model.addAttribute("prixVol", prixVol);
            model.addAttribute("categories", categories);
            model.addAttribute("typeSieges", typeSieges);
            model.addAttribute("configForm", new ConfigPrixForm());
            model.addAttribute("initForm", new InitPrixVolForm());

            return "prix-age/vol-prix";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/prix-age";
        }
    }

    /**
     * Initialiser tous les prix pour un vol
     */
    @PostMapping("/vol/{idVol}/initialiser")
    public String initialiserPrixVol(@PathVariable Long idVol,
            @Valid @ModelAttribute("initForm") InitPrixVolForm form,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Prix invalides");
            return "redirect:/prix-age/vol/" + idVol;
        }

        try {
            int nbPrixCrees = configPrixAgeService.initialiserPrixPourVol(
                    idVol, form.getPrixEcoBase(), form.getPrixBusinessBase());

            redirectAttributes.addFlashAttribute("success",
                    nbPrixCrees + " prix initialisés avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l'initialisation: " + e.getMessage());
        }

        return "redirect:/prix-age/vol/" + idVol;
    }

    /**
     * Configurer un prix spécifique
     */
    @PostMapping("/vol/{idVol}/configurer")
    public String configurerPrix(@PathVariable Long idVol,
            @Valid @ModelAttribute("configForm") ConfigPrixForm form,
            BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Configuration invalide");
            return "redirect:/prix-age/vol/" + idVol;
        }

        try {
            Vol vol = volRepository.findById(idVol).orElseThrow();
            TypeSiege siege = typeSiegeRepository.findById(form.getIdTypeSiege().longValue()).orElseThrow();
            CategorieAge cat = categorieAgeRepository.findById(form.getIdCategorieAge().longValue()).orElseThrow();

            configPrixAgeService.configurerPrix(vol, siege, cat,
                    form.getPrixBase(), form.getMultiplicateur());

            redirectAttributes.addFlashAttribute("success", "Prix configuré avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la configuration: " + e.getMessage());
        }

        return "redirect:/prix-age/vol/" + idVol;
    }

    /**
     * Simulation des prix pour différents âges
     */
    @GetMapping("/vol/{idVol}/simulation")
    public String simulationPrix(@PathVariable Long idVol, Model model) {
        try {
            Vol vol = volService.getVolById(idVol);
            List<SimulationPrixDTO> simulation = configPrixAgeService.simulerPrixParAge(idVol);

            model.addAttribute("vol", vol);
            model.addAttribute("simulation", simulation);

            return "prix-age/simulation";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la simulation: " + e.getMessage());
            return "redirect:/prix-age/vol/" + idVol;
        }
    }

    /**
     * API AJAX pour obtenir les prix en temps réel
     */
    @GetMapping("/api/vol/{idVol}/prix/{age}")
    @ResponseBody
    public PrixCalculeResponse calculerPrixPourAge(@PathVariable Long idVol, @PathVariable Integer age) {
        try {
            // Calculer les prix pour cet âge spécifique
            List<TypeSiege> typeSieges = configPrixAgeService.getAllTypeSieges();
            PrixCalculeResponse response = new PrixCalculeResponse();

            for (TypeSiege typeSiege : typeSieges) {
                BigDecimal prix = configPrixAgeService.calculerPrixPourAge(idVol, typeSiege.getId(), age);
                if ("Économique".equals(typeSiege.getRubrique())) {
                    response.setPrixEco(prix);
                } else if ("Business".equals(typeSiege.getRubrique())) {
                    response.setPrixBusiness(prix);
                }
            }

            response.setAge(age);
            response.setCategorieAge(configPrixAgeService.getCategorieForAge(age));

            return response;

        } catch (Exception e) {
            PrixCalculeResponse errorResponse = new PrixCalculeResponse();
            errorResponse.setError("Erreur: " + e.getMessage());
            return errorResponse;
        }
    }
}