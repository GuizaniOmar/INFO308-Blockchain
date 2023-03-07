
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
public class RSA {

    private static final String ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding";
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
    public RSA(String privKey, String pubKey) throws Exception {
        keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);


        privateKey = RSA.privateKeyFromString(privKey);
        publicKey = RSA.publicKeyFromString(pubKey);

        if (publicKey == null ||  privateKey == null) {
            System.out.println("Erreur : Les clefs entrées ne sont pas correctes ! Nous allons te générer une autre pair de clef" );
            pair = keyGen.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }


    }
    public static String encode(String message, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encoded = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static String decode(String message, PublicKey key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
    // Fonction pour convertir la privatekey en string en format base64
    public static String privateKeyToString() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Fonction pour convertir la publickey en string en format base64
    public static String publicKeyToString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static PublicKey publicKeyFromString(String publicKeyString) throws Exception {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            System.out.println("Error converting Base64 encoded public key to PublicKey: " + e.getMessage());
            return null;
        }
    }
    public static PrivateKey privateKeyFromString(String privateKeyString) throws Exception {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            System.out.println("Error converting Base64 encoded private key to PrivateKey: " + e.getMessage());
            return null;
        }
    }
}