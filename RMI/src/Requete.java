import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public void addRestaurant(Restaurant restaurant) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_RESTAURANTS (NOM, ADRESSE, COORD) VALUES (?, ?, ?)";
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

    public void addClient(Client client) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_CLIENT (NOM, PRENOM, NUMTEL) VALUES (?, ?, ?)";
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

    //region requête Table
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

    public void addTable(Table table) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_TABLE (IDRES, NUMTABLE, NBPLACES) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, table.getIdRes());
            pstmt.setString(2, table.getNumTable());
            pstmt.setInt(3, table.getNbPlaces());
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
        List<Reservation> reservationList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "Select * from E46438U.RMI_RESERVATION";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDate("DATERESERVATION")));
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
            if (rs.next()) {
                reservationList.add(new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDate("DATERESERVATION")));
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
                reservation = new Reservation(rs.getString("ID"), rs.getString("IDCLI"), rs.getString("IDTAB"), rs.getInt("NBCONVIVES"), rs.getDate("DATERESERVATION"));
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

    public boolean addReservation(Reservation reservation) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO E46438U.RMI_RESERVATION (IDCLI, IDTAB, NBCONVIVES) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reservation.getIdCli());
            pstmt.setString(2, reservation.getIdTab());
            pstmt.setInt(3, reservation.getNbConvives());
            int rows = pstmt.executeUpdate();
            pstmt.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion ou de l'exécution de la requête.");
            e.printStackTrace();
        }
        return false;
    }
    //endregion
}
