<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<%@ page import="model.Vol" %>
<%@ page import="model.Reservation" %>
<%@ page import="model.CategorieAge" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Modifier Réservation - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/editVol.css">
    <link rel="stylesheet" href="css/flight-form.css">
</head>
<body>
<div class="container">
    <%
        User user = (User) request.getAttribute("user");
        Reservation reservation = (Reservation) request.getAttribute("reservation");
        List<Vol> vols = (List<Vol>) request.getAttribute("vols");
        List<User> users = (List<User>) request.getAttribute("users");
        List<CategorieAge> categoriesAge = (List<CategorieAge>) request.getAttribute("categoriesAge");
        String errorMessage = (String) request.getAttribute("error");
        Map<Long, Integer> siegeBusinessParCategorie = reservation.getSiegeBusinessParCategorie();
        Map<Long, Integer> siegeEcoParCategorie = reservation.getSiegeEcoParCategorie();
    %>

    <div class="header-actions">
        <div>
            <h1>✏️ Modifier Réservation #<%= reservation.getIdReservation() %></h1>
            <p>Connecté en tant que <strong><%= user.getUsername() %></strong> | 
               <a href="reservations">Retour à la liste</a>
            </p>
        </div>
    </div>

    <% if (errorMessage != null) { %>
        <div class="error-message">❌ <%= errorMessage %></div>
    <% } %>

    <form method="post" action="updateReservation" class="flight-form" id="reservationForm">
        <input type="hidden" name="id" value="<%= reservation.getIdReservation() %>">

        <div class="form-section">
            <h2>Informations de la réservation</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="idUser">Client *</label>
                    <select id="idUser" name="idUser" required>
                        <option value="">-- Sélectionnez --</option>
                        <% for (User u : users) { %>
                            <option value="<%= u.getId() %>"
                                <%= reservation.getIdUser() != null && reservation.getIdUser().equals(u.getId()) ? "selected" : "" %>>
                                <%= u.getUsername() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="idVol">Vol *</label>
                    <select id="idVol" name="idVol" required>
                        <option value="">-- Sélectionnez --</option>
                        <% for (Vol v : vols) { %>
                            <option value="<%= v.getIdVol() %>"
                                data-numero="<%= v.getNumeroVol() %>"
                                data-destination="<%= v.getNomVilleDestination() %>"
                                <%= reservation.getIdVol() != null && reservation.getIdVol().equals(v.getIdVol()) ? "selected" : "" %>>
                                <%= v.getNumeroVol() %> - <%= v.getNomVilleDestination() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="dateReservation">Date & Heure *</label>
                    <input type="datetime-local" id="dateReservation" name="dateReservation"
                           value="<%= reservation.getDateReservation() != null 
                               ? reservation.getDateReservation().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T"," ") 
                               : "" %>"
                           required>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h2>Sièges par Catégorie d'Âge</h2>
            <div class="age-categories">
                <% if (categoriesAge != null) { %>
                    <% for (CategorieAge categorie : categoriesAge) { 
                        Long catId = Long.valueOf(categorie.getIdCategorieAge());
                        Integer businessQty = siegeBusinessParCategorie != null ? siegeBusinessParCategorie.getOrDefault(catId, 0) : 0;
                        Integer ecoQty = siegeEcoParCategorie != null ? siegeEcoParCategorie.getOrDefault(catId, 0) : 0;
                    %>
                        <div class="age-category">
                            <h3><%= categorie.getNom() %></h3>
                            <div class="category-description">
                                <%= categorie.getDescription() != null ? categorie.getDescription() :
                                    "Âge: " + categorie.getAgeMin() +
                                    (categorie.getAgeMax() != null ? " à " + categorie.getAgeMax() : " et plus") %>
                            </div>
                            <div class="form-group">
                                <label for="siegeBusiness_<%= catId %>">Sièges Économique</label>
                                <input type="number" id="siegeBusiness_<%= catId %>" 
                                       name="siegeBusiness_<%= catId %>" 
                                       min="0" value="<%= businessQty %>">
                            </div>
                            <div class="form-group">
                                <label for="siegeEco_<%= catId %>">Sièges Business</label>
                                <input type="number" id="siegeEco_<%= catId %>" 
                                       name="siegeEco_<%= catId %>" 
                                       min="0" value="<%= ecoQty %>">
                            </div>
                            <div class="price-error" id="priceError_<%= catId %>"></div>
                        </div>
                    <% } %>
                <% } else { %>
                    <p style="color:red;">⚠️ Aucune catégorie d'âge trouvée.</p>
                <% } %>
            </div>
        </div>

        <input type="hidden" id="prixTotal" name="prixTotal" value="<%= reservation.getPrixTotal() != null ? reservation.getPrixTotal() : 0 %>">

        <div class="form-actions">
            <button type="submit" class="btn btn-primary btn-submit-flight" id="submitButton">
                <span class="btn-icon">💾</span>
                <span class="btn-text">Enregistrer les Modifications</span>
            </button>
            <a href="reservationDetails?id=<%= reservation.getIdReservation() %>" class="btn btn-secondary">❌ Annuler</a>
        </div>
    </form>
</div>
</body>
</html>
