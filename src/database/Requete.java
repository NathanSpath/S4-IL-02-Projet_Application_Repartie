package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Requete {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;

    static {
        Properties prop = new Properties();
        try (InputStream secretInput = Requete.class.getClassLoader().getResourceAsStream("database/secret.properties")) {
            if (secretInput != null) {
                prop.load(secretInput);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de secret.properties (ceci est peut-être normal).");
            e.printStackTrace();
        }

        dbUrl = prop.getProperty("db.url");
        dbUser = prop.getProperty("db.user");
        dbPass = prop.getProperty("db.password");

        if (dbUrl == null || dbUser == null || dbPass == null) {

            try (InputStream configInput = Requete.class.getClassLoader().getResourceAsStream("database/config.properties")) {
                if (configInput == null) {
                    System.err.println("ERREUR CRITIQUE: Le fichier 'database/config.properties' est introuvable dans le classpath.");
                } else {
                    prop.load(configInput);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture de config.properties ou secret.properties.");
                e.printStackTrace();

            }
        }
    }

    /**
     * Établit et retourne une connexion à la base de données Oracle.
     *
     * @return Connection L'objet de connexion JDBC
     * @throws SQLException Si une erreur de connexion survient
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Le driver JDBC Oracle n'a pas été trouvé. Avez-vous ajouté ojdbc.jar à vos dépendances ?");
            e.printStackTrace();
            throw new SQLException("Driver Oracle non trouvé", e);
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    /**
     * Méthode de base où vous pourrez écrire et exécuter vos requêtes.
     */
    public void executerMaRequete() {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESTAURANT";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("NOM"));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
    }
}
