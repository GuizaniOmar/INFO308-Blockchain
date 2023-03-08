import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSAPSS {

    // Déclarer les variables privées pour stocker la clef publique et la clef privée
    public static PublicKey publicKey;
    private static final String ALGORITHM = "RSASSA-PSS";
    public static PrivateKey privateKey;

    // Générer une paire de clés RSA
    public  RSAPSS() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    // Encoder un message avec RSASSA-PSS
    public static String encode(String message, PrivateKey privateKey) throws Exception {
        byte[] messageBytes = message.getBytes();
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
        signature.initSign(privateKey);
        signature.update(messageBytes);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    // Décoder un message avec RSASSA-PSS
    public static boolean decode(String message, String signatureBase64,PublicKey publicKey) throws Exception {
        byte[] messageBytes = message.getBytes();
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
        signature.initVerify(publicKey);
        signature.update(messageBytes);
        return signature.verify(signatureBytes);
    }

    // Getter de clef publique
    public PublicKey getPublicKey() {
        return publicKey;
    }

    // Getter de clef privée
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
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