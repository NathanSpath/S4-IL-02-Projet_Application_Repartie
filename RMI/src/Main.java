public class Main {
    public static void main(String[] args) {

        Requete requete = new Requete();

        System.out.println("--- Restaurants ---");
        Restaurant[] restaurants = requete.getRestaurants();
        if (restaurants != null && restaurants.length > 0) {
            for (Restaurant restaurant : restaurants) {
                System.out.println(restaurant.getName());
            }
            Restaurant restaurant = requete.getRestaurantById(restaurants[0].getId());
            if (restaurant != null) {
                System.out.println("Trouvé par ID : " + restaurant.getName());
            }
        } else {
            System.out.println("Aucun restaurant trouvé dans la base de données.");
        }

        System.out.println("\n--- Clients ---");
        Client[] clients = requete.getClients();
        if (clients != null && clients.length > 0) {
            for (Client client : clients) {
                System.out.println(client.getNom());
            }
            Client client = requete.getClientById(clients[0].getId());
            if (client != null) {
                System.out.println("Trouvé par ID : " + client.getNom());
            }
        } else {
            System.out.println("Aucun client trouvé dans la base de données.");
        }

        System.out.println("\n--- Réservations ---");
        Reservation[] reservations = requete.getReservations();
        if (reservations != null && reservations.length > 0) {
            for (Reservation reservation : reservations) {
                System.out.println("ID Client: " + reservation.getIdCli() + ", ID Restaurant: " + reservation.getIdRes());
            }
            Reservation reservation = requete.getReservationById(reservations[0].getIdCli(), reservations[0].getIdRes());
            if (reservation != null) {
                 System.out.println("Trouvé par ID : Client " + reservation.getIdCli() + ", Restaurant " + reservation.getIdRes());
            }
        } else {
            System.out.println("Aucune réservation trouvée dans la base de données.");
        }
    }
}
