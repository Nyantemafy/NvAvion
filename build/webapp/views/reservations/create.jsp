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

        <button class="debug-toggle" onclick="toggleDebug()">Mode Débogage</button>
        <div class="debug-info" id="debugInfo">
            Mode débogage activé - Les informations de requête s'afficheront ici
        </div>

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
                                    data-numero="<%= v.getNumeroVol() %>"
                                    data-destination="<%= v.getNomVilleDestination() %>"
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
                <div class="loading" id="loadingIndicator">
                    Calcul des prix en cours...
                </div>
                
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
                                <div class="price-error" id="priceError_<%= categorie.getIdCategorieAge() %>"></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p style="color:red;">⚠️ Aucune catégorie d'âge trouvée.</p>
                    <% } %>
                </div>
                
                <div class="total-price" id="totalPriceDisplay">
                    Prix Total: 0,00 €
                </div>
                <div class="price-error" id="totalPriceError"></div>
            </div>

            <input type="hidden" id="prixTotal" name="prixTotal" value="0">

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-submit-flight" id="submitButton">
                    <span class="btn-icon">📋</span>
                    <span class="btn-text">Créer la Réservation</span>
                </button>
                <a href="reservations" class="btn btn-secondary">Annuler</a>
            </div>
        </form>
    </div>

    <script>
        // Variable pour le débogage
        var debugMode = false;
        var lastRequestData = null;
        var lastResponseData = null;
        
        function toggleDebug() {
            debugMode = !debugMode;
            var debugInfo = document.getElementById('debugInfo');
            debugInfo.style.display = debugMode ? 'block' : 'none';
            
            if (debugMode && lastRequestData) {
                updateDebugInfo();
            }
            
            alert('Mode débogage ' + (debugMode ? 'activé' : 'désactivé'));
        }
        
        function updateDebugInfo() {
            var debugInfo = document.getElementById('debugInfo');
            var info = '=== DERNIÈRE REQUÊTE ===\n';
            info += 'URL: calculatePrice\n';
            info += 'Méthode: POST\n';
            info += 'Données: ' + JSON.stringify(lastRequestData, null, 2) + '\n\n';
            
            info += '=== DERNIÈRE RÉPONSE ===\n';
            info += 'Données: ' + JSON.stringify(lastResponseData, null, 2) + '\n\n';
            
            info += '=== VOL SÉLECTIONNÉ ===\n';
            var volSelect = document.getElementById('idVol');
            var selectedVol = volSelect.options[volSelect.selectedIndex];
            info += 'ID: ' + volSelect.value + '\n';
            info += 'Vol: ' + (selectedVol ? selectedVol.text : 'Aucun') + '\n\n';
            
            info += '=== QUANTITÉS SAISIES ===\n';
            var quantities = {};
            <% for (CategorieAge categorie : categoriesAge) { %>
                var businessSeats = parseInt(document.getElementById('siegeBusiness_<%= categorie.getIdCategorieAge() %>').value) || 0;
                var ecoSeats = parseInt(document.getElementById('siegeEco_<%= categorie.getIdCategorieAge() %>').value) || 0;
                
                if (businessSeats > 0 || ecoSeats > 0) {
                    quantities[<%= categorie.getIdCategorieAge() %>] = {
                        business: businessSeats,
                        eco: ecoSeats
                    };
                }
            <% } %>
            info += JSON.stringify(quantities, null, 2);
            
            debugInfo.textContent = info;
        }
        
        function updatePriceCalculation() {
            var idVol = document.getElementById('idVol').value;
            var loadingIndicator = document.getElementById('loadingIndicator');
            var debugInfo = document.getElementById('debugInfo');
            
            // Masquer les erreurs précédentes
            hideAllErrors();
            
            if (!idVol) {
                resetPrices();
                return;
            }
            
            // Afficher l'indicateur de chargement
            loadingIndicator.style.display = 'block';
            
            // Récupérer les quantités par catégorie et classe
            var quantities = {};
            var hasSeats = false;
            
            <% for (CategorieAge categorie : categoriesAge) { %>
                var businessSeats = parseInt(document.getElementById('siegeBusiness_<%= categorie.getIdCategorieAge() %>').value) || 0;
                var ecoSeats = parseInt(document.getElementById('siegeEco_<%= categorie.getIdCategorieAge() %>').value) || 0;
                
                if (businessSeats > 0 || ecoSeats > 0) {
                    hasSeats = true;
                    quantities[<%= categorie.getIdCategorieAge() %>] = {
                        business: businessSeats,
                        eco: ecoSeats
                    };
                }
            <% } %>
            
            if (!hasSeats) {
                resetPrices();
                loadingIndicator.style.display = 'none';
                return;
            }
            
            // Stocker les données pour le débogage
            lastRequestData = {
                idVol: idVol,
                quantities: quantities
            };
            
            // Informations de débogage
            if (debugMode) {
                updateDebugInfo();
            }
            
            // Appel AJAX pour calculer le prix
            fetch('calculatePrice', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'idVol=' + encodeURIComponent(idVol) + '&quantities=' + encodeURIComponent(JSON.stringify(quantities))
            })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Erreur réseau: ' + response.status);
                }
                return response.json();
            })
            .then(function(data) {
                console.log('Réponse du serveur:', data);
                lastResponseData = data;
                
                if (debugMode) {
                    updateDebugInfo();
                }
                
                if (data.success) {
                    updatePrices(data);
                    
                    // Afficher un avertissement si le prix total est 0
                    if (data.prixTotal === 0 || data.prixTotal === "0") {
                        document.getElementById('totalPriceError').textContent = 
                            '⚠️ Attention: Le prix calculé est de 0. Cela peut indiquer un problème de configuration des prix.';
                    }
                } else {
                    console.error('Erreur dans la réponse:', data);
                    if (data.error) {
                        document.getElementById('totalPriceError').textContent = 'Erreur: ' + data.error;
                    } else {
                        document.getElementById('totalPriceError').textContent = 
                            'Erreur inconnue lors du calcul des prix.';
                    }
                }
                
                loadingIndicator.style.display = 'none';
            })
            .catch(function(error) {
                console.error('Erreur lors de la requête:', error);
                document.getElementById('totalPriceError').textContent = 
                    'Erreur de connexion: ' + error.message;
                loadingIndicator.style.display = 'none';
            });
        }

        function hideAllErrors() {
            var errors = document.querySelectorAll('.price-error');
            errors.forEach(function(error) {
                error.textContent = '';
            });
        }

        function resetPrices() {
            <% for (CategorieAge categorie : categoriesAge) { %>
                document.getElementById('price_<%= categorie.getIdCategorieAge() %>').textContent = 'Prix: 0,00 €';
            <% } %>
            document.getElementById('totalPriceDisplay').textContent = 'Prix Total: 0,00 €';
            document.getElementById('prixTotal').value = '0';
            hideAllErrors();
        }

        function updatePrices(data) {
            // Réinitialiser tous les prix
            <% for (CategorieAge categorie : categoriesAge) { %>
                document.getElementById('price_<%= categorie.getIdCategorieAge() %>').textContent = 'Prix: 0,00 €';
            <% } %>
            
            // Mettre à jour les prix par catégorie
            if (data.details && Array.isArray(data.details)) {
                data.details.forEach(function(detail) {
                    var priceElement = document.getElementById('price_' + detail.categorieAgeId);
                    if (priceElement) {
                        priceElement.textContent = 'Prix: ' + formatPrice(detail.prixTotal || 0);
                    }
                });
            }
            
            // Mettre à jour le prix total
            var totalPrice = parseFloat(data.prixTotal) || 0;
            document.getElementById('totalPriceDisplay').textContent = 'Prix Total: ' + formatPrice(totalPrice);
            document.getElementById('prixTotal').value = totalPrice;
        }

        function formatPrice(price) {
            return parseFloat(price).toFixed(2).replace('.', ',') + ' €';
        }

        // Initialiser le calcul du prix si un vol est déjà sélectionné
        document.addEventListener('DOMContentLoaded', function() {
            // Ajouter les événements sur tous les champs de quantité
            <% for (CategorieAge categorie : categoriesAge) { %>
                document.getElementById('siegeBusiness_<%= categorie.getIdCategorieAge() %>').addEventListener('input', updatePriceCalculation);
                document.getElementById('siegeEco_<%= categorie.getIdCategorieAge() %>').addEventListener('input', updatePriceCalculation);
            <% } %>
            
            if (document.getElementById('idVol').value) {
                updatePriceCalculation();
            }
        });

        // Validation du formulaire
        document.getElementById('reservationForm').addEventListener('submit', function(e) {
            var totalSeats = 0;
            
            <% for (CategorieAge categorie : categoriesAge) { %>
                totalSeats += parseInt(document.getElementById('siegeBusiness_<%= categorie.getIdCategorieAge() %>').value) || 0;
                totalSeats += parseInt(document.getElementById('siegeEco_<%= categorie.getIdCategorieAge() %>').value) || 0;
            <% } %>
            
            var idVol = document.getElementById('idVol').value;
            var idUser = document.getElementById('idUser').value;
            var prixTotal = parseFloat(document.getElementById('prixTotal').value) || 0;
            
            if (!idVol || !idUser) {
                e.preventDefault();
                alert('Veuillez sélectionner un vol et un client.');
                return false;
            }
            
            if (totalSeats <= 0) {
                e.preventDefault();
                alert('Veuillez sélectionner au moins un siège.');
                return false;
            }
            
            // Avertissement si le prix est 0 mais permet la soumission
            if (prixTotal === 0) {
                var confirmSubmit = confirm('Le prix total est de 0 €. Cela peut indiquer un problème de configuration. Souhaitez-vous tout de même continuer?');
                if (!confirmSubmit) {
                    e.preventDefault();
                    return false;
                }
            }
            
            return true;
        });
    </script>
</body>
</html>