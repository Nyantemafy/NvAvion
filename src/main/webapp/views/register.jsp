<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription</title>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/style.css">
</head>
<body>
    <div class="login-container">
        <h2>Créer un compte</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"> <%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"> <%= request.getAttribute("success") %></div>
        <% } %>
        
        <% 
            User user = (User) request.getAttribute("user");
            String username = (user != null && user.getUsername() != null) ? user.getUsername() : "";
            String email = (user != null && user.getEmail() != null) ? user.getEmail() : "";
        %>
        
        <form method="post" action="processRegister">
            <div class="form-group">
                <label for="username">Nom d'utilisateur *</label>
                <input type="text" 
                       id="username" 
                       name="user.username" 
                       value="<%= username %>"
                       placeholder="Choisissez un nom d'utilisateur"
                       required>
            </div>
            
            <div class="form-group">
                <label for="email">Adresse email *</label>
                <input type="email" 
                       id="email" 
                       name="user.email" 
                       value="<%= email %>"
                       placeholder="votre@email.com"
                       required>
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe *</label>
                <input type="password" 
                       id="password" 
                       name="user.password" 
                       placeholder="Choisissez un mot de passe sécurisé"
                       required>
            </div>
            
            <div class="form-group">
                <label for="role">Rôle</label>
                <select id="role" name="user.role">
                    <option value="USER">Utilisateur</option>
                    <option value="MODERATOR">Modérateur</option>
                </select>
            </div>
            
            <button type="submit">S'inscrire</button>
        </form>
        
        <p>
            Déjà un compte ? 
            <a href="login">Se connecter</a>
        </p>
    </div>

    <script>
        // Validation côté client simple
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;

            if (password.length < 6) {
                alert('Le mot de passe doit contenir au moins 6 caractères');
                e.preventDefault();
                return false;
            }

            if (username.length < 3) {
                alert('Le nom d\'utilisateur doit contenir au moins 3 caractères');
                e.preventDefault();
                return false;
            }

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                alert('Veuillez saisir une adresse email valide');
                e.preventDefault();
                return false;
            }
        });
    </script>
</body>
</html>