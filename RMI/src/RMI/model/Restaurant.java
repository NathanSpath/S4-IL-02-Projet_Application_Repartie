package RMI.model;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String id;
    private String name;
    private String adresse;
    private String coordonnees;

    public Restaurant(String id, String name, String adresse ,String coordonnees) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.coordonnees = coordonnees;
    }

    public Restaurant(String name, String adresse ,String coordonnees) {
        this.name = name;
        this.adresse = adresse;
        this.coordonnees = coordonnees;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoordonnees() {
        return coordonnees;
    }

    public String getAdresse() { return adresse;}

    public void setId(String id) {
        this.id = id;
    }
}