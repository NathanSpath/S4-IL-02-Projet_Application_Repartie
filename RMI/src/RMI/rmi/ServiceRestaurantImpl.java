package RMI.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;

import RMI.model.Client;
import RMI.model.Reservation;
import RMI.model.Restaurant;
import RMI.model.Table;
import RMI.service.ClientService;
import RMI.service.RestaurantService;
import RMI.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceRestaurantImpl extends UnicastRemoteObject implements ServiceRestaurant {
    private final ClientService clientService;
    private final RestaurantService restaurantService;
    private final ReservationService reservationService;

    public ServiceRestaurantImpl(ClientService clientService, RestaurantService restaurantService, ReservationService reservationService) throws RemoteException {
        super();
        this.clientService = clientService;
        this.restaurantService = restaurantService;
        this.reservationService = reservationService;
    }

    @Override
    public String getRestaurants() throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult;
        try {
            Restaurant[] restaurants = restaurantService.getAllRestaurants();
            jsonResult = mapper.writeValueAsString(restaurants);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON des restaurants: " + e.getMessage());
            throw new RemoteException("Erreur interne lors de la récupération des détails.", e);
        }
        System.out.println("Restaurants récupérés et sérialisés en JSON: " + jsonResult);
        return jsonResult;
    }

    @Override
    public String creerReservation(String idTab, String nom, String prenom, String num, int nbPersonnes, double duree, Timestamp dateReservation) throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String idClient = clientService.getClient(nom,prenom,num).getId();
        Reservation r = new Reservation(idClient,idTab,nbPersonnes,duree,dateReservation);
        boolean success;
        try {
            success = reservationService.addReservation(r) != null;
        } catch (SQLException e) {
            System.err.println("Erreur BDD ou métier lors de la réservation: " + e.getMessage());
            throw new RemoteException("Échec de la réservation: " + e.getMessage(), e);
        }
        String jsonResult;
        try {
            jsonResult = mapper.writeValueAsString(success);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON du résultat de réservation: " + e.getMessage());
            throw new RemoteException("Erreur interne lors de la sérialisation du résultat.", e);
        }
        System.out.println(jsonResult);
        return jsonResult;
    }

    @Override
    public String getReservations(String nom, String prenom, String numTel) throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult;
        try {
            String clientId = null;
            Client client = clientService.getClient(nom, prenom, numTel);
            if (client != null) {
                clientId = client.getId();
            } else {
                return mapper.writeValueAsString(new Reservation[0]);
            }

            Reservation[] reservations = reservationService.getReservationByClient(clientId);
            jsonResult = mapper.writeValueAsString(reservations);

        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON des réservations: " + e.getMessage());
            throw new RemoteException("Erreur interne lors de la récupération des détails.", e);
        }
        return jsonResult;
    }

    @Override
    public String getAllReservation() throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult;
        try {
            Reservation[] reservations = reservationService.getReservations();
            jsonResult = mapper.writeValueAsString(reservations);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON des restaurants: " + e.getMessage());
            throw new RemoteException("Erreur interne lors de la récupération des détails.", e);
        }
        System.out.println("Restaurants récupérés et sérialisés en JSON: " + jsonResult);
        return jsonResult;
    }

    @Override
    public String getTables() throws RemoteException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult;
        try {
            Table[] tables = restaurantService.getTables();
            jsonResult = mapper.writeValueAsString(tables);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur de sérialisation JSON des tables: " + e.getMessage());
            throw new RemoteException("Erreur interne lors de la récupération des détails.", e);
        }
        return jsonResult;
    }
}
