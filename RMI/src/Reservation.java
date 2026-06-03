import java.util.Date;
import java.util.UUID;

public class Reservation {
    private String id;
    private String idCli;
    private String idTab;
    private int nbConvives;
    private Date dateReservation;
    private double duree;//en heure

    /**
     * Constructeur pour créer une NOUVELLE réservation avant de l'insérer en BDD.
     * L'ID sera généré automatiquement.
     */
    public Reservation(String idCli, String idTab, int nbConvives, double duree) {
        this.id = null;
        this.idCli = idCli;
        this.idTab = idTab;
        this.nbConvives = nbConvives;
        this.duree = duree;
        this.dateReservation = new Date();
    }

    /**
     * Constructeur utilisé pour créer un objet à partir des données lues en BDD.
     */
    public Reservation(String id, String idCli, String idTab, int nbConvives, double duree, Date dateReservation) {
        this.id = id;
        this.idCli = idCli;
        this.idTab = idTab;
        this.nbConvives = nbConvives;
        this.duree = duree;
        this.dateReservation = dateReservation;
    }

    // Getters
    public String getId() { return id; }
    public String getIdCli() { return idCli; }
    public String getIdTab() { return idTab; }
    public int getNbConvives() { return nbConvives; }
    public Date getDateReservation() { return dateReservation; }
    public double getDuree() { return duree; }


    //Setter (recuper l'id genrer par la bdd apres insersion)
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Vérifie si cette réservation est en conflit de temps avec une autre réservation (r).
     * @param r L'autre réservation à comparer.
     * @return true si elles sont en conflit (se chevauchent), false si elles ne se chevauchent pas.
     */
    public boolean estEnConflitAvec(Reservation r) {
        long thisDebutMillis = this.dateReservation.getTime();
        long thisFinMillis = thisDebutMillis + (long)(this.duree * 3600000); 

        long otherDebutMillis = r.getDateReservation().getTime();
        long otherFinMillis = otherDebutMillis + (long)(r.getDuree() * 3600000);

        boolean pasDeConflit = (thisFinMillis <= otherDebutMillis) || (thisDebutMillis >= otherFinMillis);

        return !pasDeConflit;
    }
}