import java.rmi.Remote;
import java.rmi.RemoteException;

interface ServiceRestaurant extends Remote {

    /**
     * Récupère les informations détaillées du restaurant (nom, adresse, etc.).
     * @return Une chaîne JSON représentant l'objet Restaurant.
     * @throws RemoteException
     */
    String getRestaurants() throws RemoteException;

    /**

     * @param idTab L'identifiant unique de la table à réserver.
     * @param idCli L'identifiant unique du client qui réserve.
     * @param nbPers Le nombre de personnes pour la réservation.
     * @return Une chaîne JSON représentant un booléen (true si la réservation a réussi, false sinon).
     * @throws RemoteException
     */
    String creerReservation(String idTab, String idCli, int nbPers, double duree) throws RemoteException;

    String getReservations(String nom, String prenom, int numTel) throws  RemoteException;

    String getTables() throws RemoteException;
}
