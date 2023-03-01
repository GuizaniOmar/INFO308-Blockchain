class Paquet {
    private int type;

    public static final int LOGIN = 1;
    public static final int PAQUET_A_SIGNER = 2;
    public static final int SEND_GAME = 3;
    public static final int JSON = 4;
    public Paquet(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}