package RMI.test;

import RMI.dao.Requete;
import RMI.model.Client;
import RMI.model.Reservation;
import RMI.model.Restaurant;
import RMI.model.Table;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequeteTest {

    private Requete requete;
    private Connection testConnection;

    @BeforeEach
    void setUp() throws SQLException {
        requete = new Requete();
        testConnection = Requete.getConnection();
        testConnection.setAutoCommit(false); // Début de la transaction
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testConnection != null) {
            testConnection.rollback(); // Annulation de toutes les modifications du test
            testConnection.close();
        }
    }

    @Test
    @Order(1)
    void testAddClient() throws SQLException {
        Client newClient = new Client(null, "Doe", "John", "0123456789");
        Client addedClient = requete.addClient(testConnection, newClient);
        testConnection.commit(); // On commit pour pouvoir le lire juste après

        assertNotNull(addedClient);
        assertNotNull(addedClient.getId());

        Client foundClient = requete.getClientById(testConnection, addedClient.getId());
        assertNotNull(foundClient);
        assertEquals("Doe", foundClient.getNom());
    }

    @Test
    @Order(2)
    void testGetClientByNomPrenomNumTel() throws SQLException {
        Client existingClient = new Client(null, "TestNom", "TestPrenom", "0987654321");
        requete.addClient(testConnection, existingClient);
        testConnection.commit(); // Commit pour rendre le client visible à la recherche

        Client foundClient = requete.getClient("TestNom", "TestPrenom", "0987654321");
        assertNotNull(foundClient, "Le client créé devrait être trouvé.");
        assertEquals("TestNom", foundClient.getNom());
    }

    @Test
    @Order(3)
    void testAddRestaurant() throws SQLException {
        Restaurant newRestaurant = new Restaurant(null, "Le Test", "123 Rue Test", "48.8566,2.3522");
        Restaurant addedRestaurant = requete.addRestaurant(testConnection, newRestaurant);
        testConnection.commit();

        assertNotNull(addedRestaurant);
        assertNotNull(addedRestaurant.getId());

        Restaurant foundRestaurant = requete.getRestaurantById(testConnection, addedRestaurant.getId());
        assertNotNull(foundRestaurant);
    }

    @Test
    @Order(4)
    void testAddTable() throws SQLException {
        Restaurant newRestaurant = new Restaurant(null, "Restaurant Table", "Adresse Table", "0,0");
        Restaurant addedRestaurant = requete.addRestaurant(testConnection, newRestaurant);
        testConnection.commit();

        Table newTable = new Table(null, addedRestaurant.getId(), "T101", 4);
        Table addedTable = requete.addTable(testConnection, newTable);
        testConnection.commit();

        assertNotNull(addedTable);
        assertNotNull(addedTable.getId());

        Table foundTable = requete.getTableById(testConnection, addedTable.getId());
        assertNotNull(foundTable);
    }

    @Test
    @Order(5)
    void testAddReservation() throws SQLException {
        Client newClient = new Client(null, "ResClient", "ResPrenom", "111222333");
        Client addedClient = requete.addClient(testConnection, newClient);

        Restaurant newRestaurant = new Restaurant(null, "ResResto", "ResAdresse", "1,1");
        Restaurant addedRestaurant = requete.addRestaurant(testConnection, newRestaurant);

        Table newTable = new Table(null, addedRestaurant.getId(), "ResT1", 2);
        Table addedTable = requete.addTable(testConnection, newTable);
        testConnection.commit(); // On commit tout (client, resto, table)

        Reservation newReservation = new Reservation(addedClient.getId(), addedTable.getId(), 2, 1.5);
        Reservation addedReservation = requete.addReservation(testConnection, newReservation);
        
        assertNotNull(addedReservation, "La réservation n'aurait pas dû échouer (pas de conflit ici).");
        testConnection.commit(); // On commit la réservation

        assertNotNull(addedReservation.getId());

        Reservation foundReservation = requete.getReservationById(addedReservation.getId());
        assertNotNull(foundReservation);
    }
}
