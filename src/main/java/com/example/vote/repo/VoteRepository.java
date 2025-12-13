package com.example.vote.repo;


import com.example.vote.model.Vote;
import java.util.List;

/**
 * Interface définissant le contrat pour la persistence des votes.
 * Permet de changer facilement l'implémentation (mémoire, fichier, base de données).
 *
 * DESIGN PATTERN: Repository Pattern
 * - Abstrait l'accès aux données
 * - Permet de tester le code indépendamment de la persistence
 * - Facilite le changement d'implémentation
 */
public interface VoteRepository {

    /**
     * Sauvegarde un vote dans le système de persistence.
     *
     * @param vote Le vote à sauvegarder
     * @throws IllegalArgumentException si le vote est null
     */
    void save(Vote vote);

    /**
     * Récupère tous les votes enregistrés.
     *
     * @return Liste de tous les votes (copie défensive)
     */
    List<Vote> findAll();

    /**
     * Supprime tous les votes du système.
     * Utile pour les tests et le reset.
     */
    void clear();

    /**
     * Compte le nombre total de votes.
     *
     * @return Le nombre de votes enregistrés
     */
    default int count() {
        return findAll().size();
    }
}