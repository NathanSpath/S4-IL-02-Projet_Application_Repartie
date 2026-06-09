package RMI.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceDistributeur extends Remote {
    // Retourne la liste des clients déjà connectés au moment de l'inscription
   void enregistrerClient(ServiceRestaurant sb) throws RemoteException;
}
