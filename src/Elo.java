/**

Calcule le score Elo pour une partie donnée en utilisant le système de classement Elo.

Le nouveau score Elo est calculé pour les deux joueurs en utilisant le classement actuel des joueurs et le résultat de la partie.

Le nouveau score est ensuite mis à jour dans le leaderboard.

@param leaderboard un map contenant le classement actuel des joueurs et leur score Elo

@param winner le nom du joueur gagnant

@param loser le nom du joueur perdant
*/
public static void calculateEloScore(Map<String, Integer> leaderboard, String winner, String loser) {
    double kFactor = 32.0; // facteur K pour le système de classement Elo( peut varier)
    
    int winnerRank = leaderboard.get(winner); // récupère le score Elo du joueur gagnant dans le leaderboard
    int loserRank = leaderboard.get(loser); // récupère le score Elo du joueur perdant dans le leaderboard
    
    // Calcule le score attendu pour chaque joueur en utilisant leur score actuel et leur rang dans le leaderboard
    double winnerExpectedScore = 1.0 / (1.0 + Math.pow(10.0, ((double)(loserRank - winnerRank) / 400.0)));
    double loserExpectedScore = 1.0 - winnerExpectedScore;
    
    // Calcule les nouveaux scores Elo pour les deux joueurs en utilisant le score attendu et le résultat de la partie
    double winnerNewScore = leaderboard.get(winner) + kFactor * (1.0 - winnerExpectedScore);
    double loserNewScore = leaderboard.get(loser) + kFactor * (0.0 - loserExpectedScore);
    
    // Met à jour les scores Elo dans le leaderboard pour les deux joueurs
    leaderboard.put(winner, (int) winnerNewScore);
    leaderboard.put(loser, (int) loserNewScore);
    }
    
    
    
    
    