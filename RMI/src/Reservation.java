import java.util.Date;
import java.util.UUID;

public class Reservation {
    private String id;
    private String idCli;
    private String idTab;
    private int nbConvives;
    private Date dateReservation;*

    /**
     * Constructeur pour créer une NOUVELLE réservation avant de l'insérer en BDD.
     * L'ID sera généré automatiquement.
     */
    public Reservation(String idCli, String idTab, int nbConvives) {
        this.id = null;
        this.idCli = idCli;
        this.idTab = idTab;
        this.nbConvives = nbConvives;
        this.dateReservation = new Date();
    }

    /**
     * Constructeur utilisé pour créer un objet à partir des données lues en BDD.
     */
    public Reservation(String id, String idCli, String idTab, int nbConvives, Date dateReservation) {
        this.id = id;
        this.idCli = idCli;
        this.idTab = idTab;
        this.nbConvives = nbConvives;
        this.dateReservation = dateReservation;
    }

    // Getters
    public String getId() { return id; }
    public String getIdCli() { return idCli; }
    public String getIdTab() { return idTab; }
    public int getNbConvives() { return nbConvives; }
    public Date getDateReservation() { return dateReservation; }

    //Setter (recuper l'id genrer par la bdd apres insersion)
    public void setId(String id) {
        this.id = id;
    }
}
