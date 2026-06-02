import java.rmi.Remote;
import java.rmi.RemoteException;

interface ServiceRestaurant extends Remote {
    Restaurant getRestaurantById(String id) throws RemoteException;
    Boolean reservation(String restaurantId, String date) throws RemoteException;
}