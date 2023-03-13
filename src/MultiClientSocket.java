import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiClientSocket implements Runnable {

    private static List<Socket> sockets;
    private static Map<Socket, ClientInfo> serverInfo;
    private static String hashClient = null;
    private static boolean isRunning = false;

    private static MultiClientSocket instance = null;

    public static synchronized MultiClientSocket getInstance() { // MultiClientSocket is a singleton
        if (instance == null) {
            instance = new MultiClientSocket();
        }
        return instance;
    }

    public void add(String serverAddress, int serverPort) throws IOException, IOException {
        boolean isAlreadyConnected = false;
        for (ClientInfo info : serverInfo.values()) {
            if (info.getIpAddress().equals(serverAddress) && info.getPort() == serverPort) {
                isAlreadyConnected = true;
                break;
            }
        }
        if (!isAlreadyConnected) {
            System.out.println("Connexion au serveur " + serverAddress + ":" + serverPort + "...");
            Socket socket = new Socket(serverAddress, serverPort);
            sockets.add(socket);
            ClientInfo clientInfo = new ClientInfo(serverAddress, serverPort);
            // Ajouter une méthode pour récuperer le hash du serveur
            serverInfo.put(socket, clientInfo);
        }
    }

    public void setServerInfo(Socket socket, ClientInfo clientinfo) {
        serverInfo.put(socket, clientinfo);
    }

    MultiClientSocket() {
        sockets = new ArrayList<>();
        serverInfo = new HashMap<>();
        System.out.println("Le multiclient est initialisé ! ");

        // On tourne une boucle...
    }

    @Override
    public void run() {
        if (!isRunning) {
            isRunning = true;
            while (true) {
                System.out.println("hash du client en boucle" + hashClient);



                // supprimerConnexionsFermees();
                System.out.println("Nombre de connexions: " + sockets.size());
                // Autres actions à effectuer dans la boucle infinie
                try {
                    Thread.sleep(1000); // Attendre 1 seconde entre chaque itération
                } catch (InterruptedException e) {
                    // Gérer l'exception si nécessaire
                }
            }
        }
    }

    public void demanderClefsPubliques() throws IOException {
        // Etape 1 demander les comptes
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(20);//20 = demande de clé publique
            outputStream.flush();
            System.out.println("On a envoyé la demande de clé publique ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String pseudo = (String) obj.get("pseudo");
                        // On récupère pas la clef privée String clefPriveeCryptee = (String) obj.get("clefPrivee");
                        String clefPublique = (String) obj.get("clefPublique");
                        System.out.println("On est bon?!");
                        if ((pseudo != null) && (clefPublique != null)) {
                            System.out.println("pseudo: " + pseudo + " clefpublique: " + clefPublique);
                        } else {
                            System.out.println("ERREUR ! " + "pseudo: " + pseudo + " clefpublique: " + clefPublique);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
    }

    public void demanderConfirmations() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(21);//20 = demande de clé publique
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String hashPartie = (String) obj.get("hashPartie");
                        String hashVote = (String) obj.get("hashVote");
                        String clefPublique = (String) obj.get("clefPublique");
                        String signatureHashVote = (String) obj.get("signatureHashVote");


                        System.out.println("Traitement reçues");
                        if ((hashPartie != null) && (clefPublique != null) && (hashVote != null) && (signatureHashVote != null)) {
                            System.out.println("hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
    }
    public void demanderPlaintes() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(21);//20 = demande de clé publique
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String hashPartie = (String) obj.get("hashPartie");
                        String hashVote = (String) obj.get("hashVote");
                        String clefPublique = (String) obj.get("clefPublique");
                        String signatureHashVote = (String) obj.get("signatureHashVote");


                        System.out.println("Traitement reçues");
                        if ((hashPartie != null) && (clefPublique != null) && (hashVote != null) && (signatureHashVote != null)) {
                            System.out.println("hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
    }

    public void demanderParties() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(22);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques

                     List<String> list = Json.extraireMots(msg);
                    int c;
                    System.out.println("Size:" + list.size());
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");
                        String hashVote = (String) obj.get("hashVote");


                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) && (hashVote != null)) {
                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " + hashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " + hashVote);
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                msg = inputStream.readUTF();

            }
            System.out.println("Serveur nous envoie : " + message);
        }
        System.out.println("Serveur nous envoie : " + message);
    }

}
    public void demanderPartiesAEnvoyer() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(24);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");



                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) ) {
                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }

    }

    public void demanderPartiesARecevoir() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(23);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("end")) {
                    //JSON parser pour récuperer les clés publiques
                    List<String> list = Json.extraireMots(msg);
                    int c;

                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");



                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) ) {
                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }

    }

    public void ajouterPartie(Partie partie) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre, partie.hashVote};
        try {
          db.insert("partie", new String[]{"timestamp", "hashPartie", "clefPubliqueJ1", "clefPubliqueJ2", "clefPubliqueArbitre", "voteJ1", "voteJ2", "voteArbitre", "signatureJ1", "signatureJ2", "signatureArbitre", "hashVote"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

        }
    public void ajouterPartieAEnvoyer(Partie partie) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre};
        try {
            db.insert("partieAEnvoyer", new String[]{"timestamp", "hashPartie", "clefPubliqueJ1", "clefPubliqueJ2", "clefPubliqueArbitre", "voteJ1", "voteJ2", "voteArbitre", "signatureJ1", "signatureJ2", "signatureArbitre"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

    }
    public void ajouterPartieARecevoir(Partie partie) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre};
        try {
            db.insert("partieARecevoir", new String[]{"timestamp", "hashPartie", "clefPubliqueJ1", "clefPubliqueJ2", "clefPubliqueArbitre", "voteJ1", "voteJ2", "voteArbitre", "signatureJ1", "signatureJ2", "signatureArbitre"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

    }

    public void ajouterleaderboard(String clefPublique, String pseudo, String elo, String coefficientArbitrage, String scoreTotal, String nbParties, String nbVictoire, String nbConfirmation, String nbPartieArbitre, String nbNul, String nbDefaite){

        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {clefPublique, pseudo, elo, coefficientArbitrage, scoreTotal, nbParties, nbVictoire, nbConfirmation, nbPartieArbitre, nbNul, nbDefaite};
        try {
            db.insert("leaderboard", new String[]{"clefPublique", "pseudo", "elo", "coefficientArbitrage", "scoreTotal", "nbParties", "nbVictoire", "nbConfirmation", "nbPartieArbitre", "nbNul", "nbDefaite"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

    }
    public void ajouterCompte(String pseudo, String clefPublique, String clefPrivee) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {pseudo, clefPublique, clefPrivee};
        try {
            db.insert("compte", new String[]{"pseudo","clefPublique","clefPrivee"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

    }

    public void ajouterCompte(String pseudo, String clefPublique) {
     ajouterCompte(pseudo, clefPublique, "");
    }

    public void ajouterConfirmation(String hashPartie, String hashVote, String clefPublique, String signatureHashVote) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {hashPartie, hashVote, clefPublique, signatureHashVote};
        try {
            db.insert("confirmation", new String[]{"hashPartie", "hashVote", "clefPublique", "signatureHashVote"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la confirmation");
        }

    }

    public void ajouterPlainte(String hashPartie, String hashVote, String clefPublique, String signatureHashVote) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {hashPartie, hashVote, clefPublique, signatureHashVote};
        try {
            db.insert("plainte", new String[]{"hashPartie", "hashVote", "clefPublique", "signatureHashVote"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la plainte");
        }

    }
    public void ajouterPartieDetail(String hashPartie, String resultat, String eloJ1, String eloJ2, String fiabiliteArbitre) {
        DatabaseManager db = DatabaseManager.getInstance(); // On récupère une instance de la base de données

        String[] values = {hashPartie, resultat, eloJ1, eloJ2, fiabiliteArbitre};
        try {
            db.insert("partieDetail", new String[]{"hashPartie", "resultat", "eloJ1", "eloJ2", "fiabiliteArbitre"}, values);
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la plainte");
        }

    }

    public void send(String message) throws IOException {
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeInt(0);
            outputStream.writeUTF(message);


            outputStream.flush();
System.out.print(" On a envoyé ! ");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            String message2 = inputStream.readUTF();
            System.out.println("Serveur nous envoie : " + message2);

        }
    }


    // Création de plusieurs méthodes pour gérer les demandes à la base de données !
    public void supprimerConnexionsFermees() {
        List<Socket> socketsAFermer = new ArrayList<>();
        for (Socket socket : serverInfo.keySet()) {
            if (socket.isClosed()) {
                socketsAFermer.add(socket);
            }
        }
        for (Socket socketAFermer : socketsAFermer) {
            serverInfo.remove(socketAFermer);
            try {
                socketAFermer.close();
            } catch (IOException e) {
// Gérer l'exception si nécessaire
            }
        }
    }

    public void receive() throws IOException {
        for (Socket socket : sockets) {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            String message = inputStream.readUTF();
            System.out.println(socket.getInetAddress().getHostAddress() + "," + serverInfo.get(socket) + "," + socket);
            System.out.println(message);
        }
    }
}