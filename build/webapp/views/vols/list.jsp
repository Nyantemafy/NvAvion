<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Vols - Avion</title>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/listeVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            List<Vol> vols = (List<Vol>) request.getAttribute("vols");
            List<Ville> villes = (List<Ville>) request.getAttribute("villes");
            List<Avion> avions = (List<Avion>) request.getAttribute("avions");
            List<TypeSiege> typeSieges = (List<TypeSiege>) request.getAttribute("typeSieges");
            List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
            VolFilter filter = (VolFilter) request.getAttribute("filter");
            
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("successMessage");
            session.removeAttribute("errorMessage");
        %>

        <!-- Header avec actions -->
        <div class="header-actions">
            <div>
                <h1>🛫 Gestion des Vols</h1>
                <p>Bienvenue, <strong><%= user.getUsername() %></strong> 
                   (<%= user.getRole() %>) | 
                   <a href="reservations">Allez reservez</a> | 
                   <a href="logout">Déconnexion</a>
                </p>
            </div>
            <div>
                <% if ("ADMIN".equals(user.getRole())) { %>
                    <a href="createVolForm" class="btn btn-primary">➕ Nouveau Vol</a>
                <% } %>
            </div>
        </div>

        <!-- Messages -->
        <% if (successMessage != null) { %>
            <div class="success-message">✅ <%= successMessage %></div>
        <% } %>
        <% if (errorMessage != null) { %>
            <div class="error-message">❌ <%= errorMessage %></div>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <div class="success-message">ℹ️ <%= request.getAttribute("message") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">❌ <%= request.getAttribute("error") %></div>
        <% } %>

        <!-- Filtres -->
        <div class="filters-container">
            <h3>🔍 Filtres de Recherche</h3>
            <form method="post" action="searchVols">
                <div class="filters-grid">
                    <!-- Filtre par promotion -->
                    <div class="form-group">
                        <label>Promotion:</label>
                        <select name="filter.promotionNom">
                            <option value="">-- Toutes les promotions --</option>
                            <% if (promotions != null) {
                                for (Promotion promo : promotions) { %>
                                <option value="<%= promo.getNom() %>" 
                                    <%= (filter != null && promo.getNom().equals(filter.getPromotionNom())) ? "selected" : "" %>>
                                    <%= promo.getNom() %>
                                </option>
                            <% }
                            } %>
                        </select>
                    </div>

                    <!-- Filtre par ville de destination -->
                    <div class="form-group">
                        <label>Destination:</label>
                        <select name="filter.villeDestination">
                            <option value="">-- Toutes les destinations --</option>
                            <% if (villes != null) {
                                for (Ville ville : villes) { %>
                                <option value="<%= ville.getNom() %>"
                                    <%= (filter != null && ville.getNom().equals(filter.getVilleDestination())) ? "selected" : "" %>>
                                    <%= ville.getNom() %>
                                </option>
                            <% }
                            } %>
                        </select>
                    </div>

                    <!-- Filtre par type de siège -->
                    <div class="form-group">
                        <label>Type de siège:</label>
                        <select name="filter.typeSiege">
                            <option value="">-- Tous les types --</option>
                            <% if (typeSieges != null) {
                                for (TypeSiege type : typeSieges) { %>
                                <option value="<%= type.getRubrique() %>"
                                    <%= (filter != null && type.getRubrique().equals(filter.getTypeSiege())) ? "selected" : "" %>>
                                    <%= type.getRubrique() %>
                                </option>
                            <% }
                            } %>
                        </select>
                    </div>

                    <!-- Filtre par numéro de vol -->
                    <div class="form-group">
                        <label>Numéro de vol:</label>
                        <input type="text" name="filter.numeroVol" 
                               value="<%= (filter != null && filter.getNumeroVol() != null) ? filter.getNumeroVol() : "" %>"
                               placeholder="Ex: AF123">
                    </div>

                    <!-- Filtre par prix minimum -->
                    <div class="form-group">
                        <label>Prix minimum (€):</label>
                        <input type="number" name="filter.prixMin" step="0.01"
                               value="<%= (filter != null && filter.getPrixMin() != null) ? filter.getPrixMin() : "" %>"
                               placeholder="0.00">
                    </div>

                    <!-- Filtre par prix maximum -->
                    <div class="form-group">
                        <label>Prix maximum (€):</label>
                        <input type="number" name="filter.prixMax" step="0.01"
                               value="<%= (filter != null && filter.getPrixMax() != null) ? filter.getPrixMax() : "" %>"
                               placeholder="1000.00">
                    </div>

                    <!-- Filtre par date de vol (début) -->
                    <div class="form-group">
                        <label>Date de vol (à partir du):</label>
                        <input type="date" name="filter.dateVolDebut"
                               value="<%= (filter != null && filter.getDateVolDebut() != null) ? filter.getDateVolDebut() : "" %>">
                    </div>

                    <!-- Filtre par date de vol (fin) -->
                    <div class="form-group">
                        <label>Date de vol (jusqu'au):</label>
                        <input type="date" name="filter.dateVolFin"
                               value="<%= (filter != null && filter.getDateVolFin() != null) ? filter.getDateVolFin() : "" %>">
                    </div>
                </div>

                <div style="margin-top: 15px; text-align: center;">
                    <button type="submit" class="btn btn-primary">🔍 Rechercher</button>
                    <a href="vols" class="btn btn-secondary">🔄 Réinitialiser</a>
                </div>
            </form>
        </div>

        <!-- Tableau des vols -->
        <% if (vols != null && !vols.isEmpty()) { %>
            <table class="vol-table">
                <thead>
                    <tr>
                        <th>Numéro Vol</th>
                        <th>Date & Heure</th>
                        <th>Destination</th>
                        <th>Avion</th>
                        <th>Promotion</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (Vol vol : vols) { %>
                        <tr>
                            <td><strong><%= vol.getNumeroVol() %></strong></td>
                            <td><%= vol.getDateVol().format(formatter) %></td>
                            <td>
                                🏙️ <%= vol.getNomVilleDestination() != null ? vol.getNomVilleDestination() : "N/A" %>
                            </td>
                            <td>
                                ✈️ <%= vol.getPseudoAvion() != null ? vol.getPseudoAvion() : "N/A" %>
                            </td>
                            <td>
                                <% if (vol.getPromotionNom() != null) { %>
                                    <span class="promotion-badge">
                                        <%= vol.getPromotionNom() %> 
                                        (-<%= vol.getPromotionReduction() %>%)
                                    </span>
                                <% } else { %>
                                    <em>Aucune</em>
                                <% } %>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <a href="volDetails?id=<%= vol.getIdVol() %>" class="btn-small btn-view" title="Voir détails">
                                        👁️ Voir
                                    </a>
                                    <% if ("ADMIN".equals(user.getRole())) { %>
                                        <a href="editVolForm?id=<%= vol.getIdVol() %>" class="btn-small btn-edit" title="Modifier">
                                            ✏️ Modifier
                                        </a>
                                        <form method="post" action="deleteVol" style="display: inline;" 
                                              onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer ce vol ?');">
                                            <input type="hidden" name="id" value="<%= vol.getIdVol() %>">
                                            <button type="submit" class="btn-small btn-delete" title="Supprimer">
                                                🗑️ Supprimer
                                            </button>
                                        </form>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <div style="text-align: center; padding: 40px; color: #666;">
                <h3>😔 Aucun vol trouvé</h3>
                <p>Essayez de modifier vos critères de recherche ou 
                   <% if ("ADMIN".equals(user.getRole())) { %>
                       <a href="createVolForm">créez un nouveau vol</a>
                   <% } else { %>
                       contactez un administrateur
                   <% } %>
                </p>
            </div>
        <% } %>
    </div>

    <script>
        // Auto-submit du formulaire quand on change un select
        document.querySelectorAll('.filters-container select').forEach(select => {
            select.addEventListener('change', function() {
                // On peut auto-submit ou laisser l'utilisateur cliquer sur Rechercher
                // this.form.submit();
            });
        });
    </script>
</body>
</html>