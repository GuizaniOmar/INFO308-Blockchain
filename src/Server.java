import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
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

        int backlog = 50;
        ServerSocket serverSocket = new ServerSocket(port, backlog);

        // serverSocket = new ServerSocket(port);
        DatabaseManager db = DatabaseManager.getInstance();

        // Créer une table de type PseudoClefs composées : d'un id, d'un pseudo, d'une clef publique et d'une clef privée encryptée pour garantir la sécurité(seul l'utilisateur connait la clef de décryptage de la clef)
        String[] colonnesPseudoClefs = {"_id", "Pseudo", "ClefPublique","ClefPriveeCryptee"};
        String[] typesPseudoClefs = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL UNIQUE", "TEXT NOT NULL UNIQUE","TEXT"};
        db.createTable("PseudosClefs", colonnesPseudoClefs, typesPseudoClefs);

        String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"};
        String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT"};
        db.createTable("Parties", colonnesParties, typesParties);

        System.out.println("Le serveur est en écoute sur le port " + port + "...");
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (Exception e) {
                    System.out.println("Serveur: La connexion n'a pas pu être accepté");
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
                            handlePartiesASigner();
                            break;
                        case 3:
                            handleEnvoyerPartie();
                            break;
                        case 4:
                            handleJson(); //Envoie
                            break;
                        case 5:
                            handleAjouterSignature();
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
            System.out.println("ClefPublique: " + clefPublique);
            DatabaseManager db = DatabaseManager.getInstance();

            String[] values = {username, clefPublique,clefPriveeCryptee};
            try {
                if (db.insert("PseudosClefs", new String[] {"Pseudo", "ClefPublique","ClefPriveeCryptee"}, values) == true){  outputStream.writeUTF("Ajout du compte réussie ! ");}
                else{ outputStream.writeUTF("Ajout du compte raté ! "); }

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
        private void handleEnvoyerPartie() throws IOException {
            // On va tenter de rajouter le compte dans la base de données...
            DatabaseManager db = DatabaseManager.getInstance();

            // Créer une table de type PseudoClefs composées : d'un id, d'un pseudo, d'une clef publique et d'une clef privée encryptée pour garantir la sécurité(seul l'utilisateur connait la clef de décryptage de la clef)
            String[] colonnesPseudoClefs = {"_id", "Pseudo", "ClefPublique","ClefPriveeCryptee"};
            String[] typesPseudoClefs = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL UNIQUE", "TEXT NOT NULL UNIQUE","TEXT"};
            db.createTable("PseudosClefs", colonnesPseudoClefs, typesPseudoClefs);

            String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT"};
            db.createTable("Parties", colonnesParties, typesParties);

            String timestampPartie = inputStream.readUTF();
            String hashPartie = inputStream.readUTF();
            String clefPubliqueJ1 = inputStream.readUTF();
            String clefPubliqueJ2 = inputStream.readUTF();
            String clefPubliqueArbitre = inputStream.readUTF();

            System.out.println("timestampPartie :" + timestampPartie );
            System.out.println("hashPartie :" + hashPartie );
            System.out.println("J1 :" + clefPubliqueJ1 );
            System.out.println("J2 :" + clefPubliqueJ2 );
            System.out.println("Arbitre :" + clefPubliqueArbitre );

            outputStream.writeInt(Paquet.SEND_GAME);
            String[] values = {timestampPartie, hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,"","","","","",""};
            try {
                db.insert("Parties", new String[] {"Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"}, values);
                outputStream.writeUTF("Envoie de création de partie reçues ! ");
            }catch(Exception e){

                outputStream.writeUTF("Ajout de la partie  raté ! " + e.getMessage());
            }
            // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
            ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"}, "HashPartie = '" + hashPartie + "'");
            try {
                if (result.next()) {
                    outputStream.writeUTF("Ajout de la partie "+hashPartie+" réussie");
                }
            } catch (Exception e) {
                outputStream.writeUTF("La partie  '" + hashPartie + "' n'existe pas");

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

            String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT"};
            db.createTable("Parties", colonnesParties, typesParties);

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
        private void handlePartiesASigner() throws IOException {

            DatabaseManager db = DatabaseManager.getInstance();



            // On prépare le hash
            String contenue = "";
            try {
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"}, "Timestamp Like '%'");
                try {
                    int i=0;
                    while (result.next()) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("ID", result.getInt("_id"));
                        map2.put("Timestamp", result.getString("Timestamp"));
                        map2.put("HashPartie", result.getString("HashPartie"));
                        map2.put("ClefPubliqueJ1", result.getString("ClefPubliqueJ1"));
                        map2.put("ClefPubliqueJ2", result.getString("ClefPubliqueJ2"));
                        map2.put("ClefPubliqueArbitre", result.getString("ClefPubliqueArbitre"));
                        map2.put("VoteJ1", result.getString("VoteJ1"));
                        map2.put("VoteJ2", result.getString("VoteJ2"));
                        map2.put("VoteArbitre", result.getString("VoteArbitre"));
                        map2.put("SignatureJ1", result.getString("SignatureJ1"));
                        map2.put("SignatureJ2", result.getString("SignatureJ2"));
                        map2.put("SignatureArbitre", result.getString("SignatureArbitre"));

                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur Parties - PartiesASigner" + e.getMessage());
            }

            // Provisoire après ça sera amélioré

            System.out.println("Serveur: Liste de parties envoyés au client");
            PaquetJson paquet = new PaquetJson(contenue);
            outputStream.writeInt(paquet.getType());
            outputStream.writeUTF(paquet.getMsg());
            System.out.println(paquet.getMsg());
            outputStream.flush();

        }

        private void handleAjouterSignature() throws Exception {
            // On va tenter de rajouter le compte dans la base de données...
            String hashPartie = inputStream.readUTF();

            String acteurSignature = inputStream.readUTF();
            String vote = inputStream.readUTF();
            String signature = inputStream.readUTF();
            System.out.println("hashPartie : " + hashPartie);
            System.out.println("acteurSignature : " + acteurSignature);
            System.out.println("vote : " + vote);
            System.out.println("signature : " + signature);

            outputStream.writeInt(Paquet.SIGNATURE);

            if (PaquetAjouterSignature.isVoteActeur("Vote"+acteurSignature)){
                DatabaseManager db = DatabaseManager.getInstance();

                try {
                    ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"}, "HashPartie = '" + hashPartie + "'");
                    if (result.next()){
                        String nomColonneClef = PaquetAjouterSignature.getClefActeur("Vote"+acteurSignature);
                        System.out.println("nomColonneClef : " + nomColonneClef);
                        if (nomColonneClef != null){

                            String ClefPublique = result.getString(nomColonneClef);
                            boolean voteEstCorrect = RSAPSS.decode(vote,signature, RSAPSS.publicKeyFromString(ClefPublique));

                            System.out.println("ClefPublique : " + ClefPublique);
                            System.out.println("vote : " + vote);
                            System.out.println("signature : " + signature);
                            System.out.println("signature correct : " + voteEstCorrect);



                            if (voteEstCorrect){
                                db.update("Parties", new String[]{"Signature" + acteurSignature,"Vote"+acteurSignature}, new String[]{signature,vote}, "HashPartie = '" + hashPartie + "'");
                                outputStream.writeUTF("Signature ajoutée ! ");
                            }else{
                                outputStream.writeUTF("La signature est incorrecte ! ");
                            }
                        } else{
                            System.out.println("Le nom de l'acteur est incorrect ! ");
                            outputStream.writeUTF("Le nom de l'acteur est incorrect ! ");
                        }

                    }else
                        outputStream.writeUTF("La partie n'existe pas ! ");


                }catch(Exception e){

                    outputStream.writeUTF("Ajout de la signature raté #1 " + e.getMessage());
                }
                // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre","SignatureJ1","SignatureJ2","SignatureArbitre"}, "HashPartie = '" + hashPartie + "'");
                try {
                    if (result.next()) {
                        String voteClef = result.getString("Vote"+acteurSignature);
                        String signatureVote = result.getString("Signature"+acteurSignature);
                        if (vote.equals(voteClef) && signature.equals(signatureVote))
                        outputStream.writeUTF("Ajout de la signature réussie ! ");
                        else
                            outputStream.writeUTF("L'update n'a pas été fait ! ");
                    }
                } catch (Exception e) {
                    outputStream.writeUTF("La partie avec le hash " + hashPartie + " n'existe pas ! ");

                }

                outputStream.flush();

            }
            else{
                outputStream.writeUTF("Erreur acteur signature");
                outputStream.writeUTF("Veuillez encoder un bon acteur");
                outputStream.flush();
            }


        }

    }
}