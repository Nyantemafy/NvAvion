<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/gh/Nyantemafy/aero-css@master/style.css">
</head>
<body>
    <div class="login-container">
        <h2>Connexion</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <form method="post" action="processLogin">
            <div class="form-group">
                <label>Nom d'utilisateur:</label>
                <input type="text" name="user.username" required>
            </div>
            
            <div class="form-group">
                <label>Mot de passe:</label>
                <input type="password" name="user.password" required>
            </div>
            
            <button type="submit">Se connecter</button>
        </form>
        
        <p><a href="register">Créer un compte</a></p>
    </div>
</body>
</html>