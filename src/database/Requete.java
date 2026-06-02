package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Requete {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;

    // Méthode pour nettoyer les valeurs lues (enlever les guillemets et espaces)
    private static String cleanValue(String value) {
        if (value == null) return null;
        value = value.trim();
        if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
            if (value.length() >= 2) {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    static {
        Properties prop = new Properties();
        
        // 1. Charger les propriétés par défaut depuis config.properties (qui est sur Git)
        try (InputStream configInput = Requete.class.getClassLoader().getResourceAsStream("database/config.properties")) {
            if (configInput != null) {
                prop.load(configInput);
            } else {
                System.err.println("AVERTISSEMENT: Le fichier 'database/config.properties' est introuvable dans le classpath.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de config.properties.");
        }

        // 2. Tenter de charger les secrets et d'écraser les valeurs par défaut
        try (InputStream secretInput = Requete.class.getClassLoader().getResourceAsStream("database/secret.properties")) {
            if (secretInput != null) {
                prop.load(secretInput); // Les secrets écrasent les valeurs de config
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de secret.properties.");
        }

        // Nettoyage des valeurs pour éviter les erreurs communes (guillemets dans le fichier properties)
        dbUrl = cleanValue(prop.getProperty("db.url"));
        dbUser = cleanValue(prop.getProperty("db.user"));
        dbPass = cleanValue(prop.getProperty("db.password"));
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


    public void executerMaRequete() {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESTAURANTS";
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
