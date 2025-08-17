package service;

import model.User;
import util.DatabaseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserService {

    public static void testConnection() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/login_db",
                "postgres",
                "antema")) {
            System.out.println("✅ Connexion directe réussie !");

            // Test requête SQL brute
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            System.out.println("Nombre d'utilisateurs : " + rs.getInt(1));
        } catch (SQLException e) {
            System.out.println("❌ Échec de connexion :");
            e.printStackTrace();
        }
    }

    public User authenticate(String username, String password) {
        System.out.println("=== Début authenticate ===");
        System.out.println("username = " + username);
        System.out.println("password = " + password);

        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            // 1. D'abord le test COUNT
            System.out.println("Test simple COUNT...");
            Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            System.out.println("Nombre d'utilisateurs en base : " + count);

            // 2. Ensuite la requête d'authentification
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                    User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            System.out.println("Exécution de la requête...");
            User user = query.getSingleResult();
            System.out.println("Utilisateur trouvé : " + user);
            return user;
        } catch (NoResultException e) {
            System.out.println("Aucun utilisateur trouvé avec ces identifiants !");
            return null;
        } catch (Exception e) {
            System.out.println("Exception inattendue dans authenticate :");
            e.printStackTrace();
            return null;
        } finally {
            em.close(); // On ferme SEULEMENT ici à la fin
            System.out.println("EntityManager fermé");
        }
    }

    public boolean register(User user) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Vérifier si l'utilisateur existe déjà
            if (userExists(user.getUsername(), user.getEmail())) {
                return false;
            }

            // Hasher le mot de passe
            user.setPassword(hashPassword(user.getPassword()));

            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    private boolean userExists(String username, String email) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :username OR u.email = :email",
                    Long.class);
            query.setParameter("username", username);
            query.setParameter("email", email);

            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

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