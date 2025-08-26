<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.Vol" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Reservation" %>
<%@ page import="model.CategorieAge" %>
<!DOCTYPE html>
<html>
<head>
    <title>Créer une Nouvelle Réservation - Avion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/createVol.css">
    <link rel="stylesheet" href="css/flight-form.css">
</head>
<body>
    <div class="container">
        <%
            User user = (User) request.getAttribute("user");
            List<Vol> vols = (List<Vol>) request.getAttribute("vols");
            List<User> users = (List<User>) request.getAttribute("users");
            List<CategorieAge> categoriesAge = (List<CategorieAge>) request.getAttribute("categoriesAge");
            Reservation reservation = (Reservation) request.getAttribute("reservation");
            String error = (String) request.getAttribute("error");
        %>

        <div class="header-actions">
            <div>
                <h1>📋 Créer une Nouvelle Réservation</h1>
                <p>Connecté en tant que <strong><%= user.getUsername() %></strong> | 
                   <a href="reservations">Retour à la liste</a>
                </p>
            </div>
        </div>

        <% if (error != null) { %>
            <div class="error-message">❌ <%= error %></div>
        <% } %>

        <form method="post" action="createReservation" class="flight-form" id="reservationForm">
            <div class="form-section">
                <h2>Informations de la réservation</h2>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="idUser">Client *</label>
                        <select id="idUser" name="idUser" required>
                            <option value="">-- Sélectionnez --</option>
                            <% for (User u : users) { %>
                                <option value="<%= u.getId() %>"
                                    <%= reservation != null && reservation.getIdUser() != null 
                                        && reservation.getIdUser().equals(u.getId()) ? "selected" : "" %>>
                                    <%= u.getUsername() %>
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="idVol">Vol *</label>
                        <select id="idVol" name="idVol" required onchange="updatePriceCalculation()">
                            <option value="">-- Sélectionnez --</option>
                            <% for (Vol v : vols) { %>
                                <option value="<%= v.getIdVol() %>"
                                    <%= reservation != null && reservation.getIdVol() != null
                                        && reservation.getIdVol().equals(v.getIdVol()) ? "selected" : "" %>>
                                    <%= v.getNumeroVol() %> - <%= v.getNomVilleDestination() %>
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="dateReservation">Date & Heure de réservation *</label>
                        <input type="datetime-local" id="dateReservation" name="dateReservation"
                            value="<%= reservation != null && reservation.getDateReservation() != null
                                ? reservation.getDateReservation().toString().replace("T"," ") : "" %>"
                            required>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2>Sièges par Catégorie d'Âge</h2>
                <div class="age-categories">
                    <% if (categoriesAge != null) { %>
                        <% for (CategorieAge categorie : categoriesAge) { %>
                            <div class="age-category">
                                <h3><%= categorie.getNom() %></h3>
                                <div class="category-description">
                                    <%= categorie.getDescription() != null ? categorie.getDescription() :
                                        "Âge: " + categorie.getAgeMin() +
                                        (categorie.getAgeMax() != null ? " à " + categorie.getAgeMax() : " et plus") %>
                                </div>
                                <div class="form-group">
                                    <label for="siegeBusiness_<%= categorie.getIdCategorieAge() %>">Sièges Business</label>
                                    <input type="number" id="siegeBusiness_<%= categorie.getIdCategorieAge() %>" 
                                        name="siegeBusiness_<%= categorie.getIdCategorieAge() %>" 
                                        min="0" value="0" onchange="updatePriceCalculation()">
                                </div>
                                <div class="form-group">
                                    <label for="siegeEco_<%= categorie.getIdCategorieAge() %>">Sièges Économique</label>
                                    <input type="number" id="siegeEco_<%= categorie.getIdCategorieAge() %>" 
                                        name="siegeEco_<%= categorie.getIdCategorieAge() %>" 
                                        min="0" value="0" onchange="updatePriceCalculation()">
                                </div>
                                <div class="price-display" id="price_<%= categorie.getIdCategorieAge() %>">
                                    Prix: 0,00 €
                                </div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p style="color:red;">⚠️ Aucune catégorie d'âge trouvée.</p>
                    <% } %>

                </div>
                
                <div class="total-price" id="totalPriceDisplay">
                    Prix Total: 0,00 €
                </div>
            </div>

            <input type="hidden" id="prixTotal" name="prixTotal" value="0">

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-submit-flight">
                    <span class="btn-icon">📋</span>
                    <span class="btn-text">Créer la Réservation</span>
                </button>
                <a href="reservations" class="btn btn-secondary">Annuler</a>
            </div>
        </form>
    </div>

    <script>
        // Date par défaut = maintenant
        const now = new Date();
        document.getElementById('dateReservation').value = now.toISOString().slice(0,16);
        
        // Fonction pour mettre à jour le calcul du prix
        function updatePriceCalculation() {
            const idVol = document.getElementById('idVol').value;
            
            if (!idVol) {
                return;
            }
            
            // Récupérer les quantités par catégorie et classe
            const quantities = {};
            <% for (CategorieAge categorie : categoriesAge) { %>
                quantities[<%= categorie.getIdCategorieAge() %>] = {
                    business: parseInt(document.getElementById('siegeBusiness_<%= categorie.getIdCategorieAge() %>').value) || 0,
                    eco: parseInt(document.getElementById('siegeEco_<%= categorie.getIdCategorieAge() %>').value) || 0
                };
            <% } %>
            
            // Appel AJAX pour calculer le prix
            fetch('calculatePrice', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `idVol=${idVol}&quantities=${JSON.stringify(quantities)}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Mettre à jour les prix par catégorie
                    <% for (CategorieAge categorie : categoriesAge) { %>
                        const priceElement<%= categorie.getIdCategorieAge() %> = document.getElementById('price_<%= categorie.getIdCategorieAge() %>');
                        // Trouver le prix pour cette catégorie dans les détails
                        let categoryPrice = 0;
                        if (data.details) {
                            for (const detail of data.details) {
                                if (detail.categorieAgeId === <%= categorie.getIdCategorieAge() %>) {
                                    categoryPrice += detail.prixTotal;
                                }
                            }
                        }
                        priceElement<%= categorie.getIdCategorieAge() %>.textContent = `Prix: ${categoryPrice.toFixed(2)} €`;
                    <% } %>
                    
                    // Mettre à jour le prix total
                    document.getElementById('totalPriceDisplay').textContent = `Prix Total: ${data.prixTotal.toFixed(2)} €`;
                    document.getElementById('prixTotal').value = data.prixTotal;
                } else {
                    alert('Erreur lors du calcul du prix: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Erreur:', error);
                alert('Erreur lors du calcul du prix');
            });
        }

        // Initialiser le calcul du prix si un vol est déjà sélectionné
        document.addEventListener('DOMContentLoaded', function() {
            if (document.getElementById('idVol').value) {
                updatePriceCalculation();
            }
        });
    </script>
</body>
</html>
