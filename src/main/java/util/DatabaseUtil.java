package util;

import java.sql.*;
import java.util.Properties;

public class DatabaseUtil {

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/login_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "antema";
    private static final String DRIVER_CLASS = "org.postgresql.Driver";

    // Bloc statique pour charger le driver une seule fois
    static {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("✅ Driver PostgreSQL chargé avec succès dans DatabaseUtil");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERREUR CRITIQUE: Driver PostgreSQL non trouvé!");
            System.err.println("Vérifiez que postgresql-42.7.2.jar est dans WEB-INF/lib/");
            throw new RuntimeException("Driver PostgreSQL non disponible", e);
        }
    }

    /**
     * Obtenir une nouvelle connexion à la base de données
     * 
     * @return Connection - Une nouvelle connexion
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Nouvelle connexion DB créée");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création de la connexion:");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            System.err.println("Erreur: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fermer une connexion proprement
     * 
     * @param conn - La connexion à fermer
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ Connexion DB fermée");
            } catch (SQLException e) {
                System.err.println("⚠️  Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }

    /**
     * Fermer un PreparedStatement proprement
     * 
     * @param stmt - Le statement à fermer
     */
    public static void closeStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("⚠️  Erreur lors de la fermeture du PreparedStatement: " + e.getMessage());
            }
        }
    }

    /**
     * Fermer un ResultSet proprement
     * 
     * @param rs - Le ResultSet à fermer
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("⚠️  Erreur lors de la fermeture du ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Fermer toutes les ressources en une fois
     * 
     * @param conn - La connexion
     * @param stmt - Le statement
     * @param rs   - Le ResultSet
     */
    public static void closeAll(Connection conn, PreparedStatement stmt, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    /**
     * Tester la connexion à la base de données
     * 
     * @return true si la connexion fonctionne, false sinon
     */
    public static boolean testConnection() {
        System.out.println("=== Test de connexion à la base de données ===");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Test simple avec COUNT
            stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM users");
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("✅ Connexion réussie! Nombre d'utilisateurs: " + count);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion:");
            e.printStackTrace();
            return false;
        } finally {
            closeAll(conn, stmt, rs);
        }

        return false;
    }

    /**
     * Exécuter une requête SELECT et retourner le ResultSet
     * ATTENTION: L'appelant doit fermer les ressources!
     * 
     * @param query  - La requête SQL
     * @param params - Les paramètres de la requête
     * @return Un objet contenant la connexion, le statement et le resultset
     */
    public static class QueryResult {
        public Connection connection;
        public PreparedStatement statement;
        public ResultSet resultSet;

        public QueryResult(Connection conn, PreparedStatement stmt, ResultSet rs) {
            this.connection = conn;
            this.statement = stmt;
            this.resultSet = rs;
        }

        public void close() {
            DatabaseUtil.closeAll(connection, statement, resultSet);
        }
    }

    /**
     * Exécuter une requête SELECT
     * 
     * @param query  - La requête SQL avec des ?
     * @param params - Les paramètres pour remplacer les ?
     * @return QueryResult - À fermer obligatoirement avec .close()
     */
    public static QueryResult executeQuery(String query, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query);

            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            rs = stmt.executeQuery();
            return new QueryResult(conn, stmt, rs);

        } catch (SQLException e) {
            // En cas d'erreur, fermer les ressources
            closeAll(conn, stmt, rs);
            throw e;
        }
    }

    /**
     * Exécuter une requête UPDATE/INSERT/DELETE
     * 
     * @param query  - La requête SQL avec des ?
     * @param params - Les paramètres pour remplacer les ?
     * @return int - Nombre de lignes affectées
     */
    public static int executeUpdate(String query, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query);

            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            int result = stmt.executeUpdate();
            System.out.println("✅ Requête UPDATE exécutée: " + result + " ligne(s) affectée(s)");
            return result;

        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }
    }

    /**
     * Exécuter un INSERT et retourner l'ID généré
     * 
     * @param query  - La requête INSERT avec des ?
     * @param params - Les paramètres pour remplacer les ?
     * @return long - L'ID généré, ou -1 si erreur
     */
    public static long executeInsertWithGeneratedKey(String query, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // Définir les paramètres
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    System.out.println("✅ INSERT exécuté avec ID généré: " + generatedId);
                    return generatedId;
                }
            }

            return -1;

        } finally {
            closeAll(conn, stmt, rs);
        }
    }

    /**
     * Exécuter une requête COUNT(*) et retourner le résultat
     *
     * @param query  - La requête SQL avec des ?
     * @param params - Les paramètres pour remplacer les ?
     * @return long - Résultat du COUNT, ou 0 si rien trouvé
     */
    public static long executeCount(String query, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(query);

            // Remplacer les ?
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } finally {
            closeAll(conn, stmt, rs);
        }
    }

    /**
     * Obtenir les informations de configuration (pour debug)
     */
    public static void printConnectionInfo() {
        System.out.println("=== Configuration de la base de données ===");
        System.out.println("URL: " + DB_URL);
        System.out.println("User: " + DB_USER);
        System.out.println("Driver: " + DRIVER_CLASS);
    }
}