package RMI.dao;

import RMI.model.Client;
import RMI.model.Reservation;
import RMI.model.Restaurant;
import RMI.model.Table;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


public class Requete {
    private static final String dbUrl;
    private static final String dbUser;
    private static final String dbPass;

    //region Setup
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
        try (InputStream configInput = Requete.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (configInput != null) {
                prop.load(configInput);
            } else {
                System.err.println("AVERTISSEMENT: Le fichier 'config.properties' est introuvable dans le classpath.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de config.properties.");
        }
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

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (ClassNotFoundException e) {
            System.err.println("Le driver JDBC Oracle n'a pas été trouvé.");
            throw new SQLException("Driver Oracle non trouvé", e);
        }
    }
    //endregion

    //region Restaurant
    public Restaurant[] getRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_RESTAURANTS";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                restaurantList.add(new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantList.toArray(new Restaurant[0]);
    }

    public Restaurant[] getRestaurants(String sort, int pages, int size) {
        List<Restaurant> restaurantList = new ArrayList<>();

        List<String> sortCollumnName = Arrays.asList("ID", "NOM", "ADRESSE"); // liste des trie possible
        String sortColumn = "NOM";
        if (sort != null && sortCollumnName.contains(sort.toUpperCase())) {
            sortColumn = sort.toUpperCase();
        }

        //on construit la requet dynamiquement en utilisant les sort disponnible dans la lsite des chanmps predefini;
        String sql = String.format("SELECT * FROM E46438U.RMI_RESTAURANTS ORDER BY %s OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", sortColumn);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pages * size);
            pstmt.setInt(2, size);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    restaurantList.add(new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return restaurantList.toArray(new Restaurant[0]);
    }

    public Restaurant[] getRestaurants(int page, int size) {
        return getRestaurants("NOM", page, size);
    }

    public Restaurant getRestaurantById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM E46438U.RMI_RESTAURANTS WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Restaurant(rs.getString("ID"), rs.getString("NOM"), rs.getString("ADRESSE"), rs.getString("COORD"));
                }
            }
        }
        return null;
    }

    public Restaurant getRestaurantById(String id) {
        try (Connection conn = getConnection()) {
            return getRestaurantById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Restaurant addRestaurant(Connection conn, Restaurant restaurant) throws SQLException {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        restaurant.setId(generatedId);
        String sql = "INSERT INTO E46438U.RMI_RESTAURANTS (ID, NOM, ADRESSE, COORD) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, restaurant.getId());
            pstmt.setString(2, restaurant.getName());
            pstmt.setString(3, restaurant.getAdresse());
            pstmt.setString(4, restaurant.getCoordonnees());
            pstmt.executeUpdate();
        }
        return restaurant;
    }
    //endregion

    //region Client
    public Client[] getClients() {
        List<Client> clientList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_CLIENT";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clientList.add(new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientList.toArray(new Client[0]);
    }

    public Client getClient(String nom, String prenom,  int numTel) {
        Client client = null;
        String sql = "SELECT * FROM E46438U.RMI_CLIENT WHERE NOM = ? AND PRENOM = ? AND NUMTEL = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setInt(3, numTel);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    client=new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }

    public Client getClientById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM E46438U.RMI_CLIENT WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(rs.getString("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("NUMTEL"));
                }
            }
        }
        return null;
    }

    public Client getClientById(String id) {
        try (Connection conn = getConnection()) {
            return getClientById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client addClient(Connection conn, Client client) throws SQLException {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        client.setId(generatedId);
        String sql = "INSERT INTO E46438U.RMI_CLIENT (ID, NOM, PRENOM, NUMTEL) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, client.getId());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getPrenom());
            pstmt.setString(4, client.getNumTel());
            pstmt.executeUpdate();
        }
        return client;
    }
    //endregion

    //region Table

    public Table[] getTables(){
        List<Table> tableList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_TABLE";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tableList.add(new Table(rs.getString("ID"), rs.getString("IDRES"), rs.getString("NUMTABLE"), rs.getInt("NBPLACES")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return tableList.toArray(new Table[0]);
    }
    public Table getTableById(Connection conn, String id) throws SQLException {
        String sql = "SELECT * FROM E46438U.RMI_TABLE WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Table(rs.getString("ID"), rs.getString("IDRES"), rs.getString("NUMTABLE"), rs.getInt("NBPLACES"));
                }
            }
        }
        return null;
    }

    public Table getTableById(String id) {
        try (Connection conn = getConnection()) {
            return getTableById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Table addTable(Connection conn, Table table) throws SQLException {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        table.setId(generatedId);
        String sql = "INSERT INTO E46438U.RMI_TABLE (ID, IDRES, NUMTABLE, NBPLACES) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, table.getId());
            pstmt.setString(2, table.getIdRes());
            pstmt.setString(3, table.getNumTable());
            pstmt.setInt(4, table.getNbPlaces());
            pstmt.executeUpdate();
        }
        return table;
    }
//endregion

    //region Reservation
    public Reservation[] getReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_RESERVATION";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"),rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationList.toArray(new Reservation[0]);
    }

    public Reservation[] getReservationByClient(String idCli) {
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_RESERVATION WHERE IDCLI = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idCli);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationList.toArray(new Reservation[0]);
    }

    public Reservation getReservationById(String idReservation) {
        String sql = "SELECT * FROM E46438U.RMI_RESERVATION WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idReservation);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reservation[] getReservationByTableAndDate(Connection conn, String idTab, Date date) throws SQLException {
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM E46438U.RMI_RESERVATION WHERE IDTAB = ? AND TRUNC(DATERESERVATION) = TRUNC(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idTab);
            pstmt.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDouble("DUREE"), rs.getTimestamp("DATERESERVATION")));
                }
            }
        }
        return reservationList.toArray(new Reservation[0]);
    }

    public Reservation addReservation(Connection conn, Reservation reservation) throws SQLException {
        String generatedId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        reservation.setId(generatedId);

        String sql = "INSERT INTO E46438U.RMI_RESERVATION (ID, IDCLI, IDTAB, NBCONVIVES, DUREE, DATERESERVATION) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getId());
            pstmt.setString(2, reservation.getIdCli());
            pstmt.setString(3, reservation.getIdTab());
            pstmt.setInt(4, reservation.getNbConvives());
            pstmt.setDouble(5, reservation.getDuree());
            pstmt.setTimestamp(6, new java.sql.Timestamp(reservation.getDateReservation().getTime()));
            pstmt.executeUpdate();
        }
        return reservation;
    }


//endregion
}
