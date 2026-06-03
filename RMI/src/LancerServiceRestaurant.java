import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceRestaurant {
    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length < 1) {
            System.err.println("Usage: java LancerServiceRestaurant <ip_serveur_central>");
            return;
        }
        String ipCentral = args[0];

        try {
            Registry reg = LocateRegistry.getRegistry(ipCentral, 1099);
            ServiceDistributeur central = (ServiceDistributeur) reg.lookup("ServiceCentral");
            ServiceRestaurant sr = new ServiceRestaurantImpl();

            central.enregistrerClient(sr);
            System.out.println("Service restaurant démarré et enregistré auprès du Central.");
            System.out.println("Détails du restaurant (Test local) :");
            System.out.println(sr.getRestaurants());
            System.out.println(sr.creerReservation("5356ED08A4E80A22E06338AAD6C21047","5356ED08A4EB0A22E06338AAD6C21047",2));
            
        } catch (Exception e) {
            System.err.println("Erreur critique lors du lancement du service restaurant:");
            e.printStackTrace();
        }
    }
}
