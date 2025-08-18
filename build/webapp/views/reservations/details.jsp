<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<%@ page import="model.Reservation" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails de la Réservation - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/detailsVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            Reservation reservation = (Reservation) request.getAttribute("reservation");
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        %>

        <div class="header-actions">
            <div>
                <h1>📋 Détails de la Réservation #<%= reservation.getIdReservation() %></h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> 
                   (<%= user.getRole() %>) | 
                   <a href="reservations">Retour à la liste des réservations</a>
                </p>
            </div>
        </div>

        <% if (errorMessage != null) { %>
            <div class="error-message">❌ <%= errorMessage %></div>
        <% } %>

        <div class="vol-details-container">
            <div class="vol-details-card">
                <div class="vol-details-header">
                    <h2>Vol <%= reservation.getNumeroVol() %></h2>
                    <span class="vol-status">Réservé</span>
                </div>

                <div class="vol-details-grid">
                    <div class="detail-group">
                        <h3>Informations principales</h3>
                        <div class="detail-item">
                            <span class="detail-label">Date & Heure de réservation :</span>
                            <span class="detail-value">
                                <%= reservation.getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Client :</span>
                            <span class="detail-value">
                                👤 <%= reservation.getUsernameUser() != null ? reservation.getUsernameUser() : "N/A" %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Destination :</span>
                            <span class="detail-value">
                                🏙️ <%= reservation.getVilleDestination() != null ? reservation.getVilleDestination() : "N/A" %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Avion :</span>
                            <span class="detail-value">
                                ✈️ <%= reservation.getPseudoAvion() != null ? reservation.getPseudoAvion() : "N/A" %>
                            </span>
                        </div>
                    </div>

                    <div class="detail-group">
                        <h3>Configuration des sièges</h3>
                        <div class="detail-item">
                            <span class="detail-label">Sièges Classe Affaire :</span>
                            <span class="detail-value"><%= reservation.getSiegeBusiness() %> sièges</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Sièges Classe Éco :</span>
                            <span class="detail-value"><%= reservation.getSiegeEco() %> sièges</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Total de sièges :</span>
                            <span class="detail-value"><%= reservation.getSiegeBusiness() + reservation.getSiegeEco() %> sièges</span>
                        </div>
                    </div>

                    <div class="detail-group">
                        <h3>Tarification</h3>
                        <div class="detail-item">
                            <span class="detail-label">Prix total :</span>
                            <span class="detail-value"><%= reservation.getPrixTotal() %> €</span>
                        </div>
                    </div>

                    <div class="detail-group">
                        <h3>Détails du vol</h3>
                        <div class="detail-item">
                            <span class="detail-label">Date du vol :</span>
                            <span class="detail-value">
                                <%= reservation.getDateVol() != null 
                                    ? reservation.getDateVol().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) 
                                    : "N/A" %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Numéro du vol :</span>
                            <span class="detail-value"><%= reservation.getNumeroVol() %></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="vol-map-container">
                <div class="map-placeholder">
                    <div class="map-overlay">
                        <h3>Itinéraire du vol</h3>
                        <p>Départ → <%= reservation.getVilleDestination() %></p>
                    </div>
                </div>
            </div>
        </div>

        <div class="vol-actions">
            <% if ("ADMIN".equals(user.getRole())) { %>
                <a href="editReservationForm?id=<%= reservation.getIdReservation() %>" class="btn btn-primary">✏️ Modifier cette réservation</a>
                <form method="post" action="deleteReservation" style="display: inline;" 
                      onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cette réservation ?');">
                    <input type="hidden" name="id" value="<%= reservation.getIdReservation() %>">
                    <button type="submit" class="btn btn-danger">🗑️ Supprimer</button>
                </form>
            <% } %>
        </div>
    </div>
</body>
</html>
