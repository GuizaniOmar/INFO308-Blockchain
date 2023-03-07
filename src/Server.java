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

        String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
        String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
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

            String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
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
            String[] values = {timestampPartie, hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,"","",""};
            try {
                db.insert("Parties", new String[] {"Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, values);
                outputStream.writeUTF("Envoie de création de partie reçues ! ");
            }catch(Exception e){

                outputStream.writeUTF("Ajout de la partie  raté ! " + e.getMessage());
            }
            // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
            ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "HashPartie = '" + hashPartie + "'");
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

            String[] colonnesParties = {"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
            db.createTable("Parties", colonnesParties, typesParties);
            int k = 1;
            for (k = 190; k<10;k++){
                // String[] values = {"Miguel"+k, k+"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt5mo5LrPLpe36HIOGYNu+zO98TcsFwtI+AqiIHHV6R2D1n97PyOcgdwc7QroH7fW5szdXjzTEpiJRLJ2/zXMAhBv0o79XOHoP8cdjUp1hdkaOXmQEI8Zzawsbyd0pZhBLmayUodwkZMs7ie0JRDnEpTMcAcz1gFTmTjrcCE79BINXg+W1uuQ6QdEnyADDPnOf2DofuKh40KBHJkoSAwC31L+Xo5L5631+wEcR5Hz1LLN9KUDGUrD2AVfo+r/Y/UAUSQ2n7m4NMVjNaed5K6zMznZbRtadI0w0e12UBx2zBa7UQaapqWu1xmuiHd8BzbJyXwYU8283FlfU3igmtgJWQIDAQAB", k+ "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"};
                String[] values = {"Miguel"+k,"ClefPublique"+k,"ClefPriveeCryptee"+k};
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
        private void handlePartiesASigner() throws IOException {

            DatabaseManager db = DatabaseManager.getInstance();



            // On prépare le hash
            String contenue = "";
            try {
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "Timestamp Like '%'");
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

            System.out.println("Json envoyée à un client");
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
            String signature = inputStream.readUTF();
            outputStream.writeInt(Paquet.SIGNATURE);

            if (PaquetAjouterSignature.isVoteActeur(acteurSignature)){
                DatabaseManager db = DatabaseManager.getInstance();

                try {
                    ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "HashPartie = '" + hashPartie + "'");
                    if (result.next()){
                        String nomColonneClef = PaquetAjouterSignature.getClefActeur(acteurSignature);
                        System.out.println("nomColonneClef : " + nomColonneClef);
                        if (nomColonneClef != null){
                            String ClefPublique = result.getString(nomColonneClef);
                            System.out.println("ClefPublique : " + ClefPublique);
                            System.out.println("signature : " + signature);
                            System.out.println("vote crypter : " + RSA.encode("4f6e9abd8338f6dbb869e1a9c53b74455c4281dbc6edf6635875755303d72cf8-V",RSA.privateKeyFromString("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDCSY3+GOveA0K7yDKg2xFhGgKu/YiuikWLliaQl+q/p2fU9Ew5GsSi+3EdX4JOaUdffch1bmtjSafcfaAJ+RFelbGBVBDW41b3rEWg0UypnofJOgL43R/fWDk/FvGCaX3v0YHZjQSDGUsXUgJRUfbXA9ODhJZSzgpJj0KSVaFnLi0uzIr/i96F66wRrxJoJUD+eWWm5nzvWtyXcsZc/MZkvzPqKJhb5EjTB338bGo0YZM7FBVIcNOa3CuLTfSlkkUAIqDjNPwVI7Zt6SrdIHFFCz4JpEa6SW8Xg+F+jQ/u/P5q4HzGAYn5tiG6Amh7q5tManV3r4w3MYvyIeobDfSzAgMBAAECggEAUtRQ+NdfF1OEi9IZ44IBssgIBNqJ/v9sDsqAvnxF1FdmaN+N73p+Ao3393HUd+FiUE+ruoTMu5OXQOU96YKJ6S0gc+aHF1XsqcKEHL+eU2IGub9FrRt4jxtprH/3joWy7x7+oOUB23JZFSQdYNX0yk4TSqqn+71jnWC6HPDs0rOaz1FfS3we/Ml7nOGxhhvcDDQz7c6Qd5XAp5nTyxWK7vvX0e68IH3vAaJO2N1oVKxnDShRDtf0avNkcI3+3bshtbRMyTGFsOSW1JPnl8DwllJucWAC22LsSZG+z7agenNiO+rxXSIsX5u38D1YAXxAC9//wfvaxVBInHExkn+SQQKBgQDktaWkYzy8k0XI50WzOfgmHEnFghskj0pSC/WlJIMAWF0oUd7JqN5x+oNZhhJDNamQ+MXEfO7YNJFB5G5YqDWCIi8CL7v3ximorTiJSff6I4Axmk/PKh8ym+qt+tw/fLhnZmc1PLhndssS6ialKHyMDakEo8zGmEKxGKjq+Mvp+wKBgQDZeGrTb07tPKPU0FnGBaDvkA0ZnrS2Gy3Sg1Rqzi202s6KRwxvvw/QCGfYIMWsIBBDs5jEndUlxudXIDL+SZNytLO3WlGg2uKsb7SDtU+auZOB+RyAG6zStN0cMRGIIEiBU7yhXVi74At/SoOeGWvPqAOPha2oxEBYxapFuXcaqQKBgBakRPm5OVIR4l65RpUvr/lV45fCAZ3k8Z6dwHvQ2Yc3OEG9mSitpxfxjP9X3ob40QihGDMTizGsQpUbYDE2tdVkPPMidqFY5NbmDyrIP9xrl01R0YTYzq3b8ae3pgZC+p6B7MXAdSEJCaGdvKWtFGdpEsp6zNL8T93oaxCYANfxAoGBAKyKcs9QZ+GoFCA8Mo3/V0HbG9mw5pX5mHCGjEq1TORKGkbxufdLMuOOV73NuqgnRGBCtTmCNGPlnRSuUmcYvyjqIBCgU1V5dRD1C9bX2tHa3SLpH+iwjH0czLWaZNuJKZ2ab3Xj1www5U5YM3cTmktGdZTArhjRpHAKXF83mD0JAoGBAN+5zQ8w7qmnOwDNkHgYx14NiSKRhltmOW4gMXGswc/JdbOVN8GYNY4sDAy4pPgUXKlap0rr8rCDz1cLe5z4q23Y+6RZTlM0F4eO0m/JqVigZUzUzJey1mXyqD2EEgNra3Zh6QhFkpGUKHQUiQAqud5OP3oNba7ez6ojGlCsh2Ol")));


                             //   System.out.println("On va décoder : " + RSA.decode("beqAMKOyE6+Sb1XCxi/S69dUhb1cF6VDpVzWzANN39jO2M8gDycO09CAswS0ad20OPFfjRqAeAEG3NfwRWV6mlyH2jSfEI39Rde+aTflWwr3Elyfn+6eZrMiDmqSCnn9nD0K+zg1xmNa3DeFyz+8BxHuNr/yxPtTFmXUpLqCqf/wig7oT3Xb7OWNGZssHD4JGTmiK0JoHUd/i6Lh+gbJ2MQ4v31rE2WbCouYZ2wcf79PO4dw4ejaRESnwTtQrB609WGV1/j7pmivOjuu28tziMS+nan3UFAxI5KlGLR9dqD3It+J2v+8zk4/8wkG+wGyoqEJtyhFLdq4waKlFD8SwQ==",RSA.publicKeyFromString("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwkmN/hjr3gNCu8gyoNsRYRoCrv2IropFi5YmkJfqv6dn1PRMORrEovtxHV+CTmlHX33IdW5rY0mn3H2gCfkRXpWxgVQQ1uNW96xFoNFMqZ6HyToC+N0f31g5Pxbxgml979GB2Y0EgxlLF1ICUVH21wPTg4SWUs4KSY9CklWhZy4tLsyK/4veheusEa8SaCVA/nllpuZ871rcl3LGXPzGZL8z6iiYW+RI0wd9/GxqNGGTOxQVSHDTmtwri030pZJFACKg4zT8FSO2bekq3SBxRQs+CaRGuklvF4Phfo0P7vz+auB8xgGJ+bYhugJoe6ubTGp1d6+MNzGL8iHqGw30swIDAQAB")));
                              String  rsaDecode = RSA.decode(signature, RSA.publicKeyFromString(ClefPublique));



                            if (rsaDecode.contains(hashPartie)){
                                db.update("Parties", new String[]{acteurSignature}, new String[]{signature}, "HashPartie = '" + hashPartie + "'");
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
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "HashPartie = '" + hashPartie + "'");
                try {
                    if (result.next()) {
                        String signatureClef = result.getString(acteurSignature);
                        if (signatureClef == acteurSignature)
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