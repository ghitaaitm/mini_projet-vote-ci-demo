package com.example.vote.model;

/**
 * Représente un vote dans le système.
 * Record immutable contenant les informations d'un vote.
 *
 * @param voterId      Identifiant de la personne qui vote
 * @param candidateId  Identifiant du candidat pour lequel on vote
 * @param timestamp    Horodatage du vote (millisecondes depuis epoch)
 */
public record Vote(String voterId, String candidateId, long timestamp) {

    /**
     * Constructeur compact avec validation.
     */
    public Vote {
        if (voterId == null || voterId.isBlank()) {
            throw new IllegalArgumentException("L'ID du votant ne peut pas être vide");
        }
        if (candidateId == null || candidateId.isBlank()) {
            throw new IllegalArgumentException("L'ID du candidat ne peut pas être vide");
        }
        if (timestamp <= 0) {
            throw new IllegalArgumentException("Le timestamp doit être positif");
        }
    }

    /**
     * Constructeur de commodité qui génère automatiquement le timestamp.
     */
    public Vote(String voterId, String candidateId) {
        this(voterId, candidateId, System.currentTimeMillis());
    }
}