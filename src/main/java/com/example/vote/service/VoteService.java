package com.example.vote.service;

import com.example.vote.model.Vote;
import com.example.vote.repo.VoteRepository;
import com.example.vote.strategy.CountingStrategy;
import com.example.vote.observer.VoteListener;

import java.util.*;

/**
 * Service principal orchestrant la logique métier du système de vote.
 *
 * Responsabilités:
 * - Enregistrer les votes via le repository
 * - Notifier les observers (pattern Observer)
 * - Calculer les résultats via les strategies (pattern Strategy)
 * - Gérer les règles métier (validation, etc.)
 *
 * Cette classe coordonne les différents design patterns:
 * - Repository: pour la persistence
 * - Strategy: pour le comptage flexible
 * - Observer: pour les notifications
 */
public class VoteService {

    private final VoteRepository repository;
    private final List<VoteListener> listeners;
    private final Set<String> votedVoterIds; // Anti-double vote

    /**
     * Constructeur avec injection de dépendance.
     *
     * @param repository Repository pour persister les votes
     */
    public VoteService(VoteRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Le repository ne peut pas être null");
        }
        this.repository = repository;
        this.listeners = new ArrayList<>();
        this.votedVoterIds = new HashSet<>();
    }

    /**
     * Enregistre un listener pour les événements de vote.
     *
     * @param listener Observer à notifier
     * @throws IllegalArgumentException si listener est null
     */
    public void addListener(VoteListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Le listener ne peut pas être null");
        }
        listeners.add(listener);
    }

    /**
     * Retire un listener.
     *
     * @param listener Observer à retirer
     */
    public void removeListener(VoteListener listener) {
        listeners.remove(listener);
    }

    /**
     * Enregistre un vote dans le système.
     *
     * @param vote Vote à enregistrer
     * @throws IllegalArgumentException si vote est null
     * @throws IllegalStateException si le votant a déjà voté
     */
    public void cast(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("Le vote ne peut pas être null");
        }

        // Vérifier le double vote
        if (votedVoterIds.contains(vote.voterId())) {
            throw new IllegalStateException(
                    "Le votant " + vote.voterId() + " a déjà voté");
        }

        // Sauvegarder le vote
        repository.save(vote);
        votedVoterIds.add(vote.voterId());

        // Notifier tous les observers
        notifyListeners(vote);
    }

    /**
     * Enregistre un vote sans vérification de double vote.
     * À utiliser uniquement pour les tests ou cas spéciaux.
     *
     * @param vote Vote à enregistrer
     */
    public void castUnchecked(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("Le vote ne peut pas être null");
        }

        repository.save(vote);
        notifyListeners(vote);
    }

    /**
     * Compte les votes selon la stratégie spécifiée.
     *
     * @param strategy Stratégie de comptage à utiliser
     * @return Map avec les résultats (candidateId → nombre de votes)
     * @throws IllegalArgumentException si strategy est null
     */
    public Map<String, Integer> count(CountingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La stratégie ne peut pas être null");
        }

        List<Vote> allVotes = repository.findAll();
        return strategy.count(allVotes);
    }

    /**
     * Retourne tous les votes enregistrés.
     *
     * @return Liste de tous les votes
     */
    public List<Vote> getAllVotes() {
        return repository.findAll();
    }

    /**
     * Retourne le nombre total de votes.
     *
     * @return Nombre de votes
     */
    public int getTotalVotes() {
        return repository.count();
    }

    /**
     * Vérifie si un votant a déjà voté.
     *
     * @param voterId ID du votant
     * @return true si le votant a déjà voté
     */
    public boolean hasVoted(String voterId) {
        return votedVoterIds.contains(voterId);
    }

    /**
     * Réinitialise complètement le système de vote.
     * Efface tous les votes et réinitialise le tracking des votants.
     */
    public void reset() {
        repository.clear();
        votedVoterIds.clear();
        // Optionnel: notifier les listeners du reset
    }

    /**
     * Retourne le nombre de listeners enregistrés.
     *
     * @return Nombre d'observers
     */
    public int getListenerCount() {
        return listeners.size();
    }

    /**
     * Notifie tous les listeners qu'un vote a été enregistré.
     *
     * @param vote Vote qui vient d'être enregistré
     */
    private void notifyListeners(Vote vote) {
        for (VoteListener listener : listeners) {
            try {
                listener.onVote(vote);
            } catch (Exception e) {
                // Log l'erreur mais continue avec les autres listeners
                System.err.println("Erreur lors de la notification du listener: " + e.getMessage());
            }
        }
    }

    /**
     * Retourne les statistiques du système.
     *
     * @return Map avec les statistiques
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVotes", getTotalVotes());
        stats.put("uniqueVoters", votedVoterIds.size());
        stats.put("listenersCount", listeners.size());
        return stats;
    }
}