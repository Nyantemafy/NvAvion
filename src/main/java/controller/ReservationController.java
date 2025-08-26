package controller;

import mg.itu.prom16.*;
import model.*;
import service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@AnnotedController
public class ReservationController {
    private ReservationService reservationService = new ReservationService();
    private VolService volService = new VolService();
    private UserService userService = new UserService();
    private CategorieAgeService categorieAgeService = new CategorieAgeService();

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String PDF_SERVICE_URL = "http://localhost:8081/pdf-service/api/pdf/reservation";

    /**
     * Afficher la liste des réservations avec filtres (dashboard principal)
     */
    @AnnotedMth("reservations")
    public ModelView showReservations(CurrentSession session) {
        // Vérifier si l'utilisateur est connecté
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        System.out.println("=== Affichage liste des réservations ===");

        try {
            // Récupérer toutes les réservations
            List<Reservation> reservations = reservationService.findAllReservations();
            System.out.println(reservations);

            // Récupérer les données pour les filtres
            List<Vol> vols = volService.findAllVols();
            List<User> users = userService.findAllUsers();

            // Récupérer les statistiques (pour les admins)
            java.util.Map<String, Object> stats = null;
            if ("ADMIN".equals(user.getRole())) {
                stats = reservationService.getReservationStats();
            }

            ModelView mv = new ModelView("views/reservations/list.jsp");
            mv.addObject("user", user);
            mv.addObject("reservations", reservations);
            mv.addObject("vols", vols);
            mv.addObject("users", users);
            mv.addObject("stats", stats);
            mv.addObject("message", reservations.size() + " réservation(s) trouvée(s)");

            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des réservations:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/reservations/list.jsp");
            mv.addObject("user", user);
            mv.addObject("error", "Erreur lors du chargement des réservations");
            return mv;
        }
    }

    /**
     * Rechercher des réservations avec filtres
     */
    @POST("searchReservations")
    @AnnotedMth("searchReservations")
    public ModelView searchReservations(@Param(name = "filter") ReservationFilter filter, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        System.out.println("=== Recherche de réservations avec filtres ===");
        System.out.println("Filtre reçu: " + filter);

        try {
            // Rechercher avec les filtres
            List<Reservation> reservations = reservationService.searchReservationsWithFilters(filter);

            // Récupérer les données pour les filtres (pour repeupler les selects)
            List<Vol> vols = volService.findAllVols();
            List<User> users = userService.findAllUsers();

            // Récupérer les statistiques (pour les admins)
            java.util.Map<String, Object> stats = null;
            if ("ADMIN".equals(user.getRole())) {
                stats = reservationService.getReservationStats();
            }

            ModelView mv = new ModelView("views/reservations/list.jsp");
            mv.addObject("user", user);
            mv.addObject("reservations", reservations);
            mv.addObject("vols", vols);
            mv.addObject("users", users);
            mv.addObject("stats", stats);
            mv.addObject("filter", filter); // Pour maintenir les valeurs des filtres
            mv.addObject("message", reservations.size() + " réservation(s) trouvée(s) avec les filtres appliqués");

            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche de réservations:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/reservations/list.jsp");
            mv.addObject("user", user);
            mv.addObject("error", "Erreur lors de la recherche");
            return mv;
        }
    }

    /**
     * Afficher le formulaire de création d'une réservation
     */
    @AnnotedMth("createReservationForm")
    public ModelView showCreateReservationForm(CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            List<Vol> vols = volService.findAllVols();
            List<User> users = userService.findAllUsers();
            List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();

            ModelView mv = new ModelView("views/reservations/create.jsp");
            mv.addObject("user", user);
            mv.addObject("vols", vols);
            mv.addObject("users", users);
            mv.addObject("categoriesAge", categoriesAge);
            return mv;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du formulaire de modification:");
            e.printStackTrace();

            ModelView mv = new ModelView("views/reservations/list.jsp");
            mv.addObject("error", "Erreur lors du chargement du formulaire");
            return mv;
        }
    }

    /**
     * Créer une nouvelle réservation
     */
    @POST("createReservation")
    @AnnotedMth("createReservation")
    public ModelView createReservation(
            @Param(name = "dateReservation") String dateReservationStr,
            @Param(name = "idVol") String idVolStr,
            @Param(name = "idUser") String idUserStr,
            CurrentSession session) {

        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            LocalDateTime dateReservation = dateReservationStr != null && !dateReservationStr.isEmpty()
                    ? LocalDateTime.parse(dateReservationStr.replace(" ", "T"))
                    : LocalDateTime.now();

            Long idVol = parseLongSafe(idVolStr);
            Long idUser = parseLongSafe(idUserStr);

            if (idVol == null || idUser == null) {
                ModelView mv = new ModelView("views/reservations/create.jsp");
                mv.addObject("error", "Veuillez sélectionner un vol et un utilisateur");
                mv.addObject("user", user);
                mv.addObject("vols", volService.findAllVols());
                mv.addObject("users", userService.findAllUsers());
                mv.addObject("categoriesAge", categorieAgeService.findAllCategories());
                return mv;
            }

            // 👉 On utilise session.getParameterMap() au lieu de request.getParameterMap()
            Map<String, String[]> parameterMap = session.getParameterMap();

            Map<Long, Integer> siegeBusinessParCategorie = new HashMap<>();
            Map<Long, Integer> siegeEcoParCategorie = new HashMap<>();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue()[0];

                if (paramName.startsWith("siegeBusiness_")) {
                    Long categorieId = Long.parseLong(paramName.substring("siegeBusiness_".length()));
                    Integer quantity = parseIntegerSafe(paramValue);
                    if (quantity > 0) {
                        siegeBusinessParCategorie.put(categorieId, quantity);
                    }
                } else if (paramName.startsWith("siegeEco_")) {
                    Long categorieId = Long.parseLong(paramName.substring("siegeEco_".length()));
                    Integer quantity = parseIntegerSafe(paramValue);
                    if (quantity > 0) {
                        siegeEcoParCategorie.put(categorieId, quantity);
                    }
                }
            }

            if (siegeBusinessParCategorie.isEmpty() && siegeEcoParCategorie.isEmpty()) {
                ModelView mv = new ModelView("views/reservations/create.jsp");
                mv.addObject("error", "Veuillez réserver au moins un siège");
                mv.addObject("user", user);
                mv.addObject("vols", volService.findAllVols());
                mv.addObject("users", userService.findAllUsers());
                mv.addObject("categoriesAge", categorieAgeService.findAllCategories());
                return mv;
            }

            Reservation reservation = new Reservation();
            reservation.setDateReservation(dateReservation);
            reservation.setIdVol(idVol);
            reservation.setIdUser(idUser);
            reservation.setSiegeBusinessParCategorie(siegeBusinessParCategorie);
            reservation.setSiegeEcoParCategorie(siegeEcoParCategorie);

            int totalBusiness = siegeBusinessParCategorie.values().stream().mapToInt(Integer::intValue).sum();
            int totalEco = siegeEcoParCategorie.values().stream().mapToInt(Integer::intValue).sum();
            reservation.setSiegeBusiness(totalBusiness);
            reservation.setSiegeEco(totalEco);

            boolean success = reservationService.createReservation(reservation);

            if (success) {
                Reservation createdReservation = reservationService.findReservationById(reservation.getIdReservation());
                ModelView mv = new ModelView("views/reservations/details.jsp");
                mv.addObject("reservation", createdReservation);
                mv.addObject("user", user);
                mv.addObject("success", "Réservation créée avec succès ! Prix calculé: "
                        + createdReservation.getPrixTotal() + "€");
                mv.addObject("prixDetails", reservationService.getPrixDetailsForUser(
                        createdReservation.getIdVol(),
                        createdReservation.getIdUser()));
                return mv;
            } else {
                ModelView mv = new ModelView("views/reservations/create.jsp");
                mv.addObject("error", "Échec lors de la création de la réservation");
                mv.addObject("user", user);
                mv.addObject("vols", volService.findAllVols());
                mv.addObject("users", userService.findAllUsers());
                mv.addObject("categoriesAge", categorieAgeService.findAllCategories());
                return mv;
            }

        } catch (Exception e) {
            e.printStackTrace();
            ModelView mv = new ModelView("views/reservations/create.jsp");
            mv.addObject("error", "Erreur lors du traitement de la réservation: " + e.getMessage());
            mv.addObject("user", user);
            try {
                mv.addObject("vols", volService.findAllVols());
                mv.addObject("users", userService.findAllUsers());
                mv.addObject("categoriesAge", categorieAgeService.findAllCategories());
            } catch (Exception ignore) {
            }
            return mv;
        }
    }

    private BigDecimal parseBigDecimalSafe(String value) {
        try {
            return (value != null && !value.isEmpty()) ? new BigDecimal(value) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Long parseLongSafe(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntegerSafe(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Afficher le formulaire de modification d'une réservation
     */
    @AnnotedMth("editReservationForm")
    public ModelView showEditReservationForm(@Param(name = "id") String idStr,
            CurrentSession session) {
        User user = (User) session.get("user");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Reservation reservation = reservationService.findReservationById(id);

            if (reservation == null) {
                ModelView mv = new ModelView("reservations");
                session.add("errorMessage", "Réservation non trouvée");
                return mv;
            }

            List<Vol> vols = volService.findAllVols();
            List<User> users = userService.findAllUsers();
            List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();

            ModelView mv = new ModelView("views/reservations/edit.jsp");
            mv.addObject("user", user);
            mv.addObject("reservation", reservation);
            mv.addObject("vols", vols);
            mv.addObject("users", users);
            mv.addObject("categoriesAge", categoriesAge);
            return mv;

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "ID de réservation invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du formulaire de modification:");
            e.printStackTrace();

            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "Erreur lors du chargement du formulaire");
            return mv;
        }
    }

    /**
     * Mettre à jour une réservation
     */
    @POST("updateReservation")
    @AnnotedMth("updateReservation")
    public ModelView updateReservation(
            @Param(name = "id") String idStr,
            @Param(name = "dateReservation") String dateReservationStr,
            @Param(name = "idVol") String idVolStr,
            @Param(name = "idUser") String idUserStr,
            CurrentSession session) {

        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            LocalDateTime dateReservation = dateReservationStr != null && !dateReservationStr.isEmpty()
                    ? LocalDateTime.parse(dateReservationStr.replace(" ", "T"))
                    : LocalDateTime.now();

            Long idVol = parseLongSafe(idVolStr);
            Long idUser = parseLongSafe(idUserStr);

            // Récupérer toutes les quantités dynamiques pour les catégories d'âge
            Map<String, String[]> parameterMap = session.getParameterMap();
            Map<Long, Integer> siegeBusinessParCategorie = new HashMap<>();
            Map<Long, Integer> siegeEcoParCategorie = new HashMap<>();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue()[0];

                if (paramName.startsWith("siegeBusiness_")) {
                    Long categorieId = Long.parseLong(paramName.substring("siegeBusiness_".length()));
                    Integer qty = parseIntegerSafe(paramValue);
                    if (qty > 0)
                        siegeBusinessParCategorie.put(categorieId, qty);
                } else if (paramName.startsWith("siegeEco_")) {
                    Long categorieId = Long.parseLong(paramName.substring("siegeEco_".length()));
                    Integer qty = parseIntegerSafe(paramValue);
                    if (qty > 0)
                        siegeEcoParCategorie.put(categorieId, qty);
                }
            }

            if (siegeBusinessParCategorie.isEmpty() && siegeEcoParCategorie.isEmpty()) {
                // renvoyer au formulaire avec message d'erreur
                Reservation reservation = reservationService.findReservationById(id);
                List<Vol> vols = volService.findAllVols();
                List<User> users = userService.findAllUsers();
                List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();

                ModelView mv = new ModelView("views/reservations/edit.jsp");
                mv.addObject("user", user);
                mv.addObject("reservation", reservation);
                mv.addObject("vols", vols);
                mv.addObject("users", users);
                mv.addObject("categoriesAge", categoriesAge);
                mv.addObject("error", "Veuillez réserver au moins un siège");
                return mv;
            }

            // Créer l'objet Reservation pour mise à jour
            Reservation reservation = new Reservation();
            reservation.setIdReservation(id);
            reservation.setDateReservation(dateReservation);
            reservation.setIdVol(idVol);
            reservation.setIdUser(idUser);
            reservation.setSiegeBusinessParCategorie(siegeBusinessParCategorie);
            reservation.setSiegeEcoParCategorie(siegeEcoParCategorie);

            // Calculer le total général (pour compatibilité)
            int totalBusiness = siegeBusinessParCategorie.values().stream().mapToInt(Integer::intValue).sum();
            int totalEco = siegeEcoParCategorie.values().stream().mapToInt(Integer::intValue).sum();
            reservation.setSiegeBusiness(totalBusiness);
            reservation.setSiegeEco(totalEco);

            boolean success = reservationService.updateReservation(reservation);

            if (success) {
                session.add("successMessage", "Réservation mise à jour avec succès !");
                return new ModelView("reservations");
            } else {
                throw new Exception("Échec de la mise à jour");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour de la réservation:");
            e.printStackTrace();

            try {
                Long id = Long.parseLong(idStr);
                Reservation reservation = reservationService.findReservationById(id);
                List<Vol> vols = volService.findAllVols();
                List<User> users = userService.findAllUsers();
                List<CategorieAge> categoriesAge = categorieAgeService.findAllCategories();

                ModelView mv = new ModelView("views/reservations/edit.jsp");
                mv.addObject("user", session.get("user"));
                mv.addObject("reservation", reservation);
                mv.addObject("vols", vols);
                mv.addObject("users", users);
                mv.addObject("categoriesAge", categoriesAge);
                mv.addObject("error", "Erreur lors de la mise à jour: " + e.getMessage());
                return mv;
            } catch (Exception ex) {
                ModelView mv = new ModelView("reservations");
                session.add("errorMessage", "Erreur lors de la mise à jour");
                return mv;
            }
        }
    }

    /**
     * Supprimer une réservation
     */
    @POST("deleteReservation")
    @AnnotedMth("deleteReservation")
    public ModelView deleteReservation(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Accès non autorisé");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            System.out.println("=== Suppression de la réservation ID: " + id + " ===");

            if (reservationService.deleteReservation(id)) {
                session.add("successMessage", "Réservation supprimée avec succès !");
            } else {
                session.add("errorMessage", "Erreur lors de la suppression de la réservation");
            }

        } catch (NumberFormatException e) {
            session.add("errorMessage", "ID de réservation invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la réservation:");
            e.printStackTrace();
            session.add("errorMessage", "Erreur interne lors de la suppression");
        }

        return new ModelView("reservations");
    }

    /**
     * Afficher les détails d'une réservation
     */
    @AnnotedMth("reservationDetails")
    public ModelView showReservationDetails(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Reservation reservation = reservationService.findReservationById(id);

            if (reservation == null) {
                ModelView mv = new ModelView("reservations");
                session.add("errorMessage", "Réservation non trouvée");
                return mv;
            }

            // Récupérer les détails de prix pour affichage
            List<PrixDetail> prixDetails = reservationService.getPrixDetailsForUser(
                    reservation.getIdVol(),
                    reservation.getIdUser());

            ModelView mv = new ModelView("views/reservations/details.jsp");
            mv.addObject("user", user);
            mv.addObject("reservation", reservation);
            mv.addObject("prixDetails", prixDetails);
            return mv;

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "ID de réservation invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des détails:");
            e.printStackTrace();

            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "Erreur lors du chargement des détails");
            return mv;
        }
    }

    // @AnnotedMth("reservationDetails")
    // public ModelView showReservationDetails(@Param(name = "id") String idStr,
    // CurrentSession session) {
    // User user = (User) session.get("user");
    // if (user == null) {
    // ModelView mv = new ModelView("views/login.jsp");
    // mv.addObject("error", "Vous devez vous connecter d'abord");
    // return mv;
    // }

    // try {
    // Long id = Long.parseLong(idStr);
    // Reservation reservation = reservationService.findReservationById(id);

    // if (reservation == null) {
    // ModelView mv = new ModelView("reservations");
    // session.add("errorMessage", "Réservation non trouvée");
    // return mv;
    // }

    // ModelView mv = new ModelView("views/reservations/details.jsp");
    // mv.addObject("user", user);
    // mv.addObject("reservation", reservation);
    // return mv;

    // } catch (NumberFormatException e) {
    // ModelView mv = new ModelView("reservations");
    // session.add("errorMessage", "ID de réservation invalide");
    // return mv;
    // } catch (Exception e) {
    // System.err.println("❌ Erreur lors de l'affichage des détails:");
    // e.printStackTrace();

    // ModelView mv = new ModelView("reservations");
    // session.add("errorMessage", "Erreur lors du chargement des détails");
    // return mv;
    // }
    // }

    /**
     * Télécharger le PDF d'une réservation (NOUVELLE MÉTHODE)
     */
    @AnnotedMth("downloadReservationPdf")
    public ModelView downloadReservationPdf(@Param(name = "id") String idStr, CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        try {
            Long id = Long.parseLong(idStr);
            Reservation reservation = reservationService.findReservationById(id);

            if (reservation == null) {
                ModelView mv = new ModelView("reservations");
                session.add("errorMessage", "Réservation non trouvée");
                return mv;
            }

            System.out.println("📋 Génération du PDF pour la réservation ID: " + id);

            // Appeler le service PDF Spring Boot
            byte[] pdfContent = callPdfService(reservation);

            if (pdfContent != null) {
                // Créer une réponse pour télécharger le PDF
                // Note: Dans votre framework, vous devrez adapter cette partie
                // pour renvoyer directement le PDF au navigateur

                String fileName = String.format("Reservation_%06d.pdf", reservation.getIdReservation());

                // Ici, nous utilisons une approche de redirection vers une JSP spéciale
                // qui se chargera de servir le PDF
                ModelView mv = new ModelView("views/reservations/download-pdf.jsp");
                mv.addObject("pdfContent", pdfContent);
                mv.addObject("fileName", fileName);
                mv.addObject("contentLength", pdfContent.length);

                System.out.println("✅ PDF généré avec succès: " + fileName + " (" + pdfContent.length + " bytes)");
                return mv;

            } else {
                ModelView mv = new ModelView("reservations");
                session.add("errorMessage", "Erreur lors de la génération du PDF");
                return mv;
            }

        } catch (NumberFormatException e) {
            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "ID de réservation invalide");
            return mv;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF:");
            e.printStackTrace();

            ModelView mv = new ModelView("reservations");
            session.add("errorMessage", "Erreur lors de la génération du PDF");
            return mv;
        }
    }

    /**
     * Appelle le service PDF Spring Boot via HTTP
     */
    private byte[] callPdfService(Reservation reservation) {
        try {
            // Convertir la réservation en JSON pour l'envoyer au service PDF
            ReservationDTO dto = convertToDTO(reservation);
            String jsonPayload = objectMapper.writeValueAsString(dto);

            System.out.println("🌐 Appel du service PDF: " + PDF_SERVICE_URL);
            System.out.println("📦 Payload: " + jsonPayload);

            // Créer la requête HTTP POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PDF_SERVICE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Envoyer la requête
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                System.out.println("✅ Service PDF répondu avec succès (Code: " + response.statusCode() + ")");
                return response.body();
            } else {
                System.err.println("❌ Service PDF a répondu avec une erreur (Code: " + response.statusCode() + ")");
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Erreur lors de l'appel au service PDF:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convertit une Reservation en ReservationDTO pour l'envoi au service PDF
     */
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setIdReservation(reservation.getIdReservation());
        dto.setDateReservation(reservation.getDateReservation());
        dto.setPrixTotal(reservation.getPrixTotal());
        dto.setIdVol(reservation.getIdVol());
        dto.setIdUser(reservation.getIdUser());
        dto.setSiegeBusiness(reservation.getSiegeBusiness());
        dto.setSiegeEco(reservation.getSiegeEco());

        // Propriétés étendues
        dto.setNumeroVol(reservation.getNumeroVol());
        dto.setDateVol(reservation.getDateVol());
        dto.setVilleDestination(reservation.getVilleDestination());
        dto.setUsernameUser(reservation.getUsernameUser());
        dto.setPseudoAvion(reservation.getPseudoAvion());

        return dto;
    }
}
