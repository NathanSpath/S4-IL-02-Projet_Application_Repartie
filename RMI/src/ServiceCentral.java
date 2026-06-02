import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServiceCentral extends UnicastRemoteObject implements ServiceDistributeur {
    
    private final ArrayList<ServiceRestaurant> restaurantsConnectes = new ArrayList<>();

    public ServiceCentral() throws RemoteException {
        super();
    }
    
    public synchronized void enregistrerClient(ServiceRestaurant restaurantStub) throws RemoteException {
        restaurantsConnectes.add(restaurantStub);
        System.out.println("Un nouveau restaurant s'est enregistré !");
        afficherClientsConnectes();
    }

    public void afficherClientsConnectes() {
        System.out.println("--- Clients Connectés ---");
        for (ServiceRestaurant r : restaurantsConnectes) {
            System.out.println("Clients distant : " + r.toString());
        }
    }
}