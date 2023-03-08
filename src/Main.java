import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //   test();
     // Seul le serveur est utilisé dans la partie Github, le client est sur Android
   //     liste_ips();




        Server server = new Server(52000);

        try {

            PingIP.main(args);
            // On liste toutes les ips !
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // test_elo();
     //     test_client();
       // test_p2P();
    }

    public static void liste_ips() throws UnknownHostException {
        try {
            // Obtenir toutes les adresses IP locales de l'ordinateur
            InetAddress[] adresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());

            // Afficher toutes les adresses IP locales
            for (int i = 0; i < adresses.length; i++) {
                String ip = adresses[i].getHostAddress();
                if (ip.startsWith("192.")) {
                    System.out.println("Adresse IP locale: " + ip);}
                    else{
                    System.out.println("Adresse IP non-local: " + ip);}

            }
        } catch (UnknownHostException e) {
            // Gérer les erreurs
            e.printStackTrace();
        }


    }
    public static void test_p2P() throws IOException {
 //     P2PServer server = new P2PServer(52000);
   //     server.start();

        // Pour utiliser le client
        P2PClient client = new P2PClient(InetAddress.getByName("93.115.97.128"), 52000);
        client.sendMessage("Bonjour tout le monde!");
         String message = client.receiveMessage();
        System.out.println("Message reçu: " + message);
        client.close();

        // Pour fermer le serveur
      //  server.close();
    }
    public static void test_elo(){
        Partie partie1 = new Partie("Joueur A", "Joueur B", 1200, 1000, 1.0);
        Partie partie2 = new Partie("Joueur C", "Joueur D", 800, 1500, 1);

        List<Partie> parties = Arrays.asList(partie1, partie2);

        Elo.updateElo(parties);
                //String joueur1, String joueur2, int eloJoueur1, int eloJoueur2, double score
    }
    public static void test_client(){
        Client client = null;
        try {
            client = new Client("192.168.1.57", 52001);
            client.login("user1", "motdepasse1");


            client.json();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void test() {
        DatabaseManager db = DatabaseManager.getInstance();

        // Créer une table de type PseudoClefs composées : d'un id, d'un pseudo, d'une clef publique et d'une clef privée encryptée pour garantir la sécurité(seul l'utilisateur connait la clef de décryptage de la clef)
        String[] colonnesPseudoClefs = {"_id", "Pseudo", "ClefPublique","ClefPriveeCryptee"};
        String[] typesPseudoClefs = {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT NOT NULL UNIQUE", "TEXT NOT NULL UNIQUE","TEXT"};
        db.createTable("PseudosClefs", colonnesPseudoClefs, typesPseudoClefs);

        String[] colonnesParties = {"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};
        String[] typesParties = {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL","TEXT NOT NULL","TEXT NOT NULL  CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre)","TEXT","TEXT","TEXT"};
        db.createTable("Parties", colonnesParties, typesParties);

        int k1 = 0;
        for (k1 = 0;k1 < 10; k1++){
            String pseudo1 = "MiguelJ1-" + k1;
            String pseudo2 = "MiguelJ2-" + k1;
            String pseudo3 = "MiguelArbitre-" + k1;
            String timestampActuel = Timestamp.getCurrentTimestamp();
            String hashpartie = SHA2.encrypt(timestampActuel + "-" + pseudo1 + "-" + pseudo2 + "-" + pseudo3);
            String[] values = {timestampActuel,hashpartie,pseudo1,pseudo2,pseudo3,"MACLEFPRIVE.Encrypt(" + hashpartie + "-V) -> Ici il vote Victoire J1" ,"MACLEFPRIVE.Encrypt(" + hashpartie + "-D) -> Ici il vote Victoire J2","MACLEFPRIVE.Encrypt(" + hashpartie + "-N) -> Ici il vote match NUL "};
            db.insert("Parties",new  String[]{ "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"},values);
        }


        // Insérer des données dans la table
        int k = 110;
        for (k = 110; k<100;k++){
            String[] values = {"Miguel"+k, k+"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt5mo5LrPLpe36HIOGYNu+zO98TcsFwtI+AqiIHHV6R2D1n97PyOcgdwc7QroH7fW5szdXjzTEpiJRLJ2/zXMAhBv0o79XOHoP8cdjUp1hdkaOXmQEI8Zzawsbyd0pZhBLmayUodwkZMs7ie0JRDnEpTMcAcz1gFTmTjrcCE79BINXg+W1uuQ6QdEnyADDPnOf2DofuKh40KBHJkoSAwC31L+Xo5L5631+wEcR5Hz1LLN9KUDGUrD2AVfo+r/Y/UAUSQ2n7m4NMVjNaed5K6zMznZbRtadI0w0e12UBx2zBa7UQaapqWu1xmuiHd8BzbJyXwYU8283FlfU3igmtgJWQIDAQAB", k+ "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"};
            db.insert("PseudosClefs", new String[] {"Pseudo", "ClefPublique","ClefPriveeCryptee"}, values);


        }
        // Sélectionner des données à partir de la table
        if (true) {
            try {
                ResultSet result = db.select("Parties", new String[]{"_id", "Timestamp","HashageTimestampClefs","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"}, "ClefPubliqueJ1 Like '%'");
                try {
                    while (result.next()) {
                        System.out.println("ID : " + result.getInt("_id"));
                        System.out.println("Timestamp : " + result.getString("Timestamp"));
                        System.out.println("Hashage : " + result.getString("HashageTimestampClefs"));
                        System.out.println("Clef Publique J1 : " + result.getString("ClefPubliqueJ1"));
                        System.out.println("Clef Publique J2 : " + result.getString("ClefPubliqueJ2"));
                        System.out.println("Clef Publique Arbitre : " + result.getString("ClefPubliqueArbitre"));
                        System.out.println("Vote J1 : " + result.getString("VoteJ1"));

                        System.out.println("Vote J2 : " + result.getString("VoteJ2"));
                        System.out.println("Vote Arbitre : " + result.getString("VoteArbitre"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PseudoClefs");
            }
        }
        if (false) {
            try {
                ResultSet result = db.select("PseudosClefs", new String[]{"_id", "Pseudo", "ClefPublique", "ClefPriveeCryptee"}, "Pseudo Like '%'");
                try {
                    while (result.next()) {
                        System.out.println("ID : " + result.getInt("_id"));
                        System.out.println("Pseudo : " + result.getString("Pseudo"));
                        System.out.println("Clef Publique : " + result.getString("ClefPublique"));
                        System.out.println("Clef Privee Cryptee : " + result.getString("ClefPriveeCryptee"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PseudoClefs");
            }
        }
        // Mettre à jour des données dans la table
        //  db.update("PseudosClefs", new String[] {"age"}, new String[] {"26"}, "name='John'");

        // Supprimer des données dans la table
        //db.delete("students", "name='John'");
    }
}