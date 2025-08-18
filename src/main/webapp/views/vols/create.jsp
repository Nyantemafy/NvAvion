<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.Ville" %>
<%@ page import="model.Avion" %>
<%@ page import="model.Vol" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer un Nouveau Vol - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/createVol.css">
    <link rel="stylesheet" href="css/flight-form.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            List<Ville> villes = (List<Ville>) request.getAttribute("villes");
            List<Avion> avions = (List<Avion>) request.getAttribute("avions");
            Vol vol = (Vol) request.getAttribute("vol");
            String error = (String) request.getAttribute("error");
        %>

        <div class="header-actions">
            <div>
                <h1>✈️ Créer un Nouveau Vol</h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> (ADMIN) | 
                   <a href="vols">Retour à la liste</a> | 
                   <a href="reservation">Allez reservez</a>
                </p>
            </div>
        </div>

        <% if (error != null) { %>
            <div class="error-message">❌ <%= error %></div>
        <% } %>

        <form method="post" action="createVol" class="flight-form">
            <div class="form-section">
                <h2>Informations de base</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="numeroVol">Numéro de vol *</label>
                        <input type="text" id="numeroVol" name="numeroVol" 
                            value="<%= vol != null && vol.getNumeroVol() != null ? vol.getNumeroVol() : "" %>" 
                            required pattern="[A-Z]{2}\d{3,4}" 
                            title="2 lettres suivies de 3-4 chiffres (ex: AF123)">
                    </div>

                    <div class="form-group">
                        <label for="dateVol">Date & Heure *</label>
                        <input type="datetime-local" id="dateVol" name="dateVol" required>
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
                                    <%= vol != null && vol.getIdVille() != null && vol.getIdVille().equals(ville.getIdVille()) ? "selected" : "" %>>
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
                                    <%= vol != null && vol.getIdAvion() != null && vol.getIdAvion().equals(avion.getIdAvion()) ? "selected" : "" %>>
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
                            value="<%= vol != null && vol.getPrixMin() != null ? vol.getPrixMin() : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="prixMax">Prix maximum (€)</label>
                        <input type="number" id="prixMax" name="prixMax" step="0.01"
                            value="<%= vol != null && vol.getPrixMax() != null ? vol.getPrixMax() : "" %>">
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-submit-flight">
                    <span class="btn-icon">✈</span>
                    <span class="btn-text">Créer le Vol</span>
                </button>
                <a href="vols" class="btn btn-secondary">Annuler</a>
            </div>
        </form>
    </div>

    <script>
        // Validation du numéro de vol
        document.getElementById('numeroVol').addEventListener('input', function(e) {
            this.value = this.value.toUpperCase();
        });

        // Date par défaut = maintenant + 1 jour
        const now = new Date();
        now.setDate(now.getDate() + 1);
        document.getElementById('dateVol').value = now.toISOString().slice(0, 16);
    </script>
</body>
</html>