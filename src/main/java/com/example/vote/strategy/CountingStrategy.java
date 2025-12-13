package com.example.vote.strategy;

import com.example.vote.model.Vote;
import java.util.List;
import java.util.Map;

/**
 * Interface définissant le contrat pour les stratégies de comptage des votes.
 *
 * DESIGN PATTERN: Strategy Pattern
 *
 * Avantages:
 * - Permet de changer l'algorithme de comptage dynamiquement
 * - Facilite l'ajout de nouvelles méthodes de comptage
 * - Rend le code facilement testable
 * - Respecte le principe Open/Closed (ouvert à l'extension, fermé à la modification)
 *
 * Exemples d'implémentations:
 * - PluralityCountingStrategy: Comptage simple (1 vote = 1 point)
 * - MajorityCountingStrategy: Vérification de majorité absolue (> 50%)
 * - RankedChoiceStrategy: Vote préférentiel (future implémentation)
 */
public interface CountingStrategy {

    /**
     * Compte les votes selon l'algorithme de la stratégie.
     *
     * @param votes Liste des votes à compter
     * @return Map associant chaque candidateId à son nombre de points/votes
     *         Exemple: {"Alice": 5, "Bob": 3, "Charlie": 2}
     */
    Map<String, Integer> count(List<Vote> votes);

    /**
     * Retourne le nom de la stratégie (pour logs/debug).
     *
     * @return Nom descriptif de la stratégie
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}