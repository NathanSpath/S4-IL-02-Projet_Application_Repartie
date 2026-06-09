package RMI.service;

import RMI.dao.Requete;
import RMI.model.Reservation;
import RMI.model.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class ReservationService {

    private final Requete requete;

    public ReservationService(Requete requete) {
        this.requete = requete;
    }

    public Reservation[] getReservations() {
        return requete.getReservations();
    }

    public Reservation[] getReservationByClient(String idCli) {
        return requete.getReservationByClient(idCli);
    }

    public Reservation getReservationById(String idReservation) {
        return requete.getReservationById(idReservation);
    }

    /**
     * Ajoute une nouvelle réservation en gérant la transaction et la logique métier.
     * @param reservation L'objet Reservation à ajouter.
     * @return La réservation avec son ID généré.
     * @throws SQLException Si une erreur de base de données survient ou si une règle métier est violée.
     */
    public Reservation addReservation(Reservation reservation) throws SQLException {
        Connection conn = null;
        try {
            conn = Requete.getConnection();
            conn.setAutoCommit(false); // mode transactionnelle activé
            Table table = requete.getTableById(conn, reservation.getIdTab());
            if (table == null) {
                throw new SQLException("La table avec l'ID " + reservation.getIdTab() + " n'existe pas.");
            }
            //verifi de la capacité de resevation
            if (table.getNbPlaces() < reservation.getNbConvives()) {
                throw new SQLException("Pas assez de places. La table " + table.getNumTable() + " a " + table.getNbPlaces() + " places, mais " + reservation.getNbConvives() + " sont demandées.");
            }

            // Vérifier les conflits d'horaire
            Reservation[] reservationsExistantes = requete.getReservationByTableAndDate(conn, reservation.getIdTab(), reservation.getDateReservation());
            for (Reservation r : reservationsExistantes) {
                if (reservation.estEnConflitAvec(r)) {
                    throw new SQLException("Conflit d'horaire. Une réservation existe déjà à cette heure pour cette table.");
                }
            }
            //on effectue la requet esi elle est compatible avec les condition testé ci dessus
            requete.addReservation(conn, reservation);
            conn.commit(); // Valider la transaction
            return reservation;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // on propage l'exception
        } finally {  // on ferme toujours la connection
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
