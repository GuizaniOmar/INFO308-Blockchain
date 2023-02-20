

public class Main {
    public static void main(String[] args) {

            try{
                RSA rsa = new RSA();

                String message = "Bonjour";
                System.out.println("Message à encoder: " + message);

                String encoded = rsa.encode(message, rsa.privateKey);
                System.out.println("Message encodé: " + encoded);

                String decoded = rsa.decode(encoded, rsa.publicKey);
                System.out.println("Message décodé: " + decoded);
            }catch(Exception e){
            System.out.println("Exception produite !");
        }




    }
}