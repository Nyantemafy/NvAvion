<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Réservations - Avion</title>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/listeVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            List<Vol> vols = (List<Vol>) request.getAttribute("vols");
            List<User> users = (List<User>) request.getAttribute("users");
            Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
            ReservationFilter filter = (ReservationFilter) request.getAttribute("filter");
            
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("successMessage");
            session.removeAttribute("errorMessage");
        %>

        <!-- Header avec actions -->
        <div class="header-actions">
            <div>
                <h1>📝 Gestion des Réservations</h1>
                <p>Bienvenue, <strong><%= user.getUsername() %></strong> 
                   (<%= user.getRole() %>) | 
                   <a href="vols">Liste des vols</a> | 
                   <a href="logout">Déconnexion</a>
                </p>
            </div>
            <div>
                <a href="createReservationForm" class="btn btn-primary">➕ Nouvelle Réservation</a>
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

        <!-- Statistiques (pour admin) -->
        <% if ("ADMIN".equals(user.getRole()) && stats != null) { %>
            <div class="stats-container">
                <h3>📊 Statistiques</h3>
                <div class="stats-grid">
                    <div class="stat-card">
                        <span class="stat-value"><%= stats.get("totalReservations") %></span>
                        <span class="stat-label">Réservations totales</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value"><%= String.format("%.2f€", stats.get("totalRevenue")) %></span>
                        <span class="stat-label">Chiffre d'affaires</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value"><%= stats.get("reservationsThisMonth") %></span>
                        <span class="stat-label">Ce mois-ci</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value"><%= stats.get("averageReservationValue") %>€</span>
                        <span class="stat-label">Moyenne par réservation</span>
                    </div>
                </div>
            </div>
        <% } %>

        <!-- Filtres -->
        <div class="filters-container">
            <h3>🔍 Filtres de Recherche</h3>
            <form method="post" action="searchReservations">
                <div class="filters-grid">

                    <!-- Filtre par client (username) -->
                    <div class="form-group">
                        <label>Client:</label>
                        <select name="username">
                            <option value="">-- Tous les clients --</option>
                            <% if (users != null) {
                                for (User client : users) { %>
                                <option value="<%= client.getUsername() %>" 
                                    <%= (filter != null && filter.getUsername() != null && filter.getUsername().equals(client.getUsername())) ? "selected" : "" %>>
                                    <%= client.getUsername() %> (<%= client.getEmail() %>)
                                </option>
                            <% }
                            } %>
                        </select>
                    </div>

                    <!-- Filtre par vol (numeroVol) -->
                    <div class="form-group">
                        <label>Vol:</label>
                        <select name="numeroVol">
                            <option value="">-- Tous les vols --</option>
                            <% if (vols != null) {
                                for (Vol vol : vols) { %>
                                <option value="<%= vol.getNumeroVol() %>"
                                    <%= (filter != null && filter.getNumeroVol() != null && filter.getNumeroVol().equals(vol.getNumeroVol())) ? "selected" : "" %>>
                                    <%= vol.getNumeroVol() %> - <%= vol.getNomVilleDestination() %>
                                </option>
                            <% }
                            } %>
                        </select>
                    </div>

                    <!-- Filtre par ville destination -->
                    <div class="form-group">
                        <label>Ville destination:</label>
                        <input type="text" name="villeDestination"
                            value="<%= (filter != null && filter.getVilleDestination() != null) ? filter.getVilleDestination() : "" %>"
                            placeholder="Ex: Paris">
                    </div>

                    <!-- Filtre par date de réservation (début) -->
                    <div class="form-group">
                        <label>Date réservation (à partir du):</label>
                        <input type="date" name="dateReservationDebut"
                            value="<%= (filter != null && filter.getDateReservationDebut() != null) ? filter.getDateReservationDebut() : "" %>">
                    </div>

                    <!-- Filtre par date de réservation (fin) -->
                    <div class="form-group">
                        <label>Date réservation (jusqu'au):</label>
                        <input type="date" name="dateReservationFin"
                            value="<%= (filter != null && filter.getDateReservationFin() != null) ? filter.getDateReservationFin() : "" %>">
                    </div>

                    <!-- Filtre par montant minimum -->
                    <div class="form-group">
                        <label>Montant minimum (€):</label>
                        <input type="number" name="prixMin" step="0.01"
                            value="<%= (filter != null && filter.getPrixMin() != null) ? filter.getPrixMin() : "" %>"
                            placeholder="0.00">
                    </div>

                    <!-- Filtre par montant maximum -->
                    <div class="form-group">
                        <label>Montant maximum (€):</label>
                        <input type="number" name="prixMax" step="0.01"
                            value="<%= (filter != null && filter.getPrixMax() != null) ? filter.getPrixMax() : "" %>"
                            placeholder="10000.00">
                    </div>
                </div>

                <div style="margin-top: 15px; text-align: center;">
                    <button type="submit" class="btn btn-primary">🔍 Rechercher</button>
                    <a href="reservations" class="btn btn-secondary">🔄 Réinitialiser</a>
                </div>
            </form>
        </div>

        <!-- Tableau des réservations -->
        <% if (reservations != null && !reservations.isEmpty()) { %>
           <table class="vol-table">
                <thead>
                    <tr>
                        <th>ID Réservation</th>
                        <th>Date</th>
                        <th>Client</th>
                        <th>Vol</th>
                        <th>Montant</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (Reservation resa : reservations) { %>
                        <tr>
                            <!-- Identifiant réservation -->
                            <td><strong><%= resa.getIdReservation() %></strong></td>

                            <!-- Date de réservation -->
                            <td><%= resa.getDateReservation().format(formatter) %></td>

                            <!-- Client -->
                            <td>
                                👤 <%= resa.getUsernameUser() %>
                            </td>

                            <!-- Vol -->
                            <td>
                                ✈️ <%= resa.getNumeroVol() %> 
                                vers <%= resa.getVilleDestination() %>
                                (avion: <%= resa.getPseudoAvion() %>)
                            </td>

                            <!-- Montant -->
                            <td>
                                <span class="price-range">
                                    <%= String.format("%.2f€", resa.getPrixTotal()) %>
                                </span>
                            </td>

                            <!-- Actions -->
                            <td>
                                <div class="action-buttons">
                                    <a href="reservationDetails?id=<%= resa.getIdReservation() %>" class="btn-small btn-view" title="Voir détails">
                                        👁️ Voir
                                    </a>
                                    <a href="editReservationForm?id=<%= resa.getIdReservation() %>" class="btn-small btn-edit" title="Modifier">
                                        ✏️ Modifier
                                    </a>
                                    <form method="post" action="deleteReservation" style="display: inline;" 
                                        onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer définitivement cette réservation ?');">
                                        <input type="hidden" name="id" value="<%= resa.getIdReservation() %>">
                                        <button type="submit" class="btn-small btn-delete" title="Supprimer">
                                            🗑️ Supprimer
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

        <% } else { %>
            <div style="text-align: center; padding: 40px; color: #666;">
                <h3>😔 Aucune réservation trouvée</h3>
                <p>Essayez de modifier vos critères de recherche ou 
                   <a href="createReservationForm">créez une nouvelle réservation</a>
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