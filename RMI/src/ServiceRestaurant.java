import java.rmi.Remote;
import java.rmi.RemoteException;

interface ServiceRestaurant extends Remote {
    String getRestaurant() throws RemoteException;
    String reservation(String restaurantId, String idCli ,int nbPers) throws RemoteException;
}