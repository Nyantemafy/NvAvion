<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="model.Vol" %>
<%@ page import="model.Promotion" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails du Vol - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/detailsVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            Vol vol = (Vol) request.getAttribute("vol");
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        %>
        

        <div class="header-actions">
            <div>
                <h1>✈️ Détails du Vol <%= vol.getNumeroVol() %></h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> 
                   (<%= user.getRole() %>) | 
                   <a href="vols">Retour à la liste</a> | 
                   <a href="reservations">Allez reservez</a>
                </p>
            </div>
        </div>

        <% if (errorMessage != null) { %>
            <div class="error-message">❌ <%= errorMessage %></div>
        <% } %>

        <div class="vol-details-container">
            <div class="vol-details-card">
                <div class="vol-details-header">
                    <h2>Vol <%= vol.getNumeroVol() %></h2>
                    <span class="vol-status">Actif</span>
                </div>

                <div class="vol-details-grid">
                    <div class="detail-group">
                        <h3>Informations principales</h3>
                        <div class="detail-item">
                            <span class="detail-label">Date & Heure :</span>
                            <span class="detail-value">
                                <%= vol.getDateVol().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Destination :</span>
                            <span class="detail-value">
                                🏙️ <%= vol.getNomVilleDestination() != null ? vol.getNomVilleDestination() : "N/A" %>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Avion :</span>
                            <span class="detail-value">
                                ✈️ <%= vol.getPseudoAvion() != null ? vol.getPseudoAvion() : "N/A" %>
                            </span>
                        </div>
                    </div>

                    <div class="detail-group">
                        <h3>Configuration</h3>
                        <div class="detail-item">
                            <span class="detail-label">Nombre de sièges :</span>
                            <span class="detail-value">180</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Classe Affaire :</span>
                            <span class="detail-value">20 sièges</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Classe Éco :</span>
                            <span class="detail-value">160 sièges</span>
                        </div>
                    </div>

                    <div class="detail-group">
                        <h3>Tarification</h3>
                        <div class="detail-item">
                            <span class="detail-label">Prix de base :</span>
                            <span class="detail-value">250.00€</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Promotions :</span>
                            <span class="detail-value">
                                <span class="promotion-badge">Early Bird (-15%)</span>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Prix final :</span>
                            <span class="detail-value price-final">212.50€</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="vol-map-container">
                <div class="map-placeholder">
                    <div class="map-overlay">
                        <h3>Itinéraire du vol</h3>
                        <p>Paris (CDG) → <%= vol.getNomVilleDestination() %></p>
                    </div>
                </div>
            </div>
        </div>

        <div class="detail-item">
            <span class="detail-label">Promotions :</span>
            <span class="detail-value">
                <%
                    List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
                    if (promotions != null && !promotions.isEmpty()) {
                        for (Promotion promo : promotions) {
                %>
                            <div class="promotion-item">
                                <span class="promotion-badge">
                                    <%= promo.getNom() %> 
                                </span>
                                <% if ("ADMIN".equals(user.getRole())) { %>
                                    <!-- Boutons Update / Delete -->
                                    <!-- <a href="editPromotionForm?id=<%= promo.getIdPromotion() %>&volId=<%= vol.getIdVol() %>" 
                                    class="btn btn-small btn-primary">✏️ Update</a> -->
                                    <form method="post" action="deletePromotion" style="display:inline;"
                                        onsubmit="return confirm('Supprimer cette promotion ?');">
                                        <input type="hidden" name="id" value="<%= promo.getIdPromotion() %>">
                                        <input type="hidden" name="idVol" value="<%= promo.getIdVol() %>">
                                        <button type="submit" class="btn btn-small btn-danger">🗑️ Delete</button>
                                    </form>

                                <% } %>
                            </div>
                <%
                        }
                    } else {
                %>
                        <span>Aucune promotion disponible</span>
                <%
                    }
                %>
            </span>
        </div>
        
        <% if ("ADMIN".equals(user.getRole())) { %>
            <div class="promotion-actions">
                <a href="createPromotionForm?volId=<%= vol.getIdVol() %>" class="btn btn-success">➕ Nouvelle Promotion</a>
            </div>
        <% } %>
        
        <div class="vol-actions">
            <% if ("ADMIN".equals(user.getRole())) { %>
                <a href="editVolForm?id=<%= vol.getIdVol() %>" class="btn btn-primary">✏️ Modifier ce vol</a>
                <form method="post" action="deleteVol" style="display: inline;" 
                      onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer ce vol ?');">
                    <input type="hidden" name="id" value="<%= vol.getIdVol() %>">
                    <button type="submit" class="btn btn-danger">🗑️ Supprimer ce vol</button>
                </form>
            <% } %>
            <a href="reserveVolForm?id=<%= vol.getIdVol() %>" class="btn btn-secondary">🎫 Réserver ce vol</a>
        </div>
    </div>
</body>
</html>