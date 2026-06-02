import java.io.Serializable;

public class Restaurant implements Serializable {
    private String id;
    private String name;
    private String coordonnees;

    public Restaurant(String id, String name, String coordonnees) {
        this.id = id;
        this.name = name;
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
}