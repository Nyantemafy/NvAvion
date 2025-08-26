<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.CategorieAge" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer une Nouvelle Promotion - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/createVol.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            Long volId = (Long) request.getAttribute("volId");
            List<CategorieAge> categoriesAge = (List<CategorieAge>) request.getAttribute("categoriesAge");
            String error = (String) request.getAttribute("error");
        %>

        <div class="header-actions">
            <div>
                <h1>🎯 Créer une Nouvelle Promotion</h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> (ADMIN) | 
                   <a href="vols">Retour à la liste des vols</a>
                </p>
            </div>
        </div>

        <% if (error != null) { %>
            <div class="error-message">❌ <%= error %></div>
        <% } %>

        <form method="post" action="createPromotion" class="flight-form">
            <input type="hidden" name="idVol" value="<%= volId %>">
            
            <div class="form-section">
                <h2>Informations de la promotion</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="nom">Nom de la promotion *</label>
                        <input type="text" id="nom" name="nom" required 
                               placeholder="Ex: Early Bird, Été 2023, etc.">
                    </div>

                    <div class="form-group">
                        <label for="reductionPourcentage">Réduction (%) *</label>
                        <input type="number" id="reductionPourcentage" name="reductionPourcentage" 
                               min="1" max="100" required placeholder="Ex: 15">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2>Période de validité</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="dateDebut">Date de début *</label>
                        <input type="date" id="dateDebut" name="dateDebut" required 
                               value="<%= LocalDate.now().toString() %>">
                    </div>

                    <div class="form-group">
                        <label for="dateFin">Date de fin *</label>
                        <input type="date" id="dateFin" name="dateFin" required 
                               value="<%= LocalDate.now().plusMonths(1).toString() %>">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2>Catégorie d'âge ciblée</h2>
                <div class="form-group">
                    <label for="categorieAge">Catégorie</label>
                    <select id="categorieAge" name="categorieAge">
                        <option value="">-- Toutes catégories --</option>
                        <%
                            if (categoriesAge != null && !categoriesAge.isEmpty()) {
                                for (CategorieAge categorie : categoriesAge) {
                                    if (categorie.getIsActive()) {
                                        String displayText = categorie.getNom() + " (" + categorie.getAgeMin() + 
                                            (categorie.getAgeMax() != null ? "-" + categorie.getAgeMax() : "+") + " ans)";
                        %>
                                        <option value="<%= categorie.getIdCategorieAge() %>">
                                            <%= displayText %>
                                        </option>
                        <%
                                    }
                                }
                            }
                        %>
                    </select>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-submit-flight">
                    <span class="btn-icon">🎯</span>
                    <span class="btn-text">Créer la Promotion</span>
                </button>
                <a href="volDetails?id=<%= volId %>" class="btn btn-secondary">Annuler</a>
            </div>
        </form>
    </div>

    <script>
        // Validation des dates
        document.getElementById('dateDebut').addEventListener('change', function() {
            const dateFin = document.getElementById('dateFin');
            if (new Date(this.value) > new Date(dateFin.value)) {
                dateFin.value = this.value;
            }
        });
    </script>
</body>
</html>