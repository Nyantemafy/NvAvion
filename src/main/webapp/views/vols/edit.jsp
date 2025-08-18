<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.User" %>
<%@ page import="model.Vol" %>
<%@ page import="model.Ville" %>
<%@ page import="model.Avion" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Modifier Vol - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/editVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            Vol vol = (Vol) request.getAttribute("vol");
            List<Ville> villes = (List<Ville>) request.getAttribute("villes");
            List<Avion> avions = (List<Avion>) request.getAttribute("avions");
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        %>

        <div class="header-actions">
            <div>
                <h1>✏️ Modifier Vol <%= vol.getNumeroVol() %></h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> (ADMIN) | 
                   <a href="vols">Retour à la liste</a> | 
                   <a href="reservations">Allez reservez</a>
                </p>
            </div>
        </div>

        <% if (errorMessage != null) { %>
            <div class="error-message">❌ <%= errorMessage %></div>
        <% } %>

        <form method="post" action="updateVol" class="vol-edit-form">
            <input type="hidden" name="id" value="<%= vol.getIdVol() %>">

            <div class="form-section">
                <h2>Informations de base</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="numeroVol">Numéro de vol *</label>
                        <input type="text" id="numeroVol" name="numeroVol" 
                               value="<%= vol.getNumeroVol() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="dateVol">Date & Heure *</label>
                        <input type="datetime-local" id="dateVol" name="dateVol" 
                            value="<%= vol.getDateVol().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ") %>" required>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2>Destination et Avion</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="idVille">Ville de destination *</label>
                        <select id="idVille" name="idVille" required>
                            <option value="">-- Sélectionnez --</option>
                            <% for (Ville ville : villes) { %>
                                <option value="<%= ville.getIdVille() %>" 
                                    <%= ville.getIdVille().equals(vol.getIdVille()) ? "selected" : "" %>>
                                    <%= ville.getNom() %>
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="idAvion">Avion *</label>
                        <select id="idAvion" name="idAvion" required>
                            <option value="">-- Sélectionnez --</option>
                            <% for (Avion avion : avions) { %>
                                <option value="<%= avion.getIdAvion() %>"
                                    <%= avion.getIdAvion().equals(vol.getIdAvion()) ? "selected" : "" %>>
                                    <%= avion.getPseudo() %> 
                                    (<%= avion.getSiegeBusiness() %> biz, <%= avion.getSiegeEco() %> éco)
                                </option>
                            <% } %>
                        </select>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2>Configuration des prix</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="prixMin">Prix minimum (€)</label>
                        <input type="number" id="prixMin" name="prixMin" step="0.01"
                               value="<%= vol.getPrixMin() != null ? vol.getPrixMin() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="prixMax">Prix maximum (€)</label>
                        <input type="number" id="prixMax" name="prixMax" step="0.01"
                               value="<%= vol.getPrixMax() != null ? vol.getPrixMax() : "" %>">
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">💾 Enregistrer</button>
                <a href="volDetails?id=<%= vol.getIdVol() %>" class="btn btn-secondary">❌ Annuler</a>
            </div>
        </form>
    </div>

    <script>
        // Validation avant soumission
        document.querySelector('.vol-edit-form').addEventListener('submit', function(e) {
            const numeroVol = document.getElementById('numeroVol').value;
            if (!/^[A-Z]{2}\d{3,4}$/.test(numeroVol)) {
                alert('Le numéro de vol doit commencer par 2 lettres suivies de 3-4 chiffres (ex: AF123)');
                e.preventDefault();
            }
        });
    </script>
</body>
</html>