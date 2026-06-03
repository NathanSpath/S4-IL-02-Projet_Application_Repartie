import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceRestaurantImpl extends UnicastRemoteObject implements ServiceRestaurant {
    
    public ServiceRestaurantImpl() throws RemoteException {
        super();
    }

    @Override
    public String getRestaurant() throws RemoteException {
        // Implémentation pour récupérer la liste des restaurants
        Requete requete = new Requete();
        ObjectMapper mapper = new ObjectMapper();
        Restaurant[] listeResto = requete.getRestaurants();
        String jsonResult = "";
        try {
            jsonResult = mapper.writeValueAsString(listeResto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonResult;
    }

    @Override
    public String reservation(String restaurantId, String idClient,int nbPersonnes) throws RemoteException {
        Requete requete = new Requete();
        ObjectMapper mapper = new ObjectMapper();
        Reservation r = new Reservation(restaurantId, idClient,nbPersonnes);
        Boolean result = requete.addReservation(r);
        String jsonResult = "";
        try{
            jsonResult = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonResult; // Retourne true si la réservation est réussie, sinon false
    }
}