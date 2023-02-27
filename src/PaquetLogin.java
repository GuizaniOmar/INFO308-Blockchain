
public class PaquetLogin extends Paquet{
    private String username;

    private String clefPublique;
    private String clefPriveeCryptee;
    public PaquetLogin(String username, String clefPublique, String clefPriveeCryptee) {
        super(Paquet.LOGIN);
        this.username = username;

        this.clefPriveeCryptee = clefPriveeCryptee;
        this.clefPublique = clefPublique;
    }

    public String getUsername() {
        return username;
    }


    public String getClefPublique() {
        return clefPublique;
    }
    public String getClefPriveeCryptee() {
        return clefPriveeCryptee;
    }
}

