package database;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class main {
    public static void main(String[] args) {

        System.out.println("--- Vérification des drivers JDBC disponibles ---");
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            if (!drivers.hasMoreElements()) {
                if (!drivers.hasMoreElements()) {
                    System.out.println("ERREUR: Aucun driver JDBC n'est enregistré !");
                } else {
                    while (drivers.hasMoreElements()) {
                        Driver d = drivers.nextElement();
                        System.out.println("Driver trouvé: " + d.getClass().getName());
                    }
                }
            }
        } catch(Exception e){
            System.out.println("Erreur lors de la vérification des drivers: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("-------------------------------------------------");
        System.out.println("Tentative de connexion...");
        Requete r = new Requete();

        r.executerMaRequete();
    }
}