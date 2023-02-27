import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private Map<String, DataOutputStream> clients;
    private ServerSocket serverSocket;
    private List<String> authorizedUsernames = List.of("user1", "user2", "user3");

    public Server(int port) throws IOException {
        clients = new HashMap<>();
        serverSocket = new ServerSocket(port);
        System.out.println("Le serveur est en écoute sur le port " + port + "...");
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (Exception e) {
                    System.out.println("Erreur de connexion");
                    //  e.printStackTrace();
                }
            }
        }).start();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int type = inputStream.readInt();
                    switch (type) {
                        case 1:
                            handleLogin();
                            break;
                        case 2:
                            handleDeleteAccount();
                            break;
                        case 3:
                            handleCreerCompte();
                            break;
                        case 4:
                            handleJson();
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur de connexion #2");
                // e.printStackTrace();
            }
        }

        private void handleLogin() throws IOException {
            // On va tenter de rajouter le compte dans la base de données...


            String username = inputStream.readUTF();
            String clefPublique = inputStream.readUTF();
            String clefPriveeCryptee = inputStream.readUTF();
            System.out.println("Connexion de l'utilisateur " + username);
            DatabaseManager db = DatabaseManager.getInstance();

            String[] values = {username, clefPublique,clefPriveeCryptee};
            try {
                db.insert("PseudosClefs", new String[] {"Pseudo", "ClefPublique","ClefPriveeCryptee"}, values);
                outputStream.writeUTF("Ajout du compte réussie ! ");
            }catch(Exception e){

                outputStream.writeUTF("Ajout du compte raté ! " + e.getMessage());
            }
            // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
            ResultSet result = db.select("PseudosClefs", new String[]{"_id", "Pseudo", "ClefPublique", "ClefPriveeCryptee"}, "Pseudo = '" + username + "'");
            try {
                if (result.next()) {
                    outputStream.writeUTF("Connexion réussie");
                }
            } catch (Exception e) {
                outputStream.writeUTF("Le compte '" + username + "' n'existe pas");

            }

            outputStream.flush();

        }
        private void handleJson() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            DatabaseManager db = DatabaseManager.getInstance();

            // Créer une table de type PseudoClefs composées : d'un id, d'un pseudo, d'une clef publique et d'une clef privée encryptée pour garantir la sécurité(seul l'utilisateur connait la clef de décryptage de la clef)
            String[] colonnesPseudoClefs = {"_id", "Pseudo", "ClefPublique","ClefPriveeCryptee"};
            String[] typesPseudoClefs = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL UNIQUE", "TEXT NOT NULL UNIQUE","TEXT"};
            db.createTable("PseudosClefs", colonnesPseudoClefs, typesPseudoClefs);

            String[] colonnesParties = {"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
            db.createTable("Parties", colonnesParties, typesParties);
            int k = 1;
            for (k = 100; k<10;k++){
                String[] values = {"miguel"+k,"ClefPublique"+k,"ClefPriveeCryptee"+k};
                db.insert("PseudosClefs", new String[] {"Pseudo", "ClefPublique","ClefPriveeCryptee"}, values);


            }

            // On prépare le hash
            String contenue = "";
            try {
                ResultSet result = db.select("PseudosClefs", new String[]{"_id", "Pseudo", "ClefPublique", "ClefPriveeCryptee"}, "Pseudo Like '%'");
                try {
                    int i=0;
                    while (result.next()) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("ID", result.getInt("_id"));
                        map2.put("Pseudo", result.getString("Pseudo"));
                        map2.put("ClefPublique", result.getString("ClefPublique"));
                        map2.put("ClefPriveeCryptee", result.getString("ClefPriveeCryptee"));
                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PseudoClefs");
            }

            // Provisoire après ça sera amélioré

            System.out.println("Json reçu de quelqu'un");
            PaquetJson paquet = new PaquetJson(contenue);
            outputStream.writeInt(paquet.getType());
            outputStream.writeUTF(paquet.getMsg());
            System.out.println(paquet.getMsg());
            outputStream.flush();

        }
        private void handleCreerCompte() throws IOException {
            String pseudo = inputStream.readUTF();
            String clefPublique = inputStream.readUTF();
            String clefPrivee = inputStream.readUTF();
            System.out.println("Connexion de l'utilisateur " + pseudo + " avec la clef publique " + clefPublique);
            // Je mets la vérification dans la base de donnée ! et ça ira :D
            if (authorizedUsernames.contains(pseudo)) {
                clients.put(pseudo, outputStream);
                outputStream.writeUTF("Connexion réussie");
            } else {
                outputStream.writeUTF("Connexion échouée : nom d'utilisateur");
            }

        }
        private void handleDeleteAccount() throws IOException {
            String username = inputStream.readUTF();
            if (clients.containsKey(username)) {
                clients.remove(username);
                System.out.println("Suppression du compte de l'utilisateur " + username);
                outputStream.writeUTF("Compte supprimé avec succès");
            } else {
                outputStream.writeUTF("Impossible de supprimer le compte : utilisateur non connecté");
            }
        }
    }
}