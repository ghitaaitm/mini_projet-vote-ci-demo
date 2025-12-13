package com.example.vote.strategy;

import com.example.vote.model.Vote;
import java.util.*;

/**
 * Stratégie de comptage simple: Pluralité (Plurality).
 * Le candidat avec le plus de votes gagne.
 *
 * DESIGN PATTERN: Strategy Pattern
 *
 * Algorithme: 1 vote = 1 point
 * Le gagnant est celui avec le plus de points.
 */
public class PluralityCountingStrategy implements CountingStrategy {

    /**
     * Compte les votes selon la méthode de pluralité.
     *
     * @param votes Liste des votes à compter
     * @return Map avec candidateId -> nombre de votes
     */
    @Override
    public Map<String, Integer> count(List<Vote> votes) {
        if (votes == null) {
            throw new IllegalArgumentException("La liste de votes ne peut pas être null");
        }

        Map<String, Integer> results = new HashMap<>();

        // Compter chaque vote
        for (Vote vote : votes) {
            String candidateId = vote.candidateId();
            results.merge(candidateId, 1, Integer::sum);
        }

        return results;
    }

    /**
     * Détermine le gagnant selon les résultats.
     *
     * @param results Map des résultats (candidateId -> votes)
     * @return ID du candidat gagnant, ou null si aucun vote
     */
    public String getWinner(Map<String, Integer> results) {
        if (results == null || results.isEmpty()) {
            return null;
        }

        String winner = null;
        int maxVotes = -1;

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }

    @Override
    public String getStrategyName() {
        return "Plurality (Simple Majority)";
    }
}