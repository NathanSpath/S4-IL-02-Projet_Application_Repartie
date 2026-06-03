import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceRestaurantImpl extends UnicastRemoteObject implements ServiceRestaurant {

    public ServiceRestaurantImpl() throws RemoteException {
        super();
    }

    @Override
    public String getRestaurants() throws RemoteException {
        Requete requete = new Requete();
        Restaurant[] restaurants = requete.getRestaurants();
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = "false";
        try {
            jsonResult = mapper.writeValueAsString(restaurants);
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
            success = requete.addReservation(r);
        } catch (Exception e) {
            System.err.println("Erreur lors de la réservation: " + e.getMessage());
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
    public String getReservations(String nom, String prenom, int numTel) throws RemoteException {
        Requete requete = new Requete();
        Reservation[] reservations = requete.getReservationByClient(requete.getIdClient(nom, prenom, numTel));
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = "false";
        try {
            jsonResult = mapper.writeValueAsString(reservations);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON: " + e.getMessage());
            return "{\"error\":\"Erreur interne lors de la récupération des détails.\"}";
        }
        return jsonResult;
    }


}
