package controller;

import mg.itu.prom16.*;
import model.*;
import service.VolService;
import service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@AnnotedController
public class VolController {
    private VolService volService = new VolService();

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
    public ModelView createVol(@Param(name = "vol") Vol vol, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        System.out.println("=== Création d'un nouveau vol ===");
        System.out.println("Vol reçu: " + vol.getNumeroVol());

        try {
            if (volService.createVol(vol)) {
                ModelView mv = new ModelView("redirect:/vols");
                session.add("successMessage", "Vol créé avec succès !");
                return mv;
            } else {
                List<Ville> villes = volService.findAllVilles();
                List<Avion> avions = volService.findAllAvions();

                ModelView mv = new ModelView("views/vols/create.jsp");
                mv.addObject("user", user);
                mv.addObject("villes", villes);
                mv.addObject("avions", avions);
                mv.addObject("vol", vol);
                mv.addObject("error", "Erreur lors de la création du vol");
                return mv;
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création du vol:");
            e.printStackTrace();

            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();

            ModelView mv = new ModelView("views/vols/create.jsp");
            mv.addObject("user", user);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            mv.addObject("vol", vol);
            mv.addObject("error", "Erreur interne lors de la création");
            return mv;
        }
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
                ModelView mv = new ModelView("redirect:/vols");
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
            ModelView mv = new ModelView("redirect:/vols");
            session.add("errorMessage", "ID de vol invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du formulaire de modification:");
            e.printStackTrace();

            ModelView mv = new ModelView("redirect:/vols");
            session.add("errorMessage", "Erreur lors du chargement du formulaire");
            return mv;
        }
    }

    /**
     * Mettre à jour un vol
     */
    @POST("updateVol")
    @AnnotedMth("updateVol")
    public ModelView updateVol(@Param(name = "vol") Vol vol, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        System.out.println("=== Mise à jour du vol ID: " + vol.getIdVol() + " ===");

        try {
            if (volService.updateVol(vol)) {
                ModelView mv = new ModelView("redirect:/vols");
                session.add("successMessage", "Vol mis à jour avec succès !");
                return mv;
            } else {
                List<Ville> villes = volService.findAllVilles();
                List<Avion> avions = volService.findAllAvions();

                ModelView mv = new ModelView("views/vols/edit.jsp");
                mv.addObject("user", user);
                mv.addObject("vol", vol);
                mv.addObject("villes", villes);
                mv.addObject("avions", avions);
                mv.addObject("error", "Erreur lors de la mise à jour du vol");
                return mv;
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du vol:");
            e.printStackTrace();

            List<Ville> villes = volService.findAllVilles();
            List<Avion> avions = volService.findAllAvions();

            ModelView mv = new ModelView("views/vols/edit.jsp");
            mv.addObject("user", user);
            mv.addObject("vol", vol);
            mv.addObject("villes", villes);
            mv.addObject("avions", avions);
            mv.addObject("error", "Erreur interne lors de la mise à jour");
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

        return new ModelView("redirect:/vols");
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
                ModelView mv = new ModelView("redirect:/vols");
                session.add("errorMessage", "Vol non trouvé");
                return mv;
            }

            ModelView mv = new ModelView("views/vols/details.jsp");
            mv.addObject("user", user);
            mv.addObject("vol", vol);
            return mv;

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("redirect:/vols");
            session.add("errorMessage", "ID de vol invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des détails:");
            e.printStackTrace();

            ModelView mv = new ModelView("redirect:/vols");
            session.add("errorMessage", "Erreur lors du chargement des détails");
            return mv;
        }
    }
}