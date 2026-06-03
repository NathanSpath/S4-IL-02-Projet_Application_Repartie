import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestClient {

    private Requete requete;

    @BeforeEach
    public void setUp() {
        requete = new Requete();
    }

    @Test
    public void testCreationEtLectureClient() {
        System.out.println("=== TEST : Création et lecture d'un client ===");
        
        // 1. Création d'un client de test
        Client nouveauClient = new Client("Test", "0600000000","0123456789");
        Client clientSauvegarde = requete.addClient(nouveauClient);

        // Vérifier que l'ID a bien été généré et retourné
        assertNotNull(clientSauvegarde, "Le client retourné ne doit pas être null");
        assertNotNull(clientSauvegarde.getId(), "L'ID du client doit avoir été généré");
        
        String idGenere = clientSauvegarde.getId();
        System.out.println("Client créé avec succès (ID: " + idGenere + ")");

        // 2. Lecture du client par son ID
        Client clientLu = requete.getClientById(idGenere);
        
        // Vérifier que c'est bien le même
        assertNotNull(clientLu, "Le client lu en base ne doit pas être null");
        assertEquals("Unit", clientLu.getNom(), "Le nom doit correspondre");
        assertEquals("Test", clientLu.getPrenom(), "Le prénom doit correspondre");
        assertEquals("0600000000", clientLu.getNumTel(), "Le numéro de téléphone doit correspondre");
    }

    @Test
    public void testLectureTousClients() {
        System.out.println("=== TEST : Lecture de tous les clients ===");
        
        Client[] clients = requete.getClients();
        assertNotNull(clients, "Le tableau de clients ne doit pas être null (il peut être vide)");
        
        System.out.println("Nombre total de clients en base : " + clients.length);
    }
}
