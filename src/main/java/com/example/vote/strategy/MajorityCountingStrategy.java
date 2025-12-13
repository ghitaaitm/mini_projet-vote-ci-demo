package com.example.vote.strategy;

import com.example.vote.model.Vote;
import java.util.*;

/**
 * Stratégie de comptage par majorité absolue (> 50%).
 */
public class MajorityCountingStrategy implements CountingStrategy {

    @Override
    public Map<String, Integer> count(List<Vote> votes) {
        if (votes == null) {
            throw new IllegalArgumentException("La liste de votes ne peut pas être null");
        }

        Map<String, Integer> results = new HashMap<>();

        // Compter chaque vote (même logique que Plurality pour le comptage de base)
        for (Vote vote : votes) {
            String candidateId = vote.candidateId();
            results.merge(candidateId, 1, Integer::sum);
        }

        return results;
    }

    /**
     * Détermine le gagnant par majorité absolue (> 50%).
     *
     * @param results Map des résultats (candidateId -> votes)
     * @param totalVotes Nombre total de votes
     * @return ID du candidat gagnant, ou null si aucun n'a la majorité
     */
    public String getMajorityWinner(Map<String, Integer> results, int totalVotes) {
        if (results == null || results.isEmpty() || totalVotes <= 0) {
            return null;
        }

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (hasMajority(entry.getKey(), results, totalVotes)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Vérifie si un candidat a la majorité absolue.
     */
    public boolean hasMajority(String candidateId, Map<String, Integer> results, int totalVotes) {
        if (!results.containsKey(candidateId) || totalVotes <= 0) {
            return false;
        }

        int candidateVotes = results.get(candidateId);
        double percentage = (candidateVotes * 100.0) / totalVotes;

        // Majorité absolue = plus de 50%
        return percentage > 50.0;
    }

    @Override
    public String getStrategyName() {
        return "Majority (Absolute > 50%)";
    }
}