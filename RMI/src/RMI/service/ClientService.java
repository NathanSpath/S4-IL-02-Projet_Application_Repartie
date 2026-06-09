package RMI.service;

import RMI.dao.Requete;
import RMI.model.Client;

import java.sql.Connection;
import java.sql.SQLException;

public class ClientService {

    private final Requete requete;

    public ClientService(Requete requete) {
        this.requete = requete;
    }

    public Client[] getAllClients() {
        return requete.getClients();
    }

    public Client getClientById(String id) {
        return requete.getClientById(id);
    }

    // Nouvelle méthode pour récupérer un client par nom, prénom et numéro de téléphone
    public Client getClient(String nom, String prenom, int numTel) {
        return requete.getClient(nom, prenom, numTel);
    }

    /**
     * Ajoute un nouveau client en gérant la transaction.
     * @param client L'objet Client à ajouter.
     * @return Le client avec son ID généré.
     * @throws SQLException Si une erreur de base de données survient.
     */
    public Client addClient(Client client) throws SQLException {
        Connection conn = null;
        try {
            conn = Requete.getConnection();
            conn.setAutoCommit(false); //mode transactionnelle activée
            requete.addClient(conn, client);
            conn.commit();
            return client;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // on propage l'exception
        } finally { //on ferme toujours la connection
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
