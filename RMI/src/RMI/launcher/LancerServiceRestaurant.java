package RMI.launcher;

import RMI.rmi.ServiceDistributeur;
import RMI.rmi.ServiceRestaurant;
import RMI.rmi.ServiceRestaurantImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceRestaurant {
    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length < 1) {
            System.err.println("Usage: java RMI.launcher.LancerServiceRestaurant <ip_serveur_central>");
            return;
        }
        String ipCentral = args[0];

        try {
            Registry reg = LocateRegistry.getRegistry(ipCentral, 1099);
            ServiceDistributeur central = (ServiceDistributeur) reg.lookup("RMI.rmi.ServiceCentral");
            ServiceRestaurant sr = new ServiceRestaurantImpl();

            central.enregistrerClient(sr);
            System.out.println("Service restaurant démarré et enregistré auprès du Central.");
            System.out.println("Détails du restaurant (Test local) :");
            System.out.println(sr.getRestaurants());
            String tablesJson = sr.getTables();
            
        } catch (Exception e) {
            System.err.println("Erreur critique lors du lancement du service restaurant:");
            e.printStackTrace();
        }
    }
}
