package RMI.launcher;

import RMI.dao.Requete;
import RMI.rmi.ServiceCentral;
import RMI.rmi.ServiceRestaurantImpl;
import RMI.service.ClientService;
import RMI.service.ReservationService;
import RMI.service.RestaurantService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceRestaurant {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java RMI.launcher.LancerServiceRestaurant <nom_du_service> <ip_serveur_central>");
            return;
        }
        String serviceName = args[0];
        String ipCentral = args[0];

        try {
            Requete requeteDAO = new Requete();
            ClientService clientService = new ClientService(requeteDAO);
            RestaurantService restaurantService = new RestaurantService(requeteDAO);
            ReservationService reservationService = new ReservationService(requeteDAO);

            ServiceRestaurantImpl serviceRestaurant = new ServiceRestaurantImpl(clientService, restaurantService, reservationService);


            System.out.println("Recherche du service central à l'adresse : " + ipCentral);
            Registry centralRegistry = LocateRegistry.getRegistry(ipCentral, 1099);
            ServiceCentral serviceCentralDistant = (ServiceCentral) centralRegistry.lookup("ServiceCentral");

            serviceCentralDistant.enregistrerClient(serviceRestaurant);
            System.out.println("Service '" + serviceName + "' enregistré avec succès auprès du service central.");

        } catch (Exception e) {
            System.err.println("Erreur lors du lancement ou de l'enregistrement du ServiceRestaurant : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
