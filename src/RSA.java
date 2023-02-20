import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;

public class RSA {
    KeyPairGenerator keyGen;
    KeyPair pair;
    Cipher cipher;
    byte[] encrypted;

    public RSA(){
        try{
            this.keyGen = KeyPairGenerator.getInstance("RSA");
            this.keyGen.initialize(1024);
            this.pair = this.keyGen.generateKeyPair();
            this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            System.out.println(this.pair.getPrivate());
            System.out.println(this.pair.getPublic());
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public byte[] Encrypt(String plainText) {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.pair.getPrivate());
            this.encrypted = cipher.doFinal(plainText.getBytes());
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return this.encrypted;
    }

    public String Decrypt(byte[] encrypted) {
        String decryptedText = "";
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.pair.getPublic());
            decryptedText = new String(cipher.doFinal(encrypted));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return decryptedText;
    }

}