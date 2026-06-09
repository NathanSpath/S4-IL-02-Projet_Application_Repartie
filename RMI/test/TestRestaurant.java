import RMI.dao.Requete;
import RMI.model.Restaurant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestRestaurant {

    private Requete requete;
    private Connection testConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        requete = new Requete();
        testConnection = Requete.getConnection();
        testConnection.setAutoCommit(false); //mode transactionelle pour les test aussi
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (testConnection != null) {
            testConnection.rollback(); // roll back comme ca on ne laisse pas de transaction non commité ce qui pourrait bloquer la bdd
            testConnection.close();
        }
    }

    @Test
    public void testCreationEtLectureRestaurant() throws SQLException {
        System.out.println("=== TEST : Création et lecture d'un restaurant ===");
        
        // 1. Création d'un restaurant de test
        Restaurant nouveauResto = new Restaurant(null, "Resto Test unitaire", "123 Rue du Test", "48.0, 6.0");
        Restaurant restoSauvegarde = requete.addRestaurant(testConnection, nouveauResto);
        testConnection.commit();

        // Vérifier que l'ID a bien été généré et retourné
        assertNotNull(restoSauvegarde, "Le restaurant retourné ne doit pas être null");
        assertNotNull(restoSauvegarde.getId(), "L'ID du restaurant doit avoir été généré");
        
        String idGenere = restoSauvegarde.getId();
        System.out.println("RMI.model.Restaurant créé avec succès (ID: " + idGenere + ")");

        // 2. Lecture du restaurant par son ID
        Restaurant restoLu = requete.getRestaurantById(testConnection, idGenere);
        
        // Vérifier que c'est bien le même
        assertNotNull(restoLu, "Le restaurant lu en base ne doit pas être null");
        assertEquals("Resto Test unitaire", restoLu.getName(), "Le nom doit correspondre");
        assertEquals("123 Rue du Test", restoLu.getAdresse(), "L'adresse doit correspondre");
    }

    @Test
    public void testPaginationRestaurants() throws SQLException {
        System.out.println("=== TEST : Pagination des restaurants ===");
        
        // Ajout de quelques restaurants pour le test
        requete.addRestaurant(testConnection, new Restaurant(null, "Resto A", "Addr A", "0,0"));
        requete.addRestaurant(testConnection, new Restaurant(null, "Resto B", "Addr B", "0,0"));
        requete.addRestaurant(testConnection, new Restaurant(null, "Resto C", "Addr C", "0,0"));
        testConnection.commit();

        // On demande la page 0 (la première), avec 2 éléments par page
        Restaurant[] restaurantsPage1 = requete.getRestaurants("NOM", 0, 2);
        
        // On vérifie qu'on ne reçoit pas plus de 2 éléments
        assertNotNull(restaurantsPage1, "La liste ne doit pas être null");
        assertEquals(2, restaurantsPage1.length, "La première page doit contenir 2 éléments");
        
        System.out.println("Nombre de restaurants reçus pour la page 1 : " + restaurantsPage1.length);
    }
}
