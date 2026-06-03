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
            System.out.println(sr.creerReservation("5359E3C5751F4174E06338AAD6C2F9AA","5359E3C575224174E06338AAD6C2F9AA",2,2));
            System.out.println(sr.creerReservation("5359E3C575204174E06338AAD6C2F9AA","5359E3C575224174E06338AAD6C2F9AA",8,2));
            System.out.println(sr.creerReservation("5359E3C575204174E06338AAD6C2F9AA","5359E3C575224174E06338AAD6C2F9AA",4,2));
            
        } catch (Exception e) {
            System.err.println("Erreur critique lors du lancement du service restaurant:");
            e.printStackTrace();
        }
    }
}
