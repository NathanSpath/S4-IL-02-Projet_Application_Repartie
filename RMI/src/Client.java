public class Client {
    private String id;
    private String nom;
    private String prenom;
    private String numTel;

    public Client(String id, String nom, String prenom, String numTel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numTel = numTel;
    }

    public Client(String nom, String prenom, String numTel) {
        this.nom = nom;
        this.prenom = prenom;
        this.numTel = numTel;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setId(String id) {
        this.id = id;
    }
}
