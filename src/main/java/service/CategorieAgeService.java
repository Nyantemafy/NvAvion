package service;

import model.CategorieAge;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategorieAgeService {

    /**
     * Récupère toutes les catégories d'âge actives
     */
    public List<CategorieAge> findAllCategories() {
        List<CategorieAge> categories = new ArrayList<>();

        String query = "SELECT id_categorie_age, nom, age_min, age_max, multiplicateur_prix, description, is_active, created_at "
                +
                "FROM categorie_age " +
                "WHERE is_active = TRUE " +
                "ORDER BY age_min ASC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategorieAge categorie = new CategorieAge();
                categorie.setIdCategorieAge(rs.getInt("id_categorie_age"));
                categorie.setNom(rs.getString("nom"));
                categorie.setAgeMin(rs.getInt("age_min"));
                categorie.setAgeMax(rs.getObject("age_max") != null ? rs.getInt("age_max") : null);
                categorie.setMultiplicateurPrix(rs.getBigDecimal("multiplicateur_prix"));
                categorie.setDescription(rs.getString("description"));
                categorie.setIsActive(rs.getBoolean("is_active"));
                categorie.setCreatedAt(rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime()
                        : LocalDateTime.now());

                categories.add(categorie);
            }

            System.out.println("✅ Catégories d'âge récupérées: " + categories.size());

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des catégories d'âge:");
            e.printStackTrace();
        }

        return categories;
    }
}
