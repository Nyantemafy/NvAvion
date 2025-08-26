package service;

import model.*;
import util.DatabaseUtil;
import util.DatabaseUtil.QueryResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import java.util.Iterator;

public class ReservationService {

    /**
     * Rechercher des réservations avec filtres multicritères
     */
    public List<Reservation> searchReservationsWithFilters(ReservationFilter filter) {
        List<Reservation> reservations = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            StringBuilder queryBuilder = new StringBuilder();
            List<Object> params = new ArrayList<>();

            // Requête de base avec jointures pour récupérer toutes les infos
            queryBuilder.append("SELECT r.id_reservation, r.date_reservation_, r.prix_total_, ");
            queryBuilder.append("r.id_vol, r.id_user, r.siege_business, r.siege_eco, ");
            queryBuilder.append("v.numero_vol_, v.date_vol_, ");
            queryBuilder.append("vi.nom as ville_destination, ");
            queryBuilder.append("u.username as username_user, ");
            queryBuilder.append("a.pseudo as avion_pseudo ");
            queryBuilder.append("FROM reservation r ");
            queryBuilder.append("LEFT JOIN vol v ON r.id_vol = v.id_vol ");
            queryBuilder.append("LEFT JOIN ville vi ON v.id_ville = vi.id_ville ");
            queryBuilder.append("LEFT JOIN users u ON r.id_user = u.id ");
            queryBuilder.append("LEFT JOIN avion a ON v.id_avion = a.id_avion ");
            queryBuilder.append("WHERE 1=1 ");

            // Filtres
            if (filter != null) {
                if (filter.getNumeroVol() != null && !filter.getNumeroVol().trim().isEmpty()) {
                    queryBuilder.append("AND LOWER(v.numero_vol_) LIKE LOWER(?) ");
                    params.add("%" + filter.getNumeroVol().trim() + "%");
                }
                if (filter.getVilleDestination() != null && !filter.getVilleDestination().trim().isEmpty()) {
                    queryBuilder.append("AND LOWER(vi.nom) LIKE LOWER(?) ");
                    params.add("%" + filter.getVilleDestination().trim() + "%");
                }
                if (filter.getUsername() != null && !filter.getUsername().trim().isEmpty()) {
                    queryBuilder.append("AND LOWER(u.username) LIKE LOWER(?) ");
                    params.add("%" + filter.getUsername().trim() + "%");
                }
                if (filter.getPrixMin() != null) {
                    queryBuilder.append("AND r.prix_total_ >= ? ");
                    params.add(filter.getPrixMin());
                }
                if (filter.getPrixMax() != null) {
                    queryBuilder.append("AND r.prix_total_ <= ? ");
                    params.add(filter.getPrixMax());
                }
                if (filter.getDateReservationDebut() != null) {
                    queryBuilder.append("AND r.date_reservation_ >= ? ");
                    params.add(Timestamp.valueOf(filter.getDateReservationDebut().atStartOfDay()));
                }
                if (filter.getDateReservationFin() != null) {
                    queryBuilder.append("AND r.date_reservation_ <= ? ");
                    params.add(Timestamp.valueOf(filter.getDateReservationFin().atTime(23, 59, 59)));
                }
                if (filter.getDateVolDebut() != null) {
                    queryBuilder.append("AND v.date_vol_ >= ? ");
                    params.add(Timestamp.valueOf(filter.getDateVolDebut().atStartOfDay()));
                }
                if (filter.getDateVolFin() != null) {
                    queryBuilder.append("AND v.date_vol_ <= ? ");
                    params.add(Timestamp.valueOf(filter.getDateVolFin().atTime(23, 59, 59)));
                }
                if (filter.getHasBusinessSeats() != null && filter.getHasBusinessSeats()) {
                    queryBuilder.append("AND r.siege_business > 0 ");
                }
                if (filter.getHasEcoSeats() != null && filter.getHasEcoSeats()) {
                    queryBuilder.append("AND r.siege_eco > 0 ");
                }
                if (filter.getMinSieges() != null) {
                    queryBuilder.append("AND (r.siege_business + r.siege_eco) >= ? ");
                    params.add(filter.getMinSieges());
                }
                if (filter.getMaxSieges() != null) {
                    queryBuilder.append("AND (r.siege_business + r.siege_eco) <= ? ");
                    params.add(filter.getMaxSieges());
                }
            }

            queryBuilder.append("ORDER BY r.date_reservation_ DESC");

            System.out.println("🔍 Requête de recherche réservations: " + queryBuilder.toString());
            System.out.println("🔍 Paramètres: " + params);

            queryResult = DatabaseUtil.executeQuery(queryBuilder.toString(), params.toArray());

            while (queryResult.resultSet.next()) {
                Reservation r = new Reservation();
                r.setIdReservation(queryResult.resultSet.getLong("id_reservation"));
                r.setDateReservation(queryResult.resultSet.getTimestamp("date_reservation_").toLocalDateTime());
                r.setPrixTotal(queryResult.resultSet.getBigDecimal("prix_total_"));
                r.setIdVol(queryResult.resultSet.getLong("id_vol"));
                r.setIdUser(queryResult.resultSet.getLong("id_user"));
                r.setSiegeBusiness(queryResult.resultSet.getInt("siege_business"));
                r.setSiegeEco(queryResult.resultSet.getInt("siege_eco"));

                r.setNumeroVol(queryResult.resultSet.getString("numero_vol_"));
                Timestamp tsVol = queryResult.resultSet.getTimestamp("date_vol_");
                if (tsVol != null)
                    r.setDateVol(tsVol.toLocalDateTime());
                r.setVilleDestination(queryResult.resultSet.getString("ville_destination"));
                r.setUsernameUser(queryResult.resultSet.getString("username_user"));
                r.setPseudoAvion(queryResult.resultSet.getString("avion_pseudo"));

                reservations.add(r);
            }

            System.out.println("✅ " + reservations.size() + " réservation(s) trouvée(s) avec les filtres");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de réservations:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return reservations;
    }

    /**
     * Récupérer toutes les réservations (sans filtre)
     */
    public List<Reservation> findAllReservations() {
        ReservationFilter emptyFilter = new ReservationFilter();
        return searchReservationsWithFilters(emptyFilter);
    }

    /**
     * Trouver une réservation par ID
     */
    public Reservation findReservationById(Long idReservation) {
        if (idReservation == null)
            return null;

        QueryResult queryResult = null;
        try {
            String query = "SELECT r.id_reservation, r.date_reservation_, r.prix_total_, " +
                    "r.id_vol, r.id_user, r.siege_business, r.siege_eco, " +
                    "v.numero_vol_, v.date_vol_, " +
                    "vi.nom as ville_destination, " +
                    "u.username as username_user, " +
                    "a.pseudo as avion_pseudo " +
                    "FROM reservation r " +
                    "LEFT JOIN vol v ON r.id_vol = v.id_vol " +
                    "LEFT JOIN ville vi ON v.id_ville = vi.id_ville " +
                    "LEFT JOIN users u ON r.id_user = u.id " +
                    "LEFT JOIN avion a ON v.id_avion = a.id_avion " +
                    "WHERE r.id_reservation = ?";

            queryResult = DatabaseUtil.executeQuery(query, idReservation);

            if (queryResult.resultSet.next()) {
                return mapResultSetToReservation(queryResult.resultSet);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de la réservation par ID:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return null;
    }

    /**
     * Créer une nouvelle réservation
     */
    public boolean createReservation(Reservation reservation) {
        if (reservation == null || reservation.getIdVol() == null || reservation.getIdUser() == null) {
            System.out.println("❌ Données de réservation invalides: " + reservation);
            return false;
        }

        try {
            // Calculer le prix total avec les nouvelles quantités par catégorie
            Map<Long, Map<String, Integer>> quantities = reservation.getQuantitiesForCalculation();
            BigDecimal prixCalcule = calculateReservationPrice(reservation.getIdVol(), quantities);

            // Utiliser le prix calculé plutôt que celui fourni
            reservation.setPrixTotal(prixCalcule);

            // Préparer la requête SQL
            String query = "INSERT INTO reservation (date_reservation_, prix_total_, id_vol, id_user, siege_business, siege_eco) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            // Calculer les totaux par type de siège (pour compatibilité avec l'ancienne
            // structure)
            int totalSiegeBusiness = reservation.getSiegeBusinessParCategorie().values().stream()
                    .mapToInt(Integer::intValue).sum();
            int totalSiegeEco = reservation.getSiegeEcoParCategorie().values().stream()
                    .mapToInt(Integer::intValue).sum();

            Object[] params = {
                    reservation.getDateReservation() != null ? Timestamp.valueOf(reservation.getDateReservation())
                            : Timestamp.valueOf(LocalDateTime.now()),
                    reservation.getPrixTotal(),
                    reservation.getIdVol(),
                    reservation.getIdUser(),
                    totalSiegeBusiness,
                    totalSiegeEco
            };

            long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(query, params);

            if (generatedId > 0) {
                reservation.setIdReservation(generatedId);

                // Insérer les détails par catégorie d'âge dans une nouvelle table
                insertReservationDetails(generatedId, reservation.getSiegeBusinessParCategorie(),
                        reservation.getSiegeEcoParCategorie());

                System.out.println(
                        "✅ Réservation créée avec succès (ID: " + generatedId + ", Prix: " + prixCalcule + "€)");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la création de la réservation:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la réservation:");
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour insérer les détails de réservation par catégorie d'âge
    private void insertReservationDetails(Long reservationId, Map<Long, Integer> siegeBusinessParCategorie,
            Map<Long, Integer> siegeEcoParCategorie) throws SQLException {
        // Créer une table pour stocker les détails si elle n'existe pas
        String createTableQuery = "CREATE TABLE IF NOT EXISTS reservation_details (" +
                "id_detail SERIAL PRIMARY KEY, " +
                "id_reservation BIGINT NOT NULL, " +
                "id_categorie_age BIGINT NOT NULL, " +
                "type_siege VARCHAR(20) NOT NULL, " +
                "quantite INTEGER NOT NULL, " +
                "FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE" +
                ")";

        DatabaseUtil.executeUpdate(createTableQuery);

        // Insérer les sièges business
        for (Map.Entry<Long, Integer> entry : siegeBusinessParCategorie.entrySet()) {
            if (entry.getValue() > 0) {
                String insertQuery = "INSERT INTO reservation_details (id_reservation, id_categorie_age, type_siege, quantite) "
                        +
                        "VALUES (?, ?, 'business', ?)";
                DatabaseUtil.executeUpdate(insertQuery, reservationId, entry.getKey(), entry.getValue());
            }
        }

        // Insérer les sièges eco
        for (Map.Entry<Long, Integer> entry : siegeEcoParCategorie.entrySet()) {
            if (entry.getValue() > 0) {
                String insertQuery = "INSERT INTO reservation_details (id_reservation, id_categorie_age, type_siege, quantite) "
                        +
                        "VALUES (?, ?, 'eco', ?)";
                DatabaseUtil.executeUpdate(insertQuery, reservationId, entry.getKey(), entry.getValue());
            }
        }
    }

    // public boolean createReservation(Reservation reservation) {
    // if (reservation == null || reservation.getIdVol() == null) {
    // System.out.println("❌ Données de réservation invalides: " + reservation);
    // return false;
    // }

    // try {
    // String query = "INSERT INTO reservation (date_reservation_, prix_total_,
    // id_vol, id_user, siege_business, siege_eco) "
    // +
    // "VALUES (?, ?, ?, ?, ?, ?)";

    // Object[] params = {
    // reservation.getDateReservation() != null ?
    // Timestamp.valueOf(reservation.getDateReservation())
    // : Timestamp.valueOf(LocalDateTime.now()),
    // reservation.getPrixTotal(),
    // reservation.getIdVol(),
    // reservation.getIdUser(),
    // reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness() : 0,
    // reservation.getSiegeEco() != null ? reservation.getSiegeEco() : 0
    // };

    // long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(query, params);

    // if (generatedId > 0) {
    // reservation.setIdReservation(generatedId);
    // System.out.println("✅ Réservation créée avec succès (ID: " + generatedId +
    // ")");
    // return true;
    // }
    // } catch (SQLException e) {
    // System.err.println("❌ Erreur SQL lors de la création de la réservation:");
    // e.printStackTrace();
    // }
    // return false;
    // }

    /**
     * Mettre à jour une réservation
     */
    public boolean updateReservation(Reservation reservation) {
        if (reservation == null || reservation.getIdReservation() == null) {
            System.out.println("❌ Réservation ou ID invalide");
            return false;
        }

        try {
            // Calculer le prix total avec les nouvelles quantités par catégorie
            Map<Long, Map<String, Integer>> quantities = reservation.getQuantitiesForCalculation();
            BigDecimal prixCalcule = calculateReservationPrice(reservation.getIdVol(), quantities);

            // Utiliser le prix calculé
            reservation.setPrixTotal(prixCalcule);

            // Calculer les totaux par type de siège (pour compatibilité avec l'ancienne
            // structure)
            int totalSiegeBusiness = reservation.getSiegeBusinessParCategorie().values().stream()
                    .mapToInt(Integer::intValue).sum();
            int totalSiegeEco = reservation.getSiegeEcoParCategorie().values().stream()
                    .mapToInt(Integer::intValue).sum();

            String query = "UPDATE reservation SET date_reservation_ = ?, prix_total_ = ?, " +
                    "id_vol = ?, id_user = ?, siege_business = ?, siege_eco = ? " +
                    "WHERE id_reservation = ?";

            int rowsAffected = DatabaseUtil.executeUpdate(
                    query,
                    reservation.getDateReservation() != null ? Timestamp.valueOf(reservation.getDateReservation())
                            : null,
                    reservation.getPrixTotal(),
                    reservation.getIdVol(),
                    reservation.getIdUser(),
                    totalSiegeBusiness,
                    totalSiegeEco,
                    reservation.getIdReservation());

            if (rowsAffected > 0) {
                // Mettre à jour les détails de réservation
                updateReservationDetails(reservation.getIdReservation(),
                        reservation.getSiegeBusinessParCategorie(),
                        reservation.getSiegeEcoParCategorie());

                System.out.println("✅ Réservation mise à jour avec succès (Prix: " + prixCalcule + "€)");
                return true;
            } else {
                System.out.println("❌ Aucune réservation trouvée avec l'ID " + reservation.getIdReservation());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la réservation:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour de la réservation:");
            e.printStackTrace();
        }

        return false;
    }

    // Méthode pour mettre à jour les détails de réservation
    private void updateReservationDetails(Long reservationId, Map<Long, Integer> siegeBusinessParCategorie,
            Map<Long, Integer> siegeEcoParCategorie) throws SQLException {
        // Supprimer les anciens détails
        String deleteQuery = "DELETE FROM reservation_details WHERE id_reservation = ?";
        DatabaseUtil.executeUpdate(deleteQuery, reservationId);

        // Insérer les nouveaux détails
        insertReservationDetails(reservationId, siegeBusinessParCategorie, siegeEcoParCategorie);
    }

    /**
     * Obtenir le détail des prix par catégorie d'âge pour un vol et un utilisateur
     */
    public List<PrixDetail> getPrixDetailsForUser(Long idVol, Long idUser) {
        List<PrixDetail> details = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            String query = """
                        SELECT
                            ts.rubrique as type_siege,
                            ca.nom as categorie_age,
                            pav.prix_base,
                            pav.multiplicateur,
                            pav.prix_final,
                            EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) as age_user
                        FROM users u
                        CROSS JOIN type_siege ts
                        LEFT JOIN categorie_age ca ON (
                            EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) >= ca.age_min
                            AND (ca.age_max IS NULL OR EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) <= ca.age_max)
                            AND ca.is_active = TRUE
                        )
                        LEFT JOIN prix_age_vol pav ON (
                            pav.id_vol = ?
                            AND pav.id_type_siege = ts.id_type_siege
                            AND pav.id_categorie_age = ca.id_categorie_age
                        )
                        WHERE u.id = ?
                        ORDER BY ts.id_type_siege, ca.age_min
                    """;

            queryResult = DatabaseUtil.executeQuery(query, idVol, idUser);

            while (queryResult.resultSet.next()) {
                PrixDetail detail = new PrixDetail();
                detail.setTypeSiege(queryResult.resultSet.getString("type_siege"));
                detail.setCategorieAge(queryResult.resultSet.getString("categorie_age"));
                detail.setPrixBase(queryResult.resultSet.getBigDecimal("prix_base"));
                detail.setMultiplicateur(queryResult.resultSet.getBigDecimal("multiplicateur"));
                detail.setPrixFinal(queryResult.resultSet.getBigDecimal("prix_final"));
                detail.setAgeUser(queryResult.resultSet.getInt("age_user"));
                details.add(detail);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des détails de prix:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return details;
    }

    // public boolean updateReservation(Reservation reservation) {
    // if (reservation == null || reservation.getIdReservation() == null) {
    // System.out.println("❌ Réservation ou ID invalide");
    // return false;
    // }

    // try {
    // String query = "UPDATE reservation SET date_reservation_ = ?, prix_total_ =
    // ?, " +
    // "id_vol = ?, id_user = ?, siege_business = ?, siege_eco = ? " +
    // "WHERE id_reservation = ?";

    // int rowsAffected = DatabaseUtil.executeUpdate(
    // query,
    // reservation.getDateReservation() != null ?
    // Timestamp.valueOf(reservation.getDateReservation())
    // : null,
    // reservation.getPrixTotal(),
    // reservation.getIdVol(),
    // reservation.getIdUser(),
    // reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness() : 0,
    // reservation.getSiegeEco() != null ? reservation.getSiegeEco() : 0,
    // reservation.getIdReservation());

    // if (rowsAffected > 0) {
    // System.out.println("✅ Réservation mise à jour avec succès");
    // return true;
    // } else {
    // System.out.println("❌ Aucune réservation trouvée avec l'ID " +
    // reservation.getIdReservation());
    // }

    // } catch (SQLException e) {
    // System.err.println("❌ Erreur lors de la mise à jour de la réservation:");
    // e.printStackTrace();
    // }

    // return false;
    // }

    /**
     * Supprimer une réservation
     */
    public boolean deleteReservation(Long idReservation) {
        if (idReservation == null) {
            System.out.println("❌ ID de réservation invalide");
            return false;
        }

        try {
            String query = "DELETE FROM reservation WHERE id_reservation = ?";
            int rowsAffected = DatabaseUtil.executeUpdate(query, idReservation);

            if (rowsAffected > 0) {
                System.out.println("✅ Réservation supprimée avec succès");
                return true;
            } else {
                System.out.println("❌ Aucune réservation trouvée avec l'ID " + idReservation);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la réservation:");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Mapper un ResultSet vers un objet Reservation
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();

        // Mapping des colonnes de base
        reservation.setIdReservation(rs.getLong("id_reservation"));

        Timestamp dateReservation = rs.getTimestamp("date_reservation_");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }

        reservation.setPrixTotal(rs.getBigDecimal("prix_total_"));
        reservation.setIdVol(rs.getLong("id_vol"));

        // Gestion de l'ID utilisateur (peut être null)
        Integer id = rs.getInt("id_reservation");
        reservation.setIdReservation(id != null ? id.longValue() : null);

        // Gestion des sièges (pas de null pour les entiers)
        reservation.setSiegeBusiness(rs.getInt("siege_business"));
        reservation.setSiegeEco(rs.getInt("siege_eco"));

        // Propriétés étendues (peuvent être null avec les LEFT JOIN)
        reservation.setNumeroVol(rs.getString("numero_vol_"));

        Timestamp dateVol = rs.getTimestamp("date_vol_");
        if (dateVol != null) {
            reservation.setDateVol(dateVol.toLocalDateTime());
        }

        reservation.setVilleDestination(rs.getString("ville_destination"));
        reservation.setUsernameUser(rs.getString("username_user"));
        reservation.setPseudoAvion(rs.getString("avion_pseudo"));

        return reservation;
    }

    /**
     * Récupérer les statistiques des réservations (pour les admin)
     */
    public java.util.Map<String, Object> getReservationStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        QueryResult queryResult = null;

        try {
            // Nombre total de réservations
            String countQuery = "SELECT COUNT(*) as total FROM reservation";
            queryResult = DatabaseUtil.executeQuery(countQuery);
            if (queryResult.resultSet.next()) {
                stats.put("totalReservations", queryResult.resultSet.getInt("total"));
            }
            queryResult.close();

            // Chiffre d'affaires total
            String revenueQuery = "SELECT SUM(prix_total_) as total_revenue FROM reservation";
            queryResult = DatabaseUtil.executeQuery(revenueQuery);
            if (queryResult.resultSet.next()) {
                BigDecimal revenue = queryResult.resultSet.getBigDecimal("total_revenue");
                stats.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
            }
            queryResult.close();

            // Nombre de sièges réservés par type
            String seatsQuery = "SELECT SUM(siege_business) as total_business, SUM(siege_eco) as total_eco FROM reservation";
            queryResult = DatabaseUtil.executeQuery(seatsQuery);
            if (queryResult.resultSet.next()) {
                stats.put("totalSiegesBusiness", queryResult.resultSet.getInt("total_business"));
                stats.put("totalSiegesEco", queryResult.resultSet.getInt("total_eco"));
            }

            System.out.println("✅ Statistiques calculées: " + stats);

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du calcul des statistiques:");
            e.printStackTrace();
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return stats;
    }

    /**
     * Calculer le prix total d'une réservation en fonction de l'âge de
     * l'utilisateur
     */
    public BigDecimal calculateReservationPrice(Long idVol, Map<Long, Map<String, Integer>> quantities) {
        BigDecimal prixTotal = BigDecimal.ZERO;
        Map<Long, BigDecimal> categoryPrices = new HashMap<>();

        try {
            System.out.println("📊 Calcul du prix pour vol " + idVol);

            // Pour chaque catégorie d'âge
            for (Map.Entry<Long, Map<String, Integer>> entry : quantities.entrySet()) {
                Long idCategorie = entry.getKey();
                Map<String, Integer> seats = entry.getValue();
                BigDecimal categoryTotal = BigDecimal.ZERO;

                int siegeBusiness = seats.getOrDefault("business", 0);
                int siegeEco = seats.getOrDefault("eco", 0);

                // Calculer le prix pour les sièges Business
                if (siegeBusiness > 0) {
                    BigDecimal prixBusiness = getPrixPourCategorieEtType(idVol, idCategorie, 1);
                    BigDecimal totalBusiness = prixBusiness.multiply(BigDecimal.valueOf(siegeBusiness));
                    categoryTotal = categoryTotal.add(totalBusiness);
                }

                // Calculer le prix pour les sièges Économique
                if (siegeEco > 0) {
                    BigDecimal prixEco = getPrixPourCategorieEtType(idVol, idCategorie, 2);
                    BigDecimal totalEco = prixEco.multiply(BigDecimal.valueOf(siegeEco));
                    categoryTotal = categoryTotal.add(totalEco);
                }

                categoryPrices.put(idCategorie, categoryTotal);
                prixTotal = prixTotal.add(categoryTotal);
            }

            System.out.println("💰 Prix total calculé: " + prixTotal);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du calcul du prix:");
            e.printStackTrace();
        }

        return prixTotal;
    }

    private BigDecimal getPrixPourCategorieEtType(Long idVol, Long idCategorie, int idTypeSiege) {
        String query = "SELECT prix_final FROM prix_age_vol WHERE id_vol = ? AND id_categorie_age = ? AND id_type_siege = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idVol);
            stmt.setLong(2, idCategorie);
            stmt.setInt(3, idTypeSiege);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("prix_final");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du prix:");
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public Map<Long, Map<String, Integer>> parseQuantities(String quantitiesJson) {
        Map<Long, Map<String, Integer>> quantities = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(quantitiesJson);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Long categoryId = Long.parseLong(key);
                JSONObject categoryData = jsonObject.getJSONObject(key);

                Map<String, Integer> seats = new HashMap<>();
                seats.put("business", categoryData.optInt("business", 0));
                seats.put("eco", categoryData.optInt("eco", 0));

                quantities.put(categoryId, seats);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du parsing des quantités:");
            e.printStackTrace();
        }

        return quantities;
    }

    public List<PrixDetail> getPrixDetailsForVol(Long idVol, Map<Long, Map<String, Integer>> quantities) {
        List<PrixDetail> details = new ArrayList<>();

        try {
            for (Map.Entry<Long, Map<String, Integer>> entry : quantities.entrySet()) {
                Long idCategorie = entry.getKey();
                Map<String, Integer> seats = entry.getValue();

                int siegeBusiness = seats.getOrDefault("business", 0);
                int siegeEco = seats.getOrDefault("eco", 0);

                if (siegeBusiness > 0) {
                    BigDecimal prixBusiness = getPrixPourCategorieEtType(idVol, idCategorie, 1);
                    PrixDetail detail = new PrixDetail();
                    detail.setCategorieAgeId(idCategorie);
                    detail.setTypeSiege("Business");
                    detail.setQuantite(siegeBusiness);
                    detail.setPrixUnitaire(prixBusiness);
                    detail.setPrixTotal(prixBusiness.multiply(BigDecimal.valueOf(siegeBusiness)));
                    details.add(detail);
                }

                if (siegeEco > 0) {
                    BigDecimal prixEco = getPrixPourCategorieEtType(idVol, idCategorie, 2);
                    PrixDetail detail = new PrixDetail();
                    detail.setCategorieAgeId(idCategorie);
                    detail.setTypeSiege("Économique");
                    detail.setQuantite(siegeEco);
                    detail.setPrixUnitaire(prixEco);
                    detail.setPrixTotal(prixEco.multiply(BigDecimal.valueOf(siegeEco)));
                    details.add(detail);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des détails de prix:");
            e.printStackTrace();
        }

        return details;
    }

}