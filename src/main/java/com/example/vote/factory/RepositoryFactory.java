package com.example.vote.factory;

import com.example.vote.repo.*;

/**
 * Factory pour créer des instances de VoteRepository.
 *
 * DESIGN PATTERN: Factory Method
 *
 * Avantages:
 * - Découple le code client de l'implémentation concrète
 * - Facilite l'ajout de nouvelles implémentations (fichier, DB)
 * - Centralise la logique de création
 * - Permet de configurer facilement via paramètres
 *
 * Utilisation:
 * VoteRepository repo = RepositoryFactory.createRepository("memory");
 */
public class RepositoryFactory {

    // Types de repository supportés
    public static final String TYPE_MEMORY = "memory";
    public static final String TYPE_FILE = "file";      // Future implémentation
    public static final String TYPE_DATABASE = "database"; // Future implémentation

    /**
     * Crée une instance de VoteRepository selon le type spécifié.
     *
     * @param type Type de repository ("memory", "file", "database")
     * @return Une instance de VoteRepository
     * @throws IllegalArgumentException si le type est inconnu
     */
    public static VoteRepository createRepository(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Le type de repository ne peut pas être vide");
        }

        return switch (type.toLowerCase()) {
            case TYPE_MEMORY -> new InMemoryVoteRepository();

            // Future: FileVoteRepository
            case TYPE_FILE ->
                    throw new UnsupportedOperationException(
                            "Le repository de type 'file' n'est pas encore implémenté");

            // Future: DatabaseVoteRepository
            case TYPE_DATABASE ->
                    throw new UnsupportedOperationException(
                            "Le repository de type 'database' n'est pas encore implémenté");

            default ->
                    throw new IllegalArgumentException(
                            "Type de repository inconnu: " + type +
                                    ". Types supportés: " + TYPE_MEMORY);
        };
    }

    /**
     * Crée un repository par défaut (en mémoire).
     *
     * @return Instance de InMemoryVoteRepository
     */
    public static VoteRepository createDefaultRepository() {
        return createRepository(TYPE_MEMORY);
    }
}