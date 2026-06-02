public class Reservation {
    private String idCli;
    private String idRes;
    private int nbConvives;

    public Reservation(String idCli, String idRes, int nbConvives) {
        this.idCli = idCli;
        this.idRes = idRes;
        this.nbConvives = nbConvives;
    }

    public String getIdCli() {
        return idCli;
    }

    public String getIdRes() {
        return idRes;
    }

    public int getNbConvives() {
        return nbConvives;
    }

}
