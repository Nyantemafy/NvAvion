package service;

import model.*;
import util.DatabaseUtil;
import util.DatabaseUtil.QueryResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PromotionService {

    /**
     * Trouver toutes les promotions
     */
    public List<Promotion> findAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT p.id_promotion, p.nom, p.date_debut, p.date_fin, " +
                    "p.reduction_pourcentage_, p.id_vol, p.id_categorie_age, " +
                    "c.nom as categorie_nom, c.age_min, c.age_max, c.multiplicateur_prix " +
                    "FROM promotion p " +
                    "LEFT JOIN categorie_age c ON p.id_categorie_age = c.id_categorie_age";

            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                Promotion promotion = new Promotion();
                promotion.setIdPromotion(queryResult.resultSet.getLong("id_promotion"));
                promotion.setNom(queryResult.resultSet.getString("nom"));
                promotion.setDateDebut(queryResult.resultSet.getDate("date_debut").toLocalDate());
                promotion.setDateFin(queryResult.resultSet.getDate("date_fin").toLocalDate());
                promotion.setReductionPourcentage(queryResult.resultSet.getInt("reduction_pourcentage_"));
                promotion.setIdVol(queryResult.resultSet.getLong("id_vol"));

                if (queryResult.resultSet.getObject("id_categorie_age") != null) {
                    CategorieAge categorie = new CategorieAge();
                    categorie.setIdCategorieAge(queryResult.resultSet.getInt("id_categorie_age"));
                    categorie.setNom(queryResult.resultSet.getString("categorie_nom"));
                    categorie.setAgeMin(queryResult.resultSet.getInt("age_min"));
                    categorie.setAgeMax(queryResult.resultSet.getObject("age_max", Integer.class));
                    categorie.setMultiplicateurPrix(queryResult.resultSet.getBigDecimal("multiplicateur_prix"));
                    promotion.setCategorieAge(categorie);
                }

                promotions.add(promotion);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de toutes les promotions:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return promotions;
    }

    public List<Promotion> findPromotionsByVolId(Long idVol) {
        List<Promotion> promotions = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT p.id_promotion, p.nom, p.date_debut, p.date_fin, " +
                    "p.reduction_pourcentage_, p.id_vol, " +
                    "c.id_categorie_age, c.nom as categorie_nom, " +
                    "c.age_min, c.age_max, c.multiplicateur_prix " +
                    "FROM promotion p " +
                    "LEFT JOIN categorie_age c ON p.id_categorie_age = c.id_categorie_age " +
                    "WHERE p.id_vol = ? ORDER BY p.date_debut DESC";

            queryResult = DatabaseUtil.executeQuery(query, idVol);

            while (queryResult.resultSet.next()) {
                Promotion promotion = new Promotion();
                promotion.setIdPromotion(queryResult.resultSet.getLong("id_promotion"));
                promotion.setNom(queryResult.resultSet.getString("nom"));
                promotion.setDateDebut(queryResult.resultSet.getDate("date_debut").toLocalDate());
                promotion.setDateFin(queryResult.resultSet.getDate("date_fin").toLocalDate());
                promotion.setReductionPourcentage(queryResult.resultSet.getInt("reduction_pourcentage_"));
                promotion.setIdVol(queryResult.resultSet.getLong("id_vol"));

                // Informations sur la catégorie d'âge
                if (queryResult.resultSet.getObject("id_categorie_age") != null) {
                    CategorieAge categorie = new CategorieAge();
                    categorie.setIdCategorieAge(queryResult.resultSet.getInt("id_categorie_age"));
                    categorie.setNom(queryResult.resultSet.getString("categorie_nom"));
                    categorie.setAgeMin(queryResult.resultSet.getInt("age_min"));
                    categorie.setAgeMax(queryResult.resultSet.getObject("age_max", Integer.class));
                    categorie.setMultiplicateurPrix(queryResult.resultSet.getBigDecimal("multiplicateur_prix"));
                    promotion.setCategorieAge(categorie);
                }

                promotions.add(promotion);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des promotions:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return promotions;
    }

    /**
     * Créer une nouvelle promotion
     */
    public boolean createPromotion(Promotion promotion) {
        if (promotion == null || promotion.getIdVol() == null) {
            System.out.println("❌ Données de promotion invalides");
            return false;
        }

        try {
            String query = "INSERT INTO promotion (nom, date_debut, date_fin, reduction_pourcentage_, id_vol, id_categorie_age) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            Object[] params = {
                    promotion.getNom(),
                    java.sql.Date.valueOf(promotion.getDateDebut()),
                    java.sql.Date.valueOf(promotion.getDateFin()),
                    promotion.getReductionPourcentage(),
                    promotion.getIdVol(),
                    promotion.getCategorieAge() != null ? promotion.getCategorieAge().getIdCategorieAge() : null
            };

            long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(query, params);

            if (generatedId > 0) {
                promotion.setIdPromotion(generatedId);
                System.out.println("✅ Promotion créée avec succès (ID: " + generatedId + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la création de la promotion:");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mettre à jour une promotion
     */
    public boolean updatePromotion(Promotion promotion) {
        if (promotion == null || promotion.getIdPromotion() == null) {
            System.out.println("❌ Promotion ou ID invalide");
            return false;
        }

        try {
            String query = "UPDATE promotion SET nom = ?, date_debut = ?, date_fin = ?, " +
                    "reduction_pourcentage_ = ?, id_categorie_age = ? WHERE id_promotion = ?";

            int rowsAffected = DatabaseUtil.executeUpdate(
                    query,
                    promotion.getNom(),
                    java.sql.Date.valueOf(promotion.getDateDebut()),
                    java.sql.Date.valueOf(promotion.getDateFin()),
                    promotion.getReductionPourcentage(),
                    promotion.getCategorieAge() != null ? promotion.getCategorieAge().getIdCategorieAge() : null,
                    promotion.getIdPromotion());

            if (rowsAffected > 0) {
                System.out.println("✅ Promotion mise à jour avec succès");
                return true;
            } else {
                System.out.println("❌ Aucune promotion trouvée avec l'ID " + promotion.getIdPromotion());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la promotion:");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Supprimer une promotion
     */
    public boolean deletePromotion(Long idPromotion) {
        if (idPromotion == null) {
            System.out.println("❌ ID de promotion invalide");
            return false;
        }

        try {
            String query = "DELETE FROM promotion WHERE id_promotion = ?";
            int rowsAffected = DatabaseUtil.executeUpdate(query, idPromotion);

            if (rowsAffected > 0) {
                System.out.println("✅ Promotion supprimée avec succès");
                return true;
            } else {
                System.out.println("❌ Aucune promotion trouvée avec l'ID " + idPromotion);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la promotion:");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Trouver une promotion par ID
     */
    public Promotion findPromotionById(Long idPromotion) {
        if (idPromotion == null)
            return null;

        QueryResult queryResult = null;
        try {
            String query = "SELECT p.id_promotion, p.nom, p.date_debut, p.date_fin, " +
                    "p.reduction_pourcentage_, p.id_vol, p.id_categorie_age, " +
                    "c.nom as categorie_nom, c.age_min, c.age_max, c.multiplicateur_prix " +
                    "FROM promotion p " +
                    "LEFT JOIN categorie_age c ON p.id_categorie_age = c.id_categorie_age " +
                    "WHERE p.id_promotion = ?";

            queryResult = DatabaseUtil.executeQuery(query, idPromotion);

            if (queryResult.resultSet.next()) {
                Promotion promotion = new Promotion();
                promotion.setIdPromotion(queryResult.resultSet.getLong("id_promotion"));
                promotion.setNom(queryResult.resultSet.getString("nom"));
                promotion.setDateDebut(queryResult.resultSet.getDate("date_debut").toLocalDate());
                promotion.setDateFin(queryResult.resultSet.getDate("date_fin").toLocalDate());
                promotion.setReductionPourcentage(queryResult.resultSet.getInt("reduction_pourcentage_"));
                promotion.setIdVol(queryResult.resultSet.getLong("id_vol"));

                if (queryResult.resultSet.getObject("id_categorie_age") != null) {
                    CategorieAge categorie = new CategorieAge();
                    categorie.setIdCategorieAge(queryResult.resultSet.getInt("id_categorie_age"));
                    categorie.setNom(queryResult.resultSet.getString("categorie_nom"));
                    categorie.setAgeMin(queryResult.resultSet.getInt("age_min"));
                    categorie.setAgeMax(queryResult.resultSet.getObject("age_max", Integer.class));
                    categorie.setMultiplicateurPrix(queryResult.resultSet.getBigDecimal("multiplicateur_prix"));
                    promotion.setCategorieAge(categorie);
                }

                return promotion;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de promotion par ID:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return null;
    }
}
