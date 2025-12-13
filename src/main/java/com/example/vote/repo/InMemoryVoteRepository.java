package com.example.vote.repo;

import com.example.vote.model.Vote;
import java.util.*;

/**
 * Implémentation du VoteRepository qui stocke les votes en mémoire.
 * Thread-safe grâce à l'utilisation de Collections.synchronizedList.
 *
 * Avantages:
 * - Rapide
 * - Pas de dépendance externe
 * - Parfait pour les tests
 *
 * Inconvénients:
 * - Données perdues au redémarrage
 * - Limité par la mémoire RAM
 */
public class InMemoryVoteRepository implements VoteRepository {

    // Liste synchronisée pour assurer la thread-safety
    private final List<Vote> store = Collections.synchronizedList(new ArrayList<>());

    /**
     * Sauvegarde un vote en mémoire.
     *
     * @param vote Le vote à sauvegarder
     * @throws IllegalArgumentException si le vote est null
     */
    @Override
    public void save(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("Le vote ne peut pas être null");
        }
        store.add(vote);
    }

    /**
     * Retourne une copie défensive de tous les votes.
     * Empêche la modification directe de la liste interne.
     *
     * @return Nouvelle liste contenant tous les votes
     */
    @Override
    public List<Vote> findAll() {
        // Copie défensive pour éviter les modifications externes
        synchronized (store) {
            return new ArrayList<>(store);
        }
    }

    /**
     * Efface tous les votes de la mémoire.
     */
    @Override
    public void clear() {
        store.clear();
    }

    /**
     * Retourne le nombre de votes stockés.
     *
     * @return Taille de la collection
     */
    @Override
    public int count() {
        return store.size();
    }
}