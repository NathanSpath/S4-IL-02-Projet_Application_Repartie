import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceRestaurantImpl extends UnicastRemoteObject implements ServiceRestaurant {

    private final Restaurant restaurant;

    public ServiceRestaurantImpl(Restaurant restaurant) throws RemoteException {
        super();
        this.restaurant = restaurant;
    }

    @Override
    public String getRestaurantDetails() throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = "";
        try {
            jsonResult = mapper.writeValueAsString(this.restaurant);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON: " + e.getMessage());
            return "{\"error\":\"Erreur interne lors de la récupération des détails.\"}";
        }
        return jsonResult;
    }

    @Override
    public String creerReservation(String idTab, String idClient, int nbPersonnes) throws RemoteException {
        Requete requete = new Requete();
        ObjectMapper mapper = new ObjectMapper();
        Reservation r = new Reservation(idClient, idTab, nbPersonnes);
        boolean success = false;
        try {
            requete.addReservation(r); 
            success = true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la réservation: " + e.getMessage());
            success = false;
        }
        String jsonResult = "false";
        try {
            jsonResult = mapper.writeValueAsString(success);
        } catch (JsonProcessingException e) {
             System.err.println("Erreur de sérialisation JSON du résultat de réservation: " + e.getMessage());
        }
        return jsonResult;
    }

    @Override
    public boolean getRestaurant() {
        return false;
    }
}
