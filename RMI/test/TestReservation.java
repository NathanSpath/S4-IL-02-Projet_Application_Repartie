import RMI.model.Client;
import RMI.dao.Requete;
import RMI.model.Reservation;
import RMI.model.Restaurant;
import RMI.model.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TestReservation {

    private Requete requete;
    private Connection testConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        requete = new Requete();
        testConnection = Requete.getConnection();
        testConnection.setAutoCommit(false);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (testConnection != null) {
            testConnection.rollback();
            testConnection.close();
        }
    }

    @Test
    public void testCreationEtConflitReservation() throws SQLException {
        System.out.println("=== TEST : Réservation et détection de conflit ===");
        
        // 1. Préparation des données spécifiques au test
        Client client = new Client(null, "Conflit", "Client", "1234567890");
        Client addedClient = requete.addClient(testConnection, client);

        // Création d'un VRAI restaurant pour obtenir un ID valide
        Restaurant restaurant = new Restaurant(null, "Resto Pour Table", "Adresse", "0,0");
        Restaurant addedRestaurant = requete.addRestaurant(testConnection, restaurant);
        
        // Création de la table avec l'ID valide du restaurant
        Table table = new Table(null, addedRestaurant.getId(), "T-CONFLIT", 4);
        Table addedTable = requete.addTable(testConnection, table);
        
        testConnection.commit(); // Commit pour rendre le client, le restaurant et la table visibles

        assertNotNull(addedClient, "Le client de test devrait être ajouté.");
        assertNotNull(addedTable, "La table de test devrait être ajoutée.");

        String idClient = addedClient.getId();
        String idTable = addedTable.getId();

        // 2. Première réservation
        System.out.println("Tentative de création de la première réservation...");
        Reservation resa1 = new Reservation(idClient, idTable, 2, 2.0); 
        Reservation addedResa1 = requete.addReservation(testConnection, resa1);
        
        assertNotNull(addedResa1, "La première réservation aurait dû réussir.");
        testConnection.commit();
        System.out.println("Première réservation réussie (ID: " + addedResa1.getId() + ")");

        // 3. Deuxième réservation en conflit
        System.out.println("Tentative de création d'une réservation en conflit...");
        // On crée une réservation qui chevauche la première
        Date dateConflit = new Date(addedResa1.getDateReservation().getTime() + (long)(0.5 * 3600 * 1000)); // 30 mins après le début de la première
        Reservation resaConflit = new Reservation(idClient, idTable, 4, 1.0, dateConflit);
        Reservation addedResaConflit = requete.addReservation(testConnection, resaConflit);
        
        assertNull(addedResaConflit, "La deuxième réservation aurait dû être bloquée (conflit d'horaire)");
        System.out.println("Le conflit a été correctement détecté et bloqué.");
    }
}
