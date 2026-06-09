package RMI.model;

public class Table {
    private String id;
    private String idRes;
    private String numTable;
    private int nbPlaces;

    public Table(String id, String idRes, String numTable, int nbPlaces) {
        this.id = id;
        this.idRes = idRes;
        this.numTable = numTable;
        this.nbPlaces = nbPlaces;
    }

    public String getId() {
        return id;
    }

    public String getIdRes() {
        return idRes;
    }

    public String getNumTable() {
        return numTable;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setId(String id) {
        this.id = id;
    }
}
