
class PaquetCreerCompte extends Paquet {
    private String pseudo;
    private String clefPublique;
    private String clefPriveeCryptee;
    public PaquetCreerCompte(String pseudo,String clefPublique,String clefPriveeCryptee) {
        super(Paquet.DELETE_ACCOUNT);
        this.pseudo = pseudo;
        this.clefPublique = clefPublique;
        this.clefPriveeCryptee = clefPriveeCryptee;
    }

    public String getPseudo() {
        return pseudo;
    }
}