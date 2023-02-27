
class PaquetDeleteAccount extends Paquet {
    private String username;
    public PaquetDeleteAccount(String username) {
        super(Paquet.DELETE_ACCOUNT);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
