
import javax.print.attribute.HashPrintJobAttributeSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client  {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;



    public Client(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void login(String username, String password) throws IOException {
        //    PaquetLogin paquet = new PaquetLogin(username, password);
        //   outputStream.writeInt(paquet.getType());
        // outputStream.writeUTF(paquet.getUsername());
        // outputStream.writeUTF(paquet.getPassword());
        //outputStream.flush();
        //String response = inputStream.readUTF();
        //System.out.println("Réponse du serveur pour la connexion: " + response);
    }
    public String json() throws IOException {
        String msg = "Message vide";
        PaquetJson paquet = new PaquetJson(msg);
        outputStream.writeInt(paquet.getType());
        outputStream.writeUTF(paquet.getMsg());
        outputStream.flush();
        int idresponse = inputStream.readInt();
        String response = inputStream.readUTF();
        List<String> list = Json.extraireMots(response);
        int c;
        for (c = 0;c<list.size();c++){
            //     System.out.println("Element " + c + " : " + list.get(c));
            // On tente d'ajouter l'élement dans une base de données !
            Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
            String pseudo = (String) obj.get("Pseudo");
            String clefPriveeCryptee = (String) obj.get("ClefPriveeCryptee");
            String clefPublique = (String) obj.get("ClefPublique");
            if ((pseudo != null) && (clefPriveeCryptee != null) && (clefPublique != null)) {
                System.out.println("pseudo: " + pseudo + "clefpriveecryptee: " + clefPriveeCryptee + "clefpublique: " + clefPublique );
            }else {
                System.out.println("ERREUR ! " + "pseudo: " + pseudo + "clefpriveecryptee: " + clefPriveeCryptee + "clefpublique: " + clefPublique );
            }
            // CODE POUR RAJOUTER A LA BASE DE DONNEES...
        }
        return response;
    }

}

