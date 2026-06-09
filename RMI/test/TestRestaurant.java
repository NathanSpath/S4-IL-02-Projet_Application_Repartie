import RMI.dao.Requete;
import RMI.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestRestaurant {

    private Requete requete;

    @BeforeEach
    public void setUp() {
        requete = new Requete();
    }

    @Test
    public void testCreationEtLectureRestaurant() {
        System.out.println("=== TEST : Création et lecture d'un restaurant ===");
        
        // 1. Création d'un restaurant de test
        Restaurant nouveauResto = new Restaurant("Resto Test unitaire", "123 Rue du Test", "48.0, 6.0");
        Restaurant restoSauvegarde = requete.addRestaurant(nouveauResto);

        // Vérifier que l'ID a bien été généré et retourné
        assertNotNull(restoSauvegarde, "Le restaurant retourné ne doit pas être null");
        assertNotNull(restoSauvegarde.getId(), "L'ID du restaurant doit avoir été généré");
        
        String idGenere = restoSauvegarde.getId();
        System.out.println("RMI.model.Restaurant créé avec succès (ID: " + idGenere + ")");

        // 2. Lecture du restaurant par son ID
        Restaurant restoLu = requete.getRestaurantById(idGenere);
        
        // Vérifier que c'est bien le même
        assertNotNull(restoLu, "Le restaurant lu en base ne doit pas être null");
        assertEquals("Resto Test unitaire", restoLu.getName(), "Le nom doit correspondre");
        assertEquals("123 Rue du Test", restoLu.getAdresse(), "L'adresse doit correspondre");
    }

    @Test
    public void testPaginationRestaurants() {
        System.out.println("=== TEST : Pagination des restaurants ===");
        
        // On demande la page 0 (la première), avec 3 éléments par page
        Restaurant[] restaurantsPage1 = requete.getRestaurants("NOM", 0, 3);
        
        // On vérifie qu'on ne reçoit pas plus de 3 éléments
        assertNotNull(restaurantsPage1, "La liste ne doit pas être null");
        assertTrue(restaurantsPage1.length <= 3, "La page ne doit pas contenir plus de 3 éléments");
        
        System.out.println("Nombre de restaurants reçus pour la page 1 : " + restaurantsPage1.length);
    }
}
