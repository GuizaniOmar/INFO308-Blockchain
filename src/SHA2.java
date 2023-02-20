// Fonction permettant d'encrypter un fichier avec sha2
import java.security.MessageDigest;

public class SHA2 {
    private static String toHexString(byte[] bytes) {
        StringBuffer resultat = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            resultat.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return resultat.toString();
    }

    public static String encrypter(String message) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(message.getBytes());
            return toHexString(messageDigest.digest());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
