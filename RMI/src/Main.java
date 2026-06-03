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
                System.out.println("\nTrouvé par ID : " + restaurant.getName());
            }
        } else {
            System.out.println("Aucun restaurant trouvé dans la base de données.");
        }


        System.out.println("\n3 restaurants page 1 ");
        Restaurant[] restaurants1 = requete.getRestaurants("NOM", 0, 3);
        if (restaurants1 != null && restaurants1.length > 0) {
            for (Restaurant restaurant : restaurants1) {
                System.out.println(restaurant.getName());
            }
        } else {
            System.out.println("Aucun restaurant trouvé dans la base de données.");
        }

        System.out.println("\n3 restaurants page 2 ");
        Restaurant[] restaurants2 = requete.getRestaurants("NOM", 1, 3);
        if (restaurants2 != null && restaurants2.length > 0) {
            for (Restaurant restaurant : restaurants2) {
                System.out.println(restaurant.getName());
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
                System.out.println("\nTrouvé par ID : " + client.getNom());
            }
        } else {
            System.out.println("Aucun client trouvé dans la base de données.");
        }




        System.out.println("\n--- Réservations ---");
        Reservation[] reservations = requete.getReservations();
        if (reservations != null && reservations.length > 0) {
            for (Reservation reservation : reservations) {
                System.out.println("ID Client: " + reservation.getIdCli() + ", ID Restaurant: " + reservation.getIdTab());
            }
            Reservation reservation = requete.getReservationById(reservations[0].getId());
            if (reservation != null) {
                 System.out.println("\nTrouvé par ID : Client " + reservation.getIdCli() + ", Restaurant " + reservation.getIdTab());
            }
        } else {
            System.out.println("Aucune réservation trouvée dans la base de données.");
        }
    }
}
