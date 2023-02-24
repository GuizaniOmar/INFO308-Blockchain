/**
 * Fonction pour calculer le coefficient de l'arbitre à partir des votes des joueurs et de l'arbitre ainsi que du résultat du jeu.
 * @param vote1 Le vote du joueur 1.
 * @param vote2 Le vote du joueur 2.
 * @param refereeVote Le vote de l'arbitre.
 * @param result Le résultat du jeu (1 si le joueur 1 a gagné, 2 si le joueur 2 a gagné, 0 si le jeu est un match nul).
 * @return Le coefficient de l'arbitre calculé en fonction des votes et du résultat.
 */
public static double calculateRefereeCoefficient(int vote1, int vote2, int refereeVote, int result) {
    double coefficientArbitre = 0.5; // Le coefficient initial de l'arbitre est de 0,5
    double delta = 0.05; // La valeur de changement de coefficient pour chaque jeu

    // Vérifie si le vote de l'arbitre est différent de celui de la majorité des joueurs
    if ((refereeVote > vote1 && refereeVote > vote2) || (refereeVote < vote1 && refereeVote < vote2)) {
        // Si l'arbitre est en minorité, diminue le coefficient de delta
        coefficientArbitre -= delta;
    } else {
        // Si l'arbitre est en majorité ou qu'il n'y a pas de majorité, augmente le coefficient de delta
        coefficientArbitre += delta;
    }

    // Vérifie si le vote de l'arbitre correspond au résultat du jeu
    if ((result == 1 && refereeVote > 0) || (result == 2 && refereeVote < 0)) {
        // Si l'arbitre a raison, augmente le coefficient de delta
        coefficientArbitre += delta;
    } else {
        // Si l'arbitre a tort ou que le jeu est un match nul, diminue le coefficient de delta
        coefficientArbitre -= delta;
    }

    // Limite le coefficient entre 0 et 1
    coefficientArbitre = Math.max(0.0, Math.min(1.0, coefficientArbitre));

    return coefficientArbitre;
}
