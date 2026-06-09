package RMI.service;

import RMI.dao.Requete;
import RMI.model.Restaurant;

import java.sql.Connection;
import java.sql.SQLException;

public class RestaurantService {

    private final Requete requete;

    public RestaurantService(Requete requete) {
        this.requete = requete;
    }

    public Restaurant[] getAllRestaurants() {
        return requete.getRestaurants();
    }

    public Restaurant getRestaurantById(String id) {
        return requete.getRestaurantById(id);
    }

    /**
     * Ajoute un nouveau restaurant en gérant la transaction.
     * @param restaurant L'objet Restaurant à ajouter.
     * @return Le restaurant avec son ID généré.
     * @throws SQLException Si une erreur de base de données survient.
     */
    public Restaurant addRestaurant(Restaurant restaurant) throws SQLException {
        Connection conn = null;
        try {
            conn = Requete.getConnection();
            conn.setAutoCommit(false); // mode transactionnelle activé
            requete.addRestaurant(conn, restaurant);
            conn.commit(); // Valider la transaction
            return restaurant;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Propager l'exception
        } finally {// Toujours fermer la connexion
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
