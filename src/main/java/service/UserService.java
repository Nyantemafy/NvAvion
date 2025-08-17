package service;

import model.User;
import util.DatabaseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {

    public User authenticate(String username, String password) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            String hashedPassword = hashPassword(password);
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                    User.class);
            query.setParameter("username", username);
            query.setParameter("password", hashedPassword);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
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