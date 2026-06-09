package RMI.launcher;

import RMI.dao.Requete;
import RMI.service.ClientService;
import RMI.service.ReservationService;
import RMI.service.RestaurantService;


public class Main {
    public static void main(String[] args) {
        Requete requete = new Requete();
        ClientService clientService = new ClientService(requete);
        RestaurantService restaurantService = new RestaurantService(requete);
        ReservationService reservationService = new ReservationService(requete);

    }
}
