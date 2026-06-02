import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Requete {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;

    //netoie les guillemet pour eviter les erreur qui serait du a al completion du fichier config
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
        //chargement de la config public (GIT)
        try (InputStream configInput = Requete.class.getClassLoader().getResourceAsStream("RMI/src/database/config.properties")) {
            if (configInput != null) {
                prop.load(configInput);
            } else {
                System.err.println("AVERTISSEMENT: Le fichier 'database/config.properties' est introuvable dans le classpath.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de config.properties.");
        }

        //chargement de la config secret (non GIT)
        try (InputStream secretInput = Requete.class.getClassLoader().getResourceAsStream("RMI/src/database/config.secret.properties")) {
            if (secretInput != null) {
                prop.load(secretInput);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de config.secret.properties.");
        }

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

    //region requête Restaurant
    public Restaurant[] getRestaurants() {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESTAURANTS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            Restaurant[] restaurants = new Restaurant[rs.getFetchSize()];
            int i=0;
            while (rs.next()) {
                restaurants[i] = new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD"));
                i++;
            }
            rs.close();
            pstmt.close();
            return restaurants;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Restaurant getRestaurantById(String id) {

        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESTAURANTS " +
                    "where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            Restaurant restaurant = null;
            while (rs.next()) {
                restaurant = new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD"));
            }
            rs.close();
            pstmt.close();
            return restaurant;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return  null;
    }

    public void addRestaurant(Restaurant restaurant) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "INSERT INTO E46438U.RMI_RESTAURANTS (NOM, ADRESSE, COORD) VALUES (NOM = ?, ADRESSE = ?, COORD = ? )";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAdresse());
            pstmt.setString(3, restaurant.getCoordonnees());
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
    }
    //endregion
    //region requête Client
    public Client[] getClients() {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_CLIENT";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            Client[] clients = new Client[rs.getFetchSize()];
            int i=0;
            while (rs.next()) {
                clients[i] = new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL"));
                i++;
            }
            rs.close();
            pstmt.close();
            return clients;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Client getClientById(String id) {

        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_CLIENT " +
                    "where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            Client client = null;
            while (rs.next()) {
                client = new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD"));
            }
            rs.close();
            pstmt.close();
            return client;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return  null;
    }

    public void addClient(Client client) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "INSERT INTO E46438U.RMI_CLIENT (NOM, PRENOM, NUMTEL) VALUES (NOM = ?, PRENOM = ?, NUMTEL = ? )";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getNumTel());
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
    }
    //endregion
    //region requête Reservation
    public Reservation[] getReservations() {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESERVATION";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            Reservation[] reservations = new Reservation[rs.getFetchSize()];
            int i=0;
            while (rs.next()) {
                reservations[i] = new Reservation(rs.getString("IDCLI"), rs.getString("IDRES"), rs.getInt("NBCONVIVES"));
            }
            rs.close();
            pstmt.close();
            return reservations;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Reservation getReservationById(String idCli, String idRes) {

        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "Select * from E46438U.RMI_RESERVATION " +
                    "where IDCLI = ? and  idRes = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idCli);
            pstmt.setString(2, idRes);

            ResultSet rs = pstmt.executeQuery();
            Reservation reservation = null;
            while (rs.next()) {
                reservation = new Reservation(rs.getString("IDClI"), rs.getString("IDRES"), rs.getInt("NBCONVIVES"));
            }
            rs.close();
            pstmt.close();
            return reservation;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return  null;
    }

    public void addReservation(Reservation reservation) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion à la base de données réussie !");

            String sql = "INSERT INTO E46438U.RMI_RESERVATION (IDCLI, IDRES, NBCONVIVES) VALUES (IDCLI = ?, IDRES = ?, NBCONVIVES = ? )";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reservation.getIdCli());
            pstmt.setString(2, reservation.getIdRes());
            pstmt.setInt(3, reservation.getNbConvives());
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
    }
    //endregion



}
