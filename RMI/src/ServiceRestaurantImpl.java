import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ServiceRestaurantImpl extends UnicastRemoteObject implements ServiceRestaurant {
    
    public ServiceRestaurantImpl() throws RemoteException {
        super();
    }

    @Override
    public Restaurant getRestaurantById(String id) throws RemoteException {
        
        // Implémentation pour récupérer un restaurant par son ID
        return new Restaurant(id, "Nom du Restaurant", "Coordonnées du Restaurant");
    }

    @Override
    public Boolean reservation(String restaurantId, String date) throws RemoteException {
        // Implémentation pour effectuer une réservation
        return true; // Retourne true si la réservation est réussie, sinon false
    }
}