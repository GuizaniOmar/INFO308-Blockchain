

public class Main {
    public static void main(String[] args) {
        RSA testing = new RSA();
        byte[] testencrypter;
        String testdecrypter = new String("");
        testencrypter = testing.Encrypt("Coucou on va encrypté");
        System.out.println(new String(testing.Encrypt("Coucou on va encrypté")));
        testdecrypter  = testing.Decrypt(testencrypter);
        System.out.println("Résultat du texte décrypté:");
        System.out.println(testdecrypter);

    }
}