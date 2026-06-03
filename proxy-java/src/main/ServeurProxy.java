package main;

import com.sun.net.httpserver.HttpServer;
import handlers.IncidentsHandler;
import handlers.ReservationHandler;
import handlers.RestaurantHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServeurProxy {

    public static void main(String[] args) throws IOException {
        // Démarrage du serveur sur le port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Déclaration des routes (URLs)
        server.createContext("/api/incidents", new IncidentsHandler());
        server.createContext("/api/restaurants", new RestaurantHandler());
        server.createContext("/api/reservations", new ReservationHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ Serveur Proxy démarré sur le port 8080 !");
        System.out.println("👉 Testez les incidents sur : http://localhost:8080/api/incidents");
    }
}