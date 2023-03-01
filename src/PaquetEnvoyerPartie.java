

public class PaquetEnvoyerPartie extends Paquet {
    private String ClefPubliqueJ1;
    private String ClefPubliqueJ2;
    private String ClefPubliqueArbitre;
    private String hashPartie;
    private String timestampPartie;

    public PaquetEnvoyerPartie(String timestampPartie, String hashPartie,String ClefPubliqueJ1, String ClefPubliqueJ2, String ClefPubliqueArbitre) {
        super(Paquet.PAQUET_A_SIGNER);
        this.ClefPubliqueJ1 = ClefPubliqueJ1;
        this.ClefPubliqueJ2 = ClefPubliqueJ2;
        this.ClefPubliqueArbitre = ClefPubliqueArbitre;
        this.hashPartie = hashPartie;
        this.timestampPartie = timestampPartie;
    }

    public String getClefPubliqueJ1() {
        return ClefPubliqueJ1;
    }

    public String getClefPubliqueJ2() {
        return ClefPubliqueJ2;
    }

    public String getClefPubliqueArbitre() {
        return ClefPubliqueArbitre;
    }

    public String getTimestampPartie() {
        return timestampPartie;
    }

    public String getHashPartie() {
        return hashPartie;
    }
}
