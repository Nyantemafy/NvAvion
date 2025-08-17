<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <% User user = (User) request.getAttribute("user"); %>
        <h1>Bienvenue, <%= user.getUsername() %>!</h1>
        
        <div class="user-info">
            <p>Email: <%= user.getEmail() %></p>
            <p>Rôle: <%= user.getRole() %></p>
        </div>
        
        <a href="logout" class="logout-btn">Déconnexion</a>
    </div>
</body>
</html>