import java.util.HashMap;
import java.util.List;
public class Elo {
    // Méthode qui calcule le coefficient K en fonction du niveau du joueur
    private static int calculateK(int elo) {
        if (elo < 300) {
            return 60;
        } else if (elo < 600) {
            return 50;
        } else if (elo < 1200) {
            return 40;
        } else if (elo < 1800) {
            return 30;
        } else if (elo < 2600) {
            return 20;
        } else {
            return 10;
        }
    }
    // Méthode qui calcule le nouvel Elo d'un joueur après une partie
    private static int calculateNewElo(int oldElo, int opponentElo, double score) {
        // Calculer l'espérance de score du joueur
        double expectedScore = 1 / (1 + Math.pow(10, (opponentElo - oldElo) / 400.0));
        // Calculer le coefficient K adapté au niveau du joueur
        int K = calculateK(oldElo);
        // Calculer le nouvel Elo du joueur
        return (int) Math.round(oldElo + K * (score - expectedScore));
    }
    public static void updateElo(List<Partie> parties) {
        // Créer une map pour stocker le classement des joueurs
        HashMap<String, Integer> eloMap = new HashMap<>();
        // Parcourir les parties par ordre chronologique
        for (Partie partie : parties) {
            // Récupérer les informations de la partie
            String joueur1 = partie.getJoueur1();
            String joueur2 = partie.getJoueur2();
            int eloJoueur1 = partie.getEloJoueur1();
            int eloJoueur2 = partie.getEloJoueur2();
            double score = partie.getScore();
            // Calculer le nouvel Elo des joueurs
            int newEloJoueur1 = calculateNewElo(eloJoueur1, eloJoueur2, score);
            int newEloJoueur2 = calculateNewElo(eloJoueur2, eloJoueur1, 1 - score);
            // Mettre à jour la map avec les nouveaux Elo
            eloMap.put(joueur1, newEloJoueur1);
            eloMap.put(joueur2, newEloJoueur2);
        }
        // Afficher le classement final des joueurs
        for (String joueur : eloMap.keySet()) {
            System.out.println(joueur + " : " + eloMap.get(joueur));
        }

}}
