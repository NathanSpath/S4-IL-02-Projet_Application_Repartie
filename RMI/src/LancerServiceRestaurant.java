
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LancerServiceRestaurant {
   public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.getRegistry(args[0], 1099);
            ServiceDistributeur central = (ServiceDistributeur) reg.lookup("ServiceCentral");
            ServiceRestaurant sr = (ServiceRestaurant) new ServiceRestaurantImpl();
            central.enregistrerClient(sr);
            
            System.out.println("Service restaurant démarré et enregistré auprès du Central.");
            System.out.println("Demande restaurant");
            System.out.println(sr.getRestaurant());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}