package RMI.launcher;

import RMI.rmi.ServiceCentral;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceCentral {
    public static void main(String[] args) {
        try {
            Registry reg;
            try {
                reg = LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                reg = LocateRegistry.getRegistry(1099);
            }

            ServiceCentral central = new ServiceCentral();
            reg.rebind("RMI.rmi.ServiceCentral", central);

            System.out.println("Service Central prêt et enregistré dans le Registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}