package controller;

import mg.itu.prom16.*;
import model.*;
import service.VolService;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AnnotedController
public class VolController {
    private VolService volService = new VolService();

    @AnnotedMth("testVol")
    public ModelView testVol() {
        System.out.println("🎯 TEST VOL CONTROLLER - Méthode simple appelée");
        ModelView mv = new ModelView("views/login.jsp");
        mv.addObject("message", "VolController fonctionne !");
        return mv;
    }

    /**
     * Afficher la liste des vols avec filtres (dashboard principal)
     */
    @AnnotedMth("vols")
    public ModelView showVols(CurrentSession session) {
        // Vérifier si l'utilisateur est connecté
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        System.out.println("=== Affichage liste des vols ===");

        try {
            // Récupérer tous les vols
            List<Vol> vols = volService.findAllVols();

            // Récupérer les données pour les filtres
            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();
            List<TypeSiege> typeSieges = volService.findAllTypeSieges();
            List<Promotion> promotions = volService.findActivePromotions();

            ModelView mv = new ModelView("views/vols/list.jsp");
            mv.addObject("user", user);
            mv.addObject("vols", vols);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            mv.addObject("typeSieges", typeSieges);
            mv.addObject("promotions", promotions);
            mv.addObject("message", vols.size() + " vol(s) trouvé(s)");

            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des vols:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/vols/list.jsp");
            mv.addObject("user", user);
            mv.addObject("error", "Erreur lors du chargement des vols");
            return mv;
        }
    }

    /**
     * Rechercher des vols avec filtres
     */
    @POST("searchVols")
    @AnnotedMth("searchVols")
    public ModelView searchVols(@Param(name = "filter") VolFilter filter, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        System.out.println("=== Recherche de vols avec filtres ===");
        System.out.println("Filtre reçu: " + filter);

        try {
            // Rechercher avec les filtres
            List<Vol> vols = volService.searchVolsWithFilters(filter);

            // Récupérer les données pour les filtres (pour repeupler les selects)
            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();
            List<TypeSiege> typeSieges = volService.findAllTypeSieges();
            List<Promotion> promotions = volService.findActivePromotions();

            ModelView mv = new ModelView("views/vols/list.jsp");
            mv.addObject("user", user);
            mv.addObject("vols", vols);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            mv.addObject("typeSieges", typeSieges);
            mv.addObject("promotions", promotions);
            mv.addObject("filter", filter); // Pour maintenir les valeurs des filtres
            mv.addObject("message", vols.size() + " vol(s) trouvé(s) avec les filtres appliqués");

            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche de vols:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/vols/list.jsp");
            mv.addObject("user", user);
            mv.addObject("error", "Erreur lors de la recherche");
            return mv;
        }
    }

    /**
     * Afficher le formulaire de création d'un vol
     */
    @AnnotedMth("createVolForm")
    public ModelView showCreateVolForm(CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();

            ModelView mv = new ModelView("views/vols/create.jsp");
            mv.addObject("user", user);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du formulaire de création:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/vols/list.jsp");
            mv.addObject("error", "Erreur lors du chargement du formulaire");
            return mv;
        }
    }

    /**
     * Créer un nouveau vol
     */
    @POST("createVol")
    @AnnotedMth("createVol")
    public ModelView createVol(
            @Param(name = "numeroVol") String numeroVol,
            @Param(name = "dateVol") String dateVolStr,
            @Param(name = "idVille") String idVilleStr,
            @Param(name = "idAvion") String idAvionStr,
            @Param(name = "prixMin") String prixMinStr,
            @Param(name = "prixMax") String prixMaxStr,
            CurrentSession session) {

        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            // Conversion des paramètres
            LocalDateTime dateVol = LocalDateTime.parse(dateVolStr.replace(" ", "T"));
            Long idVille = parseLongSafe(idVilleStr);
            Long idAvion = parseLongSafe(idAvionStr);
            BigDecimal prixMin = parseBigDecimalSafe(prixMinStr);
            BigDecimal prixMax = parseBigDecimalSafe(prixMaxStr);

            // Validation des champs obligatoires
            if (idVille == null) {
                throw new IllegalArgumentException("La ville de destination est obligatoire");
            }

            // Création de l'objet Vol
            Vol vol = new Vol();
            vol.setNumeroVol(numeroVol);
            vol.setDateVol(dateVol);
            vol.setIdVille(idVille);
            vol.setIdAvion(idAvion); // Peut être null
            vol.setPrixMin(prixMin); // Peut être null
            vol.setPrixMax(prixMax); // Peut être null

            if (volService.createVol(vol)) {
                ModelView mv = new ModelView("vols");
                session.add("successMessage", "Vol créé avec succès !");
                return mv;
            } else {
                throw new Exception("Échec de la création en base de données");
            }

        } catch (Exception e) {
            return reloadCreateFormWithError(session,
                    "Erreur: " + e.getMessage(),
                    numeroVol, dateVolStr, idVilleStr, idAvionStr);
        }
    }

    // Méthodes utilitaires
    private Long parseLongSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimalSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ModelView reloadCreateFormWithError(CurrentSession session,
            String errorMessage,
            String numeroVol,
            String dateVol,
            String idVille,
            String idAvion) {
        User user = (User) session.get("user");
        List<Ville> villes = volService.findAllVilles();
        List<Avion> avions = volService.findAllAvions();

        // Créer un objet Vol partiel pour pré-remplir le formulaire
        Vol vol = new Vol();
        vol.setNumeroVol(numeroVol);
        // Note: Les autres champs ne sont pas settés car non utilisés dans le
        // formulaire

        ModelView mv = new ModelView("views/vols/create.jsp");
        mv.addObject("user", user);
        mv.addObject("villes", villes);
        mv.addObject("avions", avions);
        mv.addObject("vol", vol);
        mv.addObject("error", errorMessage);

        // Ajouter les valeurs brutes pour les champs non mappés à l'objet Vol
        mv.addObject("rawDateVol", dateVol);
        mv.addObject("rawIdVille", idVille);
        mv.addObject("rawIdAvion", idAvion);

        return mv;
    }

    /**
     * Afficher le formulaire de modification d'un vol
     */
    @AnnotedMth("editVolForm")
    public ModelView showEditVolForm(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Vol vol = volService.findVolById(id);

            if (vol == null) {
                ModelView mv = new ModelView("vols");
                session.add("errorMessage", "Vol non trouvé");
                return mv;
            }

            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();

            ModelView mv = new ModelView("views/vols/edit.jsp");
            mv.addObject("user", user);
            mv.addObject("vol", vol);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            return mv;

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("vols");
            session.add("errorMessage", "ID de vol invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du formulaire de modification:");
            e.printStackTrace();

            ModelView mv = new ModelView("vols");
            session.add("errorMessage", "Erreur lors du chargement du formulaire");
            return mv;
        }
    }

    /**
     * Mettre à jour un vol
     */
    @POST("updateVol")
    @AnnotedMth("updateVol")
    public ModelView updateVol(
            @Param(name = "id") String idStr,
            @Param(name = "numeroVol") String numeroVol,
            @Param(name = "dateVol") String dateVolStr,
            @Param(name = "idVille") String idVilleStr,
            @Param(name = "idAvion") String idAvionStr,
            @Param(name = "prixMin") String prixMinStr,
            @Param(name = "prixMax") String prixMaxStr,
            CurrentSession session) {
        System.out.println("Paramètres reçus:");
        System.out.println("id: " + idStr);
        System.out.println("numeroVol: " + numeroVol);
        System.out.println("dateVol: " + dateVolStr);
        System.out.println("idVille: " + idVilleStr);
        System.out.println("idAvion: " + idAvionStr);
        System.out.println("prixMin: " + prixMinStr);
        System.out.println("prixMax: " + prixMaxStr);

        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            // Conversion des paramètres
            Long id = Long.parseLong(idStr);
            LocalDateTime dateVol = LocalDateTime.parse(dateVolStr.replace(" ", "T"));
            Long idVille = idVilleStr != null && !idVilleStr.isEmpty() ? Long.parseLong(idVilleStr) : null;
            Long idAvion = idAvionStr != null && !idAvionStr.isEmpty() ? Long.parseLong(idAvionStr) : null;
            BigDecimal prixMin = prixMinStr != null && !prixMinStr.isEmpty() ? new BigDecimal(prixMinStr) : null;
            BigDecimal prixMax = prixMaxStr != null && !prixMaxStr.isEmpty() ? new BigDecimal(prixMaxStr) : null;

            // Création de l'objet Vol
            Vol vol = new Vol();
            vol.setIdVol(id);
            vol.setNumeroVol(numeroVol);
            vol.setDateVol(dateVol);
            vol.setIdVille(idVille);
            vol.setIdAvion(idAvion);
            vol.setPrixMin(prixMin);
            vol.setPrixMax(prixMax);

            if (volService.updateVol(vol)) {
                ModelView mv = new ModelView("vols");
                session.add("successMessage", "Vol mis à jour avec succès !");
                return mv;
            } else {
                throw new Exception("Échec de la mise à jour");
            }

        } catch (NumberFormatException e) {
            // Gestion spécifique des erreurs de conversion numérique
            ModelView mv = new ModelView("vols");
            session.add("errorMessage", "Format numérique invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du vol:");
            e.printStackTrace();

            // Récupération des données pour réafficher le formulaire
            Long id = Long.parseLong(idStr);
            Vol vol = volService.findVolById(id);
            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();

            ModelView mv = new ModelView("views/vols/edit.jsp");
            mv.addObject("user", user);
            mv.addObject("vol", vol);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            mv.addObject("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return mv;
        }
    }

    /**
     * Supprimer un vol
     */
    @POST("deleteVol")
    @AnnotedMth("deleteVol")
    public ModelView deleteVol(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            System.out.println("=== Suppression du vol ID: " + id + " ===");

            if (volService.deleteVol(id)) {
                session.add("successMessage", "Vol supprimé avec succès !");
            } else {
                session.add("errorMessage", "Erreur lors de la suppression du vol");
            }

        } catch (NumberFormatException e) {
            session.add("errorMessage", "ID de vol invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression du vol:");
            e.printStackTrace();
            session.add("errorMessage", "Erreur interne lors de la suppression");
        }

        return new ModelView("vols");
    }

    /**
     * Afficher les détails d'un vol
     */
    @AnnotedMth("volDetails")
    public ModelView showVolDetails(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Vol vol = volService.findVolById(id);

            if (vol == null) {
                ModelView mv = new ModelView("vols");
                session.add("errorMessage", "Vol non trouvé");
                return mv;
            }

            ModelView mv = new ModelView("views/vols/details.jsp");
            mv.addObject("user", user);
            mv.addObject("vol", vol);
            return mv;

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("vols");
            session.add("errorMessage", "ID de vol invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des détails:");
            e.printStackTrace();

            ModelView mv = new ModelView("vols");
            session.add("errorMessage", "Erreur lors du chargement des détails");
            return mv;
        }
    }
}