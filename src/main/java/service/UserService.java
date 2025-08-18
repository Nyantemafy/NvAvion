package service;

import model.User;
import util.DatabaseUtil;
import util.DatabaseUtil.QueryResult;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class UserService {

    /**
     * Tester la connexion à la base de données
     */
    public static void testConnection() {
        DatabaseUtil.testConnection();
    }

    /**
     * Authentifier un utilisateur
     * 
     * @param username - Le nom d'utilisateur
     * @param password - Le mot de passe (sera hashé automatiquement si nécessaire)
     * @return User - L'utilisateur authentifié, ou null si échec
     */
    public User authenticate(String username, String password) {
        System.out.println("=== Début authenticate ===");
        System.out.println("username = " + username);
        System.out.println("password = " + (password != null ? "[MASQUÉ]" : "null"));

        if (username == null || password == null) {
            System.out.println("❌ Username ou password null");
            return null;
        }

        // Hash du mot de passe si nécessaire
        String hashedPassword = password;
        if (!password.matches("[a-f0-9]{64}")) {
            // Si ce n'est pas déjà un hash SHA-256
            hashedPassword = hashPassword(password);
            System.out.println("✅ Mot de passe hashé");
        }

        QueryResult queryResult = null;
        try {
            String query = "SELECT id, username, email, password, role, created_at, updated_at, is_active " +
                    "FROM users WHERE username = ? AND password = ? AND is_active = true";

            queryResult = DatabaseUtil.executeQuery(query, username, hashedPassword);

            if (queryResult.resultSet.next()) {
                User user = mapResultSetToUser(queryResult.resultSet);
                System.out.println("✅ Utilisateur authentifié: " + user.getUsername() + " (ID: " + user.getId() + ")");
                return user;
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec ces identifiants");

                // Debug: vérifier si l'utilisateur existe sans le mot de passe
                debugUserExists(username);

                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'authentification:");
            e.printStackTrace();
            return null;
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    /**
     * Inscrire un nouvel utilisateur
     * 
     * @param user - L'utilisateur à inscrire
     * @return boolean - true si réussi, false sinon
     */
    public boolean register(User user) {
        System.out.println("=== Début register ===");
        System.out.println("username = " + user.getUsername());
        System.out.println("email = " + user.getEmail());

        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            System.out.println("❌ Champs obligatoires manquants");
            return false;
        }

        try {
            // Vérifier si l'utilisateur existe déjà
            if (userExists(user.getUsername(), user.getEmail())) {
                System.out.println("❌ Utilisateur déjà existant");
                return false;
            }

            // Hash du mot de passe
            String hashedPassword = hashPassword(user.getPassword());

            // Insertion
            String query = "INSERT INTO users (username, email, password, role, is_active) VALUES (?, ?, ?, ?, ?)";
            long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(
                    query,
                    user.getUsername(),
                    user.getEmail(),
                    hashedPassword,
                    user.getRole() != null ? user.getRole() : "USER",
                    true);

            if (generatedId > 0) {
                user.setId(generatedId);
                System.out.println("✅ Utilisateur inscrit avec succès (ID: " + generatedId + ")");
                return true;
            } else {
                System.out.println("❌ Échec de l'insertion");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'inscription:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifier si un utilisateur existe déjà
     * 
     * @param username - Le nom d'utilisateur
     * @param email    - L'email
     * @return boolean - true si existe, false sinon
     */
    private boolean userExists(String username, String email) {
        QueryResult queryResult = null;
        try {
            String query = "SELECT COUNT(*) as count FROM users WHERE username = ? OR email = ?";
            queryResult = DatabaseUtil.executeQuery(query, username, email);

            if (queryResult.resultSet.next()) {
                int count = queryResult.resultSet.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification d'existence:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return false;
    }

    /**
     * Trouver un utilisateur par ID
     * 
     * @param userId - L'ID de l'utilisateur
     * @return User - L'utilisateur trouvé, ou null
     */
    public User findById(Long userId) {
        if (userId == null)
            return null;

        QueryResult queryResult = null;
        try {
            String query = "SELECT id, username, email, password, role, created_at, updated_at, is_active " +
                    "FROM users WHERE id = ? AND is_active = true";

            queryResult = DatabaseUtil.executeQuery(query, userId);

            if (queryResult.resultSet.next()) {
                return mapResultSetToUser(queryResult.resultSet);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche par ID:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return null;
    }

    /**
     * Lister tous les utilisateurs (pour admin)
     * 
     * @return List<User> - Liste des utilisateurs
     */
    public java.util.List<User> findAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT id, username, email, password, role, created_at, updated_at, is_active " +
                    "FROM users ORDER BY created_at DESC";

            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                users.add(mapResultSetToUser(queryResult.resultSet));
            }

            System.out.println("✅ " + users.size() + " utilisateurs trouvés");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return users;
    }

    /**
     * Mapper un ResultSet vers un objet User
     * 
     * @param rs - Le ResultSet
     * @return User - L'objet User mappé
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));

        // Note: created_at et updated_at sont gérés automatiquement par PostgreSQL
        // Si vous avez besoin de les récupérer, décommentez les lignes suivantes:
        // Timestamp createdAt = rs.getTimestamp("created_at");
        // if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());

        return user;
    }

    /**
     * Debug: vérifier si un utilisateur existe (sans mot de passe)
     * 
     * @param username - Le nom d'utilisateur
     */
    private void debugUserExists(String username) {
        QueryResult queryResult = null;
        try {
            String query = "SELECT username, role FROM users WHERE username = ?";
            queryResult = DatabaseUtil.executeQuery(query, username);

            if (queryResult.resultSet.next()) {
                String foundUsername = queryResult.resultSet.getString("username");
                String foundRole = queryResult.resultSet.getString("role");
                System.out.println("🔍 Debug: Utilisateur '" + foundUsername + "' existe (rôle: " + foundRole
                        + ") mais mot de passe incorrect");
            } else {
                System.out.println("🔍 Debug: Utilisateur '" + username + "' n'existe pas du tout");
            }

        } catch (SQLException e) {
            System.out.println("🔍 Debug: Erreur lors de la vérification");
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    /**
     * Hasher un mot de passe avec SHA-256
     * 
     * @param password - Le mot de passe en clair
     * @return String - Le hash SHA-256 en hexadécimal
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
}