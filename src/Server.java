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
        DatabaseManager db = DatabaseManager.getInstance();

        // Créer une table de type PseudoClefs composées : d'un id, d'un pseudo, d'une clef publique et d'une clef privée encryptée pour garantir la sécurité(seul l'utilisateur connait la clef de décryptage de la clef)
        String[] colonnesPseudoClefs = {"_id", "Pseudo", "ClefPublique","ClefPriveeCryptee"};
        String[] typesPseudoClefs = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL UNIQUE", "TEXT NOT NULL UNIQUE","TEXT"};
        db.createTable("PseudosClefs", colonnesPseudoClefs, typesPseudoClefs);

        String[] colonnesParties = {"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
        String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
        db.createTable("Parties", colonnesParties, typesParties);

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
                            handlePartiesASigner();
                            break;
                        case 3:
                            handleEnvoyerPartie();
                            break;
                        case 4:
                            handleJson(); //Envoie
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

            String[] colonnesParties = {"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
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
                db.insert("Parties", new String[] {"Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, values);
                outputStream.writeUTF("Envoie de création de partie reçues ! ");
            }catch(Exception e){

                outputStream.writeUTF("Ajout de la partie  raté ! " + e.getMessage());
            }
            // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
            ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "HashageTimestampClefs = '" + hashPartie + "'");
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

            String[] colonnesParties = {"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
            String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
            db.createTable("Parties", colonnesParties, typesParties);
            int k = 1;
            for (k = 190; k<10;k++){
                // String[] values = {"Miguel"+k, k+"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt5mo5LrPLpe36HIOGYNu+zO98TcsFwtI+AqiIHHV6R2D1n97PyOcgdwc7QroH7fW5szdXjzTEpiJRLJ2/zXMAhBv0o79XOHoP8cdjUp1hdkaOXmQEI8Zzawsbyd0pZhBLmayUodwkZMs7ie0JRDnEpTMcAcz1gFTmTjrcCE79BINXg+W1uuQ6QdEnyADDPnOf2DofuKh40KBHJkoSAwC31L+Xo5L5631+wEcR5Hz1LLN9KUDGUrD2AVfo+r/Y/UAUSQ2n7m4NMVjNaed5K6zMznZbRtadI0w0e12UBx2zBa7UQaapqWu1xmuiHd8BzbJyXwYU8283FlfU3igmtgJWQIDAQAB", k+ "MrHIc4mjKvTXzy/j0ZJKUIR98SynrH1X333po4B8FWvpM4197NyP8kR/KsXGGekZmZsGOKg1N28Eggc/280y+82lXFG5+G1Y0D+eYt+GTc19gstOPJ1Lx+yqNIvau+nxWFeCCUo8VL+10wz0TenA6UwHJ08rEMipDYx8Wvj/1zn0QUgz4CH1teKiz9FO98fVCKXzZ1NXZns+dxmRTVQOS/VimG9sV5cEf4hxaxzfpEKGIJcylJKrftMUuCWgps3NeJ3/dRO60dASId2inTfbDqoi0J8BmJ7ZXxm+Fj3SyBoFXwQyY96p8k9dL/LhjqJv0yV/x2xAfjC6VOzEa044rUw9UlLCerIKbSCDLkaxTcF3UfFSiSh2XIdhVPd8naYtrBPz3nuBPFycq1KrkalFYvGkOddh3RXJ0OKjigcoqfnGMEOiE4Z2YilgJQttn13mJPtldq6c+Va14PvY0+pQtJ1T2w81V3duHLgQ9/TdT4Q3sO/oPn1JBjKvwy4x4GofIOMlm0j1Wn/DygaC2WG9M3t8gGKN1nGR4g6mQ6YZURhKRKxeP+Bw5XVThIbft8BvQFQwG+abJECtWSBuvxgIFIxFAVQSPvlXScLH14Sd5u4trNfDWJsESt5ZKQIvMxtSRGPx2zKU/OG9EYYKI2O/6sN3Wu/+83nHjHk8dzi7alj6PWx4EzP0jRDCEglFgFVuCZISXRbguh5U7y9MQbfYUdXrQN/HGkgLmg2H3ocmek4p+BAp5Ugo/eY9MnsSylmsg/ZENks9VTM9z6DyrDmhQp9aKK+jI55PR7P+O/T3c+0/e5mW+oTnyYC7YYRoBNxSrAaQjJejpMy+ZDl5qYm3VYrqoKJxRg+rv0+/OPmUGiKUT32uo3Dgt6w/NCw3Mve9/Kg+cf0He8NwYbSM/WXe+8H1EOT2Sm7vMu01zGEGQj/MYYEb+gyw71TYr4VHKOaNz/7gmpVXA0dtyRo+v2aTOgZKICQgLuG/wECPUXIYQvd3EyAHC6yyZdv1+oynUVTOeoSJQ3Pus7g2t1K+t++U7rrQ+fKkb7XGEINMo3ZIu4F9hqKL0yrsHK4FWhODzVH/wxB6uRdBYVISnuA/sUcdAiN015EDlLaJ6i3HCSvaj/GrdLfDyfv7AMb2k0m21c5KJQ0uZ7iFOaLHIoi5vRovKE/iGGW8o/rhPIR4x52U5qcY5M0LtApMMjOVwpg1dRi5E9gpoNy+0dokqlZ0XOva89FdCfto+m6TGzht62wQ8zI4BKwpcOeoA9C25aZPR7jtbQe/GEGOjHBJew8i8lrgXvGjAT+zs6FJnCzZRIVOhI7JBh1ox40kFkUkJ0jZm+5ijwMjZMUE0yDUdxOygM9HOmDetlwTgkFbkkgyGQPce7brs2pleogZotcpshqtaECGQ5gQmtdiOcn1bmiQqB6CD2fWb+FzSQD/Ikvc5CUWo0F1Jb3QxUltZdL3po4T/24/3psQAz8QDh4B0GnxQbJJHZDmLf622+lmzJoAakW6bzLPQvVhLGZ4xFmACIJTyvK8ktE8UEoLm+eBmQmBaYPQlYhXGkQUs0EpsjlOszsdngvZwFi18C3hG31dHB8c+7lWdmz2emRT48RSSQIsYJXCUls/b1H7DiC/VzjlY8xqWyV2wLQH+819oKncd7/y3pQi2nVdjtNTrH6Nj/nSr1XMPOCHX9VGcHKOSte7qSPhJSoIZ767xsPrnrnE+Nvh4MC+qau7KgTPRgIGmvalFA1h3LOhcQArOAxQhJ7oo7zy/enMavi4JFJumJNEJ86Dm/bDAas3h5MbceuK4GiSuTDnuZElB5tbq6wOsdzDxTv7HWwM4UxTz+w9Fy4SBCnA8JxN6pO3947TjljoMpcS4t/ISt7PrHlI8qKnbo50wbCFoxjrqOBCOXMNwKJB9ZDn1g2tztwhmFlQMhkAgfehuSpfC+fAxYOOq07ZoLTMj8I5DQcnCWkKN24oTmsGAvOM1qHEDz08tetvAYlccIZsr2SI1bAkDjKsfTXeSn+pr0tFxgA1jge/ayxU9xRkjqAGFpRxaUb0XNf+w28Ti4ZpsbqfV+wlVZzKF+7kDAGmt11J6mZ4VrOPEWQsZW+Nht6DcN7R4Yub6YmknIsHGNDOtk5zrMWsN1rI90RuzsPQNEf/VKO5rq6GzXYviEYg2RnlYGmb"};
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
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "Timestamp Like '%'");
                try {
                    int i=0;
                    while (result.next()) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("ID", result.getInt("_id"));
                        map2.put("Timestamp", result.getString("Timestamp"));
                       map2.put("HashageTimestampClefs", result.getString("HashageTimestampClefs"));
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

    }
}