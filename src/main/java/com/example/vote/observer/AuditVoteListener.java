package com.example.vote.observer;

import com.example.vote.model.Vote;
import java.util.*;

/**
 * Implémentation d'un VoteListener pour l'audit des votes.
 *
 * DESIGN PATTERN: Observer Pattern
 *
 * Responsabilités:
 * - Enregistrer l'historique complet de tous les votes
 * - Fournir des méthodes d'interrogation par votant et par candidat
 * - Assurer la traçabilité complète du système
 *
 * Caractéristiques:
 * - Stockage séparé par votant et par candidat pour des requêtes rapides
 * - Copie défensive pour protéger les données internes
 * - Thread-safe via synchronisation
 */
public class AuditVoteListener implements VoteListener {

    // Historique complet des votes dans l'ordre d'arrivée
    private final List<Vote> auditTrail;

    // Indexation par ID de votant pour des requêtes rapides
    private final Map<String, List<Vote>> votesByVoter;

    // Indexation par ID de candidat pour des requêtes rapides
    private final Map<String, List<Vote>> votesByCandidate;

    // Statistiques
    private int totalVotes;

    /**
     * Constructeur initialisant les structures de données.
     */
    public AuditVoteListener() {
        this.auditTrail = Collections.synchronizedList(new ArrayList<>());
        this.votesByVoter = Collections.synchronizedMap(new HashMap<>());
        this.votesByCandidate = Collections.synchronizedMap(new HashMap<>());
        this.totalVotes = 0;
    }

    /**
     * Méthode appelée automatiquement lorsqu'un vote est enregistré.
     *
     * @param vote Le vote à auditer
     */
    @Override
    public void onVote(Vote vote) {
        if (vote == null) {
            System.err.println("[AUDIT] Tentative d'audit d'un vote null");
            return;
        }

        synchronized (this) {
            // Ajouter au sentier d'audit principal
            auditTrail.add(vote);
            totalVotes++;

            // Indexer par votant
            votesByVoter.computeIfAbsent(vote.voterId(), k -> new ArrayList<>())
                    .add(vote);

            // Indexer par candidat
            votesByCandidate.computeIfAbsent(vote.candidateId(), k -> new ArrayList<>())
                    .add(vote);

            // Log d'audit
            logAuditEntry(vote);
        }
    }

    /**
     * Retourne une copie de l'historique complet d'audit.
     *
     * @return Liste de tous les votes audités (copie défensive)
     */
    public List<Vote> getAuditLog() {
        synchronized (auditTrail) {
            return new ArrayList<>(auditTrail);
        }
    }

    /**
     * Retourne le nombre total de votes audités.
     *
     * @return Nombre de votes dans l'historique d'audit
     */
    public int getAuditCount() {
        return totalVotes;
    }

    /**
     * Retourne tous les votes d'un votant spécifique.
     *
     * @param voterId ID du votant
     * @return Liste des votes du votant (copie défensive)
     */
    public List<Vote> getVotesByVoter(String voterId) {
        synchronized (votesByVoter) {
            List<Vote> votes = votesByVoter.get(voterId);
            return votes != null ? new ArrayList<>(votes) : Collections.emptyList();
        }
    }

    /**
     * Retourne tous les votes pour un candidat spécifique.
     *
     * @param candidateId ID du candidat
     * @return Liste des votes pour ce candidat (copie défensive)
     */
    public List<Vote> getVotesForCandidate(String candidateId) {
        synchronized (votesByCandidate) {
            List<Vote> votes = votesByCandidate.get(candidateId);
            return votes != null ? new ArrayList<>(votes) : Collections.emptyList();
        }
    }

    /**
     * Efface complètement l'historique d'audit.
     */
    public void clearAudit() {
        synchronized (this) {
            auditTrail.clear();
            votesByVoter.clear();
            votesByCandidate.clear();
            totalVotes = 0;
            System.out.println("[AUDIT] Historique d'audit effacé");
        }
    }

    /**
     * Retourne le nombre de votes pour un candidat spécifique.
     *
     * @param candidateId ID du candidat
     * @return Nombre de votes pour ce candidat
     */
    public int getVoteCountForCandidate(String candidateId) {
        synchronized (votesByCandidate) {
            List<Vote> votes = votesByCandidate.get(candidateId);
            return votes != null ? votes.size() : 0;
        }
    }

    /**
     * Vérifie si un votant a déjà voté.
     *
     * @param voterId ID du votant
     * @return true si le votant a au moins un vote
     */
    public boolean hasVoterVoted(String voterId) {
        synchronized (votesByVoter) {
            return votesByVoter.containsKey(voterId);
        }
    }

    /**
     * Retourne le nombre de votants distincts.
     *
     * @return Nombre de votants uniques
     */
    public int getUniqueVoterCount() {
        synchronized (votesByVoter) {
            return votesByVoter.size();
        }
    }

    /**
     * Retourne le nombre de candidats distincts.
     *
     * @return Nombre de candidats uniques
     */
    public int getUniqueCandidateCount() {
        synchronized (votesByCandidate) {
            return votesByCandidate.size();
        }
    }

    /**
     * Retourne les statistiques d'audit.
     *
     * @return Map contenant toutes les statistiques
     */
    public Map<String, Object> getAuditStatistics() {
        Map<String, Object> stats = new HashMap<>();

        synchronized (this) {
            stats.put("totalVotes", totalVotes);
            stats.put("uniqueVoters", getUniqueVoterCount());
            stats.put("uniqueCandidates", getUniqueCandidateCount());
            stats.put("auditTrailSize", auditTrail.size());

            // Statistiques par candidat
            Map<String, Integer> candidateStats = new HashMap<>();
            for (Map.Entry<String, List<Vote>> entry : votesByCandidate.entrySet()) {
                candidateStats.put(entry.getKey(), entry.getValue().size());
            }
            stats.put("votesByCandidate", candidateStats);
        }

        return stats;
    }

    /**
     * Retourne l'horodatage du premier vote.
     *
     * @return Timestamp du premier vote, ou -1 si aucun vote
     */
    public long getFirstVoteTimestamp() {
        synchronized (auditTrail) {
            if (auditTrail.isEmpty()) {
                return -1;
            }
            return auditTrail.get(0).timestamp();
        }
    }

    /**
     * Retourne l'horodatage du dernier vote.
     *
     * @return Timestamp du dernier vote, ou -1 si aucun vote
     */
    public long getLastVoteTimestamp() {
        synchronized (auditTrail) {
            if (auditTrail.isEmpty()) {
                return -1;
            }
            return auditTrail.get(auditTrail.size() - 1).timestamp();
        }
    }

    /**
     * Log une entrée d'audit dans la console.
     *
     * @param vote Le vote à logger
     */
    private void logAuditEntry(Vote vote) {
        System.out.println(String.format(
                "[AUDIT] Vote #%d | Votant: %s → Candidat: %s | Time: %tF %tT",
                totalVotes,
                vote.voterId(),
                vote.candidateId(),
                vote.timestamp(),
                vote.timestamp()
        ));
    }

    /**
     * Exporte l'audit complet en format texte lisible.
     *
     * @return Audit sous forme de texte formaté
     */
    public String exportAuditReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== RAPPORT D'AUDIT DES VOTES ===\n");
        report.append(String.format("Total votes: %d\n", totalVotes));
        report.append(String.format("Votants uniques: %d\n", getUniqueVoterCount()));
        report.append(String.format("Candidats uniques: %d\n\n", getUniqueCandidateCount()));

        report.append("Votes par candidat:\n");
        synchronized (votesByCandidate) {
            for (Map.Entry<String, List<Vote>> entry : votesByCandidate.entrySet()) {
                report.append(String.format("  %s: %d votes\n",
                        entry.getKey(), entry.getValue().size()));
            }
        }

        report.append("\nHistorique chronologique:\n");
        synchronized (auditTrail) {
            for (int i = 0; i < auditTrail.size(); i++) {
                Vote vote = auditTrail.get(i);
                report.append(String.format("  %d. Votant: %s → Candidat: %s\n",
                        i + 1, vote.voterId(), vote.candidateId()));
            }
        }

        return report.toString();
    }
}