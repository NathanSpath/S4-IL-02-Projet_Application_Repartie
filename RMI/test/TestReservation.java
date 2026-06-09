import RMI.model.Client;
import RMI.dao.Requete;
import RMI.model.Reservation;
import RMI.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestReservation {

    private Requete requete;

    @BeforeEach
    public void setUp() {
        requete = new Requete();
    }

    @Test
    public void testCreationEtConflitReservation() throws SQLException {
        System.out.println("=== TEST : Réservation et détection de conflit ===");
        
        // 1. Préparation des données
        Client[] clients = requete.getClients();
        Table[] tables = requete.getTables();
        
        // on annule si les données ne sont pas sufficient
        if (clients == null || clients.length == 0 || tables == null || tables.length == 0) {
            System.out.println("Test annulé : il faut au moins un client et une table dans la base.");
            return;
        }

        String idClient = clients[0].getId();
        String idTable = tables[0].getId();

        // 2. Première réservation
        System.out.println("Tentative de création de la première réservation...");
        // Réservation de 2h maintenant
        Reservation resa1 = new Reservation(idClient, idTable, 2, 2.0); 
        boolean succes1 = requete.addReservation(Requete.getConnection(), resa1)!= null;
        
        assertTrue(succes1, "La première réservation aurait dû réussir (sauf si une existe déjà à cette heure exacte !)");
        if(succes1) {
             System.out.println("Première réservation réussie (ID: " + resa1.getId() + ")");
        }

        // 3. Deuxième réservation en conflit (sur la même table, au même moment)
        System.out.println("Tentative de création d'une réservation en conflit...");
        // Réservation de 1h, aussi maintenant, sur la MÊME table
        Reservation resaConflit = new Reservation(idClient, idTable, 4, 1.0);
        boolean succes2 = requete.addReservation(Requete.getConnection(),resaConflit)!=null;
        
        // La deuxième devrait échouer à cause de votre logique de conflit
        assertFalse(succes2, "La deuxième réservation aurait dû être bloquée (conflit d'horaire)");
        System.out.println("Le conflit a été correctement détecté et bloqué.");
    }
}
