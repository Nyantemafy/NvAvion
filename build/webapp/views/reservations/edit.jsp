<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<%@ page import="model.Vol" %>
<%@ page import="model.Reservation" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Modifier Réservation - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/editVol.css">
</head>
<body>
<div class="container">
    <%
        User user = (User) request.getAttribute("user");
        Reservation reservation = (Reservation) request.getAttribute("reservation");
        List<Vol> vols = (List<Vol>) request.getAttribute("vols");
        List<User> users = (List<User>) request.getAttribute("users");
        String errorMessage = (String) request.getAttribute("error");
    %>

    <div class="header-actions">
        <div>
            <h1>✏️ Modifier Réservation #<%= reservation.getIdReservation() %></h1>
            <p>Connecté en tant que <strong><%= user.getUsername() %></strong> (<%= user.getRole() %>) | 
               <a href="reservations">Retour à la liste</a>
            </p>
        </div>
    </div>

    <% if (errorMessage != null) { %>
        <div class="error-message">❌ <%= errorMessage %></div>
    <% } %>

    <form method="post" action="updateReservation" class="vol-edit-form">
        <input type="hidden" name="id" value="<%= reservation.getIdReservation() %>">

        <div class="form-section">
            <h2>Détails de la réservation</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="dateReservation">Date & Heure *</label>
                    <input type="datetime-local" id="dateReservation" name="dateReservation"
                           value="<%= reservation.getDateReservation().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T"," ") %>"
                           required>
                </div>

                <div class="form-group">
                    <label for="prixTotal">Prix Total (€)</label>
                    <input type="number" id="prixTotal" name="prixTotal" step="0.01"
                           value="<%= reservation.getPrixTotal() %>">
                </div>
            </div>
        </div>

        <div class="form-section">
            <h2>Vol et Utilisateur</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="idVol">Vol *</label>
                    <select id="idVol" name="idVol" required>
                        <option value="">-- Sélectionnez --</option>
                        <% for (Vol vol : vols) { %>
                            <option value="<%= vol.getIdVol() %>" 
                                <%= vol.getIdVol().equals(reservation.getIdVol()) ? "selected" : "" %>>
                                <%= vol.getNumeroVol() %> - <%= vol.getNomVilleDestination() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="idUser">Utilisateur *</label>
                    <select id="idUser" name="idUser" required>
                        <option value="">-- Sélectionnez --</option>
                        <% for (User u : users) { %>
                            <option value="<%= u.getId() %>" 
                                <%= u.getId().equals(reservation.getIdUser()) ? "selected" : "" %>>
                                <%= u.getUsername() %> (<%= u.getRole() %>)
                            </option>
                        <% } %>
                    </select>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h2>Sièges</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="siegeBusiness">Sièges Business</label>
                    <input type="number" id="siegeBusiness" name="siegeBusiness"
                           value="<%= reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness() : "" %>">
                </div>

                <div class="form-group">
                    <label for="siegeEco">Sièges Éco</label>
                    <input type="number" id="siegeEco" name="siegeEco"
                           value="<%= reservation.getSiegeEco() != null ? reservation.getSiegeEco() : "" %>">
                </div>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">💾 Enregistrer</button>
            <a href="reservationDetails?id=<%= reservation.getIdReservation() %>" class="btn btn-secondary">❌ Annuler</a>
        </div>
    </form>
</div>

</body>
</html>
