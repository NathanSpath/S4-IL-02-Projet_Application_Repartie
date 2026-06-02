public class Main {
    public static void main(String[] args) {

        Requete requete = new Requete();
        Restaurant[] restaurants = requete.getRestaurants();
        for (Restaurant restaurant : restaurants) {
            System.out.println(restaurant.getName());
        }
        Restaurant  restaurant = requete.getRestaurantById(restaurants[0].getId());
        System.out.println(restaurant.getName());

        Client[] clients = requete.getClients();
        for (Client client : clients) {
            System.out.println(client.getNom());
        }
        Client client = requete.getClientById(clients[0].getId());
        System.out.println(client.getNom());

        Reservation[] reservations = requete.getReservations();
        for (Reservation reservation : reservations) {
            System.out.println(reservation.getIdCli());
        }
        Reservation reservation = requete.getReservationById(reservations[0].getIdCli(), reservations[0].getIdRes());
        System.out.println(reservation.getIdCli());
    }
}
