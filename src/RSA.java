import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSA {

    private static final String ALGORITHM = "RSA";
    private static KeyPairGenerator keyGen;
    private static KeyPair pair;
    public static PrivateKey privateKey;
    public static PublicKey publicKey;

    // Attention ici publickey et privatekey sont public ! On va devoir les générer et les stocker (dans la fonction main) En mode:  Au lancement du programme si le fichier clef.txt n'est pas présent, générer les 2 clefs)

    public RSA() throws Exception {
        keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        pair = keyGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    public static String encode(String message, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encoded = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static String decode(String message, PublicKey key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }



}