import java.rmi.Remote;
import java.rmi.RemoteException;

interface ServiceRestaurant extends Remote {
    String getRestaurantById(String id) throws RemoteException;
    Boolean reservation(String restaurantId, String date) throws RemoteException;
}