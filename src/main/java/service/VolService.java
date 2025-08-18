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

public class VolService {

    /**
     * Rechercher des vols avec filtres multicritères
     */
    public List<Vol> searchVolsWithFilters(VolFilter filter) {
        List<Vol> vols = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            StringBuilder queryBuilder = new StringBuilder();
            List<Object> params = new ArrayList<>();

            // Requête de base avec jointures pour récupérer toutes les infos
            queryBuilder.append("SELECT DISTINCT v.id_vol, v.numero_vol_, v.date_vol_, v.id_ville, v.id_avion, ");
            queryBuilder.append("vi.nom as ville_destination, a.pseudo as avion_pseudo, ");
            queryBuilder.append("MIN(psv.prix_) as prix_min, MAX(psv.prix_) as prix_max, ");
            queryBuilder.append("p.nom as promotion_nom, p.reduction_pourcentage_ ");
            queryBuilder.append("FROM vol v ");
            queryBuilder.append("LEFT JOIN ville vi ON v.id_ville = vi.id_ville ");
            queryBuilder.append("LEFT JOIN avion a ON v.id_avion = a.id_avion ");
            queryBuilder.append("LEFT JOIN prix_siege_vol_ psv ON v.id_vol = psv.id_vol ");
            queryBuilder.append("LEFT JOIN type_siege ts ON psv.id_type_siege = ts.id_type_siege ");
            queryBuilder.append("LEFT JOIN promotion p ON v.id_vol = p.id_vol ");
            queryBuilder.append("WHERE 1=1 ");

            // Filtre par promotion
            if (filter.getPromotionNom() != null && !filter.getPromotionNom().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(p.nom) LIKE LOWER(?) ");
                params.add("%" + filter.getPromotionNom().trim() + "%");
            }

            // Filtre par ville de destination
            if (filter.getVilleDestination() != null && !filter.getVilleDestination().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(vi.nom) LIKE LOWER(?) ");
                params.add("%" + filter.getVilleDestination().trim() + "%");
            }

            // Filtre par numéro de vol
            if (filter.getNumeroVol() != null && !filter.getNumeroVol().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(v.numero_vol_) LIKE LOWER(?) ");
                params.add("%" + filter.getNumeroVol().trim() + "%");
            }

            // Filtre par type de siège
            if (filter.getTypeSiege() != null && !filter.getTypeSiege().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(ts.rubrique) LIKE LOWER(?) ");
                params.add("%" + filter.getTypeSiege().trim() + "%");
            }

            // Filtre par date de vol
            if (filter.getDateVolDebut() != null) {
                queryBuilder.append("AND v.date_vol_ >= ? ");
                params.add(Timestamp.valueOf(filter.getDateVolDebut().atStartOfDay()));
            }

            if (filter.getDateVolFin() != null) {
                queryBuilder.append("AND v.date_vol_ <= ? ");
                params.add(Timestamp.valueOf(filter.getDateVolFin().atTime(23, 59, 59)));
            }

            // Grouper et ordonner
            queryBuilder.append("GROUP BY v.id_vol, v.numero_vol_, v.date_vol_, v.id_ville, v.id_avion, ");
            queryBuilder.append("vi.nom, a.pseudo, p.nom, p.reduction_pourcentage_ ");

            // Filtre par prix (HAVING car c'est sur une fonction d'agrégation)
            if (filter.getPrixMin() != null) {
                queryBuilder.append("HAVING MIN(psv.prix_) >= ? ");
                params.add(filter.getPrixMin());
            }

            if (filter.getPrixMax() != null) {
                if (filter.getPrixMin() != null) {
                    queryBuilder.append("AND MAX(psv.prix_) <= ? ");
                } else {
                    queryBuilder.append("HAVING MAX(psv.prix_) <= ? ");
                }
                params.add(filter.getPrixMax());
            }

            queryBuilder.append("ORDER BY v.date_vol_ DESC");

            System.out.println("🔍 Requête de recherche: " + queryBuilder.toString());
            System.out.println("🔍 Paramètres: " + params);

            queryResult = DatabaseUtil.executeQuery(queryBuilder.toString(), params.toArray());

            while (queryResult.resultSet.next()) {
                Vol vol = new Vol();
                vol.setIdVol(queryResult.resultSet.getLong("id_vol"));
                vol.setNumeroVol(queryResult.resultSet.getString("numero_vol_"));
                vol.setDateVol(queryResult.resultSet.getTimestamp("date_vol_").toLocalDateTime());
                vol.setIdVille(queryResult.resultSet.getLong("id_ville"));
                vol.setIdAvion(queryResult.resultSet.getLong("id_avion"));
                vol.setNomVilleDestination(queryResult.resultSet.getString("ville_destination"));
                vol.setPseudoAvion(queryResult.resultSet.getString("avion_pseudo"));

                BigDecimal prixMin = queryResult.resultSet.getBigDecimal("prix_min");
                BigDecimal prixMax = queryResult.resultSet.getBigDecimal("prix_max");
                vol.setPrixMin(prixMin);
                vol.setPrixMax(prixMax);

                vol.setPromotionNom(queryResult.resultSet.getString("promotion_nom"));
                Integer reduction = queryResult.resultSet.getObject("reduction_pourcentage_", Integer.class);
                vol.setPromotionReduction(reduction);

                vols.add(vol);
            }

            System.out.println("✅ " + vols.size() + " vols trouvés avec les filtres");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de vols:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return vols;
    }

    /**
     * Récupérer tous les vols (sans filtre)
     */
    public List<Vol> findAllVols() {
        VolFilter emptyFilter = new VolFilter();
        return searchVolsWithFilters(emptyFilter);
    }

    /**
     * Trouver un vol par ID
     */
    public Vol findVolById(Long idVol) {
        if (idVol == null)
            return null;

        QueryResult queryResult = null;
        try {
            String query = "SELECT v.id_vol, v.numero_vol_, v.date_vol_, v.id_ville, v.id_avion, " +
                    "vi.nom as ville_destination, a.pseudo as avion_pseudo " +
                    "FROM vol v " +
                    "LEFT JOIN ville vi ON v.id_ville = vi.id_ville " +
                    "LEFT JOIN avion a ON v.id_avion = a.id_avion " +
                    "WHERE v.id_vol = ?";

            queryResult = DatabaseUtil.executeQuery(query, idVol);

            if (queryResult.resultSet.next()) {
                Vol vol = new Vol();
                vol.setIdVol(queryResult.resultSet.getLong("id_vol"));
                vol.setNumeroVol(queryResult.resultSet.getString("numero_vol_"));
                vol.setDateVol(queryResult.resultSet.getTimestamp("date_vol_").toLocalDateTime());
                vol.setIdVille(queryResult.resultSet.getLong("id_ville"));
                vol.setIdAvion(queryResult.resultSet.getLong("id_avion"));
                vol.setNomVilleDestination(queryResult.resultSet.getString("ville_destination"));
                vol.setPseudoAvion(queryResult.resultSet.getString("avion_pseudo"));

                return vol;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du vol par ID:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return null;
    }

    /**
     * Créer un nouveau vol
     */
    public boolean createVol(Vol vol) {
        if (vol == null || vol.getNumeroVol() == null || vol.getDateVol() == null
                || vol.getIdVille() == null) {
            System.out.println("❌ Données de vol invalides: " + vol);
            return false;
        }

        try {
            String query = "INSERT INTO vol (numero_vol_, date_vol_, id_ville, id_avion) VALUES (?, ?, ?, ?)";
            Object[] params = {
                    vol.getNumeroVol(),
                    Timestamp.valueOf(vol.getDateVol()),
                    vol.getIdVille(),
                    vol.getIdAvion() != null ? vol.getIdAvion() : null
            };

            long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(query, params);

            if (generatedId > 0) {
                vol.setIdVol(generatedId);
                System.out.println("✅ Vol créé avec succès (ID: " + generatedId + ")");

                // ➕ Insérer aussi les prix dans prix_siege_vol_
                createOrUpdatePrixSiegeVol(generatedId, vol.getPrixMin(), vol.getPrixMax());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la création du vol:");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mettre à jour un vol
     */
    public boolean updateVol(Vol vol) {
        if (vol == null || vol.getIdVol() == null) {
            System.out.println("❌ Vol ou ID invalide");
            return false;
        }

        try {
            String query = "UPDATE vol SET numero_vol_ = ?, date_vol_ = ?, id_ville = ?, id_avion = ? WHERE id_vol = ?";
            int rowsAffected = DatabaseUtil.executeUpdate(
                    query,
                    vol.getNumeroVol(),
                    Timestamp.valueOf(vol.getDateVol()),
                    vol.getIdVille(),
                    vol.getIdAvion(),
                    vol.getIdVol());

            if (rowsAffected > 0) {
                System.out.println("✅ Vol mis à jour avec succès");

                // ➕ Mettre à jour aussi les prix associés
                createOrUpdatePrixSiegeVol(vol.getIdVol(), vol.getPrixMin(), vol.getPrixMax());
                return true;
            } else {
                System.out.println("❌ Aucun vol trouvé avec l'ID " + vol.getIdVol());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du vol:");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Insère ou met à jour les prix dans prix_siege_vol_
     */
    private void createOrUpdatePrixSiegeVol(Long idVol, BigDecimal prixMin, BigDecimal prixMax) throws SQLException {
        if (idVol == null)
            return;

        // Supposons que Id_type_siege = 1 (Éco) et 2 (Business)
        if (prixMin != null) {
            upsertPrixSiege(idVol, 1, prixMin);
        }
        if (prixMax != null) {
            upsertPrixSiege(idVol, 2, prixMax);
        }
    }

    /**
     * Met à jour si déjà existant, sinon insère
     */
    private void upsertPrixSiege(Long idVol, int idTypeSiege, BigDecimal prix) throws SQLException {
        // Vérifier si une ligne existe déjà
        String checkQuery = "SELECT COUNT(*) FROM prix_siege_vol_ WHERE id_vol = ? AND id_type_siege = ?";
        long count = DatabaseUtil.executeCount(checkQuery, idVol, idTypeSiege);

        if (count > 0) {
            // Update
            String updateQuery = "UPDATE prix_siege_vol_ SET prix_ = ? WHERE id_vol = ? AND id_type_siege = ?";
            DatabaseUtil.executeUpdate(updateQuery, prix, idVol, idTypeSiege);
            System.out.println("🔄 Prix mis à jour (Vol " + idVol + ", Type " + idTypeSiege + ")");
        } else {
            // Insert
            String insertQuery = "INSERT INTO prix_siege_vol_ (prix_, id_type_siege, id_vol) VALUES (?, ?, ?)";
            DatabaseUtil.executeUpdate(insertQuery, prix, idTypeSiege, idVol);
            System.out.println("➕ Prix inséré (Vol " + idVol + ", Type " + idTypeSiege + ")");
        }
    }

    /**
     * Supprimer un vol
     */
    public boolean deleteVol(Long idVol) {
        if (idVol == null) {
            System.out.println("❌ ID de vol invalide");
            return false;
        }

        try {
            // D'abord, supprimer les dépendances (prix, promotions, réservations)
            DatabaseUtil.executeUpdate("DELETE FROM prix_siege_vol_ WHERE id_vol = ?", idVol);
            DatabaseUtil.executeUpdate("DELETE FROM promotion WHERE id_vol = ?", idVol);
            // Note: Les réservations devraient peut-être être gérées différemment selon la
            // logique métier

            // Ensuite, supprimer le vol
            String query = "DELETE FROM vol WHERE id_vol = ?";
            int rowsAffected = DatabaseUtil.executeUpdate(query, idVol);

            if (rowsAffected > 0) {
                System.out.println("✅ Vol supprimé avec succès");
                return true;
            } else {
                System.out.println("❌ Aucun vol trouvé avec l'ID " + idVol);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du vol:");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Récupérer toutes les villes (pour les select)
     */
    public List<Ville> findAllVilles() {
        List<Ville> villes = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT id_ville, nom FROM ville ORDER BY nom";
            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                Ville ville = new Ville();
                ville.setIdVille(queryResult.resultSet.getLong("id_ville"));
                ville.setNom(queryResult.resultSet.getString("nom"));
                villes.add(ville);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des villes:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return villes;
    }

    /**
     * Récupérer tous les avions (pour les select)
     */
    public List<Avion> findAllAvions() {
        List<Avion> avions = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT id_avion, pseudo, siege_business, siege_eco, date_fabrication FROM avion ORDER BY pseudo";
            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                Avion avion = new Avion();
                avion.setIdAvion(queryResult.resultSet.getLong("id_avion"));
                avion.setPseudo(queryResult.resultSet.getString("pseudo"));
                avion.setSiegeBusiness(queryResult.resultSet.getInt("siege_business"));
                avion.setSiegeEco(queryResult.resultSet.getInt("siege_eco"));

                Date dateFab = queryResult.resultSet.getDate("date_fabrication");
                if (dateFab != null) {
                    avion.setDateFabrication(dateFab.toLocalDate());
                }

                avions.add(avion);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des avions:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return avions;
    }

    /**
     * Récupérer tous les types de siège (pour les filtres)
     */
    public List<TypeSiege> findAllTypeSieges() {
        List<TypeSiege> types = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT id_type_siege, rubrique, prix_ FROM type_siege ORDER BY rubrique";
            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                TypeSiege type = new TypeSiege();
                type.setIdTypeSiege(queryResult.resultSet.getLong("id_type_siege"));
                type.setRubrique(queryResult.resultSet.getString("rubrique"));
                type.setPrix(queryResult.resultSet.getBigDecimal("prix_"));
                types.add(type);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des types de siège:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return types;
    }

    /**
     * Récupérer toutes les promotions actives (pour les filtres)
     */
    public List<Promotion> findActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = "SELECT DISTINCT nom FROM promotion WHERE date_fin >= CURRENT_DATE ORDER BY nom";
            queryResult = DatabaseUtil.executeQuery(query);

            while (queryResult.resultSet.next()) {
                Promotion promo = new Promotion();
                promo.setNom(queryResult.resultSet.getString("nom"));
                promotions.add(promo);
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
}
