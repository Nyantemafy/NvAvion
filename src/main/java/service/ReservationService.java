package service;

import model.*;
import util.DatabaseUtil;
import util.DatabaseUtil.QueryResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

            // Filtre par numéro de vol
            if (filter.getNumeroVol() != null && !filter.getNumeroVol().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(v.numero_vol_) LIKE LOWER(?) ");
                params.add("%" + filter.getNumeroVol().trim() + "%");
            }

            // Filtre par ville de destination
            if (filter.getVilleDestination() != null && !filter.getVilleDestination().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(vi.nom) LIKE LOWER(?) ");
                params.add("%" + filter.getVilleDestination().trim() + "%");
            }

            // Filtre par utilisateur
            if (filter.getUsername() != null && !filter.getUsername().trim().isEmpty()) {
                queryBuilder.append("AND LOWER(u.username) LIKE LOWER(?) ");
                params.add("%" + filter.getUsername().trim() + "%");
            }

            // Filtre par prix
            if (filter.getPrixMin() != null) {
                queryBuilder.append("AND r.prix_total_ >= ? ");
                params.add(filter.getPrixMin());
            }

            if (filter.getPrixMax() != null) {
                queryBuilder.append("AND r.prix_total_ <= ? ");
                params.add(filter.getPrixMax());
            }

            // Filtre par date de réservation
            if (filter.getDateReservationDebut() != null) {
                queryBuilder.append("AND r.date_reservation_ >= ? ");
                params.add(Timestamp.valueOf(filter.getDateReservationDebut().atStartOfDay()));
            }

            if (filter.getDateReservationFin() != null) {
                queryBuilder.append("AND r.date_reservation_ <= ? ");
                params.add(Timestamp.valueOf(filter.getDateReservationFin().atTime(23, 59, 59)));
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

            // Filtre par sièges business minimum
            if (filter.getPrixMax() != null) {
                queryBuilder.append("AND r.siege_business >= ? ");
                params.add(filter.getPrixMax());
            }

            // Filtre par sièges éco minimum
            if (filter.getPrixMin() != null) {
                queryBuilder.append("AND r.siege_eco >= ? ");
                params.add(filter.getPrixMin());
            }

            queryBuilder.append("ORDER BY r.date_reservation_ DESC");

            System.out.println("🔍 Requête de recherche réservations: " + queryBuilder.toString());
            System.out.println("🔍 Paramètres: " + params);

            queryResult = DatabaseUtil.executeQuery(queryBuilder.toString(), params.toArray());

            while (queryResult.resultSet.next()) {
                Reservation reservation = mapResultSetToReservation(queryResult.resultSet);
                reservations.add(reservation);
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
        if (reservation == null || reservation.getIdVol() == null) {
            System.out.println("❌ Données de réservation invalides: " + reservation);
            return false;
        }

        try {
            String query = "INSERT INTO reservation (date_reservation_, prix_total_, id_vol, id_user, siege_business, siege_eco) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            Object[] params = {
                    reservation.getDateReservation() != null ? Timestamp.valueOf(reservation.getDateReservation())
                            : Timestamp.valueOf(LocalDateTime.now()),
                    reservation.getPrixTotal(),
                    reservation.getIdVol(),
                    reservation.getIdUser(),
                    reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness() : 0,
                    reservation.getSiegeEco() != null ? reservation.getSiegeEco() : 0
            };

            long generatedId = DatabaseUtil.executeInsertWithGeneratedKey(query, params);

            if (generatedId > 0) {
                reservation.setIdReservation(generatedId);
                System.out.println("✅ Réservation créée avec succès (ID: " + generatedId + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la création de la réservation:");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mettre à jour une réservation
     */
    public boolean updateReservation(Reservation reservation) {
        if (reservation == null || reservation.getIdReservation() == null) {
            System.out.println("❌ Réservation ou ID invalide");
            return false;
        }

        try {
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
                    reservation.getSiegeBusiness() != null ? reservation.getSiegeBusiness() : 0,
                    reservation.getSiegeEco() != null ? reservation.getSiegeEco() : 0,
                    reservation.getIdReservation());

            if (rowsAffected > 0) {
                System.out.println("✅ Réservation mise à jour avec succès");
                return true;
            } else {
                System.out.println("❌ Aucune réservation trouvée avec l'ID " + reservation.getIdReservation());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la réservation:");
            e.printStackTrace();
        }

        return false;
    }

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
        reservation.setIdReservation(rs.getLong("id_reservation"));

        Timestamp dateReservation = rs.getTimestamp("date_reservation_");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }

        reservation.setPrixTotal(rs.getBigDecimal("prix_total_"));
        reservation.setIdVol(rs.getLong("id_vol"));

        Long idUser = rs.getObject("id_user", Long.class);
        reservation.setIdUser(idUser);

        reservation.setSiegeBusiness(rs.getInt("siege_business"));
        reservation.setSiegeEco(rs.getInt("siege_eco"));

        // Propriétés étendues
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
}
