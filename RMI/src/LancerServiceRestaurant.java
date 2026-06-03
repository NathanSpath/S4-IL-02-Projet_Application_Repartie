import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceRestaurant {
    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length < 2) {
            System.err.println("Usage: java LancerServiceRestaurant <ip_serveur_central> <id_restaurant>");
            System.err.println("Exemple: java LancerServiceRestaurant 127.0.0.1 \"VOTRE_ID_RESTAURANT\"");
            return;
        }

        String ipCentral = args[0];
        String idRestaurant = args[1];

        try {
            Requete requete = new Requete();
            Restaurant monRestaurant = requete.getRestaurantById(idRestaurant);

            if (monRestaurant == null) {
                System.err.println("ERREUR: Impossible de trouver un restaurant avec l'ID : " + idRestaurant);
                return;
            }

            System.out.println("Démarrage du service pour le restaurant: " + monRestaurant.getName());

            Registry reg = LocateRegistry.getRegistry(ipCentral, 1099);
            ServiceDistributeur central = (ServiceDistributeur) reg.lookup("ServiceCentral");
            ServiceRestaurant sr = new ServiceRestaurantImpl(monRestaurant);

            central.enregistrerClient(sr);
            System.out.println("Service restaurant démarré et enregistré auprès du Central.");
            System.out.println("Détails du restaurant (Test local) :");
            System.out.println(sr.getRestaurantDetails());
            
        } catch (Exception e) {
            System.err.println("Erreur critique lors du lancement du service restaurant:");
            e.printStackTrace();
        }
    }
}
