package RMI.dao;

import RMI.model.Client;
import RMI.model.Reservation;
import RMI.model.Restaurant;
import RMI.model.Table;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class Requete {
    //region setup connection
    private static final String dbUrl;
    private static final String dbUser;
    private static final String dbPass;

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
        // Utilisation de getResourceAsStream à partir de la racine du classpath
        try (InputStream configInput = Requete.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (configInput != null) {
                prop.load(configInput);
            } else {
                System.err.println("AVERTISSEMENT: Le fichier 'config.properties' est introuvable dans le classpath.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de config.properties.");
        }

        //chargement de la config secret (non GIT)
        try (InputStream secretInput = Requete.class.getClassLoader().getResourceAsStream("config.secret.properties")) {
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
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            System.err.println("Le driver JDBC Oracle n'a pas été trouvé. Avez-vous ajouté ojdbc.jar à vos dépendances ?");
            e.printStackTrace();
            throw new SQLException("Driver Oracle non trouvé", e);
        }
        return conn;
    }
    //endregion

    //region requête RMI.model.Restaurant
    public Restaurant[] getRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESTAURANTS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                restaurantList.add(new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD")));
            }
            rs.close();
            pstmt.close();

            return restaurantList.toArray(new Restaurant[0]);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }


    public Restaurant[] getRestaurants(String sort, int pages, int size) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESTAURANTS " +
                    "ORDER BY ? " +
                    "OFFSET ? ROWS " +
                    "FETCH NEXT ? ROWS ONLY";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sort);
            pstmt.setInt(2, pages * size);
            pstmt.setInt(3, size);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                restaurantList.add(new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD")));
            }
            rs.close();
            pstmt.close();

            return restaurantList.toArray(new Restaurant[0]);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Restaurant[] getRestaurants(int page, int size) {
        return getRestaurants("NOM", page, size);
    }

    public Restaurant getRestaurantById(String id) {
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESTAURANTS where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            Restaurant restaurant = null;
            if (rs.next()) {
                restaurant = new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD"));
            }
            rs.close();
            pstmt.close();
            return restaurant;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Restaurant addRestaurant(Restaurant restaurant) {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        restaurant.setId(generatedId);

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_RESTAURANTS (ID, NOM, ADRESSE, COORD) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, restaurant.getId());
            pstmt.setString(2, restaurant.getName());
            pstmt.setString(3, restaurant.getAdresse());
            pstmt.setString(4, restaurant.getCoordonnees());
            pstmt.executeUpdate();
            pstmt.close();
            return restaurant; // Retourne l'objet avec son nouvel ID
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
            return null;
        }
    }
    //endregion

    //region requête RMI.model.Client
    public Client[] getClients() {
        List<Client> clientList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_CLIENT";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                clientList.add(new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL")));
            }
            rs.close();
            pstmt.close();
            return clientList.toArray(new Client[0]);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Client getClientById(String id) {
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_CLIENT where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            Client client = null;
            if (rs.next()) {
                client = new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL"));
            }
            rs.close();
            pstmt.close();
            return client;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    // Méthode conservée au cas où, mais plus nécessaire pour l'insertion
    public String getIdClient(String nom, String prenom, int numTel){
        try(Connection conn = getConnection()){
            String sql = "Select ID FROM E46438U.RMI_CLIENT where NOM = ? and PRENOM = ? and NUMTEL = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,nom);
            pstmt.setString(2,prenom);
            pstmt.setInt(3,numTel);
            ResultSet rs = pstmt.executeQuery();
            String idClient = "";
            if(rs.next()){
                idClient=rs.getString("ID");
            }
            rs.close();
            pstmt.close();
            return  idClient;
        }catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Client addClient(Client client) {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        client.setId(generatedId);

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_CLIENT (ID, NOM, PRENOM, NUMTEL) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getId());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getNumTel());
            pstmt.executeUpdate();
            pstmt.close();
            return client; // Retourne l'objet avec son nouvel ID
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
            return null;
        }
    }
    //endregion

    //region requête RMI.model.Table
    public Table[] getTables() {
        List<Table> tableList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_TABLE";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableList.add(new Table(rs.getString("ID"), rs.getString("IDRES"), rs.getString("NUMTABLE"), rs.getInt("NBPLACES")));
            }
            rs.close();
            pstmt.close();
            return tableList.toArray(new Table[0]);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
        }
        return null;
    }

    public Table getTableById(String id) {
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_TABLE where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            Table table = null;
            if (rs.next()) {
                table = new Table(rs.getString("ID"), rs.getString("IDRES"), rs.getString("NUMTABLE"), rs.getInt("NBPLACES"));
            }
            rs.close();
            pstmt.close();
            return table;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Table addTable(Table table) {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        table.setId(generatedId);

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_TABLE (ID, IDRES, NUMTABLE, NBPLACES) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, table.getId());
            pstmt.setString(2, table.getIdRes());
            pstmt.setString(3, table.getNumTable());
            pstmt.setInt(4, table.getNbPlaces());
            pstmt.executeUpdate();
            pstmt.close();
            return table; // Retourne l'objet avec son nouvel ID
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
            return null;
        }
    }

    //endregion

    //region requête RMI.model.Reservation
    public Reservation[] getReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESERVATION";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"),rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
            }
            rs.close();
            pstmt.close();
            return reservationList.toArray(new Reservation[0]);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Reservation[] getReservationByClient(String idCli) {
        List<Reservation> reservationList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESERVATION where IDCLI = ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idCli);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
            }
            rs.close();
            pstmt.close();
            return reservationList.toArray(new Reservation[0]);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Reservation[] getReservationByTableAndDate(Table table, Date date) {
        List<Reservation> reservationList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM E46438U.RMI_RESERVATION WHERE IDTAB = ? AND TRUNC(DATERESERVATION) = TRUNC(?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, table.getId());
            pstmt.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
            }
            rs.close();
            pstmt.close();
            return reservationList.toArray(new Reservation[0]);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }



    public Reservation getReservationById(String idReservation) {
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESERVATION where ID = ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idReservation);
            ResultSet rs = pstmt.executeQuery();
            Reservation reservation = null;
            if (rs.next()) {
                reservation = new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION"));
            }
            rs.close();
            pstmt.close();
            return reservation;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return null;
    }

    public Reservation addReservation(Reservation reservation) {
        Table table = getTableById(reservation.getIdTab());
        if (table.getNbPlaces() < reservation.getNbConvives()) {
            return null; // Pas assez de places
        }
        Reservation[] reservations = getReservationByTableAndDate(table, reservation.getDateReservation());
        for (Reservation r : reservations) {
            if(reservation.estEnConflitAvec(r)){
                return null; // Conflit de temps
            }
        }

        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        reservation.setId(generatedId);

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_RESERVATION (ID, IDCLI, IDTAB, NBCONVIVES, DUREE, DATERESERVATION) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reservation.getId());
            pstmt.setString(2, reservation.getIdCli());
            pstmt.setString(3, reservation.getIdTab());
            pstmt.setInt(4, reservation.getNbConvives());
            pstmt.setDouble(5, reservation.getDuree());
            pstmt.setTimestamp(6, new java.sql.Timestamp(reservation.getDateReservation().getTime()));
            
            pstmt.executeUpdate();
            pstmt.close();
            return reservation; // Retourne l'objet avec son nouvel ID
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
            return null;
        }
    }
    //endregion
}
