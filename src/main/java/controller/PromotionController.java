package controller;

import java.time.LocalDate;
import java.util.List;

import mg.itu.prom16.*;
import model.*;
import service.CategorieAgeService;
import service.PromotionService;

public class PromotionController {

    private PromotionService promotionService = new PromotionService();
    private CategorieAgeService categorieAgeService = new CategorieAgeService();

    @AnnotedMth("promotions")
    public ModelView showPromotions(CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            List<Promotion> promotions = promotionService.findAllPromotions();

            ModelView mv = new ModelView("views/promotions/list.jsp");
            mv.addObject("user", user);
            mv.addObject("promotions", promotions);
            mv.addObject("message", promotions.size() + " promotion(s) trouvée(s)");
            return mv;

        } catch (Exception e) {
            e.printStackTrace();
            ModelView mv = new ModelView("views/promotions/list.jsp");
            mv.addObject("user", user);
            mv.addObject("error", "Erreur lors du chargement des promotions");
            return mv;
        }
    }

    @AnnotedMth("createPromotionForm")
    public ModelView showCreateForm(@Param(name = "volId") String volIdStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        Long volId = volIdStr != null ? Long.parseLong(volIdStr) : null;
        List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();

        ModelView mv = new ModelView("views/promotions/create.jsp");
        mv.addObject("user", user);
        mv.addObject("volId", volId);
        mv.addObject("categoriesAge", categoriesAge);
        return mv;
    }

    @POST("createPromotion")
    @AnnotedMth("createPromotion")
    public ModelView createPromotion(
            @Param(name = "nom") String nomPromotion,
            @Param(name = "reductionPourcentage") String tauxStr,
            @Param(name = "dateDebut") String dateDebutStr,
            @Param(name = "dateFin") String dateFinStr,
            @Param(name = "categorieAge") String categorieAgeIdStr,
            @Param(name = "idVol") String volIdStr,
            CurrentSession session) {

        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Integer taux = Integer.parseInt(tauxStr);
            LocalDate dateDebut = LocalDate.parse(dateDebutStr);
            LocalDate dateFin = LocalDate.parse(dateFinStr);
            Long volId = Long.parseLong(volIdStr);

            // Gestion de la catégorie d'âge (peut être null)
            Long categorieAgeId = null;
            if (categorieAgeIdStr != null && !categorieAgeIdStr.isEmpty()) {
                categorieAgeId = Long.parseLong(categorieAgeIdStr);
            }

            Promotion promo = new Promotion();
            promo.setNom(nomPromotion);
            promo.setReductionPourcentage(taux);
            promo.setDateDebut(dateDebut);
            promo.setDateFin(dateFin);
            promo.setIdVol(volId);

            // Création de l'objet CategorieAge si un ID est fourni
            if (categorieAgeId != null) {
                CategorieAge categorieAge = new CategorieAge();
                categorieAge.setIdCategorieAge(categorieAgeId.intValue());
                promo.setCategorieAge(categorieAge);
            }

            promotionService.createPromotion(promo);
            session.add("successMessage", "Promotion créée avec succès !");

            // Redirection vers les détails du vol plutôt que vers la liste des promotions
            return new ModelView("redirect:detailsVol?id=" + volId);

        } catch (Exception e) {
            e.printStackTrace();
            ModelView mv = new ModelView("views/promotions/create.jsp");
            mv.addObject("error", "Erreur: " + e.getMessage());
            mv.addObject("user", user);
            mv.addObject("volId", Long.parseLong(volIdStr));

            // Recharger les catégories d'âge pour le formulaire
            List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();
            mv.addObject("categoriesAge", categoriesAge);

            return mv;
        }
    }

    @AnnotedMth("editPromotionForm")
    public ModelView showEditForm(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Promotion promo = promotionService.findPromotionById(id);

            if (promo == null) {
                session.add("errorMessage", "Promotion introuvable");
                return new ModelView("promotions");
            }

            ModelView mv = new ModelView("views/promotions/edit.jsp");
            mv.addObject("user", user);
            mv.addObject("promotion", promo);
            return mv;

        } catch (Exception e) {
            e.printStackTrace();
            session.add("errorMessage", "Erreur lors du chargement du formulaire");
            return new ModelView("promotions");
        }
    }

    @POST("updatePromotion")
    @AnnotedMth("updatePromotion")
    public ModelView updatePromotion(
            @Param(name = "id") String idStr,
            @Param(name = "nomPromotion") String nomPromotion,
            @Param(name = "tauxReduction") String tauxStr,
            @Param(name = "dateDebut") String dateDebutStr,
            @Param(name = "dateFin") String dateFinStr,
            CurrentSession session) {

        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Integer taux = Integer.parseInt(tauxStr);
            LocalDate dateDebut = LocalDate.parse(dateDebutStr);
            LocalDate dateFin = LocalDate.parse(dateFinStr);

            Promotion promo = new Promotion();
            promo.setIdPromotion(id);
            promo.setNom(nomPromotion);
            promo.setReductionPourcentage(taux);
            promo.setDateDebut(dateDebut);
            promo.setDateFin(dateFin);

            promotionService.updatePromotion(promo);
            session.add("successMessage", "Promotion mise à jour !");

            return new ModelView("promotions");

        } catch (Exception e) {
            e.printStackTrace();
            session.add("errorMessage", "Erreur update: " + e.getMessage());
            return new ModelView("promotions");
        }
    }

    @POST("deletePromotion")
    @AnnotedMth("deletePromotion")
    public ModelView deletePromotion(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            promotionService.deletePromotion(id);
            session.add("successMessage", "Promotion supprimée !");
        } catch (Exception e) {
            e.printStackTrace();
            session.add("errorMessage", "Erreur suppression");
        }

        return new ModelView("promotions");
    }
}
